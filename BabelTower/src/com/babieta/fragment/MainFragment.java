package com.babieta.fragment;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.babieta.R;
import com.babieta.activity.AlbumWebViewActivity;
import com.babieta.activity.SpecialActivity;
import com.babieta.activity.VideoActivity;
import com.babieta.activity.WebViewActivity;
import com.babieta.adapter.ListPostAdapter;
import com.babieta.adapter.PageCarouselAdapter;
import com.babieta.base.ApiData;
import com.babieta.base.ApiUrl;
import com.babieta.base.S;
import com.babieta.bean.PostBean;
import com.babieta.layout.CarouselViewPager;
import com.babieta.layout.IndicatorLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateUtils;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class MainFragment extends Fragment {
	private View view;
	private View pageView;
	private CarouselViewPager viewPager;
	private IndicatorLayout indicatorLayout;
	private PageCarouselAdapter pageCarouselAdapter;

	private PullToRefreshListView listView;
	private ListPostAdapter listPostAdapter;
	private LinkedList<PostBean> postBeans;

	private int currentViewPagerItem; // ��ǰҳ��
	private ScheduledExecutorService scheduledExecutorService;
	private MyHandler mHandler = new MyHandler(this);

	private String targetURL;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initOnCreate();
		loadTimeline();
	}

	// Fragment�ڲ��ɼ���ʱ����������View�������ڳ�ջ��ʱ����һ��Fragment��Ҫ���³�ʼ��View
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		this.view = inflater.inflate(R.layout.listview_main, container, false);
		this.initPostListView(); // timeline����
		this.initPageView(); // focus����

		listPostAdapter.notifyDataSetChanged();
		TextView textView = (TextView) pageView.findViewById(R.id.vp_main_text);
		if (listPostAdapter.getCount() == 0) {
			textView.setText("��û�����ݣ�����ˢ�¿���~");
		} else {
			textView.setText("�����б�");
		}

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		// ���ñ���
		TextView titleTextView = (TextView) getActivity().findViewById(R.id.header_textview);
		titleTextView.setText("��ҳ");
		// ÿ��5�����л�һ��ͼƬ
		scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
		scheduledExecutorService
				.scheduleWithFixedDelay(new ViewPagerTask(), 5, 5, TimeUnit.SECONDS);
	}

	@Override
	public void onPause() {
		super.onPause();
		scheduledExecutorService.shutdown();
	}

	// ������ҳlistAdapter
	// (fragment��������:http://developer.android.com/guide/components/fragments.html)
	public void initOnCreate() {
		listPostAdapter = new ListPostAdapter(getActivity());
	}

	@SuppressLint("InflateParams")
	private void initPageView() {
		pageView = LayoutInflater.from(getActivity()).inflate(R.layout.viewpager_main, null);
		viewPager = (CarouselViewPager) pageView.findViewById(R.id.vp_main);
		pageCarouselAdapter = new PageCarouselAdapter(getActivity());
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
			// ���½���
			mHandler.obtainMessage().sendToTarget();
		}
	}

	public static class MyHandler extends Handler {
		WeakReference<MainFragment> mFragment;

		public MyHandler(MainFragment fragment) {
			mFragment = new WeakReference<MainFragment>(fragment);
		}

		@Override
		public void handleMessage(Message msg) {
			// ���õ�ǰҳ��
			MainFragment theFragment = mFragment.get();
			theFragment.viewPager.setCurrentItem(theFragment.currentViewPagerItem);
		}
	}

	private void initPostListView() {
		listView = (PullToRefreshListView) view.findViewById(R.id.listview_main);
		listView.getRefreshableView().setDivider(null); // �ָ���
		listView.getRefreshableView().setVerticalScrollBarEnabled(false); // ��ֱ���������
		listView.setMode(Mode.BOTH); // ����������ģʽ
		listView.setAdapter(listPostAdapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			// ���item��ת��WebView��
			public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {

				// ȡ��item��URL , pos��ʼλ��2??
				postBeans = listPostAdapter.postBeans;
				String itemContentType = postBeans.get(pos - 2).getContentType();
				final int pos_final = pos;

				if (itemContentType.equals("article")) {
					Toast.makeText(getActivity(), "��������", Toast.LENGTH_SHORT).show();

					Intent intent = new Intent(getActivity(), WebViewActivity.class);
					Bundle bundle = new Bundle();
					bundle.putInt("id", postBeans.get(pos - 2).getId());
					bundle.putCharSequence("content_type", postBeans.get(pos - 2).getContentType());
					bundle.putCharSequence("itemURL", postBeans.get(pos - 2).getItemURL());
					bundle.putCharSequence("title", postBeans.get(pos - 2).getTitle());
					bundle.putCharSequence("description", postBeans.get(pos - 2).getDescription());
					bundle.putCharSequence("ImageURL", postBeans.get(pos - 2).getImageUrl());
					bundle.putCharSequence("author", postBeans.get(pos - 2).getAuthor());
					bundle.putCharSequence("created_at", postBeans.get(pos - 2).getCreatedAt());
					bundle.putCharSequence("updated_at", postBeans.get(pos - 2).getUpdatedAt());
					intent.putExtras(bundle);
					startActivity(intent);
					getActivity().overridePendingTransition(R.anim.base_slide_right_in,
							R.anim.base_slide_remain);
				} else if (itemContentType.equals("album")) {
					Toast.makeText(getActivity(), "�������", Toast.LENGTH_SHORT).show();

					Intent intent = new Intent(getActivity(), AlbumWebViewActivity.class);
					Bundle bundle = new Bundle();
					bundle.putInt("id", postBeans.get(pos - 2).getId());
					bundle.putCharSequence("content_type", postBeans.get(pos - 2).getContentType());
					bundle.putCharSequence("itemURL", postBeans.get(pos - 2).getItemURL());
					bundle.putCharSequence("title", postBeans.get(pos - 2).getTitle());
					bundle.putCharSequence("description", postBeans.get(pos - 2).getDescription());
					bundle.putCharSequence("ImageURL", postBeans.get(pos - 2).getHeaderImageUrl());
					bundle.putCharSequence("author", postBeans.get(pos - 2).getAuthor());
					bundle.putCharSequence("created_at", postBeans.get(pos - 2).getCreatedAt());
					bundle.putCharSequence("updated_at", postBeans.get(pos - 2).getUpdatedAt());
					intent.putExtras(bundle);
					startActivity(intent);
					getActivity().overridePendingTransition(R.anim.base_slide_right_in,
							R.anim.base_slide_remain);
				} else if (itemContentType.equals("video")) {
					ContextThemeWrapper themedContext = new ContextThemeWrapper(getActivity(),
							android.R.style.Theme_Holo_Light_Dialog_NoActionBar);
					AlertDialog.Builder builder = new AlertDialog.Builder(themedContext);
					AlertDialog alertDialog = builder.create();
					alertDialog.setMessage("����һ����Ƶ,������wifi�����¿���Ŷ");
					alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "�õ���", // ������İ�׿�趨,ȷ�������ұ�
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface arg0, int arg1) {
									Intent intent = new Intent(getActivity(), VideoActivity.class);
									Bundle bundle = new Bundle();
									bundle.putInt("id", postBeans.get(pos_final - 2).getId());
									bundle.putCharSequence("content_type",
											postBeans.get(pos_final - 2).getContentType());
									bundle.putCharSequence("itemURL", postBeans.get(pos_final - 2)
											.getItemURL());
									bundle.putCharSequence("title", postBeans.get(pos_final - 2)
											.getTitle());
									bundle.putCharSequence("description",
											postBeans.get(pos_final - 2).getDescription());
									bundle.putCharSequence("ImageURL", postBeans.get(pos_final - 2)
											.getImageUrl());
									bundle.putCharSequence("author", postBeans.get(pos_final - 2)
											.getAuthor());
									bundle.putCharSequence("created_at",
											postBeans.get(pos_final - 2).getCreatedAt());
									bundle.putCharSequence("updated_at",
											postBeans.get(pos_final - 2).getUpdatedAt());
									intent.putExtras(bundle);
									startActivity(intent);
									getActivity().overridePendingTransition(
											R.anim.base_slide_right_in, R.anim.base_slide_remain);
								}
							});
					alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "�ٵȵ�",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface arg0, int arg1) {
									return;
								}
							});
					alertDialog.show();

				} else if (itemContentType.equals("special")) {
					Toast.makeText(getActivity(), "ר������", Toast.LENGTH_SHORT).show();

					Intent intent = new Intent(getActivity(), SpecialActivity.class);
					Bundle bundle = new Bundle();
					bundle.putInt("id", postBeans.get(pos - 2).getId());
					bundle.putCharSequence("content_type", postBeans.get(pos - 2).getContentType());
					bundle.putCharSequence("itemURL", postBeans.get(pos - 2).getItemURL());
					bundle.putCharSequence("title", postBeans.get(pos - 2).getTitle());
					bundle.putCharSequence("description", postBeans.get(pos - 2).getDescription());
					bundle.putCharSequence("ImageURL", postBeans.get(pos - 2).getHeaderImageUrl());
					bundle.putCharSequence("author", postBeans.get(pos - 2).getAuthor());
					bundle.putCharSequence("created_at", postBeans.get(pos - 2).getCreatedAt());
					bundle.putCharSequence("updated_at", postBeans.get(pos - 2).getUpdatedAt());
					intent.putExtras(bundle);
					startActivity(intent);
					getActivity().overridePendingTransition(R.anim.base_slide_right_in,
							R.anim.base_slide_remain);
				} else {
					Toast.makeText(getActivity(), "δ֪����", Toast.LENGTH_SHORT).show();
				}
			}
		});

		listView.setOnRefreshListener(new OnRefreshListener2<ListView>() {
			// ��������ˢ��
			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
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
			}

			// �ײ�����ˢ��
			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				int count = listPostAdapter.getCount();

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
					targetURL = ApiUrl.BABIETA_BASE_URL + ApiUrl.BABIETA_CONTENT_LIST + "?max_id="
							+ (minCnt - 1);
				}

				new AsyncTask<Void, Void, String>() {

					@Override
					protected String doInBackground(Void... arg0) {
						if (isNetworkConnected(getActivity())) {
							return ApiData.httpGet(targetURL);
						} else {
							return "";
						}
					}

					protected void onPostExecute(String result) {
						handlePostExecute(result, 1); // ����ˢ�¾Ͳ��洢�����ݵ�TimeLine��
					};
				}.execute();
			}
		});
	}

	private void handlePostExecute(String result, int type) {
		if (!isNetworkConnected(getActivity())) {
			Toast.makeText(getActivity(), "û�п�������,���Ժ�����!", Toast.LENGTH_SHORT).show();
			listView.onRefreshComplete();
		} else if (result == "") {
			Toast.makeText(getActivity(), "����ʧ��,���Ժ�����!", Toast.LENGTH_SHORT).show();
			listView.onRefreshComplete();
		} else {
			int originalSize = listPostAdapter.getCount();
			LinkedList<PostBean> postBeans = PostBean.parseBabietaTimeline(result, getActivity());
			listPostAdapter.appendPost(postBeans);
			listPostAdapter.sortPost();
			if (type == 0) {
				// ����ˢ�±��ش洢TimeLine����,�����¿���һ���߳�������,���ٿ���
				new Thread(new Runnable() {
					public void run() {
						saveTimeline();
					}
				}).start();
			}
			listPostAdapter.notifyDataSetChanged();
			int currentSize = listPostAdapter.getCount();
			TextView textView = (TextView) pageView.findViewById(R.id.vp_main_text);
			if (listPostAdapter.getCount() == 0) {
				textView.setText("��û�����ݣ�����ˢ�¿���~");
			} else {
				textView.setText("�����б�");
			}
			Toast toast;
			if (currentSize - originalSize > 0) {
				toast = Toast.makeText(getActivity(), "������" + (currentSize - originalSize) + "������",
						Toast.LENGTH_SHORT);
			} else {
				toast = Toast.makeText(getActivity(), "û���µ�����", Toast.LENGTH_SHORT);
			}
			toast.setGravity(Gravity.TOP, 0, 150);
			toast.show();
			listView.onRefreshComplete();
		}
	}

	// �洢TimeLine��SharedPreferences��
	private void saveTimeline() {
		S.put(getActivity(), "timeline_list", "");

		int count = listPostAdapter.getCount() > 10 ? 10 : listPostAdapter.getCount(); // ֻ����ǰ10��

		for (int i = 0; i < count; i++) {
			PostBean bean = (PostBean) listPostAdapter.getItem(i);

			S.addStringSet(getActivity(), "timeline_list", String.valueOf(bean.getId())); // 1id
			S.addStringSet(getActivity(), "timeline_list", bean.getItemURL()); // 2����
			S.addStringSet(getActivity(), "timeline_list", bean.getTitle()); // 3����
			S.addStringSet(getActivity(), "timeline_list", bean.getAuthor()); // 4����
			S.addStringSet(getActivity(), "timeline_list", bean.getCreatedAt()); // 5ʱ��
			S.addStringSet(getActivity(), "timeline_list", bean.getImageUrl()); // 6ͼƬ��ַ
			S.addStringSet(getActivity(), "timeline_list", bean.getHeaderImageUrl()); // 7ͷͼURL
			S.addStringSet(getActivity(), "timeline_list", bean.getDescription()); // 8ע��
			S.addStringSet(getActivity(), "timeline_list", bean.getContentType()); // 9����
		}
	}

	private void loadTimeline() {
		LinkedList<PostBean> postBeans = new LinkedList<PostBean>();
		String[] collectlist = S.getStringSet(getActivity(), "timeline_list");

		for (int i = 1; i < (collectlist.length); i++) {
			System.out.println(collectlist[i]);
		}

		for (int i = 1; i < (collectlist.length);) {
			PostBean postBean = new PostBean();

			try {
				Integer.valueOf(collectlist[i]);
			} catch (Exception e) {
				e.printStackTrace();
				break;
			}

			postBean.setId(Integer.valueOf(collectlist[i]));
			postBean.setItemURL(collectlist[i + 1]);
			postBean.setTitle(collectlist[i + 2]);
			postBean.setAuthor(collectlist[i + 3]);
			postBean.setUpdatedAt(collectlist[i + 4]);
			postBean.setCreatedAt(collectlist[i + 4]); // (ע�����ʱ��ͬupdated_at)
			postBean.setImageUrl(collectlist[i + 5]);
			postBean.setHeaderImageUrl(collectlist[i + 6]);
			postBean.setDescription(collectlist[i + 7]);
			postBean.setContentType(collectlist[i + 8]);

			postBeans.add(postBean); // ��ӵ�����
			i = i + 9;
		}

		listPostAdapter.appendPost(postBeans);
		listPostAdapter.sortPost();
	}

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

}
