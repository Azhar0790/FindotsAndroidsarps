package com.knowall.findots.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.knowall.findots.R;
import com.knowall.findots.restcalls.getUser.GetUserData;
import com.knowall.findots.restcalls.getUser.GetUserRestCall;
import com.knowall.findots.restcalls.getUser.IGetUser;
import com.knowall.findots.utils.AppStringConstants;
import com.knowall.findots.utils.GeneralUtils;

/**
 * Created by parijathar on 8/9/2016.
 */
public class InitialFragment extends Fragment implements IGetUser {

    /**
     * User Types
     */
    public static final int INDIVIDUAL = 1;
    public static final int CORPORATE = 2;
    public static final int CORPORATE_ADMIN = 3;
    public static final int WEB_USER_ADMIN = 4;
    public static final int COUNTRY_SPECIFIC_ADMIN = 5;

    int userTypeID = 0;

    public static InitialFragment newInstance() {
        InitialFragment initialFragment = new InitialFragment();
        return initialFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.initial_view, null);

        userTypeID = GeneralUtils.getSharedPreferenceInt(getActivity(), AppStringConstants.USER_TYPE_ID);

        if (userTypeID == CORPORATE_ADMIN) {
            GetUserRestCall getUserRestCall = new GetUserRestCall(getActivity());
            getUserRestCall.delegate = InitialFragment.this;
            getUserRestCall.callGetUsers();
        } else if (userTypeID == CORPORATE){
            navigateBasedOnUserType(userTypeID, null);
        }

        return rootView;
    }

    private void navigateBasedOnUserType(int userTypeID, GetUserData[] userDatas) {
        switch (userTypeID) {
            case INDIVIDUAL:

                break;

            case CORPORATE:
                FragmentTransaction destinationTransaction = getFragmentManager().beginTransaction();
                destinationTransaction.replace(R.id.frameLayoutInitialContent, DestinationsTabFragment.newInstance());
                destinationTransaction.commit();
                break;

            case CORPORATE_ADMIN:
                FragmentTransaction getUsersMapFragment = getFragmentManager().beginTransaction();
                getUsersMapFragment.replace(R.id.frameLayoutInitialContent, GetUsersMapFragment.newInstance(userDatas));
                getUsersMapFragment.commit();
                break;

            case WEB_USER_ADMIN:

                break;

            case COUNTRY_SPECIFIC_ADMIN:

                break;
        }
    }

    @Override
    public void onGetUserSuccess(GetUserData[] getUserDatas) {
        navigateBasedOnUserType(userTypeID, getUserDatas);
    }

    @Override
    public void onGetUserFailure(String message) {
        GeneralUtils.createAlertDialog(getActivity(), message);
    }
}
