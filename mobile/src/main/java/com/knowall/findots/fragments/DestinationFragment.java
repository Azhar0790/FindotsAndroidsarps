package com.knowall.findots.fragments;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.knowall.findots.Constants;
import com.knowall.findots.R;
import com.knowall.findots.activities.DetailDestinationActivity;
import com.knowall.findots.adapters.DestinationsAdapter;
import com.knowall.findots.database.DataHelper;
import com.knowall.findots.distancematrix.DistanceMatrixService;
import com.knowall.findots.distancematrix.IDistanceMatrix;
import com.knowall.findots.distancematrix.model.DistanceMatrix;
import com.knowall.findots.distancematrix.model.Duration;
import com.knowall.findots.distancematrix.model.Elements;
import com.knowall.findots.distancematrix.model.Rows;
import com.knowall.findots.events.AppEvents;
import com.knowall.findots.interfaces.IDestinations;
import com.knowall.findots.locationUtils.LocationModel.LocationData;
import com.knowall.findots.locationUtils.Utils;
import com.knowall.findots.restcalls.checkInCheckOut.CheckInCheckOutRestCall;
import com.knowall.findots.restcalls.checkInCheckOut.ICheckInCheckOut;
import com.knowall.findots.restcalls.destinations.DestinationData;
import com.knowall.findots.restcalls.destinations.DestinationsModel;
import com.knowall.findots.restcalls.destinations.GetDestinationsRestCall;
import com.knowall.findots.restcalls.destinations.IGetDestinations;
import com.knowall.findots.utils.GeneralUtils;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

/**
 * Created by parijathar on 6/14/2016.
 */
