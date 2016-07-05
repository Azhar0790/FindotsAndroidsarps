package fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DecimalFormat;
import java.util.List;

import database.DataHelper;
import de.greenrobot.event.EventBus;
import events.AppEvents;
import findots.bridgetree.com.findots.Constants;
import findots.bridgetree.com.findots.R;
import locationUtils.LocationModel.LocationData;
import restcalls.destinations.DestinationData;

/**
 * Created by parijathar on 7/4/2016.
 */
public class DestinationsMapFragment extends Fragment implements OnMapReadyCallback{

    GoogleMap mGoogleMap = null;
    double currentLatitude = 0.012, currentLongitude = 0.013;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.destinations_map, null);

        EventBus.getDefault().register(this);

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
                //markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker));
                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(drawTravelTimeOnMapMarker(data.getDestinationLatitude(), data.getDestinationLongitude())));

                mGoogleMap.addMarker(markerOptions).showInfoWindow();
            }
        }
    }

    public void showAllMarkers() {
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.10);

        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(getCenterCoordinates(),
                (width - 300), (height - 300), padding));
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

    public Bitmap drawTravelTimeOnMapMarker(double destinationLatitude, double destinationLongitude) {

        float[] distance = new float[2];
        Location.distanceBetween(currentLatitude, currentLongitude, destinationLatitude, destinationLongitude, distance);

        String duration = new DecimalFormat("0").format(distance[0]/43.50);

        Bitmap bm = BitmapFactory.decodeResource(getResources(),
                R.drawable.map_marker_150).copy(Bitmap.Config.ARGB_8888, true);

        Canvas canvas = new Canvas(bm);

        Paint paint = new Paint();
        paint.setColor(Color.WHITE);

        // paint defines the text color, stroke width, size
        paint.setTextSize(50);
        canvas.drawText(duration, (bm.getWidth()/2) - 30, bm.getHeight()/2 - 10, paint);

        paint.setTextSize(30);
        canvas.drawText("min", (bm.getWidth()/2) - 20, bm.getHeight()/2+30, paint);

        BitmapDrawable draw = new BitmapDrawable(getResources(), bm);
        Bitmap drawBmp = draw.getBitmap();
        return drawBmp;
    }
}
