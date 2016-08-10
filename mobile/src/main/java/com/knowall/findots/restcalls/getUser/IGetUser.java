package com.knowall.findots.restcalls.getUser;

import java.util.ArrayList;

/**
 * Created by parijathar on 8/9/2016.
 */
public interface IGetUser {
    public void onGetUserSuccess(ArrayList<GetUserData> getUserDatas);
    public void onGetUserFailure(String message);
}
