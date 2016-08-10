package com.knowall.findots.restcalls.getUser;

/**
 * Created by parijathar on 8/9/2016.
 */
public interface IGetUser {
    public void onGetUserSuccess(GetUserData[] getUserDatas);
    public void onGetUserFailure(String message);
}
