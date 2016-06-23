package locationUtils.LocationModel;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import locationUtils.Data;


@Table(name = Data.LocationData.TABLE)
public class LocationData extends Model {
    @Column(name = Data.LocationData.COLUMN_LATITUDE)
    private double latitude;
    @Column(name = Data.LocationData.COLUMN_LONGITUDE)
    private double longitude;


    @Column(name = Data.LocationData.COLUMN_TIMESTAMP)
    private String timestamp;
    @Column(name = Data.LocationData.COLUMN_SYNCED)
    private boolean synced;

    public static LocationData getInstance(double latitude, double longitude,String timeGmt) {
        LocationData data = new LocationData();
        data.setLatitude(latitude);
        data.setLongitude(longitude);
        data.setTimestamp(timeGmt);
        return data;
    }

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

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isSynced() {
        return synced;
    }

    public void setSynced(boolean synced) {
        this.synced = synced;
    }


    @Override
    public String toString() {
        return "latitude is :" + latitude
                + ", longitude is : " + longitude
                + ", time is :" + timestamp
                + ", synced is :" + synced;
    }
}
