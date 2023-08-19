package com.example.android.smidge;

import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Arrays;
import java.util.Map;

public class Globals {
    public static Integer twist=0, Dtwist=0, fold=0, Dfold=0, tilt=0, Dtilt=0;
    public static Integer totTwist, totDTwist, totFold, totDFold, totTilt, totDTilt;
    public static String timestamp="";
    static String filename="stats";

    public static String twist1 = "com.google.android.youtube";
    public static String twist2 = "com.duolingo";
    public static  String tilt1 = "com.instagram.android";
    public static String tilt2 = "com.microsoft.office.outlook";
    public static String fold1 = "com.whatsapp";
    public static  String fold2 = "com.reddit.frontpage";

    public static String[] apps=new String[]{twist1, twist2, tilt1, tilt2, fold1, fold2};

    public static String toJSON(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        twist = preferences.getInt(context.getString(R.string.twist), 0);
        Dtwist = preferences.getInt(context.getString(R.string.Dtwist), 0);
        tilt = preferences.getInt(context.getString(R.string.tilt), 0);
        Dtilt= preferences.getInt(context.getString(R.string.Dtilt), 0);
        fold = preferences.getInt(context.getString(R.string.fold), 0);
        Dfold = preferences.getInt(context.getString(R.string.Dfold), 0);

        JSONObject jsonObject= new JSONObject();
        try {
            jsonObject.put("Timestamp", timestamp);
            jsonObject.put("Twist", twist);
            jsonObject.put("Double Twist", Dtwist);
            jsonObject.put("Tilt", tilt);
            jsonObject.put("Double Tilt", Dtilt);
            jsonObject.put("Fold", fold);
            jsonObject.put("Double Fold", Dfold);
            jsonObject.put("Total Twist", totTwist);
            jsonObject.put("Total Double Twist", totDTwist);
            jsonObject.put("Total Tilt", totTilt);
            jsonObject.put("Total Double Tilt", totDTilt);
            jsonObject.put("Total Fold", totFold);
            jsonObject.put("Total Double Fold", totDFold);

            return jsonObject.toString();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "";
        }}
    public static void fetchUsageStats(Context context){
        Long startMillis= 1692381228610L;
        Long endMillis= System.currentTimeMillis();
        boolean tw=true, Dtw=true, ti=true, Dti=true, fo=true, Dfo=true;
        totTwist=0;
        totDTwist=0;
        totFold=0;
        totDFold=0;
        totTilt=0;
        totDTilt=0;
        UsageStatsManager mUsageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        UsageEvents events= null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            events = mUsageStatsManager.queryEvents(startMillis, endMillis);
        }
        //lUsageStatsMap.get(twist1).describeContents()
        while (events.hasNextEvent()){

            UsageEvents.Event event = new UsageEvents.Event();
            events.getNextEvent(event);
            if (Arrays.asList(Globals.apps).contains(event.getPackageName())){
                String app= event.getPackageName();
                Log.d("APP", event.getPackageName()+","+event.getEventType());
            if (event.getEventType()== UsageEvents.Event.ACTIVITY_RESUMED ){
                if (app.equals(Globals.twist1) && tw){
                    Globals.totTwist+=1;
                    tw=false;
                } else if (app.equals(Globals.twist2) && Dtw) {
                    Globals.totDTwist+=1;
                    Dtw=false;
                } else if (app.equals(Globals.tilt1) && ti) {
                    Globals.totTilt+=1;
                    ti=false;
                } else if (app.equals(Globals.tilt2) && Dti) {
                    Globals.totDTilt+=1;
                    Dti=false;
                } else if (app.equals(Globals.fold1) && fo) {
                    Globals.totFold+=1;
                    fo=false;
                } else if (app.equals(Globals.fold2) && Dfo) {
                    Globals.totDFold+=1;
                    Dfo=false;
                }
            } else if (event.getEventType()== UsageEvents.Event.ACTIVITY_STOPPED){
                if (app.equals(Globals.twist1)){
                    tw=true;
                } else if (app.equals(Globals.twist2)) {
                    Dtw=true;
                } else if (app.equals(Globals.tilt1)) {
                    ti=true;
                } else if (app.equals(Globals.tilt2)) {
                    Dti=true;
                } else if (app.equals(Globals.fold1)) {
                    fo=true;
                } else if (app.equals(Globals.fold2)) {
                    Dfo=true;
                }
                }
            }
        }
    }
}
