package activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import findots.bridgetree.com.findots.R;
import locationUtils.Utils;
import restcalls.Login.ILoginRestCall;
import restcalls.Login.LoginModel;
import restcalls.Login.LoginRestCall;
import utils.AddTextWatcher;
import utils.GeneralUtils;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity  implements ILoginRestCall{

    @Bind(R.id.EditText_userName)
    EditText mEditText_userName;

    @Bind(R.id.EditText_password)
    EditText mEditText_password;

    @Bind(R.id.Button_login)
    Button mButton_login;

    @Bind(R.id.TextView_forgotPassword)
    TextView mTextView_forgotPassword;

    @Bind(R.id.TextView_donthaveaccount)
    TextView mTextView_donthaveaccount;

    @Bind(R.id.TextView_signup)
    TextView mTextView_signup;

    public static String USERNAME = "username";
    public static String PASSWORD = "password";

    String userName = null, password = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        ButterKnife.bind(this);

        setUIElementsProperty();
        setListeners();
       if(!(Utils.isLocationServiceEnabled(LoginActivity.this))) {
           Utils.createLocationServiceError(LoginActivity.this);
       }

    }

    /**
     *   adding Listeners to UI Widgets
     */
    public void setListeners() {
        mEditText_userName.addTextChangedListener(new AddTextWatcher(mEditText_userName));
        mEditText_password.addTextChangedListener(new AddTextWatcher(mEditText_password));
    }


    /**
     *   Validating the Credentials
     */
    @OnClick(R.id.Button_login)
    public void validateCredentials() {
        userName = mEditText_userName.getText().toString();
        password = mEditText_password.getText().toString();

        if (userName == null || userName.length() == 0) {
            mEditText_userName.setError(getString(R.string.prompt_required));
        } else if (password == null || password.length() == 0) {
            mEditText_password.setError(getString(R.string.prompt_password));
        } else if (!emailValidation(userName)) {
            mEditText_userName.setError(getString(R.string.validate_email));
        } else {
            /**
             *   call Login webservice
             */
            LoginRestCall loginRestCall = new LoginRestCall(LoginActivity.this);
            loginRestCall.delegate = LoginActivity.this;
            loginRestCall.callLoginService(userName, password, "", "", "", "", "", "");
        }

    }


    @Override
    public void onLoginSuccess(LoginModel loginModel) {
        if (loginModel.getErrorCode() == 2) {
            GeneralUtils.createAlertDialog(LoginActivity.this, loginModel.getMessage());
        } else {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
            sp.edit().putString(USERNAME, userName).apply();
            sp.edit().putString(PASSWORD, password).apply();

            startMenuActivity();
        }
    }

    @Override
    public void onLoginFailure() {
        Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
    }

    public boolean emailValidation(String email) {
        if (email.contains("@"))
            return true;
        return false;
    }

    /**
     *   setting UI Widgets Properties
     */
    public void setUIElementsProperty() {
        Typeface typefaceLight = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf");
        Typeface typefaceBold = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Bold.ttf");

        mEditText_userName.setTypeface(typefaceLight);
        mEditText_password.setTypeface(typefaceLight);
        mButton_login.setTypeface(typefaceLight);
        mTextView_forgotPassword.setTypeface(typefaceLight);
        mTextView_donthaveaccount.setTypeface(typefaceLight);
        mTextView_signup.setTypeface(typefaceBold);
    }

    public void startMenuActivity() {
        Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.TextView_forgotPassword)
    public void startForgotPasswordActivity() {

    }

    @OnClick(R.id.TextView_signup)
    public void startSignUpActivity() {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }



}

