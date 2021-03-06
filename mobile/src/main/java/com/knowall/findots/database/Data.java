package com.knowall.findots.database;

import android.net.Uri;

public class Data {

    private static final Uri URI_MAIN = Uri.parse("content://findots.bridgetree.com.findots");

    public static final class LocationData {
        public static final String TABLE = "LocationData";
        public static final Uri URI = Uri.withAppendedPath(URI_MAIN, TABLE);

        public static final String COLUMN_LATITUDE = "COLUMN_LATITUDE";
        public static final String COLUMN_LONGITUDE = "COLUMN_LONGITUDE";
        public static final String COLUMN_ADDRESS= "COLUMN_ADDRESS";
        public static final String COLUMN_TIMESTAMP = "COLUMN_TIMESTAMP";
        public static final String COLUMN_SYNCED = "COLUMN_SYNCED";
    }

    public static final class GeoPoint {
        public static final String TABLE = "GeoPoint";
        public static final Uri URI = Uri.withAppendedPath(URI_MAIN, TABLE);

        public static final String COLUMN_NAME = "COLUMN_NAME";
        public static final String COLUMN_LATITUDE = "COLUMN_LATITUDE";
        public static final String COLUMN_LONGITUDE = "COLUMN_LONGITUDE";
        public static final String COLUMN_RADIUS = "COLUMN_RADIUS";
    }

    public static final class CheckInData {
        public static final String TABLE = "CheckedIn";
        public static final Uri URI = Uri.withAppendedPath(URI_MAIN, TABLE);

        public static final String COLUMN_ASSIGN_DESTINATIONID = "COLUMN_ASSIGNDESTINATIONID";
        public static final String COLUMN_REPORTEDTIME = "COLUMN_REPORTEDTIME";
        public static final String COLUMN_CHECKEDINOUT_LATTITUDE= "COLUMN_CHECKEDINOUT_LATTITUDE";
        public static final String COLUMN_CHECKEDINOUT_LONGITUDE = "COLUMN_CHECKEDINOUT_LONGITUDE";
        public static final String COLUMN_ISCHECKEDIN = "COLUMN_ISCHECKEDIN";
    }

}
