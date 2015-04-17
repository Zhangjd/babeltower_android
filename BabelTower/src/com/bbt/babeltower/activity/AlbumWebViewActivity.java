package com.bbt.babeltower.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.avos.avoscloud.LogUtil.log;
import com.bbt.babeltower.R;
import com.bbt.babeltower.base.MyWebView;
import com.bbt.babeltower.base.Netroid;
import com.bbt.babeltower.base.S;
import com.bbt.babeltower.base.Util;
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

	private MyWebView mWebView = null;

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

	private ImageButton likeButton;
	private ImageButton collectButton;
	private ImageButton backButton;
	private ImageButton floatActionButton;

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
		Util.setStatusBarColor(AlbumWebViewActivity.this);

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

		mWebView = (MyWebView) findViewById(R.id.webview);
		// 启用JavaScript
		mWebView.getSettings().setJavaScriptEnabled(true);

		// 添加JS交互接口类，并起别名 ImageListener
		mWebView.addJavascriptInterface(new MyJavascriptInterface(), "ImageListener");
		mWebView.setWebViewClient(new MyWebViewClient());
		mWebView.setOnCustomScroolChangeListener(new MyWebView.ScrollInterface() {

			@Override
			public void onSChanged(int l, int t, int oldl, int oldt) {
				if (mWebView.getScrollY() == 0) {
					floatActionButton.setVisibility(View.INVISIBLE);
				} else {
					floatActionButton.setVisibility(View.VISIBLE);
				}
			}
		});

		JSONObject jsonRequest = null;
		JsonObjectRequest request = new JsonObjectRequest(itemURL, jsonRequest,
				new Listener<JSONObject>() {

					@Override
					public void onSuccess(JSONObject response) {
						if (response.has("status")) { // 出错

						} else {
							try {
								template_html = response.getString("template_html");
								photos = response.getString("photos");
								mWebView.loadDataWithBaseURL(null, template_html, "text/html",
										"utf-8", null);
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
						mProgressDialog.setCanceledOnTouchOutside(false); // 点击对话框边缘不能取消
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
		request.setTag(REQUESTS_TAG);
		Netroid.addRequest(request);
	}

	@Override
	protected void onResume() {
		super.onResume();
		TextView titleTextView = (TextView) findViewById(R.id.header_textview);
		titleTextView.setText(title);
	}

	// 注入JS函数监听
	public void addImageClickListner() {
		// 这段JS函数的功能就是，遍历所有的<img>标签，并添加OnClick函数，
		// 在图片点击的时候，调用本地java接口并传递URL过去
		mWebView.loadUrl("javascript:(function(){"
				+ "var objs = document.getElementsByTagName(\"img\"); "
				+ "for(var i=0;i<objs.length;i++)  " + "{" + "    objs[i].onclick=function()  "
				+ "{ window.ImageListener.openImage(this.src); }  " + "}" + "})()");
	}

	// JS通信接口
	public class MyJavascriptInterface {
		// Reference : http://blog.csdn.net/zgjxwl/article/details/9627685
		// API17以后，任何为JS暴露的接口，都需要加@JavascriptInterface注释，这样，这个Java对象的fields
		// 将不允许被JS访问。
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

	// 监听
	@SuppressLint("SetJavaScriptEnabled")
	public class MyWebViewClient extends WebViewClient {

		@Override
		public void onPageFinished(WebView view, String url) {
			view.getSettings().setJavaScriptEnabled(true);

			super.onPageFinished(view, url);
			// HTML加载完成之后，添加监听图片的点击JS函数
			log.d("onPageFinished", "页面加载完成");
			addImageClickListner();

		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			view.getSettings().setJavaScriptEnabled(true);

			super.onPageStarted(view, url, favicon);
		}

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			return true;
		}
	}

	private void initEventsRegister() {
		this.backButton = (ImageButton) findViewById(R.id.back_button);
		this.floatActionButton = (ImageButton) findViewById(R.id.float_action_button_up);
		this.likeButton = (ImageButton) findViewById(R.id.webview_like);
		this.collectButton = (ImageButton) findViewById(R.id.webview_collect);

		floatActionButton.setVisibility(View.INVISIBLE);

		// find collections
		String[] strings = S.getStringSet(getApplicationContext(), "collected_list");
		for (int i = 0; i < strings.length; i++) {
			if (itemURL.equals(strings[i])) {
				collectFlag = 1;
				break;
			}
		}

		// find liked
		String[] likeSet = S.getStringSet(getApplicationContext(), "liked_list");
		for (int i = 1; i < likeSet.length; i++) {
			if (itemURL.equals(likeSet[i])) {
				likeFlag = true;
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

		floatActionButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mWebView.setScrollY(0);
			}
		});
	}

	// 处理点赞
	private void handleLike() {
		if (likeFlag)
			return;

		String url = "http://218.192.166.167:3030/v1/contents/" + String.valueOf(id) + "/like";

		Map<String, String> mParams = new HashMap<String, String>();
		Netroid.getRequestQueue().add(new PutRequest(url, mParams, new Listener<String>() {
			@Override
			public void onSuccess(String arg0) {
				log.d(arg0);
			}
		}));

		// 点赞 +1
		likeFlag = true;
		likeButton.setImageResource(R.drawable.babeltower_like_on);
		S.addStringSet(getApplicationContext(), "liked_list", itemURL); // 记录
		Util.showToast(AlbumWebViewActivity.this, "Nice!");
	}

	public class PutRequest extends StringRequest {
		private Map<String, String> mParams;

		// 传入Post参数的Map集合
		public PutRequest(String url, Map<String, String> params, Listener<String> listener) {
			super(Method.PUT, url, listener);
			mParams = params;
		}

		@Override
		public Map<String, String> getParams() throws AuthFailureError {
			return mParams;
		}
	}

	// 处理收藏
	private void handleCollect() {
		if (collectFlag == 1) { // 取消收藏
			String[] collectSet = S.getStringSet(getApplicationContext(), "collected_list");
			String regularEx = S.regularEx;
			Boolean status = false;
			String tmp_all = "";
			for (int i = 1; i < collectSet.length; i++) {
				if ((i + 1) < collectSet.length && collectSet[i + 1].equals(itemURL)) {
					status = true;
					i = i + 8; // 算上i++,跳过9个
					continue;
				}
				tmp_all = tmp_all + regularEx + collectSet[i];
			}
			S.put(getApplicationContext(), "collected_list", tmp_all);

			if (status) {
				collectFlag = 0;
				collectButton.setImageResource(R.drawable.babeltower_collect_off);
				Util.showToast(AlbumWebViewActivity.this, "已取消收藏");
			} else {
				log.w("取消收藏失败");
			}
		} else { // 收藏
			Boolean status = S.addStringSet(getApplicationContext(), "collected_list",
					String.valueOf(id));

			if (status) {
				collectFlag = 1;
				collectButton.setImageResource(R.drawable.babeltower_collect_on);

				// 处理缓存
				S.addStringSet(getApplicationContext(), "collected_list", itemURL);
				S.addStringSet(getApplicationContext(), "collected_list", content_type);
				S.addStringSet(getApplicationContext(), "collected_list", imgURL);
				S.addStringSet(getApplicationContext(), "collected_list", title);
				S.addStringSet(getApplicationContext(), "collected_list", description);
				S.addStringSet(getApplicationContext(), "collected_list", author);
				S.addStringSet(getApplicationContext(), "collected_list", created_at);
				S.addStringSet(getApplicationContext(), "collected_list", updated_at);

				Util.showToast(AlbumWebViewActivity.this, "收藏成功");
			} else {
				log.w("收藏失败");
			}
		}
	}
}
