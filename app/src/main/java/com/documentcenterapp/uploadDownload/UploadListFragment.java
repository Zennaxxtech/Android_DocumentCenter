package com.documentcenterapp.uploadDownload;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.documentcenterapp.R;
import com.documentcenterapp.adapter.UploadListFragmentAdapter;
import com.documentcenterapp.helper.ProgressHelper;
import com.documentcenterapp.home.HomeFragment;
import com.documentcenterapp.util.ItemClickSupport;

import mbanje.kurt.fabbutton.FabButton;

@SuppressLint("LongLogTag")
public class UploadListFragment extends Fragment {

    private static final String TAG = "[UploadListFragment] : ";

    private Context context;

    private LayoutInflater inflater;

    private RelativeLayout relNoDataFound;

    private RecyclerView rv_upload_list;
    private UploadListFragmentAdapter uploadListFragmentAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private static int position = 0;
    private static View view;

    public UploadListFragment() {
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        Log.i(TAG, "onResume() called ");

        try {

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
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.upload_list_fragment_layout, container, false);

        this.inflater = inflater;

        rv_upload_list = (RecyclerView) rootView.findViewById(R.id.rv_upload_list);

        relNoDataFound = (RelativeLayout) rootView.findViewById(R.id.relNoDataFound);

        mLayoutManager = new LinearLayoutManager(context);
        rv_upload_list.setLayoutManager(mLayoutManager);
        rv_upload_list.setItemAnimator(new DefaultItemAnimator());

        uploadListFragmentAdapter = new UploadListFragmentAdapter(context, inflater);
        rv_upload_list.setAdapter(uploadListFragmentAdapter);

        ItemClickSupport.addTo(rv_upload_list)
                .setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View view) {
                        try {
                            UploadListFragment.view = view;
                            UploadListFragment.position = position;

                            final LinearLayout ll_upload, ll_download_with_play_icon;
                            final FabButton button;

                            button = (FabButton) view.findViewById(R.id.determinate_upload);
                            ll_upload = (LinearLayout)view.findViewById(R.id.ll_upload);
                            ll_download_with_play_icon = (LinearLayout)view.findViewById(R.id.ll_upload_with_play_icon);

                            final ProgressHelper helper = new ProgressHelper(button, getActivity());

                            ll_upload.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    ll_upload.setVisibility(View.GONE);
                                    ll_download_with_play_icon.setVisibility(View.VISIBLE);
                                    helper.startDeterminate();
                                }
                            });

                            button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            });

                            Log.i(TAG, "onItemClicked(), UploadListFragment.position : " + UploadListFragment.position);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

        return rootView;
    }
}