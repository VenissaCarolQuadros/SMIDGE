
package com.example.android.smidge;

import android.accessibilityservice.AccessibilityService;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

public class SmidgeService extends AccessibilityService implements SensorEventListener {
    private Integer twistCount=0, dipCount=0, tiltCount=0;
    private boolean twistStop = false, tiltStop = false, dipStop = false;
    boolean coolDown=false;
    private Long twistTime=System.nanoTime(), dipTime=System.nanoTime(), tiltTime=System.nanoTime(), coolDownTime=System.nanoTime();
    Sensor gyroscope;
    SensorManager sensorManager;
    KeyguardManager keyguardManager;

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
        sensorManager.registerListener((SensorEventListener) this, gyroscope, SensorManager.SENSOR_DELAY_FASTEST);
        keyguardManager = (KeyguardManager) getApplicationContext().getSystemService(Context.KEYGUARD_SERVICE);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event != null && (System.nanoTime() - coolDownTime > 1000000000) && !keyguardManager.inKeyguardRestrictedInputMode()) {
            coolDown=false;
            String twist1 = Globals.twist1;
            String twist2 = Globals.twist2;
            String tilt1 = Globals.tilt1;
            String tilt2 = Globals.tilt2;
            String dip1 = Globals.fold1;
            String dip2 = Globals.fold2;
            if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
                // TWIST!
                if (twistCount == 0) {
                    twistTime = System.nanoTime();
                }
                if (event.values[1] > -5 && twistCount == 1)
                    twistStop = true;

                else if (event.values[1] < -5 && twistCount == 1 && twistStop) {
                    twistCount = 0;
                    //coolDown=true;
                    twistStop = false;
                    coolDownTime=System.nanoTime();
                    launchApp(twist2);
                    Globals.logSimpleEvent(getApplicationContext(), "Gesture", twist2);
                    Log.d("Sensor", "LAUNCHING TWO TWIST INTENT");
                }
                else if (event.values[1] < -10 && twistCount == 0) {
                    twistCount = 1;
                    twistStop = false;
                    Log.d("Sensor", "FIRST TWIST $twistCount");
                    //launchIntent?.let { startActivity(launchIntent) }
                }
                if (System.nanoTime() - twistTime > 1000000000) {
                    twistCount = 0;
                    //Log.d("TWIST COUNT", twistCount);
                    //coolDown=true;
                    coolDownTime=System.nanoTime();
                    launchApp(twist1);
                    Globals.logSimpleEvent(getApplicationContext(), "Gesture", twist1);
                    Log.d("Sensor", "LAUNCHING ONE TWIST INTENT");
                }

                // TILT
                if (tiltCount == 0) {
                    tiltTime = System.nanoTime();
                }
                if (event.values[2] < 5 && tiltCount == 1)
                    tiltStop = true;
                else if (event.values[2] > 5 && tiltCount == 1 && tiltStop) {
                    tiltCount = 0;
                    tiltStop = false;
                    //coolDown=true;
                    coolDownTime=System.nanoTime();
                    launchApp(tilt2);
                    Globals.logSimpleEvent(getApplicationContext(), "Gesture", tilt2);
                    Log.d("Sensor", "LAUNCHING TWO TILT INTENT");
                }
                else if (event.values[2] > 10 && tiltCount == 0) {
                    tiltCount = 1;
                    tiltStop = false;
                    Log.d("Sensor", "FIRST TILT $tiltCount");
                    //launchIntent?.let { startActivity(launchIntent) }
                }
                if (System.nanoTime() - tiltTime > 1000000000) {
                    tiltCount = 0;
                    //coolDown=true;
                    coolDownTime=System.nanoTime();
                    launchApp(tilt1);
                    Globals.logSimpleEvent(getApplicationContext(), "Gesture", tilt1);
                    Log.d("Sensor", "LAUNCHING ONE TILT INTENT");
                }

                //DIP
                if (dipCount == 0) {
                    dipTime = System.nanoTime();
                }
                if (event.values[0] < 5 && dipCount == 1)
                    dipStop = true;
                else if (event.values[0] > 5 && dipCount == 1 && dipStop) {
                    dipCount = 0;
                    dipStop = false;
                    //coolDown=true;
                    coolDownTime=System.nanoTime();
                    launchApp(dip2);
                    Globals.logSimpleEvent(getApplicationContext(), "Gesture", dip2);
                    Log.d("Sensor", "LAUNCHING TWO DIP INTENT");
                }
                else if (event.values[0] > 10 && dipCount == 0) {
                    dipCount = 1;
                    dipStop = false;
                    Log.d("Sensor", "FIRST DIP $dipCount");
                    //launchIntent?.let { startActivity(launchIntent) }
                }
                if (System.nanoTime() - dipTime > 1000000000) {
                    dipCount = 0;
                    //coolDown=true;
                    coolDownTime=System.nanoTime();
                    launchApp(dip1);
                    Globals.logSimpleEvent(getApplicationContext(), "Gesture", dip1);
                    Log.d("Sensor", "LAUNCHING ONE DIP INTENT");
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

