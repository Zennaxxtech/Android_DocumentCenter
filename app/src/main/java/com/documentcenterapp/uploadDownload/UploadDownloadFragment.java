package com.documentcenterapp.uploadDownload;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.documentcenterapp.R;
import com.documentcenterapp.model.DownloadItemListData;
import com.documentcenterapp.util.TinyDB;

import java.util.ArrayList;
import java.util.List;

@SuppressLint({"LongLogTag", "ValidFragment"})
public class UploadDownloadFragment extends Fragment {

    private static final String TAG = "[UploadDownloadFragment] : ";

    private Context context;

    private LayoutInflater inflater;

    private TinyDB tinyDB;

    private TabLayout tabLayout;
    private ViewPager viewPager;

    int position;

    private UploadListFragment uploadListFragment;
    private DownloadListFragment downloadListFragment;

    public UploadDownloadFragment(int position) {
        this.position = position;
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

        setupViewPager(viewPager);
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        Log.i(TAG, "onResume() called ");

        try {
            viewPager.setCurrentItem(position);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.upload_download_fragment_layout, container, false);

        context = getActivity();
        this.inflater = inflater;

        viewPager = (ViewPager) rootView.findViewById(R.id.viewpager);

        tabLayout = (TabLayout) rootView.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        return rootView;
    }

    private void setupViewPager(ViewPager viewPager) {
        Log.i(TAG, "setupViewPager(ViewPager) called for viewPager : " + viewPager);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());

        uploadListFragment = new UploadListFragment();
        adapter.addFragment(uploadListFragment, context.getResources().getString(R.string.upload_list));

        downloadListFragment = new DownloadListFragment();
        adapter.addFragment(downloadListFragment, context.getResources().getString(R.string.download_list));

        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {

        private static final String INNER_TAG = "[ViewPagerAdapter] : ";

        private final List<android.support.v4.app.Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            Log.i(TAG + INNER_TAG, "getItem(int) called for position : " + position);
            android.support.v4.app.Fragment fragment = mFragmentList.get(position);
            Log.i(TAG + INNER_TAG, "getItem(), fragment.getClass().getName() : " + fragment.getClass().getName());
            return fragment;
        }

        @Override
        public int getCount() {
            int count = mFragmentList.size();
            Log.i(TAG + INNER_TAG, "getCount(), count : " + count);
            return count;
        }

        public void addFragment(android.support.v4.app.Fragment fragment, String title) {
            Log.i(TAG + INNER_TAG, "addFragment(Fragment, String) called for fragment : " + fragment.getClass().getName() + ", title : " + title);
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);

            Log.i(TAG + INNER_TAG, "addFragment(), mFragmentList : " + mFragmentList.toString());
            Log.i(TAG + INNER_TAG, "addFragment(), mFragmentTitleList : " + mFragmentTitleList.toString());
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Log.i(TAG + INNER_TAG, "getPageTitle(int) called for position : " + position);
            String title = mFragmentTitleList.get(position);
            Log.i(TAG + INNER_TAG, "getPageTitle(), title : " + title);
            return title;
        }
    }
}