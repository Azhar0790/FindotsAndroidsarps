package com.knowall.findots.restservice;

/**
 * Created by parijathar on 6/6/2016.
 */
public class RestURLs {

    /**
     *   development url
     */
    public static final String BASE_URL = "http://182.73.82.185/Findots/API/";
    /**
     *   production url
     */
//    public static final String BASE_URL = "https://service.findots.com/API/";

    // ------------------------   METHODS   ----------------------------------

    public static final String METHOD_LOGIN = "Login";
    public static final String METHOD_REGISTER_USER = "RegisterUser";
    public static final String METHOD_FORGOT_PASSWORD = "ForgotPassword";
    public static final String METHOD_GET_ASSIGNED_DESTINATIONS = "GetAssignedDestinationsDetails";
    public static final String METHOD_MODIFY_DESTINATION  = "ModifyDestinationLocation";
    public static final String METHOD_ADD_DESTINATION  = "AddDestinationInApp";
    public static final String METHOD_CHECK_IN = "CheckedIn";
    public static final String METHOD_CHECK_OUT = "CheckedOut";
    public static final String METHOD_LCATION_TRACKING  = "InsertLocations";
    public static final String METHOD_LOGOUT  = "Logout";
    public static final String METHOD_ACCOUNTINFO  = "AccountSettings";
    public static final String METHOD_SAVEACCOUNTINFO  = "SaveSettings";
    public static final String METHOD_CHANGEPASSWORD  = "ChangePassword";
    public static final String METHOD_DELETE_ASSIGNED_DESTINATION  = "DeleteAssignedDestination";
    public static final String METHOD_RENAME_ASSIGNED_DESTINATION  = "RenameDestinationName";
    public static final String METHOD_SCHEDULE_DESTINATION_VISIT  = "UpdateScheduleForDestination";
    public static final String METHOD_RESCHEDULE_DESTINATION_VISIT = "ReScheduleForDestination";
    public static final String METHOD_GETREPORT = "GetReport";
    public static final String METHOD_JOINATEAM = "JoinTeam";
    public static final String METHOD_GETUSERS = "GetUsers";

}
