package com.deone.extrmtasks.tools;

import static com.deone.extrmtasks.tools.Constants.ACTION_START_LOCATION_SERVICE_ID;
import static com.deone.extrmtasks.tools.Constants.ACTION_STOP_LOCATION_SERVICE_ID;
import static com.deone.extrmtasks.tools.Constants.LOCATION_SERVICE_ID;
import static com.deone.extrmtasks.tools.Constants.LOCATION_SERVICE_SIGNAL;
import static com.deone.extrmtasks.tools.Constants.LOCATION_SERVICE_SIGNAL_LATITUDE;
import static com.deone.extrmtasks.tools.Constants.LOCATION_SERVICE_SIGNAL_LONGITUDE;
import static com.deone.extrmtasks.tools.Lctools.requestAccessFineLocationPermissions;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.deone.extrmtasks.R;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

public class LocService extends Service {

    private double latitude;
    private double longitude;

    private final LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            super.onLocationResult(locationResult);
            if (locationResult != null && locationResult.getLastLocation() != null) {
                latitude = locationResult.getLastLocation().getLatitude();
                longitude = locationResult.getLastLocation().getLongitude();
                Log.e("LOCATION_UPDATE", latitude + ", " + longitude);
            }
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null){
            String action = intent.getAction();
            if (action != null){
                if (action.equals(ACTION_START_LOCATION_SERVICE_ID)){
                    startLocationService();
                } else if (action.equals(ACTION_STOP_LOCATION_SERVICE_ID)) {
                    stopLocationService();
                }
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void startLocationService() {
        // TODO: Creating notification
        String channelId = "location_notification_channel";
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Intent resultIntent = new Intent();
        PendingIntent pendingIntent = PendingIntent.getActivity(
                getApplicationContext(),
                0,
                resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                getApplicationContext(),
                channelId
        );
        builder.setSmallIcon(R.drawable.logo_expert);
        builder.setContentTitle("Location service");
        builder.setDefaults(NotificationCompat.DEFAULT_ALL);
        builder.setContentText("Running");
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(false);
        builder.setPriority(NotificationCompat.PRIORITY_MAX);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager != null && notificationManager.getNotificationChannel(channelId) == null) {
                NotificationChannel notificationChannel = new NotificationChannel(
                        channelId,
                        "Location service",
                        NotificationManager.IMPORTANCE_HIGH
                );
                notificationChannel.setDescription("This channel is used by location service");
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }

        // TODO: Launch service
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(4000);
        locationRequest.setFastestInterval(2000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling ActivityCompat#requestPermissions
            requestAccessFineLocationPermissions(this);
            return;
        }
        LocationServices.getFusedLocationProviderClient(this)
                .requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        startForeground(LOCATION_SERVICE_ID, builder.build());
    }

    private void stopLocationService(){
        // TODO: Stop service
        LocationServices.getFusedLocationProviderClient(this)
                .removeLocationUpdates(locationCallback);
        stopForeground(true);
        stopSelf();
    }

    private void sendDataLocation() {
        Intent sendLocation = new Intent();
        sendLocation.setAction(LOCATION_SERVICE_SIGNAL);
        sendLocation.putExtra( LOCATION_SERVICE_SIGNAL_LATITUDE, latitude);
        sendLocation.putExtra( LOCATION_SERVICE_SIGNAL_LONGITUDE, longitude);
        sendBroadcast(sendLocation);

    }

}
