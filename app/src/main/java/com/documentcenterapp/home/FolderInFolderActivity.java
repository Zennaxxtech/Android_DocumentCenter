package com.documentcenterapp.home;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.documentcenterapp.R;
import com.documentcenterapp.adapter.FolderInFolderAdapter;
import com.documentcenterapp.helper.Helper;
import com.documentcenterapp.model.NewFolderData;
import com.documentcenterapp.model.NewFolderItemData;
import com.documentcenterapp.requestResponse.AndyUtils;
import com.documentcenterapp.requestResponse.ApiCall;
import com.documentcenterapp.requestResponse.ApiUrl;
import com.documentcenterapp.util.Constants;
import com.documentcenterapp.util.ItemClickSupport;
import com.documentcenterapp.util.SortingArraylistByObjest;
import com.documentcenterapp.util.TinyDB;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;

import cz.msebera.android.httpclient.Header;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

@SuppressLint("LongLogTag")
public class FolderInFolderActivity extends AppCompatActivity {

    private static final String TAG = "[FolderInFolderActivity] : ";

    private Context context = FolderInFolderActivity.this;

    private ImageView iv_back, iv_uploadFiles;

    private LayoutInflater inflater;

    private static final int PERMISSION_REQUEST_CODE = 200;

    private Uri uri = null;
    private String filePath = null;

    private String folderName, folderNo;

    private FolderInFolderAdapter folderInFolderAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView rv_folder_list;

    private RelativeLayout relNoDataFound;

    private static int position = 0;
    private static View view;

    private Dialog sortingOrderDialog = null;
    private Dialog folderDialog = null;

    private ArrayList<NewFolderItemData> folderDetailArrayList;

    private TinyDB tinyDB;

    private LinearLayout ll_sorting, ll_folder;

    private LinearLayout ll_copy_cancel_main, ll_move_cancel_main, ll_move, ll_cancel_move, ll_copy, ll_cancel_copy;

    private TextView folder_in_folder_name;

    String[] mimeTypes =
            {"image/*", "application/pdf", "application/msword", "application/vnd.ms-powerpoint", "application/vnd.ms-excel", "text/plain"};

