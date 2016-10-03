package com.knowall.findots.notifications;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.knowall.findots.Constants;
import com.knowall.findots.FinDotsApplication;
import com.knowall.findots.restmodels.ResponseModel;
import com.knowall.findots.utils.AppStringConstants;
import com.knowall.findots.utils.GeneralUtils;

import java.util.HashMap;
import java.util.Map;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by jpaulose on 9/27/2016.
 */
public class FCM_NotificationInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();


        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        if(((GeneralUtils.getSharedPreferenceInt(this, AppStringConstants.USERID))>-1))
        sendRegistrationToServer(refreshedToken);
    }
    // [END refresh_token]

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param refreshedToken The new token.
     */
    private void sendRegistrationToServer(String refreshedToken) {
        Log.d(TAG, "Refreshed token: " + refreshedToken);
        Call<ResponseModel> tokenUpdateCall = FinDotsApplication.getRestClient().getApiService().setNotification_RefreshedToken(getTokenUpdateRequest(refreshedToken));


        tokenUpdateCall.enqueue(new Callback<ResponseModel>() {
                                   @Override
                                   public void onResponse(Response<ResponseModel> response, Retrofit retrofit) {
                                       if(response.body()!=null) {
                                           if (response.isSuccess() && response.body().getErrorCode() == 0) {


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

    private Map<String, Object> getTokenUpdateRequest(String refreshedToken) {

        int userID = GeneralUtils.getSharedPreferenceInt(this, AppStringConstants.USERID);

        Map<String, Object> postValues = new HashMap<>();
        postValues.put("appVersion", GeneralUtils.getAppVersion(this));
        postValues.put("deviceTypeID", Constants.DEVICETYPEID);
        postValues.put("deviceInfo", GeneralUtils.getDeviceInfo());
        postValues.put("deviceID", ""+refreshedToken);
        postValues.put("userID", 240);
        postValues.put("ipAddress", "");
        return postValues;
    }
}
