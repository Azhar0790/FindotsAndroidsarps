package findots.bridgetree.com.findots;

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
import com.google.android.gms.location.LocationRequest;

import locationUtils.LocationModel.LocationData;
import locationUtils.LocationRequestData;
import locationUtils.TrackLocationPreferencesManager;
import locationUtils.Utils;
import restservice.RestClient;

/**
 * Created by parijathar on 5/31/2016.
 */
public class FinDotsApplication extends MultiDexApplication {

    private static final String KEY_REQUEST_DATA_NAME = "KEY_REQUEST_DATA_NAME";
    public static RestClient restClient = null;
    private LocationRequestData locationRequestData;
    private Location startLocation;

    @Override
    public void onCreate() {
        super.onCreate();
        restClient = new RestClient();
        checkAndSetDeviceId();
        checkAndSetUserName();

        if (!retrieveLocationRequestData()) {
            setLocationRequestData(LocationRequestData.FREQUENCY_LOW);
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
                setLocationRequestData(LocationRequestData.FREQUENCY_MEDIUM);
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
            String deviceId = Utils.getUniqueDeviceId(this);
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
        ActiveAndroid.initialize(configurationBuilder.create());
    }


}
