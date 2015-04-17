package com.bbt.babeltower.fragment;

import java.util.LinkedList;

import com.bbt.babeltower.R;
import com.bbt.babeltower.adapter.ListPostAdapter;
import com.bbt.babeltower.base.Util;
import com.bbt.babeltower.bean.PostBean;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
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
		titleTextView.setText("�ҵ��ղ�");

		listPostAdapter.clearPost();
		LinkedList<PostBean> postBeans = PostBean.parseCollectContents(getActivity()); // ���������
		listPostAdapter.appendCollection(postBeans);
		listPostAdapter.sortPost();
		listPostAdapter.notifyDataSetChanged();

		TextView textView = (TextView) getActivity().findViewById(R.id.collect_fragment_text);
		if (listPostAdapter.getCount() > 0) {
			textView.setText("�ղ��б�");
			textView.setVisibility(View.GONE);
		} else {
			textView.setText("�ղؼ�Ϊ��,ȥ��ҳ��һ����~");
			textView.setGravity(Gravity.CENTER);
			textView.setTextColor(Color.rgb(96, 96, 96));
		}
	}

	public void initPostListView() {
		listView = (PullToRefreshListView) view.findViewById(R.id.collect_list_view);
		listView.getRefreshableView().setDivider(null);
		listView.getRefreshableView().setVerticalScrollBarEnabled(false);
		listView.setMode(Mode.DISABLED); // �ղؼйر�������ˢ��
		listPostAdapter = new ListPostAdapter(getActivity());
		listView.setAdapter(listPostAdapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			// ���item��ת��WebView��
			public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
				PostBean postBean = listPostAdapter.postBeans.get(pos - 1);
				Activity activity = getActivity();
				Util.handleItemClick(activity, postBean);
			}
		});
	}

}
