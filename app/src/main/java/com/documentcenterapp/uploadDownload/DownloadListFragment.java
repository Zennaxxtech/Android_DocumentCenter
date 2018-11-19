package com.documentcenterapp.uploadDownload;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.documentcenterapp.R;
import com.documentcenterapp.adapter.DownloadListCompleteFragmentAdapter;
import com.documentcenterapp.adapter.DownloadListFragmentAdapter;
import com.documentcenterapp.database.DatabaseHelper;
import com.documentcenterapp.database.DatabaseHelperDownloadComplete;
import com.documentcenterapp.model.DownloadItemListData;
import com.documentcenterapp.util.Constants;
import com.documentcenterapp.util.ItemClickSupport;

import java.util.ArrayList;

@SuppressLint({"LongLogTag", "ValidFragment"})
public class DownloadListFragment extends Fragment {

    private static final String TAG = "[DownloadListFragment] : ";

    private Context context;

    private LayoutInflater inflater;

    private RelativeLayout relNoDataFound;

    private RecyclerView rv_download_list, rv_complete_list;
    private DownloadListFragmentAdapter downloadListFragmentAdapter;
    private DownloadListCompleteFragmentAdapter downloadListCompleteFragmentAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.LayoutManager mLayoutManagercomplete;

    private static int position = 0;
    private static View view;

    private DatabaseHelper db;
    private DatabaseHelperDownloadComplete dbDownloadComplete;

    private ArrayList<DownloadItemListData> downloadItemListDataArrayList = new ArrayList<DownloadItemListData>();
    private ArrayList<DownloadItemListData> downloadCompleteItemListDataArrayList = new ArrayList<DownloadItemListData>();

    private Handler mHandler = new Handler() {

        public void handleMessage(android.os.Message msg) {
            Log.i(TAG, "in mHandler class");
            switch (msg.what) {

                case Constants.ServiceCode.SERVICE_PARSE_LIST:
                    Log.i(TAG, "handleMessage(), Constants.ServiceCode.SERVICE_PARSE_LIST");
                    parseDownloadCompleteList();
                    parseDownloadList();
                    break;

                case Constants.ServiceCode.SERVICE_DELETE:
                    Log.i(TAG, "handleMessage(), Constants.ServiceCode.SERVICE_DELETE");
                    try {
                        if (downloadItemListDataArrayList != null && downloadItemListDataArrayList.size() > 0) {
                            if (msg.obj != null) {
                                int position = ((Integer) msg.obj).intValue();
                                Log.i(TAG, "handleMessage(), position : " + position);
                                if (position >= 0 && position < downloadItemListDataArrayList.size()) {
                                    db.deleteNote(downloadItemListDataArrayList.get(position));
                                    downloadItemListDataArrayList.remove(position);
                                    mHandler.sendEmptyMessage(Constants.ServiceCode.SERVICE_PARSE_LIST);
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

                case Constants.ServiceCode.SERVICE_DELETE_COMPLETED:
                    Log.i(TAG, "handleMessage(), Constants.ServiceCode.SERVICE_DELETE_COMPLETED");
                    try {
                        if (downloadCompleteItemListDataArrayList != null && downloadCompleteItemListDataArrayList.size() > 0) {
                            if (msg.obj != null) {
                                int position = ((Integer) msg.obj).intValue();
                                Log.i(TAG, "handleMessage(), position : " + position);
                                if (position >= 0 && position < downloadCompleteItemListDataArrayList.size()) {
                                    dbDownloadComplete.deleteNote(downloadCompleteItemListDataArrayList.get(position));
                                    downloadCompleteItemListDataArrayList.remove(position);
                                    mHandler.sendEmptyMessage(Constants.ServiceCode.SERVICE_PARSE_LIST);
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

                default:
                    break;
            }
        }
    };

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        Log.i(TAG, "onResume() called ");

        try {
            parseDownloadList();
            parseDownloadCompleteList();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        db = new DatabaseHelper(getActivity());
        dbDownloadComplete = new DatabaseHelperDownloadComplete(getActivity());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.download_list_fragment_layout, container, false);

        this.inflater = inflater;

        rv_download_list = (RecyclerView) rootView.findViewById(R.id.rv_download_list);
        rv_complete_list = (RecyclerView) rootView.findViewById(R.id.rv_complete_list);

        relNoDataFound = (RelativeLayout) rootView.findViewById(R.id.relNoDataFound);

        mLayoutManager = new LinearLayoutManager(context);
        mLayoutManagercomplete = new LinearLayoutManager(context);

        rv_download_list.setLayoutManager(mLayoutManager);
        rv_download_list.setItemAnimator(new DefaultItemAnimator());

        rv_complete_list.setLayoutManager(mLayoutManagercomplete);
        rv_complete_list.setItemAnimator(new DefaultItemAnimator());

        rv_complete_list.setNestedScrollingEnabled(false);
        rv_download_list.setNestedScrollingEnabled(false);

        ItemClickSupport.addTo(rv_download_list)
                .setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View view) {
                        try {
                            DownloadListFragment.view = view;
                            DownloadListFragment.position = position;

                            Log.i(TAG, "onItemClicked(), DownloadListFragment.position : " + DownloadListFragment.position);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

        return rootView;
    }

    private void parseDownloadList() {
        Log.i(TAG, "parseDownloadList(), called : ");

        downloadItemListDataArrayList = new ArrayList<>();

        downloadItemListDataArrayList.addAll(db.getAllNotes());

        downloadListFragmentAdapter = new DownloadListFragmentAdapter(context, mHandler, inflater, downloadItemListDataArrayList);
        rv_download_list.setAdapter(downloadListFragmentAdapter);
    }

    private void parseDownloadCompleteList() {
        Log.i(TAG, "parseDownloadCompleteList(), called : ");

        downloadCompleteItemListDataArrayList = new ArrayList<>();

        downloadCompleteItemListDataArrayList.addAll(dbDownloadComplete.getAllNotes());

        downloadListCompleteFragmentAdapter = new DownloadListCompleteFragmentAdapter(context, mHandler, inflater, downloadCompleteItemListDataArrayList);
        rv_complete_list.setAdapter(downloadListCompleteFragmentAdapter);
    }
}