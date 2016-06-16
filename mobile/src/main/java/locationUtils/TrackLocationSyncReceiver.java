package locationUtils;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.List;

import findots.bridgetree.com.findots.R;
import locationUtils.LocationModel.LocationData;


public class TrackLocationSyncReceiver extends BroadcastReceiver {
    private List<LocationData> locations;
    private DataHelper dataHelper;

    @Override
    public void onReceive(final Context context, Intent intent) {
        dataHelper = DataHelper.getInstance(context);
        locations = dataHelper.getLocationsToSync();
        for (LocationData d : locations) {
            Log.d("jomy","Lat : "+ d.getLatitude()+"  : Longit : "+d.getLongitude());
            Log.d("jomy","Lat : "+ d.getTimestamp()+"  : Longit : "+d.getLocationAddress());
        }
//        new AsyncTask<Void, Void, TrackLocationResponse>() {
//            private List<LocationData> locations;
//            private DataHelper dataHelper;
//
//            @Override
//            protected TrackLocationResponse doInBackground(Void[] params) {
//                TrackLocationResponse response = null;
//                dataHelper = DataHelper.getInstance(context);
//                locations = dataHelper.getLocationsToSync();
//                if (locations != null && locations.size() > 0) {
//                    String deviceId = TrackLocationPreferencesManager.getDeviceId(context);
//                    String userName = TrackLocationPreferencesManager.getUserName(context);
//                    TrackLocationRequest request = TrackLocationRequest.getInstance(locations, deviceId, userName);
//                    ITrackLocationClient client = new TrackLocationClient();
//                    response = client.addTrack(request);
//                    if (response != null && response.getStatus() == TrackLocationResponse.RESPONSE_CODE_OK) {
//                        Log.d("TrackLocationSync", "Synced " + locations.size() + " items");
//                        dataHelper.markLocationsSynced(locations);
//                    }
//                } else {
//                    Log.d("TrackLocationSync", "No data to be Synced...");
//                }
//                return response;
//            }
//
//            @Override
//            protected void onPostExecute(TrackLocationResponse response) {
//                super.onPostExecute(response);
//
//                if (response != null && response.getStatus() == TrackLocationResponse.RESPONSE_CODE_OK) {
//                    String message = "Synchronized " + locations.size() + " items at " + Utils.formatTime(System.currentTimeMillis());
//                    updateNotification(message, context);
//                }
//            }
//        }.execute();
        updateNotification("helloo", context);

    }

    private void updateNotification(String text, Context context) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(context.getString(R.string.app_name))
                        .setContentText(text);

        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notification = mBuilder.build();
        mNotificationManager.notify(1234, notification);
    }
}
