package restservice;

import java.util.Map;

import restcalls.ForgotPassword.ForgotPasswordModel;
import restcalls.Login.LoginModel;
import restcalls.Register.RegisterModel;
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

}
