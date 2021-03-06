package com.knowall.findots.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.knowall.findots.Constants;
import com.knowall.findots.R;
import com.knowall.findots.activities.DetailDestinationActivity;
import com.knowall.findots.activities.MenuActivity;
import com.knowall.findots.adapters.DestinationsAdapter;
import com.knowall.findots.database.DataHelper;
import com.knowall.findots.distancematrix.DistanceMatrixService;
import com.knowall.findots.distancematrix.IDistanceMatrix;
import com.knowall.findots.distancematrix.model.Distance;
import com.knowall.findots.distancematrix.model.DistanceMatrix;
import com.knowall.findots.distancematrix.model.Duration;
import com.knowall.findots.distancematrix.model.Elements;
import com.knowall.findots.distancematrix.model.Rows;
import com.knowall.findots.events.AppEvents;
import com.knowall.findots.interfaces.IDestinations;
import com.knowall.findots.locationUtils.LocationModel.LocationData;
import com.knowall.findots.restcalls.checkInCheckOut.CheckInCheckOutRestCall;
import com.knowall.findots.restcalls.checkInCheckOut.ICheckInCheckOut;
import com.knowall.findots.restcalls.destinations.DestinationData;
import com.knowall.findots.restcalls.destinations.DestinationsModel;
import com.knowall.findots.restcalls.destinations.GetDestinationsRestCall;
import com.knowall.findots.restcalls.destinations.IGetDestinations;
import com.knowall.findots.utils.GeneralUtils;
import com.knowall.findots.utils.timeUtils.TimeSettings;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

import static com.knowall.findots.adapters.DestinationsAdapter.show_admin_comment;

/**
 * Created by parijathar on 6/14/2016.
 */
