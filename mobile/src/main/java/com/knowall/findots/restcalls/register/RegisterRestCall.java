package com.knowall.findots.restcalls.register;

import android.content.Context;

import com.knowall.findots.Constants;
import com.knowall.findots.FinDotsApplication;
import com.knowall.findots.R;
import com.knowall.findots.utils.GeneralUtils;

import java.util.HashMap;
import java.util.Map;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by parijathar on 6/17/2016.
 */
public class RegisterRestCall {

    public IRegisterRestCall delegate = null;
    Context context = null;

    public RegisterRestCall(Context context) {
        this.context = context;
    }

    public void callRegisterUserService(String email, String password, String mobile,
                                        String name, String redeemCode, String company,
                                        double lat, double lng) {
        GeneralUtils.initialize_progressbar(context);

        Map<String, Object> postValues = getRegisterUserRequest(email, password, mobile,
                name, redeemCode, company, lat, lng);

        Call<RegisterModel> call = FinDotsApplication.getRestClient().getApiService().registerUser(postValues);
        call.enqueue(new Callback<RegisterModel>() {
            @Override
            public void onResponse(Response<RegisterModel> response, Retrofit retrofit) {
                GeneralUtils.stop_progressbar();
                if (response.body() != null) {
                    if (response.isSuccess()) {
                        RegisterModel registerModel = response.body();
                        delegate.onRegisterUserSuccess(registerModel);
                    } else {
                        RegisterModel registerModel = response.body();
                        delegate.onRegisterUserFailure(registerModel.getMessage());
                    }
                } else {
                    delegate.onRegisterUserFailure(context.getString(R.string.data_error));
                }
            }

            @Override
            public void onFailure(Throwable t) {
                GeneralUtils.stop_progressbar();
                delegate.onRegisterUserFailure(context.getString(R.string.data_error));
            }
        });

    }

    private Map<String, Object> getRegisterUserRequest(String email, String password, String mobile,
                                                       String name, String redeemCode, String company,
                                                       double lat, double lng) {
        Map<String, Object> postValues = new HashMap<>();
        postValues.put("email", email);
        postValues.put("password", password);
        postValues.put("mobileNumber", mobile);
        postValues.put("name", name);
        postValues.put("redeemCode", redeemCode);
        postValues.put("company", "");
        postValues.put("deviceID", GeneralUtils.getUniqueDeviceId(context));
        postValues.put("appVersion", GeneralUtils.getAppVersion(context));
        postValues.put("deviceTypeID", Constants.DEVICETYPEID);
        postValues.put("deviceInfo", GeneralUtils.getDeviceInfo());
        postValues.put("userID", 0);
        postValues.put("latitude", lat);
        postValues.put("longitude", lng);
        postValues.put("userTypeID", 2);
        postValues.put("organizationID", 0);
        postValues.put("countryID", 0);
        postValues.put("city", "");
        postValues.put("pincode", "");
        postValues.put("address", "");
        postValues.put("contactPerson", "");
        postValues.put("fbLoginID", "");
        postValues.put("googleID", "");
        postValues.put("corporateAdminUserID", 0);
        postValues.put("base64String", "");

        return postValues;
    }
}
