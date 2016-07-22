package com.knowall.findots.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.knowall.findots.Constants;
import com.knowall.findots.FinDotsApplication;
import com.knowall.findots.R;
import com.knowall.findots.activities.DetailDestinationActivity;
import com.knowall.findots.database.DataHelper;
import com.knowall.findots.distancematrix.DistanceMatrixService;
import com.knowall.findots.distancematrix.IDistanceMatrix;
import com.knowall.findots.distancematrix.model.DistanceMatrix;
import com.knowall.findots.distancematrix.model.Duration;
import com.knowall.findots.distancematrix.model.Elements;
import com.knowall.findots.distancematrix.model.Rows;
import com.knowall.findots.events.AppEvents;
import com.knowall.findots.locationUtils.LocationModel.LocationData;
import com.knowall.findots.locationUtils.Utils;
import com.knowall.findots.restcalls.destinations.DestinationData;
import com.knowall.findots.restcalls.destinations.DestinationsModel;
import com.knowall.findots.restcalls.destinations.GetDestinationsRestCall;
import com.knowall.findots.restcalls.destinations.IGetDestinations;
import com.knowall.findots.restservice.RestClient;
import com.knowall.findots.utils.GeneralUtils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import de.greenrobot.event.EventBus;

/**
 * Created by parijathar on 7/4/2016.
 */
