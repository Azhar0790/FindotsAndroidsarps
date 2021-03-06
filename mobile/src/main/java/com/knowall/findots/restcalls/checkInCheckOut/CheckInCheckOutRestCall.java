package com.knowall.findots.restcalls.checkInCheckOut;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;
import android.widget.Toast;

import com.knowall.findots.Constants;
import com.knowall.findots.FinDotsApplication;
import com.knowall.findots.R;
import com.knowall.findots.database.DataHelper;
import com.knowall.findots.database.dataModel.checkIn;
import com.knowall.findots.utils.AppStringConstants;
import com.knowall.findots.utils.GeneralUtils;
import com.knowall.findots.utils.NetworkChangeReceiver;
import com.knowall.findots.utils.timeUtils.TimeSettings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by parijathar on 6/22/2016.
 */
public class CheckInCheckOutRestCall {

    public ICheckInCheckOut delegate = null;
    Context context = null;
    String reportedTime;
    int assignedDestinationID;
    double currentLatitude, currentLongitude;


    public CheckInCheckOutRestCall(Context context) {
        this.context = context;
    }

    public void callCheckInService(String checkOutNote, final boolean isCheckIn, final int assignedDestinationID, double lattitude, double longitude) {
        this.assignedDestinationID = assignedDestinationID;
        GeneralUtils.initialize_progressbar(context);

        Map<String, Object> postValues = getDestinationsRequest(checkOutNote, assignedDestinationID,lattitude,longitude);

        Call<CheckInCheckOutModel> call = null;

        if (!isCheckIn) {
            call = FinDotsApplication.getRestClient().getApiService().checkIn(postValues);
        } else {
            call = FinDotsApplication.getRestClient().getApiService().checkOut(postValues);
        }

        call.enqueue(new Callback<CheckInCheckOutModel>() {
            @Override
            public void onResponse(Response<CheckInCheckOutModel> response, Retrofit retrofit) {
                GeneralUtils.stop_progressbar();
                if (response.body() != null) {
                    if (response.isSuccess()) {
                        delegate.onCheckInSuccess(response.body().getMessage());
                    } else {
                        if (!isCheckIn) {
                            delegate.onCheckInFailure(context.getString(R.string.checkin_failed));
                        } else {
                            delegate.onCheckInFailure(context.getString(R.string.data_error));
                        }
                    }
                } else
                    delegate.onCheckInFailure(context.getString(R.string.data_error));
            }

            @Override
            public void onFailure(Throwable t) {
                GeneralUtils.stop_progressbar();
                if (!NetworkChangeReceiver.isNetworkAvailable(context)) {
                    Log.d("jomy","No inter");
                    checkIn checkInData = checkIn.getInstance(assignedDestinationID, reportedTime, currentLatitude, currentLongitude, isCheckIn);
                    DataHelper dataHelper = DataHelper.getInstance(context);
                    dataHelper.saveCheckInOut(checkInData);
                    delegate.onCheckInFailure(context.getString(R.string.noInternet));
                } else {
                    Toast.makeText(context, context.getString(R.string.data_error), Toast.LENGTH_SHORT).show();
//                    delegate.onCheckInFailure(context.getString(R.string.data_error));
                }
            }
        });
    }

    private Map<String, Object> getDestinationsRequest(String checkOutNote, int assignedDestinationID,double latitude,double longitude) {

        currentLatitude = latitude;
        currentLongitude = longitude;
        /**
         *   fetch current lat and lng
         */

        if (currentLatitude == 0 || currentLongitude == 0)
        {
            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(location==null)
                location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
            try {
                currentLatitude = location.getLatitude();
                currentLongitude = location.getLongitude();
            } catch (Exception e) {
            }
    }

        /**
         *   fetching current time in UTC format
         */

        reportedTime = TimeSettings.DateTimeInUTC();

        List<Map<String, Object>> list = new ArrayList<>();

        /**
         *   fetch all the checkIn details from com.knowall.findots.database If any
         *   and create request
         */
        Map<String, Object> checkInValues = new HashMap<>();
        checkInValues.put("assignDestinationID", assignedDestinationID);
        checkInValues.put("reportedTime", reportedTime);
        checkInValues.put("checkedInOutLatitude", currentLatitude);
        checkInValues.put("checkedInOutLongitude", currentLongitude);
        checkInValues.put("checkoutComment", checkOutNote);

        list.add(checkInValues);

        int userID = GeneralUtils.getSharedPreferenceInt(context, AppStringConstants.USERID);

        Map<String, Object> postValues = new HashMap<>();
        postValues.put("checkedInsandOuts", list);
        postValues.put("appVersion", GeneralUtils.getAppVersion(context));
        postValues.put("deviceTypeID", Constants.DEVICETYPEID);
        postValues.put("deviceInfo", GeneralUtils.getDeviceInfo());
        postValues.put("deviceID", GeneralUtils.getUniqueDeviceId(context));
        postValues.put("userID", userID);
        return postValues;
    }

}
