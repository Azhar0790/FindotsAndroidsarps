package com.knowall.findots.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.knowall.findots.FinDotsApplication;
import com.knowall.findots.R;
import com.knowall.findots.locationUtils.Utils;
import com.knowall.findots.restcalls.register.IRegisterRestCall;
import com.knowall.findots.restcalls.register.RegisterModel;
import com.knowall.findots.restcalls.register.RegisterRestCall;
import com.knowall.findots.utils.AddTextWatcher;
import com.knowall.findots.utils.AppStringConstants;
import com.knowall.findots.utils.GeneralUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by parijathar on 6/13/2016.
 */
public class RegisterActivity extends AppCompatActivity
        implements
        View.OnClickListener,
        IRegisterRestCall,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks,
        LocationListener {


    @Bind(R.id.EditText_name)
    EditText mEditText_name;

    @Bind(R.id.EditText_company)
    EditText mEditText_company;

    @Bind(R.id.EditText_emailID)
    EditText mEditText_emailID;

    @Bind(R.id.EditText_mobileNo)
    EditText mEditText_mobileNo;

    @Bind(R.id.EditText_password)
    EditText mEditText_password;

    @Bind(R.id.ImageView_onOff)
    ImageView mImageView_onOff;

    @Bind(R.id.TextView_agree)
    TextView mTextView_agree;

    @Bind(R.id.Button_createAccount)
    Button mButton_createAccount;

    String name = null, company = null, emailID = null;
    String mobileNo = null, password = null;

    private GoogleApiClient mGoogleApiClient = null;
    Location currentLocation = null;

    // Location updates intervals in sec
    private static long INTERVAL = 1000 * 10; // 10 sec
    private static int FATEST_INTERVAL = 1000 * 5; // 5 sec

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        ButterKnife.bind(this);
        setUIElementsProperty();
        setListeners();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        if (!GeneralUtils.checkPlayServices(RegisterActivity.this)) {
            finish();
        }

        buildGoogleApiClient();
    }

    /**
     * sets all UI elements properties
     */
    public void setUIElementsProperty() {
        Typeface typefaceMyriadHebrew = Typeface.createFromAsset(getAssets(),
                "fonts/MyriadHebrew-Bold.otf");
        Typeface typefaceLight = Typeface.createFromAsset(getAssets(),
                "fonts/Roboto-Light.ttf");

        mEditText_name.setTypeface(typefaceLight);
        mEditText_company.setTypeface(typefaceLight);
        mEditText_emailID.setTypeface(typefaceLight);
        mEditText_mobileNo.setTypeface(typefaceLight);
        mEditText_password.setTypeface(typefaceLight);
        mTextView_agree.setTypeface(typefaceLight);
        mButton_createAccount.setTypeface(typefaceLight);

        mImageView_onOff.setTag(false);
        changeSwitchButtonBackground();

        //mTextView_agree.setText(getSpannableString());
        //mTextView_agree.setMovementMethod(LinkMovementMethod.getInstance());

    }

    /**
     * change the background of switch(ImageView) button
     */
    public void changeSwitchButtonBackground() {
        if (mImageView_onOff.getTag() == true) {
            mImageView_onOff.setBackgroundResource(R.drawable.ic_switch_on);
        } else if (mImageView_onOff.getTag() == false) {
            mImageView_onOff.setBackgroundResource(R.drawable.ic_switch_off);
        }
    }

    @OnClick(R.id.Button_createAccount)
    public void validateAndCreateAccount() {

        /**
         *   let the user to register if all the permissions granted
         */
        if (validateEnteredValues()) {
            /**
             *   check whether terms and conditions accepted
             */
            if ((Utils.isLocationServiceEnabled(RegisterActivity.this)) && currentLocation!=null) {


                /**
                 *   Call WebService to create an account
                 */
                RegisterRestCall registerRestCall = new RegisterRestCall(RegisterActivity.this);
                registerRestCall.delegate = RegisterActivity.this;
                registerRestCall.callRegisterUserService(emailID, password, mobileNo,
                        name, company, currentLocation.getLatitude(), currentLocation.getLongitude());

//            } else if (mImageView_onOff.getTag() == false) {
//                GeneralUtils.createAlertDialog(RegisterActivity.this, getString(R.string.please_agree));
//            }
            }
            else
            {
                buildGoogleApiClient();

            }
        }
    }

    @OnClick(R.id.TextView_termsCondition)
    public void openTermsandCondition() {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse("https://findots.com/terms-privacy.html"));
        startActivity(i);
    }

    @Override
    public void onRegisterUserFailure(String errorMessage) {
        Toast.makeText(RegisterActivity.this, errorMessage, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRegisterUserSuccess(RegisterModel registerModel) {
        if (registerModel.getErrorCode() == 0) {
            GeneralUtils.setSharedPreferenceString(this, AppStringConstants.USERNAME, emailID);
            GeneralUtils.setSharedPreferenceString(this, AppStringConstants.NAME, name);

//            Toast.makeText(RegisterActivity.this, registerModel.getMessage(), Toast.LENGTH_SHORT).show();
            if (registerModel.getRegisterData().length > 0 && registerModel.getRegisterData()[0].getUserID() > 0) {
                GeneralUtils.setSharedPreferenceInt(this, AppStringConstants.USERID, registerModel.getRegisterData()[0].getUserID());
                startMenuActivity();
            }

        } else {
            Toast.makeText(RegisterActivity.this, registerModel.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * validating the entered values for creating account
     */
    public boolean validateEnteredValues() {
        name = mEditText_name.getText().toString();
        company = mEditText_company.getText().toString();
        emailID = mEditText_emailID.getText().toString();
        mobileNo = mEditText_mobileNo.getText().toString();
        password = mEditText_password.getText().toString();

        if (name == null || name.length() == 0) {
            mEditText_name.setError(getString(R.string.prompt_required));
            return false;
//        }
//        else if (company == null || company.length() == 0) {
//            mEditText_company.setError(getString(R.string.prompt_required));
//            return false;
        } else if (emailID == null || emailID.length() == 0) {
            mEditText_emailID.setError(getString(R.string.prompt_required));
            return false;
        } else if (mobileNo == null || mobileNo.length() == 0) {
            mEditText_mobileNo.setError(getString(R.string.prompt_required));
            return false;
        } else if (password == null || password.length() == 0) {
            mEditText_password.setError(getString(R.string.prompt_password));
            return false;
        } else if (!GeneralUtils.isvalid_email(emailID)) {
            mEditText_emailID.setError(getString(R.string.validate_email));
            return false;
        }

        return true;
    }

    /**
     * set Listeners to the UI Widgets
     */
    public void setListeners() {
        mEditText_name.addTextChangedListener(new AddTextWatcher(mEditText_name));
        mEditText_company.addTextChangedListener(new AddTextWatcher(mEditText_company));
        mEditText_emailID.addTextChangedListener(new AddTextWatcher(mEditText_emailID));
        mEditText_mobileNo.addTextChangedListener(new AddTextWatcher(mEditText_mobileNo));
        mEditText_password.addTextChangedListener(new AddTextWatcher(mEditText_password));
        mImageView_onOff.setOnClickListener(this);
    }


    public SpannableString getSpannableString() {
        String str = getString(R.string.agree);
        SpannableString spannableString = new SpannableString(str);
        spannableString.setSpan(new RelativeSizeSpan(1.1f), 0, str.length(), 0);
        spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.black_70)), 0, 12, 0);
        spannableString.setSpan(new UnderlineSpan(), 13, str.length(), 0);
        spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.app_color)), 13, str.length(), 0);

        // clickable text
        ClickableSpan clickableSpan = new ClickableSpan() {

            @Override
            public void onClick(View widget) {
                // We display a Toast. You could do anything you want here.
                Toast.makeText(RegisterActivity.this, "Clicked", Toast.LENGTH_SHORT).show();

            }
        };

        spannableString.setSpan(clickableSpan, 13, str.length(), 0);
        return spannableString;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ImageView_onOff:
                if (mImageView_onOff.getTag() == false) {
                    mImageView_onOff.setTag(true);
                } else if (mImageView_onOff.getTag() == true) {
                    mImageView_onOff.setTag(false);
                }
                changeSwitchButtonBackground();

            default:
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        FinDotsApplication.getInstance().trackScreenView("Registration Screen");


    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }


    protected synchronized void buildGoogleApiClient() {

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this, 34992, this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }
        Utils.createLocationServiceChecker(mGoogleApiClient, RegisterActivity.this);
    }

    @Override
    public void onConnected(Bundle bundle) {


        currentLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

        if (currentLocation == null) {
            try {
                LocationServices.FusedLocationApi.removeLocationUpdates(
                        mGoogleApiClient, this);

            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                LocationRequest mLocationRequest = new LocationRequest();
                mLocationRequest.setInterval(10000);
                mLocationRequest.setFastestInterval(5000);
                mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                LocationServices.FusedLocationApi.requestLocationUpdates(
                        mGoogleApiClient, mLocationRequest, this);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onLocationChanged(Location location) {
        try {
            if (location != null)
                currentLocation = location;
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(RegisterActivity.this, "Connection Failed", Toast.LENGTH_SHORT).show();
    }


    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    public void startMenuActivity() {
        Intent intent = new Intent(RegisterActivity.this, MenuActivity.class);
        intent.putExtra("fromRegister", true);
        startActivity(intent);
        finish();
    }
}
