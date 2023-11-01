package com.example.android.smidge;

import static java.lang.System.currentTimeMillis;

import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class Globals {
    static String logFile="user_stats_";

    public static Integer uid = null;
    public static String twist1 = "com.google.android.youtube";
    public static String twist2 = "com.duolingo";
    public static  String tilt1 = "com.instagram.android";
    public static String tilt2 = "com.microsoft.office.outlook";
    public static String fold1 = "com.whatsapp";
    public static  String fold2 = "com.reddit.frontpage";

    public static String[] apps=new String[]{twist1, twist2, tilt1, tilt2, fold1, fold2};
    private static String periodicLog;

    public static void createTimestampTracker(Context context) throws IOException {
        String tT = "user_stats_timestamp.log";
        File file = new File(context.getFilesDir(), tT);
        if(!file.exists()){
            file.createNewFile();
            Log.d("TIME TRACKER", "File Created");
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(String.valueOf(currentTimeMillis()-200000).getBytes(StandardCharsets.UTF_8));
            fos.close();
        }
    }

    public static void updateTimestampTracker(Context context, Long endMillis) throws IOException {
        String tT = "user_stats_timestamp.log";
        File file = new File(context.getFilesDir(), tT);
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(String.valueOf(endMillis).getBytes(StandardCharsets.UTF_8));
        fos.close();
    }
    public static void appLogs(Context context, Long startMillis, Long endMillis) throws IOException {
        UsageStatsManager mUsageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        UsageEvents events= null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            events = mUsageStatsManager.queryEvents(startMillis, endMillis);
        }
        String periodicLog = "";
        SimpleDateFormat date = new SimpleDateFormat("yyyy_MM_dd");
        Calendar calendar = Calendar.getInstance();
        //calendar.setTimeInMillis(time);
        String day = null;
        while (events.hasNextEvent()) {
            UsageEvents.Event event = new UsageEvents.Event();
            events.getNextEvent(event);
            if (day == null) {
                calendar.setTimeInMillis(event.getTimeStamp());
                day = date.format(calendar.getTime());
            }
            calendar.setTimeInMillis(event.getTimeStamp());
            if (day.equals(date.format(calendar.getTime()))){
                if (Arrays.asList(Globals.apps).contains(event.getPackageName())) {
                    String app = event.getPackageName();
                    if (event.getEventType() == UsageEvents.Event.ACTIVITY_RESUMED) {
                        periodicLog += Globals.logLogEvent("Started", app, event.getTimeStamp());
                    } else if (event.getEventType() == UsageEvents.Event.ACTIVITY_STOPPED) {
                        periodicLog += Globals.logLogEvent("Stopped", app, event.getTimeStamp());
                    }
                }
            }
            else{
                File file = getTodaysFile(context, day);
                Globals.periodicLogger(file, periodicLog.substring(0, periodicLog.length()-1));
                day = null;
                periodicLog = "";
            }
        }
        if (!periodicLog.equals("")){
            File file = getTodaysFile(context, day);
            Globals.periodicLogger(file, periodicLog.substring(0, periodicLog.length()-1));
        }
    }

    /*
    // Oldish version
        public static void appLogs(Context context, Long startMillis, Long endMillis){
        UsageStatsManager mUsageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        UsageEvents events= null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            events = mUsageStatsManager.queryEvents(startMillis, endMillis);
        }
        String periodicLog = null;
        SimpleDateFormat date = new SimpleDateFormat("yyyy_MM_dd");
        Calendar calendar = Calendar.getInstance();
        //calendar.setTimeInMillis(time);
        String day = null;
        while (events.hasNextEvent()){
            UsageEvents.Event event = new UsageEvents.Event();
            events.getNextEvent(event);
            if (day == null){
                calendar.setTimeInMillis(event.getTimeStamp());
                day = date.format(calendar.getTime());
            }
                if (Arrays.asList(Globals.apps).contains(event.getPackageName())){
                    String app= event.getPackageName();
                    if (event.getEventType()== UsageEvents.Event.ACTIVITY_RESUMED ){
                        Globals.logLogEvent(context, "Started", app,event.getTimeStamp());
                    } else if (event.getEventType()== UsageEvents.Event.ACTIVITY_STOPPED){
                        Globals.logLogEvent(context, "Stopped", app, event.getTimeStamp());
                    }
                }
        }
    }

    public static void logLogEvent( Context context, String type, String packageName,  Long time){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss z");
        SimpleDateFormat date = new SimpleDateFormat("yyyy_MM_dd");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        String timestamp = sdf.format(calendar.getTime());
        String day = date.format(calendar.getTime());
        try{
            File file = getTodaysFile(context, day);
            createRecord(file, timestamp, packageName, type);
        }
        catch(java.io.IOException e){
            Log.d("ERROR", String.valueOf(e));
        }
    }
     */

    public static void periodicLogger(File file, String periodicLog) throws IOException {
        String lineSeparator = System.getProperty("line.separator");
        FileOutputStream fos = new FileOutputStream(file, true);
        fos.write(periodicLog.getBytes(StandardCharsets.UTF_8));
        fos.write(lineSeparator.getBytes(StandardCharsets.UTF_8));
        fos.close();
    }

    public static void createRecord(File file, String timestamp, String packageName, String type) throws IOException {
        String record = timestamp + ", " + packageName + ", "+ type;
        //Log.d("LOGGING", record);
        String lineSeparator = System.getProperty("line.separator");
        FileOutputStream fos = new FileOutputStream(file, true);
        fos.write(record.getBytes(StandardCharsets.UTF_8));
        fos.write(lineSeparator.getBytes(StandardCharsets.UTF_8));
        fos.close();
    }

    public static File getTodaysFile(Context context, String day) throws IOException {
        String filename = logFile + day +".csv";
        File file = new File(context.getFilesDir(), filename);
        if(!file.exists()){
            file.createNewFile();
            String header =  "Timestamp, Package, Type";
            String lineSeparator = System.getProperty("line.separator");
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(header.getBytes(StandardCharsets.UTF_8));
            fos.write(lineSeparator.getBytes(StandardCharsets.UTF_8));
            fos.flush();
            fos.close();
        }
        return file;
    }

    public static void logSimpleEvent( Context context, String type, String packageName){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss z");
        String timestamp = sdf.format(new Date());
        SimpleDateFormat date = new SimpleDateFormat("yyyy_MM_dd");
        String day = date.format(new Date());
        try{
            File file = getTodaysFile(context, day);
            createRecord(file, timestamp, packageName, type);
        }
        catch(java.io.IOException e){
            Log.d("ERROR", String.valueOf(e));
        }
    }

    public static String logLogEvent( String type, String packageName,  Long time){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss z");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        String timestamp = sdf.format(calendar.getTime());
        return timestamp + ", " + packageName + ", "+ type + "\n";
    }

    public static void doCheckApps(Context context, Boolean limit){
        String ust = "user_stats_timestamp.log";
        File file = new File(context.getFilesDir(), ust);
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            Long startMillis = null;
            while ((line = br.readLine()) != null) {
                startMillis = Long.valueOf(line);
            }
            Long endMillis = currentTimeMillis();
            if ((endMillis - startMillis > 1800000)|| !limit) {
                Globals.appLogs(context, startMillis, endMillis);
                Globals.updateTimestampTracker(context, endMillis);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }
}
