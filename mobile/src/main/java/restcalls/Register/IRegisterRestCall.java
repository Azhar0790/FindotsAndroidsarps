package restcalls.Register;

/**
 * Created by parijathar on 6/17/2016.
 */
public interface IRegisterRestCall {
    public void onRegisterUserSucess(RegisterModel registerModel);
    public void onRegisterUserFailure();
}
