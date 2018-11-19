package com.documentcenterapp.adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.documentcenterapp.R;
import com.documentcenterapp.helper.Helper;
import com.documentcenterapp.home.FolderInFolderActivity;
import com.documentcenterapp.model.FolderListItemData;
import com.documentcenterapp.util.Constants;
import com.documentcenterapp.util.TinyDB;

import java.util.ArrayList;

@SuppressLint("LongLogTag")
public class HomeFragmentListAdapter extends RecyclerSwipeAdapter<HomeFragmentListAdapter.ViewHolder> {

	private static final String TAG = "[HomeFragmentListAdapter] : ";

	private LayoutInflater inflater;
	private static ArrayList<FolderListItemData> folderDetailArrayList;
    private static Context context;
	private int position;

	private Dialog moreDialog = null;

	private TinyDB tinyDB;

	public HomeFragmentListAdapter(Context context, LayoutInflater inflater, ArrayList<FolderListItemData> folderDetailArrayList) {
		this.context = context;
		this.inflater = inflater;
		this.folderDetailArrayList = new ArrayList<FolderListItemData>();
		this.folderDetailArrayList.addAll(folderDetailArrayList);
		tinyDB = new TinyDB(context);
	}

	public void setList(ArrayList<FolderListItemData> folderDetailArrayList) {
		Log.i(TAG, "setList(ArrayList<FolderListData>) called for folderDetailArrayList : " + folderDetailArrayList);
		this.folderDetailArrayList.clear();
		this.folderDetailArrayList.addAll(folderDetailArrayList);
		this.notifyDataSetChanged();
	}

	@Override
	public int getSwipeLayoutResourceId(int position) {
		return R.id.swipe;
	}

	public class ViewHolder extends RecyclerView.ViewHolder {

		private LinearLayout ll_delete, ll_edit, ll_more, ll_folder;
		private TextView tv_folder_name, tv_date;
		ImageView iv_check_green, iv_uncheck;

		ImageView iv_folder;

		SwipeLayout swipeLayout;

		public ViewHolder(View view) {
			super(view);
			swipeLayout = (SwipeLayout) view.findViewById(R.id.swipe);

			ll_folder = (LinearLayout)view.findViewById(R.id.ll_folder);
			ll_delete = (LinearLayout)view.findViewById(R.id.ll_delete);
			ll_edit = (LinearLayout)view.findViewById(R.id.ll_edit);
			ll_more = (LinearLayout)view.findViewById(R.id.ll_more);

			tv_date = (TextView)view.findViewById(R.id.tv_date);
			tv_folder_name = (TextView)view.findViewById(R.id.tv_folder_name);

			iv_uncheck = (ImageView)view.findViewById(R.id.iv_uncheck);
			iv_check_green = (ImageView)view.findViewById(R.id.iv_check_green);

			iv_folder = (ImageView)view.findViewById(R.id.iv_folder);
		}
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

		LayoutInflater inflater = LayoutInflater.from(parent.getContext());

		View view = inflater.inflate(R.layout.home_fragment_list_adapter_layout, parent, false);

		ViewHolder holder = new ViewHolder(view);
		return holder;
	}

	@Override
	public void onBindViewHolder(final ViewHolder holder, final int position) {

		this.position = position;

		final FolderListItemData folderListData = folderDetailArrayList.get(position);
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

		holder.tv_folder_name.setText(folderListData.getFolderName());

		String date = folderListData.getCreateDate();

		String dateAfterRemoveExtra = Helper.removeExtraFromDate(date);
		Log.i(TAG, "onBindViewHolder(), dateAfterRemoveExtra : " + dateAfterRemoveExtra);

		String parseDate = Helper.ConvertMilliSecondsToFormattedDate(dateAfterRemoveExtra);
		Log.i(TAG, "onBindViewHolder(), parseDate : " + parseDate);

		holder.tv_date.setText(parseDate);

		holder.ll_delete.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				holder.swipeLayout.close();
			}
		});

		holder.ll_edit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				holder.swipeLayout.close();
			}
		});

		holder.ll_more.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				holder.swipeLayout.close();
				moreDialog();
			}
		});

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

		holder.ll_folder.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context , FolderInFolderActivity.class);
				intent.putExtra("folderName", folderListData.getFolderName());
				intent.putExtra("folderNo", folderListData.getFolderNo());
				context.startActivity(intent);
			}
		});

		mItemManger.bindView(holder.itemView, position);

	}

	@Override
	public int getItemCount() {
		return folderDetailArrayList.size();
	}

	@SuppressLint("LongLogTag")
	private void moreDialog() {
		Log.i(TAG, "moreDialog() called: ");
		try {
			moreDialog = new Dialog(context);

			moreDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

			moreDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
			moreDialog.setContentView(R.layout.more_dialog_layout);

			TextView tv_cancel, tv_copy;

			tv_cancel = (TextView) moreDialog.findViewById(R.id.tv_cancel);
			tv_copy = (TextView) moreDialog.findViewById(R.id.tv_copy);

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
}
