package com.babieta.adapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;

import com.avos.avoscloud.LogUtil.log;
import com.babieta.R;
import com.babieta.base.AsyncImageLoader;
import com.babieta.base.Util;
import com.babieta.bean.PostBean;

import android.R.integer;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ListPostAdapter extends BaseAdapter {
	public LinkedList<PostBean> postBeans;
	Context context;
	ImageView imageView;
	TextView titleTextView;
	TextView publisherTextView;
	TextView timeTextView;

	public ListPostAdapter(Context context) {
		this.context = context;
		postBeans = new LinkedList<PostBean>();
	}

	public ListPostAdapter(Context context, LinkedList<PostBean> postBeans) {
		this.postBeans = postBeans;
		this.context = context;
	}

	@Override
	public int getCount() {
		return postBeans.size();
	}

	@Override
	public Object getItem(int index) {
		return postBeans.get(index);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup arg2) {
		PostBean postBean = postBeans.get(position);
		// TODO view == null
		String imageURL;
		if (postBean.getContentType().equals("album")) {
			view = LayoutInflater.from(context).inflate(R.layout.listview_main_item_album, null);
			imageURL = postBean.getHeaderImageUrl();
		} else {
			view = LayoutInflater.from(context).inflate(R.layout.listview_main_item, null);
			imageURL = postBean.getImageUrl();
		}
		final String final_imageURL = imageURL;

		titleTextView = (TextView) view.findViewById(R.id.post_list_text);
		publisherTextView = (TextView) view.findViewById(R.id.post_list_publisher);
		timeTextView = (TextView) view.findViewById(R.id.post_list_updated_at);
		imageView = (ImageView) view.findViewById(R.id.post_list_image);

		titleTextView.setText(postBean.getText()); // TextView换行?Util.getText
		publisherTextView.setText(Util.getText(postBean.getAuthor()));

		String pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"; // 设置时间TextView
		Locale locale = new Locale("zh", "CN");
		SimpleDateFormat format = new SimpleDateFormat(pattern, locale);
		String dateStr = postBean.getUpdatedAt();
		try {
			Date date = format.parse(dateStr);
			SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", locale);
			timeTextView.setText(f.format(date));
		} catch (ParseException e) {
			e.printStackTrace();
		}

		if (final_imageURL != null) {
			AsyncImageLoader loader = new AsyncImageLoader(context);
			// 将图片缓存至外部文件中
			loader.setCache2File(true); // false
			// 设置外部缓存文件夹
			loader.setCachedDir(context.getCacheDir().getAbsolutePath());
			// 设置外部缓存文件夹
			loader.downloadImage(final_imageURL, true/* false */,
					new AsyncImageLoader.ImageCallback() {
						@Override
						public void onImageLoaded(Bitmap bitmap, String imageUrl) {
							if (bitmap != null) {
								imageView.setImageBitmap(bitmap);
							} else {
								// 下载失败，设置默认图片
							}
						}
					});
		} else {
			imageView.setImageResource(postBean.getImage());
		}

		view.setTag(position);
		return view;
	}

	// 添加到链表头部
	public void appendPost(LinkedList<PostBean> newPostBeans) {
		for (int i = 0; i < newPostBeans.size(); i++) {
			PostBean bean = newPostBeans.get(i);
			int itemId = bean.getId();
			if (hasItem(itemId)) {
				break;
			} else {
				postBeans.addFirst(bean);
			}
		}
	}

	public void appendCollection(LinkedList<PostBean> newPostBeans) {
		for (int i = newPostBeans.size() - 1; i >= 0; i--) {
			postBeans.addFirst(newPostBeans.get(i));
		}
	}

	private boolean hasItem(int itemId) {
		for (int i = 0; i < postBeans.size(); i++) {
			int id = postBeans.get(i).getId();
			if (itemId == id)
				return true;
		}
		return false;
	}

	public void clearPost() {
		postBeans.clear();
	}

	public void sortPost() {
		Collections.sort(postBeans, new PostBeanComparator());
	}

	public class PostBeanComparator implements Comparator<PostBean> {
		@Override
		public int compare(PostBean o1, PostBean o2) {
			return (o2.getId() - o1.getId());
		}
	}
}
