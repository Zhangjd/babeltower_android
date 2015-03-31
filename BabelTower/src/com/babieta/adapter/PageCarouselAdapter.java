package com.babieta.adapter;

import java.util.ArrayList;
import java.util.LinkedList;

import com.babieta.R;
import com.babieta.base.AsyncImageLoader;

import android.R.integer;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class PageCarouselAdapter extends PageBaseAdapter {
	Context context;
	ArrayList<ImageView> imageViews = new ArrayList<ImageView>();

	public PageCarouselAdapter(Context context) {
		views = new LinkedList<View>();

		String[] imageUrls = {
				"http://218.192.166.167:3030/system/contents/header_images/000/000/025/medium/gyarados_by_krukmeister-d6ikwig.png?1426182790",
				"http://218.192.166.167:3030/system/contents/header_images/000/000/023/medium/minimal_wind_waker_wallpaper__with_shadow__by_cheetashock-d7injfy.png?1426182497",
				"http://218.192.166.167:3030/system/contents/header_images/000/000/019/medium/_request__love_live____yazawa_nico_by_krukmeister-d84adnf.png?1426090886",
				"http://218.192.166.167:3030/system/contents/header_images/000/000/015/medium/Cogumelo3_.png?1426076914" };

		for (int j = 0; j < imageUrls.length; j++) {
			View view = LayoutInflater.from(context).inflate(R.layout.viewpage, null);

			imageViews.add((ImageView) view.findViewById(R.id.view_page_image));

			final int temp = j;

			AsyncImageLoader loader = new AsyncImageLoader(context);
			// 将图片缓存至外部文件中
			loader.setCache2File(true); // false
			// 设置外部缓存文件夹
			loader.setCachedDir(context.getCacheDir().getAbsolutePath());
			// 设置外部缓存文件夹
			loader.downloadImage(imageUrls[j], true, new AsyncImageLoader.ImageCallback() {
				@Override
				public void onImageLoaded(Bitmap bitmap, String imageUrl) {
					if (bitmap != null) {
						imageViews.get(temp).setImageBitmap(bitmap);
					} else {
						// 下载失败，设置默认图片
					}
				}
			});

			TextView textView = (TextView) view.findViewById(R.id.view_page_title);
			textView.setText("测试标题 " + j);

			view.setTag(Integer.toString(j));
			views.add(view);
		}
		this.context = context;
	}

}
