package restcalls.register;

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
public class RegisterRestCall {

    public IRegisterRestCall delegate = null;
    Context context = null;

    public RegisterRestCall(Context context) {
        this.context = context;
    }

    public void callRegisterUserService(String email, String password, String mobile,
                                        String name, String redeemCode, String company) {
        GeneralUtils.initialize_progressbar(context);

        Map<String, Object> postValues = getRegisterUserRequest(email, password, mobile,
                name, redeemCode, company);

        Call<RegisterModel> call = FinDotsApplication.getRestClient().getApiService().registerUser(postValues);
        call.enqueue(new Callback<RegisterModel>() {
            @Override
            public void onResponse(Response<RegisterModel> response, Retrofit retrofit) {
                GeneralUtils.stop_progressbar();
                if (response.isSuccess()) {
                    RegisterModel registerModel = response.body();
                    delegate.onRegisterUserSuccess(registerModel);
                } else {
                    RegisterModel registerModel = response.body();
                    delegate.onRegisterUserFailure(registerModel.getMessage());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                GeneralUtils.stop_progressbar();
                delegate.onRegisterUserFailure(context.getString(R.string.data_error));
            }
        });

    }

    private Map<String, Object> getRegisterUserRequest(String email, String password, String mobile,
                                                      String name, String redeemCode, String company) {
        Map<String, Object> postValues = new HashMap<>();
        postValues.put("email", email);
        postValues.put("password", password);
        postValues.put("mobileNumber", mobile);
        postValues.put("name", name);
        postValues.put("redeemCode", redeemCode);
        postValues.put("company", company);
        postValues.put("deviceID", GeneralUtils.getUniqueDeviceId(context));
        postValues.put("appVersion", GeneralUtils.getAppVersion(context));
        postValues.put("deviceTypeID", Constants.DEVICETYPEID);
        postValues.put("deviceInfo", GeneralUtils.getDeviceInfo());
        postValues.put("userID", 0);
        postValues.put("latitude", 12.34567);
        postValues.put("longitude", 27.567890);
        postValues.put("userTypeID", 2);
        postValues.put("organizationID", 1);
        postValues.put("countryID", 103);
        postValues.put("city", "");
        postValues.put("pincode", "");
        postValues.put("address", "");
        postValues.put("contactPerson", "");
        postValues.put("fbLoginID", "");
        postValues.put("googleID", "");
        postValues.put("corporateAdminUserID", 0);
        postValues.put("base64String", "");

        return postValues;
    }
}
