package com.bbt.babeltower.adapter;

import java.util.LinkedList;

import com.bbt.babeltower.R;
import com.bbt.babeltower.bean.PostBean;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class PageCarouselAdapter extends PagerAdapter {
	private Context context;
	private DisplayImageOptions options;
	private LinkedList<PostBean> postBeans = new LinkedList<PostBean>();

	public PageCarouselAdapter(Context context) {
		this.context = context;
		this.options = new DisplayImageOptions.Builder().showImageForEmptyUri(R.drawable.ic_empty)
				.showImageOnFail(R.drawable.ic_error).resetViewBeforeLoading(true)
				.cacheOnDisk(true).imageScaleType(ImageScaleType.EXACTLY)
				.bitmapConfig(Bitmap.Config.RGB_565).considerExifParams(true)
				.displayer(new FadeInBitmapDisplayer(300)).build();
	}

	public void setPostBeans(LinkedList<PostBean> postBeans) {
		this.postBeans = postBeans;
	}

	// 返回要滑动的View的个数
	@Override
	public int getCount() {
		return postBeans.size();
	}

	// 做了两件事，第一：将当前视图添加到container中，第二：返回当前View
	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		final View view = LayoutInflater.from(context).inflate(R.layout.index_focus, container, false);
		final ImageView imageView = (ImageView) view.findViewById(R.id.view_page_image);

		ImageLoader.getInstance().displayImage(postBeans.get(position).getHeaderImageUrl(),
				imageView, options, new SimpleImageLoadingListener() {
					@Override
					public void onLoadingStarted(String imageUri, View view) {
						imageView.setBackgroundColor(Color.rgb(240, 240, 240));
					}
				});

		TextView textView = (TextView) view.findViewById(R.id.view_page_title);
		textView.setText(postBeans.get(position).getTitle());
		
		container.addView(view);
		return view;
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	// 从当前container中删除指定位置（position）的View;
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View) object);
	}

}
