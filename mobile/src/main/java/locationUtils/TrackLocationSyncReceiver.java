package locationUtils;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import database.DataHelper;
import findots.bridgetree.com.findots.Constants;
import findots.bridgetree.com.findots.R;
import locationUtils.LocationModel.BackgroundLocData;
import locationUtils.LocationModel.LocationData;
import locationUtils.LocationModel.LocationResponseData;
import locationUtils.LocationModel.LocationSyncData;
import restservice.RestClient;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import utils.AppStringConstants;
import utils.GeneralUtils;


public class TrackLocationSyncReceiver extends BroadcastReceiver {
    private List<LocationData> locations;
    private DataHelper dataHelper;

    @Override
    public void onReceive(final Context context, Intent intent) {

        new AsyncTask<Void, Void, Void>()
        {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Void doInBackground(Void... params) {

                dataHelper = DataHelper.getInstance(context);
                locations = dataHelper.getLocationsToSync();
                if(locations.size()>0) {
                    BackgroundLocData bgData = new BackgroundLocData();
                    bgData.setDeviceID(GeneralUtils.getUniqueDeviceId(context));
                    bgData.setDeviceInfo(GeneralUtils.getDeviceInfo());
                    bgData.setAppVersion(GeneralUtils.getAppVersion(context));
                    bgData.setUserID(GeneralUtils.getSharedPreferenceInt(context, AppStringConstants.USERID));
                    bgData.setDeviceTypeID(Constants.DEVICETYPEID);
                    ArrayList<LocationSyncData> locSyncList = new ArrayList<LocationSyncData>();

                    for (LocationData locData : locations) {
                        LocationSyncData locationSyncData = new LocationSyncData();
                        locationSyncData.setLatitude(locData.getLatitude());
                        locationSyncData.setLongitude(locData.getLongitude());
                        locationSyncData.setReportedDate(locData.getTimestamp());
                        locSyncList.add(locationSyncData);
                    }

                    bgData.setLocations(locSyncList);

                    /**
                     * Hit the Login API to get the Userdetails
                     */
                    RestClient restClient = new RestClient();
                    Call<LocationResponseData> bglocationCall = restClient.getApiService().saveLocationPath(bgData);


                    bglocationCall.enqueue(new Callback<LocationResponseData>() {
                                      @Override
                                      public void onResponse(Response<LocationResponseData> response, Retrofit retrofit) {

                                          if (response.isSuccess() && response.body().getErrorCode() == 0) {

                                              dataHelper.markLocationsSynced(locations);
                                              Log.d("jomy", "inside success : " + response.body().getMessage());
                                          } else {
                                              Log.e("data", "inside failure");

                                          }
                                      }

                                      @Override
                                      public void onFailure(Throwable t) {
                                          Log.d("jomy", "onFailure  : " + t.getMessage());
                                      }
                                  }

                    );
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
            }
        }.execute();






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

//    updateNotification("helloo",context);

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
