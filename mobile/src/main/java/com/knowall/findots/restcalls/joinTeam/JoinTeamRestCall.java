package com.knowall.findots.restcalls.joinTeam;

import android.content.Context;

import com.knowall.findots.Constants;
import com.knowall.findots.FinDotsApplication;
import com.knowall.findots.R;
import com.knowall.findots.restcalls.login.LoginModel;
import com.knowall.findots.utils.AppStringConstants;
import com.knowall.findots.utils.GeneralUtils;

import java.util.HashMap;
import java.util.Map;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by parijathar on 8/2/2016.
 */
public class JoinTeamRestCall {

    public IJoinTeam delegate = null;
    Context context = null;

    public JoinTeamRestCall(Context context) {
        this.context = context;
    }

    public void callJoinTeam(String redeemCode) {

        GeneralUtils.initialize_progressbar(context);

        Map<String, Object> postValues = getJoinTeamRequest(redeemCode);

        Call<JoinTeamModel> call = FinDotsApplication.getRestClient().getApiService().joinTeam(postValues);

        call.enqueue(new Callback<JoinTeamModel>() {
            @Override
            public void onResponse(Response<JoinTeamModel> response, Retrofit retrofit) {
                GeneralUtils.stop_progressbar();

                if(response.body()!=null) {
                    if (response.isSuccess()) {
                        JoinTeamModel joinTeamModel = response.body();
                        delegate.joinTeamFinish(joinTeamModel.getMessage());
                    } else {
                        JoinTeamModel joinTeamModel = response.body();
                        delegate.joinTeamFinish(joinTeamModel.getMessage());
                    }
                } else {
                    delegate.joinTeamFinish(context.getString(R.string.data_error));
                }

            }

            @Override
            public void onFailure(Throwable t) {
                GeneralUtils.stop_progressbar();
                delegate.joinTeamFinish(context.getString(R.string.data_error));
            }
        });

    }

    private Map<String, Object> getJoinTeamRequest(String redeemCode) {

        int userID = GeneralUtils.getSharedPreferenceInt(context, AppStringConstants.USERID);

        Map<String, Object> postValues = new HashMap<>();
        postValues.put("redeemCode", redeemCode);
        postValues.put("deviceID", GeneralUtils.getUniqueDeviceId(context));
        postValues.put("appVersion", GeneralUtils.getAppVersion(context));
        postValues.put("deviceTypeID", Constants.DEVICETYPEID);
        postValues.put("deviceInfo", GeneralUtils.getDeviceInfo());
        postValues.put("userID", userID);

        return postValues;
    }


}
