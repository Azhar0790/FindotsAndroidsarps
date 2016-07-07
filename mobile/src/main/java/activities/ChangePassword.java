package activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import database.DataHelper;
import findots.bridgetree.com.findots.Constants;
import findots.bridgetree.com.findots.FinDotsApplication;
import findots.bridgetree.com.findots.R;
import locationUtils.TrackLocationService;
import restmodels.ResponseModel;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import utils.AppStringConstants;
import utils.GeneralUtils;

/**
 * Created by jpaulose on 6/23/2016.
 */
public class ChangePassword extends AppCompatActivity {

    @Bind(R.id.editText_current_password)
    EditText mEditText_current_password;

    @Bind(R.id.editText_new_password)
    EditText mEditText_new_password;

    @Bind(R.id.editText_conf_new_password)
    EditText mEditText_conf_new_password;

    @Bind(R.id.button_changePassword)
    Button mButton_changePassword;

    @Bind(R.id.TextView_heading)
    TextView mTextView_heading;

    Toolbar mToolbar = null;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.change_password_activity);

        ButterKnife.bind(this);

        setUIElementsProperty();
    }

    public void setUIElementsProperty() {

        mToolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Typeface typefaceLight = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf");
        Typeface typefaceMyriadHebrew = Typeface.createFromAsset(getAssets(), "fonts/MyriadHebrew-Bold.otf");

        mTextView_heading.setText(getString(R.string.changepwd));
        mTextView_heading.setTypeface(typefaceMyriadHebrew);
        mEditText_current_password.setTypeface(typefaceLight);
        mEditText_new_password.setTypeface(typefaceLight);
        mEditText_conf_new_password.setTypeface(typefaceLight);
        mButton_changePassword.setTypeface(typefaceLight);

    }

    @OnClick(R.id.button_changePassword)
    public void changePassword() {
        if (validateEnteredValues()) {

            GeneralUtils.initialize_progressbar(this);
            Call<ResponseModel> changePassWord = FinDotsApplication.getRestClient().getApiService().changePassword(changePasswordRequest());

            changePassWord.enqueue(new Callback<ResponseModel>() {


                                       @Override
                                       public void onResponse(Response<ResponseModel> response, Retrofit retrofit) {
                                           GeneralUtils.stop_progressbar();

                                           if (response.isSuccess() && response.body().getErrorCode() == 0) {

                                               Toast.makeText(ChangePassword.this, response.body().getMessage() , Toast.LENGTH_LONG).show();
                                               logOut();
                                           } else
                                               Toast.makeText(ChangePassword.this,response.body().getMessage() , Toast.LENGTH_SHORT).show();

                                       }

                                       @Override
                                       public void onFailure(Throwable t) {
                                           GeneralUtils.stop_progressbar();
                                           Toast.makeText(ChangePassword.this, getResources().getString(R.string.password_updateInfoError), Toast.LENGTH_SHORT).show();

                                       }
                                   }
            );

        }
    }

    private Map<String, Object> changePasswordRequest() {
        Map<String, Object> postValues = new HashMap<>();
        postValues.put("oldPassword", mEditText_current_password.getText().toString());
        postValues.put("newPassword", mEditText_new_password.getText().toString());
        postValues.put("appVersion", GeneralUtils.getAppVersion(this));
        postValues.put("deviceTypeID", Constants.DEVICETYPEID);
        postValues.put("deviceInfo", GeneralUtils.getDeviceInfo());
        postValues.put("userID", GeneralUtils.getSharedPreferenceInt(this, AppStringConstants.USERID));
        postValues.put("ipAddress", "");

        return postValues;
    }

    public boolean validateEnteredValues() {
        if (mEditText_current_password.getText().toString() == null || mEditText_current_password.getText().toString().trim().length() == 0) {
            mEditText_current_password.setError(getString(R.string.prompt_required));
            return false;

        } else if (mEditText_new_password.getText().toString() == null || mEditText_new_password.getText().toString().trim().length() < 5) {
            mEditText_new_password.setError(getString(R.string.prompt_password));
            return false;
        } else if (mEditText_conf_new_password.getText().toString() == null || mEditText_conf_new_password.getText().toString().trim().length() == 0) {
            mEditText_conf_new_password.setError(getString(R.string.prompt_required));
            return false;
        } else if (mEditText_current_password.getText().toString().equalsIgnoreCase(mEditText_new_password.getText().toString())) {
            mEditText_new_password.setError(getString(R.string.password_cannotBeSame));
            return false;
        } else if (!(mEditText_conf_new_password.getText().toString().equalsIgnoreCase(mEditText_new_password.getText().toString()))) {
            mEditText_conf_new_password.setError(getString(R.string.password_same));
            return false;
        }

        return true;

    }



    public void logOut() {
        GeneralUtils.initialize_progressbar(this);
        Map<String, Object> postValues = new HashMap<>();

        postValues.put("deviceID", GeneralUtils.getUniqueDeviceId(this));
        postValues.put("appVersion", GeneralUtils.getAppVersion(this));
        postValues.put("deviceTypeID", Constants.DEVICETYPEID);
        postValues.put("deviceInfo", GeneralUtils.getDeviceInfo());
        postValues.put("ipAddress", "");
        postValues.put("userID", GeneralUtils.getSharedPreferenceInt(this, AppStringConstants.USERID));

        Call<ResponseModel> call = FinDotsApplication.getRestClient().getApiService().logOut(postValues);
        call.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Response<ResponseModel> response, Retrofit retrofit) {

                logOutNavigation();
//                if (response.isSuccess() & response.body().getData().size() > 0) {
//                    Toast.makeText(ChangePassword.this, response.body().getData().get(0).getStatus(), Toast.LENGTH_SHORT).show();

//                } else {
//                    Toast.makeText(ChangePassword.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
//                }
            }

            @Override
            public void onFailure(Throwable t) {
                logOutNavigation();

            }
        });

    }
    public void logOutNavigation() {
        GeneralUtils.stop_progressbar();
        stopService(new Intent(this, TrackLocationService.class));
        DataHelper dataHelper = DataHelper.getInstance(ChangePassword.this);
        dataHelper.deleteAllLocations();
        dataHelper.deleteCheckinList();
        GeneralUtils.removeSharedPreference(ChangePassword.this, AppStringConstants.USERID);
        Intent intentLogout = new Intent(ChangePassword.this, LoginActivity.class);
        startActivity(intentLogout);
        finish();
    }
}