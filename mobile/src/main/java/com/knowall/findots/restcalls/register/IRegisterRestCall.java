package com.knowall.findots.restcalls.register;

/**
 * Created by parijathar on 6/17/2016.
 */
public interface IRegisterRestCall {
    public void onRegisterUserSuccess(RegisterModel registerModel);
    public void onRegisterUserFailure(String errorMessage);
}
