package com.knowall.findots.activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.knowall.findots.Constants;
import com.knowall.findots.FinDotsApplication;
import com.knowall.findots.R;
import com.knowall.findots.locationUtils.FetchAddressIntentService;
import com.knowall.findots.locationUtils.Utils;
import com.knowall.findots.restmodels.ResponseModel;
import com.knowall.findots.utils.AppStringConstants;
import com.knowall.findots.utils.GeneralUtils;
import com.knowall.findots.utils.MaterialDatePickerDialogCustom;
import com.knowall.findots.utils.MaterialTimePickerDialogCustom;
import com.knowall.findots.utils.NetworkChangeReceiver;
import com.knowall.findots.utils.mapUtils.MapStateListener;
import com.knowall.findots.utils.mapUtils.TouchableMapFragment;
import com.knowall.findots.utils.timeUtils.TimeSettings;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by jpaulose on 7/6/2016.
 */
public class DestinationAddMapActivity extends AppCompatActivity implements MaterialDatePickerDialogCustom.OnDateScheduledListener, MaterialTimePickerDialogCustom.OnTimeScheduledListener,OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener,
        TimePickerDialog.OnTimeSetListener,
        DatePickerDialog.OnDateSetListener {
    private long lastClickTime = 0;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static String TAG = "MAP LOCATION";
    Context mContext;
    private LatLng mCenterLatLong;
    Location mAdressLoc;
    String scheduleDate = "",destinationName;
    int day, month, year;
    /**
     * Receiver registered with this activity to get the response from FetchAddressIntentService.
     */
    private AddressResultReceiver mResultReceiver;
    /**
     * The formatted location address.
     */
    protected String mAddressOutput;
    protected String mAreaOutput;
    protected String mCityOutput;
    protected String mStateOutput;


    private static final int REQUEST_CODE_AUTOCOMPLETE = 1;
    Toolbar mToolbar;

    TouchableMapFragment mapFragment;

    @Bind(R.id.locationMarkertext)
    TextView mLocationMarkerText;
    @Bind(R.id.TextView_heading)
    TextView mTextView_heading;
    @Bind(R.id.addDestination)
    Button mAddDestination;
    @Bind(R.id.address)
    TextView mLocationAddress;
    @Bind(R.id.lochead)
    TextView mLochead;
    @Bind(R.id.imageView_back)
    ImageView imageView_back;
    Typeface typefaceRoboRegular;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_destination_map);
        ButterKnife.bind(this);
        mContext = this;
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (TouchableMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        actionBarSettings();
        setUpFonts();
        mLocationAddress.setSelected(true);

        mAddDestination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SystemClock.elapsedRealtime() - lastClickTime < 1000){
                    return;
                }
                lastClickTime = SystemClock.elapsedRealtime();
                LayoutInflater layoutInflaterAndroid = LayoutInflater.from(DestinationAddMapActivity.this);
                View mView = layoutInflaterAndroid.inflate(R.layout.dialog_user_input, null);
                AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(DestinationAddMapActivity.this);
                alertDialogBuilderUserInput.setView(mView);
                alertDialogBuilderUserInput.setTitle("" + getResources().getString(R.string.app_name));


                final EditText userInputDialogEditText = (EditText) mView.findViewById(R.id.userInputDialog);
                TextView userInputDialogTitle = (TextView) mView.findViewById(R.id.dialogTitle);
                userInputDialogTitle.setText(getResources().getString(R.string.add_destination));
                if(mAreaOutput!=null && mAreaOutput.length()>0)
                userInputDialogEditText.setText(""+mAreaOutput.toString().trim());
                userInputDialogEditText.setHint(getResources().getString(R.string.destination_name));
                alertDialogBuilderUserInput
                        .setCancelable(false)
                        .setPositiveButton("" + getResources().getString(R.string.add), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                if (userInputDialogEditText.getText().toString().length() > 0) {
                                    dialogBox.dismiss();
                                    destinationName=userInputDialogEditText.getText().toString().trim();
                                    scheduleDestination();
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


        });
        mapFragment.getMapAsync(this);
        mResultReceiver = new AddressResultReceiver(new Handler());

        if (checkPlayServices()) {
            // If this check succeeds, proceed with normal processing.
            // Otherwise, prompt user to get valid Play Services APK.
//            if (!(Utils.isLocationServiceEnabled(this))) {
//                Utils.createLocationServiceError(this);
//            }
            buildGoogleApiClient();
        } else {
            Toast.makeText(mContext, "Location not supported in this device", Toast.LENGTH_SHORT).show();
        }
