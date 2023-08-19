
package com.example.android.smidge;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import java.io.FileOutputStream;


public class SmidgeService extends AccessibilityService implements SensorEventListener {
    private Integer twistCount=0, foldCount=0, tiltCount=0;
    boolean coolDown=false;
    private Long twistTime=System.nanoTime(), foldTime=System.nanoTime(), tiltTime=System.nanoTime(), coolDownTime=System.nanoTime();
    Sensor gyroscope;
    SensorManager sensorManager;




    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

    }

    @Override
    public void onInterrupt() {

    }

    @Override
    protected void onServiceConnected() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = preferences.edit();
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        preferences.getInt(getString(R.string.twist), 0);
        preferences.getInt(getString(R.string.Dtwist), 0);
        preferences.getInt(getString(R.string.tilt), 0);
        preferences.getInt(getString(R.string.Dtilt), 0);
        preferences.getInt(getString(R.string.fold), 0);
        preferences.getInt(getString(R.string.Dfold), 0);
        sensorManager.registerListener( this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = preferences.edit();
        if (event != null && (!coolDown || System.nanoTime() - coolDownTime > 1500000000)) {
            coolDown=false;
            String twist1 = Globals.twist1;
            String twist2 = Globals.twist2;
            String tilt1 = Globals.tilt1;
            String tilt2 = Globals.tilt2;
            String fold1 = Globals.fold1;
            String fold2 = Globals.fold2;
            if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
                // TWIST!
                if (twistCount == 0) {
                    twistTime = System.nanoTime();
                }
                if (event.values[1] < -5 && twistCount == 1) {
                    twistCount += 1;
                    launchApp(twist2);
                    coolDown=true;
                    coolDownTime=System.nanoTime();
                    Globals.Dtwist+=1;
                    editor.putInt(getString(R.string.Dtwist), Globals.Dtwist);
                    Log.d("Sensor", "LAUNCHING TWO TWIST INTENT");
                }
                if (event.values[1] < -10 && twistCount == 0) {
                    twistCount += 1;
                    Log.d("Sensor", "FIRST TWIST $twistCount");
                    //launchIntent?.let { startActivity(launchIntent) }
                }
                if (System.nanoTime() - twistTime > 1000000000) {
                    twistCount = 0;
                    launchApp(twist1);
                    coolDown=true;
                    coolDownTime=System.nanoTime();
                    Globals.twist+=1;
                    editor.putInt(getString(R.string.twist), Globals.twist);
                    Log.d("Sensor", "LAUNCHING ONE TWIST INTENT");
                }
                if (twistCount > 1) {
                    Log.d("Sensor", "RESETTING TWIST COUNT");
                    twistCount = 0;
                }

                // TILT
                if (tiltCount == 0) {
                    tiltTime = System.nanoTime();
                }
                if (event.values[2] > 5 && tiltCount == 1) {
                    tiltCount += 1;
                    launchApp(tilt2);
                    coolDown=true;
                    coolDownTime=System.nanoTime();
                    Globals.Dtilt+=1;
                    editor.putInt(getString(R.string.Dtilt), Globals.Dtilt);
                    Log.d("Sensor", "LAUNCHING TWO TILT INTENT");
                }
                if (event.values[2] > 10 && tiltCount == 0) {
                    tiltCount += 1;
                    Log.d("Sensor", "FIRST TILT $tiltCount");
                    //launchIntent?.let { startActivity(launchIntent) }
                }
                if (System.nanoTime() - tiltTime > 1000000000) {
                    tiltCount = 0;
                    launchApp(tilt1);
                    coolDown=true;
                    coolDownTime=System.nanoTime();
                    Globals.tilt+=1;
                    editor.putInt(getString(R.string.tilt), Globals.tilt);
                    Log.d("Sensor", "LAUNCHING ONE TILT INTENT");
                }
                if (tiltCount > 1) {
                    Log.d("Sensor", "RESETTING TILT COUNT");
                    tiltCount = 0;
                }

                //FOLD
                if (foldCount == 0) {
                    foldTime = System.nanoTime();
                }
                if (event.values[0] > 5 && foldCount == 1) {
                    foldCount += 1;
                    launchApp(fold2);
                    coolDown=true;
                    coolDownTime=System.nanoTime();
                    Globals.Dfold+=1;
                    editor.putInt(getString(R.string.Dfold), Globals.Dfold);
                    Log.d("Sensor", "LAUNCHING TWO FOLD INTENT");
                }
                if (event.values[0] > 10 && foldCount == 0) {
                    foldCount += 1;
                    Log.d("Sensor", "FIRST FOLD $foldCount");
                    //launchIntent?.let { startActivity(launchIntent) }
                }
                if (System.nanoTime() - foldTime > 1000000000) {
                    foldCount = 0;
                    launchApp(fold1);
                    coolDown=true;
                    coolDownTime=System.nanoTime();
                    Globals.fold+=1;
                    editor.putInt(getString(R.string.fold), Globals.fold);
                    Log.d("Sensor", "LAUNCHING ONE FOLD INTENT");
                }
                if (foldCount > 1) {
                    Log.d("Sensor", "RESETTING FOLD COUNT");
                    foldCount = 0;
                }
            editor.apply();
            }}
    }

    @Override
    public void onAccuracyChanged (Sensor sensor,int accuracy){
    }

    private void launchApp(String appName){
        //Intent launchIntent= getPackageManager().getLaunchIntentForPackage(appName).setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT|Intent.FLAG_ACTIVITY_NEW_TASK);
        Intent launchIntent= getPackageManager().getLaunchIntentForPackage(appName).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(launchIntent);
    }

}


