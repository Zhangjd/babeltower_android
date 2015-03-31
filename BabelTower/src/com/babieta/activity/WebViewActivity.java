package com.babieta.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.babieta.R;
import com.babieta.base.AsyncImageLoader;
import com.babieta.base.Netroid;
import com.babieta.base.S;
import com.duowan.mobile.netroid.AuthFailureError;
import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.duowan.mobile.netroid.request.JsonObjectRequest;
import com.duowan.mobile.netroid.request.StringRequest;

import android.R.bool;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class WebViewActivity extends SwipeBackActivity {
	private WebView webView;
	private int collectFlag = 0;

	private Button likeButton;
	private TextView likeTextView;
	private Button collectButton;
	private TextView colletTextView;
	private ImageButton backButton;
	private ProgressDialog mProgressDialog;

	private String itemURL;
	private String imgURL;

	private String title = "";
	private String content_type = "";
	private String body_data = "";
	private String author = "";
	private String updated_at = "";
	private String description = "";
	private int articleId = 0;
	private int like = 0;
	private int views = 0;
	private boolean likeFlag = false;

	private final String REQUESTS_TAG = "special_request";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_web_view);

		// get url
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		content_type = bundle.getString("content_type");
		itemURL = bundle.getString("itemURL");
		imgURL = bundle.getString("ImageURL");
		author = bundle.getString("author");
		updated_at = bundle.getString("updated_at");
		title = bundle.getString("title");
		description = bundle.getString("description");

		this.initEventsRegister();

		webView = (WebView) findViewById(R.id.webview);

		WebSettings settings = webView.getSettings();
		settings.setJavaScriptEnabled(true);// 设置可以运行JS脚本
		// settings.setTextZoom(120); //The default is 100.
		settings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		// settings.setUseWideViewPort(true); //打开页面时， 自适应屏幕
		// settings.setLoadWithOverviewMode(true);//打开页面时， 自适应屏幕
		settings.setSupportZoom(false);// 用于设置WebView放大
		settings.setBuiltInZoomControls(false);

		webView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				return false;
			}
		});

		JSONObject jsonRequest = null;
		JsonObjectRequest request = new JsonObjectRequest(itemURL, jsonRequest,
				new Listener<JSONObject>() {
					@Override
					public void onSuccess(JSONObject response) {

						if (response.has("status")) { // 有status:出错
							try {
								webView.loadDataWithBaseURL(null, response.getString("message"),
										"text/html", "utf-8", null);
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}

						String content_type = ""; // [:album, :article, :video,
													// :special]

						try {
							content_type = response.getString("content_type");
							articleId = response.getInt("id");
							views = response.getInt("views");
							like = response.getInt("like");
							title = response.getString("title");

							TextView titleTextView = (TextView) findViewById(R.id.header_textview);
							titleTextView.setText(title);

							likeTextView.setText(String.valueOf(like));
							colletTextView.setText(String.valueOf(views));

							if (content_type.equals("article")) { // 文章类型
								body_data = response.getString("template_html");
								webView.loadDataWithBaseURL(null, body_data, "text/html", "utf-8",
										null);
							} else {
							}
						} catch (JSONException e) {
							System.out.println("error in parsing JSON : body_html");
							e.printStackTrace();
						}
					}

					@Override
					public void onError(NetroidError error) {
						String data = error.getMessage();
						webView.loadData(data, "text/plain", "utf-8");
					}

					@Override
					public void onPreExecute() {
						mProgressDialog = new ProgressDialog(WebViewActivity.this,
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
		// 设置请求标识，这个标识可用于终止该请求时传入的Key
		request.setTag(REQUESTS_TAG);
		Netroid.addRequest(request);
	}

	private void initEventsRegister() {
		this.likeButton = (Button) findViewById(R.id.bottombar_like);
		this.likeTextView = (TextView) findViewById(R.id.bottombar_like_textview);
		this.collectButton = (Button) findViewById(R.id.bottombar_collect);
		this.colletTextView = (TextView) findViewById(R.id.bottombar_collect_textview);
		this.backButton = (ImageButton) findViewById(R.id.back_button);

		// find collections
		String[] strings = S.getStringSet(getApplicationContext(), "collected_list");

		for (int i = 0; i < strings.length; i++) {
			if (itemURL.equals(strings[i])) {
				collectFlag = 1;
				collectButton.setBackgroundResource(R.drawable.news_collected);
				break;
			}
		}

		likeButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				handleLike();
			}
		});

		likeTextView.setOnClickListener(new View.OnClickListener() {

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

		colletTextView.setOnClickListener(new View.OnClickListener() {

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

	// 处理点赞
	private void handleLike() {
		if (likeFlag)
			return;

		String url = "http://218.192.166.167:3030/v1/contents/" + String.valueOf(articleId)
				+ "/like";

		Map<String, String> mParams = new HashMap<String, String>();
		Netroid.getRequestQueue().add(new PutRequest(url, mParams, new Listener<String>() {
			@Override
			public void onSuccess(String arg0) {
				System.out.println(arg0);
			}
		}));

		// 点赞 +1
		likeFlag = true;
		int cnt = Integer.valueOf(likeTextView.getText().toString());
		likeTextView.setText(String.valueOf(++cnt));
		likeButton.setBackgroundResource(R.drawable.message_vote);

		Toast.makeText(WebViewActivity.this, "Nice!", Toast.LENGTH_SHORT).show();
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
			System.out.println("取消收藏,当前url" + itemURL + "当前长度" + collectSet.length);
			String regularEx = S.regularEx;
			Boolean status = false;
			String tmp_all = "";
			for (int i = 1; i < collectSet.length; i++) {
				if (collectSet[i].equals(itemURL)) {
					status = true;
					i = i + 6; // 算上i++,跳过7个
					continue;
				}
				tmp_all = tmp_all + regularEx + collectSet[i];
			}
			S.put(getApplicationContext(), "collected_list", tmp_all);

			System.out.println("tmp_all : " + tmp_all);

			if (status) {
				collectFlag = 0;
				collectButton.setBackgroundResource(R.drawable.news_collect);
				Toast.makeText(WebViewActivity.this, "已取消收藏", Toast.LENGTH_SHORT).show();
			} else {
				System.out.println("取消收藏失败");
			}
		} else { // 收藏

			System.out.println("收藏");

			Boolean status = S.addStringSet(getApplicationContext(), "collected_list", itemURL);

			if (status) {
				collectFlag = 1;
				collectButton.setBackgroundResource(R.drawable.news_collected);

				TextView collectCnt = (TextView) findViewById(R.id.bottombar_collect_textview);
				int cnt = Integer.valueOf((String) collectCnt.getText());
				collectCnt.setText(String.valueOf(++cnt));

				// 处理缓存
				S.addStringSet(getApplicationContext(), "collected_list", content_type);
				S.addStringSet(getApplicationContext(), "collected_list", imgURL);
				S.addStringSet(getApplicationContext(), "collected_list", title);
				S.addStringSet(getApplicationContext(), "collected_list", author);
				S.addStringSet(getApplicationContext(), "collected_list", updated_at);
				S.addStringSet(getApplicationContext(), "collected_list", description);
				AsyncImageLoader loader = new AsyncImageLoader(getApplicationContext());

				// 将图片缓存至外部文件中
				loader.setCache2File(true); // false
				// 设置外部缓存文件夹
				loader.setCachedDir(getApplicationContext().getCacheDir().getAbsolutePath());

				// 下载图片，第二个参数是否缓存至内存中
				loader.downloadImage(imgURL, true/* false */, new AsyncImageLoader.ImageCallback() {
					@Override
					public void onImageLoaded(Bitmap bitmap, String imageUrl) {
						if (bitmap != null) {

						} else {
							// 下载失败
						}
					}
				});

				Toast.makeText(WebViewActivity.this, "已收藏", Toast.LENGTH_SHORT).show();
			} else {
				System.out.println("收藏失败");
			}
		}
	}
}
