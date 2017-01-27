package com.knowall.findots.restcalls.login;

import android.content.Context;
import android.telephony.PhoneNumberUtils;
import android.util.Patterns;

import com.google.firebase.iid.FirebaseInstanceId;
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

import static android.util.Patterns.EMAIL_ADDRESS;

/**
 * Created by parijathar on 6/17/2016.
 */
public class LoginRestCall {

    public ILoginRestCall delegate = null;
    Context context = null;

    public LoginRestCall(Context context) {
        this.context = context;
    }


    public void callLoginService(String username, String password, boolean isEmailID, boolean isMobileNumber) {

        GeneralUtils.initialize_progressbar(context);

        Map<String, Object> postValues = getLoginRequest(username, password, isEmailID, isMobileNumber);

        Call<LoginModel> call = FinDotsApplication.getRestClient().getApiService().login(postValues);
        call.enqueue(new Callback<LoginModel>() {
            @Override
            public void onResponse(Response<LoginModel> response, Retrofit retrofit) {
                GeneralUtils.stop_progressbar();
                if(response.body()!=null) {
                    if (response.isSuccess()) {
                        LoginModel loginModel = response.body();
                        delegate.onLoginSuccess(loginModel);
                    } else {
                        LoginModel loginModel = response.body();
                        delegate.onLoginFailure(loginModel.getMessage());
                    }

                }
                else
                {
                    delegate.onLoginFailure(context.getString(R.string.data_error));
                }
            }

            @Override
            public void onFailure(Throwable t) {
                GeneralUtils.stop_progressbar();
                delegate.onLoginFailure(context.getString(R.string.data_error));
            }
        });

    }


    private Map<String, Object> getLoginRequest(String username, String password, boolean isEmailID, boolean isMobileNumber) {

        int userID = GeneralUtils.getSharedPreferenceInt(context, AppStringConstants.USERID);

        Map<String, Object> postValues = new HashMap<>();

        if (isEmailID) {
            postValues.put("email", username);
            postValues.put("mobileNumber", "");
        } else if (isMobileNumber) {
            postValues.put("email", "");
            postValues.put("mobileNumber", username);
        }

        postValues.put("password", password);
        postValues.put("deviceID", FirebaseInstanceId.getInstance().getToken());
        postValues.put("appVersion", GeneralUtils.getAppVersion(context));
        postValues.put("deviceTypeID", Constants.DEVICETYPEID);
        postValues.put("deviceInfo", GeneralUtils.getDeviceInfo());
        postValues.put("userID", userID);

        return postValues;
    }



}
