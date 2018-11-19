package com.documentcenterapp.adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.documentcenterapp.R;
import com.documentcenterapp.database.DatabaseHelperDownloadComplete;
import com.documentcenterapp.helper.Helper;
import com.documentcenterapp.model.DownloadItemListData;
import com.documentcenterapp.util.Constants;
import com.documentcenterapp.util.ImageLazyLoadingConfig;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.text.DecimalFormat;
import java.util.ArrayList;

@SuppressLint("LongLogTag")
public class DownloadListCompleteFragmentAdapter extends RecyclerSwipeAdapter<DownloadListCompleteFragmentAdapter.ViewHolder> {

    private static final String TAG = "[DownloadListCompleteFragmentAdapter] : ";

    private LayoutInflater inflater;
    private static ArrayList<DownloadItemListData> downloadItemListDataArrayList;
    private static Context context;
    private Handler mHandler;
    private int position;
    private DatabaseHelperDownloadComplete db;

    private Dialog deleteDialog = null;

    private long fileinMb;

    public DownloadListCompleteFragmentAdapter(Context context, Handler mHandler, LayoutInflater inflater, ArrayList<DownloadItemListData> downloadItemListDataArrayList) {
        this.context = context;
        this.inflater = inflater;
        this.mHandler = mHandler;
        this.downloadItemListDataArrayList = new ArrayList<DownloadItemListData>();
        this.downloadItemListDataArrayList.addAll(downloadItemListDataArrayList);
    }

    public void setBuyEnquiriesList(ArrayList<DownloadItemListData> downloadItemListDataArrayList) {
        Log.i(TAG, "setBuyEnquiriesList(ArrayList<DownloadItemListData>) called for downloadItemListDataArrayList : " + downloadItemListDataArrayList);
        this.downloadItemListDataArrayList.clear();
        this.downloadItemListDataArrayList.addAll(downloadItemListDataArrayList);
        this.notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView file_name, tv_date;
        ImageView iv_file_icon;

        SwipeLayout swipeLayout;

        final LinearLayout ll_cancel, ll_delete;
        private TextView tv_download_size;

        public ViewHolder(View view) {
            super(view);

            swipeLayout = (SwipeLayout) view.findViewById(R.id.swipe);

            file_name = (TextView) view.findViewById(R.id.file_name);
            tv_date = (TextView) view.findViewById(R.id.tv_date);

            iv_file_icon = (ImageView) view.findViewById(R.id.iv_file_icon);

            ll_cancel = (LinearLayout) view.findViewById(R.id.ll_cancel);
            ll_delete = (LinearLayout) view.findViewById(R.id.ll_delete);

            tv_download_size = (TextView) view.findViewById(R.id.tv_download_size);
        }
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(R.layout.download_complete_list_fragment_adapter_layout, parent, false);

        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        this.position = position;

        db = new DatabaseHelperDownloadComplete(context);

        final DownloadItemListData folderListData = downloadItemListDataArrayList.get(position);
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

        holder.file_name.setText(folderListData.getFileName());
        holder.tv_date.setText(folderListData.getDate());

        String imageURL = folderListData.getIcon();
        if (imageURL != null) {
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
                                holder.iv_file_icon.setImageResource(R.drawable.no_file_icon);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {

                    }
                };
                Helper.displayImageUsingImageLoader(context, imageURL, holder.iv_file_icon, R.drawable.ic_launcher, imageLoadingListener);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            holder.iv_file_icon.setImageResource(R.drawable.no_file_icon);
        }

        String fileSizeInBytes = size(folderListData.getFileSize());

        holder.tv_download_size.setText(fileSizeInBytes);

        Log.i(TAG, "onBindViewHolder() called for fileSizeInBytes : " + fileSizeInBytes);

        holder.ll_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //PRDownloader.cancel(downloadIdOne);
                holder.swipeLayout.close();
            }
        });

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
                            Message message = Message.obtain(mHandler, Constants.ServiceCode.SERVICE_DELETE_COMPLETED, Integer.valueOf(position));
                            mHandler.sendMessage(message);
                            deleteDialog.dismiss();
                        }
                    });

                    deleteDialog.setCancelable(false);

                    deleteDialog.dismiss();

                    deleteDialog.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        mItemManger.bindView(holder.itemView, position);
    }

    public String size(int size) {
        String hrSize = "";

        double b = size;
        double k = size / 1024.0;
        double m = ((size / 1024.0) / 1024.0);
        double g = (((size / 1024.0) / 1024.0) / 1024.0);
        double t = ((((size / 1024.0) / 1024.0) / 1024.0) / 1024.0);

        DecimalFormat dec = new DecimalFormat("0.00");
        if (t > 1) {
            hrSize = dec.format(t).concat(" TB");
        } else if (g > 1) {
            hrSize = dec.format(g).concat(" GB");
        } else if (m > 1) {
            hrSize = dec.format(m).concat(" MB");
        } else if (k > 1) {
            hrSize = dec.format(k).concat(" KB");
        } else {
            hrSize = dec.format(b).concat(" Bytes");
        }
        return hrSize;
    }

    @Override
    public int getItemCount() {
        return downloadItemListDataArrayList.size();
    }
}
