package com.knowall.findots.locationUtils;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.knowall.findots.Constants;
import com.knowall.findots.R;
import com.knowall.findots.database.DataHelper;
import com.knowall.findots.locationUtils.LocationModel.BackgroundLocData;
import com.knowall.findots.locationUtils.LocationModel.LocationData;
import com.knowall.findots.locationUtils.LocationModel.LocationSyncData;
import com.knowall.findots.restmodels.ResponseModel;
import com.knowall.findots.restservice.RestClient;
import com.knowall.findots.utils.AppStringConstants;
import com.knowall.findots.utils.GeneralUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;


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
                    Call<ResponseModel> bglocationCall = restClient.getApiService().saveLocationPath(bgData);


                    bglocationCall.enqueue(new Callback<ResponseModel>() {
                                      @Override
                                      public void onResponse(Response<ResponseModel> response, Retrofit retrofit) {
                                          if(response.body()!=null) {
                                              if (response.isSuccess() && response.body().getErrorCode() == 0) {

                                                  dataHelper.markLocationsSynced(locations);
                                                  Log.d("jomy", "inside success : " + response.body().getMessage());
                                              } else {
                                                  Log.e("data", "inside failure");

                                              }
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
