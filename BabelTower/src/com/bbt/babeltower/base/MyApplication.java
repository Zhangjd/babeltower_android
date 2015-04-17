package com.bbt.babeltower.base;

import com.avos.avoscloud.AVAnalytics;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.PushService;
import com.avos.avoscloud.SaveCallback;
import com.bbt.babeltower.activity.PushCallBackActivity;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import android.app.Application;
import android.content.Context;
import android.util.Log;

// 应用程序入口在这里
public class MyApplication extends Application {
	@Override
	public void onCreate() { // onCreate负责对所有全局变量赋初期值
		super.onCreate();
		initLeanCloud();
		initImageLoader(getApplicationContext());
	}

	private void initLeanCloud() {
		// 初始化LeanCloud
		AVOSCloud.initialize(this, "8tz2rhws2xs9x3kg00omc6x0t21gv58utzmuf9vtme1ogjz1",
				"jij190is0jclhevgbzltml1ex62n4bigcbmbc4h6sj87cwvt");
		// 启用崩溃错误统计
		AVAnalytics.enableCrashReport(this.getApplicationContext(), true);
		// 保存 installation 到服务器
		AVInstallation.getCurrentInstallation().saveInBackground(new SaveCallback() {
			public void done(AVException e) {
				if (e == null) {
					// 保存成功
					String installationId = AVInstallation.getCurrentInstallation()
							.getInstallationId();
					// 关联 installationId 到用户表等操作……
					Log.i("installationIdSaved", installationId);
				} else {
					// 保存失败，输出错误信息
				}
			}
		});

		// set a default callback. It's necessary for current SDK.
		// 在v2.0以后的版本请务必添加这段代码，以避免推送无法成功达到客户端的问题
		PushService.setDefaultPushCallback(this, PushCallBackActivity.class);

		// TestObject 的新对象将被发送到 LeanCloud 并保存下来
		// AVObject testObject = new AVObject("TestObject");
		// testObject.put("foo", "bar");
		// testObject.saveInBackground();
	}

	public static void initImageLoader(Context context) {
		// This configuration tuning is custom. You can tune every option, you
		// may tune some of them,
		// or you can create default configuration by
		// ImageLoaderConfiguration.createDefault(this);
		// method.
		ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
		config.threadPriority(Thread.NORM_PRIORITY - 2);
		config.denyCacheImageMultipleSizesInMemory();
		config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
		config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
		config.tasksProcessingOrder(QueueProcessingType.LIFO);
		config.writeDebugLogs(); // Remove for release app

		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config.build());
	}
}
