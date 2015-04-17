package com.bbt.babeltower.activity;

import com.bbt.babeltower.R;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

public class VideoActivity extends SwipeBackActivity {

	private WebView mWebView;
	private FrameLayout mFullscreenContainer;
	private FrameLayout mContentView;
	private View mCustomView = null;

	private String videoURL = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_video);

		// get url
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		videoURL = bundle.getString("videoURL");

		initViews();
		initWebView();

		// if (getPhoneAndroidSDK() >= 14) {// 4.0 需打开硬件加速
		// getWindow().setFlags(0x1000000, 0x1000000);
		// }

		mWebView.loadUrl(videoURL);

	}

	private void initViews() {
		mFullscreenContainer = (FrameLayout) findViewById(R.id.video_fullscreen_custom_content);
		mContentView = (FrameLayout) findViewById(R.id.video_main_content);
		mWebView = (WebView) findViewById(R.id.video_webview_player);
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void initWebView() {
		WebSettings settings = mWebView.getSettings();
		settings.setJavaScriptEnabled(true);
		settings.setJavaScriptCanOpenWindowsAutomatically(true);
		settings.setAllowFileAccess(true);
		settings.setLoadWithOverviewMode(true);

		mWebView.setWebChromeClient(new MyWebChromeClient());
		mWebView.setWebViewClient(new MyWebViewClient());
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
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
			mWebView.loadData("", "text/html; charset=UTF-8", null);
			finish();
			overridePendingTransition(0, R.anim.base_slide_right_out);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public static int getPhoneAndroidSDK() {
		int version = 0;
		try {
			version = Integer.valueOf(android.os.Build.VERSION.SDK_INT);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return version;
	}

	class MyWebChromeClient extends WebChromeClient {
		private CustomViewCallback mCustomViewCallback;
		private int mOriginalOrientation = 1;

		@Override
		public void onShowCustomView(View view, CustomViewCallback callback) {
			onShowCustomView(view, mOriginalOrientation, callback);
			super.onShowCustomView(view, callback);
		}

		public void onShowCustomView(View view, int requestedOrientation,
				WebChromeClient.CustomViewCallback callback) {
			if (mCustomView != null) {
				callback.onCustomViewHidden();
				return;
			}
			if (getPhoneAndroidSDK() >= 14) {
				mFullscreenContainer.addView(view);
				mCustomView = view;
				mCustomViewCallback = callback;
				mOriginalOrientation = getRequestedOrientation();
				mContentView.setVisibility(View.INVISIBLE);
				mFullscreenContainer.setVisibility(View.VISIBLE);
				mFullscreenContainer.bringToFront();

				setRequestedOrientation(mOriginalOrientation);
			}

		}

		public void onHideCustomView() {
			mContentView.setVisibility(View.VISIBLE);
			if (mCustomView == null) {
				return;
			}
			mCustomView.setVisibility(View.GONE);
			mFullscreenContainer.removeView(mCustomView);
			mCustomView = null;
			mFullscreenContainer.setVisibility(View.GONE);
			try {
				mCustomViewCallback.onCustomViewHidden();
			} catch (Exception e) {
			}
			// Show the content view.
			setRequestedOrientation(mOriginalOrientation);
		}
	}

	class MyWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			return true; // 返回true表明点击网页里面的链接还是在当前的WebView里跳转
		}
	}

}
