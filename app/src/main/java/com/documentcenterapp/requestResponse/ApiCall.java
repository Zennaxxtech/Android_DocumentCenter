package com.documentcenterapp.requestResponse;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;

import com.documentcenterapp.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import cz.msebera.android.httpclient.Header;


public class ApiCall {

    private Context context;
    private AlertDialog.Builder builder;
    private AlertDialog dialog;

    public static String BASE_URL ;


    public ApiCall(Context mContext) {
        context = mContext;

    }

    public void firePostWithDefaultParameter(boolean isLoging, final String url, RequestParams requestParams, final AsyncHttpResponseHandler asyncHttpResponseHandler) {
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.setTimeout(150000);
        asyncHttpClient.setMaxRetriesAndTimeout(3, 150000);
        asyncHttpClient.setConnectTimeout(150000);

        asyncHttpClient.setUserAgent(System.getProperty("http.client"));
//        asyncHttpClient.addHeader("Accept", "application/json");
        //showProgressDialog();

        //requestParams.add("AppsVersionNo", BuildConfig.VERSION_NAME);
        //requestParams.add("SiteType", "innoways-production");

        if (isLoging) {
           // requestParams.add("SiteType", "innoways-production");
            //requestParams.add("projectName", /*"atoms-dev"*/BuildConfig.COMPANYNAME + "-" + BuildConfig.ENVIRONMENT);
        } else {
           // requestParams.add("SiteType", "innoways-production");
            //requestParams.add("projectName", /*"atoms-dev"*/BuildConfig.COMPANYNAME + "-" + BuildConfig.ENVIRONMENT);
        }

        Log.d("APIcall", "url :" + url);

        asyncHttpClient.post(url, requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String res = new String(responseBody);
                //dismissProgressDialog();
                Log.d("APIcall", "onSuccess res :" + url + "\n" + res);
                asyncHttpResponseHandler.onSuccess(statusCode, headers, responseBody);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d("APIcall", "onFailure res :" + url + "\n" + error.getMessage());
                //dismissProgressDialog();
                asyncHttpResponseHandler.onFailure(statusCode, headers, responseBody, error);
            }
        });

    }

    public void firePost(boolean isLoging, final String url, RequestParams requestParams, final AsyncHttpResponseHandler asyncHttpResponseHandler) {
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.setTimeout(150000);
        asyncHttpClient.setMaxRetriesAndTimeout(3, 150000);
        asyncHttpClient.setConnectTimeout(150000);

        asyncHttpClient.setUserAgent(System.getProperty("http.client"));
        Log.d("APIcall", "url :" + url);

        asyncHttpClient.post(url, requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String res = new String(responseBody);
                //dismissProgressDialog();
                Log.d("APIcall", "onSuccess res :" + url + "\n" + res);
                asyncHttpResponseHandler.onSuccess(statusCode, headers, responseBody);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d("APIcall", "onFailure res :" + url + "\n" + error.getMessage());
                //dismissProgressDialog();
                asyncHttpResponseHandler.onFailure(statusCode, headers, responseBody, error);
            }
        });

    }

  /*  public void showProgressDialog() {
        builder = new AlertDialog.Builder(context, R.style.TransparentDialog);
        builder.setView(LayoutInflater.from(context).inflate(R.layout.dialog_custom_progress, null));
        dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    public void dismissProgressDialog() {
        if ((builder != null)) {
            try {
                dialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }*/
}