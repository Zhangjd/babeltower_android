package com.bbt.babeltower.adapter;

import java.util.List;
import java.util.Map;

import uk.co.senab.photoview.PhotoView;

import com.bbt.babeltower.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class ImageAdapter extends PagerAdapter {
	private Activity activity;
	private LayoutInflater inflater;
	private View mCurrentView;
	private List<Map<String, String>> photoItems;
	private DisplayImageOptions options;

	public ImageAdapter(Activity activity, List<Map<String, String>> photoItems) {
		this.activity = activity;
		this.photoItems = photoItems;
		this.inflater = LayoutInflater.from(activity);
		this.options = new DisplayImageOptions.Builder().showImageForEmptyUri(R.drawable.ic_empty)
				.showImageOnFail(R.drawable.ic_error).resetViewBeforeLoading(true)
				.cacheOnDisk(true).imageScaleType(ImageScaleType.EXACTLY)
				.bitmapConfig(Bitmap.Config.RGB_565).considerExifParams(true)
				.displayer(new FadeInBitmapDisplayer(300)).build();
	}

	@Override
	public void setPrimaryItem(ViewGroup container, int position, Object object) {
		super.setPrimaryItem(container, position, object);
		mCurrentView = (View) object;
	}

	public View getPrimaryItem() {
		return mCurrentView;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		View imageLayout = inflater.inflate(R.layout.item_pager_image, container, false);
		assert imageLayout != null;

		final ProgressBar spinner = (ProgressBar) imageLayout.findViewById(R.id.item_paper_loading);
		final PhotoView photoView = (PhotoView) imageLayout.findViewById(R.id.item_paper_image);
		final TextView photoDescription = (TextView) imageLayout.findViewById(R.id.item_paper_text);
		photoDescription.setText(photoItems.get(position).get("description"));

		ImageLoader.getInstance().displayImage(photoItems.get(position).get("image_url"),
				photoView, options, new SimpleImageLoadingListener() {
					@Override
					public void onLoadingStarted(String imageUri, View view) {
						spinner.setVisibility(View.VISIBLE);
					}

					@Override
					public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
						String message = null;
						switch (failReason.getType()) {
						case IO_ERROR:
							message = "Input/Output error";
							break;
						case DECODING_ERROR:
							message = "Image can't be decoded";
							break;
						case NETWORK_DENIED:
							message = "Downloads are denied";
							break;
						case OUT_OF_MEMORY:
							message = "Out Of Memory error";
							break;
						case UNKNOWN:
							message = "Unknown error";
							break;
						}
						Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();

						spinner.setVisibility(View.GONE);
					}

					@Override
					public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
						spinner.setVisibility(View.GONE);
					}
				});

		photoView.setOnLongClickListener(new View.OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				activity.openContextMenu(photoView);
				return true;
			}
		});

		photoView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {

			@Override
			public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
				menu.add(1, 0, 0, "±£´æÍ¼Æ¬");
			}
		});

		container.addView(imageLayout, 0);
		return imageLayout;
	}

	@Override
	public int getCount() {
		return photoItems.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object arg1) {
		return view.equals(arg1);
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View) object);
	}
}