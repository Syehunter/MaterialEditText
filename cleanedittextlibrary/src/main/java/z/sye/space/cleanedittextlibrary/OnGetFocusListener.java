package z.sye.space.cleanedittextlibrary;

import android.text.Editable;

/**
 * Created by Syehunter on 16/1/11.
 */
public interface OnGetFocusListener {

    void onGetFocus();

    void afterTextChanged(Editable s);

    void onTextChanged(CharSequence s, int start, int before, int count);

    void beforeTextChanged(CharSequence s, int start, int count, int after);
}
