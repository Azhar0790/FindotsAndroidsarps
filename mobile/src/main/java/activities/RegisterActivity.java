package activities;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import findots.bridgetree.com.findots.R;
import utils.AddTextWatcher;

/**
 * Created by parijathar on 6/13/2016.
 */
public class RegisterActivity extends AppCompatActivity {

    @Bind(R.id.TextView_signUpHeading)
    TextView mTextView_signUpHeading;

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

    @Bind(R.id.CheckBox_onOff)
    ImageView mCheckBox_onOff;

    @Bind(R.id.TextView_agree)
    TextView mTextView_agree;

    @Bind(R.id.Button_createAccount)
    Button mButton_createAccount;

    String name = null, company = null, emailID = null;
    String mobileNo = null, password = null, redeemCode = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        ButterKnife.bind(this);

        setUIElementsProperty();
        setListeners();

        /**
         * android:thumb="@drawable/selector_switch"
         android:trackTint="@color/app_color_50"
         */
    }


    public void setUIElementsProperty() {
        Typeface typefaceMyriadHebrew = Typeface.createFromAsset(getAssets(), "fonts/MyriadHebrew-Bold.otf");
        Typeface typefaceLight = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf");

        mTextView_signUpHeading.setTypeface(typefaceMyriadHebrew);
        mEditText_name.setTypeface(typefaceLight);
        mEditText_company.setTypeface(typefaceLight);
        mEditText_emailID.setTypeface(typefaceLight);
        mEditText_mobileNo.setTypeface(typefaceLight);
        mEditText_password.setTypeface(typefaceLight);
        mEditText_redeemCode.setTypeface(typefaceLight);
        mTextView_agree.setTypeface(typefaceLight);
        mButton_createAccount.setTypeface(typefaceLight);

        //mTextView_agree.setText(getSpannableString());
        //mTextView_agree.setMovementMethod(LinkMovementMethod.getInstance());

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
        mEditText_name.addTextChangedListener(new AddTextWatcher(mEditText_name));
        mEditText_company.addTextChangedListener(new AddTextWatcher(mEditText_company));
        mEditText_emailID.addTextChangedListener(new AddTextWatcher(mEditText_emailID));
        mEditText_mobileNo.addTextChangedListener(new AddTextWatcher(mEditText_mobileNo));
        mEditText_password.addTextChangedListener(new AddTextWatcher(mEditText_password));
        mEditText_redeemCode.addTextChangedListener(new AddTextWatcher(mEditText_redeemCode));
    }


    public SpannableString getSpannableString() {

        String str = getString(R.string.agree);

        SpannableString spannableString = new SpannableString(str);
        //spannableString.setSpan(new RelativeSizeSpan(1.1f), 0, str.length(), 0);
        spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.black_70)), 0, 12, 0);
        //spannableString.setSpan(new UnderlineSpan(), 13, str.length(), 0);
        spannableString.setSpan(new ForegroundColorSpan(Color.RED), 13, str.length(), 0);

        // clickable text
        ClickableSpan clickableSpan = new ClickableSpan() {

            @Override
            public void onClick(View widget) {
                // We display a Toast. You could do anything you want here.
                Toast.makeText(RegisterActivity.this, "Clicked", Toast.LENGTH_SHORT).show();

            }
        };

        spannableString.setSpan(clickableSpan, 13, str.length(), 0);

        return spannableString;
    }
}
