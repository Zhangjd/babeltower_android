package com.babieta.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.babieta.R;
import com.babieta.base.ApiUrl;
import com.babieta.base.Netroid;
import com.babieta.base.Util;
import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.duowan.mobile.netroid.request.JsonObjectRequest;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

//专题activity
public class SpecialActivity extends SwipeBackActivity {

	private String subContentsURL = "";

	private ImageView special_header_image;
	private TextView special_title;
	private TextView special_description;
	private ProgressDialog mProgressDialog;

	private String id = "";
	private String itemURL = "";
	private String headerImageURL = "";
	private String title = "";
	private String description = "";

	private ListView listView = null;
	private List<Map<String, String>> data = new ArrayList<Map<String, String>>();

	private final String REQUESTS_TAG = "special_request";
	private String result = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_special);

		TextView titleTextView = (TextView) findViewById(R.id.header_textview);
		titleTextView.setText("专题视图");

		this.initEventsRegister();

		// get data from bundle
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		id = bundle.getString("id");
		itemURL = bundle.getString("itemURL");
		title = bundle.getString("title");
		description = bundle.getString("description");
		headerImageURL = bundle.getString("headerImageURL");
		subContentsURL = ApiUrl.BABIETA_BASE_URL + "/v1/contents/" + id + "/subcontents";

		special_title = (TextView) findViewById(R.id.special_title);
		special_description = (TextView) findViewById(R.id.special_description);
		special_header_image = (ImageView) findViewById(R.id.special_header_image);
		special_title.setText(title);
		special_description.setText(description);

		// 子内容ListView,点击跳转到WebView
		listView = (ListView) findViewById(R.id.special_subcontents_listview);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// TODO Auto-generated method stub
				String idStr = data.get(arg2).get("id");
				System.out.println(id);

				Intent intent = new Intent(SpecialActivity.this, WebViewActivity.class);
				Bundle urlBundle = new Bundle();
				urlBundle.putCharSequence("itemURL", ApiUrl.BABIETA_BASE_URL
						+ ApiUrl.BABIETA_ARTICLE + idStr);
				urlBundle.putCharSequence("title", data.get(arg2).get("title"));
				urlBundle.putCharSequence("ImageURL", data.get(arg2).get("image"));
				urlBundle.putCharSequence("author", data.get(arg2).get("author"));
				urlBundle.putCharSequence("updated_at", data.get(arg2).get("updated_at"));
				intent.putExtras(urlBundle);
				startActivity(intent);
			}
		});

		loadSubContents();
		loadHeaderImage();
	}

	private void loadSubContents() {
		JSONObject jsonRequest = null;
		JsonObjectRequest request = new JsonObjectRequest(subContentsURL, jsonRequest,
				new Listener<JSONObject>() {
					@Override
					public void onPreExecute() {
						mProgressDialog = new ProgressDialog(SpecialActivity.this,
								AlertDialog.THEME_HOLO_LIGHT);
						mProgressDialog.setMessage("Loading...");
						mProgressDialog.setIndeterminate(false);
						mProgressDialog.setCancelable(true);
						mProgressDialog.setCanceledOnTouchOutside(false);
						mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

							@Override
							public void onCancel(DialogInterface dialog) {
								// TODO Auto-generated method stub
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

					@Override
					public void onSuccess(JSONObject response) {
						// System.out.println(response.toString());
						try {
							if (response.has("status") && response.getInt("status") == 0) {
								JSONArray jsonArray = (JSONArray) response.get("list");
								for (int i = 0; i < jsonArray.length(); i++) {
									Map<String, String> map = new HashMap<String, String>();
									JSONObject jsonObject = (JSONObject) jsonArray.get(i);
									String subTitle = jsonObject.getString("title");
									String subImage = jsonObject.getString("thumb_image_url");
									String subAuthor = jsonObject.getJSONObject("author")
											.getString("display_name");
									String subUpdated_at = jsonObject.getString("updated_at");
									String subId = String.valueOf(jsonObject.getInt("id"));
									// System.out.println(subTitle);
									map.put("title", subTitle);
									map.put("image", subImage);
									map.put("author", subAuthor);
									map.put("updated_at", subUpdated_at);
									map.put("id", subId);
									data.add(map);
								}
								changeListView();
							} else {
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							Log.w("JSONException", "解析子内容出错了!");
							e.printStackTrace();
						}
					}

					@Override
					public void onError(NetroidError error) {
						String data = error.getMessage();
						result = "";
					}
				});
		// 设置请求标识，这个标识可用于终止该请求时传入的Key
		request.setTag(REQUESTS_TAG);
		Netroid.addRequest(request);
	}

	private void loadHeaderImage() {
		if (headerImageURL != "") {
			ImageLoader.getInstance().displayImage(headerImageURL, special_header_image,
					Util.getImageOption(getApplicationContext()));
		}
	}

	// 子标题listview
	private void changeListView() {

		listView.setAdapter(new mySimpleAdater(this, data, android.R.layout.simple_list_item_1,
				new String[] { "title" }, new int[] { android.R.id.text1 }));
		setListViewHeightBasedOnChildren(listView);
	}

	private void initEventsRegister() {
		final ImageButton backButton = (ImageButton) findViewById(R.id.back_button);

		backButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
				overridePendingTransition(0, R.anim.base_slide_right_out);
			}
		});
	}

	// 动态设置ListView的高度
	// 固定ListView的高度，让其不自动调整调整，就不会与ScrollView冲突了
	private void setListViewHeightBasedOnChildren(ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return;
		}
		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0); // 在还没有构建View 之前无法取得View的度宽。在此之前我们必须选
									// measure 一下.
			totalHeight += listItem.getMeasuredHeight();
		}
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		// params.height += 5;// if without this statement,the Listview will be
		// a little short
		// listView.getDividerHeight()获取子项间分隔符占用的高度
		// params.height最后得到整个ListView完整显示需要的高度
		listView.setLayoutParams(params);
	}

	//重写mySimpleAdater的getView
	private class mySimpleAdater extends SimpleAdapter {

		public mySimpleAdater(Context context, List<? extends Map<String, ?>> data, int resource,
				String[] from, int[] to) {
			super(context, data, resource, from, to);
			// TODO Auto-generated constructor stub
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			View v = super.getView(position, convertView, parent);
			TextView textView = (TextView)v.findViewById(android.R.id.text1);
			textView.setTextColor(Color.rgb(48, 48, 48));
			return v;
		}

	}

}
