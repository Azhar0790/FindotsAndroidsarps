package com.knowall.findots.restcalls.history;

import com.knowall.findots.utils.timeUtils.TimeSettings;

import org.parceler.Parcel;

/**
 * Created by parijathar on 7/27/2016.
 */
@Parcel
public class HistoryData {
    private String checkInDate;

    private String checkOutDate;

    private double checkedInLatitude;

    private double destinationLongitude;

    private String destinationName;

    private boolean isCheckIn;

    private double checkedOutLatitude;

    private double destinationLatitude;

    private int destinationID;

    private double checkedOutLongitude;

    private String address;

    private double checkedInLongitude;

    private boolean isCheckOut;

    public String getCheckInDate() {
        return TimeSettings.dateTimeInUTCToLocal(checkInDate);
    }

    public void setCheckInDate(String checkInDate) {
        this.checkInDate = checkInDate;
    }

    public String getCheckOutDate() {
        return TimeSettings.dateTimeInUTCToLocal(checkOutDate);
    }

    public void setCheckOutDate(String checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public double getCheckedInLatitude() {
        return checkedInLatitude;
    }

    public void setCheckedInLatitude(double checkedInLatitude) {
        this.checkedInLatitude = checkedInLatitude;
    }

    public double getDestinationLongitude() {
        return destinationLongitude;
    }

    public void setDestinationLongitude(double destinationLongitude) {
        this.destinationLongitude = destinationLongitude;
    }

    public String getDestinationName() {
        return destinationName;
    }

    public void setDestinationName(String destinationName) {
        this.destinationName = destinationName;
    }

    public boolean isCheckIn() {
        return isCheckIn;
    }

    public void setIsCheckIn(boolean isCheckIn) {
        this.isCheckIn = isCheckIn;
    }

    public double getCheckedOutLatitude() {
        return checkedOutLatitude;
    }

    public void setCheckedOutLatitude(double checkedOutLatitude) {
        this.checkedOutLatitude = checkedOutLatitude;
    }

    public double getDestinationLatitude() {
        return destinationLatitude;
    }

    public void setDestinationLatitude(double destinationLatitude) {
        this.destinationLatitude = destinationLatitude;
    }

    public int getDestinationID() {
        return destinationID;
    }

    public void setDestinationID(int destinationID) {
        this.destinationID = destinationID;
    }

    public double getCheckedOutLongitude() {
        return checkedOutLongitude;
    }

    public void setCheckedOutLongitude(double checkedOutLongitude) {
        this.checkedOutLongitude = checkedOutLongitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getCheckedInLongitude() {
        return checkedInLongitude;
    }

    public void setCheckedInLongitude(double checkedInLongitude) {
        this.checkedInLongitude = checkedInLongitude;
    }

    public boolean isCheckOut() {
        return isCheckOut;
    }

    public void setIsCheckOut(boolean isCheckOut) {
        this.isCheckOut = isCheckOut;
    }

    @Override
    public String toString() {
        return "ClassPojo [checkInDate = " + checkInDate + ", checkOutDate = " + checkOutDate + ", checkedInLatitude = " + checkedInLatitude + ", destinationLongitude = " + destinationLongitude + ", destinationName = " + destinationName + ", isCheckIn = " + isCheckIn + ", checkedOutLatitude = " + checkedOutLatitude + ", destinationLatitude = " + destinationLatitude + ", destinationID = " + destinationID + ", checkedOutLongitude = " + checkedOutLongitude + ", address = " + address + ", checkedInLongitude = " + checkedInLongitude + ", isCheckOut = " + isCheckOut + "]";
    }
}
