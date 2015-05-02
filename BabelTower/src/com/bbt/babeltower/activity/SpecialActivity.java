package com.bbt.babeltower.activity;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.avos.avoscloud.LogUtil.log;
import com.bbt.babeltower.R;
import com.bbt.babeltower.adapter.ListPostAdapter;
import com.bbt.babeltower.base.ApiUrl;
import com.bbt.babeltower.base.Netroid;
import com.bbt.babeltower.base.S;
import com.bbt.babeltower.base.Util;
import com.bbt.babeltower.bean.PostBean;
import com.duowan.mobile.netroid.AuthFailureError;
import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.duowan.mobile.netroid.request.JsonObjectRequest;
import com.duowan.mobile.netroid.request.StringRequest;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

//ר��activity
public class SpecialActivity extends SwipeBackActivity {

	private String subContentsURL = "";

	private ImageView special_header_image;
	private TextView special_title;
	private TextView special_description;
	private ProgressDialog mProgressDialog;
	private ImageButton likeButton;
	private ImageButton collectButton;
	private RelativeLayout area_switch;

	private String id = "";
	private String itemURL = "";
	private String imgURL = "";
	private String title = "";
	private String content_type = "";
	private String author = "";
	private String created_at = "";
	private String updated_at = "";
	private String description = "";

	private int collectFlag = 0;
	private boolean likeFlag = false;

	private ListView listView = null;
	private ListPostAdapter listPostAdapter;
	private LinkedList<PostBean> postBeans;

