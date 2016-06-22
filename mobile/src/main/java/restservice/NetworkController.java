package restservice;

import java.util.Map;

import locationUtils.LocationModel.BackgroundLocData;
import locationUtils.LocationModel.LocationResponseData;
import restcalls.Destinations.DestinationsModel;
import restcalls.ForgotPassword.ForgotPasswordModel;
import restcalls.Login.LoginModel;
import restcalls.Register.RegisterModel;
import restmodels.ResponseModel;
import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.POST;

/**
 * Created by parijathar on 6/6/2016.
 */


public interface NetworkController {

    @POST(RestURLs.LOGIN)
    Call<LoginModel> login(@Body Map<String, Object> request);

    @POST(RestURLs.REGISTER_USER)
    Call<RegisterModel> registerUser(@Body Map<String, Object> request);

    @POST(RestURLs.FORGOT_PASSWORD)
    Call<ForgotPasswordModel> forgotPassword(@Body Map<String, Object> request);

    @POST(RestURLs.GET_DESTINATIONS)
    Call<DestinationsModel> getDestinations(@Body Map<String, Object> request);

    @POST(RestURLs.METHOD_LCATION_TRACKING)
    Call<LocationResponseData> getLogin(@Body BackgroundLocData bgData);

    @POST(RestURLs.LOGOUT)
    Call<ResponseModel> logOut(@Body Map<String, Object> request);

}
