package com.knowall.findots;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.preference.PreferenceManager;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;
import android.util.Log;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Configuration;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.StandardExceptionParser;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.location.LocationRequest;
import com.knowall.findots.database.DataHelper;
import com.knowall.findots.database.dataModel.checkIn;
import com.knowall.findots.locationUtils.LocationModel.LocationData;
import com.knowall.findots.locationUtils.LocationRequestData;
import com.knowall.findots.locationUtils.TrackLocationPreferencesManager;
import com.knowall.findots.restservice.RestClient;
import com.knowall.findots.utils.AnalyticsTrackers;
import com.knowall.findots.utils.GeneralUtils;

import java.util.List;

/**
 * Created by parijathar on 5/31/2016.
 */
public class FinDotsApplication extends MultiDexApplication {

    private static final String KEY_REQUEST_DATA_NAME = "KEY_REQUEST_DATA_NAME";
    public static RestClient restClient = null;
    private LocationRequestData locationRequestData;
    private Location startLocation;
    private static FinDotsApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        restClient = new RestClient();
        checkAndSetDeviceId();
        checkAndSetUserName();
        AnalyticsTrackers.initialize(this);
        AnalyticsTrackers.getInstance().get(AnalyticsTrackers.Target.APP);

        if (!retrieveLocationRequestData()) {
            setLocationRequestData(LocationRequestData.FREQUENCY_HIGH);
        }
        initializeDB();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
        Log.i(Constants.TAG, "MultiDexApplication install is done");
    }

    public static RestClient getRestClient() {
        if(restClient==null)
            restClient = new RestClient();
        return restClient;
    }

    private boolean retrieveLocationRequestData() {
        String name = PreferenceManager.getDefaultSharedPreferences(this).getString(KEY_REQUEST_DATA_NAME, null);
        if (!TextUtils.isEmpty(name)) {
            LocationRequestData data = LocationRequestData.valueOf(name);
            if (data != null) {
                locationRequestData = data;
                return true;
            }
        }
        return false;
    }

    public void setLocationRequestData(LocationRequestData requestData) {
        locationRequestData = requestData;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferences.edit().putString(KEY_REQUEST_DATA_NAME, requestData.name()).apply();
    }

    public LocationRequestData getLocationRequestData() {
        if (locationRequestData == null) {
            if (!retrieveLocationRequestData()) {
                setLocationRequestData(LocationRequestData.FREQUENCY_HIGH);
            }
        }
        return locationRequestData;
    }

    public Location getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(Location startLocation) {
        this.startLocation = startLocation;
    }

    public LocationRequest createLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(locationRequestData.getInterval());
        locationRequest.setFastestInterval(locationRequestData.getFastestInterval());
        locationRequest.setPriority(locationRequestData.getPriority());
        locationRequest.setSmallestDisplacement(locationRequestData.getSmallestDisplacement());
        return locationRequest;
    }

    private void checkAndSetDeviceId() {
        if (TextUtils.isEmpty(TrackLocationPreferencesManager.getDeviceId(this))) {
            String deviceId = GeneralUtils.getUniqueDeviceId(this);
            TrackLocationPreferencesManager.setDeviceId(deviceId, this);
        }
    }

    private void checkAndSetUserName() {
        if (TextUtils.isEmpty(TrackLocationPreferencesManager.getUserName(this))) {
            String userName = android.os.Build.MODEL;
            TrackLocationPreferencesManager.setUserName(userName, this);
        }
    }

    private void initializeDB() {
        Configuration.Builder configurationBuilder = new Configuration.Builder(this);
        configurationBuilder.addModelClasses(LocationData.class);
        configurationBuilder.addModelClasses(checkIn.class);

        ActiveAndroid.initialize(configurationBuilder.create());
    }

    public static void getDatabaseInfo(Context context)
    {
        DataHelper dataHelper = DataHelper.getInstance(context);
        List<LocationData> locations = dataHelper.getLocationsToSync();

        for (LocationData locData : locations) {
         Log.d("jomy"," Lat : "+locData.getLatitude()+"  Long:  "+locData.getLongitude()+"  RepTime : "+locData.getTimestamp());


        }
        List<LocationData> locations1 = dataHelper.getLocationLastRecord();
        for (LocationData locData1 : locations1) {
            Log.d("jomy"," Latt23 : "+locData1.getLatitude()+"  Long:  "+locData1.getLongitude()+"  RepTime : "+locData1.getTimestamp());


        }



        List<checkIn> checkInlist = dataHelper.getCheckInListToSync();

        for ( checkIn  checkInData : checkInlist)
            Log.d("jomy", " destinationId : " + checkInData.getAssigned_destinationId() + "  ReportedTime:  " + checkInData.getReportedTime());


        List<checkIn> checkInlist1 = dataHelper.getCheckOutListToSync();
            for ( checkIn  checkInData1 : checkInlist1)
                Log.d("jomy"," destinationId : "+checkInData1.getAssigned_destinationId()+"  ReportedTime:  "+checkInData1.getReportedTime());

    }


    public static synchronized FinDotsApplication getInstance() {
        return mInstance;
    }

    public synchronized Tracker getGoogleAnalyticsTracker() {
        AnalyticsTrackers analyticsTrackers = AnalyticsTrackers.getInstance();
        return analyticsTrackers.get(AnalyticsTrackers.Target.APP);
    }

    /***
     * Tracking screen view
     *
     * @param screenName screen name to be displayed on GA dashboard
     */
    public void trackScreenView(String screenName) {
        Tracker t = getGoogleAnalyticsTracker();

        // Set screen name.
        t.setScreenName(screenName);

        // Send a screen view.
        t.send(new HitBuilders.ScreenViewBuilder().build());

        GoogleAnalytics.getInstance(this).dispatchLocalHits();
    }

    /***
     * Tracking exception
     *
     * @param e exception to be tracked
     */
    public void trackException(Exception e) {
        if (e != null) {
            Tracker t = getGoogleAnalyticsTracker();

            t.send(new HitBuilders.ExceptionBuilder()
                    .setDescription(
                            new StandardExceptionParser(this, null)
                                    .getDescription(Thread.currentThread().getName(), e))
                    .setFatal(false)
                    .build()
            );
        }
    }

    /***
     * Tracking event
     *
     * @param category event category
     * @param action   action of the event
     * @param label    label
     */
    public void trackEvent(String category, String action, String label) {
        Tracker t = getGoogleAnalyticsTracker();

        // Build and send an Event.
        t.send(new HitBuilders.EventBuilder().setCategory(category).setAction(action).setLabel(label).build());
    }
}
