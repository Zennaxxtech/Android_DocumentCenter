package com.documentcenterapp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.documentcenterapp.MainActivity;
import com.documentcenterapp.R;
import com.documentcenterapp.helper.Helper;
import com.documentcenterapp.home.FolderInFolderActivity;
import com.documentcenterapp.login.LoginActivity;
import com.documentcenterapp.model.FolderListItemData;
import com.documentcenterapp.util.Constants;
import com.documentcenterapp.util.TinyDB;

import java.util.ArrayList;

@SuppressLint("LongLogTag")

public class HomeFragmentGridAdapter extends RecyclerView.Adapter<HomeFragmentGridAdapter.ViewHolder>{

	private static final String TAG = "[HomeFragmentGridAdapter] : ";

	private LayoutInflater inflater;
	private static ArrayList<FolderListItemData> folderDetailArrayList;
    private static Context context;
	private Handler mHandler;
	private int position;

	private TinyDB tinyDB;

	public HomeFragmentGridAdapter(Context context, LayoutInflater inflater, ArrayList<FolderListItemData> folderDetailArrayList) {
		this.context = context;
		this.inflater = inflater;
		this.folderDetailArrayList = new ArrayList<FolderListItemData>();
		this.folderDetailArrayList.addAll(folderDetailArrayList);
		tinyDB = new TinyDB(context);
	}

	public void setGridList(ArrayList<FolderListItemData> folderDetailArrayList) {
		Log.i(TAG, "setGridList(ArrayList<FolderListData>) called for folderDetailArrayList : " + folderDetailArrayList);
		this.folderDetailArrayList.clear();
		this.folderDetailArrayList.addAll(folderDetailArrayList);
		this.notifyDataSetChanged();
	}

	public class ViewHolder extends RecyclerView.ViewHolder {

		private TextView tv_folder_name, tv_date;
		ImageView iv_check_green, iv_uncheck;
		ImageView iv_folder;
		LinearLayout ll_grid_folder;

		public ViewHolder(View view) {
			super(view);

			tv_folder_name = (TextView)view.findViewById(R.id.tv_folder_name);
			tv_date = (TextView)view.findViewById(R.id.tv_date);

			iv_uncheck = (ImageView)view.findViewById(R.id.iv_uncheck);
			iv_check_green = (ImageView)view.findViewById(R.id.iv_check_green);
			iv_folder = (ImageView)view.findViewById(R.id.iv_folder);
		}
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

		LayoutInflater inflater = LayoutInflater.from(parent.getContext());

		View view = inflater.inflate(R.layout.home_fragment_grid_adapter_layout, parent, false);

		ViewHolder holder = new ViewHolder(view);
		return holder;
	}

	@Override
	public void onBindViewHolder(final ViewHolder holder, final int position) {

		this.position = position;

		final FolderListItemData folderListData = folderDetailArrayList.get(position);
		Log.i(TAG, "onBindViewHolder(), folderListData : " + folderListData.toString());

		holder.tv_folder_name.setText(folderListData.getFolderName());

		String date = folderListData.getCreateDate();

		String dateAfterRemoveExtra = Helper.removeExtraFromDate(date);
		Log.i(TAG, "onBindViewHolder(), dateAfterRemoveExtra : " + dateAfterRemoveExtra);

		String parseDate = Helper.ConvertMilliSecondsToFormattedDate(dateAfterRemoveExtra);
		Log.i(TAG, "onBindViewHolder(), parseDate : " + parseDate);

		holder.tv_date.setText(parseDate);

		boolean checkUncheck = tinyDB.getBoolean(Constants.Preferences.PREF_CHECK_UNCHECK);
		if(checkUncheck){
			holder.iv_check_green.setVisibility(View.GONE);
			holder.iv_uncheck.setVisibility(View.VISIBLE);
		}

		holder.iv_check_green.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				holder.iv_uncheck.setVisibility(View.VISIBLE);
				holder.iv_check_green.setVisibility(View.GONE);
			}
		});

		holder.iv_uncheck.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				holder.iv_uncheck.setVisibility(View.GONE);
				holder.iv_check_green.setVisibility(View.VISIBLE);
			}
		});

		holder.iv_folder.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context , FolderInFolderActivity.class);
				intent.putExtra("folderName", folderListData.getFolderName());
				intent.putExtra("folderNo", folderListData.getFolderNo());
				context.startActivity(intent);
			}
		});
	}

	@Override
	public int getItemCount() {
		return folderDetailArrayList.size();
	}
}
