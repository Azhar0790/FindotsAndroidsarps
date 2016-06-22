package restcalls.checkInCheckOut;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import findots.bridgetree.com.findots.Constants;
import findots.bridgetree.com.findots.FinDotsApplication;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import utils.GeneralUtils;

/**
 * Created by parijathar on 6/22/2016.
 */
public class CheckInCheckOutRestCall {

    public ICheckInCheckOut delegate = null;
    Context context = null;

    public CheckInCheckOutRestCall(Context context) {
        this.context = context;
    }

    public void callCheckInService(boolean isCheckIn, long userID) {
        GeneralUtils.initialize_progressbar(context);

        Map<String, Object> postValues = getDestinationsRequest(userID);

        Call<CheckInCheckOutModel> call = null;

        if (isCheckIn) {
            call = FinDotsApplication.getRestClient().getApiService().checkIn(postValues);
        } else {
            call = FinDotsApplication.getRestClient().getApiService().checkOut(postValues);
        }

        call.enqueue(new Callback<CheckInCheckOutModel>() {
            @Override
            public void onResponse(Response<CheckInCheckOutModel> response, Retrofit retrofit) {
                GeneralUtils.stop_progressbar();
                if (response.isSuccess()) {
                    delegate.onCheckInSuccess();
                } else {
                    delegate.onCheckInFailure();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                GeneralUtils.stop_progressbar();
                delegate.onCheckInFailure();
            }
        });
    }

    private Map<String, Object> getDestinationsRequest(long userID) {

        List<Map<String, Object>> list = new ArrayList<>();

        /**
         *   fetch all the checkIn details from database If any
         *   and create request
         */
        Map<String, Object> checkInValues = new HashMap<>();
        checkInValues.put("assignDestinationID", 0);
        checkInValues.put("reportedTime", "");

        list.add(checkInValues);

        Map<String, Object> postValues = new HashMap<>();
        postValues.put("checkedInsandOuts", list);
        postValues.put("appVersion", GeneralUtils.getAppVersion(context));
        postValues.put("deviceTypeID", Constants.DEVICETYPEID);
        postValues.put("deviceInfo", GeneralUtils.getDeviceInfo());
        postValues.put("userID", 2);
        return postValues;
    }

}
