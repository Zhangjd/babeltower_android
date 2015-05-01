package com.bbt.babeltower.fragment;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.json.JSONException;
import org.json.JSONObject;

import com.bbt.babeltower.R;
import com.bbt.babeltower.activity.MainActivity;
import com.bbt.babeltower.adapter.ListPostAdapter;
import com.bbt.babeltower.adapter.PageCarouselAdapter;
import com.bbt.babeltower.base.ApiData;
import com.bbt.babeltower.base.ApiUrl;
import com.bbt.babeltower.base.MyApplication;
import com.bbt.babeltower.base.Netroid;
import com.bbt.babeltower.base.S;
import com.bbt.babeltower.base.Util;
import com.bbt.babeltower.bean.PostBean;
import com.bbt.babeltower.layout.CarouselViewPager;
import com.bbt.babeltower.layout.IndicatorLayout;
import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.duowan.mobile.netroid.request.JsonObjectRequest;
import com.gc.materialdesign.widgets.SnackBar;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class MainFragment extends Fragment {
	private View view;
	private View pageView;
	private CarouselViewPager viewPager;
	private IndicatorLayout indicatorLayout;
	private PageCarouselAdapter pageCarouselAdapter;

	private PullToRefreshListView listView;
	private ListPostAdapter listPostAdapter;
	private ScheduledExecutorService scheduledExecutorService; // 自动翻页ViewPager的服务

	// 关于Focus ViewPager的数据
	private int currentViewPagerItem; // 当前ViewPager页面索引
	private boolean focusLoadedFlag = false;
	private LinkedList<PostBean> focusData = new LinkedList<PostBean>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initOnCreate();
		loadTimeline(); // 从本地读取Timeline

		// 提示更新
		new Thread() {
			public void run() {
				try {
					Thread.sleep(1000);
					if (MyApplication.updateResponse != null) {
						Message msg = new Message();
						mUpdateHandler.sendMessage(msg);
					}
					if (S.getString(getActivity(), "firstOpen").equals("")) {
						S.put(getActivity(), "firstOpen", "1");
						Message msg = new Message();
						mFirstStartHandler.sendMessage(msg);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			};
		}.start();
	}

	// Fragment在不可见的时候会回收所有View，所以在出栈的时候上一个Fragment需要重新初始化View
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		this.view = inflater.inflate(R.layout.index_timeline, container, false);
		this.initPostListView(); // Timeline部分
		this.initPageView(); // Focus部分
		if (focusLoadedFlag == false)
			requestForFocus();

		listPostAdapter.notifyDataSetChanged();
		TextView textView = (TextView) pageView.findViewById(R.id.vp_main_text);
		if (listPostAdapter.getCount() == 0) {
			textView.setText("下拉刷新看看~");
		} else {
			textView.setText("新闻列表");
			textView.setVisibility(View.GONE);
		}

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		Util.setStatusBarColor(getActivity());
		// 设置标题
		TextView titleTextView = (TextView) getActivity().findViewById(R.id.header_textview);
		titleTextView.setText("今日新闻");
		// 改变ViewPager高度按16:9
		viewPager.post(new Runnable() {
			@Override
			public void run() {
				int width = viewPager.getWidth();
				if (width == 0)
					return;
				int height = width * 9 / 16;
				viewPager.setLayoutParams(new RelativeLayout.LayoutParams(width, height));
			}
		});
		// 每隔5秒钟切换一张图片
		startViewPagerTask();
	}

	@Override
	public void onPause() {
		super.onPause();
		// 停止翻页
		stopViewPagerTask();
	}

	// 每隔5秒钟切换一张图片
	public void startViewPagerTask() {
		scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
		scheduledExecutorService
				.scheduleWithFixedDelay(new ViewPagerTask(), 5, 5, TimeUnit.SECONDS);
	}

	// 停止翻页
	public void stopViewPagerTask() {
		scheduledExecutorService.shutdown();
	}

	// 加载主页listAdapter
	// (fragment生命周期:http://developer.android.com/guide/components/fragments.html)
	public void initOnCreate() {
		listPostAdapter = new ListPostAdapter(getActivity());
	}

	@SuppressLint("InflateParams")
	private void initPageView() {
		pageView = LayoutInflater.from(getActivity()).inflate(R.layout.index_outline, null);
		viewPager = (CarouselViewPager) pageView.findViewById(R.id.vp_main);
		pageCarouselAdapter = new PageCarouselAdapter(getActivity());
		pageCarouselAdapter.setPostBeans(focusData);
		viewPager.setAdapter(pageCarouselAdapter);
		indicatorLayout = (IndicatorLayout) pageView.findViewById(R.id.indicate_main);
		indicatorLayout.setViewPage(viewPager);
		listView.getRefreshableView().addHeaderView(pageView, null, false);
	}

	private class ViewPagerTask implements Runnable {

		@Override
		public void run() {
			currentViewPagerItem = (viewPager.getCurrentItem() + 1)
					% (pageCarouselAdapter.getCount());
			// 更新界面
			mHandler.obtainMessage().sendToTarget();
		}
	}

	// 翻页ViewPager的Handler
	private MyHandler mHandler = new MyHandler(this);

	public static class MyHandler extends Handler {
		WeakReference<MainFragment> mFragment;

		public MyHandler(MainFragment fragment) {
			mFragment = new WeakReference<MainFragment>(fragment);
		}

		@Override
		public void handleMessage(Message msg) {
			// 设置当前页面
			MainFragment theFragment = mFragment.get();
			theFragment.viewPager.setCurrentItem(theFragment.currentViewPagerItem);
		}
	}

	// 弹出升级提示框的Handler
	private UpdateHandler mUpdateHandler = new UpdateHandler(this);

	public static class UpdateHandler extends Handler {
		WeakReference<MainFragment> mFragment;

		public UpdateHandler(MainFragment fragment) {
			mFragment = new WeakReference<MainFragment>(fragment);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			final MainFragment theFragment = mFragment.get();
			JSONObject response = MyApplication.updateResponse;
			try {
				MyApplication.updateResponse = null;
				// Dialog materialDialog = new Dialog(theFragment.getActivity(),
				// "新版本下载", "版本号:"
				// + response.getString("version") + "\n更新说明: "
				// + response.getString("description"));
				// materialDialog.setOnAcceptButtonClickListener(new
				// View.OnClickListener() {
				//
				// @Override
				// public void onClick(View v) {
				// Intent intent = new Intent();
				// intent.setAction("android.intent.action.VIEW");
				// Uri content_url = Uri.parse(ApiUrl.BABIETA_DOWNLOAD);
				// intent.setData(content_url);
				// theFragment.startActivity(intent);
				// }
				// });
				// materialDialog.addCancelButton("取消", new
				// View.OnClickListener() {
				//
				// @Override
				// public void onClick(View v) {
				//
				// }
				// });
				// materialDialog.show();

				SnackBar snackbar = new SnackBar(theFragment.getActivity(), "检测到新版本\n版本号:"
						+ response.getString("version") + "\n更新说明: "
						+ response.getString("description"), "下载", new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent();
						intent.setAction("android.intent.action.VIEW");
						Uri content_url = Uri.parse(ApiUrl.BABIETA_DOWNLOAD);
						intent.setData(content_url);
						theFragment.startActivity(intent);
					}
				});
				snackbar.setDismissTimer(5000);
				snackbar.show();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	// 首次启动的Handler
	private FirstStartHandler mFirstStartHandler = new FirstStartHandler(this);

	public static class FirstStartHandler extends Handler {
		WeakReference<MainFragment> mFragment;

		public FirstStartHandler(MainFragment fragment) {
			mFragment = new WeakReference<MainFragment>(fragment);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			MainActivity.toggleMenu();
		}
	}

	private void initPostListView() {
		listView = (PullToRefreshListView) view.findViewById(R.id.listview_main);
		listView.getRefreshableView().setDivider(null); // 分隔符
		// listView.getRefreshableView().setVerticalScrollBarEnabled(false); //
		// 竖直方向滚动条
		listView.setMode(Mode.BOTH); // 设置上下拉模式
		listView.getLoadingLayoutProxy().setRefreshingLabel(
				getResources().getString(R.string.waiting_tips));
		listView.setAdapter(listPostAdapter);

		// 设置点击Timeline Item的监听
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// position起始位置是2,因为前面放置了ViewPager和TextView
				PostBean postBean = listPostAdapter.postBeans.get(position - 2);
				Activity activity = getActivity();
				Util.handleItemClick(activity, postBean);
			}
		});

		// 设置上下拉的监听
		listView.setOnRefreshListener(new OnRefreshListener2<ListView>() {
			// 顶部下拉刷新timeline
			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				// 显示最近一次刷新的时间
				String label = DateUtils.formatDateTime(getActivity(), System.currentTimeMillis(),
						DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE
								| DateUtils.FORMAT_ABBREV_ALL);
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);

				new AsyncTask<Void, Void, String>() {

					@Override
					protected String doInBackground(Void... arg0) {
						if (isNetworkConnected(getActivity())) {
							return ApiData.httpGet(ApiUrl.BABIETA_BASE_URL
									+ ApiUrl.BABIETA_CONTENT_LIST + "?on_timeline=true");
							// "http://218.192.166.167:3030/v1/contents?on_timeline=true"
						} else {
							return "";
						}
					}

					protected void onPostExecute(String result) {
						handlePostExecute(result, 0);
					}
				}.execute();

				requestForFocus();
			}

			// 底部上拉刷新timeline
			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				int count = listPostAdapter.getCount();
				String targetURL;
				if (count == 0) {
					targetURL = ApiUrl.BABIETA_BASE_URL + ApiUrl.BABIETA_CONTENT_LIST;
				} else {
					int minCnt = 0;
					for (int i = 0; i < count; i++) {
						PostBean bean = (PostBean) listPostAdapter.getItem(i);
						if (minCnt == 0)
							minCnt = bean.getId();
						else
							minCnt = minCnt > bean.getId() ? bean.getId() : minCnt;
					}
					targetURL = ApiUrl.BABIETA_BASE_URL + ApiUrl.BABIETA_CONTENT_LIST
							+ "?on_timeline=true&max_id=" + (minCnt - 1);
				}
				final String final_targetURL = targetURL;

				new AsyncTask<Void, Void, String>() {

					@Override
					protected String doInBackground(Void... arg0) {
						if (isNetworkConnected(getActivity())) {
							return ApiData.httpGet(final_targetURL);
						} else {
							return "";
						}
					}

					protected void onPostExecute(String result) {
						handlePostExecute(result, 1); // 下拉刷新就不存储旧内容的TimeLine了
					};
				}.execute();
			}
		});
	}

	// 解析Timeline接口返回的数据
	private void handlePostExecute(String result, int type) {
		if (!isNetworkConnected(getActivity())) {
			Util.showToast(getActivity(), "没有可用网络,请检查网络设置~");
			listView.onRefreshComplete();
		} else if (result == "") {
			Util.showToast(getActivity(), "网络开小差了,不如再试试吧~");
			listView.onRefreshComplete();
		} else {
			LinkedList<PostBean> postBeans = PostBean.parseBabietaTimeline(result, getActivity());
			if (type == 0) {
				listPostAdapter.clearPost(); // 上拉刷新,清空原来的数据
				listPostAdapter.appendPost(postBeans);
				listPostAdapter.sortPost();

				saveTimeline();
			} else { // 下拉刷新,直接添加数据
				listPostAdapter.appendPost(postBeans);
				listPostAdapter.sortPost();
			}
			listPostAdapter.notifyDataSetInvalidated();
			TextView textView = (TextView) pageView.findViewById(R.id.vp_main_text);
			if (listPostAdapter.getCount() == 0) {
				textView.setText("还没有内容，下拉刷新看看~");
			} else {
				textView.setText("新闻列表");
				textView.setVisibility(View.GONE);
			}
			listView.onRefreshComplete();
		}
	}

	// 存储TimeLine到SharedPreferences中
	private void saveTimeline() {
		S.put(getActivity(), "timeline_list", "");
		int count = listPostAdapter.getCount() > 10 ? 10 : listPostAdapter.getCount(); // 只保存前10条
		String targetString = "";
		String seperator = S.regularEx;
		for (int i = 0; i < count; i++) {
			PostBean bean = (PostBean) listPostAdapter.getItem(i);

			// 1id 2链接 3标题 4作者 5时间 6图片地址 7头图URL 8注释 9类型
			targetString = targetString + seperator + String.valueOf(bean.getId()) + seperator
					+ bean.getItemURL() + seperator + bean.getTitle() + seperator
					+ bean.getAuthor() + seperator + bean.getCreatedAt() + seperator
					+ bean.getImageUrl() + seperator + bean.getHeaderImageUrl() + seperator
					+ bean.getDescription() + seperator + bean.getContentType();
		}
		S.put(getActivity(), "timeline_list", targetString);
	}

	// 从本地缓存读取TimeLine
	private boolean loadTimeline() {
		LinkedList<PostBean> postBeans = new LinkedList<PostBean>();
		String[] collectlist = S.getStringSet(getActivity(), "timeline_list");

		for (int i = 1; i < (collectlist.length); i++) {
			// System.out.println(collectlist[i]);
		}

		for (int i = 1; i < (collectlist.length);) {
			PostBean postBean = new PostBean();

			try {
				Integer.valueOf(collectlist[i]);
			} catch (Exception e) {
				e.printStackTrace();
				S.put(getActivity(), "timeline_list", "");
				return false;
			}

			postBean.setId(Integer.valueOf(collectlist[i]));
			postBean.setItemURL(collectlist[i + 1]);
			postBean.setTitle(collectlist[i + 2]);
			postBean.setAuthor(collectlist[i + 3]);
			postBean.setUpdatedAt(collectlist[i + 4]);
			postBean.setCreatedAt(collectlist[i + 4]); // (注意这个时间同updated_at)
			postBean.setImageUrl(collectlist[i + 5]);
			postBean.setHeaderImageUrl(collectlist[i + 6]);
			postBean.setDescription(collectlist[i + 7]);
			postBean.setContentType(collectlist[i + 8]);

			postBeans.add(postBean); // 添加到链表
			i = i + 9;
		}

		listPostAdapter.appendPost(postBeans);
		listPostAdapter.sortPost();
		return true;
	}

	// 网络读取Focus内容
	private boolean requestForFocus() {
		if (!isNetworkConnected(getActivity())) {
			Util.showToast(getActivity(), "没有可用网络,请检查网络设置~");
			return false;
		} else {
			JSONObject jsonRequest = null;
			String url = ApiUrl.BABIETA_BASE_URL + ApiUrl.BABIETA_CONTENT_LIST
					+ "?on_focus=true&limit=5";
			JsonObjectRequest request = new JsonObjectRequest(url, jsonRequest,
					new Listener<JSONObject>() {
						@Override
						public void onSuccess(JSONObject response) {
							try {
								if (response.has("status") && response.getInt("status") == 0) {
									// 先清空
									LinkedList<PostBean> temp = new LinkedList<PostBean>();
									pageCarouselAdapter.setPostBeans(temp);
									pageCarouselAdapter.notifyDataSetChanged();
									// 再读取内容
									String result = response.toString();
									focusData = PostBean.parseSection(result, getActivity());
									pageCarouselAdapter.setPostBeans(focusData);
									pageCarouselAdapter.notifyDataSetChanged();
									indicatorLayout.setViewPage(viewPager); // 设置focus右下角的指示器
									focusLoadedFlag = true;
								} else {
									// 接口出错
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}

						@Override
						public void onError(NetroidError error) {
							Util.showToast(getActivity(), "网络开小差了,不如再试试吧~");
						}
					});
			// 设置请求标识，这个标识可用于终止该请求时传入的Key
			request.setTag("LOAD_FOCUS");
			Netroid.addRequest(request);
		}
		return true;
	}

	// 检测网络连接
	public boolean isNetworkConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
			if (mNetworkInfo != null) {
				return mNetworkInfo.isAvailable();
			}
		}
		return false;
	}

	// 检测Wifi连接
	public boolean isWifiConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mWiFiNetworkInfo = mConnectivityManager
					.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			if (mWiFiNetworkInfo != null) {
				return mWiFiNetworkInfo.isAvailable();
			}
		}
		return false;
	}

	// 检测移动数据连接
	public boolean isMobileConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mMobileNetworkInfo = mConnectivityManager
					.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			if (mMobileNetworkInfo != null) {
				return mMobileNetworkInfo.isAvailable();
			}
		}
		return false;
	}

	public void manualRefresh() {
		listView.setMode(Mode.PULL_FROM_START);
		listView.setRefreshing();
		listView.setMode(Mode.BOTH);
	}

	public void handleFocusClick(int index) {
		PostBean postBean = focusData.get(index);
		Activity activity = getActivity();
		Util.handleItemClick(activity, postBean);
	}
}
