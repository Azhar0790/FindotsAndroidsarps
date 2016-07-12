package com.knowall.findots.restcalls.accountSettings;



/**
 * Created by jpaulose on 6/23/2016.
 */
public interface IGetAccountInfoCallBack
{
    public void onGetAccountInfoSuccess(GetAccountInfoModel accountInfoModel);
    public void onGetAccountInfoFailure(String errorMessage);
}
