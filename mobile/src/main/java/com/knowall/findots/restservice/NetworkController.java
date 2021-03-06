package com.knowall.findots.restservice;

import com.knowall.findots.distancematrix.model.DistanceMatrix;
import com.knowall.findots.locationUtils.LocationModel.BackgroundLocData;
import com.knowall.findots.restcalls.accountSettings.GetAccountInfoModel;
import com.knowall.findots.restcalls.checkInCheckOut.CheckInCheckOutModel;
import com.knowall.findots.restcalls.destinations.DestinationsModel;
import com.knowall.findots.restcalls.forgotPassword.ForgotPasswordModel;
import com.knowall.findots.restcalls.getUser.GetUserModel;
import com.knowall.findots.restcalls.history.HistoryModel;
import com.knowall.findots.restcalls.joinTeam.JoinTeamModel;
import com.knowall.findots.restcalls.login.LoginModel;
import com.knowall.findots.restcalls.register.RegisterModel;
import com.knowall.findots.restmodels.ResponseModel;

import java.util.Map;

import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;
import retrofit.http.Url;

/**
 * Created by parijathar on 6/6/2016.
 */


public interface NetworkController {

    @POST(RestURLs.METHOD_LOGIN)
    Call<LoginModel> login(@Body Map<String, Object> request);

    @POST(RestURLs.METHOD_REGISTER_USER)
    Call<RegisterModel> registerUser(@Body Map<String, Object> request);

    @POST(RestURLs.METHOD_FORGOT_PASSWORD)
    Call<ForgotPasswordModel> forgotPassword(@Body Map<String, Object> request);

    @POST(RestURLs.METHOD_GET_ASSIGNED_DESTINATIONS)
    Call<DestinationsModel> getDestinations(@Body Map<String, Object> request);

    @POST(RestURLs.METHOD_MODIFY_DESTINATION)
    Call<ResponseModel> modifyDestination(@Body Map<String, Object> request);

    @POST(RestURLs.METHOD_ADD_DESTINATION)
    Call<ResponseModel> addDestination(@Body Map<String, Object> request);

    @POST(RestURLs.METHOD_DELETE_ASSIGNED_DESTINATION)
    Call<ResponseModel> deleteAssignedDestination(@Body Map<String, Object> request);

    @POST(RestURLs.METHOD_RENAME_ASSIGNED_DESTINATION)
    Call<ResponseModel> renameAssignedDestination(@Body Map<String, Object> request);

    @POST(RestURLs.METHOD_SCHEDULE_DESTINATION_VISIT)
    Call<ResponseModel> scheduleDestinationVisit(@Body Map<String, Object> request);

    @POST(RestURLs.METHOD_RESCHEDULE_DESTINATION_VISIT)
    Call<ResponseModel> reScheduleDestinationVisit(@Body Map<String, Object> request);

    @POST(RestURLs.METHOD_GETHISTORY)
    Call<HistoryModel> getHistory(@Body Map<String, Object> request);

    @POST(RestURLs.METHOD_LCATION_TRACKING)
    Call<ResponseModel> saveLocationPath(@Body BackgroundLocData bgData);

    @POST(RestURLs.METHOD_LOGOUT)
    Call<ResponseModel> logOut(@Body Map<String, Object> request);

    @POST(RestURLs.METHOD_CHECK_IN)
    Call<CheckInCheckOutModel> checkIn(@Body Map<String, Object> request);

    @POST(RestURLs.METHOD_CHECK_OUT)
    Call<CheckInCheckOutModel> checkOut(@Body Map<String, Object> request);

    @POST(RestURLs.METHOD_ACCOUNTINFO)
    Call<GetAccountInfoModel> getAccountInfo(@Body Map<String, Object> request);

    @POST(RestURLs.METHOD_SAVEACCOUNTINFO)
    Call<ResponseModel> saveAccountInfo(@Body Map<String, Object> request);

    @POST(RestURLs.METHOD_CHANGEPASSWORD)
    Call<ResponseModel> changePassword(@Body Map<String, Object> request);

    @POST(RestURLs.METHOD_JOINATEAM)
    Call<JoinTeamModel> joinTeam(@Body Map<String, Object> request);

    @POST(RestURLs.METHOD_GETUSERS)
    Call<GetUserModel> getUsers(@Body Map<String, Object> request);

    @POST(RestURLs.METHOD_REGISTERNOTICATION_TOKEN)
    Call<ResponseModel> setNotification_RefreshedToken(@Body Map<String, Object> request);

    @GET
    Call<DistanceMatrix> distanceMatrix(@Url String url, @Query("units") String units,
                                        @Query("key") String key,
                                        @Query("origins") String origins,
                                        @Query("destinations") String destinations);
}
