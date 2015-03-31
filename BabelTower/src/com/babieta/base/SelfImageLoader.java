package com.babieta.base;

import java.util.concurrent.TimeUnit;

import com.duowan.mobile.netroid.RequestQueue;
import com.duowan.mobile.netroid.request.ImageRequest;
import com.duowan.mobile.netroid.toolbox.ImageLoader;

public class SelfImageLoader extends ImageLoader {

	public SelfImageLoader(RequestQueue queue, ImageCache imageCache) {
		super(queue, imageCache);
	}

	@Override
    public ImageRequest buildRequest(String requestUrl, int maxWidth, int maxHeight) {
        ImageRequest request = new ImageRequest(requestUrl, maxWidth, maxHeight);
        request.setCacheExpireTime(TimeUnit.MINUTES, 20);
        return request;
    }

}
