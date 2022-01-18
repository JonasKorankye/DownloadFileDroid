package com.usg.downloadfile;


import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.view.View;
import android.widget.RatingBar;
import android.provider.Settings.Secure;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;


public class BatteryActivity extends Activity {

    TextView tv;
    ToggleButton toggleButton1,toggleButton2;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mian);

        tv = findViewById(R.id.text);
        toggleButton1 = findViewById(R.id.toggleButton1);
        toggleButton2 = findViewById(R.id.toggleButton2);

        String deviceId = Secure.getString(this.getContentResolver(),Secure.ANDROID_ID);
        Toast.makeText(this, deviceId, Toast.LENGTH_LONG).show();
        tv.setText(deviceId);

    }

    public void onClick(View view) {
        // Register for the battery changed event
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);

        // Intent is sticky so using null as receiver works fine
        // return value contains the status
        Intent batteryStatus = this.registerReceiver(null, filter);

        // Are we charging / charged?
        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING
                || status == BatteryManager.BATTERY_STATUS_FULL;

        boolean isFull = status == BatteryManager.BATTERY_STATUS_FULL;
//        int percentage = BatteryManager.BATTERY_PROPERTY_CAPACITY;
//
//        System.out.println("Percentage::" + percentage);
//        tv.setText(percentage);

        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        float batteryPct = level * 100 / (float)scale;

        tv.setText(batteryPct + "%");



        // How are we charging?
        int chargePlug = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
        boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;

        System.out.println(usbCharge);
        if(usbCharge){

            toggleButton1.setChecked(true);
        } else  if(acCharge){
            toggleButton2.setChecked(true);
        }

        // Update the user interface (Rating bar) based on our findings

        RatingBar bar = findViewById(R.id.ratingBar1);

        if (isFull) {
            bar.setProgress(10);
        } else if (isCharging) {
            bar.setProgress(5);
        }

    }




}