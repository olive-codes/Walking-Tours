package com.oliviabecht.obechtwalkingtours;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.net.Uri;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.ArrayList;
import java.util.List;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "GeofenceBroadcastRcvr";
    private static final String CHANNEL_ID = "FENCE_CHANNEL";

    private ArrayList<FenceData>  geoFenceList = new ArrayList<>();
    MapsActivity mapsActivity;

    @Override
    public void onReceive(Context context, Intent intent) {

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        if (geofencingEvent == null) {
            Log.d(TAG, "onReceive: NULL GeofencingEvent received");
            return;
        }

        if (geofencingEvent.hasError()) {
            Log.e(TAG, "Error: " + geofencingEvent.getErrorCode());
            return;
        }

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {

            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
            if (triggeringGeofences != null) {
                for (Geofence g : triggeringGeofences) {
                    sendNotification(context, g.getRequestId(), geofenceTransition);
                }
            }
        }
    }

    public void sendNotification(Context context, String id, int transitionType) {

        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager == null) return;

        if (notificationManager.getNotificationChannel(CHANNEL_ID) == null) {

            Uri soundUri = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.notify_sound);
            AudioAttributes att = new AudioAttributes.Builder().
                    setUsage(AudioAttributes.USAGE_NOTIFICATION).build();

            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_ID, importance);
            mChannel.setSound(soundUri, att);
            mChannel.setLightColor(Color.RED);
            mChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            mChannel.setShowBadge(true);

            notificationManager.createNotificationChannel(mChannel);

        }

        ////
        String transitionString = transitionType == Geofence.GEOFENCE_TRANSITION_ENTER
                ? "Welcome!" : "Goodbye!";

        String address = null;
        String body = null;
        String image = null;
        String fenceString;
        String compareString = id;
        //get arrayList
        
        geoFenceList = mapsActivity.mapsFenceList;
        for (FenceData fence : geoFenceList) {
            //geoFenceList.get(geoFenceList.indexOf(fence));
            fenceString = fence.getId();
            if (compareString.matches(fenceString)) {
                address = fence.getBuildingAddress();
                body = fence.getBuildingDescription();
                image = fence.getBuildingImage();
            }
        }

        Intent resultIntent = new Intent(context.getApplicationContext(), FenceInfoActivity.class);
        resultIntent.putExtra("FENCE_TITLE", id);
        resultIntent.putExtra("FENCE_ADDRESS", address);
        resultIntent.putExtra("FENCE_BODY", body);
        resultIntent.putExtra("FENCE_IMAGE", image);
        resultIntent.putExtra("FENCE_TRANS", transitionString);

        PendingIntent pi = PendingIntent.getActivity(
                context.getApplicationContext(), 0, resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);


        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentIntent(pi)
                .setSmallIcon(R.drawable.fence_notif)
                .setContentTitle("Walking Tours - " + id) // Bold title, may need to add a second icon
                .setContentText(id + " (Tap to see details)\n" + address) // Detail info
                //.setContentTitle("Walking Tours - " + buildingName + " - ") // Bold title, may need to add a second icon
                //.setContentText(buildingName + " (Tap to see details)\n" + buildingAddress) // Detail info
                .setAutoCancel(true)
                .build();

        notificationManager.notify(getUniqueId(), notification);
    }

    private static int getUniqueId() {
        return(int) (System.currentTimeMillis() % 10000);
    }

}
