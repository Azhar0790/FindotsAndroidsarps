package com.knowall.findots.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.method.ScrollingMovementMethod;
import android.text.style.UnderlineSpan;
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
import com.knowall.findots.events.AppEvents;
import com.knowall.findots.locationUtils.Utils;
import com.knowall.findots.restcalls.checkInCheckOut.CheckInCheckOutRestCall;
import com.knowall.findots.restcalls.checkInCheckOut.ICheckInCheckOut;
import com.knowall.findots.restcalls.destinations.DestinationData;
import com.knowall.findots.restcalls.destinations.DestinationsModel;
import com.knowall.findots.restcalls.destinations.GetDestinationsRestCall;
import com.knowall.findots.restcalls.destinations.IGetDestinations;
import com.knowall.findots.restmodels.ResponseModel;
import com.knowall.findots.utils.AddTextWatcher;
import com.knowall.findots.utils.AppStringConstants;
import com.knowall.findots.utils.GeneralUtils;
import com.knowall.findots.utils.timeUtils.TimeSettings;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

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
        com.google.android.gms.location.LocationListener,
        TimePickerDialog.OnTimeSetListener,
        DatePickerDialog.OnDateSetListener {


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


    @Bind(R.id.destSchedule)
    TextView mdestSchedule;

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
    boolean checkedIn = false, checkedOut = false, isEditable = false, isRequiresApproval = false, destRenamOnly = false, rescheduleFlag = false,mMapLoaded=false;
    String address = null, destinationName = null, checkedOutReportedDate = null;

    public static String offlineCheckInCheckOutStatus = null;
    public static boolean FLAG_CHECKINCHECKOUT = false;
    public static boolean FLAG_OFFLINECHECKINCHECKOUT = false;
    public boolean mFlag_Scheduled = false;
    public static final int STROKE_WIDTH = 6;
    private static final int REQUEST_CODE_MODIFY_DESTINATION = 1;
    double currentLatitude = 0, currentLongitude = 0;
    String scheduleDate = "", serverRequest_scheduleDate = "";
    int day, month, year;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_destination);

        ButterKnife.bind(this);
        buildGoogleApiClient();
        getBundleData();
        actionBarSettings();
        setData();

//        checkLocationData();

        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);

        mTextView_address.setMovementMethod(new ScrollingMovementMethod());
