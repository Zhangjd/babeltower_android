package com.babieta.view;

import java.util.LinkedList;

import com.babieta.R;
import com.babieta.activity.AlbumWebViewActivity;
import com.babieta.activity.WebViewActivity;
import com.babieta.adapter.ListPostAdapter;
import com.babieta.bean.PostBean;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class CollectFragment extends Fragment {
	private View view;
	private PullToRefreshListView listView;
	private ListPostAdapter listPostAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_collect, container, false);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initPostListView();
	}

	@Override
	public void onResume() {
		super.onResume();

		TextView titleTextView = (TextView) getActivity().findViewById(R.id.header_textview);
		titleTextView.setText("我的收藏");

		listPostAdapter.clearPost();
		LinkedList<PostBean> postBeans = PostBean.parseCollectContents(getActivity()); // 在这里解析
		listPostAdapter.appendCollection(postBeans);
		listPostAdapter.sortPost();
		listPostAdapter.notifyDataSetChanged();

		TextView textView = (TextView) getActivity().findViewById(R.id.collect_fragment_text);
		if (listPostAdapter.getCount() > 0) {
			textView.setText("收藏列表");
		} else {
			textView.setText("收藏夹为空,去主页看一看吧~");
			textView.setGravity(Gravity.CENTER);
			textView.setTextColor(Color.rgb(96, 96, 96));
		}
	}

	public void initPostListView() {
		listView = (PullToRefreshListView) view.findViewById(R.id.collect_list_view);
		listView.getRefreshableView().setDivider(null);
		listView.getRefreshableView().setVerticalScrollBarEnabled(false);
		listView.setMode(Mode.DISABLED); // 收藏夹关闭上下拉刷新
		listPostAdapter = new ListPostAdapter(getActivity());
		listView.setAdapter(listPostAdapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			// 点击item跳转到WebView中
			public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
				// 取出item的URL , position起始位置1 (前面有个TextView)
				LinkedList<PostBean> postBeans = listPostAdapter.postBeans;
				String itemContentType = postBeans.get(pos - 1).getContentType();

				if (itemContentType.equals("article")) {
					Toast.makeText(getActivity(), "文章类型", Toast.LENGTH_SHORT).show();

					Intent intent = new Intent(getActivity(), WebViewActivity.class);
					Bundle bundle = new Bundle();
					bundle.putInt("id", postBeans.get(pos - 1).getId());
					bundle.putCharSequence("content_type", postBeans.get(pos - 1).getContentType());
					bundle.putCharSequence("itemURL", postBeans.get(pos - 1).getItemURL());
					bundle.putCharSequence("title", postBeans.get(pos - 1).getTitle());
					bundle.putCharSequence("description", postBeans.get(pos - 1).getDescription());
					bundle.putCharSequence("ImageURL", postBeans.get(pos - 1).getHeaderImageUrl());
					bundle.putCharSequence("author", postBeans.get(pos - 1).getAuthor());
					bundle.putCharSequence("created_at", postBeans.get(pos - 1).getCreatedAt());
					bundle.putCharSequence("updated_at", postBeans.get(pos - 1).getUpdatedAt());
					intent.putExtras(bundle);
					startActivity(intent);
					getActivity().overridePendingTransition(R.anim.base_slide_right_in,
							R.anim.base_slide_remain);
				} else if (itemContentType.equals("album")) {
					Toast.makeText(getActivity(), "相册类型", Toast.LENGTH_SHORT).show();

					Intent intent = new Intent(getActivity(), AlbumWebViewActivity.class);
					Bundle bundle = new Bundle();
					bundle.putInt("id", postBeans.get(pos - 1).getId());
					bundle.putCharSequence("content_type", postBeans.get(pos - 1).getContentType());
					bundle.putCharSequence("itemURL", postBeans.get(pos - 1).getItemURL());
					bundle.putCharSequence("title", postBeans.get(pos - 1).getTitle());
					bundle.putCharSequence("description", postBeans.get(pos - 1).getDescription());
					bundle.putCharSequence("ImageURL", postBeans.get(pos - 1).getHeaderImageUrl());
					bundle.putCharSequence("author", postBeans.get(pos - 1).getAuthor());
					bundle.putCharSequence("created_at", postBeans.get(pos - 1).getCreatedAt());
					bundle.putCharSequence("updated_at", postBeans.get(pos - 1).getUpdatedAt());
					intent.putExtras(bundle);
					startActivity(intent);
					getActivity().overridePendingTransition(R.anim.base_slide_right_in,
							R.anim.base_slide_remain);
				} else {

				}
			}
		});
	}
}
