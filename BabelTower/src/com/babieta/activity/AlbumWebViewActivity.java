package com.babieta.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.avos.avoscloud.LogUtil.log;
import com.babieta.R;
import com.babieta.base.Netroid;
import com.babieta.base.S;
import com.babieta.base.Util;
import com.duowan.mobile.netroid.request.JsonObjectRequest;
import com.duowan.mobile.netroid.request.StringRequest;
import com.duowan.mobile.netroid.AuthFailureError;
import com.duowan.mobile.netroid.Listener;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.TextView;

public class AlbumWebViewActivity extends SwipeBackActivity {

	private WebView contentWebView = null;

	private ProgressDialog mProgressDialog;

	private int id = 0;
	private String itemURL = "";
	private String imgURL = "";
	private String title = "";
	private String content_type = "";
	private String author = "";
	private String created_at = "";
	private String updated_at = "";
	private String description = "";

	private int albumId = 0;
	private int like = 0;
	private int views = 0;

	private ImageButton likeButton;
	private TextView likeTextView;
	private ImageButton collectButton;
	private TextView colletTextView;
	private ImageButton backButton;
	private int collectFlag = 0;
	private boolean likeFlag = false;

	private String template_html = "";
	private String photos = "";

	private final String REQUESTS_TAG = "album_request";

