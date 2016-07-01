package fragments;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import activities.DetailDestinationActivity;
import adapters.DestinationsAdapter;
import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import events.AppEvents;
import findots.bridgetree.com.findots.Constants;
import findots.bridgetree.com.findots.R;
import interfaces.IDestinations;
import restcalls.checkInCheckOut.CheckInCheckOutRestCall;
import restcalls.checkInCheckOut.ICheckInCheckOut;
import restcalls.destinations.DestinationData;
import restcalls.destinations.DestinationsModel;
import restcalls.destinations.GetDestinationsRestCall;
import restcalls.destinations.IGetDestinations;
import utils.GeneralUtils;

/**
 * Created by parijathar on 6/14/2016.
 */
public class DestinationFragment extends Fragment implements IDestinations, IGetDestinations, ICheckInCheckOut {

    @Bind(R.id.RecyclerView_destinations)
    RecyclerView mRecyclerView_destinations;

    LinearLayoutManager layoutManager = null;
    Parcelable listViewState = null;
    private static final int REQUEST_CODE_ACTIVITYDETAILS = 1;

    public static DestinationFragment newInstance() {
        DestinationFragment destinationFragment = new DestinationFragment();
        return destinationFragment;
    }

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

        ButterKnife.bind(this, rootView);

        layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView_destinations.setLayoutManager(layoutManager);

        GetDestinationsRestCall destinationsRestCall = new GetDestinationsRestCall(getActivity());
        destinationsRestCall.delegate = DestinationFragment.this;
        destinationsRestCall.callGetDestinations();
        return rootView;
    }

    @Override
    public void onDestinationSelected(int itemPosition) {

        String address = destinationDatas[itemPosition].getAddress();
        boolean checkedIn = destinationDatas[itemPosition].isCheckedIn();
        boolean checkedOut = destinationDatas[itemPosition].isCheckedOut();
        int checkInRadius = destinationDatas[itemPosition].getCheckInRadius();
        String destinationName = destinationDatas[itemPosition].getDestinationName();
        int assignDestinationID = destinationDatas[itemPosition].getAssignDestinationID();
        int destinationID = destinationDatas[itemPosition].getDestinationID();
        double destinationLatitude = destinationDatas[itemPosition].getDestinationLatitude();
        double destinationLongitude = destinationDatas[itemPosition].getDestinationLongitude();
        String checkedOutReportedDate = destinationDatas[itemPosition].getCheckedOutReportedDate();
        boolean isEditable = destinationDatas[itemPosition].isEditable();
        boolean isRequireApproval = destinationDatas[itemPosition].isRequiresApproval();

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
        startActivityForResult(intentDetailDestination,REQUEST_CODE_ACTIVITYDETAILS);
    }

    DestinationData[] destinationDatas = null;
    @Override
    public void onGetDestinationSuccess(DestinationsModel destinationsModel) {
        if (destinationsModel.getErrorCode() == 2) {
            GeneralUtils.createAlertDialog(getActivity(), destinationsModel.getMessage());
        } else if (destinationsModel.getDestinationData() != null) {
            destinationDatas = destinationsModel.getDestinationData();

            ArrayList<DestinationData> arrayList = sortDestinationsOnDate(destinationDatas);

            DestinationsAdapter destinationsAdapter = new DestinationsAdapter(getActivity(), arrayList);
            destinationsAdapter.delegate = DestinationFragment.this;
            mRecyclerView_destinations.setAdapter(destinationsAdapter);
            destinationsAdapter.notifyDataSetChanged();
            layoutManager.onRestoreInstanceState(listViewState);

            if(!EventBus.getDefault().isRegistered(this))
                EventBus.getDefault().register(this);
        }
    }

    /**
     *   sort the destinations
     * @param destinationDatas
     * @return
     */
    public ArrayList<DestinationData> sortDestinationsOnDate(DestinationData[] destinationDatas) {
        ArrayList<DestinationData> arrayList = new ArrayList<>();

        for (DestinationData data:destinationDatas) {
            arrayList.add(data);
        }

        Log.i(Constants.TAG, "before sorting --> "+arrayList.toString());

        Collections.sort(arrayList, new Comparator<DestinationData>() {
            @Override
            public int compare(DestinationData lhs, DestinationData rhs) {

                DateTimeFormatter dateTimeFormatter = ISODateTimeFormat.dateTimeParser();
                DateTime d1 = dateTimeFormatter.parseDateTime(lhs.getAssigndestinationTime());
                DateTime d2 = dateTimeFormatter.parseDateTime(rhs.getAssigndestinationTime());

                if (d1 == null || d2 == null)
                    return 0;

                return d1.compareTo(d2);
            }
        });

        Collections.reverse(arrayList);
        Log.i(Constants.TAG, "after sorting --> "+arrayList.toString());
        return arrayList;
    }

    @Override
    public void callCheckInCheckOutService(int destinationPosition, boolean isCheckedIn) {
        listViewState = layoutManager.onSaveInstanceState();

        isDeviceEnteredWithinDestinationRadius(destinationDatas[destinationPosition].getDestinationLatitude(),
                destinationDatas[destinationPosition].getDestinationLongitude(),
                destinationDatas[destinationPosition].getCheckInRadius(),
                destinationDatas[destinationPosition].isCheckedIn(),
                destinationDatas[destinationPosition].getAssignDestinationID());
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
    }

    public void isDeviceEnteredWithinDestinationRadius(double destinationLatitude,
                                                       double destinationLongitude, double checkInRadius,
                                                       boolean checkedIn, int assignDestinationID) {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        //Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        List<String> providers = locationManager.getProviders(true);
        //Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Location location = null;

        for (String provider: providers) {

            Location currentLocation = locationManager.getLastKnownLocation(provider);
            if (currentLocation == null) {
                continue;
            }
            if (location == null || currentLocation.getAccuracy() < location.getAccuracy()) {
                location = currentLocation;
            }

        }

        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();

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
            restCall.callCheckInService(checkedIn, assignDestinationID);
        }
    }
    /**
     * Called after the autocomplete activity has finished to return its result.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check that the result was from the autocomplete widget.
        if (requestCode == REQUEST_CODE_ACTIVITYDETAILS) {
            if (resultCode == getActivity().RESULT_OK && data.getStringExtra("result").equals("success")) {

                GetDestinationsRestCall destinationsRestCall = new GetDestinationsRestCall(getActivity());
                destinationsRestCall.delegate = DestinationFragment.this;
                destinationsRestCall.callGetDestinations();
            }
        }
    }
    public void onEvent(AppEvents event) {
        GetDestinationsRestCall destinationsRestCall;

        switch (event) {

            case OFFLINECHECKIN:
                EventBus.getDefault().post(AppEvents.OFFLINECHECKIN);
                EventBus.getDefault().cancelEventDelivery(event) ;
                EventBus.getDefault().unregister(this);


                Log.d("jomy","callll checkout22...");
                destinationsRestCall = new GetDestinationsRestCall(getActivity());
                destinationsRestCall.delegate = DestinationFragment.this;
                destinationsRestCall.callGetDestinations();

                break;
            case OFFLINECHECKOUT:
                EventBus.getDefault().post(AppEvents.OFFLINECHECKOUT);
                EventBus.getDefault().cancelEventDelivery(event) ;
                EventBus.getDefault().unregister(this);
                Log.d("jomy","callll checkout...");
                destinationsRestCall = new GetDestinationsRestCall(getActivity());
                destinationsRestCall.delegate = DestinationFragment.this;
                destinationsRestCall.callGetDestinations();
                break;
        }
    }

}
