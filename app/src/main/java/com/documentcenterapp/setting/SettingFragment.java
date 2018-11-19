package com.documentcenterapp.setting;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.documentcenterapp.R;
import com.documentcenterapp.util.Constants;
import com.documentcenterapp.util.TinyDB;

@SuppressLint("LongLogTag")
public class SettingFragment extends Fragment {

    private static final String TAG = "[SettingFragment] : ";

    private Context context;

    private LayoutInflater inflater;

    boolean isToggleOnOrNot = false;

    private com.zcw.togglebutton.ToggleButton toggle_button;

    private ImageView iv_select_language;
    private String selectedLanguage;
    private TextView tv_selected_language;

    private TinyDB tinyDB;

    public SettingFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.i(TAG, "onActivityCreated() called ");

        tinyDB = new TinyDB(getActivity());
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        Log.i(TAG, "onResume() called ");

        try {
            selectedLanguage = tinyDB.getString(Constants.Preferences.PREF_LANGUAGE_SELECT);
            Log.i(TAG, "onCreateView() called for selectedLanguage :" + selectedLanguage);
            isToggleOnOrNot = tinyDB.getBoolean(Constants.Preferences.PREF_TOGGLE);
            Log.i(TAG, "onCreateView() called for isToggleOnOrNot :" + isToggleOnOrNot);
            tv_selected_language.setText(selectedLanguage);

            if(isToggleOnOrNot){
                toggle_button.setToggleOn();
            }else{
                toggle_button.setToggleOff();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.setting_fragment_layout, container, false);

        context = getActivity();
        this.inflater = inflater;

       /* selectedLanguage = tinyDB.getString(Constants.Preferences.PREF_LANGUAGE_SELECT);
        Log.i(TAG, "onCreateView() called for selectedLanguage" + selectedLanguage);*/

        toggle_button = (com.zcw.togglebutton.ToggleButton) rootView.findViewById(R.id.toggle_button);

        toggle_button.setOnToggleChanged(new com.zcw.togglebutton.ToggleButton.OnToggleChanged() {
            @Override
            public void onToggle(boolean on) {
                if(on){
                    tinyDB.putBoolean(Constants.Preferences.PREF_TOGGLE, true);
                }else{
                    tinyDB.putBoolean(Constants.Preferences.PREF_TOGGLE, false);
                }
            }
        });

        iv_select_language = (ImageView)rootView.findViewById(R.id.iv_select_language);
        iv_select_language.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, SelectLanguageActivity.class);
                startActivity(intent);
            }
        });

        tv_selected_language = (TextView)rootView.findViewById(R.id.tv_selected_language);
        tv_selected_language.setText(selectedLanguage);

        return rootView;
    }
}