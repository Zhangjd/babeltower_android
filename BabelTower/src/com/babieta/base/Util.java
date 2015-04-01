package com.babieta.base;

import java.io.File;
import android.app.Activity;
import android.content.Context;
import com.babieta.R;
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
		SlidingMenu slidingMenu = new SlidingMenu(context);
		slidingMenu.setMode(SlidingMenu.LEFT); // 设置菜单模式
		slidingMenu.setBehindOffsetRes(R.dimen.sliding_menu_offset); // 设置侧滑栏完全展开之后，距离另外一边的距离
		slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN); // 设置侧滑栏的触摸模式(3种)
		slidingMenu.setTouchModeBehind(SlidingMenu.TOUCHMODE_FULLSCREEN); // SlideMenu本身有bug，按照https://github.com/jfeinstein10/SlidingMenu/issues/446进行了修改
		slidingMenu.attachToActivity(context, SlidingMenu.SLIDING_CONTENT); // 将SlidingMenu连接到Activity
		slidingMenu.setMenu(R.layout.slidemenu_left); // 设置菜单layout
		slidingMenu.setShadowDrawable(R.drawable.shadow); // 设置阴影
		slidingMenu.setShadowWidth(30); // 设置阴影宽度
		slidingMenu.setFadeEnabled(true);
		slidingMenu.setFadeDegree(0.6f); // 设置菜单刚滑出时候的渐变度

		return slidingMenu;
	}

	public static DisplayImageOptions getImageOption(Context context) {
		File cacheDir = StorageUtils.getOwnCacheDirectory(context, "imageloader/Cache");
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
				// .memoryCacheExtraOptions(480, 800)
				// max width, max height，即保存的每个缓存文件的最大长宽
				// .diskCacheExtraOptions(480, 800, null)
				// Can slow ImageLoader, use it carefully (Better don't use it)
				.threadPoolSize(5)
				// 线程池内加载的数量
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024))
				// You can pass your own memory cache
				// implementation/你可以通过自己的内存缓存实现
				.memoryCacheSize(2 * 1024 * 1024).diskCacheSize(50 * 1024 * 1024)
				.diskCacheFileNameGenerator(new Md5FileNameGenerator())
				// 将保存的时候的URI名称用MD5 加密
				.tasksProcessingOrder(QueueProcessingType.LIFO).diskCacheFileCount(100)
				// 缓存的文件数量
				.diskCache(new UnlimitedDiskCache(cacheDir))
				// 自定义缓存路径
				.defaultDisplayImageOptions(DisplayImageOptions.createSimple())
				.imageDownloader(new BaseImageDownloader(context, 5 * 1000, 30 * 1000))
				// connectTimeout(5s), readTimeout(30s)
				.writeDebugLogs() // Remove for release app
				.build();
		ImageLoader.getInstance().init(config);
		DisplayImageOptions options;
		options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.ic_launcher) // 设置图片在下载期间显示的图片
				.showImageForEmptyUri(com.babieta.R.drawable.ic_launcher)// 设置图片Uri为空或是错误的时候显示的图片
				.showImageOnFail(com.babieta.R.drawable.ic_launcher) // 设置图片加载/解码过程中错误时候显示的图片
				.cacheInMemory(true)// 设置下载的图片是否缓存在内存中
				.cacheOnDisk(true)// 设置下载的图片是否缓存在SD卡中
				.considerExifParams(true) // 是否考虑JPEG图像EXIF参数（旋转，翻转）
				.imageScaleType(ImageScaleType.EXACTLY_STRETCHED)// 设置图片以如何的编码方式显示
				.resetViewBeforeLoading(true)// 设置图片在下载前是否重置，复位
				.displayer(new RoundedBitmapDisplayer(20))// 是否设置为圆角，弧度为多少
				.displayer(new FadeInBitmapDisplayer(100))// 是否图片加载好后渐入的动画时间
				.build();
		return options;
	}
}
