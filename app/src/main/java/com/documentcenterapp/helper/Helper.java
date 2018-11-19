package com.documentcenterapp.helper;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.documentcenterapp.R;
import com.documentcenterapp.util.AnimateFirstDisplayListener;
import com.documentcenterapp.util.Constants;
import com.documentcenterapp.util.ImageLazyLoadingConfig;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.TreeMap;


public class Helper {

    private static final String TAG = "[Helper] : ";

    public static String KEY = "temp";
    public static final String EXTRA_NAME = "name";
    public static final String EXTRA_URL = "url";
    public static final String EXTRA_ID = "id";
    public static final String EXTRA_PARAMETER_ID = "paramid";
    public static final String EXTRA_LOGIN_VARIABLE = "is_login_before";


    private static ProgressDialog mProgressDialog;
    private ProgressDialog progressDialog;

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    public static final SimpleDateFormat DATE_FORMAT_WITH_TIME = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final SimpleDateFormat DATE_FORMAT_WITH_TIME_ONLY = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    public static final SimpleDateFormat DATE_FORMAT_DISPLAY = new SimpleDateFormat("EEE, MMM dd, yyyy");
    public static final SimpleDateFormat DATE_FORMAT_WITH_DD_MMMM_YYYY = new SimpleDateFormat("dd MMMM, yyyy");


