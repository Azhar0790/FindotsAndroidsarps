package com.knowall.findots.restservice;

import com.knowall.findots.locationUtils.LocationModel.BackgroundLocData;
import com.knowall.findots.restcalls.accountSettings.GetAccountInfoModel;
import com.knowall.findots.restcalls.checkInCheckOut.CheckInCheckOutModel;
import com.knowall.findots.restcalls.destinations.DestinationsModel;
import com.knowall.findots.restcalls.forgotPassword.ForgotPasswordModel;
import com.knowall.findots.restcalls.login.LoginModel;
import com.knowall.findots.restcalls.register.RegisterModel;
import com.knowall.findots.restmodels.ResponseModel;

import java.util.Map;

import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.POST;

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


}
