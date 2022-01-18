package com.usg.downloadfile;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.downloader.Error;
import com.downloader.OnCancelListener;
import com.downloader.OnDownloadListener;
import com.downloader.OnPauseListener;
import com.downloader.OnProgressListener;
import com.downloader.OnStartOrResumeListener;
import com.downloader.PRDownloader;
import com.downloader.Progress;
import com.downloader.Status;

public class MainActivity extends AppCompatActivity {

    private EditText editTextUrl;
    private String path;
    private TextView file_downloaded_path, file_name, downloading_percent;
    private ProgressBar progressBar;
    private Button btnStart, btnCancel, buttonDownload;
    private LinearLayout details;
    int downloadID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Initializing PRDownloader library
        PRDownloader.initialize(this);

        // finding edittext by its id
        editTextUrl = findViewById(R.id.url_etText);

        // finding button by its id
        buttonDownload = findViewById(R.id.btn_download);

        // finding textview by its id
        file_downloaded_path = findViewById(R.id.txt_url);

        // finding textview by its id
        file_name = findViewById(R.id.file_name);

        // finding progressbar by its id
        progressBar = findViewById(R.id.progress_horizontal);

        // finding textview by its id
        downloading_percent = findViewById(R.id.downloading_percentage);

        // finding button by its id
        btnStart = findViewById(R.id.btn_start);

        // finding button by its id
        btnCancel = findViewById(R.id.btn_stop);

        // finding linear layout by its id
        details = findViewById(R.id.details_box);

        //storing the path of the file
        path = Util.getRootDirPath(this);

        // handling onclick event on button
        buttonDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // getting the text from edittext
                // and storing it to url variable
//                String url = editTextUrl.getText().toString().trim();
//                http://localhost:8680/WebExpress_GNMM/webresources/kiosk/getFile
//                String url = "https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf";
                String url = "https://192.168.237.1:8443/WebExpress_GNMM/webresources/kiosk/ASHITEYELIJAH.pdf";

                // setting the visibility of linear layout to visible
                details.setVisibility(View.VISIBLE);
                // calling method downloadFile passing url as parameter
//                downloadFile(url);

                Intent i = new Intent(MainActivity.this,BatteryActivity.class);
                startActivity(i);
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void downloadFile(final String url) {

        // handling click event on start button
        // which starts the downloading of the file
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Status::" + PRDownloader.getStatus(downloadID));

                // checks if the process is already running
                if (Status.RUNNING == PRDownloader.getStatus(downloadID)) {
                    // pauses the download if
                    // user click on pause button
                    PRDownloader.pause(downloadID);
                    return;
                }

                // enabling the start button
                btnStart.setEnabled(false);

                // checks if the status is paused
                if (Status.PAUSED == PRDownloader.getStatus(downloadID)) {
                    // resume the download if download is paused
                    PRDownloader.resume(downloadID);
                    return;
                }

                // getting the filename
                String fileName = URLUtil.guessFileName(url, null, null);

                // setting the file name
                file_name.setText("Downloading " + fileName);

                // making the download request
                downloadID = PRDownloader.download(url, path, fileName)
                        .build()
                        .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onStartOrResume() {
                                progressBar.setIndeterminate(false);
                                // enables the start button
                                btnStart.setEnabled(true);
                                // setting the text of start button to pause
                                btnStart.setText("Pause");
                                // enabling the stop button
                                btnCancel.setEnabled(true);
                                Toast.makeText(MainActivity.this, "Downloading started", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setOnPauseListener(new OnPauseListener() {
                            @Override
                            public void onPause() {
                                // setting the text of start button to resume
                                // when the download is in paused state
                                btnStart.setText("Resume");
                                Toast.makeText(MainActivity.this, "Downloading Paused", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setOnCancelListener(new OnCancelListener() {
                            @Override
                            public void onCancel() {
                                // resetting the downloadId when
                                // the download is cancelled
                                downloadID = 0;
                                // setting the text of start button to start
                                btnStart.setText("Start");
                                // disabling the cancel button
                                btnCancel.setEnabled(false);
                                // resetting the progress bar
                                progressBar.setProgress(0);
                                // restting the download precent
                                downloading_percent.setText("");
                                progressBar.setIndeterminate(false);
                                Toast.makeText(MainActivity.this, "Downloading Cancelled", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setOnProgressListener(new OnProgressListener() {
                            @Override
                            public void onProgress(Progress progress) {
                                // getting the progress of download
                                long progressPer = progress.currentBytes * 100 / progress.totalBytes;
                                // setting the progress to progressbar
                                progressBar.setProgress((int) progressPer);
                                // setting the download percent
                                downloading_percent.setText(Util.getProgressDisplayLine(progress.currentBytes, progress.totalBytes));
                                progressBar.setIndeterminate(false);
                            }
                        })
                        .start(new OnDownloadListener() {

                            @Override
                            public void onDownloadComplete() {
                                // disabling the start button
                                btnStart.setEnabled(false);
                                // disabling the cancel button
                                btnCancel.setEnabled(false);
                                // setting the text completed to start button
                                btnStart.setText("Completed");
                                // will show the path after the file is downloaded
                                file_downloaded_path.setText("File stored at : " + path);
                                Toast.makeText(MainActivity.this, "Downloading Completed", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onError(Error error) {
                                // setting the text start
                                btnStart.setText("Start");
                                // resetting the download percentage
                                downloading_percent.setText("0");
                                // resetting the progressbar
                                progressBar.setProgress(0);
                                // resetting the downloadID
                                downloadID = 0;
                                // enabling the start button
                                btnStart.setEnabled(true);
                                // disabling the cancel button
                                btnCancel.setEnabled(false);
                                progressBar.setIndeterminate(false);
                                Toast.makeText(MainActivity.this, "Error Occurred", Toast.LENGTH_SHORT).show();
                            }
                        });

                // handling click event on cancel button
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        btnStart.setText("Start");
                        // cancels the download
                        PRDownloader.cancel(downloadID);
                    }
                });
            }
        });
    }
}