    public static Calendar parceDate(String date, SimpleDateFormat simpleDateFormat) {
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(simpleDateFormat.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return calendar;
    }

    public static Calendar parceDate(String date) {
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(DATE_FORMAT_WITH_TIME.parse(date));
            return calendar;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Calendar parceDateWithTimeOnly(String date) {
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(DATE_FORMAT_WITH_TIME_ONLY.parse(date));
            return calendar;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String parceDateFormat(Calendar calendar) {
        String parceDateFormat = null;
        try {
            parceDateFormat = DATE_FORMAT_WITH_DD_MMMM_YYYY.format(calendar.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return parceDateFormat;
    }


    public static String parceDate(Calendar calendar, SimpleDateFormat simpleDateFormat) {
        String dateStr = null;
        try {
            dateStr = simpleDateFormat.format(calendar.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dateStr;
    }

    public static void popToast(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    public static void popToastLong(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
    }

    public static void showToast(String msg, Context ctx) {
        Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
    }

    public static void showLongToast(String msg, Context ctx) {
        Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show();
    }

    private static TreeMap<String, Boolean> userPrivilegesMap = new TreeMap<String, Boolean>();

    public static TreeMap<String, Boolean> getUserPrivilegesMap(){
        Log.i(TAG, "getUserPrivilegesMap(), userPrivilegesMap : " + userPrivilegesMap);
        return userPrivilegesMap;
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    public static boolean isNetworkAvailable(Activity activity) {
        ConnectivityManager connectivity = (ConnectivityManager) activity
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            return false;
        } else {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean haveNetworkConnection(Activity activity) {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    public static String generateRandomString(String CHAR_LIST, int RANDOM_STRING_LENGTH) {
        Log.i(TAG, "generateRandomString(String, int) called for CHAR_LIST : " + CHAR_LIST + ", RANDOM_STRING_LENGTH : " + RANDOM_STRING_LENGTH);
        StringBuffer randStr = new StringBuffer();
        try {
            for (int i = 0; i < RANDOM_STRING_LENGTH; i++) {
                int number = getRandomNumber(CHAR_LIST.length());
                char ch = CHAR_LIST.charAt(number);
                randStr.append(ch);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i(TAG, "generateRandomString(), randStr.toString() : " + randStr.toString());
        return randStr.toString();
    }

    /**
     * * This method generates random numbers
     *
     * @return int
     */
    private static int getRandomNumber(int characterListLength) {
        int randomInt = 0;
        Random randomGenerator = new Random();
        randomInt = randomGenerator.nextInt(characterListLength);
        if (randomInt - 1 == -1) {
            return randomInt;
        } else {
            return randomInt - 1;
        }
    }

    public static void showNetworkDialog(final Context context) {
        //android.R.style.Theme_Holo
        Builder alertBuilder = new Builder(context);
        alertBuilder.setTitle("Enable Connectivity");
        alertBuilder
                .setMessage("Enable your Wifi/3G connectivity to move forward ...");
        alertBuilder.setPositiveButton("Enable Wifi",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        context.startActivity(new Intent(
                                Settings.ACTION_WIFI_SETTINGS));
                    }
                });
        alertBuilder.setNeutralButton("Enable 3G",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_DATA_ROAMING_SETTINGS);
                        ComponentName cName = new ComponentName(
                                "com.android.phone",
                                "com.android.phone.Settings");
                        intent.setComponent(cName);
                        context.startActivity(intent);
                    }
                });
        alertBuilder.setNegativeButton("Exit", null);
        alertBuilder.setCancelable(false);
        alertBuilder.create().show();
    }

    public void showProgressDialog(Context context, String title, String msg, boolean isCancelable) {
        try {
            if (progressDialog == null) {
                progressDialog = ProgressDialog.show(context, title, msg);
                progressDialog.setCancelable(isCancelable);
            }

            if (!progressDialog.isShowing()) {
                progressDialog.show();
            }

        } catch (IllegalArgumentException ie) {
            ie.printStackTrace();
        } catch (RuntimeException re) {
            re.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void removeSimpleProgressDialog() {
        try {
            if (mProgressDialog != null) {
                if (mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                    mProgressDialog = null;
                }
            }
        } catch (IllegalArgumentException ie) {
            ie.printStackTrace();

        } catch (RuntimeException re) {
            re.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void removeProgressDialog() {
        try {
            if (progressDialog != null) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                    progressDialog = null;
                }
            }
        } catch (IllegalArgumentException ie) {
            ie.printStackTrace();
        } catch (RuntimeException re) {
            re.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void showNetworkErrorMessage(final Context context) {
        Builder dlg = new Builder(context);
        dlg.setCancelable(false);
        dlg.setTitle("Error");
        dlg.setMessage("Network error has occured. Please check the network status of your phone and retry");
        dlg.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dlg.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                ((Activity) context).finish();
                System.exit(0);
            }
        });
        dlg.show();
    }

    public static void showOkDialog(String title, String msg, final Activity activity) {
        Builder dialog = new Builder(activity);
        if (title != null) {

            TextView dialogTitle = new TextView(activity);
            dialogTitle.setText(title);
            dialogTitle.setPadding(10, 10, 10, 10);
            dialogTitle.setGravity(Gravity.CENTER);
            dialogTitle.setTextColor(Color.BLACK);
            dialogTitle.setTextSize(20);
            dialog.setCustomTitle(dialogTitle);

        }
        if (msg != null) {
            dialog.setMessage(msg);
        }
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                dialog.dismiss();
            }
        });
        AlertDialog dlg = dialog.show();
        TextView messageText = (TextView) dlg
                .findViewById(android.R.id.message);
        messageText.setGravity(Gravity.CENTER);

    }

    public static void showOkDialogAndExit(String title, String msg, final Activity activity) {
        Builder dialog = new Builder(activity);
        if (title != null) {

            TextView dialogTitle = new TextView(activity);
            dialogTitle.setText(title);
            dialogTitle.setPadding(10, 10, 10, 10);
            dialogTitle.setGravity(Gravity.CENTER);
            dialogTitle.setTextColor(Color.BLACK);
            dialogTitle.setTextSize(20);
            dialog.setCustomTitle(dialogTitle);

        }
        if (msg != null) {
            dialog.setMessage(msg);
        }
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                dialog.dismiss();
                activity.finish();
            }
        });
        AlertDialog dlg = dialog.show();
        TextView messageText = (TextView) dlg
                .findViewById(android.R.id.message);
        messageText.setGravity(Gravity.CENTER);

    }

    public static void selectSpinnerItemByValue(Spinner spinner, String value){
        Log.i(TAG, "selectSpinnerItemByValue(Spinner, String) called for spinner : " + spinner + ", value : "+value);
        try{
            if(value != null && !("").equals(value)){
                for (int i=0; i<spinner.getCount(); i++){
                    //Log.i(TAG, "selectSpinnerItemByValue(), spinner.getItemAtPosition("+i+") : " + spinner.getItemAtPosition(i));
                    if((spinner.getItemAtPosition(i)).equals(value)){
                        spinner.setSelection(i);
                        return;
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static String getQueryString(String strValue){
        try{
            strValue = "'" + strValue + "'";
        }catch(Exception e){
            e.printStackTrace();
        }
        return strValue;
    }

    /*
     * To convert pixel value into dp use the following code:
     */
    public static float convertPixelsToDp(float px,Context context){
        Log.i(TAG, "convertPixelsToDp(float, Context) called for px : " + px);
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        Log.i(TAG, "convertPixelsToDp(), dp : " + dp);
        return dp;
    }

    /*
     * To convert dp value into pixel use the following code:
     */
    public static float convertDpToPixel(float dp,Context context){
        Log.i(TAG, "convertDpToPixel(float, Context) called for dp : " + dp);
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        Log.i(TAG, "convertDpToPixel(), px : " + px);
        return px;
    }

    public static String getDayOfMonthSuffix(int n) {
        String[] TH_SUFFIX = ",st,nd,rd,th,th,th,th,th,th,th,th,th,th,th,th,th,th,th,th,th,st,nd,rd,th,th,th,th,th,th,th,st".split(",");
        return TH_SUFFIX[n];
    }

    public static void showCustomSnackBar(Context context, String message, int duration, int textColor, int backgroundColor) {
        try{
            View rootView = ((Activity)context).getWindow().getDecorView().findViewById(android.R.id.content);
            Snackbar snackbar = Snackbar.make(rootView, message, duration);
            View snackBarView = snackbar.getView();
            if(textColor > 0){
                snackbar.setActionTextColor(context.getResources().getColor(textColor));
            }
            if(backgroundColor > 0){
                snackBarView.setBackgroundColor(context.getResources().getColor(backgroundColor));
            }

            TextView mTextView = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text);
            mTextView.setTextSize(Helper.convertDpToPixel(5, context));

            // set text to center
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
                mTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            else
                mTextView.setGravity(Gravity.CENTER_HORIZONTAL);

            snackbar.show();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void showCustomToast(String msg, Drawable drawable, Context context, int lengthShort) {
        showCustomToast(msg, drawable, context, Toast.LENGTH_SHORT);
    }

    public static final boolean isValidPhoneNumber(CharSequence target) {
        if (target.length() != 10) {
            return false;
        } else {
            return android.util.Patterns.PHONE.matcher(target).matches();
        }
    }

    public static Calendar parceDateWithTime(String date) {
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(DATE_FORMAT_WITH_TIME.parse(date));
            return calendar;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static long getFileSize(File orgfile) {
        Log.i(TAG, "getFileSize(File) called for orgfile : " + orgfile);
        long size = 0;
        try {
            if (orgfile.isDirectory()) {
                for (File file : orgfile.listFiles()) {
                    size += getFileSize(file);
                }
            } else {
                size = orgfile.length();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i(TAG, "getFileSize(), size : " + size);
        return size;
    }

    public static String getMimeType(Context context, String fileNameWithPath) {
        Log.i(TAG, "getMimeType(Context, String) called for url : " + fileNameWithPath);
        String mimeType = null;
        try {
            Uri uri = Uri.fromFile(new File(fileNameWithPath));
            if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
                ContentResolver cr = context.getContentResolver();
                mimeType = cr.getType(uri);
            } else {
                String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri.toString());
                Log.i(TAG, "getMimeType(), fileExtension : " + fileExtension);
                mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension.toLowerCase());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i(TAG, "getMimeType(), mimeType : " + mimeType);
        return mimeType;
    }

    public static boolean deleteFile(String fileNameWithPath){
        Log.i(TAG, "deleteFile(String) called for fileNameWithPath : "+fileNameWithPath);
        boolean isFileDeleted = false;
        try{
            if(fileNameWithPath != null && !("").equals(fileNameWithPath)){
                File file = new File(fileNameWithPath);
                Log.i(TAG, "deleteFile(), file : "+file);
                if(file != null && file.exists()){
                    isFileDeleted = file.delete();
                }else{
                    Log.i(TAG, "deleteFile(), file is null or does not exist");
                }
            }
        }catch(Exception e){
            isFileDeleted = false;
            e.printStackTrace();
        }
        Log.i(TAG, "deleteFile(), isFileDeleted : "+isFileDeleted);
        return isFileDeleted;
    }

    public static byte[] getBytesFromFile(File file) {
        Log.i(TAG, "getBytesFromFile(File) called for file : "+file);
        byte[] data = null;
        try{
            // Open file
            RandomAccessFile f = new RandomAccessFile(file, "r");
            try {
                // Get and check length
                long longlength = f.length();
                int length = (int) longlength;
                if (length != longlength)
                    throw new IOException("File size >= 2 GB");
                // Read file and return data
                data = new byte[length];
                f.readFully(data);
                return data;
            }finally {
                f.close();
            }
        }catch(Exception e){
            data = null;
            e.printStackTrace();
        }
        Log.i(TAG, "getBytesFromFile(), data : "+data);
        return data;
    }

    public static String toCamelCase(String str){
        Log.i(TAG, "toCamelCase(String) called for str : " + str);
        String camelCaseString = "";
        try{
            if(str.length() == 0){
                return str;
            }
            String[] parts = str.split(" ");
            for (String part : parts){
                camelCaseString = camelCaseString +
                        ( part.substring(0, 1).toUpperCase() +
                                part.substring(1).toLowerCase() ) + " ";
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        return camelCaseString;
    }

    public static void dowloadFileInNotificationBar(Context context, String url) {
        Log.i(TAG, "dowloadFileInNotificationBar(Context, String, String) called for context : "+context+", url : "+url);
        try{
            File fileDir = new File(Environment.getExternalStorageDirectory().toString(), Constants.FILES_DOWNLOAD_PATH);
            if(!fileDir.exists()){
                Log.i(TAG, "dowloadFileInNotificationBar(), fileDir.mkdirs() : "+fileDir.mkdirs());
            }

            DownloadManager downloadmanager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

            Uri downloadUri = Uri.parse(url);
            DownloadManager.Request request = new DownloadManager.Request(downloadUri);

            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE |
                    DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            downloadmanager.enqueue(request);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Method for return file path of Gallery image
     *
     * @param context
     * @param uri
     * @return path of the selected image file from gallery
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    @SuppressLint("NewApi")
    public static String getPath(Context context, Uri uri) {

        Log.i(TAG, "getPath(Context, Uri) called for context : "+context+", uri : "+uri);

        String filePath = null;

        try{
            //check here to KITKAT or <span id="IL_AD7" class="IL_AD">new version</span>
            boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

            // DocumentProvider
            if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {

                // ExternalStorageProvider
                if (isExternalStorageDocument(uri)) {
                    String docId = DocumentsContract.getDocumentId(uri);
                    String[] split = docId.split(":");
                    String type = split[0];

                    if ("primary".equalsIgnoreCase(type)) {
                        filePath = Environment.getExternalStorageDirectory() + "/" + split[1];
                    }
                }

                // DownloadsProvider
                else if (isDownloadsDocument(uri)) {

                    String id = DocumentsContract.getDocumentId(uri);
                    Uri contentUri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                    filePath = getDataColumn(context, contentUri, null, null);
                }

                // MediaProvider
                else if (isMediaDocument(uri)) {
                    String docId = DocumentsContract.getDocumentId(uri);
                    String[] split = docId.split(":");
                    String type = split[0];

                    Uri contentUri = null;
                    if ("image".equals(type)) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }

                    String selection = "_id=?";
                    String[] selectionArgs = new String[] { split[1] };

                    filePath = getDataColumn(context, contentUri, selection, selectionArgs);
                }
            }
            // MediaStore (and general)
            else if ("content".equalsIgnoreCase(uri.getScheme())) {

                // Return the remote address
                if (isGooglePhotosUri(uri)){
                    filePath =  uri.getLastPathSegment();
                }else{
                    filePath =  getDataColumn(context, uri, null, null);
                }

            }
            // File
            else if ("file".equalsIgnoreCase(uri.getScheme())) {
                filePath = uri.getPath();
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        Log.i(TAG, "getPath(), filePath : "+filePath);
        return filePath;

    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {

        Cursor cursor = null;
        String column = "_data";
        String[] projection = { column };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
            if (cursor != null)
                cursor.close();
        } catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    public static Bitmap generateCircleBitmap(Context context, int circleColor, float diameterDP, String text){
        Log.i(TAG, "generateCircleBitmap(Context, int, float, String) called for context : "+context+", circleColor : "+circleColor+", diameterDP : "+diameterDP+", text : "+text);
        Bitmap output = null;

        try{
            final int textColor = Color.WHITE;

            if(circleColor == Constants.VALUE_NOT_PROVIDED){
                Random random = new Random();
                int randomNumber = random.nextInt(materialColors.size()-1);
                Log.i(TAG, "generateCircleBitmap(), randomNumber : "+randomNumber);

                circleColor  = context.getResources().getColor(R.color.green_notification);

               /* try{
                    circleColor = getMaterialColor(randomNumber);
                }catch(Exception e){
                    e.printStackTrace();
                    circleColor  = context.getResources().getColor(R.color.green_btn);
                }*/
            }

            Log.i(TAG, "generateCircleBitmap(), circleColor : "+circleColor);

            DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
            float diameterPixels = diameterDP * (metrics.densityDpi / 160f);
            float radiusPixels = diameterPixels/2;

            // Create the bitmap
            output = Bitmap.createBitmap((int) diameterPixels, (int) diameterPixels,
                    Bitmap.Config.ARGB_8888);

            // Create the canvas to draw on
            Canvas canvas = new Canvas(output);
            canvas.drawARGB(0, 0, 0, 0);

            // Draw the circle
            final Paint paintC = new Paint();
            paintC.setAntiAlias(true);
            paintC.setColor(circleColor);
            canvas.drawCircle(radiusPixels, radiusPixels, radiusPixels, paintC);

            // Draw the text
            if (text != null && text.length() > 0) {
                final Paint paintT = new Paint();
                paintT.setColor(textColor);
                paintT.setAntiAlias(true);
                paintT.setTextSize(radiusPixels * 1);
                //Typeface typeFace = Typeface.createFromAsset(context.getAssets(),"fonts/Roboto-Thin.ttf");
                //paintT.setTypeface(typeFace);
                final Rect textBounds = new Rect();
                paintT.getTextBounds(text, 0, text.length(), textBounds);
                canvas.drawText(text, radiusPixels - textBounds.exactCenterX(), radiusPixels - textBounds.exactCenterY(), paintT);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return output;
    }

    private static List<Integer> materialColors = Arrays.asList(
            0xffe57373,
            0xfff06292,
            0xffba68c8,
            0xff9575cd,
            0xff7986cb,
            0xff64b5f6,
            0xff4fc3f7,
            0xff4dd0e1,
            0xff4db6ac,
            0xff81c784,
            0xffaed581,
            0xffff8a65,
            0xffd4e157,
            0xffffd54f,
            0xffffb74d,
            0xffa1887f
    );

    public static int getMaterialColor(Object key) {
        return materialColors.get(Math.abs(key.hashCode()) % materialColors.size());
    }

    public static String removeExtra(String character) {

        Log.i(TAG, "removeExtra(), character : " + character);

        if (character.contains("%")) {
            character = character.replace("%", "%25");
        }if (character.contains("+")) {
            character = character.replace("+", "%2B");
        }if (character.contains(" ")) {
            character = character.replace(" ", "%20");
        }if (character.contains("/")) {
            character = character.replace("/", "%2F");
        }if (character.contains("?")) {
            character = character.replace("?", "%3F");
        }if (character.contains("&")) {
            character = character.replace("&", "%26");
        }if (character.contains("=")) {
            character = character.replace("=", "%3D");
        }
        Log.i(TAG, "removeExtra(),After character : " + character);
        return character;
    }

    public static String removeExtraFromDate(String character) {

        Log.i(TAG, "removeExtraFromDate(), character : " + character);

        if (character.contains("/")) {
            character = character.replace("/", "");
        }if (character.contains("Date")) {
            character = character.replace("Date", "");
        }if (character.contains("(")) {
            character = character.replace("(", "");
        }if (character.contains(")")) {
            character = character.replace(")", "");
        }if (character.contains("+0800")) {
            character = character.replace("+0800", "");
        }
        Log.i(TAG, "removeExtra(),After character : " + character);
        return character;
    }

    public static String dateFormat = "dd MMM, yyyy";
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);

    public static String ConvertMilliSecondsToFormattedDate(String milliSeconds){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(milliSeconds));
        return simpleDateFormat.format(calendar.getTime());
    }

    public static void displayImageUsingImageLoader(Context context, String uri, ImageView imageView, int defaultImageResId, ImageLoadingListener imageLoadingListener) {
        Log.i(TAG, "displayImageUsingImageLoader(Context, String, ImageView, int, ImageLoadingListener) called for context : " + context + ", uri : " + uri + ", imageView : " + imageView + ", defaultImageResId : " + defaultImageResId + ", imageLoadingListener : " + imageLoadingListener);
        try {

            if (defaultImageResId != Constants.VALUE_NOT_PROVIDED) {
                ImageLazyLoadingConfig.setDefaultImageLoading(defaultImageResId);
            } else {
                defaultImageResId = R.drawable.ic_launcher;
            }
            ImageLazyLoadingConfig.init(context);

            ImageLoadingListener animateFirstListener = null;
            if (imageLoadingListener == null) {
                animateFirstListener = new AnimateFirstDisplayListener();
            } else {
                animateFirstListener = imageLoadingListener;
            }
            ImageLoader imageLoader = ImageLoader.getInstance();
            imageLoader.init(ImageLazyLoadingConfig.imageLoaderConfiguration);

            imageLoader.displayImage(uri, imageView, ImageLazyLoadingConfig.displayImageOptions, animateFirstListener);

        } catch (Exception e) {
            imageView.setBackgroundResource(defaultImageResId);
            e.printStackTrace();
        }
    }

    public static String saveProductImage(Context context, Bitmap bitmap, int width, int height) {
        Log.i(TAG, "saveProductImage(Context, Bitmap, int, int) called for bitmap : " + bitmap + ", width :" + width + ", height : " + height);
        String fileNameWithPath = null;

        try {
            if (bitmap != null) {
                String fileName;
                fileName = Constants.PRODUCT_IMAGE_NAME;

                String path = Environment.getExternalStorageDirectory().toString();
                Log.i(TAG, "saveProductImage(), path : " + path);
                FileOutputStream fileOutputStream = null;
                File photoDir = new File(path, Constants.PRODUCT_IMAGE_PATH);
                if (!photoDir.exists()) {
                    Log.i(TAG, "saveProductImage(), photoDir.mkdirs() : " + photoDir.mkdirs());
                }
                File file = new File(path, Constants.PRODUCT_IMAGE_PATH + "/" + fileName + ".jpg");
                Log.i(TAG, "saveProductImage(), file.createNewFile() : " + file.createNewFile());
                fileOutputStream = new FileOutputStream(file);

                /*
                 * compress the image
                 */
                if (width > 0 && height > 0) {
                    Bitmap yourSelectedImage = bitmap;
                    Bitmap resizedBitmap = Bitmap.createScaledBitmap(yourSelectedImage, width, height, false);
                    resizedBitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                } else {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                }

                fileNameWithPath = file.getAbsolutePath();

                fileOutputStream.flush();
                fileOutputStream.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i(TAG, "saveProductImage(), fileNameWithPath : " + fileNameWithPath);
        return fileNameWithPath;
    }

    public static byte[] convertFromImageToBytes(String fileNameWithPath) {
        Log.i(TAG, "convertFromImageToBytes(String) called for fileNameWithPath : " + fileNameWithPath);
        byte[] imageByteArray = "".getBytes();

        if (fileNameWithPath != null && !("").equals(fileNameWithPath)) {
            try {
                File mFarmDir = new File(Environment.getExternalStorageDirectory().toString(), Constants.IMAGE_FILE_PATH);
                if (!mFarmDir.exists()) {
                    Log.i(TAG, "convertFromImageToBytes(), mFarmDir.mkdirs() : " + mFarmDir.mkdirs());
                }
                File file = new File(fileNameWithPath);
                if (file.exists()) {
                    Bitmap bitmap = BitmapFactory.decodeFile(fileNameWithPath);
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                    imageByteArray = byteArrayOutputStream.toByteArray();
                } else {
                    Log.i(TAG, "convertFromImageToBytes(), no such image file exists");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Log.i(TAG, "convertFromImageToBytes(), imageByteArray : " + imageByteArray);
        return imageByteArray;
    }

}