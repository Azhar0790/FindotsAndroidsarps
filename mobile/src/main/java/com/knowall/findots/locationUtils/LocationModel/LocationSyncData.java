package com.knowall.findots.locationUtils.LocationModel;

/**
 * Created by jpaulose on 6/17/2016.
 */
public class LocationSyncData {


        double latitude;
        double longitude;
        String reportedDate;

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }


    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }



    public String getReportedDate() {
        return reportedDate;
    }

    public void setReportedDate(String reportedDate) {
        this.reportedDate = reportedDate;
    }
}