public class DestinationsMapFragment extends Fragment
        implements
        OnMapReadyCallback,
        IGetDestinations,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks,
        LocationListener {

    GoogleMap mGoogleMap = null;
    GoogleApiClient mGoogleApiClient = null;

    double currentLatitude, currentLongitude;
    private static final int REQUEST_CODE_ACTIVITYDETAILS = 1;

    float mapKMTextSize = 24, kmTextSize = 15;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.destinations_map, null);

        EventBus.getDefault().register(this);

        if (GeneralUtils.checkPlayServices(getActivity())) {
            // If this check succeeds, proceed with normal processing.
            // Otherwise, prompt user to get valid Play Services APK.
            if (!(Utils.isLocationServiceEnabled(getActivity()))) {
                Utils.createLocationServiceError(getActivity());
            }
            buildGoogleApiClient();
        } else {
            Toast.makeText(getActivity(), "Location not supported in this device", Toast.LENGTH_SHORT).show();
        }

        SupportMapFragment supportMapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(DestinationsMapFragment.this);

        return rootView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        googleMapSettings();
        fetchCurrentLocation();
        addAllDestinationsOnMap();
        showAllMarkers();
    }

    ArrayList<DestinationData> arrayList = new ArrayList<>();
    /**
     * adding all the destinations to the map
     */
    public void addAllDestinationsOnMap() {

        if (mGoogleMap != null)
            mGoogleMap.clear();

        if (DestinationsTabFragment.destinationDatas != null) {

            arrayList.clear();

            for (DestinationData data : DestinationsTabFragment.destinationDatas) {
                if (data.getScheduleDate().length() != 0 && data.isScheduleDisplayStatus()) {

                    arrayList.add(data);
                    LatLng latLng = new LatLng(data.getDestinationLatitude(), data.getDestinationLongitude());

                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.title(data.getDestinationName());
                    markerOptions.position(latLng);
                    markerOptions.icon(BitmapDescriptorFactory.fromBitmap(drawTravelTimeOnMapMarker(data.isCheckedIn(), data.isCheckedOut(), data.getDestinationLatitude(), data.getDestinationLongitude())));

                    mGoogleMap.addMarker(markerOptions).showInfoWindow();
                }
            }
        }

    }

    /**
     * shows all the markers on map
     */
    public void showAllMarkers() {
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = 150;

        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(getCenterCoordinates(),
                /*(width - 300), (height - 300),*/ padding));

        mGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                if (DestinationsTabFragment.destinationDatas != null) {

                    boolean foundDestination = false;
                    /*for (DestinationData data : DestinationsTabFragment.destinationDatas) {

                        if (data.getScheduleDate().length() != 0 && data.isScheduleDisplayStatus()) {
                            if (data.getDestinationName().equals(marker.getTitle())) {
                                if (data.getDestinationLatitude() == marker.getPosition().latitude &&
                                        data.getDestinationLongitude() == marker.getPosition().longitude) {
                                    foundDestination = true;
                                    break;
                                }
                            }
                        }
                        increment++;
                    }*/

                    int increment = 0;
                    if (arrayList != null) {
                        for (DestinationData data : arrayList) {
                            if (data.getDestinationName().equals(marker.getTitle())) {
                                if (data.getDestinationLatitude() == marker.getPosition().latitude &&
                                        data.getDestinationLongitude() == marker.getPosition().longitude) {
                                    foundDestination = true;
                                    break;
                                }
                            }
                            increment++;
                        }
                    }

                    if (foundDestination)
                        callDetailDestinationActivity(increment);
                }
            }
        });

    }

    /**
     * calls DetailDestinationActivity
     *
     * @param itemPosition position from the destination list
     */
    public void callDetailDestinationActivity(int itemPosition) {
        String address = arrayList.get(itemPosition).getAddress();
        boolean checkedIn = arrayList.get(itemPosition).isCheckedIn();
        boolean checkedOut = arrayList.get(itemPosition).isCheckedOut();
        int checkInRadius = arrayList.get(itemPosition).getCheckInRadius();
        String destinationName = arrayList.get(itemPosition).getDestinationName();
        int assignDestinationID = arrayList.get(itemPosition).getAssignDestinationID();
        int destinationID = arrayList.get(itemPosition).getDestinationID();
        double destinationLatitude = arrayList.get(itemPosition).getDestinationLatitude();
        double destinationLongitude = arrayList.get(itemPosition).getDestinationLongitude();
        String checkedOutReportedDate = arrayList.get(itemPosition).getCheckedOutReportedDate();
        boolean isEditable = arrayList.get(itemPosition).isEditable();
        boolean isRequireApproval = arrayList.get(itemPosition).isRequiresApproval();

        String scheduleDate = arrayList.get(itemPosition).getScheduleDate();

        Intent intentDetailDestination = new Intent(getContext(), DetailDestinationActivity.class);
        intentDetailDestination.putExtra("address", address);
        intentDetailDestination.putExtra("checkedIn", checkedIn);
        intentDetailDestination.putExtra("checkedOut", checkedOut);
        intentDetailDestination.putExtra("destinationName", destinationName);
        intentDetailDestination.putExtra("destinationName", destinationName);
        intentDetailDestination.putExtra("destinationID", destinationID);
        intentDetailDestination.putExtra("assignDestinationID", assignDestinationID);
        intentDetailDestination.putExtra("destinationLatitude", destinationLatitude);
        intentDetailDestination.putExtra("destinationLongitude", destinationLongitude);
        intentDetailDestination.putExtra("checkedOutReportedDate", checkedOutReportedDate);
        intentDetailDestination.putExtra("checkInRadius", checkInRadius);
        intentDetailDestination.putExtra("editable", isEditable);
        intentDetailDestination.putExtra("requireApproval", isRequireApproval);
        intentDetailDestination.putExtra("scheduleDate", scheduleDate);
        startActivityForResult(intentDetailDestination, REQUEST_CODE_ACTIVITYDETAILS);
    }

    /**
     * creating bounds to zoomToFit all the destinations
     */
    public LatLngBounds getCenterCoordinates() {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        if (DestinationsTabFragment.destinationDatas != null) {
            for (DestinationData data : DestinationsTabFragment.destinationDatas) {
                if (data.getScheduleDate().length() != 0 && data.isScheduleDisplayStatus()) {
                    builder.include(new LatLng(data.getDestinationLatitude(), data.getDestinationLongitude()));
                }
            }
        }

        builder.include(new LatLng(currentLatitude, currentLongitude));

        return builder.build();
    }

    /**
     * google map settings
     */
    public void googleMapSettings() {
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mGoogleMap.setMyLocationEnabled(true);
        mGoogleMap.getUiSettings().setCompassEnabled(true);
        mGoogleMap.getUiSettings().setZoomGesturesEnabled(true);
        mGoogleMap.getUiSettings().setRotateGesturesEnabled(true);
    }

    /**
     * fetches current location
     */
    public void fetchCurrentLocation() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        Location location = null;

        for (String provider : providers) {
            Location currentLocation = locationManager.getLastKnownLocation(provider);
            if (currentLocation == null)
                continue;

            if (location == null || currentLocation.getAccuracy() < location.getAccuracy()) {
                location = currentLocation;
            }
        }

        if (location != null) {
            currentLatitude = location.getLatitude();
            currentLongitude = location.getLongitude();
            Log.i(Constants.TAG, "fetchCurrentLocation: location manager = " + currentLatitude + "," + currentLongitude);
        } else {
            /**
             *   call Google api client to fetch current location
             */
            try {
                mGoogleApiClient.connect();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        Log.i(Constants.TAG, "fetchCurrentLocation: lat and lng = " + currentLatitude + ", " + currentLongitude);
    }

    /**
     * builds Google Api client
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(DestinationsMapFragment.this)
                .addOnConnectionFailedListener(DestinationsMapFragment.this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
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
    public void onLocationChanged(Location location) {
        if (location != null) {
            currentLatitude = location.getLatitude();
            currentLongitude = location.getLongitude();
            try {
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }


    public void onEvent(AppEvents events) {
        switch (events) {
            case REFRESHDESTINATIONS:
                EventBus.getDefault().cancelEventDelivery(events);
                EventBus.getDefault().unregister(this);

                Log.i(Constants.TAG, "REFRESHDESTINATIONS");
                fetchCurrentLocation();
                addAllDestinationsOnMap();
                showAllMarkers();

                if (!EventBus.getDefault().isRegistered(this))
                    EventBus.getDefault().register(this);
                break;

            case SCHEDULEDDATE:
                EventBus.getDefault().cancelEventDelivery(events);
                EventBus.getDefault().unregister(this);

                Log.i(Constants.TAG, "REFRESHDESTINATIONS");
                fetchCurrentLocation();
                addAllDestinationsOnMap();
                showAllMarkers();

                if (!EventBus.getDefault().isRegistered(this))
                    EventBus.getDefault().register(this);
                break;
        }
    }

    public Bitmap drawTravelTimeOnMapMarker(boolean isCheckIn, boolean isCheckOut, double destinationLatitude, double destinationLongitude) {

        float[] distance = new float[2];
        Location.distanceBetween(currentLatitude, currentLongitude, destinationLatitude, destinationLongitude, distance);

        String kilometers = new DecimalFormat("##.##").format(distance[0] / 1000);

        Bitmap bm;

        /**
         *   if isCheckIn is false, then display checkin map marker
         *   if it is true, then display checkout map marker
         */
        if (!isCheckIn) {
            // CheckIn
            bm = BitmapFactory.decodeResource(getResources(),
                    R.drawable.map_marker_plain).copy(Bitmap.Config.ARGB_8888, true);
        } else if (!isCheckOut) {
            // CheckOut
            bm = BitmapFactory.decodeResource(getResources(),
                    R.drawable.map_marker_green).copy(Bitmap.Config.ARGB_8888, true);
        } else {
            // CheckOut
            bm = BitmapFactory.decodeResource(getResources(),
                    R.drawable.map_marker_blue).copy(Bitmap.Config.ARGB_8888, true);
        }

        Canvas canvas = new Canvas(bm);

        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Bold.ttf"));
        paint.setTextAlign(Paint.Align.CENTER);

        if (kilometers.length() <= 4) {
            // paint defines the text color, stroke width, size
            paint.setTextSize(mapKMTextSize);
            canvas.drawText(kilometers, (bm.getWidth() / 2), bm.getHeight() / 2 - 10, paint);

            paint.setTextSize(kmTextSize);
            canvas.drawText("KM", (bm.getWidth() / 2), (bm.getHeight() / 2) + 10, paint);
        } else {
            // paint defines the text color, stroke width, size
            mapKMTextSize = 18;
            paint.setTextSize(mapKMTextSize);
            canvas.drawText(kilometers, (bm.getWidth() / 2), bm.getHeight() / 2 - 10, paint);

            paint.setTextSize(kmTextSize);
            canvas.drawText("KM", (bm.getWidth() / 2), (bm.getHeight() / 2) + 10, paint);
        }

        BitmapDrawable draw = new BitmapDrawable(getResources(), bm);
        return draw.getBitmap();
    }


    /**
     * Called after the autocomplete activity has finished to return its result.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.i(Constants.TAG, "onActivityResult..//");
        // Check that the result was from the autocomplete widget.
        if (requestCode == REQUEST_CODE_ACTIVITYDETAILS) {

            if (resultCode == getActivity().RESULT_OK && (data.getStringExtra("result").equals("success") ||
                    data.getStringExtra("result").equals("renamedDestination") ||
                    data.getStringExtra("result").equals("deletedDestination"))) {

                Log.i(Constants.TAG, "onActivityResult..//  GetDestinationsRestCall");
                GetDestinationsRestCall destinationsRestCall = new GetDestinationsRestCall(getActivity());
                destinationsRestCall.delegate = DestinationsMapFragment.this;
                destinationsRestCall.callGetDestinations();

            } else if (resultCode == 3) {
                Log.d("jomy", "check...");
                GetDestinationsRestCall destinationsRestCall = new GetDestinationsRestCall(getActivity());
                destinationsRestCall.delegate = DestinationsMapFragment.this;
                destinationsRestCall.callGetDestinations();
            } else {
                Log.i(Constants.TAG, "onActivityResult..//  else block..");
            }
        } else {
            Log.i(Constants.TAG, "onActivityResult..//  else block");
        }
    }

    @Override
    public void onGetDestinationSuccess(DestinationsModel destinationsModel) {
        DestinationsTabFragment.destinationDatas = destinationsModel.getDestinationData();
        fetchCurrentLocation();
        addAllDestinationsOnMap();
        showAllMarkers();
    }

    @Override
    public void onGetDestinationFailure(String errorMessage) {
        Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                mGoogleApiClient.disconnect();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
