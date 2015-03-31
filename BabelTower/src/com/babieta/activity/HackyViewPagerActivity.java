package com.babieta.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import uk.co.senab.photoview.PhotoView;

import com.avos.avoscloud.LogUtil.log;
import com.babieta.R;
import com.babieta.layout.HackyViewPager;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class HackyViewPagerActivity extends Activity {

	private List<Map<String, String>> photoItems = new ArrayList<Map<String, String>>();
	private Map<String, String> photo;

	private static final String ISLOCKED_ARG = "isLocked";

	private String title;
	private String photos;
	private String clickedImageURL;

	private ActionBar actionBar;
	private ViewPager mViewPager;
	DisplayImageOptions options;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hacky_viewpager); // 一个扩展的ViewPager布局

		options = new DisplayImageOptions.Builder().showImageForEmptyUri(R.drawable.ic_empty)
				.showImageOnFail(R.drawable.ic_error).resetViewBeforeLoading(true)
				.cacheOnDisk(true).imageScaleType(ImageScaleType.EXACTLY)
				.bitmapConfig(Bitmap.Config.RGB_565).considerExifParams(true)
				.displayer(new FadeInBitmapDisplayer(300)).build();

		// 初始化数据
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		title = bundle.getString("title");
		photos = bundle.getString("photos");
		clickedImageURL = bundle.getString("clickedImage");
		JSONArray mJsonArray;
		try {
			mJsonArray = new JSONArray(photos);
			for (int i = 0; i < mJsonArray.length(); i++) {
				JSONObject mJsonObject = mJsonArray.getJSONObject(i);
				photo = new HashMap<String, String>();
				photo.put("image_url", mJsonObject.getString("image_url"));
				photo.put("description", mJsonObject.getString("description"));
				photoItems.add(photo);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		// 设置action bar
		actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		// 改变左上角那个向上的箭头图标
		int upid = Resources.getSystem().getIdentifier("up", "id", "android");
		ImageView img = (ImageView) findViewById(upid);
		img.setPadding(10, 0, 10, 0);

		// 初始化HackyViewPager
		mViewPager = (HackyViewPager) findViewById(R.id.hacky_viewpager);
		mViewPager.setAdapter(new ImageAdapter());
		actionBar.setTitle(title + "(" + (mViewPager.getCurrentItem() + 1) + "/"
				+ photoItems.size() + ")");
		mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			@Override
			// 这个方法有一个参数position，代表哪个页面被选中。当用手指滑动翻页的时候，
			// 如果翻动成功了（滑动的距离够长），手指抬起来就会立即执行这个方法，position就是当前滑动到的页面。
			public void onPageSelected(int arg0) {
				actionBar.setTitle(title + "(" + (mViewPager.getCurrentItem() + 1) + "/"
						+ photoItems.size() + ")");
			}

			@Override
			// 这个方法会在屏幕滚动过程中不断被调用。
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			// 这个方法在手指操作屏幕的时候发生变化。有三个值：0（END）,1(PRESS) , 2(UP) 。
			public void onPageScrollStateChanged(int arg0) {

			}
		});

		// 切换到从网页里面点击进来的那一张图
		for (int i = 0; i < photoItems.size(); i++) {
			String currentImageURL = photoItems.get(i).get("image_url");
			if (currentImageURL.equals(clickedImageURL)) {
				mViewPager.setCurrentItem(i);
			}
		}
	}

	private class ImageAdapter extends PagerAdapter {
		private LayoutInflater inflater;

		public ImageAdapter() {
			inflater = LayoutInflater.from(HackyViewPagerActivity.this);
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			View imageLayout = inflater.inflate(R.layout.item_pager_image, container, false);
			assert imageLayout != null;

			final ProgressBar spinner = (ProgressBar) imageLayout
					.findViewById(R.id.item_paper_loading);
			final PhotoView photoView = (PhotoView) imageLayout.findViewById(R.id.item_paper_image);
			final TextView photoDescription = (TextView) imageLayout
					.findViewById(R.id.item_paper_text);
			photoDescription.setText(photoItems.get(position).get("description"));

			ImageLoader.getInstance().displayImage(photoItems.get(position).get("image_url"),
					photoView, options, new SimpleImageLoadingListener() {
						@Override
						public void onLoadingStarted(String imageUri, View view) {
							spinner.setVisibility(View.VISIBLE);
						}

						@Override
						public void onLoadingFailed(String imageUri, View view,
								FailReason failReason) {
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
							Toast.makeText(HackyViewPagerActivity.this, message, Toast.LENGTH_SHORT)
									.show();

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
					mViewPager.getCurrentItem();
					openContextMenu(photoView);
					return true;
				}
			});

			photoView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {

				@Override
				public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
					// menu.setHeaderTitle("选项");
					menu.add(0, 0, 0, "保存图片");
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

	private boolean isViewPagerActive() {
		return (mViewPager != null && mViewPager instanceof HackyViewPager);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, 0, 0, "保存图片");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			this.finish();
			overridePendingTransition(0, R.anim.base_slide_right_out);
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 0:
			log.d("onContextItemSelected", "0");
			break;
		default:
			break;
		}
		return super.onContextItemSelected(item);
	}

}
