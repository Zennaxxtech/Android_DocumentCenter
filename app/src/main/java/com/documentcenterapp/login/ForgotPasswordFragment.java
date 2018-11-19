package com.documentcenterapp.login;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.documentcenterapp.R;

@SuppressLint("LongLogTag")
public class ForgotPasswordFragment extends AppCompatActivity implements View.OnClickListener {

    private Context context = ForgotPasswordFragment.this;

    private static final String TAG = "[ForgotPasswordFragment] : ";

    private EditText emailET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_forgot_password);
        Log.i(TAG, "onCreate(), called ");
        try {

            initView();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void initView() {
        emailET = (EditText) findViewById(R.id.edt_username);
        emailET.setOnEditorActionListener(editDoneLogin);
        findViewById(R.id.button_submit).setOnClickListener(this);
        findViewById(R.id.ivBack).setOnClickListener(this);
    }

    TextView.OnEditorActionListener editDoneLogin = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            Log.i(TAG, "onEditorAction(), actionId : " + actionId);

            if (actionId == EditorInfo.IME_ACTION_DONE ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {

                //login();
                return false;
            }

            return false;
        }
    };

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.button_submit) {

        }
        if (i == R.id.ivBack) {
            this.getSupportFragmentManager().popBackStack();
        }
    }
}
