
package com.example.android.globalactionbarservice;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.content.Context;
import android.content.Intent;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Button;
import android.widget.FrameLayout;

import java.util.ArrayDeque;
import java.util.Deque;

public class GlobalActionBarService extends AccessibilityService implements SensorEventListener {
    private Integer twistCount=0, dipCount=0, tiltCount=0;
    boolean coolDown=false;
    private Long twistTime=System.nanoTime(), dipTime=System.nanoTime(), tiltTime=System.nanoTime(), coolDownTime=System.nanoTime();
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
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sensorManager.registerListener((SensorEventListener) this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event != null && (!coolDown || System.nanoTime() - coolDownTime > 1000000000)) {
            coolDown=false;
            String twist1 = "com.google.android.youtube";
            String twist2 = "com.duolingo";
            String tilt1 = "com.instagram.android";
            String tilt2 = "com.microsoft.office.outlook";
            String dip1 = "com.whatsapp";
            String dip2 = "com.reddit.frontpage";
            if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
                // TWIST!
                if (twistCount == 0) {
                    twistTime = System.nanoTime();
                }
                if (event.values[1] > 5 && twistCount == 1) {
                    twistCount += 1;
                    launchApp(twist2);
                    coolDown=true;
                    coolDownTime=System.nanoTime();
                    Log.d("Sensor", "LAUNCHING TWO TWIST INTENT");
                }
                if (event.values[1] > 5 && twistCount == 0) {
                    twistCount += 1;
                    Log.d("Sensor", "FIRST TWIST $twistCount");
                    //launchIntent?.let { startActivity(launchIntent) }
                }
                if (System.nanoTime() - twistTime > 1000000000) {
                    twistCount = 0;
                    launchApp(twist1);
                    coolDown=true;
                    coolDownTime=System.nanoTime();
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
                    Log.d("Sensor", "LAUNCHING TWO TILT INTENT");
                }
                if (event.values[2] > 5 && tiltCount == 0) {
                    tiltCount += 1;
                    Log.d("Sensor", "FIRST TILT $tiltCount");
                    //launchIntent?.let { startActivity(launchIntent) }
                }
                if (System.nanoTime() - tiltTime > 1000000000) {
                    tiltCount = 0;
                    launchApp(tilt1);
                    coolDown=true;
                    coolDownTime=System.nanoTime();
                    Log.d("Sensor", "LAUNCHING ONE TILT INTENT");
                }
                if (tiltCount > 1) {
                    Log.d("Sensor", "RESETTING TILT COUNT");
                    tiltCount = 0;
                }

                //DIP
                if (dipCount == 0) {
                    dipTime = System.nanoTime();
                }
                if (event.values[0] > 5 && dipCount == 1) {
                    dipCount += 1;
                    launchApp(dip2);
                    coolDown=true;
                    coolDownTime=System.nanoTime();
                    Log.d("Sensor", "LAUNCHING TWO DIP INTENT");
                }
                if (event.values[0] > 5 && dipCount == 0) {
                    dipCount += 1;
                    Log.d("Sensor", "FIRST DIP $dipCount");
                    //launchIntent?.let { startActivity(launchIntent) }
                }
                if (System.nanoTime() - dipTime > 1000000000) {
                    dipCount = 0;
                    launchApp(dip1);
                    coolDown=true;
                    coolDownTime=System.nanoTime();
                    Log.d("Sensor", "LAUNCHING ONE DIP INTENT");
                }
                if (dipCount > 1) {
                    Log.d("Sensor", "RESETTING DIP COUNT");
                    dipCount = 0;
                }
            }}
    }

            @Override
            public void onAccuracyChanged (Sensor sensor,int accuracy){
                return;
            }

    private void launchApp(String appName){
        Intent launchIntent= getPackageManager().getLaunchIntentForPackage(appName).setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(launchIntent);
    }

        }

