package com.babieta.activity;

import java.util.LinkedList;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.avos.avoscloud.LogUtil.log;
import com.babieta.R;
import com.babieta.adapter.ListPostAdapter;
import com.babieta.base.Netroid;
import com.babieta.base.Util;
import com.babieta.bean.PostBean;
import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.duowan.mobile.netroid.request.JsonObjectRequest;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

// 点开"分类"的Activity
public class SectionContentActivity extends SwipeBackActivity {
	private ProgressDialog mProgressDialog;
	private final String REQUESTS_TAG = "section_request";

	private String itemURL;
	// private String category;
	private String module;

	private PullToRefreshListView listView;
	private ListPostAdapter listPostAdapter;
	private ImageButton backButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_section_content);

		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		itemURL = bundle.getString("itemURL");
		// category = bundle.getString("category");
		module = bundle.getString("module");

		TextView title = (TextView) findViewById(R.id.header_textview);
		title.setText("分类列表 :" + module);

		initPostListView();
		initSectionContent();

		backButton = (ImageButton) findViewById(R.id.back_button);
		backButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
				overridePendingTransition(0, R.anim.base_slide_right_out);

			}
		});
	}

	private void initSectionContent() {
		JSONObject jsonRequest = null;
		JsonObjectRequest request = new JsonObjectRequest(itemURL, jsonRequest,
				new Listener<JSONObject>() {
					@Override
					public void onSuccess(JSONObject response) {
						try {
							if (response.has("status") && response.getInt("status") == 0) {
								String jsonString = response.toString();
								LinkedList<PostBean> postBeans = PostBean.parseSection(jsonString,
										SectionContentActivity.this); // 在这里解析
								if (postBeans.size() == 0)
									Util.showToast(SectionContentActivity.this, "没有新的内容");
								else
									Util.showToast(SectionContentActivity.this,
											"加载了" + postBeans.size() + "条内容");
								listPostAdapter.appendPost(postBeans);
								listPostAdapter.sortPost();
								listPostAdapter.notifyDataSetChanged();
							} else {
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onError(NetroidError error) {
						String data = error.getMessage();
						log.d("onError", data);
					}

					@Override
					public void onPreExecute() {
						mProgressDialog = new ProgressDialog(SectionContentActivity.this,
								AlertDialog.THEME_HOLO_LIGHT);
						mProgressDialog.setMessage("Loading...");
						mProgressDialog.setIndeterminate(false);
						mProgressDialog.setCancelable(true);
						mProgressDialog.setCanceledOnTouchOutside(false);
						mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

							@Override
							public void onCancel(DialogInterface dialog) {
								Netroid.getRequestQueue().cancelAll(REQUESTS_TAG);
							}
						});
						mProgressDialog.show();
					}

					// 在onFinish()时cancel dialog
					@Override
					public void onFinish() {
						mProgressDialog.cancel();
					}

				});
		// 设置请求标识，这个标识可用于终止该请求时传入的Key
		request.setTag(REQUESTS_TAG);
		Netroid.addRequest(request);
	}

	public void initPostListView() {
		listView = (PullToRefreshListView) findViewById(R.id.section_content_list_view);
		listView.setMode(Mode.PULL_FROM_END);
		listPostAdapter = new ListPostAdapter(this);
		listView.setAdapter(listPostAdapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			// 点击item跳转到WebView中
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (position >= 1 && listPostAdapter.getCount() >= (position - 1)) {
					PostBean postBean = listPostAdapter.postBeans.get(position - 1);
					Util.handleItemClick(SectionContentActivity.this, postBean);
				}
			}
		});

		listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				int size = listPostAdapter.getCount();
				if (size == 0)
					return;
				else {
					PostBean bean = (PostBean) listPostAdapter.getItem(size - 1);
					int tailID = bean.getId();
					String targetURL = itemURL + "?max_id=" + (tailID - 1);
					JSONObject jsonRequest = null;
					JsonObjectRequest request = new JsonObjectRequest(targetURL, jsonRequest,
							new Listener<JSONObject>() {
								@Override
								public void onSuccess(JSONObject response) {
									try {
										if (response.has("status")
												&& response.getInt("status") == 0) {
											String jsonString = response.toString();
											LinkedList<PostBean> postBeans = PostBean.parseSection(
													jsonString, SectionContentActivity.this); // 在这里解析
											if (postBeans.size() == 0)
												Util.showToast(SectionContentActivity.this,
														"没有新的内容");
											else
												Util.showToast(SectionContentActivity.this, "加载了"
														+ postBeans.size() + "条内容");
											listPostAdapter.appendPost(postBeans);
											listPostAdapter.sortPost();
											listPostAdapter.notifyDataSetChanged();
											listView.onRefreshComplete();
										} else {
										}
									} catch (JSONException e) {
										e.printStackTrace();
									}
								}

								@Override
								public void onError(NetroidError error) {
									String data = error.getMessage();
									log.d("onError", data);
								}
							});
					// 设置请求标识，这个标识可用于终止该请求时传入的Key
					request.setTag(REQUESTS_TAG);
					Netroid.addRequest(request);
				}
			}
		});
	}
}
