package locationUtils.LocationModel;

import java.util.ArrayList;

/**
 * Created by jpaulose on 6/7/2016.
 */
public class BackgroundLocData {
    String deviceID;
    String appVersion;
    String deviceInfo;
    int deviceTypeID;
    int userID;
    String ipAddress="";

    ArrayList<LocationSyncData> locations=new ArrayList<LocationSyncData>();



    public String getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(String deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public int getDeviceTypeID() {
        return deviceTypeID;
    }

    public void setDeviceTypeID(int deviceTypeID) {
        this.deviceTypeID = deviceTypeID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }
    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public ArrayList<LocationSyncData> getLocations() {
        return locations;
    }

    public void setLocations(ArrayList<LocationSyncData> locations) {
        this.locations = locations;
    }

}
