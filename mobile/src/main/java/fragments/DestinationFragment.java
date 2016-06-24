package fragments;

import android.content.Intent;
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

import activities.DetailDestinationActivity;
import adapters.DestinationsAdapter;
import butterknife.Bind;
import butterknife.ButterKnife;
import findots.bridgetree.com.findots.Constants;
import findots.bridgetree.com.findots.R;
import interfaces.IDestinations;
import restcalls.checkInCheckOut.CheckInCheckOutRestCall;
import restcalls.checkInCheckOut.ICheckInCheckOut;
import restcalls.destinations.DestinationData;
import restcalls.destinations.DestinationsModel;
import restcalls.destinations.GetDestinationsRestCall;
import restcalls.destinations.IGetDestinations;
import retrofit.Call;
import utils.GeneralUtils;

/**
 * Created by parijathar on 6/14/2016.
 */
public class DestinationFragment extends Fragment implements IDestinations, IGetDestinations, ICheckInCheckOut {

    @Bind(R.id.RecyclerView_destinations)
    RecyclerView mRecyclerView_destinations;

    LinearLayoutManager layoutManager = null;
    Parcelable listViewState = null;

    public static DestinationFragment newInstance() {
        DestinationFragment destinationFragment = new DestinationFragment();
        return destinationFragment;
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
        double destinationLatitude = destinationDatas[itemPosition].getDestinationLatitude();
        double destinationLongitude = destinationDatas[itemPosition].getDestinationLongitude();
        String checkedOutReportedDate = destinationDatas[itemPosition].getCheckedOutReportedDate();

        listViewState = layoutManager.onSaveInstanceState();

        Intent intentDetailDestination = new Intent(getContext(), DetailDestinationActivity.class);
        intentDetailDestination.putExtra("address", address);
        intentDetailDestination.putExtra("checkedIn", checkedIn);
        intentDetailDestination.putExtra("checkedOut", checkedOut);
        intentDetailDestination.putExtra("destinationName", destinationName);
        intentDetailDestination.putExtra("assignDestinationID", assignDestinationID);
        intentDetailDestination.putExtra("destinationLatitude", destinationLatitude);
        intentDetailDestination.putExtra("destinationLongitude", destinationLongitude);
        intentDetailDestination.putExtra("checkedOutReportedDate", checkedOutReportedDate);
        intentDetailDestination.putExtra("checkInRadius", checkInRadius);
        startActivity(intentDetailDestination);
    }

    DestinationData[] destinationDatas = null;
    @Override
    public void onGetDestinationSuccess(DestinationsModel destinationsModel) {
        if (destinationsModel.getErrorCode() == 2) {
            GeneralUtils.createAlertDialog(getActivity(), destinationsModel.getMessage());
        } else if (destinationsModel.getDestinationData() != null) {
            destinationDatas = destinationsModel.getDestinationData();

            DestinationsAdapter destinationsAdapter = new DestinationsAdapter(getActivity(), destinationsModel.getDestinationData());
            destinationsAdapter.delegate = DestinationFragment.this;
            mRecyclerView_destinations.setAdapter(destinationsAdapter);
            destinationsAdapter.notifyDataSetChanged();
            layoutManager.onRestoreInstanceState(listViewState);
        }
    }

    @Override
    public void callCheckInCheckOutService(int destinationPosition, boolean isCheckedIn) {
        int assignedDestinationID = destinationDatas[destinationPosition].getAssignDestinationID();

        listViewState = layoutManager.onSaveInstanceState();
        CheckInCheckOutRestCall restCall = new CheckInCheckOutRestCall(getActivity());
        restCall.delegate = DestinationFragment.this;
        restCall.callCheckInService(isCheckedIn, assignedDestinationID);
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
}
