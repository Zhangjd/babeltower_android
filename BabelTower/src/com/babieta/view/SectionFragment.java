package com.babieta.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import com.babieta.R;
import com.babieta.activity.SectionContentActivity;
import com.babieta.base.ApiUrl;
import com.babieta.base.Netroid;
import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.duowan.mobile.netroid.request.JsonObjectRequest;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class SectionFragment extends Fragment {
	private ProgressDialog mProgressDialog;
	private final String REQUESTS_TAG = "section_request";
	private ArrayList<HashMap<String, Object>> sectionList = new ArrayList<HashMap<String, Object>>();
	private View view;
	
	private static JSONArray sections = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view = inflater.inflate(R.layout.fragment_section, container, false);
		this.initSection();
		return view;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		TextView titleTextView = (TextView) getActivity().findViewById(R.id.header_textview);
		titleTextView.setText("分类浏览");
	}

	private void initSection() {
		JSONObject jsonRequest = null;
		
		if(sections!=null){
			for (int i = 0; i < sections.length(); i++) {
				JSONObject mJsonObject;
				try {
					mJsonObject = (JSONObject) sections.get(i);
					HashMap<String, Object> map = new HashMap<String, Object>();
					map.put("ItemImage", R.drawable.account_avatar);
					map.put("ItemText", "" + i);

					map.put("id", mJsonObject.getInt("id"));
					map.put("category", mJsonObject.getString("category"));
					map.put("module", mJsonObject.getString("module"));
					map.put("created_at", mJsonObject.getString("created_at"));
					map.put("updated_at", mJsonObject.getString("updated_at"));
					map.put("active", mJsonObject.getBoolean("active"));
					map.put("section_image_file_name",
							mJsonObject.getString("section_image_file_name"));
					map.put("section_image_content_type",
							mJsonObject.getString("section_image_content_type"));
					map.put("section_image_file_size",
							mJsonObject.getString("section_image_file_size"));
					map.put("section_image_updated_at",
							mJsonObject.getString("section_image_updated_at"));

					sectionList.add(map);
					loadSectionList();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			return;
		}
		
		JsonObjectRequest request = new JsonObjectRequest(ApiUrl.BABIETA_BASE_URL
				+ ApiUrl.BABIETA_SECTION_LIST, jsonRequest, new Listener<JSONObject>() {
			@Override
			public void onSuccess(JSONObject response) {
				try {
					if (response.has("status") && response.getInt("status") == 0) {
						JSONArray jsonArray = response.getJSONArray("sections");
						sections = jsonArray;

						for (int i = 0; i < jsonArray.length(); i++) {
							JSONObject mJsonObject = (JSONObject) jsonArray.get(i);

							HashMap<String, Object> map = new HashMap<String, Object>();
							map.put("ItemImage", R.drawable.account_avatar);
							map.put("ItemText", "" + i);

							map.put("id", mJsonObject.getInt("id"));
							map.put("category", mJsonObject.getString("category"));
							map.put("module", mJsonObject.getString("module"));
							map.put("created_at", mJsonObject.getString("created_at"));
							map.put("updated_at", mJsonObject.getString("updated_at"));
							map.put("active", mJsonObject.getBoolean("active"));
							map.put("section_image_file_name",
									mJsonObject.getString("section_image_file_name"));
							map.put("section_image_content_type",
									mJsonObject.getString("section_image_content_type"));
							map.put("section_image_file_size",
									mJsonObject.getString("section_image_file_size"));
							map.put("section_image_updated_at",
									mJsonObject.getString("section_image_updated_at"));

							sectionList.add(map);
							loadSectionList();
						}
					} else {
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					System.out.println("error in parsing JSON : body_html");
					e.printStackTrace();
				}
			}

			@Override
			public void onError(NetroidError error) {
				String data = error.getMessage();
				System.out.println("error occurred : " + data);
			}

			@Override
			public void onPreExecute() {
				mProgressDialog = new ProgressDialog(getActivity(), AlertDialog.THEME_HOLO_LIGHT);
				mProgressDialog.setMessage("Loading...");
				mProgressDialog.setIndeterminate(false);
				mProgressDialog.setCancelable(true);
				mProgressDialog.setCanceledOnTouchOutside(false);
				mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

					@Override
					public void onCancel(DialogInterface dialog) {
						// TODO Auto-generated method stub
						Netroid.getRequestQueue().cancelAll(REQUESTS_TAG);

					}
				});
				mProgressDialog.show();
			}

			// 在onFinish()时cancel dialog
			@Override
			public void onFinish() {
				mProgressDialog.cancel();
			}

		});
		// 设置请求标识，这个标识可用于终止该请求时传入的Key
		request.setTag(REQUESTS_TAG);
		Netroid.addRequest(request);
	}

	private void loadSectionList() {
		GridView gridview = (GridView) view.findViewById(R.id.section_gridview);
		for (int i = 0; i < sectionList.size(); i++) {
			HashMap<String, Object> map = sectionList.get(i);
			map.put("ItemImage", R.drawable.account_avatar);
			sectionList.set(i, map);
		}

		MySectionListAdapter saItem = new MySectionListAdapter(getActivity(), sectionList, // 数据源
				R.layout.fragment_section_griditem, // xml实现
				new String[] { "ItemImage", "module" }, // 对应map的Key
				new int[] { R.id.section_griditem_imageview, R.id.section_griditem_textview }); // 对应R的Id

		// 实现ViewBinder()这个接口
		// http://blog.csdn.net/admin_/article/details/7257901

		// saItem.setViewBinder(new ViewBinder() {
		// @Override
		// public boolean setViewValue(View view, Object data, String
		// textRepresentation) {
		// // TODO Auto-generated method stub
		// if (view instanceof ImageView && data instanceof Bitmap) {
		// ImageView i = (ImageView) view;
		// i.setImageBitmap((Bitmap) data);
		// return true;
		// }
		// return false;
		// }
		// });

		// 添加Item到网格中
		gridview.setAdapter(saItem);
		// 添加点击事件
		gridview.setOnItemClickListener(new OnItemClickListener() {
			@SuppressWarnings("unchecked")
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				HashMap<String, Object> map = (HashMap<String, Object>) parent
						.getItemAtPosition(position);
				String itemId = String.valueOf(map.get("id"));
				String itemURL = ApiUrl.BABIETA_BASE_URL
						+ ApiUrl.BABIETA_SECTION_CONTENTS.replace("{id}", itemId);

				// Toast.makeText(getActivity(), "url：" + itemURL,
				// Toast.LENGTH_SHORT).show();

				// 接下来,跳转到SectionContentActivity
				Intent intent = new Intent(getActivity(), SectionContentActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("itemURL", itemURL);
				bundle.putString("category", String.valueOf(map.get("category")));
				bundle.putString("module", String.valueOf(map.get("module")));
				intent.putExtras(bundle);
				startActivity(intent);
				getActivity().overridePendingTransition(R.anim.base_slide_right_in,
						R.anim.base_slide_remain);
			}
		});
	}

	private class MySectionListAdapter extends SimpleAdapter {
		private Context context;

		public MySectionListAdapter(Context context, List<? extends Map<String, ?>> data,
				int resource, String[] from, int[] to) {
			super(context, data, resource, from, to);
			this.context = context;
			// TODO Auto-generated constructor stub
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			convertView = LayoutInflater.from(context).inflate(R.layout.fragment_section_griditem,
					null);
			convertView.setPadding(0, dip2px(context, 10), 0, dip2px(context, 20));

			return super.getView(position, convertView, parent);
		}

		private int dip2px(Context context, float dpValue) {
			final float scale = context.getResources().getDisplayMetrics().density;
			return (int) (dpValue * scale + 0.5f);
		}
	}
}
