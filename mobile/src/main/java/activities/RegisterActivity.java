package activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import findots.bridgetree.com.findots.R;
import utils.AddTextWatcher;

/**
 * Created by parijathar on 6/13/2016.
 */
public class RegisterActivity extends AppCompatActivity {

    @Bind(R.id.EditText_name)
    EditText mEditText_name;

    @Bind(R.id.EditText_company)
    EditText mEditText_company;

    @Bind(R.id.EditText_emailID)
    EditText mEditText_emailID;

    @Bind(R.id.EditText_mobileNo)
    EditText mEditText_mobileNo;

    @Bind(R.id.EditText_password)
    EditText mEditText_password;

    @Bind(R.id.EditText_redeemCode)
    EditText mEditText_redeemCode;

    @Bind(R.id.Switch_onOff)
    Switch mSwitch_onOff;

    @Bind(R.id.TextView_termsCondition)
    TextView mTextView_termsCondition;

    @Bind(R.id.Button_createAccount)
    Button mButton_createAccount;

    String name = null, company = null, emailID = null;
    String mobileNo = null, password = null, redeemCode = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        ButterKnife.bind(this);

        setListeners();

        /**
         * android:thumb="@drawable/selector_switch"
         android:trackTint="@color/app_color_50"
         */
    }

    @OnClick(R.id.Button_createAccount)
    public void validateAndCreateAccount() {

        if (validateEnteredValues()) {
            /**
             *   Call WebService to create an account
             */


        }

    }

    /**
     *   validating the entered values for creating account
     */
    public boolean validateEnteredValues() {
        name = mEditText_name.getText().toString();
        company = mEditText_company.getText().toString();
        emailID = mEditText_emailID.getText().toString();
        mobileNo = mEditText_mobileNo.getText().toString();
        password = mEditText_password.getText().toString();
        redeemCode = mEditText_redeemCode.getText().toString();

        if (name == null || name.length() == 0) {
            mEditText_name.setError(getString(R.string.prompt_required));  return false;
        } else if (company == null || company.length() == 0) {
            mEditText_company.setError(getString(R.string.prompt_required));  return false;
        } else if (emailID == null || emailID.length() == 0) {
            mEditText_emailID.setError(getString(R.string.prompt_required));  return false;
        } else if (mobileNo == null || mobileNo.length() == 0) {
            mEditText_mobileNo.setError(getString(R.string.prompt_required));  return false;
        } else if (password == null || password.length() == 0) {
            mEditText_password.setError(getString(R.string.prompt_password));  return false;
        } else if (redeemCode == null || redeemCode.length() == 0) {
            mEditText_redeemCode.setError(getString(R.string.prompt_required));  return false;
        }

        return true;
    }

    /**
     *   set Listeners to the UI Widgets
     */
    public void setListeners() {
        mEditText_name.addTextChangedListener(new AddTextWatcher());
        mEditText_company.addTextChangedListener(new AddTextWatcher());
        mEditText_emailID.addTextChangedListener(new AddTextWatcher());
        mEditText_mobileNo.addTextChangedListener(new AddTextWatcher());
        mEditText_password.addTextChangedListener(new AddTextWatcher());
        mEditText_redeemCode.addTextChangedListener(new AddTextWatcher());
    }

}
