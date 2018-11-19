package com.documentcenterapp.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.documentcenterapp.R;
import com.documentcenterapp.helper.Helper;
import com.documentcenterapp.helper.ProgressHelper;
import com.documentcenterapp.requestResponse.AndyUtils;
import com.documentcenterapp.requestResponse.ApiCall;
import com.documentcenterapp.requestResponse.ApiUrl;
import com.documentcenterapp.util.Constants;
import com.documentcenterapp.util.TinyDB;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import mbanje.kurt.fabbutton.FabButton;

@SuppressLint("LongLogTag")
public class UploadListFragmentAdapter extends RecyclerView.Adapter<UploadListFragmentAdapter.ViewHolder>{

	private static final String TAG = "[UploadListFragmentAdapter] : ";

	private LayoutInflater inflater;
	private static ArrayList<String> enquiryJsonDataArrayList;
    private static Context context;
	private Handler mHandler;
	private int position;
	private TinyDB tinyDB;

	public UploadListFragmentAdapter(Context context, LayoutInflater inflater/*, Handler mHandler, ArrayList<String> enquiryJsonDataArrayList*/) {
		this.context = context;
		this.inflater = inflater;
		this.mHandler = mHandler;
		tinyDB = new TinyDB(context);
		/*this.enquiryJsonDataArrayList = new ArrayList<EnquiryJsonData>();
		this.enquiryJsonDataArrayList.addAll(enquiryJsonDataArrayList);*/
	}

	public void setBuyEnquiriesList(ArrayList<String> enquiryJsonDataArrayList) {
		Log.i(TAG, "setBuyEnquiriesList(ArrayList<String>) called for enquiryJsonDataArrayList : " + enquiryJsonDataArrayList);
		/*this.enquiryJsonDataArrayList.clear();
		this.enquiryJsonDataArrayList.addAll(enquiryJsonDataArrayList);*/
		this.notifyDataSetChanged();
	}

	public class ViewHolder extends RecyclerView.ViewHolder {

		private LinearLayout ll_download, ll_download_with_play_icon;
		final FabButton button;

		public ViewHolder(View view) {
			super(view);

			button = (FabButton) view.findViewById(R.id.determinate_upload);
			ll_download = (LinearLayout)view.findViewById(R.id.ll_download);
			ll_download_with_play_icon = (LinearLayout)view.findViewById(R.id.ll_download_with_play_icon);
		}
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

		LayoutInflater inflater = LayoutInflater.from(parent.getContext());

		View view = inflater.inflate(R.layout.upload_list_fragment_adapter_layout, parent, false);

		ViewHolder holder = new ViewHolder(view);
		return holder;
	}

	@Override
	public void onBindViewHolder(final ViewHolder holder, final int position) {

		this.position = position;
	}

	@Override
	public int getItemCount() {
		return 4;
	}
}
