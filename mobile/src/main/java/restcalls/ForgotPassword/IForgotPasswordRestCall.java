package restcalls.ForgotPassword;

/**
 * Created by parijathar on 6/17/2016.
 */
public interface IForgotPasswordRestCall {
    public void onForgotPasswordSuccess(ForgotPasswordModel forgotPasswordModel);
    public void onForgotPasswordFailure(String errorMessage);
}
