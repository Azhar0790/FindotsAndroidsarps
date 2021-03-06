package com.knowall.findots.database;

import android.content.Context;
import android.net.Uri;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.knowall.findots.database.dataModel.checkIn;
import com.knowall.findots.locationUtils.LocationModel.LocationData;

import java.util.List;


public class DataHelper {

    private static DataHelper instance;
    private Context context;

    private DataHelper(Context context) {
        this.context = context;
    }

    public static synchronized DataHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DataHelper(context);
        }
        return instance;
    }

    public void saveLocation(LocationData data) {
        data.save();
        notifyChanged(Data.LocationData.URI);
    }

    public List<LocationData> getAllLocations() {
        List<LocationData> data = new Select().from(LocationData.class).execute();
        return data;
    }

    public LocationData getLastLocation() {
        List<LocationData> data = new Select().from(LocationData.class)
                .orderBy(Data.LocationData.COLUMN_TIMESTAMP + " DESC")
                .limit(String.valueOf(1)).execute();
        LocationData locationData = data != null && data.size() > 0 ? data.get(0) : null;
        return locationData;
    }

    public List<LocationData> getLastLocations(int lastMilliSeconds) {
        List<LocationData> data = new Select().from(LocationData.class)
                .orderBy(Data.LocationData.COLUMN_TIMESTAMP + " DESC")
                .where(Data.LocationData.COLUMN_TIMESTAMP + " > " + (System.currentTimeMillis() - lastMilliSeconds))
                .execute();
        return data;
    }

    public List<LocationData> getLastLocations(long from, long to) {
        List<LocationData> data = new Select().from(LocationData.class)
                .orderBy(Data.LocationData.COLUMN_TIMESTAMP + " DESC")
                .where(Data.LocationData.COLUMN_TIMESTAMP + " > " + from)
                .and(Data.LocationData.COLUMN_TIMESTAMP + " < " + to)
                .execute();
        return data;
    }

    public List<LocationData> getLocationsToSync() {
        List<LocationData> data = new Select().from(LocationData.class)
                .where(Data.LocationData.COLUMN_SYNCED + " = " + 0)
                .execute();
        return data;
    }

    public void markLocationsSynced(List<LocationData> locations) {
        for (LocationData location : locations) {
            markLocationSynced(location);
        }
    }

    public void markLocationSynced(LocationData location) {
        location.setSynced(true);
        location.delete();
    }

    public List<LocationData> getLocationLastRecord() {
        List<LocationData> data = new Select().from(LocationData.class)
                .orderBy("id DESC")
                .limit(1)
                .execute();
        return data;
    }

    public void deleteAllLocations() {
        new Delete().from(LocationData.class).execute();
    }

//    public void saveGeoPoint(GeoPoint geoPoint) {
//        geoPoint.save();
//    }

//    public List<GeoPoint> getAllGeoPoints() {
//        List<GeoPoint> points = new Select().from(GeoPoint.class).execute();
//        return points;
//    }

//    public void deleteAllGeoPoints() {
//        new Delete().from(GeoPoint.class).execute();
//    }

    public void saveCheckInOut(checkIn data) {
        data.save();
    }

    public List<checkIn> getCheckInListToSync() {
        List<checkIn> data = new Select().from(checkIn.class)
                .where(Data.CheckInData.COLUMN_ISCHECKEDIN + " = " + 0)
                .execute();
        return data;
    }
    public List<checkIn> getCheckOutListToSync() {
        List<checkIn> data = new Select().from(checkIn.class)
                .where(Data.CheckInData.COLUMN_ISCHECKEDIN + " = " + 1)
                .execute();
        return data;
    }

    public void deleteCheckinList() {
        new Delete().from(checkIn.class)
                .where(Data.CheckInData.COLUMN_ISCHECKEDIN + " = " + 0)
                .execute();

    }

    public void deleteCheckoutList() {
        new Delete().from(checkIn.class)
                .where(Data.CheckInData.COLUMN_ISCHECKEDIN + " = " + 1)
                .execute();

    }



    public void deleteAllCheckinInfo() {
        new Delete().from(checkIn.class).execute();
    }

    private void notifyChanged(Uri uri) {
        context.getContentResolver().notifyChange(uri, null);
    }

}
