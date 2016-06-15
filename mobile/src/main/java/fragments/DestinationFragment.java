package fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import findots.bridgetree.com.findots.R;

/**
 * Created by parijathar on 6/14/2016.
 */
public class DestinationFragment extends Fragment {

    public static DestinationFragment newInstance() {
        DestinationFragment destinationFragment = new DestinationFragment();
        return destinationFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.destinations, null);

        ButterKnife.bind(this, rootView);


        return super.onCreateView(inflater, container, savedInstanceState);


    }
}
