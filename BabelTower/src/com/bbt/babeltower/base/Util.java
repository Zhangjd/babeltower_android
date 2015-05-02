package com.bbt.babeltower.base;

import java.io.File;
import java.lang.reflect.Field;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.bbt.babeltower.R;
import com.bbt.babeltower.activity.AlbumWebViewActivity;
import com.bbt.babeltower.activity.SpecialActivity;
import com.bbt.babeltower.activity.WebViewActivity;
import com.bbt.babeltower.bean.PostBean;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;

public class Util {
	public static String getText(String text) {
		int length = text.length();
		if (length < 13) {
			return text;
		} else if (length > 24) {
			text = text.substring(0, 24);
		}
		String textHeadString = text.substring(0, 13);
		text = text.replace(textHeadString, textHeadString + "\n");
		return text;
	}

	public static SlidingMenu initSlidingMenu(Activity context) {
		@SuppressWarnings("deprecation")
		int screenWidth = context.getWindowManager().getDefaultDisplay().getWidth();
		int menuWidth = (int) (screenWidth * 0.667);
		
		SlidingMenu slidingMenu = new SlidingMenu(context);
		slidingMenu.setMode(SlidingMenu.LEFT); // ���ò˵�ģʽ
		slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN); // ���ò໬���Ĵ���ģʽ(3��)
		slidingMenu.setTouchModeBehind(SlidingMenu.TOUCHMODE_FULLSCREEN); // SlideMenu������bug������https://github.com/jfeinstein10/SlidingMenu/issues/446�������޸�
		slidingMenu.attachToActivity(context, SlidingMenu.SLIDING_CONTENT); // ��SlidingMenu���ӵ�Activity
		slidingMenu.setMenu(R.layout.slidemenu_left); // ���ò˵�layout
		slidingMenu.setShadowDrawable(R.drawable.shadow); // ������Ӱ
		slidingMenu.setShadowWidth(1); // ������Ӱ���
		slidingMenu.setFadeEnabled(true);
		slidingMenu.setFadeDegree(0.8f); // ���ò˵��ջ���ʱ��Ľ����
		slidingMenu.setBehindOffset(screenWidth-menuWidth); // ���ò໬����ȫչ��֮�󣬾�������һ�ߵľ���(pixels)

		// ���Ͻǵ��Ӳ�Ч��
		// final ImageButton switchButton = (ImageButton)
		// context.findViewById(R.id.header_but);
		// final LayoutParams params = (LayoutParams)
		// switchButton.getLayoutParams();
		// final Context ct = context;
		// CanvasTransformer mTransformer = new CanvasTransformer() {
		// @Override
		// public void transformCanvas(Canvas canvas, float percentOpen) {
		// params.width = dip2px(ct, 16 - 11 * percentOpen);
		// switchButton.setLayoutParams(params);
		// }
		// };
		// slidingMenu.setBehindCanvasTransformer(mTransformer);