///checkin and check out validation for modify is commented
        modifyTextUiCondition(checkedIn, checkedOut);

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

    public void modifyTextUiCondition(Boolean checkedIn, Boolean checkedOut) {
        if (isEditable) {
            if (!(checkedIn || checkedOut)) {
                destRenamOnly = false;
                mDestModify.setVisibility(View.VISIBLE);
            } else {
                destRenamOnly = true;
            }
        }
    }

    public void scheduleTextUiCondition(Boolean checkedIn, Boolean checkedOut, String scheduleDate) {
        Log.d("jomy", "Schedule Date2 : " + scheduleDate + " Schedule  : " + checkCurrentTimeGreater(scheduleDate));
        if ((checkedIn && checkedOut) || checkCurrentTimeGreater(scheduleDate)) {
            rescheduleFlag = true;
            setSpannableScheduleString(getString(R.string.reschedule));
        } else if (scheduleDate.trim().length() > 0) {
            extractDateInfo(scheduleDate, false);
        } else
            setSpannableScheduleString("" + getString(R.string.schedule));

    }

    public boolean checkCurrentTimeGreater(String scheduleDate) {
        long difference = 0;
        try {
            Date currentDate = new Date();

            SimpleDateFormat sdfLocal = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            sdfLocal.setTimeZone(TimeZone.getDefault());
            Date scheduleTime = sdfLocal.parse(scheduleDate);

            if ((scheduleTime.getTime() - currentDate.getTime()) < 0)
                return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
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
        scheduleDate = bundle.getString("scheduleDate");
        if (scheduleDate.trim().length() > 0)
            extractDateInfo(scheduleDate, false);
        else
            setSpannableScheduleString("" + getString(R.string.schedule));


//        if (scheduleDate.trim().length() > 0)
//            extractDateInfo(scheduleDate, false);
//        else
//            setSpannableScheduleString("" + getString(R.string.schedule));
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
            mButton_checkIncheckOut.setText(getString(R.string.checkedout_at) + " " + TimeSettings.getTimeOnly(checkedOutReportedDate));
            mButton_checkIncheckOut.setCompoundDrawables(
                    GeneralUtils.scaleDrawable(getResources().getDrawable(R.drawable.checkedout_tick), 40, 40),
                    null, null, null);
            mButton_checkIncheckOut.setTextColor(Color.WHITE);
        }

        mButton_checkIncheckOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkedIn == true && checkedOut == false) {
                    checkOutNote();
                } else {
                    if (!checkedIn)
                        isDeviceEnteredWithinDestinationRadius(true, false, "");
                    else
                        isDeviceEnteredWithinDestinationRadius(true, true, "");
                }
            }
        });

        mLinearLayout_checkIncheckOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkedIn == true && checkedOut == false) {
                    checkOutNote();
                } else {
                    if (!checkedIn)
                        isDeviceEnteredWithinDestinationRadius(true, false, "");
                    else
                        isDeviceEnteredWithinDestinationRadius(true, true, "");
                }
            }
        });

        scheduleTextUiCondition(checkedIn, checkedOut, scheduleDate);
    }


    public void checkOutNote() {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(DetailDestinationActivity.this);
        View mView = layoutInflaterAndroid.inflate(R.layout.dialog_user_input, null);
        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(DetailDestinationActivity.this);
        alertDialogBuilderUserInput.setView(mView);
        alertDialogBuilderUserInput.setTitle(getString(R.string.app_name));

        final EditText mEditText = (EditText) mView.findViewById(R.id.userInputDialog);
        mEditText.addTextChangedListener(new AddTextWatcher(mEditText));

        mEditText.setHint("Check Out Note");
        mEditText.requestFocus();

        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton(getString(R.string.done), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String checkOutNote = mEditText.getText().toString();
                        if (!checkedIn)
                            isDeviceEnteredWithinDestinationRadius(true, false, checkOutNote);
                        else
                            isDeviceEnteredWithinDestinationRadius(true, true, checkOutNote);

                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    }
                });

        AlertDialog alertDialog = alertDialogBuilderUserInput.create();
        alertDialog.show();

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        googleMapSettings();
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        latLng = new LatLng(destinationLatitude, destinationLongitude);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.title(destinationName);
        markerOptions.position(latLng);
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker));
        mGoogleMap.addMarker(markerOptions);
        mGoogleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                mMapLoaded=true;
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(getCenterCoordinates(), 200));
                mCircle = mGoogleMap.addCircle(drawCircleOnMap());

//            }
//                addAllDestinationsOnMap();
//                showAllMarkers();
            }
        });


