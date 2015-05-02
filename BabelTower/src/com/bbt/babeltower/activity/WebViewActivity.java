package com.bbt.babeltower.activity;

import java.lang.ref.WeakReference;
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
import com.duowan.mobile.netroid.AuthFailureError;
import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.duowan.mobile.netroid.request.JsonObjectRequest;
import com.duowan.mobile.netroid.request.StringRequest;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

@SuppressLint("SetJavaScriptEnabled")
public class WebViewActivity extends SwipeBackActivity {
	private MyWebView mWebView;
	private int collectFlag = 0;

	private ImageButton floatActionButton_up; // 悬浮按键-返回顶部
	private ImageButton floatActionButton_fullscreen; // 悬浮按键-全屏
	private ImageButton likeButton;
	private ImageButton collectButton;
	private RelativeLayout area_switch;
	private ProgressDialog mProgressDialog;

	private int id = 0;
	private String itemURL = "";
	private String imgURL = "";
	private String title = "";
	private String content_type = "";
	private String body_data = "";
	private String author = "";
	private String created_at = "";
	private String updated_at = "";
	private String description = "";
	private String video_url = "";

	private boolean likeFlag = false;

	private final String REQUESTS_TAG = "special_request";

	private CountTimeThread mCountTimeThread;
	private MyHandler mHandler = new MyHandler(this);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_web_view);
		Util.setStatusBarColor(WebViewActivity.this);

		// get URL
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		id = bundle.getInt("id");
		content_type = bundle.getString("content_type", " ");
		itemURL = bundle.getString("itemURL", " ");
		imgURL = bundle.getString("ImageURL", " ");
		author = bundle.getString("author", " ");
		created_at = bundle.getString("created_at", " ");
		updated_at = bundle.getString("updated_at", " ");
		title = bundle.getString("title", " ");
		description = bundle.getString("description", " ");

		TextView titleTextView = (TextView) findViewById(R.id.header_textview);
		titleTextView.setText("返回");

		this.initEventsRegister();

		mWebView = (MyWebView) findViewById(R.id.webview);
		WebSettings settings = mWebView.getSettings();
		settings.setJavaScriptEnabled(true);// 设置可以运行JS脚本
		// settings.setTextZoom(120); //The default is 100.
		settings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		// settings.setUseWideViewPort(true); //打开页面时， 自适应屏幕
		// settings.setLoadWithOverviewMode(true);//打开页面时， 自适应屏幕
		settings.setSupportZoom(false);// 用于设置WebView放大
		settings.setBuiltInZoomControls(false);

		WebViewClient mWebViewClient = new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				return false;
			}

		};
		mWebView.setWebViewClient(mWebViewClient);
		mWebView.setOnCustomScroolChangeListener(new MyWebView.ScrollInterface() {

			@Override
			public void onSChanged(int l, int t, int oldl, int oldt) {
				if (content_type.equals("article")) {
					if (mWebView.getScrollY() == 0) {
						floatActionButton_up.setVisibility(View.INVISIBLE);
					} else {
						floatActionButton_up.setVisibility(View.VISIBLE);
					}
				}
			}
		});

		JSONObject jsonRequest = null;
		JsonObjectRequest request = new JsonObjectRequest(itemURL, jsonRequest,
				new Listener<JSONObject>() {
					@Override
					public void onSuccess(JSONObject response) {
						if (response.has("status")) { // 有status:出错
							try {
								mWebView.loadDataWithBaseURL(null, response.getString("message"),
										"text/html", "utf-8", null);
							} catch (JSONException e) {
								e.printStackTrace();
							}
						} else {
							try {
								if (content_type.equals("article")) { // 文章类型
									body_data = response.getString("template_html");
									mWebView.loadDataWithBaseURL(null, body_data, "text/html",
											"utf-8", null);
								} else if (content_type.equals("video")) { // 视频类型
									body_data = response.getString("template_html");
									mWebView.loadDataWithBaseURL(null, body_data, "text/html",
											"utf-8", null);

									JSONObject video = response.getJSONObject("video");
									video_url = video.getString("player_url");
								} else {
								}
							} catch (JSONException e) {
								log.w("error in parsing JSON : body_html");
								e.printStackTrace();
							}
						}
					}

					@Override
					public void onError(NetroidError error) {
						super.onError(error);
						Util.showToast(WebViewActivity.this, "网络不给力~请检查网络哦");
					}

					@Override
					public void onPreExecute() {
						mProgressDialog = new ProgressDialog(WebViewActivity.this,
								AlertDialog.THEME_HOLO_LIGHT);
						mProgressDialog.setMessage(getResources().getString(R.string.waiting_tips));
						mProgressDialog.setIndeterminate(false);
						mProgressDialog.setCancelable(true);
						mProgressDialog.setCanceledOnTouchOutside(false); // 点击对话框边缘不能取消
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

					// 在onFinish()时cancel dialog
					@Override
					public void onFinish() {
						mProgressDialog.cancel();
					}

				});
		// 设置请求标识，这个标识可用于终止该请求时传入的Key
		request.setTag(REQUESTS_TAG);
		Netroid.addRequest(request);

		if (VideoActivity.getPhoneAndroidSDK() >= 14) {// 4.0 需打开硬件加速
			getWindow().setFlags(0x1000000, 0x1000000);
		}

		startCountTimeThread();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mWebView.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mWebView.onResume();
		mWebView.loadDataWithBaseURL(null, body_data, "text/html", "utf-8", null);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mWebView.destroy();
	}

	private void initEventsRegister() {

		this.area_switch = (RelativeLayout) findViewById(R.id.header_switch_area);
		this.floatActionButton_up = (ImageButton) findViewById(R.id.float_action_button_up);
		this.floatActionButton_fullscreen = (ImageButton) findViewById(R.id.float_action_button_fullscreen);
		this.likeButton = (ImageButton) findViewById(R.id.webview_like);
		this.collectButton = (ImageButton) findViewById(R.id.webview_collect);

		floatActionButton_up.setVisibility(View.INVISIBLE);
		if (content_type.equals("video")) {
			floatActionButton_fullscreen.setVisibility(View.VISIBLE);
		}

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
				mWebView.loadData("", "text/html; charset=UTF-8", null);
				finish();
				overridePendingTransition(0, R.anim.base_slide_right_out);
			}
		});

		floatActionButton_up.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mWebView.setScrollY(0);
			}
		});

		floatActionButton_fullscreen.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(WebViewActivity.this, VideoActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("videoURL", video_url);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
	}

	// 处理点赞
	private void handleLike() {
		if (likeFlag) { // 已经点赞->取消
			String url = "http://218.192.166.167:3030/v1/contents/" + id + "/unlike";
			Map<String, String> mParams = new HashMap<String, String>();
			Netroid.getRequestQueue().add(new PutRequest(url, mParams, new Listener<String>() {
				@Override
				public void onSuccess(String arg0) {
					log.d(arg0);
				}
			}));

			// 去除点赞列表里面对应的id
			String[] strings = S.getStringSet(getApplicationContext(), "liked_list");
			String regularEx = S.regularEx;
			String tmp_all = "";
			Boolean status = false;
			for (int i = 1; i < strings.length; i++) {
				if (itemURL.equals(strings[i])) {
					if (strings[i].equals(itemURL)) {
						status = true;
						i = i + 8; // 算上i++,跳过9个
						continue;
					}
					tmp_all = tmp_all + regularEx + strings[i];
				}
			}
			S.put(getApplicationContext(), "liked_list", tmp_all);

			if (status) {
				likeFlag = false;
				likeButton.setImageResource(R.drawable.babeltower_like_off);
				// Util.showToast(WebViewActivity.this, "已取消点赞");
			} else {
				log.w("取消点赞失败");
			}
		} else { // 未点赞->点赞
			String url = "http://218.192.166.167:3030/v1/contents/" + id + "/like";
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
			Util.showToast(WebViewActivity.this, "喜欢成功");
		}
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
				// Util.showToast(WebViewActivity.this, "已取消收藏");
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

				Util.showToast(WebViewActivity.this, "收藏成功");
			} else {
				log.w("收藏失败");
			}
		}
	}

	/**
	 * 开始启动线程控制按钮组件的显示.
	 */
	private void startCountTimeThread() {
		mCountTimeThread = new CountTimeThread(3);
		mCountTimeThread.start();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			// 重置mControllButtonLayout已经显示的时间
			mCountTimeThread.reset();

			boolean isVisible = (floatActionButton_up.getVisibility() == View.VISIBLE);
			if (!isVisible) {
				// 当有按下事件时,如果控件不可见,则使其可见.
				floatActionButton_up.setVisibility(View.VISIBLE);
				return true;
			}
		}
		return super.onTouchEvent(event);
	}

	// 隐藏悬浮按钮
	private void hide() {
		if (content_type.equals("video"))
			return;
		floatActionButton_up.setVisibility(View.INVISIBLE);
	}

	// 自动隐藏按钮的一个handler

	public static class MyHandler extends Handler {

		WeakReference<WebViewActivity> mWebViewActivity;
		private final int MSG_HIDE = 0x0001;

		public MyHandler(WebViewActivity webViewActivity) {
			mWebViewActivity = new WeakReference<WebViewActivity>(webViewActivity);
		}

		@Override
		public void handleMessage(Message msg) {
			final WebViewActivity webViewActivity = mWebViewActivity.get();
			if (webViewActivity != null) {
				switch (msg.what) {
				case MSG_HIDE:
					webViewActivity.hide();
					break;
				}
			}

			super.handleMessage(msg);
		}

		public void sendHideControllMessage() {
			obtainMessage(MSG_HIDE).sendToTarget();
		}
	}

	// 计时器进程
	private class CountTimeThread extends Thread {
		private final long maxVisibleTime;
		private long startVisibleTime;

		/**
		 * @param second
		 *            设置按钮控件最大可见时间,单位是秒
		 */
		public CountTimeThread(int second) {
			// 将时间换算成毫秒
			maxVisibleTime = second * 1000;

			// 设置为后台线程.
			setDaemon(true);
		}

		/**
		 * 每当界面有操作时就需要重置mControllButtonLayout开始显示的时间,
		 */
		public synchronized void reset() {
			startVisibleTime = System.currentTimeMillis();
		}

		public void run() {
			startVisibleTime = System.currentTimeMillis();

			while (true) {
				// 如果已经到达了最大显示时间, 则隐藏功能控件.
				if ((startVisibleTime + maxVisibleTime) < System.currentTimeMillis()) {
					// 发送隐藏按钮控件消息.
					mHandler.sendHideControllMessage();

					startVisibleTime = System.currentTimeMillis();
				}

				try {
					// 线程休眠1s.
					Thread.sleep(1000);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
