package com.bbt.babeltower.fragment;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

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
import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.duowan.mobile.netroid.request.JsonObjectRequest;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

@SuppressLint("InflateParams")
public class SectionFragment extends Fragment {
	private final String REQUESTS_TAG = "section_request";
	private View view;

	private ViewPager viewPager; // ViewPager
	private PagerTabStrip pagerTabStrip; // ViewPager的指示器，效果就是一个横的粗的下划线
	private List<View> viewList = new ArrayList<View>(); // 把需要滑动的页卡添加到这个list中
	private List<String> titleList = new ArrayList<String>(); // viewpager的标题
	private PagerAdapter pagerAdapter;

	private PullToRefreshListView listView;
	private ListPostAdapter listPostAdapter;
	private HashMap<String, ListPostAdapter> adapterList = new HashMap<String, ListPostAdapter>();

	String[] content_type = { "article", "album", "video", "special" };

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		this.view = inflater.inflate(R.layout.fragment_section, container, false);
		// MainActivity.setMenuTouchModeToMargin();

		viewPager = (ViewPager) view.findViewById(R.id.section_viewpager);
		pagerTabStrip = (PagerTabStrip) view.findViewById(R.id.section_pagertabstrip);
		pagerTabStrip.setTabIndicatorColor(getResources().getColor(R.color.babel_orange));
		pagerTabStrip.setDrawFullUnderline(false);

		pagerAdapter = new PagerAdapter() {

			@Override
			public int getCount() {
				return viewList.size();
			}

			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				return arg0 == arg1;
			}

			@Override
			public void destroyItem(ViewGroup container, int position, Object object) {
				container.removeView(viewList.get(position));
			}

			@Override
			public Object instantiateItem(ViewGroup container, int position) {
				container.addView(viewList.get(position));
				return viewList.get(position);
			}

