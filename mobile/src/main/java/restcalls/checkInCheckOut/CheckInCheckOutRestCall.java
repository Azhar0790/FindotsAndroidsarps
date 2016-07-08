package restcalls.checkInCheckOut;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import database.DataHelper;
import database.dataModel.checkIn;
import findots.bridgetree.com.findots.Constants;
import findots.bridgetree.com.findots.FinDotsApplication;
import findots.bridgetree.com.findots.R;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import utils.AppStringConstants;
import utils.GeneralUtils;
import utils.NetworkChangeReceiver;

/**
 * Created by parijathar on 6/22/2016.
 */
public class CheckInCheckOutRestCall {

    public ICheckInCheckOut delegate = null;
    Context context = null;
    double currentLatitude = 0, currentLongitude = 0;
    String reportedTime;
    int assignedDestinationID;


    public CheckInCheckOutRestCall(Context context) {
        this.context = context;
    }

    public void callCheckInService(final boolean isCheckIn, final int assignedDestinationID,double lattitude,double longitude) {
        this.assignedDestinationID = assignedDestinationID;
        currentLatitude =lattitude;
        currentLongitude = longitude;
        GeneralUtils.initialize_progressbar(context);

        Map<String, Object> postValues = getDestinationsRequest(assignedDestinationID);

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
                if (response.isSuccess()) {
                    delegate.onCheckInSuccess();
                } else {
                    if (!isCheckIn) {
                        delegate.onCheckInFailure(context.getString(R.string.checkin_failed));
                    } else {
                        delegate.onCheckInFailure(context.getString(R.string.checkout_failed));
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                GeneralUtils.stop_progressbar();
                if (!NetworkChangeReceiver.isNetworkAvailable(context)) {
                    checkIn checkInData = checkIn.getInstance(assignedDestinationID, reportedTime, currentLatitude, currentLongitude, isCheckIn);
                    DataHelper dataHelper = DataHelper.getInstance(context);
                    dataHelper.saveCheckInOut(checkInData);
                }
                delegate.onCheckInFailure(context.getString(R.string.failed));
            }
        });
    }

    private Map<String, Object> getDestinationsRequest(int assignedDestinationID) {

        /**
         *   fetch current lat and lng
         */
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        try {
            currentLatitude = location.getLatitude();
            currentLongitude = location.getLongitude();
        }
        catch(Exception e)
        {
            currentLatitude = 12.777;
            currentLongitude = 77.896;
        }

        /**
         *   fetching current time in UTC format
         */
        DateTimeFormatter fmt1 = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS");
        DateTime dateTime = new DateTime();
        reportedTime = dateTime.toString(fmt1);

        List<Map<String, Object>> list = new ArrayList<>();

        /**
         *   fetch all the checkIn details from database If any
         *   and create request
         */
        Map<String, Object> checkInValues = new HashMap<>();
        checkInValues.put("assignDestinationID", assignedDestinationID);
        checkInValues.put("reportedTime", reportedTime);
        checkInValues.put("checkedInOutLatitude", currentLatitude);
        checkInValues.put("checkedInOutLongitude", currentLongitude);

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
