package com.bbt.babeltower.adapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;

import com.avos.avoscloud.LogUtil.log;
import com.bbt.babeltower.R;
import com.bbt.babeltower.base.Util;
import com.bbt.babeltower.bean.PostBean;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ListPostAdapter extends BaseAdapter {
	public LinkedList<PostBean> postBeans;
	private Context context;
	private ImageView imageView;
	private TextView titleTextView;
	private TextView publisherTextView;
	private TextView timeTextView;
	private DisplayImageOptions options;

	public ListPostAdapter(Context context) {
		this.context = context;
		this.options = new DisplayImageOptions.Builder().showImageForEmptyUri(R.drawable.ic_empty)
				.showImageOnFail(R.drawable.ic_error).resetViewBeforeLoading(true)
				.cacheOnDisk(true).imageScaleType(ImageScaleType.EXACTLY)
				.bitmapConfig(Bitmap.Config.RGB_565).considerExifParams(true)
				.displayer(new FadeInBitmapDisplayer(300)).build();
		this.postBeans = new LinkedList<PostBean>();
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
	public View getView(int position, View convertView, ViewGroup parent) {
		PostBean postBean = postBeans.get(position);
		String imageURL;
		if (postBean.getContentType().equals("album")) {
			convertView = LayoutInflater.from(context).inflate(R.layout.listview_main_big_cell,
					parent, false);
			imageURL = postBean.getHeaderImageUrl();
		} else {
			convertView = LayoutInflater.from(context).inflate(R.layout.listview_main_small_cell, parent,
					false);
			imageURL = postBean.getImageUrl();
		}

		titleTextView = (TextView) convertView.findViewById(R.id.post_list_text);
		publisherTextView = (TextView) convertView.findViewById(R.id.post_list_publisher);
		timeTextView = (TextView) convertView.findViewById(R.id.post_list_updated_at);
		imageView = (ImageView) convertView.findViewById(R.id.post_list_image);

		// �? ImageView 设置�?�? tag (作用见下�?)
		imageView.setTag(imageURL);

		titleTextView.setText(postBean.getTitle()); // TextView换行?Util.getText
		publisherTextView.setText(Util.getText(postBean.getAuthor()));

		String pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"; // 设置时间TextView
		Locale locale = new Locale("zh", "CN");
		SimpleDateFormat format = new SimpleDateFormat(pattern, locale);
		String dateStr = postBean.getCreatedAt();
		try {
			Date date = format.parse(dateStr);
			SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", locale);
			timeTextView.setText(f.format(date));
		} catch (ParseException e) {
			e.printStackTrace();
		}

		// ListView 异步加载图片之所以错位的根本原因是重用了 convertView 且有异步操作
		// �?�?单的解决方法就是网上说的，给 ImageView 设置�?�? tag, 并预设一个图�?
		if (imageURL != null && imageView.getTag() != null && imageView.getTag().equals(imageURL)) {
			ImageLoader.getInstance().displayImage(imageURL, imageView, options,
					new SimpleImageLoadingListener() {
						@Override
						public void onLoadingStarted(String imageUri, View view) {
							imageView.setBackgroundColor(Color.rgb(240, 240, 240));
						}
					});
		} else {
			log.w("null imgURL or tag not match imgView");
		}

		convertView.setTag(position);
		return convertView;
	}

	// 添加到链表头�?
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
