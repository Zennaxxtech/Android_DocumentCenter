package com.documentcenterapp.setting;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.documentcenterapp.R;
import com.documentcenterapp.util.Constants;
import com.documentcenterapp.util.TinyDB;

@SuppressLint("LongLogTag")
public class SelectLanguageActivity extends AppCompatActivity{

    private static final String TAG = "[SelectLanguageActivity] : ";

    private Context context = SelectLanguageActivity.this;

    private ImageView iv_check_english, iv_check_english_australia, iv_check_chinese;

    private LinearLayout ll_chinese, ll_english, ll_english_australia;

    private ImageView iv_back;
    private TextView tv_done;

    private TinyDB tinyDB;
    private String selectedLanguage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_select_language_layout);

        Log.i(TAG, "onCreate() called ");

        try {

            tinyDB = new TinyDB(this);
            selectedLanguage = tinyDB.getString(Constants.Preferences.PREF_LANGUAGE_SELECT);

            tv_done = (TextView)findViewById(R.id.tv_done);
            tv_done.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

            iv_back = (ImageView)findViewById(R.id.iv_back);
            iv_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

            iv_check_english = (ImageView)findViewById(R.id.iv_check_english);
            iv_check_english_australia = (ImageView)findViewById(R.id.iv_check_english_australia);
            iv_check_chinese = (ImageView)findViewById(R.id.iv_check_chinese);

            ll_chinese = (LinearLayout)findViewById(R.id.ll_chinese);
            ll_english = (LinearLayout)findViewById(R.id.ll_english);
            ll_english_australia = (LinearLayout)findViewById(R.id.ll_english_australia);

            if(selectedLanguage.equals(context.getResources().getString(R.string.chinese))){
                iv_check_chinese.setVisibility(View.VISIBLE);
            }else if(selectedLanguage.equals(context.getResources().getString(R.string.english))){
                iv_check_english.setVisibility(View.VISIBLE);
            }else{
                iv_check_english_australia.setVisibility(View.VISIBLE);
            }

            ll_chinese.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    iv_check_english.setVisibility(View.GONE);
                    iv_check_english_australia.setVisibility(View.GONE);
                    iv_check_chinese.setVisibility(View.VISIBLE);

                    tinyDB.putString(Constants.Preferences.PREF_LANGUAGE_SELECT, context.getResources().getString(R.string.chinese));
                }
            });

            ll_english.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    iv_check_english.setVisibility(View.VISIBLE);
                    iv_check_english_australia.setVisibility(View.GONE);
                    iv_check_chinese.setVisibility(View.GONE);

                    tinyDB.putString(Constants.Preferences.PREF_LANGUAGE_SELECT, context.getResources().getString(R.string.english));
                }
            });

            ll_english_australia.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    iv_check_english.setVisibility(View.GONE);
                    iv_check_english_australia.setVisibility(View.VISIBLE);
                    iv_check_chinese.setVisibility(View.GONE);

                    tinyDB.putString(Constants.Preferences.PREF_LANGUAGE_SELECT, context.getResources().getString(R.string.english_australia));
                }
            });

            /*
             * code for hiding keyboard for focus on username
             */
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        Log.i(TAG, "onBackPressed() called ");
        super.onBackPressed();
    }
}