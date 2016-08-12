package com.knowall.findots.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.knowall.findots.R;
import com.knowall.findots.activities.MenuActivity;
import com.knowall.findots.activities.SearchUserActivity;
import com.knowall.findots.restcalls.getUser.GetUserData;
import com.knowall.findots.restcalls.getUser.GetUserRestCall;
import com.knowall.findots.restcalls.getUser.IGetUser;
import com.knowall.findots.utils.AppStringConstants;
import com.knowall.findots.utils.GeneralUtils;

import java.util.ArrayList;

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
    private static final int REQUEST_CODE_SEARCH_USER = 1000;
    int userID = -1;

    public static ArrayList<GetUserData> getUserDatasList = new ArrayList<GetUserData>();
    int userTypeID = 0;
    TextView textView_heading;

    public static InitialFragment newInstance() {
        InitialFragment initialFragment = new InitialFragment();
        return initialFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.initial_view, null);

        userTypeID = GeneralUtils.getSharedPreferenceInt(getActivity(), AppStringConstants.USER_TYPE_ID);

        textView_heading = (TextView) getActivity().findViewById(R.id.TextView_heading);
        textView_heading.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.drop_down_user,0);
        textView_heading.setCompoundDrawablePadding(5);
//        textView_heading.setText("All");
        textView_heading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.ContextMenuActivity, SearchUserActivity.class);
                intent.putExtra("userId", userID);
                startActivityForResult(intent, REQUEST_CODE_SEARCH_USER);
            }
        });

        restoreTheView();

        return rootView;
    }

    private void restoreTheView() {
        int userID = GeneralUtils.getSharedPreferenceInt(getActivity(), AppStringConstants.USERID);
        int adminID = GeneralUtils.getSharedPreferenceInt(getActivity(), AppStringConstants.ADMIN_ID);

        if (userID == adminID || userID ==-1) {
            if (userTypeID == CORPORATE_ADMIN) {
                GetUserRestCall getUserRestCall = new GetUserRestCall(getActivity());
                getUserRestCall.delegate = InitialFragment.this;
                getUserRestCall.callGetUsers();
            } else if (userTypeID == CORPORATE) {
                navigateBasedOnUserType(userTypeID, null);
            }
        } else {
            String selectedUserName = GeneralUtils.getSharedPreferenceString(getActivity(), AppStringConstants.SELECTED_USERNAME);
            textView_heading.setText(selectedUserName);

            FragmentTransaction destinationTransaction = getFragmentManager().beginTransaction();
            destinationTransaction.replace(R.id.frameLayoutInitialContent, DestinationsTabFragment.newInstance());
            destinationTransaction.commit();
        }
    }

    private void navigateBasedOnUserType(int userTypeID, ArrayList<GetUserData> userDatas) {
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
    public void onGetUserSuccess(ArrayList<GetUserData> getUserDatas) {
        getUserDatasList.clear();
        getUserDatasList.addAll(getUserDatas);
        navigateBasedOnUserType(userTypeID, getUserDatas);
    }

    @Override
    public void onGetUserFailure(String message) {
        GeneralUtils.createAlertDialog(getActivity(), message);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SEARCH_USER) {
            if ((resultCode == getActivity().RESULT_OK) && (data.getStringExtra("result").equals("success"))) {
                if (data.getBooleanExtra("allUser", false)) {

                    textView_heading.setText(getActivity().getString(R.string.all));
                    userID = -1;
                } else {
                    userID = data.getIntExtra("userID", -1);
                    if (userID != -1) {
                        textView_heading.setText("" + data.getStringExtra("userName"));
                        GeneralUtils.setSharedPreferenceString(getActivity(),
                                AppStringConstants.SELECTED_USERNAME,
                                data.getStringExtra("userName"));

                    } else {
                        textView_heading.setText(getActivity().getString(R.string.all));
                    }

                    GeneralUtils.setSharedPreferenceInt(getActivity(), AppStringConstants.USERID, userID);

                    DestinationsTabFragment.pagerCurrentItem = 0;
                    DestinationsTabFragment.current_selected_dateTime = "";

                    FragmentTransaction destinationTransaction = getFragmentManager().beginTransaction();
                    destinationTransaction.replace(R.id.frameLayoutInitialContent, DestinationsTabFragment.newInstance());
                    destinationTransaction.commit();
                }
            }

        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        textView_heading.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
        textView_heading.setOnClickListener(null);
        textView_heading = null;
    }
}