	@SuppressLint({ "JavascriptInterface", "SetJavaScriptEnabled" })
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_web_view);
		contentWebView = (WebView) findViewById(R.id.webview);
		// ����JavaScript
		contentWebView.getSettings().setJavaScriptEnabled(true);

		// ���JS�����ӿ��࣬������� ImageListener
		contentWebView.addJavascriptInterface(new MyJavascriptInterface(), "ImageListener");
		contentWebView.setWebViewClient(new MyWebViewClient());

		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		id = bundle.getInt("id", 0);
		content_type = bundle.getString("content_type", " ");
		itemURL = bundle.getString("itemURL", " ");
		imgURL = bundle.getString("ImageURL", " ");
		author = bundle.getString("author", " ");
		created_at = bundle.getString("created_at", " ");
		updated_at = bundle.getString("updated_at", " ");
		title = bundle.getString("title", " ");
		description = bundle.getString("description", " ");

		initEventsRegister();

		JSONObject jsonRequest = null;
		JsonObjectRequest request = new JsonObjectRequest(itemURL, jsonRequest,
				new Listener<JSONObject>() {

					@Override
					public void onSuccess(JSONObject response) {
						if (response.has("status")) { // ����

						} else {
							try {
								template_html = response.getString("template_html");
								photos = response.getString("photos");

								albumId = response.getInt("id");
								views = response.getInt("views");
								like = response.getInt("like");
								likeTextView.setText(String.valueOf(like));
								colletTextView.setText(String.valueOf(views));

								contentWebView.loadDataWithBaseURL(null, template_html,
										"text/html", "utf-8", null);
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					}

					@Override
					public void onPreExecute() {
						mProgressDialog = new ProgressDialog(AlbumWebViewActivity.this,
								AlertDialog.THEME_HOLO_LIGHT);
						mProgressDialog.setMessage("Loading...");
						mProgressDialog.setIndeterminate(false);
						mProgressDialog.setCancelable(true);
						mProgressDialog.setCanceledOnTouchOutside(false); // ����Ի����Ե����ȡ��
						mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

							@Override
							public void onCancel(DialogInterface dialog) {
								Netroid.getRequestQueue().cancelAll(REQUESTS_TAG);
							}
						});
						mProgressDialog.show();
					}

					// ��onFinish()ʱcancel dialog
					@Override
					public void onFinish() {
						mProgressDialog.cancel();
					}
				});
		request.setTag(REQUESTS_TAG);
		Netroid.addRequest(request);
	}

	@Override
	protected void onResume() {
		super.onResume();
		TextView titleTextView = (TextView) findViewById(R.id.header_textview);
		titleTextView.setText(title);
	}

	// ע��JS��������
	public void addImageClickListner() {
		// ���JS�����Ĺ��ܾ��ǣ��������е�<img>��ǩ�������OnClick������
		// ��ͼƬ�����ʱ�򣬵��ñ���java�ӿڲ�����URL��ȥ
		contentWebView.loadUrl("javascript:(function(){"
				+ "var objs = document.getElementsByTagName(\"img\"); "
				+ "for(var i=0;i<objs.length;i++)  " + "{" + "    objs[i].onclick=function()  "
				+ "{ window.ImageListener.openImage(this.src); }  " + "}" + "})()");
	}

	// JSͨ�Žӿ�
	public class MyJavascriptInterface {
		// Reference : http://blog.csdn.net/zgjxwl/article/details/9627685
		// API17�Ժ��κ�ΪJS��¶�Ľӿڣ�����Ҫ��@JavascriptInterfaceע�ͣ����������Java�����fields
		// ��������JS���ʡ�
		@JavascriptInterface
		public void openImage(String img) {
			Intent intent = new Intent();
			Bundle bundle = new Bundle();
			bundle.putCharSequence("clickedImage", img);
			bundle.putCharSequence("photos", photos);
			bundle.putCharSequence("title", title);
			intent.putExtras(bundle);
			intent.setClass(AlbumWebViewActivity.this, HackyViewPagerActivity.class);
			startActivity(intent);
		}
	}

	// ����
	@SuppressLint("SetJavaScriptEnabled")
	public class MyWebViewClient extends WebViewClient {

		@Override
		public void onPageFinished(WebView view, String url) {
			view.getSettings().setJavaScriptEnabled(true);

			super.onPageFinished(view, url);
			// HTML�������֮����Ӽ���ͼƬ�ĵ��JS����
			log.d("onPageFinished", "ҳ��������");
			addImageClickListner();

		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			view.getSettings().setJavaScriptEnabled(true);

			super.onPageStarted(view, url, favicon);
		}
	}

	private void initEventsRegister() {
		this.likeButton = (ImageButton) findViewById(R.id.bottombar_like);
		this.likeTextView = (TextView) findViewById(R.id.bottombar_like_counter);
		this.collectButton = (ImageButton) findViewById(R.id.bottombar_collect);
		this.colletTextView = (TextView) findViewById(R.id.bottombar_pageview_counter);
		this.backButton = (ImageButton) findViewById(R.id.back_button);

		// find collections
		String[] strings = S.getStringSet(getApplicationContext(), "collected_list");

		for (int i = 0; i < strings.length; i++) {
			if (itemURL.equals(strings[i])) {
				collectFlag = 1;
				collectButton.setImageResource(R.drawable.news_collected);
				break;
			}
		}

		// find liked
		String[] likeSet = S.getStringSet(getApplicationContext(), "liked_list");
		for (int i = 1; i < likeSet.length; i++) {
			if (itemURL.equals(likeSet[i])) {
				likeFlag = true;
				likeButton.setImageResource(R.drawable.message_vote);
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

		backButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
				overridePendingTransition(0, R.anim.base_slide_right_out);
			}
		});
	}

	// �������
	private void handleLike() {
		if (likeFlag)
			return;

		String url = "http://218.192.166.167:3030/v1/contents/" + String.valueOf(albumId) + "/like";

		Map<String, String> mParams = new HashMap<String, String>();
		Netroid.getRequestQueue().add(new PutRequest(url, mParams, new Listener<String>() {
			@Override
			public void onSuccess(String arg0) {
				log.d(arg0);
			}
		}));

		// ���� +1
		likeFlag = true;
		int cnt = Integer.valueOf(likeTextView.getText().toString());
		likeTextView.setText(String.valueOf(++cnt));
		likeButton.setImageResource(R.drawable.message_vote);
		S.addStringSet(getApplicationContext(), "liked_list", itemURL); // ��¼
		Util.showToast(AlbumWebViewActivity.this, "Nice!");
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
				collectButton.setImageResource(R.drawable.news_collect);
				Util.showToast(AlbumWebViewActivity.this, "��ȡ���ղ�");
			} else {
				log.w("ȡ���ղ�ʧ��");
			}
		} else { // �ղ�
			Boolean status = S.addStringSet(getApplicationContext(), "collected_list",
					String.valueOf(id));

			if (status) {
				collectFlag = 1;
				collectButton.setImageResource(R.drawable.news_collected);

				TextView collectCnt = (TextView) findViewById(R.id.bottombar_pageview_counter);
				int cnt = Integer.valueOf((String) collectCnt.getText());
				collectCnt.setText(String.valueOf(++cnt));

				// ������
				S.addStringSet(getApplicationContext(), "collected_list", itemURL);
				S.addStringSet(getApplicationContext(), "collected_list", content_type);
				S.addStringSet(getApplicationContext(), "collected_list", imgURL);
				S.addStringSet(getApplicationContext(), "collected_list", title);
				S.addStringSet(getApplicationContext(), "collected_list", description);
				S.addStringSet(getApplicationContext(), "collected_list", author);
				S.addStringSet(getApplicationContext(), "collected_list", created_at);
				S.addStringSet(getApplicationContext(), "collected_list", updated_at);

				Util.showToast(AlbumWebViewActivity.this, "�ղسɹ�");
			} else {
				log.w("�ղ�ʧ��");
			}
		}
	}

}