//

    }

    public void scheduleDestination() {
        Calendar now = Calendar.getInstance();

        MaterialDatePickerDialogCustom dpd = MaterialDatePickerDialogCustom.newInstance();
        dpd.initialize(DestinationAddMapActivity.this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH));
        dpd.setMinDate(now);
        dpd.setOnDateScheduledListener(DestinationAddMapActivity.this);
        dpd.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
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
        Calendar now = Calendar.getInstance();


        year = yearVal;
        month = (monthOfYear + 1);
        day = dayOfMonth;
        MaterialTimePickerDialogCustom tpd =  MaterialTimePickerDialogCustom.newInstance();
        tpd.initialize(DestinationAddMapActivity.this,
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),0,
                false);
        tpd.setOnTimeScheduledListener(DestinationAddMapActivity.this);
//        tpd.setThemeDark(true);
//        tpd.vibrate(true);
//        tpd.dismissOnPause(true);
//        tpd.enableSeconds(true);
//        tpd.setCancelText(getString(R.string.schedule_later));
        if((now.get(Calendar.YEAR)==yearVal) && (now.get(Calendar.MONTH)==monthOfYear) && (now.get(Calendar.DAY_OF_MONTH)==dayOfMonth))
        {
            tpd.setMinTime(now.get(Calendar.HOUR_OF_DAY),now.get(Calendar.MINUTE),30);
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
    public void onDateScheduled(MaterialDatePickerDialogCustom view) {
        Log.d("jomy","schedule223...");
        addDestinationRequest(destinationName,"");
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
        Log.d("jomy", "You picked the following Time: " + hourOfDay + "/" + (minute) + "/" + second);
        scheduleDate="" + year + "-" + month + "-" + day + " " + hourOfDay + ":" + minute + ":" + second + ".000";
        addDestinationRequest(destinationName,scheduleDate);
    }

    @Override
    public void onTimeScheduled(MaterialTimePickerDialogCustom view) {
        addDestinationRequest(destinationName,"");
    }

    @Override
    protected void onResume() {
        super.onResume();
        FinDotsApplication.getInstance().trackScreenView("Add New Destination Screen");
    }


    public void setUpFonts()
    {
        Typeface typefaceroboLight= Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf");
        mLocationAddress.setTypeface(typefaceroboLight);
        mLochead.setTypeface(typefaceRoboRegular);
    }

    public void actionBarSettings() {

        /* Assigning the toolbar object ot the view
         * and setting the the Action bar to our toolbar
         */
        mToolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        typefaceRoboRegular = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf");

        mTextView_heading.setText(getString(R.string.new_destination));
        mTextView_heading.setTypeface(typefaceRoboRegular);
        imageView_back.setVisibility(View.VISIBLE);
        imageView_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - lastClickTime < 1000){
                    return;
                }
                lastClickTime = SystemClock.elapsedRealtime();
                onBackPressed();
            }
        });

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "OnMapReady");
        mMap = googleMap;

        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                Log.d("Camera postion change" + "", cameraPosition + "");
                mCenterLatLong = cameraPosition.target;


                mMap.clear();

                try {

                    Log.d("jomy", "mCenterLatLong.latitude : " + mCenterLatLong.latitude);
                    if (mAdressLoc == null)
                        mAdressLoc = new Location("");
                    mAdressLoc.setLatitude(mCenterLatLong.latitude);
                    mAdressLoc.setLongitude(mCenterLatLong.longitude);
                    Log.d("jomy", "mAdressLoc.latitude : " + mAdressLoc.getLatitude());
//                    startIntentService(mAdressLoc);
//                    mLocationMarkerText.setText("Lat : " + mCenterLatLong.latitude + "," + "Long : " + mCenterLatLong.longitude);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        new MapStateListener(mMap, mapFragment, this) {
            @Override
            public void onMapTouched() {
                // Map touched
                Log.d("jomy", "Map Touched..");
            }

            @Override
            public void onMapReleased() {
                // Map released
                Log.d("jomy", "Map Release..");
            }

            @Override
            public void onMapUnsettled() {
                // Map unsettled
                Log.d("jomy", "Map UnSettled..");
            }

            @Override
            public void onMapSettled() {
                Log.d("jomy", "map Seetled ");
                CameraPosition cameraPosition = mMap.getCameraPosition();
                mCenterLatLong = cameraPosition.target;
                mMap.clear();
                if (mAdressLoc == null)
                    mAdressLoc = new Location("");
                mAdressLoc.setLatitude(mCenterLatLong.latitude);
                mAdressLoc.setLongitude(mCenterLatLong.longitude);
                startIntentService(mAdressLoc);
            }
        };
    }


    @Override
    public void onConnected(Bundle bundle) {
        Log.d("jomy", "onConnected243445... ");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            Log.d("jomy", "onConnected... ");
            changeMap(mLastLocation);
            Log.d(TAG, "ON connected");

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
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        try {
            if (location != null)
                changeMap(location);
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

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
        Utils.createLocationServiceChecker(mGoogleApiClient, DestinationAddMapActivity.this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        try {
            mGoogleApiClient.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        try {
            if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                mGoogleApiClient.disconnect();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                //finish();
            }
            return false;
        }
        return true;
    }

    private void changeMap(Location location) {

        Log.d(TAG, "Reaching map" + mMap);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //    TODO: Consider calling
            //    ActivityCompat#requestPermissions
            //    here to request the missing permissions, and then overriding
            //    public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            //    To handle the case where the user grants the permission. See the documentation
            //    For ActivityCompat#requestPermissions for more details.
            return;
        }

        // check if map is created successfully or not
        if (mMap != null) {
            mMap.getUiSettings().setZoomControlsEnabled(false);
            LatLng latLong;


            latLong = new LatLng(location.getLatitude(), location.getLongitude());
            Log.d("jomy", "Auto  Complete...Lang : " + latLong.latitude);

            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
//            mMap.setPadding(0, 200, 0, 0);
            View locationButton = mapFragment.getView().findViewById(Integer.parseInt("2"));
            RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
//            rlp.setMargins(0,0,0,200);
            rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            rlp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);


            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to location user
                    .zoom(15)                   // Sets the zoom
//                    .bearing(180)                // Sets the orientation of the camera to east
                    .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            startIntentService(location);
            mLocationMarkerText.setText("Lat : " + location.getLatitude() + "," + "Long : " + location.getLongitude());
            startIntentService(location);
        } else {
            Toast.makeText(getApplicationContext(),
                    "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                    .show();
        }

    }




    /**
     * Receiver for data sent from FetchAddressIntentService.
     */
    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        /**
         * Receives data sent from FetchAddressIntentService and updates the UI in MainActivity.
         */
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            Log.d("jomy", "adress hit..");
            // Display the address string or an error message sent from the intent service.
            mAddressOutput = resultData.getString(Constants.RESULT_DATA_KEY);

            Log.d("jomy","adree"+mAreaOutput);
            mAreaOutput = resultData.getString(Constants.LOCATION_DATA_AREA);
            mCityOutput = resultData.getString(Constants.LOCATION_DATA_CITY);
            mStateOutput = resultData.getString(Constants.LOCATION_DATA_STREET);

            displayAddressOutput();

            // Show a toast message if an address was found.
            if (resultCode == Constants.SUCCESS_RESULT) {
                //  showToast(getString(R.string.address_found));
            }
        }
    }

    /**
     * Updates the address in the UI.
     */
    protected void displayAddressOutput() {
        //  mLocationAddressTextView.setText(mAddressOutput);
        try {
            if (mAddressOutput != null || mAddressOutput.trim().length() > 0) {
                // mLocationText.setText(mAreaOutput+ "");
                Log.d("jomy", "mAddressOutput.." + mAddressOutput);

                mLocationAddress.setText(mAddressOutput);
                //mLocationText.setText(mAreaOutput);

            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("jomy", "crasjhh");
        }
    }

    /**
     * Creates an intent, adds location data to it as an extra, and starts the intent service for
     * fetching an address.
     */
    protected void startIntentService(Location mLocation) {
        // Create an intent for passing to the intent service responsible for fetching the address.
        Intent intent = new Intent(this, FetchAddressIntentService.class);

        // Pass the result receiver as an extra to the service.
        intent.putExtra(Constants.RECEIVER, mResultReceiver);

        // Pass the location data as an extra to the service.
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, mLocation);
        Log.d("jomy", " startIntentService : " + mLocation.getLatitude() + "  Longitude : " + mLocation.getLongitude());

        // Start the service. If the service isn't already running, it is instantiated and started
        // (creating a process for it if needed); if it is running then it remains running. The
        // service kills itself automatically once all intents are processed.
        startService(intent);
    }


    private void openAutocompleteActivity() {
        Log.d("jomy", "openAutocompleteActivity()");
        try {
            // The autocomplete activity requires Google Play Services to be available. The intent
            // builder checks this and throws an exception if it is not the case.
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                    .build(this);
            startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE);
        } catch (GooglePlayServicesRepairableException e) {
            // Indicates that Google Play Services is either not installed or not up to date. Prompt
            // the user to correct the issue.
            GoogleApiAvailability.getInstance().getErrorDialog(this, e.getConnectionStatusCode(),
                    0 /* requestCode */).show();
        } catch (GooglePlayServicesNotAvailableException e) {
            // Indicates that Google Play Services is not available and the problem is not easily
            // resolvable.
            String message = "Google Play Services is not available: " +
                    GoogleApiAvailability.getInstance().getErrorString(e.errorCode);

            Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Called after the autocomplete activity has finished to return its result.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check that the result was from the autocomplete widget.
        if (requestCode == REQUEST_CODE_AUTOCOMPLETE) {
            Log.d("jomy", "Auto  Complete...2");
            if (resultCode == RESULT_OK) {
                // Get the user's selected place from the Intent.
                Place place = PlaceAutocomplete.getPlace(mContext, data);

                // TODO call location based filter

//                Location targetLocation = new Location("");
                if (mAdressLoc == null)
                    mAdressLoc = new Location("");
                mAdressLoc.setLatitude(place.getLatLng().latitude);
                mAdressLoc.setLongitude(place.getLatLng().longitude);
                CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(mAdressLoc.getLatitude(), mAdressLoc.getLongitude()));
                CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);
                mMap.moveCamera(center);
                mMap.animateCamera(zoom);

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
//                mMap.setMyLocationEnabled(true);
//                mMap.animateCamera(CameraUpdateFactory
//                        .newCameraPosition(cameraPosition));


            }


        } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
            Status status = PlaceAutocomplete.getStatus(mContext, data);
            Log.d("jomy", "Auto  Complete...4");
        } else if (resultCode == RESULT_CANCELED) {
            Log.d("jomy", "Auto  Complete...5");
            // Indicates that the activity closed before a selection was made. For example if
            // the user pressed the back button.
        }
    }

    public void addDestinationRequest(String destinationName,String scheduleTime) {

        FinDotsApplication.getInstance().trackEvent("ADD Destination", "Click", "Submitted Add Destination Request Event");

        GeneralUtils.initialize_progressbar(this);
        Call<ResponseModel> addDestinationCall = FinDotsApplication.getRestClient().getApiService().addDestination(setAddDestinationRequest(destinationName, scheduleTime));

        addDestinationCall.enqueue(new Callback<ResponseModel>() {


            @Override
            public void onResponse(Response<ResponseModel> response, Retrofit retrofit) {
                GeneralUtils.stop_progressbar();
                if (response.body() != null) {
                    if (response.isSuccess() && response.body().getErrorCode() == 0) {
                        Toast.makeText(DestinationAddMapActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        finish();
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("result", "success");
                        setResult(Activity.RESULT_OK, returnIntent);
                        finish();
                    } else
                        Toast.makeText(DestinationAddMapActivity.this, getResources().getString(R.string.add_destinationError), Toast.LENGTH_SHORT).show();

                } else {
                    Toast toast = Toast.makeText(DestinationAddMapActivity.this, getString(R.string.data_error), Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.BOTTOM, 0, 0);
                    toast.show();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                GeneralUtils.stop_progressbar();
                if (NetworkChangeReceiver.isNetworkAvailable(DestinationAddMapActivity.this))
                    Toast.makeText(DestinationAddMapActivity.this, getResources().getString(R.string.add_destinationError), Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(DestinationAddMapActivity.this, getResources().getString(R.string.noInternet), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private Map<String, Object> setAddDestinationRequest(String destinationName,String scheduleTime) {
        Map<String, Object> postValues = new HashMap<>();
        postValues.put("destinationName", "" + destinationName);
        postValues.put("ScheduleDate", "" + TimeSettings.dateTimeInUTC(scheduleTime));
        postValues.put("latitude", mAdressLoc.getLatitude());
        postValues.put("longitude", mAdressLoc.getLongitude());
        postValues.put("address", "" + mAddressOutput);
        postValues.put("appVersion", GeneralUtils.getAppVersion(this));
        postValues.put("deviceTypeID", Constants.DEVICETYPEID);
        postValues.put("deviceID", GeneralUtils.getUniqueDeviceId(this));
        postValues.put("deviceInfo", GeneralUtils.getDeviceInfo());
        postValues.put("userID", GeneralUtils.getSharedPreferenceInt(this, AppStringConstants.USERID));
        postValues.put("ipAddress", "");

        return postValues;
    }

    public void openAutoCompletePlace(View view) {
        openAutocompleteActivity();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
