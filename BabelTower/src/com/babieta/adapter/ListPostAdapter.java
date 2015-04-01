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
import com.babieta.base.Util;
import com.babieta.bean.PostBean;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.SparseArray;
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

	// 在getView方法中重用convertView会引起滚动时图片排序的问题
	private SparseArray<View> viewMap = new SparseArray<View>();

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View rowView = this.viewMap.get(position);

		if (rowView == null) {
			PostBean postBean = postBeans.get(position);
			String imageURL;
			if (postBean.getContentType().equals("album")) {
				rowView = LayoutInflater.from(context).inflate(R.layout.listview_main_item_album,
						parent, false);
				imageURL = postBean.getHeaderImageUrl();
			} else {
				rowView = LayoutInflater.from(context).inflate(R.layout.listview_main_item, parent,
						false);
				imageURL = postBean.getImageUrl();
			}
			final String final_imageURL = imageURL;

			titleTextView = (TextView) rowView.findViewById(R.id.post_list_text);
			publisherTextView = (TextView) rowView.findViewById(R.id.post_list_publisher);
			timeTextView = (TextView) rowView.findViewById(R.id.post_list_updated_at);
			imageView = (ImageView) rowView.findViewById(R.id.post_list_image);

			// 给 ImageView 设置一个 tag (作用见下面)
			imageView.setTag(final_imageURL);

			titleTextView.setText(postBean.getTitle()); // TextView换行?Util.getText
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
				ImageLoader.getInstance().displayImage(final_imageURL, imageView, options,
						new SimpleImageLoadingListener() {
							@Override
							public void onLoadingStarted(String imageUri, View view) {
								imageView.setBackgroundColor(Color.rgb(240, 240, 240));
							}
						});
			} else {
				log.w("null imgURL");
			}
			viewMap.put(position, rowView);
		} else {

		}
		rowView.setTag(position);
		return rowView;
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
