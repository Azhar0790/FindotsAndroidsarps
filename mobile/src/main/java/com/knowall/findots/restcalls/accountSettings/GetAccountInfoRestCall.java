package com.knowall.findots.restcalls.accountSettings;

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
 * Created by jpaulose on 6/23/2016.
 */
public class GetAccountInfoRestCall {

    public IGetAccountInfoCallBack delegate = null;
    Context context = null;

    public GetAccountInfoRestCall(Context context) {
        this.context = context;
    }


    public void callGetAccountInfoService() {

        GeneralUtils.initialize_progressbar(context);

        Map<String, Object> postValues = getAccountInfoRequest();

        Call<GetAccountInfoModel> call = FinDotsApplication.getRestClient().getApiService().getAccountInfo(postValues);
        call.enqueue(new Callback<GetAccountInfoModel>() {
            @Override
            public void onResponse(Response<GetAccountInfoModel> response, Retrofit retrofit) {
                GeneralUtils.stop_progressbar();
                if (response.isSuccess()) {
                    GetAccountInfoModel accountInfo = response.body();
                    delegate.onGetAccountInfoSuccess(accountInfo);
                } else {
                    GetAccountInfoModel accountInfo = response.body();
                    delegate.onGetAccountInfoFailure(accountInfo.getMessage());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                GeneralUtils.stop_progressbar();
                delegate.onGetAccountInfoFailure(context.getString(R.string.data_error));
            }
        });

    }


    private Map<String, Object> getAccountInfoRequest() {
        Map<String, Object> postValues = new HashMap<>();
        postValues.put("appVersion", GeneralUtils.getAppVersion(context));
        postValues.put("deviceTypeID", Constants.DEVICETYPEID);
        postValues.put("deviceInfo", GeneralUtils.getDeviceInfo());
        postValues.put("userID", GeneralUtils.getSharedPreferenceInt(context, AppStringConstants.USERID));
        postValues.put("deviceID", GeneralUtils.getUniqueDeviceId(context));

        return postValues;
    }
}