package offlineServices;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import database.DataHelper;
import database.dataModel.checkIn;
import de.greenrobot.event.EventBus;
import events.AppEvents;
import findots.bridgetree.com.findots.Constants;
import findots.bridgetree.com.findots.FinDotsApplication;
import restcalls.checkInCheckOut.CheckInCheckOutModel;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import utils.AppStringConstants;
import utils.GeneralUtils;

/**
 * Created by jpaulose on 6/30/2016.
 */
public class CheckInCheckOutOfflineRestCall {

    Context context = null;


    public CheckInCheckOutOfflineRestCall(Context context) {
        this.context = context;
    }

    public void callCheckInOutOfflineService(final boolean isCheckIn) {

        Map<String, Object> postValues = getDestinationsRequest(isCheckIn);

        Call<CheckInCheckOutModel> call = null;

        if (!isCheckIn) {
            call = FinDotsApplication.getRestClient().getApiService().checkIn(postValues);
        } else {
            call = FinDotsApplication.getRestClient().getApiService().checkOut(postValues);
        }

        call.enqueue(new Callback<CheckInCheckOutModel>() {
            @Override
            public void onResponse(Response<CheckInCheckOutModel> response, Retrofit retrofit) {

                if (response.isSuccess()) {
                    DataHelper dataHelper = DataHelper.getInstance(context);
                    if (!isCheckIn) {
                        EventBus.getDefault().post(AppEvents.OFFLINECHECKIN);
                        Log.d("jomy","call Offline");
                        dataHelper.deleteCheckinList();
                    }
                    else {
                        EventBus.getDefault().post(AppEvents.OFFLINECHECKOUT);
                        Log.d("jomy","call Offlineout");
                        dataHelper.deleteCheckoutList();
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }

    private Map<String, Object> getDestinationsRequest(boolean isCheckIn) {

        /**
         *   fetch current lat and lng
         */

        DataHelper dataHelper = DataHelper.getInstance(context);
        List<checkIn> checkInList;
        if (!isCheckIn) {
            checkInList = dataHelper.getCheckInListToSync();
        } else
            checkInList = dataHelper.getCheckOutListToSync();

        List<Map<String, Object>> list = new ArrayList<>();

        /**
         *   fetch all the checkIn details from database If any
         *   and create request
         */
        for (checkIn checkInData : checkInList) {
            Map<String, Object> checkInValues = new HashMap<>();
            checkInValues.put("assignDestinationID", checkInData.getAssigned_destinationId());
            checkInValues.put("reportedTime", checkInData.getReportedTime());
            checkInValues.put("checkedInOutLatitude", checkInData.getCheckInOUTlatitude());
            checkInValues.put("checkedInOutLongitude", checkInData.getCheckInOUTlongitude());
            list.add(checkInValues);
        }


        int userID = GeneralUtils.getSharedPreferenceInt(context, AppStringConstants.USERID);

        Map<String, Object> postValues = new HashMap<>();
        postValues.put("checkedInsandOuts", list);
        postValues.put("appVersion", GeneralUtils.getAppVersion(context));
        postValues.put("deviceTypeID", Constants.DEVICETYPEID);
        postValues.put("deviceInfo", GeneralUtils.getDeviceInfo());
        postValues.put("userID", userID);
        return postValues;
    }

}
