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
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
import com.knowall.findots.R;
import com.knowall.findots.activities.DetailDestinationActivity;
import com.knowall.findots.database.DataHelper;
import com.knowall.findots.events.AppEvents;
import com.knowall.findots.locationUtils.LocationModel.LocationData;
import com.knowall.findots.restcalls.destinations.DestinationData;
import com.knowall.findots.restcalls.destinations.DestinationsModel;
import com.knowall.findots.restcalls.destinations.GetDestinationsRestCall;
import com.knowall.findots.restcalls.destinations.IGetDestinations;

import java.text.DecimalFormat;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by parijathar on 7/4/2016.
 */
public class DestinationsMapFragment extends Fragment implements OnMapReadyCallback, IGetDestinations{

    GoogleMap mGoogleMap = null;
    double currentLatitude = 0.012, currentLongitude = 0.013;
    private static final int REQUEST_CODE_ACTIVITYDETAILS = 1;

    float mapKMTextSize, kmTextSize;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.destinations_map, null);

        EventBus.getDefault().register(this);

        //mapKMTextSize = getActivity().getResources().getDimension(R.dimen.mapKM_TextSize);
        //kmTextSize = getActivity().getResources().getDimension(R.dimen.kmTextSize);

        mapKMTextSize = 24;
        kmTextSize = 18;

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

    public void addAllDestinationsOnMap() {
        if (DestinationsTabFragment.destinationDatas != null) {
            for (DestinationData data: DestinationsTabFragment.destinationDatas) {
                LatLng latLng = new LatLng(data.getDestinationLatitude(), data.getDestinationLongitude());

                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.title(data.getDestinationName());
                markerOptions.position(latLng);
                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(drawTravelTimeOnMapMarker(data.isCheckedIn(), data.isCheckedOut(), data.getDestinationLatitude(), data.getDestinationLongitude())));

                mGoogleMap.addMarker(markerOptions).showInfoWindow();
            }
        }
    }

    public void showAllMarkers() {
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        //int padding = (int) (width * 0.10);
        int padding = 50;

        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(getCenterCoordinates(),
                (width - 300), (height - 300), padding));

        mGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {

                Log.i(Constants.TAG, "onInfoWindowClick");

                if (DestinationsTabFragment.destinationDatas != null) {
                    int increment = 0; boolean foundDestination = false;
                    for (DestinationData data : DestinationsTabFragment.destinationDatas) {
                        if (data.getDestinationName().equals(marker.getTitle())) {
                            if (data.getDestinationLatitude() == marker.getPosition().latitude &&
                                    data.getDestinationLongitude() == marker.getPosition().longitude) {
                                foundDestination = true;
                                Log.i(Constants.TAG, "onInfoWindowClick: foundDestination");
                                break;
                            }
                        }
                        increment++;
                        Log.i(Constants.TAG, "onInfoWindowClick: increment = "+increment);
                    }

                    if (foundDestination) {
                        Log.i(Constants.TAG, "onInfoWindowClick: callDetailDestinationActivity");
                        callDetailDestinationActivity(increment);
                    }

                }
            }
        });
    }

    public void callDetailDestinationActivity(int itemPosition) {
        String address = DestinationsTabFragment.destinationDatas[itemPosition].getAddress();
        boolean checkedIn = DestinationsTabFragment.destinationDatas[itemPosition].isCheckedIn();
        boolean checkedOut = DestinationsTabFragment.destinationDatas[itemPosition].isCheckedOut();
        int checkInRadius = DestinationsTabFragment.destinationDatas[itemPosition].getCheckInRadius();
        String destinationName = DestinationsTabFragment.destinationDatas[itemPosition].getDestinationName();
        int assignDestinationID = DestinationsTabFragment.destinationDatas[itemPosition].getAssignDestinationID();
        int destinationID = DestinationsTabFragment.destinationDatas[itemPosition].getDestinationID();
        double destinationLatitude = DestinationsTabFragment.destinationDatas[itemPosition].getDestinationLatitude();
        double destinationLongitude = DestinationsTabFragment.destinationDatas[itemPosition].getDestinationLongitude();
        String checkedOutReportedDate = DestinationsTabFragment.destinationDatas[itemPosition].getCheckedOutReportedDate();
        boolean isEditable = DestinationsTabFragment.destinationDatas[itemPosition].isEditable();
        boolean isRequireApproval = DestinationsTabFragment.destinationDatas[itemPosition].isRequiresApproval();

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
        startActivityForResult(intentDetailDestination, REQUEST_CODE_ACTIVITYDETAILS);
    }

    public LatLngBounds getCenterCoordinates() {

        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        if (DestinationsTabFragment.destinationDatas != null) {
            for (DestinationData data : DestinationsTabFragment.destinationDatas) {
                builder.include(new LatLng(data.getDestinationLatitude(), data.getDestinationLongitude()));
            }
        }

        builder.include(new LatLng(currentLatitude, currentLongitude));

        LatLngBounds bounds = builder.build();
        return bounds;
    }

    public void googleMapSettings() {
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mGoogleMap.setMyLocationEnabled(true);
        mGoogleMap.getUiSettings().setCompassEnabled(true);
        mGoogleMap.getUiSettings().setZoomGesturesEnabled(true);
        mGoogleMap.getUiSettings().setRotateGesturesEnabled(true);
    }

    public void fetchCurrentLocation() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        Location location = null;

        for (String provider: providers) {
            Location currentLocation = locationManager.getLastKnownLocation(provider);
            if (currentLocation == null)
                continue;

            if (location == null || currentLocation.getAccuracy() < location.getAccuracy())
                location = currentLocation;
        }

        if (location != null) {
            currentLatitude = location.getLatitude();
            currentLongitude = location.getLongitude();
        }
        else {
            DataHelper dataHelper = DataHelper.getInstance(getActivity());
            List<LocationData> locationLatestData = dataHelper.getLocationLastRecord();
            if (locationLatestData.size() > 0) {
                for (LocationData locLastData : locationLatestData) {
                    currentLatitude = locLastData.getLatitude();
                    currentLongitude = locLastData.getLongitude();
                }
            }
        }
    }

    public void onEvent(AppEvents events) {
        switch (events) {
            case REFRESHDESTINATIONS:
                EventBus.getDefault().cancelEventDelivery(events);
                EventBus.getDefault().unregister(this);

                Log.i(Constants.TAG, "REFRESHDESTINATIONS");
                addAllDestinationsOnMap();
                showAllMarkers();

                EventBus.getDefault().register(this);
                break;
        }
    }

    public Bitmap drawTravelTimeOnMapMarker(boolean isCheckIn, boolean isCheckOut, double destinationLatitude, double destinationLongitude) {

        float[] distance = new float[2];
        Location.distanceBetween(currentLatitude, currentLongitude, destinationLatitude, destinationLongitude, distance);

        String kilometers = new DecimalFormat("0").format(distance[0]/1000);

        Bitmap bm = null;

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
        }
        else {
            // CheckOut
            bm = BitmapFactory.decodeResource(getResources(),
                    R.drawable.map_marker_blue).copy(Bitmap.Config.ARGB_8888, true);
        }

        Canvas canvas = new Canvas(bm);

        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Bold.ttf"));
        paint.setTextAlign(Paint.Align.CENTER);


        // paint defines the text color, stroke width, size
        paint.setTextSize(mapKMTextSize);
        canvas.drawText(kilometers, (bm.getWidth()/2), bm.getHeight()/2 - 10, paint);

        paint.setTextSize(kmTextSize);
        canvas.drawText("KM", (bm.getWidth()/2), (bm.getHeight()/2)+10, paint);


        BitmapDrawable draw = new BitmapDrawable(getResources(), bm);
        Bitmap drawBmp = draw.getBitmap();
        return drawBmp;
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
            if (resultCode == getActivity().RESULT_OK && data.getStringExtra("result").equals("success")) {
                Log.i(Constants.TAG, "onActivityResult..//  GetDestinationsRestCall");
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
        addAllDestinationsOnMap();
        showAllMarkers();
    }

    @Override
    public void onGetDestinationFailure(String errorMessage) {
        Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
    }
}
