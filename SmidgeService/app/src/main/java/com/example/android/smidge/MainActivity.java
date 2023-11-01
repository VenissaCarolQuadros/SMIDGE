package com.example.android.smidge;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Constraints;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AppOpsManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityTransition;
import com.google.android.gms.location.ActivityTransitionEvent;
import com.google.android.gms.location.ActivityTransitionRequest;
import com.google.android.gms.location.ActivityTransitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!NotificationManagerCompat.getEnabledListenerPackages(getApplicationContext()).contains(getPackageName()))
        {
            Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            startActivity(intent);
        }

        try {
            if (!(checkActivityRecognitionPermission())){

                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.ACTIVITY_RECOGNITION},
                        45);
            }

            if (!(checkUsageStatsPermission())){
                Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
            Globals.createTimestampTracker(getApplicationContext());
            //setPeriodicWorkRequest(getApplicationContext());
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            if (checkActivityRecognitionPermission()){
                Intent intent = new Intent(this, TransitionService.class);
                stopService(intent);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(intent);
                    Log.d("SERVICE", "Starting foreground service");
                }
                else{
                    Log.d("SERVICE", String.valueOf(Build.VERSION.SDK_INT));
                    startService(intent);
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Globals.doCheckApps(getApplicationContext(), false);
            }
        });


    }
    private boolean checkUsageStatsPermission() throws PackageManager.NameNotFoundException {
        PackageManager packageManager = getPackageManager();
        ApplicationInfo applicationInfo = packageManager.getApplicationInfo(getPackageName(), 0);
        AppOpsManager appOpsManager = (AppOpsManager) getSystemService(AppCompatActivity.APP_OPS_SERVICE);
        // `AppOpsManager.checkOpNoThrow` is deprecated from Android Q
        int mode = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            mode = appOpsManager.unsafeCheckOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                    applicationInfo.uid, applicationInfo.packageName
            );
        }
        else {
            appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                    applicationInfo.uid, applicationInfo.packageName
            );
        }
        return mode == AppOpsManager.MODE_ALLOWED;
    }
    private boolean checkActivityRecognitionPermission() throws PackageManager.NameNotFoundException {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACTIVITY_RECOGNITION
            );
        } else {
            return true;
        }

    }
    private void setPeriodicWorkRequest(Context context) {
        WorkManager.getInstance(context).cancelAllWorkByTag("periodic_work");
        // Repeated execution bug!
        //PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest.Builder(CheckAppLogs.class, 60, TimeUnit.MINUTES).setConstraints(Constraints.NONE).addTag("periodic_work").build();
        //WorkManager.getInstance(context).enqueue(periodicWorkRequest);

        //Log.d("PERIODIC", "ONE_SHOT");
        //OneTimeWorkRequest oneTimeWorkRequest = new OneTimeWorkRequest.Builder(CheckAppLogs.class).setConstraints(Constraints.NONE).addTag("periodic_work").build();
        //WorkManager.getInstance(context).enqueue(oneTimeWorkRequest);
    }
}