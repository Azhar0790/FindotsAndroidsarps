package com.knowall.findots.restcalls.history;

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
 * Created by parijathar on 7/27/2016.
 */
public class HistoryRestCall {

    public IHistory delegate = null;
    Context context = null;

    public HistoryRestCall(Context context) {
        this.context = context;
    }

    public void callGetReport(String startDate, String endDate) {

        GeneralUtils.initialize_progressbar(context);

        Map<String, Object> postValues = getHistoryRequest(startDate, endDate);

        Call<HistoryModel> call = FinDotsApplication.getRestClient().getApiService().getHistory(postValues);

        call.enqueue(new Callback<HistoryModel>() {
            @Override
            public void onResponse(Response<HistoryModel> response, Retrofit retrofit) {
                GeneralUtils.stop_progressbar();
                if(response.body()!=null) {
                    if (response.isSuccess()) {
                        HistoryModel historyModel = response.body();
                        delegate.onHistorySuccess(historyModel);
                    } else {
                        delegate.onHistoryFailure(context.getString(R.string.data_error));
                    }
                } else {
                    delegate.onHistoryFailure(context.getString(R.string.data_error));
                }
            }

            @Override
            public void onFailure(Throwable t) {
                GeneralUtils.stop_progressbar();
                delegate.onHistoryFailure(context.getString(R.string.data_error));
            }
        });

    }

    private Map<String, Object> getHistoryRequest(String startDate, String endDate) {

        int userID = GeneralUtils.getSharedPreferenceInt(context, AppStringConstants.USERID);

        Map<String, Object> postValues = new HashMap<>();
        postValues.put("startDate", startDate);
        postValues.put("endDate", endDate);
        postValues.put("deviceID", GeneralUtils.getUniqueDeviceId(context));
        postValues.put("appVersion", GeneralUtils.getAppVersion(context));
        postValues.put("deviceTypeID", Constants.DEVICETYPEID);
        postValues.put("deviceInfo", GeneralUtils.getDeviceInfo());
        postValues.put("userID", userID);

        return postValues;
    }
}
