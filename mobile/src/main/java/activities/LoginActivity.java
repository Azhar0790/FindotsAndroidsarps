package activities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import findots.bridgetree.com.findots.R;
import locationUtils.Utils;
import restcalls.forgotPassword.ForgotPasswordModel;
import restcalls.forgotPassword.ForgotPasswordRestCall;
import restcalls.forgotPassword.IForgotPasswordRestCall;
import restcalls.login.ILoginRestCall;
import restcalls.login.LoginModel;
import restcalls.login.LoginRestCall;
import utils.AddTextWatcher;
import utils.AppStringConstants;
import utils.GeneralUtils;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity  implements ILoginRestCall, IForgotPasswordRestCall{

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

    EditText mEditText_FrgtPswdUsername = null;

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

        mEditText_userName.setText("parijathar@bridgetree.com");
        mEditText_password.setText("pari@123");

        mEditText_userName.setText("vanithaergam405@gmail.com");
        mEditText_password.setText("vani@1234");
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
        } else if (!GeneralUtils.isvalid_email(userName)) {
            mEditText_userName.setError(getString(R.string.validate_email));
        } else {
            /**
             *   call Login webservice
             */
            LoginRestCall loginRestCall = new LoginRestCall(LoginActivity.this);
            loginRestCall.delegate = LoginActivity.this;
            loginRestCall.callLoginService(userName, password);
        }

    }


    @Override
    public void onLoginSuccess(LoginModel loginModel) {
        if (loginModel.getErrorCode() == 2) {
            GeneralUtils.createAlertDialog(LoginActivity.this, loginModel.getMessage());
        } else {

            GeneralUtils.setSharedPreferenceString(this, AppStringConstants.USERNAME, userName);
            GeneralUtils.setSharedPreferenceString(this,AppStringConstants.PASSWORD, password);

            if(loginModel.getLoginData().length>0) {
                GeneralUtils.setSharedPreferenceInt(this, AppStringConstants.USERID, loginModel.getLoginData()[0].getUserID());
                GeneralUtils.setSharedPreferenceInt(this, AppStringConstants.CORPORATEUSERID, loginModel.getLoginData()[0].getCorporateUserID());

                Log.d("jomy", "sP Val : " + GeneralUtils.getSharedPreferenceInt(this, AppStringConstants.USERID));
            }
            startMenuActivity();
        }
    }

    @Override
    public void onLoginFailure(String errorMessage) {
        Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
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
    public void showForgotPasswordDialog() {

        Dialog dialog = new Dialog(LoginActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(R.layout.dialog_forgot_password);
        dialog.setCancelable(false);

        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        wlp.gravity = Gravity.CENTER;
        window.setAttributes(wlp);

        dialog.show();

        setDialogWidgetsFunctionality(dialog);
    }

    public void setDialogWidgetsFunctionality(final Dialog dialog) {
        mEditText_FrgtPswdUsername = (EditText) dialog.findViewById(R.id.EditText_FrgtPswdUsername);
        mEditText_FrgtPswdUsername.addTextChangedListener(new AddTextWatcher(mEditText_FrgtPswdUsername));
        Button mButton_cancel = (Button) dialog.findViewById(R.id.Button_cancel);
        Button mButton_send = (Button) dialog.findViewById(R.id.Button_send);

        mButton_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        mButton_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = mEditText_FrgtPswdUsername.getText().toString();
                if (username == null || username.length() == 0) {
                    mEditText_FrgtPswdUsername.setError(getString(R.string.prompt_required));
                } else {
                    dialog.dismiss();
                    startForgotPasswordProcess(username);
                }
            }
        });
    }

    /**
     *  Call Forgot Password API
     */
    public void startForgotPasswordProcess(String username) {
        ForgotPasswordRestCall forgotPasswordRestCall = new ForgotPasswordRestCall(LoginActivity.this);
        forgotPasswordRestCall.delegate = LoginActivity.this;
        forgotPasswordRestCall.callForgotPasswordService(username);
    }

    @Override
    public void onForgotPasswordFailure(String errorMessage) {
        Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onForgotPasswordSuccess(ForgotPasswordModel forgotPasswordModel) {
        Toast.makeText(LoginActivity.this, forgotPasswordModel.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.TextView_signup)
    public void startSignUpActivity() {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
        finish();
    }

}

