package com.knowall.findots.restcalls.destinations;

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
 * Created by parijathar on 6/20/2016.
 */
public class GetDestinationsRestCall {

    public IGetDestinations delegate = null;
    Context context = null;

    public GetDestinationsRestCall(Context context) {
        this.context = context;
    }

    public void callGetDestinations() {

        GeneralUtils.initialize_progressbar(context);

        Map<String, Object> postValues = getDestinationsRequest();

        Call<DestinationsModel> call = FinDotsApplication.getRestClient().getApiService().getDestinations(postValues);
        call.enqueue(new Callback<DestinationsModel>() {
            @Override
            public void onResponse(Response<DestinationsModel> response, Retrofit retrofit) {
                GeneralUtils.stop_progressbar();
                if (response.isSuccess()) {
                    DestinationsModel destinationsModel = response.body();
                    delegate.onGetDestinationSuccess(destinationsModel);
                } else {
                    DestinationsModel destinationsModel = response.body();
                    delegate.onGetDestinationFailure(destinationsModel.getMessage());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                GeneralUtils.stop_progressbar();
                delegate.onGetDestinationFailure(context.getString(R.string.data_error));
            }
        });


    }

    private Map<String, Object> getDestinationsRequest() {

        int userID = GeneralUtils.getSharedPreferenceInt(context, AppStringConstants.USERID);

        Map<String, Object> postValues = new HashMap<>();
        postValues.put("appVersion", GeneralUtils.getAppVersion(context));
        postValues.put("deviceTypeID", Constants.DEVICETYPEID);
        postValues.put("deviceInfo", GeneralUtils.getDeviceInfo());
        postValues.put("deviceID", GeneralUtils.getUniqueDeviceId(context));
        postValues.put("userID", userID);
        return postValues;
    }
}
