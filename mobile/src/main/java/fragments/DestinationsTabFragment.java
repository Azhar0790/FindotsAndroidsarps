package fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import activities.DestinationAddMapActivity;
import adapters.DestinationsPagerAdapter;
import findots.bridgetree.com.findots.Constants;
import findots.bridgetree.com.findots.FinDotsApplication;
import findots.bridgetree.com.findots.R;
import restcalls.destinations.DestinationData;
import restcalls.destinations.DestinationsModel;
import restcalls.destinations.GetDestinationsRestCall;
import restcalls.destinations.IGetDestinations;

/**
 * Created by parijathar on 7/4/2016.
 */
public class DestinationsTabFragment extends Fragment implements IGetDestinations {

    ViewPager viewPagerDestinations = null;
    TabLayout tabLayout = null;

    public static DestinationData[] destinationDatas = null;
    private static final int REQUEST_CODE_ADD_DESTINATION = 9999;

    public static DestinationsTabFragment newInstance() {
        DestinationsTabFragment destinationsTabFragment = new DestinationsTabFragment();
        return destinationsTabFragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.destinations_tab_layout, null);

        tabLayout = (TabLayout) rootView.findViewById(R.id.tabLayoutDestinations);
        tabLayout.addTab(tabLayout.newTab().setText("Map"));
        tabLayout.addTab(tabLayout.newTab().setText("List"));
        FloatingActionButton fabAddDestination = (FloatingActionButton) rootView.findViewById(R.id.fabAddDestination);
        fabAddDestination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FinDotsApplication.getInstance().trackEvent("Destination","Click","Clicked Add Destination Event");

                // Click action
                Intent intent = new Intent(getActivity(), DestinationAddMapActivity.class);
                startActivityForResult(intent,REQUEST_CODE_ADD_DESTINATION);
            }
        });
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.app_color));
        tabLayout.setSelectedTabIndicatorHeight(8);
        tabLayout.setTabTextColors(getResources().getColor(R.color.app_color),
                getResources().getColor(R.color.app_color));

        ViewCompat.setElevation(tabLayout, 5f);

        viewPagerDestinations = (ViewPager) rootView.findViewById(R.id.viewPagerDestinations);

        GetDestinationsRestCall destinationsRestCall = new GetDestinationsRestCall(getActivity());
        destinationsRestCall.delegate = DestinationsTabFragment.this;
        destinationsRestCall.callGetDestinations();


        viewPagerDestinations.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPagerDestinations.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        return rootView;
    }

    @Override
    public void onGetDestinationSuccess(DestinationsModel destinationsModel) {
        destinationDatas = destinationsModel.getDestinationData();

        DestinationsPagerAdapter pagerAdapter = new DestinationsPagerAdapter(getFragmentManager(), tabLayout.getTabCount());
        viewPagerDestinations.setAdapter(pagerAdapter);
    }

    @Override
    public void onGetDestinationFailure(String errorMessage) {
        Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(Constants.TAG, "onResume: DestinationsTabFragment");
        FinDotsApplication.getInstance().trackScreenView("Destination Map & List Fragment");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("jomy", "onActivityResult..//");
        // Check that the result was from the autocomplete widget.
        if (requestCode == REQUEST_CODE_ADD_DESTINATION) {

                Log.d("jomy", "onActivityResultwwffg..//");
                GetDestinationsRestCall destinationsRestCall = new GetDestinationsRestCall(getActivity());
                destinationsRestCall.delegate = DestinationsTabFragment.this;
                destinationsRestCall.callGetDestinations();

        } else {
        }
    }
}
