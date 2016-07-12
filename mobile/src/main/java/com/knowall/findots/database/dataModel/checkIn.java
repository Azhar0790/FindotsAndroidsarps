package com.knowall.findots.database.dataModel;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import com.knowall.findots.database.Data;

/**
 * Created by jpaulose on 6/29/2016.
 */
@Table(name = Data.CheckInData.TABLE)
public class checkIn extends Model
{
    @Column(name = Data.CheckInData.COLUMN_ASSIGN_DESTINATIONID)
    private int assigned_destinationId;

    @Column(name = Data.CheckInData.COLUMN_REPORTEDTIME)
    private String reportedTime;

    @Column(name = Data.CheckInData.COLUMN_CHECKEDINOUT_LATTITUDE)
    private double checkInOUTlatitude;

    @Column(name = Data.CheckInData.COLUMN_CHECKEDINOUT_LONGITUDE)
    private double checkInOUTlongitude;

    @Column(name = Data.CheckInData.COLUMN_ISCHECKEDIN)
    private boolean isCheckedIn;

    public static checkIn getInstance(int assigned_destinationId,String reportedTime,double latitude, double longitude,boolean isCheckedIn) {
        checkIn data = new checkIn();
        data.setAssigned_destinationId(assigned_destinationId);
        data.setReportedTime(reportedTime);
        data.setCheckInOUTlatitude(latitude);
        data.setCheckInOUTlongitude(longitude);
        data.setCheckedIn(isCheckedIn);

        return data;
    }


    public int getAssigned_destinationId() {
        return assigned_destinationId;
    }

    public void setAssigned_destinationId(int assigned_destinationId) {
        this.assigned_destinationId = assigned_destinationId;
    }

    public boolean isCheckedIn() {
        return isCheckedIn;
    }

    public void setCheckedIn(boolean checkedIn) {
        isCheckedIn = checkedIn;
    }

    public double getCheckInOUTlongitude() {
        return checkInOUTlongitude;
    }

    public void setCheckInOUTlongitude(double checkInOUTlongitude) {
        this.checkInOUTlongitude = checkInOUTlongitude;
    }

    public double getCheckInOUTlatitude() {
        return checkInOUTlatitude;
    }

    public void setCheckInOUTlatitude(double checkInOUTlatitude) {
        this.checkInOUTlatitude = checkInOUTlatitude;
    }

    public String getReportedTime() {
        return reportedTime;
    }

    public void setReportedTime(String reportedTime) {
        this.reportedTime = reportedTime;
    }
}
