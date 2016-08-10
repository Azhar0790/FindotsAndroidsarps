package com.knowall.findots.restcalls.getUser;

import android.content.Context;

import com.knowall.findots.Constants;
import com.knowall.findots.FinDotsApplication;
import com.knowall.findots.R;
import com.knowall.findots.utils.AppStringConstants;
import com.knowall.findots.utils.GeneralUtils;

import java.util.HashMap;
import java.util.Map;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by parijathar on 8/9/2016.
 */
public class GetUserRestCall {

    public IGetUser delegate = null;
    Context context = null;

    public GetUserRestCall(Context context) {
        this.context = context;
    }

    public void callGetUsers() {
        GeneralUtils.initialize_progressbar(context);

        Map<String, Object> postValues = getRequestForGetUsers();

        Call<GetUserModel> call = FinDotsApplication.getRestClient().getApiService().getUsers(postValues);

        call.enqueue(new Callback<GetUserModel>() {
            @Override
            public void onResponse(Response<GetUserModel> response, Retrofit retrofit) {
                GeneralUtils.stop_progressbar();

                if (response.isSuccess()) {
                    GetUserModel getUserModel = response.body();
                    delegate.onGetUserSuccess(getUserModel.getUserData());
                } else {
                    GetUserModel getUserModel = response.body();
                    delegate.onGetUserFailure(getUserModel.getMessage());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                GeneralUtils.stop_progressbar();
                delegate.onGetUserFailure(context.getString(R.string.data_error));
            }
        });
    }


    private Map<String, Object> getRequestForGetUsers() {

        int userID = GeneralUtils.getSharedPreferenceInt(context, AppStringConstants.USERID);

        Map<String, Object> postValues = new HashMap<>();
        postValues.put("deviceID", GeneralUtils.getUniqueDeviceId(context));
        postValues.put("appVersion", GeneralUtils.getAppVersion(context));
        postValues.put("deviceTypeID", Constants.DEVICETYPEID);
        postValues.put("deviceInfo", GeneralUtils.getDeviceInfo());
        postValues.put("userID", userID);
        return postValues;
    }
}
