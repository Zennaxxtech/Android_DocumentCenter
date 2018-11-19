package com.documentcenterapp.adapter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.PermissionRequest;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.documentcenterapp.MainActivity;
import com.documentcenterapp.R;
import com.documentcenterapp.database.DatabaseHelper;
import com.documentcenterapp.helper.Helper;
import com.documentcenterapp.home.FolderInFolderActivity;
import com.documentcenterapp.model.DownloadItemListData;
import com.documentcenterapp.model.NewFolderItemData;
import com.documentcenterapp.requestResponse.AndyUtils;
import com.documentcenterapp.requestResponse.ApiCall;
import com.documentcenterapp.requestResponse.ApiUrl;
import com.documentcenterapp.uploadDownload.UploadDownloadFragment;
import com.documentcenterapp.util.Constants;
import com.documentcenterapp.util.ImageLazyLoadingConfig;
import com.documentcenterapp.util.TinyDB;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.single.PermissionListener;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

@SuppressLint("LongLogTag")
public class FolderInFolderAdapter extends RecyclerSwipeAdapter<FolderInFolderAdapter.SimpleViewHolder> {

    private static final String TAG = "[FolderInFolderAdapter] : ";

    private LayoutInflater inflater;
    private static ArrayList<NewFolderItemData> folderDetailArrayList;
    private static Context context;
    private int position;
    private Handler mHandler;

    private Dialog moreDialog = null;
    private Dialog folderDialog = null;
    private Dialog deleteDialog = null;

    private TinyDB tinyDB;

    String fileDownloadLink;

    private DatabaseHelper db;

    private ArrayList<DownloadItemListData> downloadItemListDataArrayList = new ArrayList<DownloadItemListData>();

    public FolderInFolderAdapter(Context context, Handler mHandler, LayoutInflater inflater, ArrayList<NewFolderItemData> folderDetailArrayList) {
        this.context = context;
        this.inflater = inflater;
        this.folderDetailArrayList = new ArrayList<NewFolderItemData>();
        this.folderDetailArrayList.addAll(folderDetailArrayList);
        tinyDB = new TinyDB(context);
        this.mHandler = mHandler;
    }

    public void setList(ArrayList<NewFolderItemData> folderDetailArrayList) {
        Log.i(TAG, "setList(ArrayList<NewFolderItemData>) called for folderDetailArrayList : " + folderDetailArrayList);
        this.folderDetailArrayList.clear();
        this.folderDetailArrayList.addAll(folderDetailArrayList);
        this.notifyDataSetChanged();
    }

    public static class SimpleViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout ll_delete, ll_edit, ll_more, ll_download, ll_folder;
        private TextView tv_folder_name, tv_date;

        ImageView iv_folder, iv_file, iv_forward_arrow;
        SwipeLayout swipeLayout;

