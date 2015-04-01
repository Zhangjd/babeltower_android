package com.babieta.adapter;

import com.babieta.R;
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
	private String[] imageUrls = {
			"http://218.192.166.167:3030/system/contents/header_images/000/000/025/medium/gyarados_by_krukmeister-d6ikwig.png?1426182790",
			"http://218.192.166.167:3030/system/contents/header_images/000/000/023/medium/minimal_wind_waker_wallpaper__with_shadow__by_cheetashock-d7injfy.png?1426182497",
			"http://218.192.166.167:3030/system/contents/header_images/000/000/019/medium/_request__love_live____yazawa_nico_by_krukmeister-d84adnf.png?1426090886",
			"http://218.192.166.167:3030/system/contents/header_images/000/000/015/medium/Cogumelo3_.png?1426076914" };

	public PageCarouselAdapter(Context context) {
		this.context = context;
		this.options = new DisplayImageOptions.Builder().showImageForEmptyUri(R.drawable.ic_empty)
				.showImageOnFail(R.drawable.ic_error).resetViewBeforeLoading(true)
				.cacheOnDisk(true).imageScaleType(ImageScaleType.EXACTLY)
				.bitmapConfig(Bitmap.Config.RGB_565).considerExifParams(true)
				.displayer(new FadeInBitmapDisplayer(300)).build();
	}

	// 返回要滑动的View的个数
	@Override
	public int getCount() {
		return imageUrls.length;
	}

	// 做了两件事，第一：将当前视图添加到container中，第二：返回当前View
	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		final View view = LayoutInflater.from(context).inflate(R.layout.viewpage, container, false);
		final ImageView imageView = (ImageView) view.findViewById(R.id.view_page_image);
	
		ImageLoader.getInstance().displayImage(imageUrls[position], imageView, options,
				new SimpleImageLoadingListener() {
					@Override
					public void onLoadingStarted(String imageUri, View view) {
						imageView.setBackgroundColor(Color.rgb(240, 240, 240));
					}
				});

		TextView textView = (TextView) view.findViewById(R.id.view_page_title);
		textView.setText("测试标题 " + position);

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
