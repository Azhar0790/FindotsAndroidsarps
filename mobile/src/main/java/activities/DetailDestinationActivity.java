package activities;

import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
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
import com.google.android.gms.maps.model.MarkerOptions;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import findots.bridgetree.com.findots.R;
import utils.GeneralUtils;

/**
 * Created by parijathar on 6/21/2016.
 */
public class DetailDestinationActivity extends AppCompatActivity implements
        OnMapReadyCallback,
        LocationListener,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks{

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

    Toolbar mToolbar = null;

    GoogleApiClient googleApiClient = null;
    GoogleMap mGoogleMap = null;
    Circle mCircle = null;
    LatLng latLng = null;
    LocationRequest mLocationRequest = null;
    Location currentLocation = null;

    private static final long INTERVAL = 1000 * 10;
    private static final long FASTEST_INTERVAL = 1000 * 5;

    Bundle bundle = null;

    int assignDestinationID = 0;
    double destinationLatitude = 0, destinationLongitude = 0, checkInRadius = 0;
    boolean checkedIn = false, checkedOut = false;
    String address = null, destinationName = null, checkedOutReportedDate = null;

    public static final int STROKE_WIDTH = 6;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_destination);

        if (!isGooglePlayServicesAvailable()) {
            finish();
        }

        ButterKnife.bind(this);

        actionBarSettings();

        getBundleData();

        setData();

        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);

        mTextView_address.setMovementMethod(new ScrollingMovementMethod());

        createLocationRequest();
        setGoogleAPiClient();
    }


    /**
     *   get Bundle data
     */
    public void getBundleData() {
        bundle = getIntent().getExtras();
        address = bundle.getString("address");
        checkedIn = bundle.getBoolean("checkedIn");
        checkedOut = bundle.getBoolean("checkedOut");
        destinationName = bundle.getString("destinationName");
        assignDestinationID = bundle.getInt("assignDestinationID");
        destinationLatitude = bundle.getDouble("destinationLatitude");
        destinationLongitude = bundle.getDouble("destinationLongitude");
        checkedOutReportedDate = bundle.getString("checkedOutReportedDate");
        checkInRadius = bundle.getDouble("checkInRadius");
    }

    /**
     *  set the DATa
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
            mButton_checkIncheckOut.setText(getString(R.string.checkedout_at)+" "+ GeneralUtils.getCheckedOutTime(checkedOutReportedDate));
            mButton_checkIncheckOut.setCompoundDrawables(
                    GeneralUtils.scaleDrawable(getResources().getDrawable(R.drawable.checkedout_tick), 40, 40),
                    null, null, null);
            mButton_checkIncheckOut.setTextColor(Color.WHITE);
        }
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
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

        googleMapSettings();
    }

    public void googleMapSettings() {
        mGoogleMap.setMyLocationEnabled(true);
        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
        mGoogleMap.getUiSettings().setCompassEnabled(true);
        mGoogleMap.getUiSettings().setZoomGesturesEnabled(true);
        mGoogleMap.getUiSettings().setRotateGesturesEnabled(true);
        mCircle = mGoogleMap.addCircle(drawCircleOnMap());
    }

    public CircleOptions drawCircleOnMap() {
        return new CircleOptions()
        .center(latLng)
        .radius(500)
        .strokeColor(getResources().getColor(R.color.app_color))
        .fillColor(getResources().getColor(R.color.app_color_25))
        .strokeWidth(STROKE_WIDTH);
    }

    public void setGoogleAPiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
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

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        googleApiClient.disconnect();
    }

    public boolean isGooglePlayServicesAvailable() {
        int status = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(DetailDestinationActivity.this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            Toast.makeText(DetailDestinationActivity.this, "GooglePlayServices not available.", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    public void startLocationUpdates() {
        PendingResult<Status> pendingResult = LocationServices
                .FusedLocationApi.requestLocationUpdates(googleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        startLocationUpdates();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(DetailDestinationActivity.this, "ConnectionFailed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(DetailDestinationActivity.this, "ConnectionSuspended", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        currentLocation = location;
        double lat = currentLocation.getLatitude();
        double lng = currentLocation.getLongitude();
        Toast.makeText(DetailDestinationActivity.this, lat+" "+lng, Toast.LENGTH_SHORT).show();
    }
}
