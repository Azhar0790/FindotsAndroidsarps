package fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import adapters.DestinationsAdapter;
import butterknife.Bind;
import butterknife.ButterKnife;
import findots.bridgetree.com.findots.R;
import interfaces.IDestinations;

/**
 * Created by parijathar on 6/14/2016.
 */
public class DestinationFragment extends Fragment implements IDestinations {

    @Bind(R.id.RecyclerView_destinations)
    RecyclerView mRecyclerView_destinations;

    LinearLayoutManager layoutManager = null;

    ArrayList<String> arrayList = new ArrayList<>();

    public static DestinationFragment newInstance() {
        DestinationFragment destinationFragment = new DestinationFragment();
        return destinationFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.destinations, null);

        ButterKnife.bind(this, rootView);

        arrayList.add("Bridgetree Research Services");
        arrayList.add("Yamaha Showroom");
        arrayList.add("Suguna Hospital");

        DestinationsAdapter destinationsAdapter = new DestinationsAdapter(getActivity(), arrayList);
        destinationsAdapter.delegate = DestinationFragment.this;
        mRecyclerView_destinations.setAdapter(destinationsAdapter);
        destinationsAdapter.notifyDataSetChanged();

        layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView_destinations.setLayoutManager(layoutManager);

        return rootView;
    }

    @Override
    public void onDestinationSelected(int itemPosition) {

    }
}
