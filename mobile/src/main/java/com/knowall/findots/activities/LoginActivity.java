package com.knowall.findots.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.knowall.findots.FinDotsApplication;
import com.knowall.findots.R;
import com.knowall.findots.locationUtils.Utils;
import com.knowall.findots.restcalls.forgotPassword.ForgotPasswordModel;
import com.knowall.findots.restcalls.forgotPassword.ForgotPasswordRestCall;
import com.knowall.findots.restcalls.forgotPassword.IForgotPasswordRestCall;
import com.knowall.findots.restcalls.login.ILoginRestCall;
import com.knowall.findots.restcalls.login.LoginModel;
import com.knowall.findots.restcalls.login.LoginRestCall;
import com.knowall.findots.utils.AddTextWatcher;
import com.knowall.findots.utils.AppStringConstants;
import com.knowall.findots.utils.GeneralUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

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

        //mEditText_userName.setText("parijathar@bridgetree.com");
        //mEditText_password.setText("pari@123");

        mEditText_userName.setText("vanithaergam405@gmail.com");
        mEditText_password.setText("1234");

        //mEditText_userName.setText("sravan@gmail.com");
        //mEditText_password.setText("12345");

        //mEditText_userName.setText("asingh@bridgetree.com");
        //mEditText_password.setText("Welcome");
    }

    @Override
    protected void onResume() {
        super.onResume();
        FinDotsApplication.getInstance().trackScreenView("Login Screen");
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
        if (loginModel.getErrorCode() == 0) {
            GeneralUtils.setSharedPreferenceString(this, AppStringConstants.USERNAME, userName);
            GeneralUtils.setSharedPreferenceString(this,AppStringConstants.PASSWORD, password);

            if(loginModel.getLoginData().length > 0) {
                GeneralUtils.setSharedPreferenceString(this, AppStringConstants.NAME, loginModel.getLoginData()[0].getName());
                GeneralUtils.setSharedPreferenceInt(this, AppStringConstants.USERID, loginModel.getLoginData()[0].getUserID());
                GeneralUtils.setSharedPreferenceInt(this, AppStringConstants.CORPORATEUSERID, loginModel.getLoginData()[0].getCorporateUserID());
            }

            startMenuActivity();
        } else {
            GeneralUtils.createAlertDialog(LoginActivity.this, loginModel.getMessage());
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

        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(LoginActivity.this);
        View mView = layoutInflaterAndroid.inflate(R.layout.dialog_user_input, null);
        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(LoginActivity.this);
        alertDialogBuilderUserInput.setView(mView);
        alertDialogBuilderUserInput.setTitle(""+getResources().getString(R.string.app_name));


        final EditText userInputDialogEditText = (EditText) mView.findViewById(R.id.userInputDialog);
        userInputDialogEditText.addTextChangedListener(new AddTextWatcher(userInputDialogEditText));

//        TextView userInputDialogTitle = (TextView) mView.findViewById(R.id.dialogTitle);
//        userInputDialogTitle.setText(getResources().getString(R.string.add_destination));
        userInputDialogEditText.setHint(getResources().getString(R.string.emailid));
        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton(""+getResources().getString(R.string.send), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {
                        if(userInputDialogEditText.getText().toString().length()>0) {
                            dialogBox.dismiss();
                            startForgotPasswordProcess(userInputDialogEditText.getText().toString().trim());
                        }
                    }
                })

                .setNegativeButton(""+getResources().getString(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                dialogBox.cancel();
                            }
                        });

        AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
        alertDialogAndroid.show();

    }

//
//        Dialog dialog = new Dialog(LoginActivity.this);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
//        dialog.setContentView(R.layout.dialog_forgot_password);
//        dialog.setCancelable(false);
//
//        Window window = dialog.getWindow();
//        WindowManager.LayoutParams wlp = window.getAttributes();
//        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
//        wlp.gravity = Gravity.CENTER;
//        window.setAttributes(wlp);
//
//        dialog.show();
//
//        setDialogWidgetsFunctionality(dialog);

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
