package fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import findots.bridgetree.com.findots.R;
import restcalls.accountSettings.GetAccountInfoModel;
import restcalls.accountSettings.GetAccountInfoRestCall;
import restcalls.accountSettings.IGetAccountInfoCallBack;

/**
 * Created by jpaulose on 6/22/2016.
 */
public class Account_Settings extends Fragment implements IGetAccountInfoCallBack {

    GetAccountInfoModel accountInfoModel;
    @Bind(R.id.editText_name)
    EditText mEditText_name;

    @Bind(R.id.editText_company)
    EditText mEditText_company;

    @Bind(R.id.editText_emailID)
    EditText mEditText_emailID;

    @Bind(R.id.editText_mobileNo)
    EditText mEditText_mobileNo;


    @Bind(R.id.textView_changePwd)
    TextView mTextView_changePwd;

    @Bind(R.id.button_saveAccountSettings)
    Button mButton_saveAccountSettings;


    public static Account_Settings newInstance() {
        Account_Settings account_Settings = new Account_Settings();
        return account_Settings;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.account_settings_fragment, null);

        ButterKnife.bind(this, rootView);
        setUIElementsProperty();

        GetAccountInfoRestCall accountInfoRestCall = new GetAccountInfoRestCall(getActivity());
        accountInfoRestCall.delegate = Account_Settings.this;
        accountInfoRestCall.callGetAccountInfoService();

        return rootView;
    }

    public void setUIElementsProperty() {
        Typeface typefaceLight = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Light.ttf");

        mEditText_name.setTypeface(typefaceLight);
        mEditText_company.setTypeface(typefaceLight);
        mEditText_emailID.setTypeface(typefaceLight);
        mEditText_mobileNo.setTypeface(typefaceLight);
        mTextView_changePwd.setTypeface(typefaceLight);
        mButton_saveAccountSettings.setTypeface(typefaceLight);

        SpannableString changePwdText = new SpannableString(getActivity().getResources().getString(R.string.changepwd));
        changePwdText.setSpan(new UnderlineSpan(), 0, changePwdText.length(), 0);
        mTextView_changePwd.setText(changePwdText);
        //mTextView_agree.setText(getSpannableString());
        //mTextView_agree.setMovementMethod(LinkMovementMethod.getInstance());

    }

    /**
     * Setting the Account info fetched from server
     */
    public void setAccountInfoText() {
        mEditText_name.setText("" +accountInfoModel.getData().get(0).getName());
        mEditText_company.setText("" +accountInfoModel.getData().get(0).getCompany());
        mEditText_emailID.setText("" +accountInfoModel.getData().get(0).getEmail());
        mEditText_mobileNo.setText("" +accountInfoModel.getData().get(0).getMobileNumber());
    }

    /**
     * validating the entered values for Saving Account Setting
     */
    public boolean validateEnteredValues() {

        if (mEditText_name.getText().toString() == null || mEditText_name.getText().toString().trim().length() == 0) {
            mEditText_name.setError(getString(R.string.prompt_required));
            return false;

        } else if (mEditText_mobileNo.getText().toString() == null || mEditText_mobileNo.getText().toString().trim().length() == 0) {
            mEditText_mobileNo.setError(getString(R.string.prompt_required));
            return false;
        }

        return true;
    }

    @Override
    public void onGetAccountInfoSuccess(GetAccountInfoModel accountInfoModel) {
        this.accountInfoModel = accountInfoModel;
        if(accountInfoModel.getData().size()>0)
            setAccountInfoText();
    }

    @Override
    public void onGetAccountInfoFailure(String errorMessage) {
        Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
    }
}
