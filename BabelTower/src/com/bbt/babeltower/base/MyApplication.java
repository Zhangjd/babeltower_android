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

// Ӧ�ó������������
public class MyApplication extends Application {
	@Override
	public void onCreate() { // onCreate���������ȫ�ֱ���������ֵ
		super.onCreate();
		initLeanCloud();
		initImageLoader(getApplicationContext());
	}

	private void initLeanCloud() {
		// ��ʼ��LeanCloud
		AVOSCloud.initialize(this, "8tz2rhws2xs9x3kg00omc6x0t21gv58utzmuf9vtme1ogjz1",
				"jij190is0jclhevgbzltml1ex62n4bigcbmbc4h6sj87cwvt");
		// ���ñ�������ͳ��
		AVAnalytics.enableCrashReport(this.getApplicationContext(), true);
		// ���� installation ��������
		AVInstallation.getCurrentInstallation().saveInBackground(new SaveCallback() {
			public void done(AVException e) {
				if (e == null) {
					// ����ɹ�
					String installationId = AVInstallation.getCurrentInstallation()
							.getInstallationId();
					// ���� installationId ���û���Ȳ�������
					Log.i("installationIdSaved", installationId);
				} else {
					// ����ʧ�ܣ����������Ϣ
				}
			}
		});

		// set a default callback. It's necessary for current SDK.
		// ��v2.0�Ժ�İ汾����������δ��룬�Ա��������޷��ɹ��ﵽ�ͻ��˵�����
		PushService.setDefaultPushCallback(this, PushCallBackActivity.class);

		// TestObject ���¶��󽫱����͵� LeanCloud ����������
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
