package utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

/**
 * Created by parijathar on 6/13/2016.
 */
public class AddTextWatcher implements TextWatcher {

    @Override
    public void afterTextChanged(Editable s) {
        EditText e = (EditText) s;
        e.setError(null);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

}
