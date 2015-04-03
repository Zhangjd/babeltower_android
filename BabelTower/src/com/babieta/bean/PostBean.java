package com.babieta.bean;

import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.avos.avoscloud.LogUtil.log;
import com.babieta.base.ApiUrl;
import com.babieta.base.S;

import android.app.Activity;
import android.content.Context;

//JSON Parser
//Bean������Ӧ�ó����زĵĽ�ɫ
public class PostBean {
	private int id;
	private String text = "";
	private String imageUrl = "";
	private String itemUrl = "";
	private String content_type = "";
	private String description = "";
	private String headerImageUrl = "";
	private String author = "";
	private String created_at = "";
	private String updated_at = "";
	private int views = 0;
	private int like = 0;

	private int image;

	public int getImage() {
		return image;
	}

	public void setImage(int image) {
		this.image = image;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() { // ����
		return text;
	}

	public void setTitle(String text) {
		this.text = text;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getItemURL() {
		return itemUrl;
	}

	public void setItemURL(String itemUrl) {
		this.itemUrl = itemUrl;
	}

	public String getContentType() {
		return content_type;
	}

	public void setContentType(String contentType) {
		this.content_type = contentType;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getCreatedAt() {
		return created_at;
	}

	public void setCreatedAt(String created_at) {
		this.created_at = created_at;
	}

	public String getUpdatedAt() {
		return updated_at;
	}

	public void setUpdatedAt(String updated_at) {
		this.updated_at = updated_at;
	}

	public String getHeaderImageUrl() {
		return headerImageUrl;
	}

	public void setHeaderImageUrl(String headerImageUrl) {
		this.headerImageUrl = headerImageUrl;
	}

	public int getLike() { // ����
		return like;
	}

	public void setLike(int like) {
		this.like = like;
	}

	public int getViews() { // �Ķ���
		return views;
	}

	public void setViews(int views) {
		this.views = views;
	}

	public static LinkedList<PostBean> parse(String json) {
		JSONTokener jsonTokener = new JSONTokener(json);
		LinkedList<PostBean> postBeans = new LinkedList<PostBean>();
		JSONArray jsonArray;
		try {
			jsonArray = (JSONArray) jsonTokener.nextValue();
			for (int i = 0; i < jsonArray.length(); i++) {
				PostBean postBean = new PostBean();
				JSONObject jsonObject = (JSONObject) jsonArray.get(i);
				postBean.setTitle((String) jsonObject.get("title"));
				postBean.setImageUrl(jsonObject.getString("image"));
				postBeans.add(postBean);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return postBeans;
	}

	// parse �ͱ�����������
	public static LinkedList<PostBean> parseBabietaTimeline(String json, Context c) {
		JSONTokener jsonTokener = new JSONTokener(json); // JSON������
		LinkedList<PostBean> postBeans = new LinkedList<PostBean>();
		JSONArray jsonArray;

		try {
			JSONObject jsonObject = (JSONObject) jsonTokener.nextValue();
			jsonArray = jsonObject.getJSONArray("list"); // �����б�

			for (int i = 0; i < jsonArray.length(); i++) {
				PostBean postBean = new PostBean();
				JSONObject mJsonObject = jsonArray.getJSONObject(i);

				if (mJsonObject.has("display_on_timeline")
						&& mJsonObject.getString("display_on_timeline") == "false")
					continue;

				// ��ÿ�����ŵ�������ӵ�һ��PostBean������
				postBean.setId(((Integer) mJsonObject.get("id")).intValue());
				postBean.setTitle(mJsonObject.getString("title"));
				postBean.setItemURL(ApiUrl.BABIETA_BASE_URL + ApiUrl.BABIETA_ARTICLE
						+ mJsonObject.getString("id"));
				postBean.setImageUrl(mJsonObject.getString("thumb_image_url"));
				postBean.setContentType(mJsonObject.getString("content_type"));
				postBean.setLike(mJsonObject.getInt("like"));
				postBean.setViews(mJsonObject.getInt("views"));
				if (!mJsonObject.getString("description").isEmpty())
					postBean.setDescription(mJsonObject.getString("description"));
				else
					postBean.setDescription("null");// ȡһ����ע��
				postBean.setCreatedAt(mJsonObject.getString("created_at"));
				postBean.setUpdatedAt(mJsonObject.getString("updated_at"));
				postBean.setAuthor(mJsonObject.getJSONObject("author").getString("display_name"));
				if (mJsonObject.has("header_image_url"))
					postBean.setHeaderImageUrl(mJsonObject.getString("header_image_url"));

				postBeans.add(postBean); // ��ӵ�����
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return postBeans;
	}

	// parse �ͱ������������ (δ����)
	public static LinkedList<PostBean> parseBabietaFocus(String json) {
		JSONTokener jsonTokener = new JSONTokener(json); // JSON������
		LinkedList<PostBean> postBeans = new LinkedList<PostBean>();
		JSONArray jsonArray;
		try {
			JSONObject jsonObject = (JSONObject) jsonTokener.nextValue();
			jsonArray = jsonObject.getJSONArray("list"); // �����б�
			for (int i = 0; i < jsonArray.length(); i++) {
				PostBean postBean = new PostBean();
				JSONObject mJsonObject = (JSONObject) jsonArray.get(i);

				String on_focus = mJsonObject.getString("on_focus");
				if (on_focus == "false")
					continue;

				postBean.setTitle((String) mJsonObject.get("title"));
				postBean.setItemURL(ApiUrl.BABIETA_BASE_URL + ApiUrl.BABIETA_ARTICLE
						+ mJsonObject.getString("id"));
				postBean.setImageUrl(ApiUrl.BABIETA_BASE_URL
						+ mJsonObject.getString("thumb_image_url"));
				postBeans.add(postBean); // ��ӵ�����
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return postBeans;
	}

	// parse�ղص�����
	public static LinkedList<PostBean> parseCollectContents(Activity activity) {
		LinkedList<PostBean> postBeans = new LinkedList<PostBean>();
		String[] collectlist = S.getStringSet(activity, "collected_list");

		for (int i = 0; i < (collectlist.length); i++) {
			log.d("collectlist", i + " : " + collectlist[i]);
		}

		for (int i = 1; i < (collectlist.length);) {
			PostBean postBean = new PostBean();

			postBean.setId(Integer.valueOf(collectlist[i]));
			postBean.setItemURL(collectlist[i + 1]);
			postBean.setContentType(collectlist[i + 2]);
			postBean.setImageUrl(collectlist[i + 3]);
			postBean.setHeaderImageUrl(collectlist[i + 3]); //the same
			postBean.setTitle(collectlist[i + 4]);
			postBean.setDescription(collectlist[i + 5]);
			postBean.setAuthor(collectlist[i + 6]);
			postBean.setCreatedAt(collectlist[i + 7]);
			postBean.setUpdatedAt(collectlist[i + 8]);

			postBeans.add(postBean); // ��ӵ�����
			i = i + 9;
		}
		return postBeans;
	}

	// parse �ͱ����������� && ר������
	public static LinkedList<PostBean> parseSection(String json, Context c) {
		JSONTokener jsonTokener = new JSONTokener(json); // JSON������
		LinkedList<PostBean> postBeans = new LinkedList<PostBean>();
		JSONArray jsonArray;

		try {
			JSONObject jsonObject = (JSONObject) jsonTokener.nextValue();
			jsonArray = jsonObject.getJSONArray("list"); // �����б�
			for (int i = 0; i < jsonArray.length(); i++) {
				PostBean postBean = new PostBean();
				JSONObject mJsonObject = jsonArray.getJSONObject(i);

				// ��ÿ�����ŵ�������ӵ�һ��PostBean������
				postBean.setId(((Integer) mJsonObject.get("id")).intValue());
				postBean.setTitle(mJsonObject.getString("title"));
				postBean.setItemURL(ApiUrl.BABIETA_BASE_URL + ApiUrl.BABIETA_ARTICLE
						+ mJsonObject.getString("id"));
				postBean.setImageUrl(mJsonObject.getString("thumb_image_url"));
				postBean.setContentType(mJsonObject.getString("content_type"));
				postBean.setLike(mJsonObject.getInt("like"));
				postBean.setViews(mJsonObject.getInt("views"));
				if (!mJsonObject.getString("description").isEmpty())
					postBean.setDescription(mJsonObject.getString("description"));
				else
					postBean.setDescription("null");// ȡһ����ע��
				postBean.setCreatedAt(mJsonObject.getString("created_at"));
				postBean.setUpdatedAt(mJsonObject.getString("updated_at"));
				postBean.setAuthor(mJsonObject.getJSONObject("author").getString("display_name"));
				if (mJsonObject.has("header_image_url"))
					postBean.setHeaderImageUrl(mJsonObject.getString("header_image_url"));

				postBeans.add(postBean); // ��ӵ�����
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return postBeans;
	}
}
