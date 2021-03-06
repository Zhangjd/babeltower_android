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
import com.duowan.mobile.netroid.request.JsonObjectRequest;
import com.duowan.mobile.netroid.request.StringRequest;
import com.duowan.mobile.netroid.AuthFailureError;
import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
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
	private ImageButton floatActionButton;
	private RelativeLayout area_switch;

	private int collectFlag = 0;
	private boolean likeFlag = false;

	private String template_html = "";
	private String photos = "";

	private final String REQUESTS_TAG = "album_request";

	private CountTimeThread mCountTimeThread;
	private MyHandler mHandler = new MyHandler(this);

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

					@Override
					public void onError(NetroidError error) {
						super.onError(error);
						Util.showToast(AlbumWebViewActivity.this, "网络不给力~请检查网络哦");
					}
				});
		request.setTag(REQUESTS_TAG);
		Netroid.addRequest(request);

		startCountTimeThread();
	}

	@Override
	protected void onResume() {
		super.onResume();
		TextView titleTextView = (TextView) findViewById(R.id.header_textview);
		titleTextView.setText("返回");
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
		this.area_switch = (RelativeLayout) findViewById(R.id.header_switch_area);
		this.floatActionButton = (ImageButton) findViewById(R.id.float_action_button_up);
		this.likeButton = (ImageButton) findViewById(R.id.webview_like);
		this.collectButton = (ImageButton) findViewById(R.id.webview_collect);

		floatActionButton.setVisibility(View.INVISIBLE);

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

		floatActionButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mWebView.setScrollY(0);
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
				// Util.showToast(AlbumWebViewActivity.this, "已取消点赞");
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
			Util.showToast(AlbumWebViewActivity.this, "喜欢成功");
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
				// Util.showToast(AlbumWebViewActivity.this, "已取消收藏");
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

			boolean isVisible = (floatActionButton.getVisibility() == View.VISIBLE);
			if (!isVisible) {
				// 当有按下事件时,如果控件不可见,则使其可见.
				floatActionButton.setVisibility(View.VISIBLE);
				return true;
			}
		}
		return super.onTouchEvent(event);
	}

	// 隐藏悬浮按钮
	private void hide() {
		if (content_type.equals("video"))
			return;
		floatActionButton.setVisibility(View.INVISIBLE);
	}

	// 自动隐藏按钮的一个handler

	public static class MyHandler extends Handler {

		WeakReference<AlbumWebViewActivity> mWebViewActivity;
		private final int MSG_HIDE = 0x0001;

		public MyHandler(AlbumWebViewActivity webViewActivity) {
			mWebViewActivity = new WeakReference<AlbumWebViewActivity>(webViewActivity);
		}

		@Override
		public void handleMessage(Message msg) {
			final AlbumWebViewActivity webViewActivity = mWebViewActivity.get();
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