			@Override
			public CharSequence getPageTitle(int position) {
				return titleList.get(position);
			}
		};
		viewPager.setAdapter(pagerAdapter);
		viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageSelected(int arg0) {
				// 读取缓存
				String str = S.getString(getActivity(), "section" + arg0);
				if (str != "") {
					LinkedList<PostBean> postBeans = PostBean.parseSection(str, getActivity()); // 在这里解析
					listPostAdapter = adapterList.get(String.valueOf(arg0));
					listPostAdapter.appendPost(postBeans);
					listPostAdapter.sortPost();
					listPostAdapter.notifyDataSetChanged();
				}
				manualRefresh();
			}
		});

		titleList.add("文章");
		titleList.add("图集");
		titleList.add("视频");
		titleList.add("专题");

		viewList = new ArrayList<View>();// 将要分页显示的View装入数组中

		for (int i = 0; i < 4; i++) {
			View view = inflater.inflate(R.layout.listview_section, null);
			viewList.add(view);
			registerListener(view, i);
		}
		pagerAdapter.notifyDataSetChanged();

		// 读取缓存
		String str = S.getString(getActivity(), "section0");
		if (str != "") {
			LinkedList<PostBean> postBeans = PostBean.parseSection(str, getActivity()); // 在这里解析
			listPostAdapter = adapterList.get("0");
			listPostAdapter.appendPost(postBeans);
			listPostAdapter.sortPost();
			listPostAdapter.notifyDataSetChanged();
		}

		new Thread(new Runnable() {
			public void run() {
				try {
					Thread.sleep(100);
					mHandler.obtainMessage().sendToTarget();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();

		return view;
	}

	@Override
	public void onDestroyView() {
		// MainActivity.setMenuTouchModeToFullscreen();
		super.onDestroyView();
	}

	@Override
	public void onResume() {
		super.onResume();
		TextView titleTextView = (TextView) getActivity().findViewById(R.id.header_textview);
		titleTextView.setText("分类浏览");
	}

	private MyHandler mHandler = new MyHandler(this);

	public static class MyHandler extends Handler {
		WeakReference<SectionFragment> mFragment;

		public MyHandler(SectionFragment fragment) {
			mFragment = new WeakReference<SectionFragment>(fragment);
		}

		@Override
		public void handleMessage(Message msg) {
			// 设置当前页面
			SectionFragment theFragment = mFragment.get();
			View currentView = theFragment.viewList.get(0);
			ListPostAdapter listPostAdapter = theFragment.adapterList.get("0");
			if (listPostAdapter.getCount() > 0)
				return;
			PullToRefreshListView mListView = (PullToRefreshListView) currentView
					.findViewById(R.id.section_content_list_view);
			mListView.setMode(Mode.PULL_FROM_START);
			mListView.setRefreshing();
			mListView.setMode(Mode.BOTH);
		}
	}

	private void registerListener(View view, int section_id) {
		final String idstr = String.valueOf(section_id);

		listView = (PullToRefreshListView) view.findViewById(R.id.section_content_list_view);
		listView.setMode(Mode.BOTH);
		listView.getRefreshableView().setDivider(null);
		listView.getLoadingLayoutProxy().setRefreshingLabel(
				getResources().getString(R.string.waiting_tips));

		listPostAdapter = new ListPostAdapter(getActivity());
		adapterList.put(idstr, listPostAdapter);

		listView.setAdapter(adapterList.get(idstr));
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			// 点击item跳转到WebView中
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (position >= 1 && listPostAdapter.getCount() >= (position - 1)) {
					PostBean postBean = listPostAdapter.postBeans.get(position - 1);
					Util.handleItemClick(getActivity(), postBean);
				}
			}
		});

		listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				// 显示最近一次刷新的时间
				String label = DateUtils.formatDateTime(getActivity(), System.currentTimeMillis(),
						DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE
								| DateUtils.FORMAT_ABBREV_ALL);
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);

				String targetURL = ApiUrl.BABIETA_BASE_URL + ApiUrl.BABIETA_CONTENT_LIST
						+ "?content_type=" + content_type[Integer.valueOf(idstr)];
				JSONObject jsonRequest = null;
				JsonObjectRequest request = new JsonObjectRequest(targetURL, jsonRequest,
						new Listener<JSONObject>() {
							@Override
							public void onSuccess(JSONObject response) {
								try {
									if (response.has("status") && response.getInt("status") == 0) {
										String jsonString = response.toString();
										// 缓存
										S.put(getActivity(), "section" + idstr, jsonString);
										LinkedList<PostBean> postBeans = PostBean.parseSection(
												jsonString, getActivity()); // 在这里解析
										if (postBeans.size() == 0)
											Util.showToast(getActivity(), "没有新的内容");
										listPostAdapter = adapterList.get(idstr);
										listPostAdapter.clearPost();
										listPostAdapter.appendPost(postBeans);
										listPostAdapter.sortPost();
										listPostAdapter.notifyDataSetChanged();

										View currentView = viewList.get(Integer.valueOf(idstr));
										PullToRefreshListView mListView = (PullToRefreshListView) currentView
												.findViewById(R.id.section_content_list_view);
										mListView.onRefreshComplete();
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
				request.setTag(REQUESTS_TAG + "down" + idstr);
				Netroid.addRequest(request);
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				int size = listPostAdapter.getCount();
				String itemURL = "";
				String targetURL = "";
				if (size == 0) {
					targetURL = ApiUrl.BABIETA_BASE_URL + ApiUrl.BABIETA_CONTENT_LIST
							+ "?content_type=" + content_type[Integer.valueOf(idstr)];
				} else {
					PostBean bean = (PostBean) listPostAdapter.getItem(size - 1);
					int tailID = bean.getId();
					itemURL = ApiUrl.BABIETA_BASE_URL + ApiUrl.BABIETA_CONTENT_LIST;
					targetURL = itemURL + "?max_id=" + (tailID - 1) + "&content_type="
							+ content_type[Integer.valueOf(idstr)];
				}
				JSONObject jsonRequest = null;
				JsonObjectRequest request = new JsonObjectRequest(targetURL, jsonRequest,
						new Listener<JSONObject>() {
							@Override
							public void onSuccess(JSONObject response) {
								try {
									if (response.has("status") && response.getInt("status") == 0) {
										String jsonString = response.toString();
										LinkedList<PostBean> postBeans = PostBean.parseSection(
												jsonString, getActivity()); // 在这里解析
										if (postBeans.size() == 0)
											Util.showToast(getActivity(), "没有新的内容");
										else
											Util.showToast(getActivity(), "加载了" + postBeans.size()
													+ "条内容");
										listPostAdapter = adapterList.get(idstr);
										listPostAdapter.appendPost(postBeans);
										listPostAdapter.sortPost();
										listPostAdapter.notifyDataSetChanged();

										View currentView = viewList.get(Integer.valueOf(idstr));
										PullToRefreshListView mListView = (PullToRefreshListView) currentView
												.findViewById(R.id.section_content_list_view);
										mListView.onRefreshComplete();
									} else {
									}
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}

							@Override
							public void onError(NetroidError error) {
								String data = error.getMessage();
								log.e(data);

								View currentView = viewList.get(Integer.valueOf(idstr));
								PullToRefreshListView mListView = (PullToRefreshListView) currentView
										.findViewById(R.id.section_content_list_view);
								mListView.onRefreshComplete();
								Util.showToast(getActivity(), "网络开小差了,不如再试试吧。");
							}
						});
				// 设置请求标识，这个标识可用于终止该请求时传入的Key
				request.setTag(REQUESTS_TAG + "up" + idstr);
				Netroid.addRequest(request);
			}
		});
	}

	public void manualRefresh() {
		listPostAdapter = adapterList.get(String.valueOf(viewPager.getCurrentItem()));
		if (listPostAdapter.getCount() > 0)
			return;

		View currentView = viewList.get(viewPager.getCurrentItem());
		PullToRefreshListView mListView = (PullToRefreshListView) currentView
				.findViewById(R.id.section_content_list_view);
		mListView.setMode(Mode.PULL_FROM_START);
		mListView.setRefreshing();
		mListView.setMode(Mode.BOTH);
	}

}
