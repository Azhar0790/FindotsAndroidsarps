package fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import findots.bridgetree.com.findots.R;

/**
 * Created by jpaulose on 6/22/2016.
 */
public class Account_Settings extends Fragment
{
    public static Account_Settings newInstance() {
        Account_Settings account_Settings = new Account_Settings();
        return account_Settings;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.account_settings_fragment, null);

        ButterKnife.bind(this, rootView);

        return rootView;
    }
}
