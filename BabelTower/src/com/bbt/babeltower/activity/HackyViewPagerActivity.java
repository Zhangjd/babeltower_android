package com.bbt.babeltower.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.avos.avoscloud.LogUtil.log;
import com.bbt.babeltower.R;
import com.bbt.babeltower.adapter.ImageAdapter;
import com.bbt.babeltower.base.Util;
import com.bbt.babeltower.layout.HackyViewPager;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

public class HackyViewPagerActivity extends Activity {

	private List<Map<String, String>> photoItems = new ArrayList<Map<String, String>>();
	private Map<String, String> photo;

	private String title;
	private String photos;
	private String clickedImageURL;

	private ActionBar actionBar;
	private ViewPager mViewPager;
	private ImageAdapter mImageAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hacky_viewpager); // 一个扩展的ViewPager布局

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
		mImageAdapter = new ImageAdapter(HackyViewPagerActivity.this, photoItems);
		mViewPager.setAdapter(mImageAdapter);
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
		clickedImageURL = clickedImageURL.replace("newskit.100steps.net", "218.192.166.167:3030");
		clickedImageURL = clickedImageURL.replace("original", "medium");
		for (int i = 0; i < photoItems.size(); i++) {
			String currentImageURL = photoItems.get(i).get("image_url");
			if (currentImageURL.equals(clickedImageURL)) {
				mViewPager.setCurrentItem(i);
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(1, 0, 0, "保存图片");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			this.finish();
			overridePendingTransition(0, R.anim.base_slide_right_out);
		case 0:
			if (item.getGroupId() == 1) { // 保存图片
				SaveTask mSaveTask = new SaveTask(this);
				mSaveTask.execute();
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 0:
			if (item.getGroupId() == 1) {// 保存图片
				SaveTask mSaveTask = new SaveTask(this);
				mSaveTask.execute();
			}
			break;
		default:
			break;
		}
		return super.onContextItemSelected(item);
	}

	// 异步任务AsyncTask使用解析:
	// Reference : http://blog.csdn.net/wxg630815/article/details/7003812
	class SaveTask extends AsyncTask<Void, Integer, Integer> {
		private Context context;
		private String resultStr = "";

		public SaveTask(Context context) {
			this.context = context;
		}

		@Override
		protected Integer doInBackground(Void... params) {
			View currentView = mImageAdapter.getPrimaryItem();
			ImageView imgView = (ImageView) currentView.findViewById(R.id.item_paper_image);
			// 在调用getDrawingCache()方法从ImageView对象获取图像之前，一定要调用setDrawingCacheEnabled(true)方法
			// 否则，无法从ImageView对象iv_photo中获取图像
			imgView.setDrawingCacheEnabled(true);
			// 从ImageView对象中获取图像的方法，就是ImageView类中的getDrawingCache()方法
			Bitmap bitmap = Bitmap.createBitmap(imgView.getDrawingCache());
			// 在调用getDrawingCache()方法从ImageView对象获取图像之后，一定要调用setDrawingCacheEnabled(false)方法
			// 以清空画图缓冲区，否则，下一次从ImageView对象iv_photo中获取的图像，还是原来的图像。
			imgView.setDrawingCacheEnabled(false);

			if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) { // SD卡正常挂载
				File sdCardDir = Environment.getExternalStorageDirectory();// 获取SDCard目录
				FileOutputStream outStream = null;
				String name = "/BabelTower/" + UUID.randomUUID().toString()
						+ ".png";
				try {
					File saveFile = new File(sdCardDir, name);
					if (!saveFile.getParentFile().exists()) {
						saveFile.getParentFile().mkdirs();
					}
					outStream = new FileOutputStream(saveFile);
					// 当指定压缩格式为PNG时保存下来的图片显示正常
					bitmap.compress(CompressFormat.PNG, 100, outStream);
					outStream.flush();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						outStream.close();
						log.d("dest", sdCardDir.toString() + name);
						resultStr = sdCardDir.toString() + name;
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			if (!resultStr.isEmpty()) {
				Util.showToast(context, "照片已保存在" + resultStr);
			} else {

			}
		}
	}

}
