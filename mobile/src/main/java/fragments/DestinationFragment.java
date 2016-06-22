package fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import activities.DetailDestinationActivity;
import adapters.DestinationsAdapter;
import butterknife.Bind;
import butterknife.ButterKnife;
import findots.bridgetree.com.findots.R;
import interfaces.IDestinations;
import restcalls.Destinations.DestinationsModel;
import restcalls.Destinations.GetDestinationsRestCall;
import restcalls.Destinations.IGetDestinations;

/**
 * Created by parijathar on 6/14/2016.
 */
public class DestinationFragment extends Fragment implements IDestinations, IGetDestinations {

    @Bind(R.id.RecyclerView_destinations)
    RecyclerView mRecyclerView_destinations;

    LinearLayoutManager layoutManager = null;

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
        destinationsRestCall.callGetDestinations("", "", "", 2);

        return rootView;
    }

    @Override
    public void onDestinationSelected(int itemPosition) {
        Intent intentDetailDestination = new Intent(getContext(), DetailDestinationActivity.class);
        startActivity(intentDetailDestination);
    }

    @Override
    public void onGetDestinationSuccess(DestinationsModel destinationsModel) {
        DestinationsAdapter destinationsAdapter = new DestinationsAdapter(getActivity(), destinationsModel.getDestinationData());
        destinationsAdapter.delegate = DestinationFragment.this;
        mRecyclerView_destinations.setAdapter(destinationsAdapter);
        destinationsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onGetDestinationFailure(String errorMessage) {
        Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
    }
}