public class DestinationFragment extends Fragment
        implements
        IDestinations,
        IGetDestinations,
        ICheckInCheckOut,
        IDistanceMatrix
        //GoogleApiClient.OnConnectionFailedListener,
        //GoogleApiClient.ConnectionCallbacks,
        //LocationListener
{

    @Bind(R.id.RecyclerView_destinations)
    RecyclerView mRecyclerView_destinations;

    @Bind(R.id.textViewNoDestinations)
    TextView textViewNoDestinations;

    //GoogleApiClient mGoogleApiClient = null;

    LinearLayoutManager layoutManager = null;
    Parcelable listViewState = null;
    private static final int REQUEST_CODE_ACTIVITYDETAILS = 1;
    double currentLatitude = 0.013, currentLongitude = 0.012;
    boolean loadDistanceMatrix=false;
    ArrayList<DestinationData> arrayListDestinations = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        Log.d("paul", "Oncreate");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
//        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        EventBus.getDefault().unregister(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.destinations, null);


        Log.d("paul", "Oncreateview");
        if (!GeneralUtils.checkPlayServices(getActivity())) {
            Toast.makeText(getActivity(), "Location not supported in this device", Toast.LENGTH_SHORT).show();
        }

        ButterKnife.bind(this, rootView);

        layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView_destinations.setLayoutManager(layoutManager);

        arrayListDestinations = sortDestinationsOnScheduleDate(DestinationsTabFragment.destinationDatas);

        if (arrayListDestinations == null || arrayListDestinations.size() == 0) {
            textViewNoDestinations.setVisibility(View.VISIBLE);
            mRecyclerView_destinations.setVisibility(View.INVISIBLE);
        } else {
            textViewNoDestinations.setVisibility(View.GONE);
            mRecyclerView_destinations.setVisibility(View.VISIBLE);
            setAdapterForDestinations();
        }

        return rootView;
    }


    /*@Override
    public void onStop() {
        super.onStop();
        try {
            if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                mGoogleApiClient.disconnect();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }*/

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
        String admin_comment = arrayListDestinations.get(itemPosition).getComment();

        String scheduleDate = arrayListDestinations.get(itemPosition).getScheduleDate();
        String travelTime = arrayListDestinations.get(itemPosition).getTravelTime();
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
        intentDetailDestination.putExtra("scheduleDate", scheduleDate);
        intentDetailDestination.putExtra("travelTime", travelTime);
        intentDetailDestination.putExtra("adminComment", admin_comment);

        startActivityForResult(intentDetailDestination, REQUEST_CODE_ACTIVITYDETAILS);
    }

    @Override
    public void onGetDestinationSuccess(DestinationsModel destinationsModel) {
        if (destinationsModel.getErrorCode() == 2) {
            GeneralUtils.createAlertDialog(getActivity(), destinationsModel.getMessage());
        } else if (destinationsModel.getDestinationData() != null) {
            DestinationData[] destinationDatas = destinationsModel.getDestinationData();

            DestinationsTabFragment.destinationDatas = destinationDatas;
            DestinationsTabFragment.createScheduledUnscheduledListByDate(DestinationsTabFragment.current_selected_dateTime);
//            EventBus.getDefault().post(AppEvents.REFRESHTABVALUES);
//            EventBus.getDefault().post(AppEvents.REFRESHDESTINATIONS);
//
//            arrayListDestinations = sortDestinationsOnScheduleDate(destinationDatas);
//            Log.d("jomy","arrayListDestinations ss"+arrayListDestinations.size());
//            setAdapterForDestinations();
        }

            /*
             *  display admin comment
             */
        if (DestinationsAdapter.show_admin_comment && DestinationsAdapter.admin_comment != null &&
                !DestinationsAdapter.admin_comment.equals("")) {

            DestinationsAdapter.show_admin_comment = false;

            new AlertDialog.Builder(getActivity())
                    .setTitle("Admin Comment")
                    .setMessage(DestinationsAdapter.admin_comment)
                    .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
        }
    }

    DestinationsAdapter destinationsAdapter = null;

    public void setAdapterForDestinations() {
        destinationsAdapter = new DestinationsAdapter(MenuActivity.ContextMenuActivity, arrayListDestinations);
        destinationsAdapter.delegate = DestinationFragment.this;
        mRecyclerView_destinations.setAdapter(destinationsAdapter);

        if (nextScheduledItemPosition >= 0) {
            layoutManager.scrollToPosition(nextScheduledItemPosition);
        } else {
            layoutManager.onRestoreInstanceState(listViewState);
        }

        destinationsAdapter.notifyDataSetChanged();

        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }


    int nextScheduledItemPosition = -1;

    public ArrayList<DestinationData> sortDestinationsOnScheduleDate(DestinationData[] destinationDatas) {
        ArrayList<DestinationData> arrayListScheduleDate = new ArrayList<>();
        arrayListScheduleDate.clear();

        ArrayList<DestinationData> arrayListUnScheduleDate = new ArrayList<>();
        arrayListUnScheduleDate.clear();

        /**
         *   dividing scheduled and unscheduled arraylist of destinations
         */
        for (DestinationData data : destinationDatas) {
            if (data.getScheduleDate().length() != 0) {
                if (data.isScheduleDisplayStatus())
                    arrayListScheduleDate.add(data);
            } else {
                arrayListUnScheduleDate.add(data);
            }
        }


        /**
         *    sorting of scheduled arraylist
         *    based on Schedule Date
         */
        Collections.sort(arrayListScheduleDate, new Comparator<DestinationData>() {
            @Override
            public int compare(DestinationData lhs, DestinationData rhs) {

                //DateTimeFormatter dateTimeFormatter = ISODateTimeFormat.dateTimeParser();
                DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

                DateTime d1 = dateTimeFormatter.parseDateTime(lhs.getScheduleDate());
                DateTime d2 = dateTimeFormatter.parseDateTime(rhs.getScheduleDate());

                if (d1 == null || d2 == null)
                    return 0;

                return d1.compareTo(d2);
            }
        });

        //Collections.reverse(arrayListScheduleDate);
        //Collections.reverse(arrayListScheduleDate);

        /**
         *  set time difference to the scheduled arraylist
         */
        int i = 0;
        for (DestinationData data : arrayListScheduleDate) {
            long timeDifference = TimeSettings.getTimeDifference(data.getScheduleDate());
            arrayListScheduleDate.get(i).setTimeDifference(timeDifference);
            i++;
        }


        /**
         *   find out next scheduled position of list item
         */
        loadDistanceMatrix= false;
        if (arrayListScheduleDate.size() > 0) {
            nextScheduledItemPosition = 0;
            for (DestinationData data : arrayListScheduleDate) {

                if (data.getTimeDifference() > 0) {

                    if (DestinationsTabFragment.pagerCurrentItem == 1 &&
                            isSelectedDayEqualsCurrentDay()) {
                        loadDistanceMatrix=true;
//                        Location location = GeneralUtils.getCurrentLatitudeAndLongitude(getActivity());
//
//                        if (location != null) {
//                            String origin = location.getLatitude() + "," + location.getLongitude();
//                            String destination = data.getDestinationLatitude() + "," + data.getDestinationLongitude();
//                            googleDistanceMatrixAPI(origin, destination);
//                        }
                    }
                    else
                        loadDistanceMatrix= false;

                    break;
                }
                nextScheduledItemPosition++;
            }
        }

        if (nextScheduledItemPosition > arrayListScheduleDate.size() - 1) {
            nextScheduledItemPosition = -1;
        }

        if (arrayListScheduleDate.size() > 0) {
            arrayListScheduleDate.get(0).setScheduledStatus("scheduled");
        }


        /**
         *    sorting of unscheduled arraylist
         *    based on Assign Destination Time
         */
        Collections.sort(arrayListUnScheduleDate, new Comparator<DestinationData>() {
            @Override
            public int compare(DestinationData lhs, DestinationData rhs) {

                //DateTimeFormatter dateTimeFormatter = ISODateTimeFormat.dateTimeParser();
                DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

                DateTime d1 = dateTimeFormatter.parseDateTime(lhs.getAssigndestinationTime());
                DateTime d2 = dateTimeFormatter.parseDateTime(rhs.getAssigndestinationTime());

                if (d1 == null || d2 == null)
                    return 0;

                return d1.compareTo(d2);
            }
        });

        Collections.reverse(arrayListUnScheduleDate);

        Log.d("jomy", "arrayListUnScheduleDate Date size : " + arrayListUnScheduleDate.size());

        /**
         *   connect the google api client
         */
       /* try {
            mGoogleApiClient.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        /**
         *   setting schedule status
         */

        if (arrayListUnScheduleDate.size() > 0) {
            arrayListUnScheduleDate.get(0).setScheduledStatus("unscheduled");
        }

        /**
         *   adding all the arraylist
         */
        arrayListScheduleDate.addAll(arrayListUnScheduleDate);

        Log.d("jomy", "Schedule Date size : " + arrayListScheduleDate.size());
        return arrayListScheduleDate;
    }

    public void loadDistanceMatrix()
    {
        Location location = GeneralUtils.getCurrentLatitudeAndLongitude(getActivity());

        if (location != null && arrayListDestinations.size()>0 ) {
            String origin = location.getLatitude() + "," + location.getLongitude();
            String destination = arrayListDestinations.get(nextScheduledItemPosition).getDestinationLatitude() + "," + arrayListDestinations.get(nextScheduledItemPosition).getDestinationLongitude();
            googleDistanceMatrixAPI(origin, destination);
        }
    }


    public boolean isSelectedDayEqualsCurrentDay() {

        /**
         *   check current date
         */
        CalendarDay selectedDay = DestinationsTabFragment.materialCalendarView.getSelectedDate();
        Log.i("CalendarDay", selectedDay.toString());
        Log.i("CalendarDay", CalendarDay.today().toString());

        // ------------------------------------
        SimpleDateFormat sdf3 = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);

        Date dateSelectedDay = null;
        Date dateCurrentDay = null;
        try {
            dateSelectedDay = sdf3.parse(selectedDay.getDate().toString());
            dateCurrentDay = sdf3.parse(CalendarDay.today().getDate().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }


        // ------------------------------------
        SimpleDateFormat sdf4 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.ENGLISH);

        String strSelectedDay = "";
        String strCurrentDay = "";

        try {
            strSelectedDay = sdf4.format(dateSelectedDay);
            strCurrentDay = sdf4.format(dateCurrentDay);
        } catch (Exception e) {
            e.printStackTrace();
        }

        strSelectedDay = strSelectedDay.substring(0, 10);
        strCurrentDay = strCurrentDay.substring(0, 10);

        if (strSelectedDay.equals(strCurrentDay)) {
            return true;
        }

        return false;
    }


    public static int destinationListPosition = 0;

    @Override
    public void callCheckInCheckOutService(String checkOutNote, int destinationPosition, boolean isCheckedIn) {
        listViewState = layoutManager.onSaveInstanceState();
        destinationListPosition = destinationPosition;

        isDeviceEnteredWithinDestinationRadius(arrayListDestinations.get(destinationPosition).getDestinationLatitude(),
                arrayListDestinations.get(destinationPosition).getDestinationLongitude(),
                arrayListDestinations.get(destinationPosition).getCheckInRadius(),
                arrayListDestinations.get(destinationPosition).isCheckedIn(),
                arrayListDestinations.get(destinationPosition).getAssignDestinationID(), isCheckedIn,
                checkOutNote);
    }

    @Override
    public void onGetDestinationFailure(String errorMessage) {
        Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCheckInSuccess(String status) {
        Toast.makeText(getActivity(), status, Toast.LENGTH_SHORT).show();
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
        } else if (!arrayListDestinations.get(position).isCheckedOut()) {
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
                                                       boolean checkedIn, int assignDestinationID,
                                                       boolean isCheckIn, String checkOutNote) {
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
            restCall.callCheckInService(checkOutNote, checkedIn, assignDestinationID, currentLatitude, currentLongitude);
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
                restCall.callCheckInService(checkOutNote, checkedIn, assignDestinationID, currentLatitude, currentLongitude);
            }
        }
    }


    /**
     * Called after the autocomplete activity has finished to return its result.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);

        Log.i(Constants.TAG, "onActivityResult..//");
        // Check that the result was from the autocomplete widget.
        try {
            if (requestCode == REQUEST_CODE_ACTIVITYDETAILS) {
                Log.i(Constants.TAG, "onActivityResult..//" + requestCode + "result ");
                if (resultCode == getActivity().RESULT_OK && (data.getStringExtra("result").equals("success") ||
                        data.getStringExtra("result").equals("renamedDestination") ||
                        data.getStringExtra("result").equals("deletedDestination"))) {

                    Log.i(Constants.TAG, "onActivityResult..//  GetDestinationsRestCall");
                    GetDestinationsRestCall destinationsRestCall = new GetDestinationsRestCall(getActivity());
                    destinationsRestCall.delegate = DestinationFragment.this;
                    destinationsRestCall.callGetDestinations();
                } else if (resultCode == 2) {
                    /**
                     *   offline changing the checkin/checkout status
                     *   from the detailDestination activity
                     */
                    if (data.getStringExtra("offlineData").equals("checkedIn")) {
                        arrayListDestinations.get(destinationListPosition).setCheckedIn(true);
                    } else {
                        arrayListDestinations.get(destinationListPosition).setCheckedOut(true);

                        String time = data.getStringExtra("offlineData");
                        arrayListDestinations.get(destinationListPosition).setCheckedOutReportedDate(time);
                    }
                    setAdapterForDestinations();
                } else if (resultCode == 3) {
                    Log.d("jomy", "check...");
                    GetDestinationsRestCall destinationsRestCall = new GetDestinationsRestCall(getActivity());
                    destinationsRestCall.delegate = DestinationFragment.this;
                    destinationsRestCall.callGetDestinations();
                } else {
                    super.onActivityResult(requestCode, resultCode, data);
                    Log.i(Constants.TAG, "onActivityResult..//  GetDestinationsRestCall - first else block");
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data);
                Log.i(Constants.TAG, "onActivityResult.we.//  else block");
            }
        } catch (Exception e) {
        }
    }


    public void onEvent(AppEvents event) {
        GetDestinationsRestCall destinationsRestCall;

        switch (event) {

            case OFFLINECHECKIN:
                EventBus.getDefault().post(AppEvents.OFFLINECHECKIN);
//                EventBus.getDefault().cancelEventDelivery(event);
                EventBus.getDefault().unregister(this);
                Log.d("jomy", "callll checkout22...");
                destinationsRestCall = new GetDestinationsRestCall(getActivity());
                destinationsRestCall.delegate = DestinationFragment.this;
                destinationsRestCall.callGetDestinations();

                break;
            case OFFLINECHECKOUT:
                EventBus.getDefault().post(AppEvents.OFFLINECHECKOUT);
//                EventBus.getDefault().cancelEventDelivery(event);
                EventBus.getDefault().unregister(this);
                Log.d("jomy", "callll checkout...");
                destinationsRestCall = new GetDestinationsRestCall(getActivity());
                destinationsRestCall.delegate = DestinationFragment.this;
                destinationsRestCall.callGetDestinations();

                break;

            case SCHEDULEDDATELIST:

//                EventBus.getDefault().cancelEventDelivery(event);
//              EventBus.getDefault().unregister(this);

                Log.i("paul", "Page SCHEDULEDDATELIST");
                arrayListDestinations = sortDestinationsOnScheduleDate(DestinationsTabFragment.destinationDatas);

                if (arrayListDestinations == null || arrayListDestinations.size() == 0) {
                    textViewNoDestinations.setVisibility(View.VISIBLE);
                    mRecyclerView_destinations.setVisibility(View.INVISIBLE);
                } else {
                    Log.i("paul", "Page SCHEDULEDDATELIST Adapter ");
                    textViewNoDestinations.setVisibility(View.GONE);
                    mRecyclerView_destinations.setVisibility(View.VISIBLE);
                    setAdapterForDestinations();
                    if(loadDistanceMatrix && nextScheduledItemPosition>=0)
                    {
                        Log.i("paul", "Page SCHEDULEDDATELIST Adapter Distance");
                        loadDistanceMatrix();
                        loadDistanceMatrix=false;
                    }
                }
                break;
        }
    }

    /**
     *   builds Google Api client
     */
    /*protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(DestinationFragment.this)
                .addOnConnectionFailedListener(DestinationFragment.this)
                .addApi(LocationServices.API)
                .build();
    }*/

    /*@Override
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
    }*/

    /*@Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            double originLatitude = location.getLatitude();
            double originLongitude = location.getLongitude();

            if (arrayListDestinations.size() > 0) {
                String origin = originLatitude+","+originLongitude;
                String destination = "";

                if (destinations != null) {
                    destination = destinations.toString();
                }

                googleDistanceMatrixAPI(origin, destination);
                try {
                    LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }*/

    /*@Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }*/

    /**
     * call DistanceMatrix API to display
     * duration and distance of the
     * current location and destination locations on the map markers
     */
    public void googleDistanceMatrixAPI(String origin, String destination) {
        DistanceMatrixService service = new DistanceMatrixService();
        service.delegate = DestinationFragment.this;
        service.callDistanceMatrixService(getActivity(), origin, destination);
    }


    Elements[] elements = null;

    @Override
    public void onDistanceMatrixSuccess(DistanceMatrix distanceMatrix) {
        String travelTime = "";

        if (distanceMatrix != null) {
            Rows[] rows = distanceMatrix.getRows();
            if (rows.length > 0) {
                elements = rows[0].getElements();
                if (elements.length > 0) {
                    Duration duration = elements[0].getDuration();

                    if (duration != null) {
                        travelTime = duration.getText();
                        Log.i(Constants.TAG, "onDistanceMatrixSuccess: travelTime = " + travelTime);
                    }

                    Distance distance = elements[0].getDistance();
                    if (distance != null) {
                        travelTime = travelTime + " / " + distance.getText();
                        Log.i(Constants.TAG, "onDistanceMatrixSuccess: travelTime = " + travelTime);
                    }

                    if (arrayListDestinations.size() > 0 && nextScheduledItemPosition != -1)
                        arrayListDestinations.get(nextScheduledItemPosition).setTravelTime(travelTime);

                    //setAdapterForDestinations();
                    destinationsAdapter.notifyDataSetChanged();
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

    /*StringBuilder destinations = null;
    public void createDestinations(ArrayList<DestinationData> arrayListScheduleDate) {
        if (arrayListScheduleDate.size() > 0) {
            destinations = new StringBuilder();
            for (DestinationData data:arrayListScheduleDate) {
                destinations.append(data.getDestinationLatitude()+","+data.getDestinationLongitude()+"|");
            }
        }
    }*/

}
