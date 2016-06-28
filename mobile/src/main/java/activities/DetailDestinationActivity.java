package activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.method.ScrollingMovementMethod;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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

import java.text.DecimalFormat;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import findots.bridgetree.com.findots.R;
import restcalls.checkInCheckOut.CheckInCheckOutRestCall;
import restcalls.checkInCheckOut.ICheckInCheckOut;
import restcalls.destinations.DestinationData;
import restcalls.destinations.DestinationsModel;
import restcalls.destinations.GetDestinationsRestCall;
import restcalls.destinations.IGetDestinations;
import utils.GeneralUtils;

/**
 * Created by parijathar on 6/21/2016.
 */
public class DetailDestinationActivity extends AppCompatActivity implements
        OnMapReadyCallback, ICheckInCheckOut, IGetDestinations {

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

    Toolbar mToolbar = null;

    GoogleMap mGoogleMap = null;
    Circle mCircle = null;
    LatLng latLng = null;

    Bundle bundle = null;

    int assignDestinationID = 0,destinationID=0;
    double destinationLatitude = 0, destinationLongitude = 0, checkInRadius = 0;
    boolean checkedIn = false, checkedOut = false, isEditable = false, isRequiresApproval = false;
    String address = null, destinationName = null, checkedOutReportedDate = null;

    public static final int STROKE_WIDTH = 6;
    private static final int REQUEST_CODE_MODIFY_DESTINATION = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_destination);

        ButterKnife.bind(this);

        actionBarSettings();

        getBundleData();

        setData();

        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);

        mTextView_address.setMovementMethod(new ScrollingMovementMethod());
        if (isEditable && !(checkedIn || checkedOut)) {
            SpannableString mDestModifyText = new SpannableString(getResources().getString(R.string.modify_destination));
            mDestModifyText.setSpan(new UnderlineSpan(), 0, mDestModifyText.length(), 0);
            mDestModify.setText(mDestModifyText);
            mDestModify.setVisibility(View.VISIBLE);
        }

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
        destinationID= bundle.getInt("destinationID");
        destinationLatitude = bundle.getDouble("destinationLatitude");
        destinationLongitude = bundle.getDouble("destinationLongitude");
        checkedOutReportedDate = bundle.getString("checkedOutReportedDate");
        checkInRadius = bundle.getDouble("checkInRadius");
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
                isDeviceEnteredWithinDestinationRadius(true);
            }
        });

        mLinearLayout_checkIncheckOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isDeviceEnteredWithinDestinationRadius(true);
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
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        double currentLatitude, currentLongitude;
        try {
            currentLatitude = location.getLatitude();
            currentLongitude = location.getLongitude();
        } catch (Exception e) {
            currentLatitude = 0.0;
            currentLongitude = 0.0;
        }

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
        isDeviceEnteredWithinDestinationRadius(false);
    }

    public CircleOptions drawCircleOnMap() {
        return new CircleOptions()
                .center(latLng)
                .radius(500)
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

        Typeface typefaceMyriadHebrew = Typeface.createFromAsset(getAssets(), "fonts/MyriadHebrew-Bold.otf");

        mTextView_heading.setText(getString(R.string.destinations));
        mTextView_heading.setTypeface(typefaceMyriadHebrew);
    }

    @OnClick(R.id.imageView_back)
    public void goBack() {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void isDeviceEnteredWithinDestinationRadius(boolean requestForCheckInCheckOut) {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();

        float[] distance = new float[2];

        Location.distanceBetween(currentLatitude, currentLongitude, destinationLatitude, destinationLongitude, distance);

        if (distance[0] > mCircle.getRadius()) {
            /**
             *   Outside the Radius
             */
            String kiloMeters = new DecimalFormat("0").format(distance[0] / 1000);
            mTextView_map_km.setText(kiloMeters + " kms");

            if (requestForCheckInCheckOut) {
                GeneralUtils.createAlertDialog(DetailDestinationActivity.this,
                        "You haven't reach the destination.");
            }
        } else {
            /**
             *   Inside the Radius
             */
            String kiloMeters = new DecimalFormat("0").format(distance[0] / 1000);
            mTextView_map_km.setText(kiloMeters + " kms");

            if (requestForCheckInCheckOut) {
                CheckInCheckOutRestCall restCall = new CheckInCheckOutRestCall(DetailDestinationActivity.this);
                restCall.delegate = DetailDestinationActivity.this;
                restCall.callCheckInService(checkedIn, assignDestinationID);
            }
        }
    }


    @Override
    public void onCheckInFailure(String status) {
        GeneralUtils.createAlertDialog(DetailDestinationActivity.this, status);
    }

    @Override
    public void onCheckInSuccess() {
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

        if(data!=null) {
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

            Intent intentModifyLoc = new Intent(this, DestinationModify_MapActivity.class);
            intentModifyLoc.putExtra("destinationID", destinationID);
            intentModifyLoc.putExtra("destinationLatitude", destinationLatitude);
            intentModifyLoc.putExtra("destinationLongitude", destinationLongitude);
            startActivityForResult(intentModifyLoc,REQUEST_CODE_MODIFY_DESTINATION);
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

                if(isRequiresApproval==false)
                {
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("result","success");
                    setResult(Activity.RESULT_OK,returnIntent);
                    finish();
                }
            }
        }
    }
}
