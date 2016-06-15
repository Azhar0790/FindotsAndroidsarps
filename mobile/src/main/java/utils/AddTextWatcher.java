package utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

/**
 * Created by parijathar on 6/13/2016.
 */
public class AddTextWatcher implements TextWatcher {

    EditText e = null;

    public AddTextWatcher(EditText e) {
        this.e = e;
    }

    @Override
    public void afterTextChanged(Editable s) {
        e.setError(null);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

}
