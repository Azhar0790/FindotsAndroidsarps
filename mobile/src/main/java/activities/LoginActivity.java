package activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentResolver;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import findots.bridgetree.com.findots.R;
import utils.AddTextWatcher;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity  {

    @Bind(R.id.EditText_userName)
    EditText mEditText_userName;

    @Bind(R.id.EditText_password)
    EditText mEditText_password;

    @Bind(R.id.Button_login)
    Button mButton_login;

    @Bind(R.id.TextView_forgotPassword)
    TextView mTextView_forgotPassword;

    @Bind(R.id.TextView_signup)
    TextView mTextView_signup;

    String userName = null, password = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        ButterKnife.bind(this);

        setListeners();


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
    public void validateCredentials() {

        userName = mEditText_userName.getText().toString();
        password = mEditText_password.getText().toString();

        if (userName == null || userName.length() == 0) {
            mEditText_userName.setError(getString(R.string.prompt_required));
        } else if (password == null || password.length() == 0) {
            mEditText_password.setError(getString(R.string.prompt_password));
        }

    }

    /**
     *   setting UI Widgets Properties
     */
    public void setUIElementsProperty() {

    }

    @OnClick(R.id.Button_login)
    public void startMenuActivity() {

        validateCredentials();

        Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
        startActivity(intent);
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

