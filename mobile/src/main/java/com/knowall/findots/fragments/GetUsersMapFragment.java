package com.knowall.findots.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.knowall.findots.R;
import com.knowall.findots.activities.MenuActivity;
import com.knowall.findots.restcalls.destinations.DestinationData;
import com.knowall.findots.restcalls.getUser.GetUserData;

import java.util.Arrays;
import java.util.List;

/**
 * Created by parijathar on 8/10/2016.
 */
public class GetUsersMapFragment extends Fragment implements OnMapReadyCallback {

    GoogleMap mGoogleMap = null;
    SupportMapFragment supportMapFragment;
    private static GetUserData[] userDatas = null;

    public static GetUsersMapFragment newInstance(GetUserData[] getUserDatas) {
        userDatas = getUserDatas;
        GetUsersMapFragment getUsersMapFragment = new GetUsersMapFragment();
        return getUsersMapFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.destinations_map, null);
        supportMapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(GetUsersMapFragment.this);
        return rootView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        googleMapSettings();
        addAllUsersOnMap();
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
     * creating bounds to zoomToFit all the destinations
     */
    public LatLngBounds getCenterCoordinates() {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        if (userDatas != null) {
            for (GetUserData getUserData : userDatas) {
                builder.include(new LatLng(getUserData.getLatitude(), getUserData.getLongitude()));
            }
        }

        /**
         *  fetch current lat and lng
         */
        //builder.include(new LatLng(currentLatitude, currentLongitude));

        return builder.build();
    }


    private void addAllUsersOnMap() {
        if (mGoogleMap != null)
            mGoogleMap.clear();

        for (GetUserData getUserData: userDatas) {
            LatLng latLng = new LatLng(getUserData.getLatitude(), getUserData.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.title(getUserData.getName());
            markerOptions.position(latLng);
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(drawUserName(getUserData.getName())));
            mGoogleMap.addMarker(markerOptions);
        }

        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(getCenterCoordinates(), 150));
    }

    private Bitmap drawUserName(String name) {
        Bitmap bitmap = BitmapFactory.decodeResource(MenuActivity.ContextMenuActivity.getResources(),
                R.drawable.map_marker_plain).copy(Bitmap.Config.ARGB_8888, true);

        Canvas canvas = new Canvas(bitmap);

        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTypeface(Typeface.createFromAsset(
                MenuActivity.ContextMenuActivity.getAssets(), "fonts/Roboto-Light.ttf"));
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(28f);

        /**
         *   create name to draw on marker
         */
        List<String> names = Arrays.asList(name.split(" "));
        StringBuilder userName = new StringBuilder();

        int size = names.size();

        if (size > 1) {
            if (names.get(0).length() > 1)
                userName.append(names.get(0).charAt(0));

            if (names.get(1).length() > 1)
                userName.append(names.get(1).charAt(0));
        } else if (size == 1) {
            if (names.get(0).length() > 1)
                userName.append(names.get(0).substring(0, 2));
        }


        canvas.drawText(userName.toString(), (bitmap.getWidth() / 2), (bitmap.getHeight() / 2), paint);

        BitmapDrawable draw = new BitmapDrawable(getActivity().getResources(), bitmap);

        return draw.getBitmap();
    }

}
