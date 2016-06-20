package restcalls.Login;

/**
 * Created by parijathar on 6/17/2016.
 */
public interface ILoginRestCall {

    public void onLoginSuccess(LoginModel loginModel);
    public void onLoginFailure(String errorMessage);

}
