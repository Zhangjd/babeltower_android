package com.babieta.activity;

import java.util.LinkedList;

import org.json.JSONException;
import org.json.JSONObject;

import com.avos.avoscloud.LogUtil.log;
import com.babieta.R;
import com.babieta.adapter.ListPostAdapter;
import com.babieta.base.Netroid;
import com.babieta.bean.PostBean;
import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.duowan.mobile.netroid.request.JsonObjectRequest;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

// �㿪"����"��Activity
public class SectionContentActivity extends SwipeBackActivity {
	private ProgressDialog mProgressDialog;
	private final String REQUESTS_TAG = "section_request";

	private String itemURL;
	// private String category;
	private String module;

	private ListView listView;
	private ListPostAdapter listPostAdapter;
	private LinkedList<PostBean> postBeans;

	private ImageButton backButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_section_content);

		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		itemURL = bundle.getString("itemURL");
		// category = bundle.getString("category");
		module = bundle.getString("module");

		TextView title = (TextView) findViewById(R.id.header_textview);
		title.setText("�����б� :" + module);

		initPostListView();
		initSectionContent();

		backButton = (ImageButton) findViewById(R.id.back_button);
		backButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
				overridePendingTransition(0, R.anim.base_slide_right_out);

			}
		});
	}

	private void initSectionContent() {
		JSONObject jsonRequest = null;
		JsonObjectRequest request = new JsonObjectRequest(itemURL, jsonRequest,
				new Listener<JSONObject>() {
					@Override
					public void onSuccess(JSONObject response) {
						try {
							if (response.has("status") && response.getInt("status") == 0) {
								String jsonString = response.toString();
								LinkedList<PostBean> postBeans = PostBean.parseSection(jsonString,
										SectionContentActivity.this); // ���������
								listPostAdapter.appendPost(postBeans);
								listPostAdapter.sortPost();
								listPostAdapter.notifyDataSetChanged();
							} else {
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onError(NetroidError error) {
						String data = error.getMessage();
						log.d("onError", data);
					}

					@Override
					public void onPreExecute() {
						mProgressDialog = new ProgressDialog(SectionContentActivity.this,
								AlertDialog.THEME_HOLO_LIGHT);
						mProgressDialog.setMessage("Loading...");
						mProgressDialog.setIndeterminate(false);
						mProgressDialog.setCancelable(true);
						mProgressDialog.setCanceledOnTouchOutside(false);
						mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

							@Override
							public void onCancel(DialogInterface dialog) {
								Netroid.getRequestQueue().cancelAll(REQUESTS_TAG);
							}
						});
						mProgressDialog.show();
					}

					// ��onFinish()ʱcancel dialog
					@Override
					public void onFinish() {
						mProgressDialog.cancel();
					}

				});
		// ���������ʶ�������ʶ��������ֹ������ʱ�����Key
		request.setTag(REQUESTS_TAG);
		Netroid.addRequest(request);
	}

	public void initPostListView() {
		listView = (ListView) findViewById(R.id.section_content_list_view);
		listPostAdapter = new ListPostAdapter(this);
		listView.setAdapter(listPostAdapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			// ���item��ת��WebView��
			public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {

				postBeans = listPostAdapter.postBeans;
				final int pos_final = pos;

				if (postBeans.get(pos).getContentType().equals("article")) {
					Toast.makeText(getApplicationContext(), "��������", Toast.LENGTH_SHORT).show();

					Intent intent = new Intent(SectionContentActivity.this, WebViewActivity.class);
					Bundle bundle = new Bundle();
					bundle.putInt("id", postBeans.get(pos).getId());
					bundle.putCharSequence("content_type", postBeans.get(pos).getContentType());
					bundle.putCharSequence("itemURL", postBeans.get(pos).getItemURL());
					bundle.putCharSequence("title", postBeans.get(pos).getTitle());
					bundle.putCharSequence("description", postBeans.get(pos).getDescription());
					bundle.putCharSequence("ImageURL", postBeans.get(pos).getImageUrl());
					bundle.putCharSequence("author", postBeans.get(pos).getAuthor());
					bundle.putCharSequence("created_at", postBeans.get(pos).getCreatedAt());
					bundle.putCharSequence("updated_at", postBeans.get(pos).getUpdatedAt());
					intent.putExtras(bundle);
					startActivity(intent);
					overridePendingTransition(R.anim.base_slide_right_in, R.anim.base_slide_remain);
				} else if (postBeans.get(pos).getContentType().equals("album")) {
					Toast.makeText(getApplicationContext(), "�������", Toast.LENGTH_SHORT).show();

					Intent intent = new Intent(SectionContentActivity.this,
							AlbumWebViewActivity.class);
					Bundle bundle = new Bundle();
					bundle.putInt("id", postBeans.get(pos).getId());
					bundle.putCharSequence("content_type", postBeans.get(pos).getContentType());
					bundle.putCharSequence("itemURL", postBeans.get(pos).getItemURL());
					bundle.putCharSequence("title", postBeans.get(pos).getTitle());
					bundle.putCharSequence("description", postBeans.get(pos).getDescription());
					bundle.putCharSequence("ImageURL", postBeans.get(pos).getImageUrl());
					bundle.putCharSequence("author", postBeans.get(pos).getAuthor());
					bundle.putCharSequence("created_at", postBeans.get(pos).getCreatedAt());
					bundle.putCharSequence("updated_at", postBeans.get(pos).getUpdatedAt());
					intent.putExtras(bundle);
					startActivity(intent);
					overridePendingTransition(R.anim.base_slide_right_in, R.anim.base_slide_remain);
				} else if (postBeans.get(pos).getContentType().equals("video")) {
					ContextThemeWrapper themedContext = new ContextThemeWrapper(
							SectionContentActivity.this,
							android.R.style.Theme_Holo_Light_Dialog_NoActionBar);
					AlertDialog.Builder builder = new AlertDialog.Builder(themedContext);
					AlertDialog alertDialog = builder.create();
					alertDialog.setMessage("����һ����Ƶ,������wifi�����¿���Ŷ");
					alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "�õ���", // ������İ�׿�趨,ȷ�������ұ�
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface arg0, int arg1) {
									Intent intent = new Intent(SectionContentActivity.this,
											VideoActivity.class);
									Bundle bundle = new Bundle();
									bundle.putInt("id", postBeans.get(pos_final).getId());
									bundle.putCharSequence("content_type", postBeans.get(pos_final)
											.getContentType());
									bundle.putCharSequence("itemURL", postBeans.get(pos_final)
											.getItemURL());
									bundle.putCharSequence("title", postBeans.get(pos_final)
											.getTitle());
									bundle.putCharSequence("description", postBeans.get(pos_final)
											.getDescription());
									bundle.putCharSequence("ImageURL", postBeans.get(pos_final)
											.getImageUrl());
									bundle.putCharSequence("author", postBeans.get(pos_final)
											.getAuthor());
									bundle.putCharSequence("created_at", postBeans.get(pos_final)
											.getCreatedAt());
									bundle.putCharSequence("updated_at", postBeans.get(pos_final)
											.getUpdatedAt());
									intent.putExtras(bundle);
									startActivity(intent);
									overridePendingTransition(R.anim.base_slide_right_in,
											R.anim.base_slide_remain);
								}
							});
					alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "�ٵȵ�",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface arg0, int arg1) {
								}
							});
					alertDialog.show();
				} else if (postBeans.get(pos).getContentType().equals("special")) {
					Toast.makeText(getApplicationContext(), "ר������", Toast.LENGTH_SHORT).show();

					Intent intent = new Intent(SectionContentActivity.this, SpecialActivity.class);
					Bundle bundle = new Bundle();
					bundle.putInt("id", postBeans.get(pos).getId());
					bundle.putCharSequence("content_type", postBeans.get(pos).getContentType());
					bundle.putCharSequence("itemURL", postBeans.get(pos).getItemURL());
					bundle.putCharSequence("title", postBeans.get(pos).getTitle());
					bundle.putCharSequence("description", postBeans.get(pos).getDescription());
					bundle.putCharSequence("ImageURL", postBeans.get(pos).getImageUrl());
					bundle.putCharSequence("author", postBeans.get(pos).getAuthor());
					bundle.putCharSequence("created_at", postBeans.get(pos).getCreatedAt());
					bundle.putCharSequence("updated_at", postBeans.get(pos).getUpdatedAt());
					intent.putExtras(bundle);
					startActivity(intent);
					overridePendingTransition(R.anim.base_slide_right_in, R.anim.base_slide_remain);
				} else {
					Toast.makeText(getApplicationContext(), "δ֪����", Toast.LENGTH_SHORT).show();
				}

			}
		});
	}
}