        public SimpleViewHolder(View view) {
            super(view);
            swipeLayout = (SwipeLayout) view.findViewById(R.id.swipe);
            ll_folder = (LinearLayout) view.findViewById(R.id.ll_folder);
            ll_delete = (LinearLayout) view.findViewById(R.id.ll_delete);
            ll_edit = (LinearLayout) view.findViewById(R.id.ll_edit);
            ll_more = (LinearLayout) view.findViewById(R.id.ll_more);
            ll_download = (LinearLayout) view.findViewById(R.id.ll_download);

            tv_date = (TextView) view.findViewById(R.id.tv_date);
            tv_folder_name = (TextView) view.findViewById(R.id.tv_folder_name);

            iv_folder = (ImageView) view.findViewById(R.id.iv_folder);
            iv_file = (ImageView) view.findViewById(R.id.iv_file);
            iv_forward_arrow = (ImageView) view.findViewById(R.id.iv_forward_arrow);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   /* Log.d(getClass().getSimpleName(), "onItemSelected: " + textViewData.getText().toString());
                    Toast.makeText(view.getContext(), "onItemSelected: " + textViewData.getText().toString(), Toast.LENGTH_SHORT).show();*/
                }
            });
        }
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(R.layout.folder_in_folder_adapter_layout, parent, false);
        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder holder, final int position) {

        this.position = position;

        db = new DatabaseHelper(context);

        final NewFolderItemData folderListData = folderDetailArrayList.get(position);
        Log.i(TAG, "onBindViewHolder(), folderListData : " + folderListData.toString());

        holder.swipeLayout.addSwipeListener(new SimpleSwipeListener() {
            @Override
            public void onOpen(SwipeLayout layout) {

            }
        });
        holder.swipeLayout.setOnDoubleClickListener(new SwipeLayout.DoubleClickListener() {
            @Override
            public void onDoubleClick(SwipeLayout layout, boolean surface) {

            }
        });

        try {
            String fileOrFolderName = null;
            String date = null;
            if (folderListData.getFolderName() != null || folderListData.getFileName() != null) {
                if (folderListData.getFolderName() != null) {
                    holder.ll_edit.setVisibility(View.VISIBLE);
                    holder.ll_download.setVisibility(View.GONE);
                    holder.iv_folder.setVisibility(View.VISIBLE);
                    holder.iv_file.setVisibility(View.GONE);
                    fileOrFolderName = folderListData.getFolderName();
                    date = folderListData.getCreateDate();
                } else if (folderListData.getFileName() != null) {
                    holder.ll_download.setVisibility(View.VISIBLE);
                    holder.ll_edit.setVisibility(View.GONE);
                    holder.iv_file.setVisibility(View.VISIBLE);
                    holder.iv_folder.setVisibility(View.GONE);
                    fileOrFolderName = folderListData.getFileName();
                    date = folderListData.getModifyDate();
                    holder.iv_forward_arrow.setVisibility(View.GONE);
                    if (folderListData.getFileDownloadLink() != null) {
                        try {
                            fileDownloadLink = folderListData.getFileDownloadLink().split("href=\"")[1].split("\">")[0];
                        } catch (Exception e) {
                            e.printStackTrace();
                            fileDownloadLink = folderListData.getFileDownloadLink();
                        }
                    }
                }
            }

            String imageURL = folderListData.getFileThumbnailPath();
            if (imageURL != null) {
                holder.iv_folder.setVisibility(View.GONE);

                holder.iv_file.setVisibility(View.VISIBLE);
                try {
                    ImageLoadingListener imageLoadingListener = new ImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String imageUri, View view) {

                        }

                        @Override
                        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                        }

                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap bitmap) {
                            Log.i(TAG, "onLoadingComplete(String, View, Bitmap) called for imageUri : " + imageUri + ", view : " + view + ", bitmap : " + bitmap);
                            try {
                                if (bitmap != null) {
                                    ImageView imageView = (ImageView) view;
                                    boolean firstDisplay = !ImageLazyLoadingConfig.displayedImages.contains(imageUri);
                                    if (firstDisplay) {
                                        FadeInBitmapDisplayer.animate(imageView, 500);
                                        ImageLazyLoadingConfig.displayedImages.add(imageUri);
                                    }
                                } else {
                                    holder.iv_file.setImageResource(R.drawable.no_file_icon);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onLoadingCancelled(String imageUri, View view) {

                        }
                    };
                    Helper.displayImageUsingImageLoader(context, imageURL, holder.iv_file, R.drawable.ic_launcher, imageLoadingListener);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                holder.iv_file.setImageResource(R.drawable.no_file_icon);
            }

            Log.i(TAG, "onBindViewHolder(), fileOrFolderName : " + fileOrFolderName);
            Log.i(TAG, "onBindViewHolder(), date : " + date);
            Log.i(TAG, "onBindViewHolder(), fileDownloadLink : " + fileDownloadLink);

            holder.tv_folder_name.setText(fileOrFolderName);

            String dateAfterRemoveExtra = Helper.removeExtraFromDate(date);
            Log.i(TAG, "onBindViewHolder(), dateAfterRemoveExtra : " + dateAfterRemoveExtra);

            final String parseDate = Helper.ConvertMilliSecondsToFormattedDate(dateAfterRemoveExtra);
            Log.i(TAG, "onBindViewHolder(), parseDate : " + parseDate);

            holder.tv_date.setText(parseDate);

            holder.ll_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        holder.swipeLayout.close();
                        deleteDialog = new Dialog(context);

                        deleteDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

                        deleteDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                        deleteDialog.setContentView(R.layout.custom_alert_dialog);

                        TextView cancel, delete;

                        cancel = (TextView) deleteDialog.findViewById(R.id.btn1);
                        delete = (TextView) deleteDialog.findViewById(R.id.btn2);

                        cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                deleteDialog.dismiss();
                            }
                        });

                        delete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (folderListData.getFileName() != null) {
                                    deleteFile(folderListData.getFileNo(), position);
                                } else {
                                    deleteFolder(folderListData.getFolderNo(), position);
                                }

                                deleteDialog.dismiss();
                            }
                        });

                        deleteDialog.setCancelable(false);

                        deleteDialog.dismiss();

                        deleteDialog.show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                   /* mItemManger.removeShownLayouts(holder.swipeLayout);
                    mItemManger.closeAllItems();*/
                }
            });

            holder.ll_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.swipeLayout.close();
                    renameFolderFileDialog("folder", folderListData.getFolderNo(), folderListData.getFolderName());
                    //renameFolder(folderListData.getFolderNo());

                    /*mItemManger.removeShownLayouts(holder.swipeLayout);
                    mItemManger.closeAllItems();*/
                }
            });

            holder.ll_more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.swipeLayout.close();
                    if (folderListData.getFileName() != null) {
                        moreDialog("file", folderListData.getFileNo(), folderListData.getFileName());
                    } else {
                        moreDialog("folder", folderListData.getFolderNo(), folderListData.getFolderName());
                    }

                   /* mItemManger.removeShownLayouts(holder.swipeLayout);
                    mItemManger.closeAllItems();*/
                }
            });

            holder.ll_download.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    if (Helper.isNetworkAvailable((Activity) context)) {

                        boolean isToggleOnOrNot = tinyDB.getBoolean(Constants.Preferences.PREF_TOGGLE);
                        Log.i(TAG, "onCreateView() called for isToggleOnOrNot :" + isToggleOnOrNot);

                        final ConnectivityManager connMgr = (ConnectivityManager)
                                context.getSystemService(Context.CONNECTIVITY_SERVICE);
                        final android.net.NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                        final android.net.NetworkInfo mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                        if (wifi.isConnectedOrConnecting()) {

                            holder.swipeLayout.close();
                            if (folderListData.getFileDownloadLink().contains("<a href")) {
                                createNewDownload(folderDetailArrayList.get(position).getFileName(), String.valueOf(folderDetailArrayList.get(position).getFileSize()), fileDownloadLink, folderDetailArrayList.get(position).getFileThumbnailPath(), parseDate);

                                UploadDownloadFragment newFragment = new UploadDownloadFragment(1);
                                FragmentTransaction transaction = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
                                transaction.replace(R.id.frame, newFragment);
                                transaction.addToBackStack(null);
                                transaction.commit();

                            } else {
                                Log.i(TAG, "onBindViewHolder(), Toast : " + "Not Contain Download Link");
                                Toast.makeText(v.getContext(), "Not Contain Download Link", Toast.LENGTH_SHORT).show();
                            }

                            mItemManger.removeShownLayouts(holder.swipeLayout);
                            mItemManger.closeAllItems();

                        } else if (mobile.isConnectedOrConnecting()) {

                            if(isToggleOnOrNot){
                                holder.swipeLayout.close();
                                if (folderListData.getFileDownloadLink().contains("<a href")) {
                                    createNewDownload(folderDetailArrayList.get(position).getFileName(), String.valueOf(folderDetailArrayList.get(position).getFileSize()), fileDownloadLink, folderDetailArrayList.get(position).getFileThumbnailPath(), parseDate);

                                    UploadDownloadFragment newFragment = new UploadDownloadFragment(1);
                                    FragmentTransaction transaction = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
                                    transaction.replace(R.id.frame, newFragment);
                                    transaction.addToBackStack(null);
                                    transaction.commit();

                                } else {
                                    Log.i(TAG, "onBindViewHolder(), Toast : " + "Not Contain Download Link");
                                    Toast.makeText(v.getContext(), "Not Contain Download Link", Toast.LENGTH_SHORT).show();
                                }

                                mItemManger.removeShownLayouts(holder.swipeLayout);
                                mItemManger.closeAllItems();
                            }else{
                                Toast.makeText(context, "Already disable to use network data to upload/download, please reset on Setting page.", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            // Toast.makeText(this, "No Network ", Toast.LENGTH_LONG).show();
                        }

                    } else {
                        Toast.makeText(context, "No Internet Connetion", Toast.LENGTH_LONG).show();
                    }
                }
            });

            holder.iv_folder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, FolderInFolderActivity.class);
                    intent.putExtra("folderName", folderListData.getFolderName());
                    intent.putExtra("folderNo", folderListData.getFolderNo());
                    context.startActivity(intent);

                }
            });

            mItemManger.bindView(holder.itemView, position);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createNewDownload(String name, String fileSize, String downladlink, String icon, String date) {
        // inserting note in db and getting
        // newly inserted note id
        long id = db.insertNote(name, fileSize, downladlink, icon, date);

        // get the newly inserted note from db
        DownloadItemListData n = db.getNote(id);

        if (n != null) {
            // adding new note to array list at 0 position
            downloadItemListDataArrayList.add(0, n);
        }
    }

    @Override
    public int getItemCount() {
        return folderDetailArrayList.size();
    }

    @SuppressLint("LongLogTag")
    private void moreDialog(final String fileorFolder, final String fileNo, final String folderName) {
        Log.i(TAG, "moreDialog() called: for fileorFolder" + fileorFolder + ", fileNo" + fileNo + ", folderName" + folderName);
        try {
            moreDialog = new Dialog(context);

            moreDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

            moreDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            moreDialog.setContentView(R.layout.more_dialog_layout);

            TextView tv_cancel, tv_copy, tv_rename, tv_move, tv_folder_file_name;

            tv_folder_file_name = (TextView) moreDialog.findViewById(R.id.tv_folder_file_name);
            tv_cancel = (TextView) moreDialog.findViewById(R.id.tv_cancel);
            tv_copy = (TextView) moreDialog.findViewById(R.id.tv_copy);
            tv_move = (TextView) moreDialog.findViewById(R.id.tv_move);
            tv_rename = (TextView) moreDialog.findViewById(R.id.tv_rename);

            tv_folder_file_name.setText(folderName);

            if (fileorFolder.equals("file")) {
                tv_rename.setVisibility(View.VISIBLE);
            } else {
                tv_rename.setVisibility(View.GONE);
            }

            tv_rename.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    renameFolderFileDialog(fileorFolder, fileNo, folderName);
                    moreDialog.dismiss();
                }
            });

            tv_copy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tinyDB.putString(Constants.Preferences.PREF_STATUS_COPY_MOVE, "copy");
                    if (fileorFolder.equals("file")) {
                        tinyDB.putString(Constants.Preferences.PREF_FILE_OR_FOLDER_ID, fileNo);
                        tinyDB.putString(Constants.Preferences.PREF_FILE_OR_FOLDER, fileorFolder);
                    } else {
                       /* tinyDB.putString(Constants.Preferences.PREF_FILE_OR_FOLDER_ID, fileNo);
                        tinyDB.putString(Constants.Preferences.PREF_FILE_OR_FOLDER, fileorFolder);*/
                    }
                    moreDialog.dismiss();
                }
            });

            tv_move.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tinyDB.putString(Constants.Preferences.PREF_STATUS_COPY_MOVE, "move");
                    if (fileorFolder.equals("file")) {
                        tinyDB.putString(Constants.Preferences.PREF_FILE_OR_FOLDER_ID, fileNo);
                        tinyDB.putString(Constants.Preferences.PREF_FILE_OR_FOLDER, fileorFolder);
                    } else {
                       /* tinyDB.putString(Constants.Preferences.PREF_FILE_OR_FOLDER_ID, fileNo);
                        tinyDB.putString(Constants.Preferences.PREF_FILE_OR_FOLDER, fileorFolder);*/
                    }
                    moreDialog.dismiss();
                }
            });

            tv_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    moreDialog.dismiss();
                }
            });

            moreDialog.setCancelable(false);

            moreDialog.dismiss();

            moreDialog.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("LongLogTag")
    private void renameFolderFileDialog(final String fileOrFolder, final String folderFileNo, final String oldFolderName) {
        Log.i(TAG, "renameFolderFileDialog() called: for fileOrFolder" + fileOrFolder + ", folderFileNo" + folderFileNo + ", oldFolderName" + oldFolderName);
        try {
            folderDialog = new Dialog(context);

            folderDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

            folderDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            folderDialog.setContentView(R.layout.rename_folder_dialog_layout);

            TextView tv_cancel, tv_save;
            final EditText et_folder_name;

            tv_cancel = (TextView) folderDialog.findViewById(R.id.tv_cancel);
            tv_save = (TextView) folderDialog.findViewById(R.id.tv_save);
            et_folder_name = (EditText) folderDialog.findViewById(R.id.et_folder_name);
            et_folder_name.setText(oldFolderName);

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
                    if (!newFolderName.equals("")) {
                        if (fileOrFolder.equals("folder")) {
                            renameFolder(folderFileNo, newFolderName);
                        } else {
                            renameFile(folderFileNo, newFolderName);
                        }
                    }
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

    private void deleteFolder(String folderNo, final int position) {
        Log.i(TAG, "deleteFolder() called ");

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

                String Status = "Delete";
                String statusAfterEncrypt = AndyUtils.encrypt(Status);
                String statusAfterRemveExtra = Helper.removeExtra(statusAfterEncrypt);

                String param = "?Token=" + token + "&AppsName=" + appName + "&UserName=" + userName + "&FolderNo=" + folderNoAfterRemveExtra + "&Status=" + statusAfterRemveExtra;
                Log.d("Lodin ", "param : " + param);

                new ApiCall(context).firePost(true, ApiUrl.OPERATE_FOLDER + param, params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        String res = new String(responseBody);
                        Log.d("Lodin ", "responseBody : " + res);
                        try {
                            JSONObject jsonObject = new JSONObject(res);
                            if (jsonObject.getString("Status").equals("1")) {

                                //getFolderListData();
                                notifyDataSetChanged();
                                Message message = Message.obtain(mHandler, Constants.ServiceCode.SERVICE_DELETE, Integer.valueOf(position));
                                mHandler.sendMessage(message);

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

    private void deleteFile(String fileNo, final int position) {
        Log.i(TAG, "deleteFile() called ");

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

                String Status = "Delete";
                String statusAfterEncrypt = AndyUtils.encrypt(Status);
                String statusAfterRemveExtra = Helper.removeExtra(statusAfterEncrypt);

                String param = "?Token=" + token + "&AppsName=" + appName + "&UserName=" + userName + "&FilesID=" + folderNoAfterRemveExtra + "&Status=" + statusAfterRemveExtra;
                Log.d("Lodin ", "param : " + param);

                new ApiCall(context).firePost(true, ApiUrl.OPERATE_FILE + param, params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        String res = new String(responseBody);
                        Log.d("Lodin ", "responseBody : " + res);
                        try {
                            JSONObject jsonObject = new JSONObject(res);
                            if (jsonObject.getString("Status").equals("1")) {

                                //getFolderListData();
                                notifyDataSetChanged();

                                Message message = Message.obtain(mHandler, Constants.ServiceCode.SERVICE_DELETE, Integer.valueOf(position));
                                mHandler.sendMessage(message);

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

    private void renameFolder(String folderNo, String newRenameFolder) {
        Log.i(TAG, "renameFolder() called: for folderNo" + folderNo + ", newRenameFolder" + newRenameFolder);

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

                String Status = "Rename";
                String statusAfterEncrypt = AndyUtils.encrypt(Status);
                String statusAfterRemveExtra = Helper.removeExtra(statusAfterEncrypt);

                String renameFolderEncrypt = AndyUtils.encrypt(newRenameFolder);//pas parant folder no
                String renamefolderAfterRemveExtra = Helper.removeExtra(renameFolderEncrypt);

                String param = "?Token=" + token + "&AppsName=" + appName + "&UserName=" + userName + "&FolderNo=" + folderNoAfterRemveExtra + "&Status=" + statusAfterRemveExtra + "&FolderName=" + renamefolderAfterRemveExtra;
                Log.d("Lodin ", "param : " + param);

                new ApiCall(context).firePost(true, ApiUrl.OPERATE_FOLDER + param, params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        String res = new String(responseBody);
                        Log.d("Lodin ", "responseBody : " + res);
                        try {
                            JSONObject jsonObject = new JSONObject(res);
                            if (jsonObject.getString("Status").equals("1")) {

                                //getFolderListData();
                                notifyDataSetChanged();
                                mHandler.sendEmptyMessage(Constants.ServiceCode.SERVICE_PARSE_LIST);

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

    private void renameFile(String fileNo, String renameFileName) {
        Log.i(TAG, "renameFolder() called: for fileNo" + fileNo + ", renameFileName" + renameFileName);

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

                String Status = "Rename";
                String statusAfterEncrypt = AndyUtils.encrypt(Status);
                String statusAfterRemveExtra = Helper.removeExtra(statusAfterEncrypt);

                String renameFolderEncrypt = AndyUtils.encrypt(renameFileName);//pas parant folder no
                String renamefolderAfterRemveExtra = Helper.removeExtra(renameFolderEncrypt);

                String param = "?Token=" + token + "&AppsName=" + appName + "&UserName=" + userName + "&FileID=" + folderNoAfterRemveExtra + "&Status=" + statusAfterRemveExtra + "&FileName=" + renamefolderAfterRemveExtra;
                Log.d("Lodin ", "param : " + param);

                new ApiCall(context).firePost(true, ApiUrl.OPERATE_FILE + param, params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        String res = new String(responseBody);
                        Log.d("Lodin ", "responseBody : " + res);
                        try {
                            JSONObject jsonObject = new JSONObject(res);
                            if (jsonObject.getString("Status").equals("1")) {

                                //getFolderListData();
                                notifyDataSetChanged();
                                mHandler.sendEmptyMessage(Constants.ServiceCode.SERVICE_PARSE_LIST);

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
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }
}
