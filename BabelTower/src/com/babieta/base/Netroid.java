package com.babieta.base;

import java.io.File;
import org.apache.http.protocol.HTTP;

import android.content.Context;
import android.widget.ImageView;

import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.Network;
import com.duowan.mobile.netroid.RequestQueue;
import com.duowan.mobile.netroid.cache.BitmapImageCache;
import com.duowan.mobile.netroid.cache.DiskCache;
import com.duowan.mobile.netroid.image.NetworkImageView;
import com.duowan.mobile.netroid.request.JsonObjectRequest;
import com.duowan.mobile.netroid.stack.HurlStack;
import com.duowan.mobile.netroid.toolbox.BasicNetwork;
import com.duowan.mobile.netroid.toolbox.FileDownloader;
import com.duowan.mobile.netroid.toolbox.ImageLoader;

public class Netroid {
	// Netroid��ڣ�˽�и�ʵ�����ṩ�����������
	private static RequestQueue mRequestQueue;

	// ͼƬ���ع�������˽�и�ʵ�����ṩ�����������
	private static ImageLoader mImageLoader;

	// �ļ����ع�������˽�и�ʵ�����ṩ�����������
	private static FileDownloader mFileDownloader;

	private Netroid() {
	}

	public static void init(Context ctx) {
		// if (mRequestQueue != null) throw new
		// IllegalStateException("initialized");
		if (mRequestQueue != null) return;

		// ����Netroid���ָ࣬��Ӳ�̻��淽��
		Network network = new BasicNetwork(new HurlStack(Const.USER_AGENT, null), HTTP.UTF_8);
		mRequestQueue = new RequestQueue(network, 4, new DiskCache(new File(ctx.getCacheDir(),
				Const.HTTP_DISK_CACHE_DIR_NAME), Const.HTTP_DISK_CACHE_SIZE));

		// ����ImageLoaderʵ����ָ���ڴ滺�淽��
		// ע��SelfImageLoader��ʵ��ʾ����鿴ͼƬ���ص�����ĵ�
		// ע��ImageLoader��FileDownloader���Ǳ����ʼ������������û���ô�������Ҫ����ʵ��
		mImageLoader = new SelfImageLoader(mRequestQueue, new BitmapImageCache(
				Const.HTTP_MEMORY_CACHE_SIZE));

		mFileDownloader = new FileDownloader(mRequestQueue, 1);

		mRequestQueue.start();
	}

	// ���ص���ͼƬ
	public static void displayImage(String url, ImageView imageView) {
		ImageLoader.ImageListener listener = ImageLoader.getImageListener(imageView, 0, 0);
		mImageLoader.get(url, listener, 0, 0);
	}

	// ��������ͼƬ
	public static void displayImage(String url, NetworkImageView imageView) {
		imageView.setImageUrl(url, mImageLoader);
	}

	// ִ���ļ���������
	public static FileDownloader.DownloadController addFileDownload(String storeFilePath,
			String url, Listener<Void> listener) {
		return mFileDownloader.add(storeFilePath, url, listener);
	}

	public static void addRequest(JsonObjectRequest request) {
		mRequestQueue.add(request);
	}
	
	public static RequestQueue getRequestQueue(){
		return mRequestQueue;
	}
}
