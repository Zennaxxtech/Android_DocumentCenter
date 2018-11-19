package com.documentcenterapp.home;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.documentcenterapp.R;
import com.documentcenterapp.adapter.HomeFragmentGridAdapter;
import com.documentcenterapp.adapter.HomeFragmentListAdapter;
import com.documentcenterapp.helper.Helper;
import com.documentcenterapp.model.FolderListData;
import com.documentcenterapp.model.FolderListItemData;
import com.documentcenterapp.model.NewFolderData;
import com.documentcenterapp.model.NewFolderItemData;
import com.documentcenterapp.requestResponse.AndyUtils;
import com.documentcenterapp.requestResponse.ApiCall;
import com.documentcenterapp.requestResponse.ApiUrl;
import com.documentcenterapp.util.Constants;
import com.documentcenterapp.util.GridSpacingItemDecoration;
import com.documentcenterapp.util.ItemClickSupport;
import com.documentcenterapp.util.TinyDB;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

@SuppressLint("LongLogTag")
public class HomeFragment extends Fragment {

    private static final String TAG = "[HomeFragment] : ";

    private Context context;

    private LayoutInflater inflater;

    private RelativeLayout relNoDataFound;

    private RecyclerView rv_file_list;
    private RecyclerView rv_file_grid_type_list;

    private HomeFragmentGridAdapter homeFragmentGridAdapter;
    private HomeFragmentListAdapter homeFragmentListAdapter;

    private RecyclerView.LayoutManager mLayoutManager;
    private GridLayoutManager gridLayoutManager;

    private ImageView iv_grid, iv_list;

    private int numberOfColumns = 3;

    private LinearLayout ll_download_move_copy_delet, ll_sorting_newfolder;

    private LinearLayout ll_sorting, ll_folder;

    private Dialog sortingOrderDialog = null;
    private Dialog folderDialog = null;
    private Dialog folderLongPressDialog = null;

    private static int position = 0;
    private static View view;
    private LinearLayout ll_delete, ll_edit, ll_more;

    private ImageView iv_selet_check_green, iv_select_uncheck;

    private TinyDB tinyDB;

    int spanCount = 3; // 3 columns
    int spacing = 30; // 50px
    boolean includeEdge = false;

    private ArrayList<FolderListItemData> folderDetailArrayList;

