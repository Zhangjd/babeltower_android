package com.bbt.babeltower.fragment;

import org.json.JSONException;
import org.json.JSONObject;

import com.bbt.babeltower.R;
import com.bbt.babeltower.activity.AboutActivity;
import com.bbt.babeltower.activity.FeedbackActivity;
import com.bbt.babeltower.activity.MainActivity;
import com.bbt.babeltower.adapter.SettingAdapter;
import com.bbt.babeltower.base.ApiUrl;
import com.bbt.babeltower.base.Netroid;
import com.bbt.babeltower.base.S;
import com.bbt.babeltower.base.Util;
import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.duowan.mobile.netroid.request.JsonObjectRequest;
import com.gc.materialdesign.widgets.SnackBar;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class SettingFragment extends Fragment {

	private ListView listView = null;
	private SettingAdapter settingAdapter = null;
	private ProgressDialog mProgressDialog = null;
	private AlertDialog alertDialog = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_setting, container, false);

		listView = (ListView) view.findViewById(R.id.setting_list);
		settingAdapter = new SettingAdapter(getActivity());
		listView.setAdapter(settingAdapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				switch (position) {
				case 0: // clear cache
					ContextThemeWrapper themedContext;
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
						themedContext = new ContextThemeWrapper(getActivity(),
								android.R.style.Theme_Holo_Light_Dialog_NoActionBar);
					} else {
						themedContext = new ContextThemeWrapper(getActivity(),
								android.R.style.Theme_Light_NoTitleBar);
					}
					AlertDialog.Builder builder = new AlertDialog.Builder(themedContext);
					alertDialog = builder.create();
					alertDialog.setMessage("确定要清除缓存吗?");
					alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "确定",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface arg0, int arg1) {
									S.clear(getActivity()); // 清理SharedPreferences
									MainActivity.mainFragment.initOnCreate(); // 清空首页ListView
									Util.showToast(getActivity(), "清理成功");
								}
							});
					alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "取消",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface arg0, int arg1) {

								}
							});

					alertDialog.show();
					break;
				case 1: // check for updates
					mProgressDialog = new ProgressDialog(getActivity(),
							AlertDialog.THEME_HOLO_LIGHT);
					mProgressDialog.setMessage("检查更新中...");
					mProgressDialog.setIndeterminate(false);
					mProgressDialog.setCancelable(true);
					mProgressDialog.setCanceledOnTouchOutside(false);
					mProgressDialog.setIndeterminateDrawable(getResources().getDrawable(
							R.drawable.myprogressbar));
					mProgressDialog.show();

					String targetURL = ApiUrl.BABIETA_VERSION_CHECK;
					JSONObject jsonRequest = null;
					JsonObjectRequest request = new JsonObjectRequest(targetURL, jsonRequest,
							new Listener<JSONObject>() {
								@Override
								public void onSuccess(JSONObject response) {
									String ver = "";
									try {
										int currVer = getActivity().getPackageManager()
												.getPackageInfo(getActivity().getPackageName(), 0).versionCode;
										ver = response.getString("build");
										Log.d("check for updates", "current " + currVer
												+ " newest " + ver);
										if (Integer.valueOf(ver) > currVer) {
											mProgressDialog.hide();

											SnackBar snackbar = new SnackBar(getActivity(),
													"检测到新版本\n版本号:" + response.getString("version")
															+ "\n更新说明: "
															+ response.getString("description"),
													"下载", new View.OnClickListener() {

														@Override
														public void onClick(View v) {
															Intent intent = new Intent();
															intent.setAction("android.intent.action.VIEW");
															Uri content_url = Uri
																	.parse(ApiUrl.BABIETA_DOWNLOAD);
															intent.setData(content_url);
															startActivity(intent);
														}
													});
											snackbar.setDismissTimer(5000);
											snackbar.show();
										} else {
											mProgressDialog.hide();
											Util.showToast(getActivity(), "当前已经是最新版本");
										}

									} catch (JSONException e) {
										System.out.println("error in parsing JSON");
										e.printStackTrace();
									} catch (NameNotFoundException e) {
										e.printStackTrace();
									}
								}

								@Override
								public void onError(NetroidError error) {
									Util.showToast(getActivity(), "网络错误,请稍后重试");
									mProgressDialog.hide();
								}
							});
					// 设置请求标识，这个标识可用于终止该请求时传入的Key
					request.setTag("json-request-checkNewVersion");
					Netroid.addRequest(request);

					break;
				case 2: // feedback
					Intent feedbackIntent = new Intent(getActivity(), FeedbackActivity.class);
					startActivity(feedbackIntent);
					getActivity().overridePendingTransition(R.anim.base_slide_right_in,
							R.anim.base_slide_remain);
					break;
				case 3: // about
					Intent aboutIntent = new Intent(getActivity(), AboutActivity.class);
					startActivity(aboutIntent);
					getActivity().overridePendingTransition(R.anim.base_slide_right_in,
							R.anim.base_slide_remain);
					break;
				default:
					break;
				}
			}
		});

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		TextView titleTextView = (TextView) getActivity().findViewById(R.id.header_textview);
		titleTextView.setText("设置");
	}

}
