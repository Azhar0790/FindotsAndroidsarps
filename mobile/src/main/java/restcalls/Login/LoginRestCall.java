package restcalls.login;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

import findots.bridgetree.com.findots.Constants;
import findots.bridgetree.com.findots.FinDotsApplication;
import findots.bridgetree.com.findots.R;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import utils.GeneralUtils;

/**
 * Created by parijathar on 6/17/2016.
 */
public class LoginRestCall {

    public ILoginRestCall delegate = null;
    Context context = null;

    public LoginRestCall(Context context) {
        this.context = context;
    }


    public void callLoginService(String username, String password, String deviceID, String appVersion,
                                 String deviceTypeID, String deviceInfo,
                                 String userID, String ipAddress) {

        GeneralUtils.initialize_progressbar(context);

        Map<String, Object> postValues = getLoginRequest(username, password, deviceID, appVersion,
                deviceTypeID, deviceInfo, userID, ipAddress);

        Call<LoginModel> call = FinDotsApplication.getRestClient().getApiService().login(postValues);
        call.enqueue(new Callback<LoginModel>() {
            @Override
            public void onResponse(Response<LoginModel> response, Retrofit retrofit) {
                GeneralUtils.stop_progressbar();
                if (response.isSuccess()) {
                    LoginModel loginModel = response.body();
                    delegate.onLoginSuccess(loginModel);
                } else {
                    LoginModel loginModel = response.body();
                    delegate.onLoginFailure(loginModel.getMessage());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                GeneralUtils.stop_progressbar();
                delegate.onLoginFailure(context.getString(R.string.data_error));
            }
        });

    }


    private Map<String, Object> getLoginRequest(String username, String password,
                                               String deviceID, String appVersion,
                                               String deviceTypeID, String deviceInfo,
                                               String userID, String ipAddress) {
        Map<String, Object> postValues = new HashMap<>();
        postValues.put("email", username);
        postValues.put("password", password);
        postValues.put("deviceID", GeneralUtils.getUniqueDeviceId(context));
        postValues.put("appVersion", GeneralUtils.getAppVersion(context));
        postValues.put("deviceTypeID", Constants.DEVICETYPEID);
        postValues.put("deviceInfo", GeneralUtils.getDeviceInfo());
        postValues.put("userID", 0);

        return postValues;
    }

}