    public HomeFragment() {
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        Log.i(TAG, "onResume() called ");

        try {
            getFolderListData();
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

        tinyDB = new TinyDB(getActivity());

        Log.i(TAG, "onActivityCreated() called ");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.home_fragment_layout, container, false);

        context = getActivity();
        this.inflater = inflater;

        ll_download_move_copy_delet = (LinearLayout) rootView.findViewById(R.id.ll_download_move_copy_delet);
        ll_sorting_newfolder = (LinearLayout) rootView.findViewById(R.id.ll_sorting_newfolder);

        ll_folder = (LinearLayout) rootView.findViewById(R.id.ll_folder);
        ll_folder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newFolderDialog();
            }
        });

        ll_sorting = (LinearLayout) rootView.findViewById(R.id.ll_sirting);
        ll_sorting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortingOrderDialog();
            }
        });

        rv_file_list = (RecyclerView) rootView.findViewById(R.id.rv_file_list);
        rv_file_grid_type_list = (RecyclerView) rootView.findViewById(R.id.rv_file_grid_type_list);
        relNoDataFound = (RelativeLayout) rootView.findViewById(R.id.relNoDataFound);

        mLayoutManager = new LinearLayoutManager(context);
        rv_file_list.setLayoutManager(mLayoutManager);
        rv_file_list.setItemAnimator(new DefaultItemAnimator());

        rv_file_list.setItemAnimator(new DefaultItemAnimator());
        rv_file_grid_type_list.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, includeEdge));

        rv_file_grid_type_list.setLayoutManager(new GridLayoutManager(context, numberOfColumns));

        iv_grid = (ImageView) rootView.findViewById(R.id.iv_grid);
        iv_list = (ImageView) rootView.findViewById(R.id.iv_list);

        iv_grid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                rv_file_grid_type_list.setVisibility(View.VISIBLE);
                rv_file_list.setVisibility(View.GONE);

                rv_file_grid_type_list.setLayoutManager(new GridLayoutManager(context, numberOfColumns));

                iv_list.setImageDrawable(context.getResources().getDrawable(R.drawable.list_icon_gray));
                iv_grid.setImageDrawable(context.getResources().getDrawable(R.drawable.grid_icon_green));
                getFolderListData();
            }
        });

        iv_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                rv_file_grid_type_list.setVisibility(View.GONE);
                rv_file_list.setVisibility(View.VISIBLE);

                mLayoutManager = new LinearLayoutManager(context);
                rv_file_list.setLayoutManager(mLayoutManager);
                rv_file_list.setItemAnimator(new DefaultItemAnimator());

                iv_list.setImageDrawable(context.getResources().getDrawable(R.drawable.list_icon_green));
                iv_grid.setImageDrawable(context.getResources().getDrawable(R.drawable.grid_icon_gray));

                getFolderListData();
            }
        });

        iv_select_uncheck = (ImageView) rootView.findViewById(R.id.iv_select_uncheck);
        iv_selet_check_green = (ImageView) rootView.findViewById(R.id.iv_selet_check_green);

        iv_select_uncheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iv_select_uncheck.setVisibility(View.GONE);
                iv_selet_check_green.setVisibility(View.VISIBLE);

                tinyDB.putBoolean(Constants.Preferences.PREF_CHECK_UNCHECK, true);

                getFolderListData();
            }
        });

        iv_selet_check_green.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iv_select_uncheck.setVisibility(View.VISIBLE);
                iv_selet_check_green.setVisibility(View.GONE);

                tinyDB.putBoolean(Constants.Preferences.PREF_CHECK_UNCHECK, false);
                getFolderListData();
            }
        });

        ItemClickSupport.addTo(rv_file_grid_type_list).setOnItemLongClickListener(new ItemClickSupport.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClicked(RecyclerView recyclerView, int position, View v) {
                try {
                    Log.i(TAG, "onItemLongClicked(), position : " + position);

                    folderLongPressDialog();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true; // true means the event consumed false means it forwards the event.
            }
        });

        ItemClickSupport.addTo(rv_file_list)
                .setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View view) {
                        try {
                            HomeFragment.view = view;
                            HomeFragment.position = position;

                            Log.i(TAG, "onItemClicked(), HomeFragment.position : " + HomeFragment.position);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

        ItemClickSupport.addTo(rv_file_grid_type_list)
                .setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View view) {
                        try {
                            HomeFragment.view = view;
                            HomeFragment.position = position;

                            //String folderClick = folderDetailArrayList.get(position).getFolderName();

                            Log.i(TAG, "onItemClicked(), HomeFragment.position : " + HomeFragment.position);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

        return rootView;
    }

    @SuppressLint("LongLogTag")
    private void sortingOrderDialog() {
        Log.i(TAG, "sortingOrderDialog() called: ");
        try {
            sortingOrderDialog = new Dialog(context);

            sortingOrderDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

            sortingOrderDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            sortingOrderDialog.setContentView(R.layout.sorting_order_dialog_layout);

            TextView tv_name, tv_cancel;

            tv_name = (TextView) sortingOrderDialog.findViewById(R.id.tv_name);
            tv_cancel = (TextView) sortingOrderDialog.findViewById(R.id.tv_cancel);

            tv_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
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
                    // createNewFolder(newFolderName);

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

    @SuppressLint("LongLogTag")
    private void folderLongPressDialog() {
        Log.i(TAG, "folderLongPressDialog() called: ");
        try {
            folderLongPressDialog = new Dialog(context);

            folderLongPressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

            folderLongPressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            folderLongPressDialog.setContentView(R.layout.folder_long_press_dialog_layout);

            TextView tv_cancel, tv_copy;

            tv_cancel = (TextView) folderLongPressDialog.findViewById(R.id.tv_cancel);
            tv_copy = (TextView) folderLongPressDialog.findViewById(R.id.tv_copy);

            tv_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    folderLongPressDialog.dismiss();
                }
            });

            folderLongPressDialog.setCancelable(false);

            folderLongPressDialog.dismiss();

            folderLongPressDialog.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getFolderListData() {
        Log.i(TAG, "getFolderListData() called ");

        try {
            if (!Helper.isNetworkAvailable(getActivity())) {
                //AlertDialogManager.showAlertDialog(this, getString(R.string.txt_alert_C), getString(R.string.no_internet));
                return;
            } else {
                RequestParams params = new RequestParams();

                String token = tinyDB.getString(Constants.Preferences.PREF_TOKEN);
                String appName = tinyDB.getString(Constants.Preferences.PREF_APP_NAME);
                String userName = tinyDB.getString(Constants.Preferences.PREF_DECREPTED_USER_NAME);

                String FolderNo = AndyUtils.encrypt("ALL");
                String folderNoAfterRemveExtra = Helper.removeExtra(FolderNo);

                String param = "?Token=" + token + "&AppsName=" + appName + "&UserName=" + userName + "&FolderNo=" + folderNoAfterRemveExtra;
                Log.d("Lodin ", "param : " + param);

                new ApiCall(getActivity()).firePost(true, ApiUrl.FOLDER_FILE_LIST + param, params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        String res = new String(responseBody);
                        Log.d("Lodin ", "responseBody : " + res);
                        try {
                            JSONObject jsonObject = new JSONObject(res);
                            if (jsonObject.getString("Status").equals("1")) {
                                FolderListData folderListData = new Gson().fromJson(res, FolderListData.class);

                                String s = folderListData.getData();
                                s.replace("[", "");
                                s.replace("]", "");
                                String data = AndyUtils.decrypt(s);
                                data = "[" + data + "]";

                                folderDetailArrayList = new Gson().fromJson(data, new TypeToken<ArrayList<FolderListItemData>>() {
                                }.getType());

                                if (folderDetailArrayList != null && folderDetailArrayList.size() > 0) {

                                    //HomeFragment.this.folderDetailArrayList.addAll(folderDetailArrayList);

                                    relNoDataFound.setVisibility(relNoDataFound.GONE);

                                    //if (homeFragmentGridAdapter == null) {
                                    homeFragmentGridAdapter = new HomeFragmentGridAdapter(context, inflater, folderDetailArrayList);
                                    rv_file_grid_type_list.setAdapter(homeFragmentGridAdapter);
                                    //} else {
                                    //homeFragmentGridAdapter.setGridList(HomeFragment.this.folderDetailArrayList);
                                    //}

                                    //  if (homeFragmentListAdapter == null) {
                                    homeFragmentListAdapter = new HomeFragmentListAdapter(context, inflater, folderDetailArrayList);
                                    rv_file_list.setAdapter(homeFragmentListAdapter);
                                    //} else {
                                    //homeFragmentListAdapter.setList(HomeFragment.this.folderDetailArrayList);
                                    //}
                                } else {
                                    folderDetailArrayList = new ArrayList<FolderListItemData>();
                                    if (homeFragmentListAdapter != null) {
                                        homeFragmentListAdapter.setList(folderDetailArrayList);
                                    }

                                    if (homeFragmentGridAdapter != null) {
                                        homeFragmentGridAdapter.setGridList(folderDetailArrayList);
                                    }
                                    relNoDataFound.setVisibility(relNoDataFound.VISIBLE);
                                    Log.i(TAG, "getFolderListData(), folderDetailArrayList is null or empty");
                                }

                            } else {
                                Toast.makeText(getActivity(), "" + jsonObject.getString("Msg"), Toast.LENGTH_LONG).show();
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
            if (!Helper.isNetworkAvailable(getActivity())) {
                //AlertDialogManager.showAlertDialog(this, getString(R.string.txt_alert_C), getString(R.string.no_internet));
                return;
            } else {
                RequestParams params = new RequestParams();

                String token = tinyDB.getString(Constants.Preferences.PREF_TOKEN);
                String appName = tinyDB.getString(Constants.Preferences.PREF_APP_NAME);
                String userName = tinyDB.getString(Constants.Preferences.PREF_DECREPTED_USER_NAME);

                String FolderNo = AndyUtils.encrypt("ALL");//pas parant folder no
                String folderNoAfterRemveExtra = Helper.removeExtra(FolderNo);

                String newFolderNameAfterDecrypt = AndyUtils.encrypt(newFolderName);
                String newFolderNameAfterRemoveExtra = Helper.removeExtra(newFolderNameAfterDecrypt);

                String param = "?Token=" + token + "&AppsName=" + appName + "&UserName=" + userName + "&FolderNo=" + folderNoAfterRemveExtra + "&newFolderNameAfterRemoveExtra" + newFolderNameAfterRemoveExtra;
                Log.d("Lodin ", "param : " + param);

                new ApiCall(getActivity()).firePost(true, ApiUrl.CREATE_FOLDER + param, params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        String res = new String(responseBody);
                        Log.d("Lodin ", "responseBody : " + res);
                        try {
                            JSONObject jsonObject = new JSONObject(res);
                            if (jsonObject.getString("Status").equals("1")) {
                                NewFolderData folderDetail = new Gson().fromJson(res, NewFolderData.class);

                                String s = folderDetail.getData();
                                s.replace("[", "");
                                s.replace("]", "");
                                String data = AndyUtils.decrypt(s);
                                data = "[" + data + "]";

                                ArrayList<NewFolderItemData> folderItemDataArrayList = new Gson().fromJson(data, new TypeToken<ArrayList<NewFolderItemData>>() {
                                }.getType());

                                if (folderItemDataArrayList != null && folderItemDataArrayList.size() > 0) {

                                    relNoDataFound.setVisibility(relNoDataFound.GONE);

                                    homeFragmentListAdapter = new HomeFragmentListAdapter(context, inflater, folderDetailArrayList);
                                    rv_file_list.setAdapter(homeFragmentListAdapter);

                                } else {
                                    folderDetailArrayList = new ArrayList<FolderListItemData>();
                                    if (homeFragmentListAdapter != null) {
                                        homeFragmentListAdapter.setList(folderDetailArrayList);
                                    }

                                    relNoDataFound.setVisibility(relNoDataFound.VISIBLE);
                                    Log.i(TAG, "getFolderListData(), folderDetailArrayList is null or empty");
                                }

                            } else {
                                Toast.makeText(getActivity(), "" + jsonObject.getString("Msg"), Toast.LENGTH_LONG).show();
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
}