//        mGoogleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
//
//            @Override
//            public void onCameraChange(CameraPosition arg0) {
//                // Move camera.
//                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(getCenterCoordinates(), 150));
//            }
//        });
//        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(getCenterCoordinates(),  padding));

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


    @OnClick(R.id.destSchedule)
    public void scheduleDestination() {

        Calendar calendarDate = Calendar.getInstance();
        if (serverRequest_scheduleDate != null && serverRequest_scheduleDate.trim().length() > 0 && (((TimeSettings.getTimeDifference(serverRequest_scheduleDate)) / 60000) > 0)) {
            Date date = new Date();
            SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                date = date_format.parse(serverRequest_scheduleDate);
                calendarDate.setTime(date);
            } catch (Exception e) {
            }
        }


        DatePickerDialog dpd = DatePickerDialog.newInstance(
                DetailDestinationActivity.this,
                calendarDate.get(Calendar.YEAR),
                calendarDate.get(Calendar.MONTH),
                calendarDate.get(Calendar.DAY_OF_MONTH)
        );

        Calendar calendarnow = Calendar.getInstance();
        dpd.setMinDate(calendarnow);

        dpd.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                Log.d("jomy", "Dialog was cancelled");
            }
        });
        dpd.setAccentColor(getResources().getColor(R.color.app_color));
        dpd.setTitle("" + getString(R.string.schedulevisitDate));
        dpd.show(getFragmentManager(), "Datepickerdialog");
    }

    boolean isToday = false;

    @Override
    public void onDateSet(DatePickerDialog view, int yearVal, int monthOfYear, int dayOfMonth) {
        Log.d("jomy", "You picked the following date: " + dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
        Calendar calendarDate = Calendar.getInstance();
        boolean cureentDateSelected = ((calendarDate.get(Calendar.YEAR) == yearVal) && (calendarDate.get(Calendar.MONTH) == monthOfYear) && (calendarDate.get(Calendar.DAY_OF_MONTH) == dayOfMonth));
        if ((serverRequest_scheduleDate != null && serverRequest_scheduleDate.trim().length() > 0)) {
            if (!cureentDateSelected) {
                Date date = new Date();
                SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    date = date_format.parse(serverRequest_scheduleDate);
                    calendarDate.setTime(date);
                } catch (Exception e) {
                }
            }
        }

        year = yearVal;
        month = (monthOfYear + 1);
        day = dayOfMonth;


        Log.d("jomy", "Today....");
        TimePickerDialog tpd = TimePickerDialog.newInstance(
                DetailDestinationActivity.this,
                calendarDate.get(Calendar.HOUR_OF_DAY),
                calendarDate.get(Calendar.MINUTE),
                false
        );
//        tpd.setThemeDark(true);
//        tpd.vibrate(true);
//        tpd.dismissOnPause(true);
//        tpd.enableSeconds(true);
        Calendar calendarnow = Calendar.getInstance();
        if (cureentDateSelected) {
            tpd.setMinTime(calendarnow.get(Calendar.HOUR_OF_DAY), calendarnow.get(Calendar.MINUTE), 30);
        }
        tpd.enableMinutes(true);
        tpd.setAccentColor(getResources().getColor(R.color.app_color));
        tpd.setTitle("" + getString(R.string.schedulevisitTime));

        tpd.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
            }
        });
        tpd.show(getFragmentManager(), "Timepickerdialog");

    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
        Log.d("jomy", "You picked the following Time: " + hourOfDay + "/" + (minute) + "/" + second);

        extractDateInfo("" + year + "-" + month + "-" + day + " " + hourOfDay + ":" + minute + ":" + second + ".000", true);
    }

    @Override
    public void onBackPressed() {
        if (FLAG_CHECKINCHECKOUT) {
            Log.i(Constants.TAG, "onBackPressed: FLAG_CHECKINCHECKOUT " + offlineCheckInCheckOutStatus);
            Intent returnIntent = new Intent();
            returnIntent.putExtra("result", "success");
            setResult(Activity.RESULT_OK, returnIntent);
        } else if (FLAG_OFFLINECHECKINCHECKOUT) {
            Log.i(Constants.TAG, "onBackPressed: FLAG_OFFLINECHECKINCHECKOUT " + offlineCheckInCheckOutStatus);
            Intent returnIntent2 = new Intent();
            returnIntent2.putExtra("offlineData", offlineCheckInCheckOutStatus);
            setResult(2, returnIntent2);
        } else if (mFlag_Scheduled) {
            Log.d("jomy", "check.00..");
            Intent returnIntent3 = new Intent();
            returnIntent3.putExtra("scheduled", "ScheduledData");
            setResult(3, returnIntent3);
        }

        super.onBackPressed();
    }

    public void isDeviceEnteredWithinDestinationRadius(boolean requestForCheckInCheckOut,
                                                       boolean isCheckOut, String checkOutNote) {
        Location mLastLoc;
        if (mGoogleApiClient != null && ((mLastLoc = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient)) != null)) {
            if (isCheckOut) {
                CheckInCheckOutRestCall restCall = new CheckInCheckOutRestCall(DetailDestinationActivity.this);
                restCall.delegate = DetailDestinationActivity.this;
                restCall.callCheckInService(checkOutNote, checkedIn, assignDestinationID, mLastLoc.getLatitude(), mLastLoc.getLongitude());

            } else if (requestForCheckInCheckOut)
                setLocationDistanceText(requestForCheckInCheckOut, mLastLoc.getLatitude(), mLastLoc.getLongitude(), checkOutNote);

        } else
            buildGoogleApiClient();
    }

    public void setLocationDistanceText(boolean requestForCheckInCheckOut, double mCurrentlatitude,
                                        double mCurrentlongitude, String checkOutNote) {
        float[] distance = new float[2];

        Location.distanceBetween(mCurrentlatitude, mCurrentlongitude, destinationLatitude, destinationLongitude, distance);
        String kilometers = new DecimalFormat("##.#").format(distance[0] / 1000);

        mTextView_map_km.setText(kilometers + " "+getResources().getString(R.string.km));
        if (requestForCheckInCheckOut) {

            if (distance[0] <= mCircle.getRadius()) {
                CheckInCheckOutRestCall restCall = new CheckInCheckOutRestCall(DetailDestinationActivity.this);
                restCall.delegate = DetailDestinationActivity.this;
                restCall.callCheckInService(checkOutNote, checkedIn, assignDestinationID, currentLatitude, currentLongitude);
            } else {
                Toast.makeText(this, getResources().getString(R.string.not_the_right_destination), Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public void onCheckInFailure(String status) {

        Toast.makeText(DetailDestinationActivity.this, status, Toast.LENGTH_SHORT).show();

        FLAG_CHECKINCHECKOUT = false;
        FLAG_OFFLINECHECKINCHECKOUT = true;
        if (!checkedIn) {
            checkedIn = true;
            offlineCheckInCheckOutStatus = "checkedIn";
        } else if (!checkedOut) {
            checkedOut = true;
            //offlineCheckInCheckOutStatus = "checkedOut";

            String getTime = TimeSettings.DateTimeInUTC();
            getTime = TimeSettings.dateTimeInUTCToLocal(getTime);
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
    public void onCheckInSuccess(String status) {
        FLAG_CHECKINCHECKOUT = true; Toast.makeText(DetailDestinationActivity.this, status, Toast.LENGTH_SHORT).show();

        /**
         *   the data should be refreshed after the checkin or checkout
         */
        GetDestinationsRestCall destinationsRestCall = new GetDestinationsRestCall(DetailDestinationActivity.this);
        destinationsRestCall.delegate = DetailDestinationActivity.this;
        destinationsRestCall.callGetDestinations();
        modifyTextUiCondition(true, true);
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

            if (destRenamOnly)
                popupMenu.getMenuInflater().inflate(R.menu.edit_destination_type_popup_singleitem, popupMenu.getMenu());
            else
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

                        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(DetailDestinationActivity.this);
                        alertDialogBuilderUserInput.setTitle("" + getResources().getString(R.string.app_name));

                        alertDialogBuilderUserInput.setMessage("" + getResources().getString(R.string.delete_destinationalert) + "" + destinationName);


                        alertDialogBuilderUserInput
                                .setCancelable(false)
                                .setPositiveButton("" + getResources().getString(R.string.delete), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialogBox, int id) {
                                        deleteAssigned_destinationRequest();
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

                    if (item.getItemId() == R.id.item1) {

                        renameDestinationPopupAction();
                    }
                    if (item.getItemId() == R.id.item4) {

                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.setPackage("com.whatsapp");
                        sendIntent.setType("text/plain");
                        startActivity(sendIntent);
                    }


                    return true;

                }
            });
        }
    }

    public void renameDestinationPopupAction() {

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


    protected synchronized void buildGoogleApiClient() {

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this, 34992, this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }
        Utils.createLocationServiceChecker(mGoogleApiClient, DetailDestinationActivity.this);
    }


    @Override
    public void onConnected(Bundle bundle) {


        Location currentLocation = LocationServices.FusedLocationApi.getLastLocation(
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
        } else {
            currentLatitude = currentLocation.getLatitude();
            currentLongitude = currentLocation.getLongitude();
            setLocationDistanceText(false, currentLatitude, currentLongitude, "");
            if(mMapLoaded)
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(getCenterCoordinates(), 200));
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
                setLocationDistanceText(false, currentLatitude, currentLongitude, "");
                if(mMapLoaded)
                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(getCenterCoordinates(), 200));
            }
            Log.d("jomy", "Cureent Lat22 : " + currentLatitude);
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


    public void addScheduleDestinationRequest(String scheduletime) {
        GeneralUtils.initialize_progressbar(this);
        Call<ResponseModel> addDestinationCall = FinDotsApplication.getRestClient().getApiService().scheduleDestinationVisit(setScheduleVisit_destinationRequest(TimeSettings.dateTimeInUTC(scheduletime)));

        addDestinationCall.enqueue(new Callback<ResponseModel>() {


            @Override
            public void onResponse(Response<ResponseModel> response, Retrofit retrofit) {
                GeneralUtils.stop_progressbar();

                if (response.body() != null) {
                    if (response.isSuccess() && response.body().getErrorCode() == 0) {

                        extractDateInfo(serverRequest_scheduleDate, false);
                        mFlag_Scheduled = true;
                        Toast.makeText(DetailDestinationActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();


                    } else
                        Toast.makeText(DetailDestinationActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
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

    private Map<String, Object> setScheduleVisit_destinationRequest(String scheduletime) {
        Map<String, Object> postValues = new HashMap<>();
        postValues.put("assignDestinationID", assignDestinationID);
        postValues.put("scheduleDate", "" + scheduletime);
        postValues.put("appVersion", GeneralUtils.getAppVersion(this));
        postValues.put("deviceTypeID", Constants.DEVICETYPEID);
        postValues.put("deviceID", GeneralUtils.getUniqueDeviceId(this));
        postValues.put("deviceInfo", GeneralUtils.getDeviceInfo());
        postValues.put("userID", GeneralUtils.getSharedPreferenceInt(this, AppStringConstants.USERID));
        postValues.put("ipAddress", "");

        return postValues;
    }

    public void reScheduleDestinationRequest(String reScheduletime) {
        GeneralUtils.initialize_progressbar(this);
        Call<ResponseModel> addDestinationCall = FinDotsApplication.getRestClient().getApiService().reScheduleDestinationVisit(setRescheduleVisit_destinationRequest(TimeSettings.dateTimeInUTC(reScheduletime)));

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

    private Map<String, Object> setRescheduleVisit_destinationRequest(String reScheduletime) {
        Map<String, Object> postValues = new HashMap<>();
        postValues.put("destinationID", destinationID);
        postValues.put("scheduleDate", "" + reScheduletime);
        postValues.put("appVersion", GeneralUtils.getAppVersion(this));
        postValues.put("deviceTypeID", Constants.DEVICETYPEID);
        postValues.put("deviceID", GeneralUtils.getUniqueDeviceId(this));
        postValues.put("deviceInfo", GeneralUtils.getDeviceInfo());
        postValues.put("userID", GeneralUtils.getSharedPreferenceInt(this, AppStringConstants.USERID));
        postValues.put("ipAddress", "");

        return postValues;
    }

    public void extractDateInfo(String scheduleDate, boolean loadData) {

        try {

            Date date = new Date();
            SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                date = date_format.parse(scheduleDate);
                Calendar cal1 = Calendar.getInstance();
                cal1.setTime(date);
                serverRequest_scheduleDate = scheduleDate;
                if (rescheduleFlag && loadData)
                    reScheduleDestinationRequest(serverRequest_scheduleDate);
                else
                    checkDateToday(cal1, loadData);
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("jomy", "Date22 ");
            }


        } catch (Exception e) {
            Log.d("jomy", "Date223 ");
        }


    }

    public void checkDateToday(Calendar cal1, boolean loadData) {
        try {
            String mdestScheduleText = "";
            int hour, minute;
            Date currentDate = Calendar.getInstance().getTime();
            Calendar cal2 = Calendar.getInstance();
            cal2.setTime(currentDate);
            if (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                    cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR) &&
                    cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH)) {
                mdestScheduleText = "Today @ ";
            } else {
                SimpleDateFormat month_date = new SimpleDateFormat("MMM");
                mdestScheduleText = "" + cal1.get(Calendar.DAY_OF_MONTH) + " " + month_date.format(cal1.getTime()) + " @ ";
            }
//            hour = cal1.get(Calendar.HOUR);
//            minute = cal1.get(Calendar.MINUTE);
//            int am = cal1.get(Calendar.AM_PM);
            SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            mdestScheduleText = mdestScheduleText + TimeSettings.getTimeOnly(date_format.format(cal1.getTime()));
//            if (am == Calendar.AM)
//                mdestScheduleText = mdestScheduleText + "AM";
//            else
//                mdestScheduleText = mdestScheduleText + "PM";

            if (loadData)
                addScheduleDestinationRequest(serverRequest_scheduleDate);
            else
                setSpannableScheduleString(mdestScheduleText);
        } catch (Exception e) {
        }
    }

    public void setSpannableScheduleString(String scheduleTime) {
        SpannableString content = new SpannableString(scheduleTime);
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        mdestSchedule.setText(content);
    }


}
