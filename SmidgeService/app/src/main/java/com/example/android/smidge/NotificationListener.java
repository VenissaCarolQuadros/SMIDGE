package com.example.android.smidge;

import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import java.util.Arrays;

public class NotificationListener extends NotificationListenerService {
    @Override
    public void onListenerConnected() {
        super.onListenerConnected();
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        super.onNotificationRemoved(sbn);
        boolean update = false;
        if (Arrays.asList(Globals.apps).contains(sbn.getPackageName())){
            Globals.logSimpleEvent(getApplicationContext(), "Notification", sbn.getPackageName());
            update = true;
        }
        Globals.doCheckApps(getApplicationContext(), true);
    }

}