package com.knowall.findots.restcalls.destinations;

import com.knowall.findots.utils.GeneralUtils;
import com.knowall.findots.utils.timeUtils.TimeSettings;

import org.parceler.Parcel;

/**
 * Created by parijathar on 6/20/2016.
 */
@Parcel
public class DestinationData {

    private int assignDestinationID;

    private int destinationID;

    private String checkedOutReportedDate;

    private String address;

    private String assigndestinationTime;

    private String name;

    private String checkedInReportedDate;

    private double destinationLongitude;

    private String destinationName;

    private double destinationLatitude;

    private int checkInRadius;

    private boolean checkedIn;

    private boolean checkedOut;

    private boolean isEditable;

    private boolean isRequiresApproval;

    private int assignedUserID;

    private String scheduleDate;

    private boolean scheduleDisplayStatus = false;

    private String scheduledStatus = null;

    private long timeDifference;

    private String travelTime;

    private String travelDistance;

    public String getTravelTime() {
        return travelTime;
    }

    public void setTravelTime(String travelTime) {
        this.travelTime = travelTime;
    }

    public String getTravelDistance() {
        return travelDistance;
    }

    public void setTravelDistance(String travelDistance) {
        this.travelDistance = travelDistance;
    }

    public long getTimeDifference() {
        return timeDifference;
    }

    public void setTimeDifference(long timeDifference) {
        this.timeDifference = timeDifference;
    }

    public String getScheduledStatus() {
        return scheduledStatus;
    }

    public boolean isScheduleDisplayStatus() {
        return scheduleDisplayStatus;
    }

    public void setScheduleDisplayStatus(boolean scheduleDisplayStatus) {
        this.scheduleDisplayStatus = scheduleDisplayStatus;
    }

    public void setScheduledStatus(String scheduledStatus) {
        this.scheduledStatus = scheduledStatus;
    }

    public String getScheduleDate() {
        return TimeSettings.dateTimeInUTCToLocal(scheduleDate);
    }

    public void setScheduleDate(String scheduleDate) {
        this.scheduleDate = scheduleDate;
    }

    public int getAssignedUserID() {
        return assignedUserID;
    }

    public void setAssignedUserID(int assignedUserID) {
        this.assignedUserID = assignedUserID;
    }

    public boolean isCheckedIn() {
        return checkedIn;
    }

    public void setCheckedIn(boolean checkedIn) {
        this.checkedIn = checkedIn;
    }

    public boolean isCheckedOut() {
        return checkedOut;
    }

    public void setCheckedOut(boolean checkedOut) {
        this.checkedOut = checkedOut;
    }

    public int getCheckInRadius() {
        return checkInRadius;
    }

    public void setCheckInRadius(int checkInRadius) {
        this.checkInRadius = checkInRadius;
    }

    public int getAssignDestinationID() {
        return assignDestinationID;
    }

    public void setAssignDestinationID(int assignDestinationID) {
        this.assignDestinationID = assignDestinationID;
    }

    public int getDestinationID() {
        return destinationID;
    }

    public void setDestinationID(int destinationID) {
        this.destinationID = destinationID;
    }

    public String getCheckedOutReportedDate() {
        return TimeSettings.dateTimeInUTCToLocal(checkedOutReportedDate);
    }

    public void setCheckedOutReportedDate(String checkedOutReportedDate) {
        this.checkedOutReportedDate = checkedOutReportedDate;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAssigndestinationTime() {
        return TimeSettings.dateTimeInUTCToLocal(assigndestinationTime);
    }

    public void setAssigndestinationTime(String assigndestinationTime) {
        this.assigndestinationTime = assigndestinationTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCheckedInReportedDate() {
        return checkedInReportedDate;
    }

    public void setCheckedInReportedDate(String checkedInReportedDate) {
        this.checkedInReportedDate = checkedInReportedDate;
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

    public double getDestinationLatitude() {
        return destinationLatitude;
    }

    public void setDestinationLatitude(double destinationLatitude) {
        this.destinationLatitude = destinationLatitude;
    }

    public boolean isEditable() {
        return isEditable;
    }

    public void setEditable(boolean editable) {
        isEditable = editable;
    }

    public boolean isRequiresApproval() {
        return isRequiresApproval;
    }

    public void setRequiresApproval(boolean requiresApproval) {
        isRequiresApproval = requiresApproval;
    }

    @Override
    public String toString() {
        return "ClassPojo [destinationID = " + destinationID + ", checkedOutReportedDate = " + checkedOutReportedDate + ", address = " + address + ", assigndestinationTime = " + assigndestinationTime + ", name = " + name + ", checkedIn = " + ", checkedInReportedDate = " + checkedInReportedDate + ", destinationLongitude = " + destinationLongitude + ", destinationName = " + destinationName + ", destinationLatitude = " + destinationLatitude + ", checkedOut = " + "]";
    }
}
