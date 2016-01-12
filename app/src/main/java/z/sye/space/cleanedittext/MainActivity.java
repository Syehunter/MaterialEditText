package z.sye.space.cleanedittext;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import z.sye.space.library.MaterialEditText;
import z.sye.space.library.listeners.OnErrorListener;
import z.sye.space.library.listeners.OnGetFocusListener;
import z.sye.space.library.listeners.OnLostFocusListener;

public class MainActivity extends AppCompatActivity {

    private MaterialEditText mUsername;
    private MaterialEditText mPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mUsername = (MaterialEditText) findViewById(R.id.username_material);
        mPassword = (MaterialEditText) findViewById(R.id.password_material);

        mUsername.inputType(InputType.TYPE_CLASS_TEXT);


        mPassword.inputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT)
                .maxLength(18)
                .setOnGetFocusListener(mOnGetFocusListener)
                .setOnLostFocusListener(mOnLostFocusListener)
                .setOnErrorListener(mOnErrorListener);

    }

    private OnGetFocusListener mOnGetFocusListener = new OnGetFocusListener() {
        @Override
        public void onGetFocus() {
        }

        @Override
        public void afterTextChanged(Editable s) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }
    };

    private OnLostFocusListener mOnLostFocusListener = new OnLostFocusListener() {
        @Override
        public void onLostFocus() {
        }
    };

    private OnErrorListener mOnErrorListener = new OnErrorListener() {
        @Override
        public boolean onError(CharSequence input) {
            return !isPassword(input.toString());
        }
    };


    /**
     * Required 6-18 words, Contain upper and lower case letters, numbers or a combination of at least two special symbols
     * @param password
     * @return
     */
    public boolean isPassword(String password) {
        if (TextUtils.isEmpty(password)) {
            mPassword.error("Password cannot be null !");
            return false;
        }
        String pattern = "(?!^(\\d+|[a-zA-Z]+|[!%&@#$^_.]+)$)^[\\w!%&@#$^_.]{6,18}+$";
        if (password.matches(pattern)) {
            return true;
        } else {
            mPassword.error("Illegal Password Input !");
            return false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
