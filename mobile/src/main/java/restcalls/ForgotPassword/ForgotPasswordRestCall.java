package restcalls.ForgotPassword;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

import findots.bridgetree.com.findots.FinDotsApplication;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import utils.GeneralUtils;

/**
 * Created by parijathar on 6/17/2016.
 */
public class ForgotPasswordRestCall {

    public IForgotPasswordRestCall delegate = null;
    Context context = null;

    public ForgotPasswordRestCall(Context context) {
        this.context = context;
    }

    public void callForgotPasswordService(String email, String appVersion,
                                          String deviceTypeID, String deviceInfo,
                                          String userID, String ipAddress) {
        GeneralUtils.initialize_progressbar(context);

        Map<String, Object> postValues = getForgotPasswordRequest(
                email, appVersion, deviceTypeID,
                deviceInfo, userID, ipAddress);

        Call<ForgotPasswordModel> call = FinDotsApplication.getRestClient().getApiService().forgotPassword(postValues);
        call.enqueue(new Callback<ForgotPasswordModel>() {
            @Override
            public void onResponse(Response<ForgotPasswordModel> response, Retrofit retrofit) {
                GeneralUtils.stop_progressbar();
                if (response.isSuccess()) {
                    ForgotPasswordModel forgotPasswordModel = response.body();
                    delegate.onForgotPasswordSuccess(forgotPasswordModel);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                GeneralUtils.stop_progressbar();
                delegate.onForgotPasswordFailure();
            }
        });
    }

    private Map<String, Object> getForgotPasswordRequest(String email, String appVersion,
                                                        String deviceTypeID, String deviceInfo,
                                                        String userID, String ipAddress) {
        Map<String, Object> postValues = new HashMap<>();
        postValues.put("email", email);
        postValues.put("appVersion", appVersion);
        postValues.put("deviceTypeID", deviceTypeID);
        postValues.put("deviceInfo", deviceInfo);
        postValues.put("userID", userID);
        postValues.put("ipAddress", ipAddress);
        return postValues;
    }
}