	private final String REQUESTS_TAG = "special_request";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_special);
		Util.setStatusBarColor(SpecialActivity.this);

		TextView titleTextView = (TextView) findViewById(R.id.header_textview);
		titleTextView.setText("����");

		// get data from bundle
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		id = String.valueOf(bundle.getInt("id", 0));
		content_type = bundle.getString("content_type", " ");
		itemURL = bundle.getString("itemURL", " ");
		imgURL = bundle.getString("ImageURL", " ");
		author = bundle.getString("author", " ");
		created_at = bundle.getString("created_at", " ");
		updated_at = bundle.getString("updated_at", " ");
		title = bundle.getString("title", " ");
		description = bundle.getString("description", " ");

		this.initEventsRegister();
		this.initEventsRegister2();

		// ����Id
		if (id.equals("0")) {
			id = itemURL.replace(ApiUrl.BABIETA_BASE_URL + ApiUrl.BABIETA_ARTICLE, "");
		}

		special_title = (TextView) findViewById(R.id.special_title);
		special_description = (TextView) findViewById(R.id.special_description);
		special_header_image = (ImageView) findViewById(R.id.special_header_image);
		special_title.setText(title);
		special_description.setText(description);

		// ������ListView,�����ת��WebView
		listView = (ListView) findViewById(R.id.special_subcontents_listview);
		listView.setDivider(null);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				PostBean postBean = listPostAdapter.postBeans.get(position);
				Util.handleItemClick(SpecialActivity.this, postBean);
			}
		});

		loadSubContents();
		loadHeaderImage();
	}

	private void loadSubContents() {
		subContentsURL = ApiUrl.BABIETA_BASE_URL + "/v1/contents/" + id + "/subcontents";
		JSONObject jsonRequest = null;
		JsonObjectRequest request = new JsonObjectRequest(subContentsURL, jsonRequest,
				new Listener<JSONObject>() {
					@Override
					public void onPreExecute() {
						mProgressDialog = new ProgressDialog(SpecialActivity.this,
								AlertDialog.THEME_HOLO_LIGHT);
						mProgressDialog.setMessage(getResources().getString(R.string.waiting_tips));
						mProgressDialog.setIndeterminate(false);
						mProgressDialog.setCancelable(true);
						mProgressDialog.setCanceledOnTouchOutside(false);
						mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

							@Override
							public void onCancel(DialogInterface dialog) {
								Netroid.getRequestQueue().cancelAll(REQUESTS_TAG);

							}
						});
						// mProgressDialog.setIndeterminateDrawable(getResources().getDrawable(
						// R.drawable.myprogressbar));
						mProgressDialog.show();
					}

					// ��onFinish()ʱcancel dialog
					@Override
					public void onFinish() {
						mProgressDialog.cancel();
					}

					@Override
					public void onSuccess(JSONObject response) {
						try {
							if (response.has("status") && response.getInt("status") == 0) {
								String result = response.toString();
								postBeans = PostBean.parseSection(result, SpecialActivity.this);
								changeListView(postBeans);
							} else {
							}
						} catch (JSONException e) {
							log.e("JSONException", "���������ݳ�����!");
							e.printStackTrace();
						}
					}

					@Override
					public void onError(NetroidError error) {
						super.onError(error);
						Util.showToast(SpecialActivity.this, "���粻����~��������Ŷ");
					}
				});
		// ���������ʶ�������ʶ��������ֹ������ʱ�����Key
		request.setTag(REQUESTS_TAG);
		Netroid.addRequest(request);
	}

	private void loadHeaderImage() {
		if (imgURL != "") {
			ImageLoader.getInstance().displayImage(imgURL, special_header_image,
					Util.getImageOption(getApplicationContext()));
		}
	}

	// ����SubContent��ListView
	private void changeListView(LinkedList<PostBean> postBeans) {
		listPostAdapter = new ListPostAdapter(SpecialActivity.this);
		listPostAdapter.postBeans = postBeans;

		listView.setAdapter(listPostAdapter);
		listPostAdapter.notifyDataSetChanged();
		setListViewHeightBasedOnChildren(listView);
	}

	private void initEventsRegister2() {
		final ImageButton backButton = (ImageButton) findViewById(R.id.back_button);

		backButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
				overridePendingTransition(0, R.anim.base_slide_right_out);
			}
		});
	}

	// ��̬����ListView�ĸ߶�
	// �̶�ListView�ĸ߶ȣ����䲻�Զ������������Ͳ�����ScrollView��ͻ��
	private void setListViewHeightBasedOnChildren(ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return;
		}
		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0); // �ڻ�û�й���View ֮ǰ�޷�ȡ��View�Ķȿ��ڴ�֮ǰ���Ǳ���ѡ
									// measure һ��.
			totalHeight += listItem.getMeasuredHeight();
		}
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		// params.height += 5;// if without this statement,the Listview will be
		// a little short
		// listView.getDividerHeight()��ȡ�����ָ���ռ�õĸ߶�
		// params.height���õ�����ListView������ʾ��Ҫ�ĸ߶�
		listView.setLayoutParams(params);
	}

	private void initEventsRegister() {
		this.likeButton = (ImageButton) findViewById(R.id.webview_like);
		this.collectButton = (ImageButton) findViewById(R.id.webview_collect);
		this.area_switch = (RelativeLayout) findViewById(R.id.header_switch_area);

		// find collections
		String[] strings = S.getStringSet(getApplicationContext(), "collected_list");
		for (int i = 0; i < strings.length; i++) {
			if (itemURL.equals(strings[i])) {
				collectFlag = 1;
				collectButton.setImageResource(R.drawable.babeltower_collect_on);
				break;
			}
		}

		// find liked
		String[] likeSet = S.getStringSet(getApplicationContext(), "liked_list");
		for (int i = 1; i < likeSet.length; i++) {
			if (itemURL.equals(likeSet[i])) {
				likeFlag = true;
				likeButton.setImageResource(R.drawable.babeltower_like_on);
				break;
			}
		}

		likeButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				handleLike();
			}
		});

		collectButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				handleCollect();
			}
		});

		area_switch.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
				overridePendingTransition(0, R.anim.base_slide_right_out);
			}
		});
	}

	// �������
	private void handleLike() {
		if (likeFlag) { // �Ѿ�����->ȡ��
			String url = "http://218.192.166.167:3030/v1/contents/" + id + "/unlike";
			Map<String, String> mParams = new HashMap<String, String>();
			Netroid.getRequestQueue().add(new PutRequest(url, mParams, new Listener<String>() {
				@Override
				public void onSuccess(String arg0) {
					log.d(arg0);
				}
			}));

			// ȥ�������б������Ӧ��id
			String[] strings = S.getStringSet(getApplicationContext(), "liked_list");
			String regularEx = S.regularEx;
			String tmp_all = "";
			Boolean status = false;
			for (int i = 1; i < strings.length; i++) {
				if (itemURL.equals(strings[i])) {
					if (strings[i].equals(itemURL)) {
						status = true;
						i = i + 8; // ����i++,����9��
						continue;
					}
					tmp_all = tmp_all + regularEx + strings[i];
				}
			}
			S.put(getApplicationContext(), "liked_list", tmp_all);

			if (status) {
				likeFlag = false;
				likeButton.setImageResource(R.drawable.babeltower_like_off);
				// Util.showToast(SpecialActivity.this, "��ȡ������");
			} else {
				log.w("ȡ������ʧ��");
			}
		} else { // δ����->����
			String url = "http://218.192.166.167:3030/v1/contents/" + id + "/like";
			Map<String, String> mParams = new HashMap<String, String>();
			Netroid.getRequestQueue().add(new PutRequest(url, mParams, new Listener<String>() {
				@Override
				public void onSuccess(String arg0) {
					log.d(arg0);
				}
			}));

			// ���� +1
			likeFlag = true;
			likeButton.setImageResource(R.drawable.babeltower_like_on);
			S.addStringSet(getApplicationContext(), "liked_list", itemURL); // ��¼
			Util.showToast(SpecialActivity.this, "ϲ���ɹ�");
		}
	}

	public class PutRequest extends StringRequest {
		private Map<String, String> mParams;

		// ����Post������Map����
		public PutRequest(String url, Map<String, String> params, Listener<String> listener) {
			super(Method.PUT, url, listener);
			mParams = params;
		}

		@Override
		public Map<String, String> getParams() throws AuthFailureError {
			return mParams;
		}
	}

	// �����ղ�
	private void handleCollect() {
		if (collectFlag == 1) { // ȡ���ղ�
			String[] collectSet = S.getStringSet(getApplicationContext(), "collected_list");
			String regularEx = S.regularEx;
			Boolean status = false;
			String tmp_all = "";
			for (int i = 1; i < collectSet.length; i++) {
				if ((i + 1) < collectSet.length && collectSet[i + 1].equals(itemURL)) {
					status = true;
					i = i + 8; // ����i++,����9��
					continue;
				}
				tmp_all = tmp_all + regularEx + collectSet[i];
			}
			S.put(getApplicationContext(), "collected_list", tmp_all);

			if (status) {
				collectFlag = 0;
				collectButton.setImageResource(R.drawable.babeltower_collect_off);
				// Util.showToast(SpecialActivity.this, "��ȡ���ղ�");
			} else {
				log.w("ȡ���ղ�ʧ��");
			}
		} else { // �ղ�
			Boolean status = S.addStringSet(getApplicationContext(), "collected_list",
					String.valueOf(id));

			if (status) {
				collectFlag = 1;
				collectButton.setImageResource(R.drawable.babeltower_collect_on);

				// ������
				S.addStringSet(getApplicationContext(), "collected_list", itemURL);
				S.addStringSet(getApplicationContext(), "collected_list", content_type);
				S.addStringSet(getApplicationContext(), "collected_list", imgURL);
				S.addStringSet(getApplicationContext(), "collected_list", title);
				S.addStringSet(getApplicationContext(), "collected_list", description);
				S.addStringSet(getApplicationContext(), "collected_list", author);
				S.addStringSet(getApplicationContext(), "collected_list", created_at);
				S.addStringSet(getApplicationContext(), "collected_list", updated_at);

				Util.showToast(SpecialActivity.this, "�ղسɹ�");
			} else {
				log.w("�ղ�ʧ��");
			}
		}
	}

}
