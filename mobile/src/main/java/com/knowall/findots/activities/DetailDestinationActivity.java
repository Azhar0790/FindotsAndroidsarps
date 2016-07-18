package com.knowall.findots.activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.knowall.findots.Constants;
import com.knowall.findots.FinDotsApplication;
import com.knowall.findots.R;
import com.knowall.findots.database.DataHelper;
import com.knowall.findots.events.AppEvents;
import com.knowall.findots.locationUtils.LocationModel.LocationData;
import com.knowall.findots.locationUtils.Utils;
import com.knowall.findots.restcalls.checkInCheckOut.CheckInCheckOutRestCall;
import com.knowall.findots.restcalls.checkInCheckOut.ICheckInCheckOut;
import com.knowall.findots.restcalls.destinations.DestinationData;
import com.knowall.findots.restcalls.destinations.DestinationsModel;
import com.knowall.findots.restcalls.destinations.GetDestinationsRestCall;
import com.knowall.findots.restcalls.destinations.IGetDestinations;
import com.knowall.findots.restmodels.ResponseModel;
import com.knowall.findots.utils.AppStringConstants;
import com.knowall.findots.utils.GeneralUtils;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by parijathar on 6/21/2016.
 */
public class DetailDestinationActivity extends AppCompatActivity implements
        OnMapReadyCallback, ICheckInCheckOut, IGetDestinations,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener {

    @Bind(R.id.TextView_map_km)
    TextView mTextView_map_km;

    @Bind(R.id.imageView_back)
    ImageView imageView_back;

    @Bind(R.id.TextView_address)
    TextView mTextView_address;

    @Bind(R.id.LinearLayout_checkIncheckOut)
    LinearLayout mLinearLayout_checkIncheckOut;

    @Bind(R.id.Button_checkIncheckOut)
    Button mButton_checkIncheckOut;

    @Bind(R.id.TextView_heading)
    TextView mTextView_heading;

    @Bind(R.id.destModify)
    TextView mDestModify;

    @Bind(R.id.imageViewDirections)
    ImageView imageViewDirections;

    Toolbar mToolbar = null;

    private GoogleApiClient mGoogleApiClient;
    GoogleMap mGoogleMap = null;
    Circle mCircle = null;
    LatLng latLng = null;

    Bundle bundle = null;

    int assignDestinationID = 0, destinationID = 0;
    double destinationLatitude = 0, destinationLongitude = 0, checkInRadius = 0;
    boolean checkedIn = false, checkedOut = false, isEditable = false, isRequiresApproval = false;
    String address = null, destinationName = null, checkedOutReportedDate = null;

    public static String offlineCheckInCheckOutStatus = null;
    public static boolean FLAG_CHECKINCHECKOUT = false;
    public static boolean FLAG_OFFLINECHECKINCHECKOUT = false;
    public static final int STROKE_WIDTH = 6;
    private static final int REQUEST_CODE_MODIFY_DESTINATION = 1;
    double currentLatitude = 0.0, currentLongitude = 0.0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_destination);

        ButterKnife.bind(this);

        getBundleData();
        actionBarSettings();


        setData();

        checkLocationData();

        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);

        mTextView_address.setMovementMethod(new ScrollingMovementMethod());