		return slidingMenu;
	}

	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	public static DisplayImageOptions getImageOption(Context context) {
		File cacheDir = StorageUtils.getOwnCacheDirectory(context, "imageloader/Cache");
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
				// .memoryCacheExtraOptions(480, 800)
				// max width, max height���������ÿ�������ļ�����󳤿�
				// .diskCacheExtraOptions(480, 800, null)
				// Can slow ImageLoader, use it carefully (Better don't use it)
				.threadPoolSize(5)
				// �̳߳��ڼ��ص�����
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024))
				// You can pass your own memory cache
				// implementation/�����ͨ���Լ����ڴ滺��ʵ��
				.memoryCacheSize(2 * 1024 * 1024).diskCacheSize(50 * 1024 * 1024)
				.diskCacheFileNameGenerator(new Md5FileNameGenerator())
				// �������ʱ���URI������MD5 ����
				.tasksProcessingOrder(QueueProcessingType.LIFO).diskCacheFileCount(100)
				// ������ļ�����
				.diskCache(new UnlimitedDiskCache(cacheDir))
				// �Զ��建��·��
				.defaultDisplayImageOptions(DisplayImageOptions.createSimple())
				.imageDownloader(new BaseImageDownloader(context, 5 * 1000, 30 * 1000))
				// connectTimeout(5s), readTimeout(30s)
				.writeDebugLogs() // Remove for release app
				.build();
		ImageLoader.getInstance().init(config);
		DisplayImageOptions options;
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.dark_menu_avatar_bg_mask) // ����ͼƬ�������ڼ���ʾ��ͼƬ
				.showImageForEmptyUri(R.drawable.dark_menu_avatar_bg_mask)// ����ͼƬUriΪ�ջ��Ǵ����ʱ����ʾ��ͼƬ
				.showImageOnFail(R.drawable.dark_menu_avatar_bg_mask) // ����ͼƬ����/��������д���ʱ����ʾ��ͼƬ
				.cacheInMemory(true)// �������ص�ͼƬ�Ƿ񻺴����ڴ���
				.cacheOnDisk(true)// �������ص�ͼƬ�Ƿ񻺴���SD����
				.considerExifParams(true) // �Ƿ���JPEGͼ��EXIF��������ת����ת��
				.imageScaleType(ImageScaleType.EXACTLY_STRETCHED)// ����ͼƬ����εı��뷽ʽ��ʾ
				.resetViewBeforeLoading(true)// ����ͼƬ������ǰ�Ƿ����ã���λ
				.displayer(new RoundedBitmapDisplayer(20))// �Ƿ�����ΪԲ�ǣ�����Ϊ����
				.displayer(new FadeInBitmapDisplayer(100))// �Ƿ�ͼƬ���غú���Ķ���ʱ��
				.build();
		return options;
	}

	@SuppressLint("InflateParams")
	public static void showToast(Context context, String text) {
		View myToastView = LayoutInflater.from(context).inflate(R.layout.toast, null);
		TextView myToastText = (TextView) myToastView.findViewById(R.id.toast_text);
		myToastText.setText(text);
		Toast toast = new Toast(context);
		toast.setView(myToastView);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.show();
	}

	public static void handleItemClick(Activity activity, PostBean postBean) {
		String itemContentType = postBean.getContentType();
		if (itemContentType.equals("article")) {
			// Util.showToast(activity, "��������");

			Intent intent = new Intent(activity, WebViewActivity.class);
			Bundle bundle = new Bundle();
			bundle.putInt("id", postBean.getId());
			bundle.putCharSequence("content_type", postBean.getContentType());
			bundle.putCharSequence("itemURL", postBean.getItemURL());
			bundle.putCharSequence("title", postBean.getTitle());
			bundle.putCharSequence("description", postBean.getDescription());
			bundle.putCharSequence("ImageURL", postBean.getImageUrl());
			bundle.putCharSequence("author", postBean.getAuthor());
			bundle.putCharSequence("created_at", postBean.getCreatedAt());
			bundle.putCharSequence("updated_at", postBean.getUpdatedAt());
			intent.putExtras(bundle);
			activity.startActivity(intent);
			activity.overridePendingTransition(R.anim.base_slide_right_in, R.anim.base_slide_remain);
		} else if (itemContentType.equals("album")) {
			// Util.showToast(activity, "�������");

			Intent intent = new Intent(activity, AlbumWebViewActivity.class);
			Bundle bundle = new Bundle();
			bundle.putInt("id", postBean.getId());
			bundle.putCharSequence("content_type", postBean.getContentType());
			bundle.putCharSequence("itemURL", postBean.getItemURL());
			bundle.putCharSequence("title", postBean.getTitle());
			bundle.putCharSequence("description", postBean.getDescription());
			bundle.putCharSequence("ImageURL", postBean.getHeaderImageUrl());
			bundle.putCharSequence("author", postBean.getAuthor());
			bundle.putCharSequence("created_at", postBean.getCreatedAt());
			bundle.putCharSequence("updated_at", postBean.getUpdatedAt());
			intent.putExtras(bundle);
			activity.startActivity(intent);
			activity.overridePendingTransition(R.anim.base_slide_right_in, R.anim.base_slide_remain);
		} else if (itemContentType.equals("video")) {
			// Util.showToast(activity, "��Ƶ����");

			Intent intent = new Intent(activity, WebViewActivity.class);
			Bundle bundle = new Bundle();
			bundle.putInt("id", postBean.getId());
			bundle.putCharSequence("content_type", postBean.getContentType());
			bundle.putCharSequence("itemURL", postBean.getItemURL());
			bundle.putCharSequence("title", postBean.getTitle());
			bundle.putCharSequence("description", postBean.getDescription());
			bundle.putCharSequence("ImageURL", postBean.getHeaderImageUrl());
			bundle.putCharSequence("author", postBean.getAuthor());
			bundle.putCharSequence("created_at", postBean.getCreatedAt());
			bundle.putCharSequence("updated_at", postBean.getUpdatedAt());
			intent.putExtras(bundle);
			activity.startActivity(intent);
			activity.overridePendingTransition(R.anim.base_slide_right_in, R.anim.base_slide_remain);

		} else if (itemContentType.equals("special")) {
			// Util.showToast(activity, "ר������");

			Intent intent = new Intent(activity, SpecialActivity.class);
			Bundle bundle = new Bundle();
			bundle.putInt("id", postBean.getId());
			bundle.putCharSequence("content_type", postBean.getContentType());
			bundle.putCharSequence("itemURL", postBean.getItemURL());
			bundle.putCharSequence("title", postBean.getTitle());
			bundle.putCharSequence("description", postBean.getDescription());
			bundle.putCharSequence("ImageURL", postBean.getHeaderImageUrl());
			bundle.putCharSequence("author", postBean.getAuthor());
			bundle.putCharSequence("created_at", postBean.getCreatedAt());
			bundle.putCharSequence("updated_at", postBean.getUpdatedAt());
			intent.putExtras(bundle);
			activity.startActivity(intent);
			activity.overridePendingTransition(R.anim.base_slide_right_in, R.anim.base_slide_remain);
		} else {
			// Util.showToast(activity, "δ֪����");
		}
	}

	// ����״̬����ɫ(Ҫ��API19����)
	@TargetApi(Build.VERSION_CODES.KITKAT)
	public static boolean setStatusBarColor(Activity activity) {
		if (android.os.Build.VERSION.SDK_INT >= 19) {
			// ���ñ�����͸��
			Window window = activity.getWindow();
			window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
					WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

			SystemBarTintManager tintManager = new SystemBarTintManager(activity);
			// ����״̬������
			tintManager.setStatusBarTintEnabled(true);
			tintManager.setStatusBarTintColor(Color.parseColor("#000000"));
			return true;
		} else {
			return false;
		}
	}

	// ���õ�������ɫ(Ҫ��API19����)
	@TargetApi(Build.VERSION_CODES.KITKAT)
	public static boolean setNavigationBarColor(Activity activity) {
		if (android.os.Build.VERSION.SDK_INT >= 19) {
			// ���õ�����͸��
			Window window = activity.getWindow();
			window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
					WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

			// SystemBarTintManager tintManager = new SystemBarTintManager(
			// activity);
			// �����������
			// tintManager.setNavigationBarTintEnabled(true);
			// tintManager.setNavigationBarTintColor(Color.parseColor("#FF44BC49"));
			return true;
		} else {
			return false;
		}
	}

	public static int getStatusBarHeight(Context context) {
		Class<?> c = null;
		Object obj = null;
		Field field = null;
		int x = 0, statusBarHeight = 0;
		try {
			c = Class.forName("com.android.internal.R$dimen");
			obj = c.newInstance();
			field = c.getField("status_bar_height");
			x = Integer.parseInt(field.get(obj).toString());
			statusBarHeight = context.getResources().getDimensionPixelSize(x);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return statusBarHeight;
	}

}