public class DestinationFragment extends Fragment
        implements
        IDestinations,
        IGetDestinations,
        ICheckInCheckOut,
        IDistanceMatrix,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks,
        LocationListener {

    @Bind(R.id.RecyclerView_destinations)
    RecyclerView mRecyclerView_destinations;

    GoogleApiClient mGoogleApiClient = null;

    String travelTime = null;
    LinearLayoutManager layoutManager = null;
    Parcelable listViewState = null;
    private static final int REQUEST_CODE_ACTIVITYDETAILS = 1;
    double currentLatitude = 0.013, currentLongitude = 0.012;

    ArrayList<DestinationData> arrayListDestinations = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.destinations, null);

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

        ButterKnife.bind(this, rootView);

        layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView_destinations.setLayoutManager(layoutManager);

        arrayListDestinations = sortDestinationsOnDate(DestinationsTabFragment.destinationDatas);
        setAdapterForDestinations();

        return rootView;
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

    @Override
    public void onDestinationSelected(int itemPosition) {
        destinationListPosition = itemPosition;

        String address = arrayListDestinations.get(itemPosition).getAddress();
        boolean checkedIn = arrayListDestinations.get(itemPosition).isCheckedIn();
        boolean checkedOut = arrayListDestinations.get(itemPosition).isCheckedOut();
        int checkInRadius = arrayListDestinations.get(itemPosition).getCheckInRadius();
        String destinationName = arrayListDestinations.get(itemPosition).getDestinationName();
        int assignDestinationID = arrayListDestinations.get(itemPosition).getAssignDestinationID();
        int destinationID = arrayListDestinations.get(itemPosition).getDestinationID();
        double destinationLatitude = arrayListDestinations.get(itemPosition).getDestinationLatitude();
        double destinationLongitude = arrayListDestinations.get(itemPosition).getDestinationLongitude();
        String checkedOutReportedDate = arrayListDestinations.get(itemPosition).getCheckedOutReportedDate();
        boolean isEditable = arrayListDestinations.get(itemPosition).isEditable();
        boolean isRequireApproval = arrayListDestinations.get(itemPosition).isRequiresApproval();

        listViewState = layoutManager.onSaveInstanceState();

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

    @Override
    public void onGetDestinationSuccess(DestinationsModel destinationsModel) {
        if (destinationsModel.getErrorCode() == 2) {
            GeneralUtils.createAlertDialog(getActivity(), destinationsModel.getMessage());
        } else if (destinationsModel.getDestinationData() != null) {
            DestinationData[] destinationDatas = destinationsModel.getDestinationData();

            DestinationsTabFragment.destinationDatas = destinationDatas;
            EventBus.getDefault().post(AppEvents.REFRESHDESTINATIONS);

            arrayListDestinations = sortDestinationsOnDate(destinationDatas);
            setAdapterForDestinations();
        }
    }

    public void setAdapterForDestinations() {
        DestinationsAdapter destinationsAdapter = new DestinationsAdapter(getActivity(), arrayListDestinations, travelTime);
        destinationsAdapter.delegate = DestinationFragment.this;
        mRecyclerView_destinations.setAdapter(destinationsAdapter);
        destinationsAdapter.notifyDataSetChanged();
        layoutManager.onRestoreInstanceState(listViewState);

        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }

    /**
     * sort destinations
     *
     * @param destinationDatas
     * @return
     */
    ArrayList<DestinationData> arrayList = new ArrayList<>();
    public ArrayList<DestinationData> sortDestinationsOnDate(DestinationData[] destinationDatas) {

        arrayList = new ArrayList<>();
        arrayList.clear();

        for (DestinationData data : destinationDatas) {
            arrayList.add(data);
        }

        Log.i(Constants.TAG, "before sorting --> " + arrayList.toString());

        Collections.sort(arrayList, new Comparator<DestinationData>() {
            @Override
            public int compare(DestinationData lhs, DestinationData rhs) {

                //DateTimeFormatter dateTimeFormatter = ISODateTimeFormat.dateTimeParser();
                DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS");

                DateTime d1 = dateTimeFormatter.parseDateTime(lhs.getAssigndestinationTime());
                DateTime d2 = dateTimeFormatter.parseDateTime(rhs.getAssigndestinationTime());

                if (d1 == null || d2 == null)
                    return 0;

                return d1.compareTo(d2);
            }
        });

        Collections.reverse(arrayList);
        Log.i(Constants.TAG, "after sorting --> " + arrayList.toString());

        try {
            mGoogleApiClient.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return arrayList;
    }

    public static int destinationListPosition = 0;
    @Override
    public void callCheckInCheckOutService(int destinationPosition, boolean isCheckedIn) {
        listViewState = layoutManager.onSaveInstanceState();
        destinationListPosition = destinationPosition;

        isDeviceEnteredWithinDestinationRadius(arrayListDestinations.get(destinationPosition).getDestinationLatitude(),
                arrayListDestinations.get(destinationPosition).getDestinationLongitude(),
                arrayListDestinations.get(destinationPosition).getCheckInRadius(),
                arrayListDestinations.get(destinationPosition).isCheckedIn(),
                arrayListDestinations.get(destinationPosition).getAssignDestinationID(), isCheckedIn);
    }

    @Override
    public void onGetDestinationFailure(String errorMessage) {
        Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCheckInSuccess() {
        GetDestinationsRestCall destinationsRestCall = new GetDestinationsRestCall(getActivity());
        destinationsRestCall.delegate = DestinationFragment.this;
        destinationsRestCall.callGetDestinations();

    }

    @Override
    public void onCheckInFailure(String status) {
        Toast.makeText(getActivity(), status, Toast.LENGTH_SHORT).show();
        /**
         *   offline checkin checkout, change the status
         */
        offlineCheckInCheckOut(destinationListPosition);
    }


    public void offlineCheckInCheckOut(int position) {
        if (!arrayListDestinations.get(position).isCheckedIn()) {
            arrayListDestinations.get(position).setCheckedIn(true);
        } else if(!arrayListDestinations.get(position).isCheckedOut()) {
            arrayListDestinations.get(position).setCheckedOut(true);

            DateTimeFormatter fmt1 = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS");
            DateTime dateTime = new DateTime();
            String getTime = dateTime.toString(fmt1);

            arrayListDestinations.get(position).setCheckedOutReportedDate(getTime);
        }

        setAdapterForDestinations();
    }


    public void isDeviceEnteredWithinDestinationRadius(double destinationLatitude,
                                                       double destinationLongitude, double checkInRadius,
                                                       boolean checkedIn, int assignDestinationID, boolean isCheckIn) {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        //Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

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
            DataHelper dataHelper = DataHelper.getInstance(getActivity());
            List<LocationData> locationLatestData = dataHelper.getLocationLastRecord();
            if (locationLatestData.size() > 0) {
                for (LocationData locLastData : locationLatestData) {
                    currentLatitude = locLastData.getLatitude();
                    currentLongitude = locLastData.getLongitude();
                }
            }
        }

        /**
         *   if it is true, checkIn is completed
         *   do the checkOUt
         */
        if (isCheckIn) {
            CheckInCheckOutRestCall restCall = new CheckInCheckOutRestCall(getActivity());
            restCall.delegate = DestinationFragment.this;
            restCall.callCheckInService(checkedIn, assignDestinationID, currentLatitude, currentLongitude);
        } else {

            float[] distance = new float[2];

            Location.distanceBetween(currentLatitude, currentLongitude, destinationLatitude, destinationLongitude, distance);

            if (distance[0] > checkInRadius) {
                /**
                 *   Outside the Radius
                 */
                GeneralUtils.createAlertDialog(getActivity(),
                        getActivity().getString(R.string.not_the_right_destination));
            } else {
                /**
                 *   Inside the Radius
                 */
                CheckInCheckOutRestCall restCall = new CheckInCheckOutRestCall(getActivity());
                restCall.delegate = DestinationFragment.this;
                restCall.callCheckInService(checkedIn, assignDestinationID, currentLatitude, currentLongitude);
            }
        }
    }


    /**
     * Called after the autocomplete activity has finished to return its result.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.i(Constants.TAG, "onActivityResult..//");
        // Check that the result was from the autocomplete widget.
        try {
            if (requestCode == REQUEST_CODE_ACTIVITYDETAILS) {
                if (resultCode == getActivity().RESULT_OK && (data.getStringExtra("result").equals("success") ||
                        data.getStringExtra("result").equals("renamedDestination") ||
                        data.getStringExtra("result").equals("deletedDestination"))) {

                    Log.i(Constants.TAG, "onActivityResult..//  GetDestinationsRestCall");
                    GetDestinationsRestCall destinationsRestCall = new GetDestinationsRestCall(getActivity());
                    destinationsRestCall.delegate = DestinationFragment.this;
                    destinationsRestCall.callGetDestinations();
                } else if (resultCode == getActivity().RESULT_CANCELED) {
                    /**
                     *   offline changing the checkin/checkout status
                     *   from the detailDestination activity
                     */
                    if (data.getStringExtra("result").equals("checkedIn")) {
                        arrayListDestinations.get(destinationListPosition).setCheckedIn(true);
                    } else if (data.getStringExtra("result").equals("checkedOut")) {
                        arrayListDestinations.get(destinationListPosition).setCheckedOut(true);
                    } else {
                        String time = data.getStringExtra("result");
                        arrayListDestinations.get(destinationListPosition).setCheckedOutReportedDate(time);
                    }
                    setAdapterForDestinations();
                } else {
                    Log.i(Constants.TAG, "onActivityResult..//  GetDestinationsRestCall - first else block");
                }
            } else {
                Log.i(Constants.TAG, "onActivityResult.we.//  else block");
            }
        }
        catch (Exception e){}
    }


    public void onEvent(AppEvents event) {
        GetDestinationsRestCall destinationsRestCall;

        switch (event) {

            case OFFLINECHECKIN:
                EventBus.getDefault().post(AppEvents.OFFLINECHECKIN);
                EventBus.getDefault().cancelEventDelivery(event);
                EventBus.getDefault().unregister(this);

                Log.d("jomy", "callll checkout22...");
                destinationsRestCall = new GetDestinationsRestCall(getActivity());
                destinationsRestCall.delegate = DestinationFragment.this;
                destinationsRestCall.callGetDestinations();

                break;
            case OFFLINECHECKOUT:
                EventBus.getDefault().post(AppEvents.OFFLINECHECKOUT);
                EventBus.getDefault().cancelEventDelivery(event);
                EventBus.getDefault().unregister(this);
                Log.d("jomy", "callll checkout...");
                destinationsRestCall = new GetDestinationsRestCall(getActivity());
                destinationsRestCall.delegate = DestinationFragment.this;
                destinationsRestCall.callGetDestinations();
                break;
        }
    }

    /**
     *   builds Google Api client
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(DestinationFragment.this)
                .addOnConnectionFailedListener(DestinationFragment.this)
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
            double originLatitude = location.getLatitude();
            double originLongitude = location.getLongitude();

            String origin = originLatitude+","+originLongitude;
            String destination = createDestinations();

            googleDistanceMatrixAPI(origin, destination);
            try{
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

    /**
     *   call DistanceMatrix API to display
     *   duration and distance of the
     *   current location and destination locations on the map markers
     */
    public void googleDistanceMatrixAPI(String origin, String destination) {
        DistanceMatrixService service = new DistanceMatrixService(getActivity());
        service.delegate = DestinationFragment.this;
        service.callDistanceMatrixService(origin, destination);
    }


    @Override
    public void onDistanceMatrixSuccess(DistanceMatrix distanceMatrix) {
        if (distanceMatrix != null) {
            Rows[] rows = distanceMatrix.getRows();
            if (rows.length > 0) {
                Elements[] elements = rows[0].getElements();
                if (elements.length > 0) {
                    Duration duration = elements[0].getDuration();
                    if(duration != null) {
                        travelTime = duration.getText();
                        setAdapterForDestinations();
                        Log.i(Constants.TAG, "onDistanceMatrixSuccess: travelTime = "+travelTime);
                    }
                }
            }
        }
    }

    @Override
    public void onDistanceMatrixFailure() {
        Toast.makeText(getActivity(), "Unable to fetch the travel time.", Toast.LENGTH_SHORT).show();
    }

    /**
     *   create Origins request parameter for DistanceMatrix
     * @return
     */

    public String createDestinations() {
        String destinations = "";
        if (arrayList.size() > 0) {
            destinations = arrayList.get(0).getDestinationLatitude()+","+arrayList.get(0).getDestinationLongitude();
        }
        return destinations;
    }

}