    private Handler mHandler = new Handler() {

        public void handleMessage(android.os.Message msg) {
            Log.i(TAG, "in mHandler class");
            switch (msg.what) {

                case Constants.ServiceCode.SERVICE_PARSE_LIST:
                    Log.i(TAG, "handleMessage(), Constants.ServiceCode.SERVICE_PARSE_LIST");
                    /*folderInFolderAdapter = new FolderInFolderAdapter(context, mHandler, inflater, folderDetailArrayList);
                    rv_folder_list.setAdapter(folderInFolderAdapter);*/
                    getFolderListData();
                    break;

                default:
                    break;

                case Constants.ServiceCode.SERVICE_DELETE:
                    Log.i(TAG, "handleMessage(), Constants.ServiceCode.SERVICE_DELETE");
                    try {
                        if (folderDetailArrayList != null && folderDetailArrayList.size() > 0) {
                            if (msg.obj != null) {
                                int position = ((Integer) msg.obj).intValue();
                                Log.i(TAG, "handleMessage(), position : " + position);
                                if (position >= 0 && position < folderDetailArrayList.size()) {
                                    folderDetailArrayList.remove(position);
                                    mHandler.sendEmptyMessage(Constants.ServiceCode.SERVICE_PARSE_LIST);
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.folder_in_folder_activity_layour);

        try {
            inflater = LayoutInflater.from(this);

            tinyDB = new TinyDB(FolderInFolderActivity.this);

            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                folderName = extras.getString("folderName", "");
                folderNo = extras.getString("folderNo", "");
            }
            Log.i(TAG, "folderName " + folderName);
            Log.i(TAG, "folderNo " + folderNo);

            folder_in_folder_name = (TextView) findViewById(R.id.folder_in_folder_name);
            folder_in_folder_name.setText(folderName);

            iv_back = (ImageView) findViewById(R.id.iv_back);
            iv_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

            rv_folder_list = (RecyclerView) findViewById(R.id.rv_folder_list);
            mLayoutManager = new LinearLayoutManager(context);
            rv_folder_list.setLayoutManager(mLayoutManager);
            rv_folder_list.setItemAnimator(new DefaultItemAnimator());
            relNoDataFound = (RelativeLayout) findViewById(R.id.relNoDataFound);

            ItemClickSupport.addTo(rv_folder_list)
                    .setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                        @Override
                        public void onItemClicked(RecyclerView recyclerView, int position, View view) {
                            try {
                                FolderInFolderActivity.view = view;
                                FolderInFolderActivity.position = position;

                                Log.i(TAG, "onItemClicked(), FolderInFolderActivity.position : " + FolderInFolderActivity.position);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });

            String status = tinyDB.getString(Constants.Preferences.PREF_STATUS_COPY_MOVE);
            final String fileFolderId = tinyDB.getString(Constants.Preferences.PREF_FILE_OR_FOLDER_ID);
            final String fileFolder = tinyDB.getString(Constants.Preferences.PREF_FILE_OR_FOLDER);

            Log.i(TAG, "onCreate(), status : " + status);
            Log.i(TAG, "onCreate(), fileFolderId : " + fileFolderId);
            Log.i(TAG, "onCreate(), fileFolder : " + fileFolder);

            ll_folder = (LinearLayout) findViewById(R.id.ll_folder);
            ll_sorting = (LinearLayout) findViewById(R.id.ll_sorting);
            ll_copy_cancel_main = (LinearLayout) findViewById(R.id.ll_copy_cancel);
            ll_move_cancel_main = (LinearLayout) findViewById(R.id.ll_move_cancel);
            ll_move = (LinearLayout) findViewById(R.id.ll_move);
            ll_cancel_move = (LinearLayout) findViewById(R.id.ll_cancel_move);
            ll_copy = (LinearLayout) findViewById(R.id.ll_copy);
            ll_cancel_copy = (LinearLayout) findViewById(R.id.ll_cancel_copy);

            if (!status.equals("")) {
                if (status.equals("copy")) {
                    ll_sorting.setVisibility(View.GONE);
                    ll_folder.setVisibility(View.GONE);
                    ll_copy_cancel_main.setVisibility(View.VISIBLE);
                    ll_move_cancel_main.setVisibility(View.GONE);
                } else {
                    ll_sorting.setVisibility(View.GONE);
                    ll_folder.setVisibility(View.GONE);
                    ll_copy_cancel_main.setVisibility(View.GONE);
                    ll_move_cancel_main.setVisibility(View.VISIBLE);
                }
            } else {
                ll_sorting.setVisibility(View.VISIBLE);
                ll_folder.setVisibility(View.VISIBLE);
                ll_copy_cancel_main.setVisibility(View.GONE);
                ll_move_cancel_main.setVisibility(View.GONE);
            }

            ll_cancel_move.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ll_sorting.setVisibility(View.VISIBLE);
                    ll_folder.setVisibility(View.VISIBLE);
                    ll_copy_cancel_main.setVisibility(View.GONE);
                    ll_move_cancel_main.setVisibility(View.GONE);

                    tinyDB.remove(Constants.Preferences.PREF_STATUS_COPY_MOVE);
                    tinyDB.remove(Constants.Preferences.PREF_FILE_OR_FOLDER_ID);
                    tinyDB.remove(Constants.Preferences.PREF_FILE_OR_FOLDER);
                }
            });

            ll_cancel_copy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ll_sorting.setVisibility(View.VISIBLE);
                    ll_folder.setVisibility(View.VISIBLE);
                    ll_copy_cancel_main.setVisibility(View.GONE);
                    ll_move_cancel_main.setVisibility(View.GONE);

                    tinyDB.remove(Constants.Preferences.PREF_STATUS_COPY_MOVE);
                    tinyDB.remove(Constants.Preferences.PREF_FILE_OR_FOLDER_ID);
                    tinyDB.remove(Constants.Preferences.PREF_FILE_OR_FOLDER);
                }
            });

