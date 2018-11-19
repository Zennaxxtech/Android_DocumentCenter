package com.documentcenterapp.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.documentcenterapp.MainActivity;
import com.documentcenterapp.R;
import com.documentcenterapp.helper.Helper;
import com.documentcenterapp.model.LoginInfo;
import com.documentcenterapp.requestResponse.AndyUtils;
import com.documentcenterapp.requestResponse.ApiCall;
import com.documentcenterapp.requestResponse.ApiUrl;
import com.documentcenterapp.util.Constants;
import com.documentcenterapp.util.TinyDB;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import cz.msebera.android.httpclient.Header;

public class LoginActivity extends AppCompatActivity {

    private Context context = LoginActivity.this;

    private static final String TAG = "[LoginActivity] : ";

    public ImageView profilePicIv, profilePicRememberMeIV;
    public TextView userLblTv, touchIdLblTv;
    public TextView forgotPasswordTv, switchUserTV, welcomeTV, userNameTV;
    public EditText userNameEdt, passwordEdt;
    public Button signInBtn;
    public CheckBox rememberMeChkBox, fingrPrintChkBox;
    public LinearLayout containerLl;
    public RelativeLayout remamberMeRL;

    private ImageView closeIV;
    private TinyDB tinyDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.i(TAG, "onCreate(), called ");
        try {

            tinyDB = new TinyDB(this);

            checkIsUserLogin();

            initViews();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initViews() {

        profilePicIv = (ImageView) findViewById(R.id.imgv_profilepic);
        profilePicRememberMeIV = (ImageView) findViewById(R.id.imgv_profilepic_remember_me);
        userLblTv = (TextView) findViewById(R.id.tv_lbl_user);
        welcomeTV = (TextView) findViewById(R.id.tv_welcome);
        userNameTV = (TextView) findViewById(R.id.tv_user);
        touchIdLblTv = (TextView) findViewById(R.id.tv_lbl_use_touchId);
        forgotPasswordTv = (TextView) findViewById(R.id.tv_forgot_password);
        switchUserTV = (TextView) findViewById(R.id.tv_switch_user);
        userNameEdt = (EditText) findViewById(R.id.edt_username);
        passwordEdt = (EditText) findViewById(R.id.edt_password);
        signInBtn = (Button) findViewById(R.id.btn_signin);
        rememberMeChkBox = (CheckBox) findViewById(R.id.chkbox_remember_me);
        fingrPrintChkBox = (CheckBox) findViewById(R.id.chkbox_fingrprint);
        containerLl = (LinearLayout) findViewById(R.id.ll_container);
        remamberMeRL = (RelativeLayout) findViewById(R.id.rl_remamber_me);
        closeIV = (ImageView) findViewById(R.id.iv_close);

        clickListener();
    }

    private void clickListener() {
        profilePicIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        userLblTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        userLblTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        touchIdLblTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        forgotPasswordTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordFragment.class);
                startActivity(intent);
            }
        });

        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        rememberMeChkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        fingrPrintChkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        switchUserTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        closeIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.bottom_dialog).setVisibility(View.GONE);
            }
        });
    }

    TextView.OnEditorActionListener editDoneLogin = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            Log.i(TAG, "onEditorAction(), actionId : " + actionId);

            if (actionId == EditorInfo.IME_ACTION_DONE ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {

                login();
                return false;
            }

            return false;
        }
    };

    private void login() {
        Log.i(TAG, "login() called ");

        try {
            if (userNameEdt.getText().toString().trim().length() == 0) {
                // Helper.showCustomToast(getResources().getString(R.string.enter_email), getResources().getDrawable(R.drawable.cross), LoginActivity.this);
                userNameEdt.requestFocus();
                return;
            } else if (passwordEdt.getText().toString().trim().length() == 0) {
                // Helper.showCustomToast(getResources().getString(R.string.enter_password), getResources().getDrawable(R.drawable.cross), this);
                passwordEdt.requestFocus();
                return;
            } else {

                if (!Helper.isNetworkAvailable(this)) {
                    //AlertDialogManager.showAlertDialog(this, getString(R.string.txt_alert_C), getString(R.string.no_internet));
                    return;
                } else {
                    RequestParams params = new RequestParams();
                    String password = AndyUtils.encrypt(passwordEdt.getText().toString().trim());
                    String userName = AndyUtils.encrypt(userNameEdt.getText().toString().trim());

                    DateFormat dateFormatter = new SimpleDateFormat("yyyyMMddhhmmss");
                    dateFormatter.setLenient(false);
                    Date today = new Date();
                    String date = dateFormatter.format(today);

                    String Token = AndyUtils.encrypt(date);
                    String AppsName = AndyUtils.encrypt("InnoDoc");

                    String tokenAfterRemveExtra = Helper.removeExtra(Token);
                    String appNameAfterRemoveExtra = Helper.removeExtra(AppsName);
                    String userNameAfterRemoveExtra = Helper.removeExtra(userName);
                    String passwordAfterRemoveExtra = Helper.removeExtra(password);

                    tinyDB.putString(Constants.Preferences.PREF_TOKEN, tokenAfterRemveExtra);
                    tinyDB.putString(Constants.Preferences.PREF_APP_NAME, appNameAfterRemoveExtra);
                    tinyDB.putString(Constants.Preferences.PREF_DECREPTED_USER_NAME, userNameAfterRemoveExtra);

                    String param = "?Token=" + tokenAfterRemveExtra + "&AppsName=" + appNameAfterRemoveExtra + "&UserName=" + userNameAfterRemoveExtra + "&Password=" + passwordAfterRemoveExtra;
                    Log.d("Lodin ", "param : " + param);

                    new ApiCall(LoginActivity.this).firePost(true, ApiUrl.LOGIN + param, params, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            String res = new String(responseBody);
                            Log.d("Lodin ", "responseBody : " + res);
                            try {
                                JSONObject jsonObject = new JSONObject(res);
                                if (jsonObject.getString("Status").equals("1")) {
                                    LoginInfo loginInfo = new Gson().fromJson(res, LoginInfo.class);

                                    String s = loginInfo.getData();
                                    s.replace("[", "");
                                    s.replace("]", "");
                                    String data = AndyUtils.decrypt(s);
                                    JSONObject jsonObject1 = new JSONObject(data);

                                    String UserName = jsonObject1.getString("UserName");
                                    String LoginType = jsonObject1.getString("LoginType");
                                    String Active = jsonObject1.getString("Active");

                                    tinyDB.putString(Constants.Preferences.PREF_USER_NAME, UserName);
                                    tinyDB.putBoolean(Constants.Preferences.PREF_LOGIN_DONE, true);
                                    tinyDB.putString(Constants.Preferences.PREF_USER_PASSWORD, passwordEdt.getText().toString().trim());
                                    tinyDB.putString(Constants.Preferences.PREF_LOGIN_TYPE, LoginType);

                                    checkIsUserLogin();

                                } else {
                                    Toast.makeText(LoginActivity.this, "" + jsonObject.getString("Msg"), Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
//                    dismissProgressDialog();
                        }
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkIsUserLogin() {
        boolean isLoginDone = tinyDB.getBoolean(Constants.Preferences.PREF_LOGIN_DONE);
        Log.i(TAG, "onCreate(), isLoginDone : " + isLoginDone);
        Intent intent = null;
        if (isLoginDone) {
            intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        //finish();
    }

    @Override
    public void onBackPressed() {
        Log.i(TAG, "onBackPressed() called");
        super.onBackPressed();
    }
}