//checkin and check out validation for modify is commented
//        if (isEditable && !(checkedIn || checkedOut)) {
        if (isEditable) {
//            SpannableString mDestModifyText = new SpannableString(getResources().getString(R.string.modify));
//            mDestModifyText.setSpan(new RelativeSizeSpan(1.1f), 0, 30, 0);
//            mDestModifyText.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.black_70)), 0, 23, 0);
//            mDestModifyText.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.app_color)), 24, 30, 0);
//            mDestModify.setText(mDestModifyText);
            mDestModify.setVisibility(View.VISIBLE);
        }

        EventBus.getDefault().register(this);

    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);

    }

    /**
     * get Bundle data
     */

    public void getBundleData() {
        bundle = getIntent().getExtras();
        address = bundle.getString("address");
        checkedIn = bundle.getBoolean("checkedIn");
        checkedOut = bundle.getBoolean("checkedOut");
        destinationName = bundle.getString("destinationName");
        assignDestinationID = bundle.getInt("assignDestinationID");
        destinationID = bundle.getInt("destinationID");
        destinationLatitude = bundle.getDouble("destinationLatitude");
        destinationLongitude = bundle.getDouble("destinationLongitude");
        checkedOutReportedDate = bundle.getString("checkedOutReportedDate");
        checkInRadius = (double) bundle.getInt("checkInRadius");
        isEditable = bundle.getBoolean("editable");
        isRequiresApproval = bundle.getBoolean("requireApproval");
    }

    /**
     * set the DATa
     *
     * @param
     */
    public void setData() {

        imageView_back.setVisibility(View.VISIBLE);

        if (address.length() == 0) {
            mTextView_address.setText(getString(R.string.no_address));
        } else {
            mTextView_address.setText(address);
        }

        if (!checkedIn) {
            mLinearLayout_checkIncheckOut.setBackgroundResource(R.drawable.selector_checkin);
            mLinearLayout_checkIncheckOut.setEnabled(true);
            mButton_checkIncheckOut.setEnabled(true);
            mButton_checkIncheckOut.setText(getString(R.string.checkin));
            mButton_checkIncheckOut.setTextColor(getResources().getColor(R.color.green));
        } else if (!checkedOut) {
            mLinearLayout_checkIncheckOut.setBackgroundResource(R.drawable.selector_checkout);
            mLinearLayout_checkIncheckOut.setEnabled(true);
            mButton_checkIncheckOut.setEnabled(true);
            mButton_checkIncheckOut.setText(getString(R.string.checkout));
            mButton_checkIncheckOut.setTextColor(getResources().getColor(R.color.app_color));
        } else {
            mLinearLayout_checkIncheckOut.setBackgroundResource(R.drawable.selector_checked_at);
            mLinearLayout_checkIncheckOut.setEnabled(false);
            mButton_checkIncheckOut.setEnabled(false);
            mButton_checkIncheckOut.setText(getString(R.string.checkedout_at) + " " + GeneralUtils.getCheckedOutTime(checkedOutReportedDate));
            mButton_checkIncheckOut.setCompoundDrawables(
                    GeneralUtils.scaleDrawable(getResources().getDrawable(R.drawable.checkedout_tick), 40, 40),
                    null, null, null);
            mButton_checkIncheckOut.setTextColor(Color.WHITE);
        }

        mButton_checkIncheckOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!checkedIn)
                    isDeviceEnteredWithinDestinationRadius(true, false);
                else
                    isDeviceEnteredWithinDestinationRadius(true, true);
            }
        });

        mLinearLayout_checkIncheckOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkedIn)
                    isDeviceEnteredWithinDestinationRadius(true, false);
                else
                    isDeviceEnteredWithinDestinationRadius(true, true);
            }
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        latLng = new LatLng(destinationLatitude, destinationLongitude);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.title(destinationName);
        markerOptions.position(latLng);
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker));

        mGoogleMap.addMarker(markerOptions);

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.10);

        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(getCenterCoordinates(), (width - 300), (height - 300), padding));
        googleMapSettings();
    }


    public LatLngBounds getCenterCoordinates() {

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(new LatLng(destinationLatitude, destinationLongitude));
        builder.include(new

                LatLng(currentLatitude, currentLongitude)

        );

        LatLngBounds bounds = builder.build();
        return bounds;
    }

    public void googleMapSettings() {
        mGoogleMap.setMyLocationEnabled(true);
        mGoogleMap.getUiSettings().setCompassEnabled(true);
        mGoogleMap.getUiSettings().setZoomGesturesEnabled(true);
        mGoogleMap.getUiSettings().setRotateGesturesEnabled(true);
        mCircle = mGoogleMap.addCircle(drawCircleOnMap());
        isDeviceEnteredWithinDestinationRadius(false, false);
    }

    public CircleOptions drawCircleOnMap() {
        return new CircleOptions()
                .center(latLng)
                .radius(checkInRadius)
                .strokeColor(Color.TRANSPARENT)
                .fillColor(getResources().getColor(R.color.app_color_10))
                .strokeWidth(STROKE_WIDTH);
    }

    public void actionBarSettings() {

        /* Assigning the toolbar object ot the view
         * and setting the the Action bar to our toolbar
         */
        mToolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Typeface typefaceMyriadHebrew = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf");

        if (destinationName.toString().length() > 0)
            mTextView_heading.setText("" + destinationName);
        else {
            destinationName = getResources().getString(R.string.destinations);
            mTextView_heading.setText("" + destinationName);
        }
        mTextView_heading.setTypeface(typefaceMyriadHebrew);
        imageViewDirections.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.imageViewDirections)
    public void openDirectionsMap() {
        String uri = "http://maps.google.com/maps?saddr=" + currentLatitude + "," + currentLongitude + "&daddr=" + destinationLatitude + "," + destinationLongitude;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        startActivity(intent);
    }

    @OnClick(R.id.imageView_back)
    public void goBack() {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {


        if (FLAG_CHECKINCHECKOUT) {
            Log.i(Constants.TAG, "onBackPressed: FLAG_CHECKINCHECKOUT "+offlineCheckInCheckOutStatus);
            Intent returnIntent = new Intent();
            returnIntent.putExtra("result", "success");
            setResult(Activity.RESULT_OK, returnIntent);
        } else if(FLAG_OFFLINECHECKINCHECKOUT){
            Log.i(Constants.TAG, "onBackPressed: FLAG_OFFLINECHECKINCHECKOUT "+offlineCheckInCheckOutStatus);
            Intent returnIntent2 = new Intent();
            returnIntent2.putExtra("offlineData", offlineCheckInCheckOutStatus);
            setResult(2, returnIntent2);
        }

        super.onBackPressed();
    }

    public void isDeviceEnteredWithinDestinationRadius(boolean requestForCheckInCheckOut, boolean isCheckOut) {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        List<String> providers = locationManager.getProviders(true);
        //Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Location location = null;

        for (String provider : providers) {

            Location currentLocation = locationManager.getLastKnownLocation(provider);
            if (currentLocation == null) {
                continue;
            }
            if (location == null || currentLocation.getAccuracy() < location.getAccuracy()) {
                location = currentLocation;
            }

        }

        if (location != null) {

            currentLatitude = location.getLatitude();
            currentLongitude = location.getLongitude();
        } else {
            DataHelper dataHelper = DataHelper.getInstance(DetailDestinationActivity.this);
            List<LocationData> locationLatestData = dataHelper.getLocationLastRecord();
            if (locationLatestData.size() > 0) {
                for (LocationData locLastData : locationLatestData) {
                    currentLatitude = locLastData.getLatitude();
                    currentLongitude = locLastData.getLongitude();
                }
            }
        }

        if (isCheckOut) {

            CheckInCheckOutRestCall restCall = new CheckInCheckOutRestCall(DetailDestinationActivity.this);
            restCall.delegate = DetailDestinationActivity.this;
            restCall.callCheckInService(checkedIn, assignDestinationID, currentLatitude, currentLongitude);

        } else {

            float[] distance = new float[2];

            Location.distanceBetween(currentLatitude, currentLongitude, destinationLatitude, destinationLongitude, distance);

            if (distance[0] > mCircle.getRadius()) {
                /**
                 *   Outside the Radius
                 */
                String kilometers = new DecimalFormat("##.##").format(distance[0] / 1000);

                mTextView_map_km.setText(kilometers + " km");

                if (requestForCheckInCheckOut) {
                    GeneralUtils.createAlertDialog(DetailDestinationActivity.this,
                            getString(R.string.not_the_right_destination));
                }
            } else {
                /**
                 *   Inside the Radius
                 */
                String kilometers = new DecimalFormat("##.##").format(distance[0] / 1000);

                mTextView_map_km.setText(kilometers + " km");

                if (requestForCheckInCheckOut) {
                    CheckInCheckOutRestCall restCall = new CheckInCheckOutRestCall(DetailDestinationActivity.this);
                    restCall.delegate = DetailDestinationActivity.this;
                    restCall.callCheckInService(checkedIn, assignDestinationID, currentLatitude, currentLongitude);
                }
            }
        }
    }


    @Override
    public void onCheckInFailure(String status) {
        FLAG_CHECKINCHECKOUT = false;
        FLAG_OFFLINECHECKINCHECKOUT = true;
        if (!checkedIn) {
            checkedIn = true;
            offlineCheckInCheckOutStatus = "checkedIn";
        } else if(!checkedOut) {
            checkedOut = true;
            //offlineCheckInCheckOutStatus = "checkedOut";

            DateTimeFormatter fmt1 = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS");
            DateTime dateTime = new DateTime();
            String getTime = dateTime.toString(fmt1);
            checkedOutReportedDate = getTime;
            offlineCheckInCheckOutStatus = checkedOutReportedDate;
        }
        /**
         *   call setData
         */
        setData();

        //GeneralUtils.createAlertDialog(DetailDestinationActivity.this, status);
    }

    @Override
    public void onCheckInSuccess() {
        FLAG_CHECKINCHECKOUT = true;
        /**
         *   the data should be refreshed after the checkin or checkout
         */
        GetDestinationsRestCall destinationsRestCall = new GetDestinationsRestCall(DetailDestinationActivity.this);
        destinationsRestCall.delegate = DetailDestinationActivity.this;
        destinationsRestCall.callGetDestinations();
    }

    @Override
    public void onGetDestinationSuccess(DestinationsModel destinationsModel) {

        DestinationData data = null;
        for (DestinationData destinationData : destinationsModel.getDestinationData()) {
            if (assignDestinationID == destinationData.getAssignDestinationID()) {
                data = destinationData;
                break;
            }
        }

        if (data != null) {
            address = data.getAddress();
            checkedIn = data.isCheckedIn();
            checkedOut = data.isCheckedOut();
            destinationName = data.getDestinationName();
            assignDestinationID = data.getAssignDestinationID();
            destinationLatitude = data.getDestinationLatitude();
            destinationLongitude = data.getDestinationLongitude();
            checkedOutReportedDate = data.getCheckedOutReportedDate();
            checkInRadius = data.getCheckInRadius();

            setData();
        }

    }

    @Override
    public void onGetDestinationFailure(String errorMessage) {
        GeneralUtils.createAlertDialog(DetailDestinationActivity.this, errorMessage);
    }

    @OnClick(R.id.destModify)
    public void modifyDestinationActivity() {
        if (assignDestinationID > -1) {

            PopupMenu popupMenu = new PopupMenu(DetailDestinationActivity.this, mDestModify);

            popupMenu.getMenuInflater().inflate(R.menu.edit_destination_type_popup, popupMenu.getMenu());

            popupMenu.show();
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {

                    if (item.getItemId() == R.id.item2) {
                        Intent intentModifyLoc = new Intent(DetailDestinationActivity.this, DestinationModify_MapActivity.class);
                        intentModifyLoc.putExtra("destinationID", destinationID);
                        intentModifyLoc.putExtra("destinationName", destinationName);
                        intentModifyLoc.putExtra("destinationLatitude", destinationLatitude);
                        intentModifyLoc.putExtra("destinationLongitude", destinationLongitude);
                        startActivityForResult(intentModifyLoc, REQUEST_CODE_MODIFY_DESTINATION);
                    }

                    if (item.getItemId() == R.id.item3) {
                        deleteAssigned_destinationRequest();
                    }

                    if (item.getItemId() == R.id.item1) {

                        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(DetailDestinationActivity.this);
                        View mView = layoutInflaterAndroid.inflate(R.layout.dialog_user_input, null);
                        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(DetailDestinationActivity.this);
                        alertDialogBuilderUserInput.setView(mView);
                        alertDialogBuilderUserInput.setTitle("" + getResources().getString(R.string.app_name));

                        final EditText userInputDialogEditText = (EditText) mView.findViewById(R.id.userInputDialog);
                        TextView userInputDialogTitle = (TextView) mView.findViewById(R.id.dialogTitle);
                        userInputDialogTitle.setText(getResources().getString(R.string.modify_destination_name));
                        userInputDialogEditText.setHint(getResources().getString(R.string.modify_destination_namehint));
                        alertDialogBuilderUserInput
                                .setCancelable(false)
                                .setPositiveButton("" + getResources().getString(R.string.add), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialogBox, int id) {
                                        if (userInputDialogEditText.getText().toString().length() > 0) {
                                            dialogBox.dismiss();
                                            renameAssigned_destinationRequest((userInputDialogEditText.getText().toString().trim()));
                                        }
                                    }
                                })

                                .setNegativeButton("" + getResources().getString(R.string.cancel),
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialogBox, int id) {
                                                dialogBox.cancel();
                                            }
                                        });

                        AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
                        alertDialogAndroid.show();
                    }


                    return true;

                }
            });
        }
    }

    /**
     * Called after the autocomplete activity has finished to return its result.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check that the result was from the autocomplete widget.
        if (requestCode == REQUEST_CODE_MODIFY_DESTINATION) {
            if (resultCode == RESULT_OK && data.getStringExtra("result").equals("success")) {

                if (isRequiresApproval == false) {
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("result", "success");
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                }
            }
        }
    }

    public void onEvent(AppEvents event) {
        switch (event) {
            case OFFLINECHECKIN:
                EventBus.getDefault().post(AppEvents.OFFLINECHECKIN);
                Log.d("jomy", "onLineCheckin");
                EventBus.getDefault().unregister(this);
                finish();
                break;
            case OFFLINECHECKOUT:
                EventBus.getDefault().post(AppEvents.OFFLINECHECKOUT);
                Log.d("jomy", "onLineCheckOut");
                EventBus.getDefault().unregister(this);
                finish();
                break;
        }
    }

    public void checkLocationData() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (location != null) {
            currentLatitude = location.getLatitude();
            currentLongitude = location.getLongitude();
        } else {

            DataHelper dataHelper = DataHelper.getInstance(this);
            List<LocationData> locationLatestData = dataHelper.getLocationLastRecord();
            if (locationLatestData.size() > 0) {
                for (LocationData locLastData : locationLatestData) {
                    currentLatitude = locLastData.getLatitude();
                    currentLongitude = locLastData.getLongitude();
                }
            } else {

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    return;
                }
                if (!(Utils.isLocationServiceEnabled(this))) {
                    Utils.createLocationServiceError(this);
                }

                buildGoogleApiClient();
            }
        }

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {

            currentLatitude = mLastLocation.getLatitude();
            currentLongitude = mLastLocation.getLongitude();
        } else
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

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        try {
            if (location != null) {
                currentLatitude = location.getLatitude();
                currentLongitude = location.getLongitude();
            }

            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public void deleteAssigned_destinationRequest() {
        GeneralUtils.initialize_progressbar(this);
        Call<ResponseModel> addDestinationCall = FinDotsApplication.getRestClient().getApiService().deleteAssignedDestination(deleteAssigned_DestinationRequest());

        addDestinationCall.enqueue(new Callback<ResponseModel>() {


            @Override
            public void onResponse(Response<ResponseModel> response, Retrofit retrofit) {
                GeneralUtils.stop_progressbar();

                if (response.body() != null) {
                    if (response.isSuccess() && response.body().getErrorCode() == 0) {

                        Toast.makeText(DetailDestinationActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();


                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("result", "deletedDestination");
                        setResult(Activity.RESULT_OK, returnIntent);
                        finish();
                    } else
                        Toast.makeText(DetailDestinationActivity.this, getResources().getString(R.string.delete_destinationError), Toast.LENGTH_SHORT).show();
                } else {
                    Toast toast = Toast.makeText(DetailDestinationActivity.this, getString(R.string.data_error), Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                GeneralUtils.stop_progressbar();
                Toast.makeText(DetailDestinationActivity.this, getResources().getString(R.string.delete_destinationError), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private Map<String, Object> deleteAssigned_DestinationRequest() {
        Map<String, Object> postValues = new HashMap<>();
        postValues.put("assignedDestinationID", assignDestinationID);
        postValues.put("appVersion", GeneralUtils.getAppVersion(this));
        postValues.put("deviceTypeID", Constants.DEVICETYPEID);
        postValues.put("deviceID", GeneralUtils.getUniqueDeviceId(this));
        postValues.put("deviceInfo", GeneralUtils.getDeviceInfo());
        postValues.put("userID", GeneralUtils.getSharedPreferenceInt(this, AppStringConstants.USERID));
        postValues.put("ipAddress", "");

        return postValues;
    }

    public void renameAssigned_destinationRequest(String destinationName) {
        GeneralUtils.initialize_progressbar(this);
        Call<ResponseModel> addDestinationCall = FinDotsApplication.getRestClient().getApiService().renameAssignedDestination(renameAssigned_DestinationRequest(destinationName));

        addDestinationCall.enqueue(new Callback<ResponseModel>() {


            @Override
            public void onResponse(Response<ResponseModel> response, Retrofit retrofit) {
                GeneralUtils.stop_progressbar();

                if (response.body() != null) {
                    if (response.isSuccess() && response.body().getErrorCode() == 0) {

                        Toast.makeText(DetailDestinationActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();


                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("result", "renamedDestination");
                        setResult(Activity.RESULT_OK, returnIntent);
                        finish();
                    } else
                        Toast.makeText(DetailDestinationActivity.this, getResources().getString(R.string.delete_destinationError), Toast.LENGTH_SHORT).show();
                } else {
                    Toast toast = Toast.makeText(DetailDestinationActivity.this, getString(R.string.data_error), Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                GeneralUtils.stop_progressbar();
                Toast.makeText(DetailDestinationActivity.this, getResources().getString(R.string.delete_destinationError), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private Map<String, Object> renameAssigned_DestinationRequest(String destnationName) {
        Map<String, Object> postValues = new HashMap<>();
        postValues.put("destinationID", destinationID);
        postValues.put("destinationName", "" + destnationName);
        postValues.put("appVersion", GeneralUtils.getAppVersion(this));
        postValues.put("deviceTypeID", Constants.DEVICETYPEID);
        postValues.put("deviceID", GeneralUtils.getUniqueDeviceId(this));
        postValues.put("deviceInfo", GeneralUtils.getDeviceInfo());
        postValues.put("userID", GeneralUtils.getSharedPreferenceInt(this, AppStringConstants.USERID));
        postValues.put("ipAddress", "");

        return postValues;
    }
}
