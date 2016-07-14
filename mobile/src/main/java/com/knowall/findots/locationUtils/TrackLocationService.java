package com.knowall.findots.locationUtils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.knowall.findots.FinDotsApplication;
import com.knowall.findots.database.DataHelper;
import com.knowall.findots.locationUtils.LocationModel.LocationData;
import com.knowall.findots.utils.GeneralUtils;


public class TrackLocationService extends Service implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final long SYNCHRONIZATION_INTERVAL = 1 * 60 * 1000;
    private static boolean isServiceRunning;
    private static final String TAG = TrackLocationService.class.getCanonicalName();
    private int notificationId = 9999;
    private GoogleApiClient googleApiClient;

    private FinDotsApplication app;
    private DataHelper dataHelper;

    public static boolean isServiceRunning() {
        return isServiceRunning;
    }

    private static void setIsServiceRunning(boolean isServiceRunning) {
        TrackLocationService.isServiceRunning = isServiceRunning;
//        EventBus.getDefault().post(AppEvent.SERVICE_STATE_CHANGED);
    }

    public TrackLocationService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        app = (FinDotsApplication) getApplication();
        dataHelper = DataHelper.getInstance(getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createGoogleApiClient();
        connectGoogleApiClient();
        Log.d("jomy","Location Onstart..");
        TrackLocationService.setIsServiceRunning(true);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopLocationUpdates();
        cancelNotification();
        app.setStartLocation(null);

        TrackLocationService.setIsServiceRunning(false);
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onLocationChanged(Location location) {
//        Toast.makeText(this,"lati :"+location.getLongitude(),Toast.LENGTH_SHORT).show();
//        Log.d("jomy", "New Change Lati : "+ location.getLatitude()+" Longi : "+location.getLongitude());
        if (app.getStartLocation() == null) {
            app.setStartLocation(location);
        }
        updateLocationData(location);
    }

    @Override
    public void onConnected(Bundle bundle) {
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.d(TAG, "onConnectionFailed");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Log.d(TAG, "onTaskRemoved");
    }

    private void createGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    private void connectGoogleApiClient() {
        if (googleApiClient != null) {
            if (!(googleApiClient.isConnected() || googleApiClient.isConnecting())) {
                googleApiClient.connect();
            } else {
                Log.d(TAG, "Client is connected");
                startLocationUpdates();
            }
        } else {
            Log.d(TAG, "Client is null");
        }
    }

    private void startLocationUpdates() {


        try {
        LocationRequest locationRequest = app.createLocationRequest();
        LocationServices.FusedLocationApi.requestLocationUpdates(
                googleApiClient, locationRequest, this);
        scheduleDataSynchronization();
            }
        catch (SecurityException e){}
    }

    private void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                googleApiClient, this);
        stopDataSynchronization();
    }

    private void scheduleDataSynchronization() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, TrackLocationSyncReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
                SYNCHRONIZATION_INTERVAL, alarmIntent);
    }

    private void stopDataSynchronization() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, TrackLocationSyncReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        alarmManager.cancel(alarmIntent);
    }

    private void updateLocationData(Location location) {
        Location startLocation = app.getStartLocation();
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        String timeInGmt=GeneralUtils.DateTimeInUTC();
//        float distance = Utils.distFromCoordinates((float) startLocation.getLatitude(),
//                (float) startLocation.getLongitude(),
//                (float) latitude,
//                (float) longitude);

//        String timeText = Utils.formatTime(System.currentTimeMillis());

        dataHelper.saveLocation(LocationData.getInstance(latitude, longitude,timeInGmt));
//        updateNotification(timeText);
    }

    private void updateNotification(String text) {

//        NotificationCompat.Builder mBuilder =
//                new NotificationCompat.Builder(this)
//                        .setSmallIcon(R.drawable.ic_launcher)
//                        .setContentTitle(getString(R.string.app_name))
//                        .setContentText(text);
//
//        Intent resultIntent = new Intent(this, LocationTrackerActivity.class);
//        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
//        stackBuilder.addParentStack(LocationTrackerActivity.class);
//        stackBuilder.addNextIntent(resultIntent);
//        PendingIntent resultPendingIntent =
//                stackBuilder.getPendingIntent(
//                        0,
//                        PendingIntent.FLAG_UPDATE_CURRENT
//                );
//        mBuilder.setContentIntent(resultPendingIntent);
//
//        NotificationManager mNotificationManager =
//                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//        Notification notification = mBuilder.build();
//        mNotificationManager.notify(notificationId, notification);
    }

    private void cancelNotification() {
//        NotificationManager mNotificationManager =
//                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        mNotificationManager.cancel(notificationId);
    }

    private void synchronizeData() {
//        new AsyncTask<Void, Void, TrackLocationResponse>() {
//            private List<LocationData> locations;
//
//            @Override
//            protected TrackLocationResponse doInBackground(Void[] params) {
//                TrackLocationResponse response = null;
//
//                locations = dataHelper.getLocationsToSync();
//                if (locations != null && locations.size() > 0) {
//                    String deviceId = TrackLocationPreferencesManager.getDeviceId(getApplicationContext());
//                    String userName = TrackLocationPreferencesManager.getUserName(getApplicationContext());
//                    TrackLocationRequest request = TrackLocationRequest.getInstance(locations, deviceId, userName);
//                    ITrackLocationClient client = new TrackLocationClient();
//                    response = client.addTrack(request);
//                    if (response != null && response.getStatus() == TrackLocationResponse.RESPONSE_CODE_OK) {
//                        Log.d("TrackLocationSync", "Synced " + locations.size() + " items");
//                        dataHelper.markLocationsSynced(locations);
//                    }
//                } else {
//                    Log.d("TrackLocationSync", "No data to be synced");
//                }
//                return response;
//            }
//
//            @Override
//            protected void onPostExecute(TrackLocationResponse response) {
//                super.onPostExecute(response);
//
//                if (response != null && response.getStatus() == TrackLocationResponse.RESPONSE_CODE_OK) {
//                    String message = null;
//                    List<FriendResult> results = response.getResult();
//                    if (results != null && results.size() > 0) {
//                        StringBuilder messageBuilder = new StringBuilder();
//                        messageBuilder.append("Hi from ");
//                        for (FriendResult r : results) {
//                            messageBuilder.append(" ");
//                            messageBuilder.append(r.getTitle());
//                            messageBuilder.append(",");
//                        }
//                        messageBuilder.deleteCharAt(messageBuilder.length() - 1);
//                        message = messageBuilder.toString();
//                    } else {
//                        message = "Sync " + locations.size() + " items at " + Utils.formatTime(System.currentTimeMillis());
//                    }
//
//                    updateNotification(message);
//                }
//            }
//        }.execute();
    }

}
