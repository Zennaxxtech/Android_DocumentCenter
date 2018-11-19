package com.documentcenterapp.adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Environment;
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
import android.widget.Toast;

import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.documentcenterapp.R;
import com.documentcenterapp.database.DatabaseHelper;
import com.documentcenterapp.database.DatabaseHelperDownloadComplete;
import com.documentcenterapp.helper.Helper;
import com.documentcenterapp.model.DownloadItemListData;
import com.documentcenterapp.util.Constants;
import com.documentcenterapp.util.ImageLazyLoadingConfig;
import com.documentcenterapp.util.OnProgressListener;
import com.downloader.Error;
import com.downloader.OnCancelListener;
import com.downloader.OnDownloadListener;
import com.downloader.OnPauseListener;
import com.downloader.OnStartOrResumeListener;
import com.downloader.PRDownloader;
import com.downloader.PRDownloaderConfig;
import com.downloader.Progress;
import com.downloader.Status;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;

import mbanje.kurt.fabbutton.FabButton;

@SuppressLint("LongLogTag")
public class DownloadListFragmentAdapter extends RecyclerSwipeAdapter<DownloadListFragmentAdapter.ViewHolder> {

    private static final String TAG = "[DownloadListFragmentAdapter] : ";

    private LayoutInflater inflater;
    private static ArrayList<DownloadItemListData> downloadItemListDataArrayList;
    private static ArrayList<DownloadItemListData> downloadItemCompleteListDataArrayList;
    private static Context context;
    private Handler mHandler;
    private int position;
    int downloadIdOne;
    private DatabaseHelper db;
    private DatabaseHelperDownloadComplete dbDownloadComplete;

    private Dialog deleteDialog = null;

    private static String dirPath;

    public DownloadListFragmentAdapter(Context context, Handler mHandler, LayoutInflater inflater, ArrayList<DownloadItemListData> downloadItemListDataArrayList) {
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

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView file_name, tv_date;
        ImageView iv_file_icon;

        SwipeLayout swipeLayout;

        final LinearLayout ll_download_with_play_icon, ll_cancel, ll_delete;
        final FabButton determinate_download;
        private TextView tv_download_size, tv_progress_percentage;

        public ViewHolder(View view) {
            super(view);

            swipeLayout = (SwipeLayout) view.findViewById(R.id.swipe);

            file_name = (TextView) view.findViewById(R.id.file_name);
            tv_date = (TextView) view.findViewById(R.id.tv_date);

            iv_file_icon = (ImageView) view.findViewById(R.id.iv_file_icon);

            determinate_download = (FabButton) view.findViewById(R.id.determinate_download);
            ll_download_with_play_icon = (LinearLayout) view.findViewById(R.id.ll_download_with_play_icon);

            ll_cancel = (LinearLayout) view.findViewById(R.id.ll_cancel);
            ll_delete = (LinearLayout) view.findViewById(R.id.ll_delete);

            tv_download_size = (TextView) view.findViewById(R.id.tv_download_size);
            tv_progress_percentage = (TextView) view.findViewById(R.id.tv_progress_percentage);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(R.layout.download_list_fragment_adapter_layout, parent, false);

        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        this.position = position;

        db = new DatabaseHelper(context);
        dbDownloadComplete = new DatabaseHelperDownloadComplete(context);

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

        PRDownloaderConfig config = PRDownloaderConfig.newBuilder()
                .setDatabaseEnabled(true)
                .build();
        PRDownloader.initialize(context, config);

        dirPath = com.documentcenterapp.util.Utils.getRootDirPath(context);

        holder.file_name.setText(folderListData.getFileName());
        holder.tv_date.setText(folderListData.getDate());

        final String fileDownloadUrl = folderListData.getDownladLink();

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

        final String downloadLink = fileDownloadUrl;
        Log.i(TAG, "onBindViewHolder() called for downloadLink : " + downloadLink);

        String fileSizeInBytes = size(folderListData.getFileSize());

        if (Status.RUNNING == PRDownloader.getStatus(downloadIdOne)) {
            PRDownloader.pause(downloadIdOne);
            return;
        }

        holder.determinate_download.setIndeterminate(true);

        if (Status.PAUSED == PRDownloader.getStatus(downloadIdOne)) {
            PRDownloader.resume(downloadIdOne);
            return;
        }

        downloadIdOne = PRDownloader.download(downloadLink, dirPath, folderListData.getFileName())
                .build()
                .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                    @Override
                    public void onStartOrResume() {
                        Log.i(TAG, "onStartOrResume() called for downloadLink : " + downloadLink);
                        holder.determinate_download.setIndeterminate(false);
                        holder.determinate_download.setIcon(context.getResources().getDrawable(R.drawable.stop_icon_big), context.getResources().getDrawable(R.drawable.complete_icon));
                    }
                })
                .setOnPauseListener(new OnPauseListener() {
                    @Override
                    public void onPause() {
                        holder.determinate_download.setIcon(context.getResources().getDrawable(R.drawable.play_icon), context.getResources().getDrawable(R.drawable.complete_icon));
                    }
                })
                .setOnCancelListener(new OnCancelListener() {
                    @Override
                    public void onCancel() {
                        holder.determinate_download.setIcon(context.getResources().getDrawable(R.drawable.play_icon), context.getResources().getDrawable(R.drawable.complete_icon));
                        holder.determinate_download.setProgress(0);
                        holder.tv_download_size.setText("");
                        downloadIdOne = 0;
                        holder.determinate_download.setIndeterminate(false);
                    }
                })
                .setOnProgressListener(new OnProgressListener() {
                    @Override
                    public void onProgress(BaseDownloadTask task, Progress progress) {
                        long progressPercent = progress.currentBytes * 100 / progress.totalBytes;
                       /* holder.tv_progress_percentage.setText("" + progressPercent + " %");
                        holder.determinate_download.showShadow(true);
                        holder.determinate_download.setProgress((int) progressPercent);
                        //holder.tv_download_size.setText(com.documentcenterapp.util.Utils.getDownloadSpeedString(context, progress.currentBytes));
                        holder.determinate_download.setIndeterminate(false);

                        holder.tv_download_size.setText(String.format("%dKB/s", task.getSpeed()));*/
                    }

                    @Override
                    public void onProgress(Progress progress) {
                        long progressPercent = progress.currentBytes * 100 / progress.totalBytes;
                        holder.tv_progress_percentage.setText("" + progressPercent + " %");
                        holder.determinate_download.showShadow(true);
                        holder.determinate_download.setProgress((int) progressPercent);
                        holder.tv_download_size.setText(com.documentcenterapp.util.Utils.formatSpeed(folderListData.getFileSize() / progress.currentBytes));
                        holder.determinate_download.setIndeterminate(false);
                    }
                })
                .start(new OnDownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        holder.determinate_download.setVisibility(View.GONE);

                        createNewDownload(folderListData.getFileName(), String.valueOf(folderListData.getFileSize()), folderListData.getDownladLink(), folderListData.getIcon(), folderListData.getDate());

                        Message message = Message.obtain(mHandler, Constants.ServiceCode.SERVICE_DELETE, Integer.valueOf(position));
                        mHandler.sendMessage(message);
                    }

