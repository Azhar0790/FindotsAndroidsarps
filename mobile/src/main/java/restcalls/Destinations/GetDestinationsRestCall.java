package restcalls.destinations;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

import findots.bridgetree.com.findots.Constants;
import findots.bridgetree.com.findots.FinDotsApplication;
import findots.bridgetree.com.findots.R;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import utils.GeneralUtils;

/**
 * Created by parijathar on 6/20/2016.
 */
public class GetDestinationsRestCall {

    public IGetDestinations delegate = null;
    Context context = null;

    public GetDestinationsRestCall(Context context) {
        this.context = context;
    }

    public void callGetDestinations(String appVersion,
                                    String deviceTypeID, String deviceInfo,
                                    long userID) {

        GeneralUtils.initialize_progressbar(context);

        Map<String, Object> postValues = getDestinationsRequest(appVersion, deviceTypeID, deviceInfo, userID);

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

    private Map<String, Object> getDestinationsRequest(String appVersion,
                                                         String deviceTypeID, String deviceInfo,
                                                         long userID) {
        Map<String, Object> postValues = new HashMap<>();
        postValues.put("appVersion", GeneralUtils.getAppVersion(context));
        postValues.put("deviceTypeID", Constants.DEVICETYPEID);
        postValues.put("deviceInfo", GeneralUtils.getDeviceInfo());
        postValues.put("userID", 3);
        return postValues;
    }
}