            ll_move.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (fileFolder.equals("file")) {
                        moveFile(folderNo, fileFolderId);
                    } else {
                        //copyFolder(folderNo, fileFolderId);
                    }

                }
            });

            ll_copy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (fileFolder.equals("file")) {
                        copyFile(folderNo, fileFolderId);
                    } else {
                        // moveFolder(folderNo, fileFolderId);
                    }
                }
            });

            ll_sorting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sortingOrderDialog();
                }
            });

            ll_folder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    newFolderDialog();
                }
            });

            iv_uploadFiles = (ImageView) findViewById(R.id.iv_upload);
            iv_uploadFiles.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    uploadFilesDialog();
                }
            });

            getFolderListData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void uploadFilesDialog() {

        final AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Gallery",
                "Camera",
                "Document", "Cancel"};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallary();
                                break;
                            case 1:
                                checkPhonePermissions();
                                break;
                            case 2:
                                chooseFilesFromPhone();
                                break;
                            case 3:
                                pictureDialog.setCancelable(true);
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    private void choosePhotoFromGallary() {
        Log.i(TAG, "choosePhotoFromGallary() called");
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        this.startActivityForResult(i, Constants.CHOOSE_PHOTO);
    }

    private void chooseFilesFromPhone() {
        Log.i(TAG, "chooseFilesFromPhone() called");
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent.setType(mimeTypes.length == 1 ? mimeTypes[0] : "*/*");
            if (mimeTypes.length > 0) {
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            }
        } else {
            String mimeTypesStr = "";
            for (String mimeType : mimeTypes) {
                mimeTypesStr += mimeType + "|";
            }
            intent.setType(mimeTypesStr.substring(0, mimeTypesStr.length() - 1));
        }
        startActivityForResult(intent, Constants.CHOOSE_DOCUMENT);
    }

    private void takePhotoFromCamera() {
        Log.i(TAG, "takePhotoFromCamera() called");
        Intent pictureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        if (pictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(pictureIntent, Constants.CHOOSE_CAMERA);
        }
    }

    public void checkPhonePermissions() {
        Log.i(TAG, "checkPhonePermissions() called");

        if (checkPermission()) {
            takePhotoFromCamera();
        } else {
            requestPermission();
        }
    }

    private boolean checkPermission() {
        Log.i(TAG, "CheckPermission() called");
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int result2 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);

        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        Log.i(TAG, "requestPermission() called");

        ActivityCompat.requestPermissions(this, new String[]{CAMERA, WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        Log.i(TAG, "onRequestPermissionsResult() called");
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {

                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean readAccepted = grantResults[2] == PackageManager.PERMISSION_GRANTED;

                    if (cameraAccepted && writeAccepted && readAccepted) {
                        Toast.makeText(this, "Permission Granted, Now you can access location data and camera.", Toast.LENGTH_SHORT).show();
                        //Snackbar.make(view, "Permission Granted, Now you can access location data and camera.", Snackbar.LENGTH_LONG).show();

                        takePhotoFromCamera();
                    } else {
                        Toast.makeText(this, "Permission Denied, You cannot access location data and camera.", Toast.LENGTH_SHORT).show();
                        //Snackbar.make(view, "Permission Denied, You cannot access location data and camera.", Snackbar.LENGTH_LONG).show();

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) {
                                showMessageOKCancel("You need to allow access to all the permissions",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{ACCESS_FINE_LOCATION, CAMERA, WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE},
                                                            PERMISSION_REQUEST_CODE);
                                                }
                                            }
                                        });
                                return;
                            }
                        }

                    }
                }

                break;
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new android.support.v7.app.AlertDialog.Builder(FolderInFolderActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Constants.CHOOSE_PHOTO:
                if (data != null) {

                    uri = data.getData();
                    String photopath = null;
                    try {
                        photopath = getFilePath(context, uri);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }

                    uploadFile(folderNo, photopath);
                    Log.i(TAG, "onActivityResult() uri :" + uri + ", photopath :" + photopath);
                }
                break;

            case Constants.CHOOSE_CAMERA:
                if (data != null) {

                    Bitmap mphoto = (Bitmap) data.getExtras().get("data");
                    String photopath = null;
                    uri = getImageUri(context, mphoto);
                    try {
                        photopath = getFilePath(context, uri);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }

                    uploadFile(folderNo, photopath);
                    Log.i(TAG, "onActivityResult() uri :" + uri + ", photopath :" + photopath);
                }
                break;
            case Constants.CHOOSE_DOCUMENT:
                if (data != null) {

                    Uri uri = data.getData();
                    String uriString = uri.toString();
                    File myFile = new File(uriString);

                    String photopath = null;
                    try {
                        photopath = getFilePath(context, uri);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }

                    Log.i(TAG, "onActivityResult() Choose Document uri :" + uri + ", uriString :" + uriString + ",myFile :" + myFile + ", photopath :" + photopath);

                    uploadFile(folderNo, photopath);
                }
                break;
        }
    }

    public static String getFilePath(Context context, Uri uri) throws URISyntaxException {
        String selection = null;
        String[] selectionArgs = null;
        // Uri is different in versions after KITKAT (Android 4.4), we need to
        if (Build.VERSION.SDK_INT >= 19 && DocumentsContract.isDocumentUri(context.getApplicationContext(), uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                return Environment.getExternalStorageDirectory() + "/" + split[1];
            } else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                uri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("image".equals(type)) {
                    uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                selection = "_id=?";
                selectionArgs = new String[]{
                        split[1]
                };
            }
        }
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {
                    MediaStore.Images.Media.DATA
            };
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver()
                        .query(uri, projection, selection, selectionArgs, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    @SuppressLint("LongLogTag")
    private void sortingOrderDialog() {
        Log.i(TAG, "sortingOrderDialog() called: ");
        try {
            sortingOrderDialog = new Dialog(context);

            sortingOrderDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

            sortingOrderDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            sortingOrderDialog.setContentView(R.layout.sorting_order_dialog_layout);

            TextView tv_name, tv_date, tv_size, tv_tags, tv_cancel;

            tv_name = (TextView) sortingOrderDialog.findViewById(R.id.tv_name);
            tv_date = (TextView) sortingOrderDialog.findViewById(R.id.tv_name);
            tv_size = (TextView) sortingOrderDialog.findViewById(R.id.tv_size);
            tv_tags = (TextView) sortingOrderDialog.findViewById(R.id.tv_tags);
            tv_cancel = (TextView) sortingOrderDialog.findViewById(R.id.tv_cancel);

            tv_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sortingOrderDialog.dismiss();
                }
            });

            tv_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   // Collections.sort(folderDetailArrayList, new SortingArraylistByObjest());
                    getFolderListData();
                    sortingOrderDialog.dismiss();
                }
            });

            sortingOrderDialog.setCancelable(false);

            sortingOrderDialog.dismiss();

            sortingOrderDialog.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("LongLogTag")
    private void newFolderDialog() {
        Log.i(TAG, "newFolderDialog() called: ");
        try {
            folderDialog = new Dialog(context);

            folderDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

            folderDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            folderDialog.setContentView(R.layout.folder_dialog_layout);

            TextView tv_cancel, tv_save;
            final EditText et_folder_name;

            tv_cancel = (TextView) folderDialog.findViewById(R.id.tv_cancel);
            tv_save = (TextView) folderDialog.findViewById(R.id.tv_save);
            et_folder_name = (EditText) folderDialog.findViewById(R.id.et_folder_name);

            tv_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    folderDialog.dismiss();
                }
            });

            tv_save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String newFolderName = et_folder_name.getText().toString().trim();
                    createNewFolder(newFolderName);

                    folderDialog.dismiss();
                }
            });

            folderDialog.setCancelable(false);

            folderDialog.dismiss();

            folderDialog.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getFolderListData() {
        Log.i(TAG, "getFolderListData() called ");

        try {
            if (!Helper.isNetworkAvailable(FolderInFolderActivity.this)) {
                //AlertDialogManager.showAlertDialog(this, getString(R.string.txt_alert_C), getString(R.string.no_internet));
                return;
            } else {
                RequestParams params = new RequestParams();

                String token = tinyDB.getString(Constants.Preferences.PREF_TOKEN);
                String appName = tinyDB.getString(Constants.Preferences.PREF_APP_NAME);
                String userName = tinyDB.getString(Constants.Preferences.PREF_DECREPTED_USER_NAME);

                String FolderNo = AndyUtils.encrypt(folderNo);
                String folderNoAfterRemveExtra = Helper.removeExtra(FolderNo);

                String param = "?Token=" + token + "&AppsName=" + appName + "&UserName=" + userName + "&FolderNo=" + folderNoAfterRemveExtra;
                Log.d("Lodin ", "param : " + param);

                new ApiCall(FolderInFolderActivity.this).firePost(true, ApiUrl.FOLDER_FILE_LIST + param, params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        String res = new String(responseBody);
                        Log.d("Lodin ", "responseBody : " + res);
                        try {
                            JSONObject jsonObject = new JSONObject(res);
                            if (jsonObject.getString("Status").equals("1")) {
                                NewFolderData folderListData = new Gson().fromJson(res, NewFolderData.class);

                                String s = folderListData.getData();
                                s.replace("[", "");
                                s.replace("]", "");
                                String data = AndyUtils.decrypt(s);
                                data = "[" + data + "]";

                                folderDetailArrayList = new Gson().fromJson(data, new TypeToken<ArrayList<NewFolderItemData>>() {
                                }.getType());

                                if (folderDetailArrayList != null && folderDetailArrayList.size() > 0) {

                                    relNoDataFound.setVisibility(relNoDataFound.GONE);

                                    folderInFolderAdapter = new FolderInFolderAdapter(context, mHandler, inflater, folderDetailArrayList);
                                    rv_folder_list.setAdapter(folderInFolderAdapter);

                                } else {
                                    folderDetailArrayList = new ArrayList<NewFolderItemData>();
                                    if (folderInFolderAdapter != null) {
                                        folderInFolderAdapter.setList(folderDetailArrayList);
                                    }

                                    relNoDataFound.setVisibility(relNoDataFound.VISIBLE);
                                    Log.i(TAG, "getFolderListData(), folderDetailArrayList is null or empty");
                                }

                            } else {
                                Toast.makeText(context, "" + jsonObject.getString("Msg"), Toast.LENGTH_LONG).show();
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createNewFolder(String newFolderName) {
        Log.i(TAG, "createNewFolder() called ");

        try {
            if (!Helper.isNetworkAvailable(FolderInFolderActivity.this)) {
                //AlertDialogManager.showAlertDialog(this, getString(R.string.txt_alert_C), getString(R.string.no_internet));
                return;
            } else {
                RequestParams params = new RequestParams();

                String token = tinyDB.getString(Constants.Preferences.PREF_TOKEN);
                String appName = tinyDB.getString(Constants.Preferences.PREF_APP_NAME);
                String userName = tinyDB.getString(Constants.Preferences.PREF_DECREPTED_USER_NAME);

                String FolderNo = AndyUtils.encrypt(folderNo);//pas parant folder no
                String folderNoAfterRemveExtra = Helper.removeExtra(FolderNo);

                String newFolderNameAfterDecrypt = AndyUtils.encrypt(newFolderName);
                String newFolderNameAfterRemoveExtra = Helper.removeExtra(newFolderNameAfterDecrypt);

                String param = "?Token=" + token + "&AppsName=" + appName + "&UserName=" + userName + "&ParentFolderNo=" + folderNoAfterRemveExtra + "&FolderName=" + newFolderNameAfterRemoveExtra;
                Log.d("Lodin ", "param : " + param);

                new ApiCall(FolderInFolderActivity.this).firePost(true, ApiUrl.CREATE_FOLDER + param, params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        String res = new String(responseBody);
                        Log.d("Lodin ", "responseBody : " + res);
                        try {
                            JSONObject jsonObject = new JSONObject(res);
                            if (jsonObject.getString("Status").equals("1")) {
                              /*  NewFolderData folderDetail = new Gson().fromJson(res, NewFolderData.class);

                                String s = folderDetail.getData();
                                s.replace("[", "");
                                s.replace("]", "");
                                String data = AndyUtils.decrypt(s);
                                data = "[" + data + "]";*/

                               /* ArrayList<NewFolderItemData> folderItemDataArrayList = new Gson().fromJson(data, new TypeToken<ArrayList<NewFolderItemData>>() {
                                }.getType());

                                if (folderItemDataArrayList != null && folderItemDataArrayList.size() > 0) {*/

                                getFolderListData();

                                /*} else {
                                    Log.i(TAG, "getFolderListData(), folderDetailArrayList is null or empty");
                                }*/

                            } else {
                                Toast.makeText(context, "" + jsonObject.getString("Msg"), Toast.LENGTH_LONG).show();
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void copyFile(String fileNo, String fileId) {
        Log.i(TAG, "copyFile() called: for fileNo" + fileNo);

        try {
            if (!Helper.isNetworkAvailable((Activity) context)) {
                //AlertDialogManager.showAlertDialog(this, getString(R.string.txt_alert_C), getString(R.string.no_internet));
                return;
            } else {
                RequestParams params = new RequestParams();

                String token = tinyDB.getString(Constants.Preferences.PREF_TOKEN);
                String appName = tinyDB.getString(Constants.Preferences.PREF_APP_NAME);
                String userName = tinyDB.getString(Constants.Preferences.PREF_DECREPTED_USER_NAME);

                String FolderNo = AndyUtils.encrypt(fileNo);//pas parant folder no
                String folderNoAfterRemveExtra = Helper.removeExtra(FolderNo);

                String Status = "Copy";
                String statusAfterEncrypt = AndyUtils.encrypt(Status);
                String statusAfterRemveExtra = Helper.removeExtra(statusAfterEncrypt);

                String FolderId = AndyUtils.encrypt(fileId);//pas parant folder no
                String folderIdAfterRemveExtra = Helper.removeExtra(FolderId);

                //token, appname, username, folderno, status, fileeid,

                String param = "?Token=" + token + "&AppsName=" + appName + "&UserName=" + userName + "&FolderNo=" + folderNoAfterRemveExtra + "&Status=" + statusAfterRemveExtra + "&FileID=" + folderIdAfterRemveExtra;
                Log.d("Lodin ", "param : " + param);

                new ApiCall(context).firePost(true, ApiUrl.OPERATE_FILE + param, params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        String res = new String(responseBody);
                        Log.d("Lodin ", "responseBody : " + res);
                        try {
                            JSONObject jsonObject = new JSONObject(res);
                            if (jsonObject.getString("Status").equals("1")) {

                                getFolderListData();

                                tinyDB.remove(Constants.Preferences.PREF_STATUS_COPY_MOVE);
                                tinyDB.remove(Constants.Preferences.PREF_FILE_OR_FOLDER_ID);
                                tinyDB.remove(Constants.Preferences.PREF_FILE_OR_FOLDER);

                                ll_sorting.setVisibility(View.VISIBLE);
                                ll_folder.setVisibility(View.VISIBLE);
                                ll_copy_cancel_main.setVisibility(View.GONE);
                                ll_move_cancel_main.setVisibility(View.GONE);

                            } else {
                                Toast.makeText(context, "" + jsonObject.getString("Msg"), Toast.LENGTH_LONG).show();
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void moveFile(String fileNo, final String fileId) {
        Log.i(TAG, "moveFile() called: for fileNo" + fileNo);

        try {
            if (!Helper.isNetworkAvailable((Activity) context)) {
                //AlertDialogManager.showAlertDialog(this, getString(R.string.txt_alert_C), getString(R.string.no_internet));
                return;
            } else {
                RequestParams params = new RequestParams();

                String token = tinyDB.getString(Constants.Preferences.PREF_TOKEN);
                String appName = tinyDB.getString(Constants.Preferences.PREF_APP_NAME);
                String userName = tinyDB.getString(Constants.Preferences.PREF_DECREPTED_USER_NAME);

                String FolderNo = AndyUtils.encrypt(fileNo);//pas parant folder no
                String folderNoAfterRemveExtra = Helper.removeExtra(FolderNo);

                String Status = "Move";
                String statusAfterEncrypt = AndyUtils.encrypt(Status);
                String statusAfterRemveExtra = Helper.removeExtra(statusAfterEncrypt);

                String FolderId = AndyUtils.encrypt(fileId);//pas parant folder no
                String folderIdAfterRemveExtra = Helper.removeExtra(FolderId);

                //token, appname, username, folderno, status, fileeid,

                String param = "?Token=" + token + "&AppsName=" + appName + "&UserName=" + userName + "&FolderNo=" + folderNoAfterRemveExtra + "&Status=" + statusAfterRemveExtra + "&FileID=" + folderIdAfterRemveExtra;
                Log.d("Lodin ", "param : " + param);

                new ApiCall(context).firePost(true, ApiUrl.OPERATE_FILE + param, params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        String res = new String(responseBody);
                        Log.d("Lodin ", "responseBody : " + res);
                        try {
                            JSONObject jsonObject = new JSONObject(res);
                            if (jsonObject.getString("Status").equals("1")) {

                                getFolderListData();

                                tinyDB.remove(Constants.Preferences.PREF_STATUS_COPY_MOVE);
                                tinyDB.remove(Constants.Preferences.PREF_FILE_OR_FOLDER_ID);
                                tinyDB.remove(Constants.Preferences.PREF_FILE_OR_FOLDER);

                                ll_sorting.setVisibility(View.VISIBLE);
                                ll_folder.setVisibility(View.VISIBLE);
                                ll_copy_cancel_main.setVisibility(View.GONE);
                                ll_move_cancel_main.setVisibility(View.GONE);

                            } else {
                                Toast.makeText(context, "" + jsonObject.getString("Msg"), Toast.LENGTH_LONG).show();
                            }
                        } catch (
                                JSONException e)

                        {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          byte[] responseBody, Throwable error) {
//                    dismissProgressDialog();
                    }
                });
            }
        } catch (
                Exception e)

        {
            e.printStackTrace();
        }

    }

    private void copyFolder(String folderNo, String folderId) {
        Log.i(TAG, "copyFolder() called: for folderNo" + folderNo);

        try {
            if (!Helper.isNetworkAvailable((Activity) context)) {
                //AlertDialogManager.showAlertDialog(this, getString(R.string.txt_alert_C), getString(R.string.no_internet));
                return;
            } else {
                RequestParams params = new RequestParams();

                String token = tinyDB.getString(Constants.Preferences.PREF_TOKEN);
                String appName = tinyDB.getString(Constants.Preferences.PREF_APP_NAME);
                String userName = tinyDB.getString(Constants.Preferences.PREF_DECREPTED_USER_NAME);

                String FolderNo = AndyUtils.encrypt(folderNo);//pas parant folder no
                String folderNoAfterRemveExtra = Helper.removeExtra(FolderNo);

                String Status = "Copy";
                String statusAfterEncrypt = AndyUtils.encrypt(Status);
                String statusAfterRemveExtra = Helper.removeExtra(statusAfterEncrypt);

                String FolderId = AndyUtils.encrypt(folderId);//pas parant folder no
                String folderidAfterRemveExtra = Helper.removeExtra(FolderId);

                String param = "?Token=" + token + "&AppsName=" + appName + "&UserName=" + userName + "&FolderNo=" + folderNoAfterRemveExtra + "&Status=" + statusAfterRemveExtra + "&";
                Log.d("Lodin ", "param : " + param);

                new ApiCall(context).firePost(true, ApiUrl.OPERATE_FOLDER + param, params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        String res = new String(responseBody);
                        Log.d("Lodin ", "responseBody : " + res);
                        try {
                            JSONObject jsonObject = new JSONObject(res);
                            if (jsonObject.getString("Status").equals("1")) {

                                getFolderListData();

                            } else {
                                Toast.makeText(context, "" + jsonObject.getString("Msg"), Toast.LENGTH_LONG).show();
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void uploadFile(String folderNo, String fileName) {
        Log.i(TAG, "uploadFile() called for folderNo :" + folderNo + ",fileName :" + fileName);

        try {
            if (!Helper.isNetworkAvailable((Activity) context)) {
                //AlertDialogManager.showAlertDialog(this, getString(R.string.txt_alert_C), getString(R.string.no_internet));
                return;
            } else {
                RequestParams params = new RequestParams();

                String token = tinyDB.getString(Constants.Preferences.PREF_TOKEN);
                String appName = tinyDB.getString(Constants.Preferences.PREF_APP_NAME);
                String userName = tinyDB.getString(Constants.Preferences.PREF_DECREPTED_USER_NAME);

                String FolderNo = AndyUtils.encrypt(folderNo);//pas parant folder no
                String folderNoAfterRemveExtra = Helper.removeExtra(FolderNo);

                params.put("file", new File(fileName));

                String param = "?Token=" + token + "&AppsName=" + appName + "&UserName=" + userName + "&FolderNo=" + folderNoAfterRemveExtra;
                Log.d("Lodin ", "param : " + param);

                new ApiCall(context).firePost(true, ApiUrl.UPLOAD_FILE + param, params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        String res = new String(responseBody);
                        Log.d("Lodin ", "responseBody : " + res);
                        try {
                            JSONObject jsonObject = new JSONObject(res);
                            if (jsonObject.getString("Status").equals("1")) {

                                getFolderListData();

                            } else {
                                Toast.makeText(context, "" + jsonObject.getString("Msg"), Toast.LENGTH_LONG).show();
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        Log.i(TAG, "onBackPressed() called ");
        // TODO Auto-generated method stub
        super.onBackPressed();
    }
}
