package restcalls.ForgotPassword;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

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
public class ForgotPasswordRestCall {

    public IForgotPasswordRestCall delegate = null;
    Context context = null;

    public ForgotPasswordRestCall(Context context) {
        this.context = context;
    }

    public void callForgotPasswordService(String email, String appVersion,
                                          String deviceTypeID, String deviceInfo,
                                          String userID) {
        GeneralUtils.initialize_progressbar(context);

        Map<String, Object> postValues = getForgotPasswordRequest(
                email, appVersion, deviceTypeID,
                deviceInfo, userID);

        Call<ForgotPasswordModel> call = FinDotsApplication.getRestClient().getApiService().forgotPassword(postValues);
        call.enqueue(new Callback<ForgotPasswordModel>() {
            @Override
            public void onResponse(Response<ForgotPasswordModel> response, Retrofit retrofit) {
                GeneralUtils.stop_progressbar();
                if (response.isSuccess()) {
                    ForgotPasswordModel forgotPasswordModel = response.body();
                    delegate.onForgotPasswordSuccess(forgotPasswordModel);
                } else {
                    ForgotPasswordModel forgotPasswordModel = response.body();
                    delegate.onForgotPasswordFailure(forgotPasswordModel.getMessage());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                GeneralUtils.stop_progressbar();
                delegate.onForgotPasswordFailure(context.getString(R.string.data_error));
            }
        });
    }

    private Map<String, Object> getForgotPasswordRequest(String email, String appVersion,
                                                        String deviceTypeID, String deviceInfo,
                                                        String userID) {
        Map<String, Object> postValues = new HashMap<>();
        postValues.put("email", email);
        postValues.put("appVersion", "1.0");
        postValues.put("deviceTypeID", 2);
        postValues.put("deviceInfo", "nexus");
        postValues.put("userID", 14);
        return postValues;
    }
}