                    @Override
                    public void onError(Error error) {
                        Toast.makeText(context, "Some error" + " " + "1", Toast.LENGTH_SHORT).show();
                        holder.tv_download_size.setText("");
                        holder.determinate_download.setProgress(0);
                        downloadIdOne = 0;
                        holder.determinate_download.setIndeterminate(false);
                    }
                });

        holder.determinate_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Status.RUNNING == PRDownloader.getStatus(downloadIdOne)) {
                    PRDownloader.pause(downloadIdOne);
                    return;
                }

                holder.determinate_download.setIndeterminate(true);

                if (Status.PAUSED == PRDownloader.getStatus(downloadIdOne)) {
                    PRDownloader.resume(downloadIdOne);
                    return;
                }

                downloadIdOne = PRDownloader.download(downloadLink, dirPath, folderListData.getFileName())
                        .build()
                        .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                            @Override
                            public void onStartOrResume() {
                                Log.i(TAG, "onStartOrResume() called for downloadLink : " + downloadLink);
                                holder.determinate_download.setIndeterminate(false);
                                holder.determinate_download.setIcon(context.getResources().getDrawable(R.drawable.stop_icon_big), context.getResources().getDrawable(R.drawable.complete_icon));
                            }
                        })
                        .setOnPauseListener(new OnPauseListener() {
                            @Override
                            public void onPause() {
                                holder.determinate_download.setIcon(context.getResources().getDrawable(R.drawable.play_icon), context.getResources().getDrawable(R.drawable.complete_icon));
                            }
                        })
                        .setOnCancelListener(new OnCancelListener() {
                            @Override
                            public void onCancel() {
                                holder.determinate_download.setIcon(context.getResources().getDrawable(R.drawable.play_icon), context.getResources().getDrawable(R.drawable.complete_icon));
                                holder.determinate_download.setProgress(0);
                                holder.tv_download_size.setText("");
                                downloadIdOne = 0;
                                holder.determinate_download.setIndeterminate(false);
                            }
                        })
                        .setOnProgressListener(new OnProgressListener() {
                            @Override
                            public void onProgress(BaseDownloadTask task, Progress progress) {
                                long progressPercent = progress.currentBytes * 100 / progress.totalBytes;
                              /*  holder.determinate_download.showShadow(true);
                                holder.determinate_download.setProgress((int) progressPercent);
                                //holder.tv_download_size.setText(com.documentcenterapp.util.Utils.getDownloadSpeedString(context, progress.currentBytes));
                                holder.tv_download_size.setText(com.documentcenterapp.util.Utils.getProgressDisplayLine(progress.currentBytes, progress.totalBytes));
                                holder.determinate_download.setIndeterminate(false);

                                holder.tv_download_size.setText(String.format("%dKB/s", task.getSpeed()));*/
                            }

                            @Override
                            public void onProgress(Progress progress) {
                                long progressPercent = progress.currentBytes * 100 / progress.totalBytes;
                                holder.tv_progress_percentage.setText("" + progressPercent + " %");
                                holder.determinate_download.showShadow(true);
                                holder.determinate_download.setProgress((int) progressPercent);
                                holder.tv_download_size.setText(com.documentcenterapp.util.Utils.formatSpeed(folderListData.getFileSize() / progress.currentBytes));
                                holder.determinate_download.setIndeterminate(false);
                            }
                        })
                        .start(new OnDownloadListener() {
                            @Override
                            public void onDownloadComplete() {
                                holder.determinate_download.setVisibility(View.GONE);

                                createNewDownload(folderListData.getFileName(), String.valueOf(folderListData.getFileSize()), folderListData.getDownladLink(), folderListData.getIcon(), folderListData.getDate());

                                Message message = Message.obtain(mHandler, Constants.ServiceCode.SERVICE_DELETE, Integer.valueOf(position));
                                mHandler.sendMessage(message);
                            }

                            @Override
                            public void onError(Error error) {
                                Toast.makeText(context, "Some error" + " " + "1", Toast.LENGTH_SHORT).show();
                                holder.tv_download_size.setText("");
                                holder.determinate_download.setProgress(0);
                                downloadIdOne = 0;
                                holder.determinate_download.setIndeterminate(false);
                            }
                        });
            }
        });

        holder.ll_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.swipeLayout.close();
                PRDownloader.cancel(downloadIdOne);
                holder.determinate_download.setIcon(context.getResources().getDrawable(R.drawable.play_icon), context.getResources().getDrawable(R.drawable.complete_icon));
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
                            Message message = Message.obtain(mHandler, Constants.ServiceCode.SERVICE_DELETE, Integer.valueOf(position));
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

    public String size(int size){
        String hrSize = "";

        double b = size;
        double k = size/1024.0;
        double m = ((size/1024.0)/1024.0);
        double g = (((size/1024.0)/1024.0)/1024.0);
        double t = ((((size/1024.0)/1024.0)/1024.0)/1024.0);

        DecimalFormat dec = new DecimalFormat("0.00");
        if ( t>1 ) {
            hrSize = dec.format(t).concat(" TB");
        } else if ( g>1 ) {
            hrSize = dec.format(g).concat(" GB");
        } else if ( m>1 ) {
            hrSize = dec.format(m).concat(" MB");
        } else if ( k>1 ) {
            hrSize = dec.format(k).concat(" KB");
        } else {
            hrSize = dec.format(b).concat(" Bytes");
        }
        return hrSize;
    }

    public void deleteRecord(final int position) {
        try {
            if (downloadItemListDataArrayList != null && downloadItemListDataArrayList.size() > 0) {

                Log.i(TAG, "handleMessage(), position : " + position);
                if (position >= 0 && position < downloadItemListDataArrayList.size()) {

                    db.deleteNote(downloadItemListDataArrayList.get(position));

                    // removing the note from the list
                    downloadItemListDataArrayList.remove(position);
                    notifyItemRemoved(position);
                }
            }
            mHandler.sendEmptyMessage(Constants.ServiceCode.SERVICE_PARSE_LIST);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createNewDownload(String name, String fileSize, String downladlink, String icon, String date) {

//        downloadItemCompleteListDataArrayList.clear();
        downloadItemCompleteListDataArrayList = new ArrayList<>();
        // inserting note in db and getting
        // newly inserted note id
        long id = dbDownloadComplete.insertNote(name, fileSize, downladlink, icon, date);

        // get the newly inserted note from db
        DownloadItemListData n = dbDownloadComplete.getNote(id);

        if (n != null) {
            // adding new note to array list at 0 position
            downloadItemCompleteListDataArrayList.add(0, n);
        }
    }

    @Override
    public int getItemCount() {
        return downloadItemListDataArrayList.size();
    }
}
