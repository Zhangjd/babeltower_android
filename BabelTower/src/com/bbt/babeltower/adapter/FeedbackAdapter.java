package com.bbt.babeltower.adapter;

import com.bbt.babeltower.base.MyApplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class FeedbackAdapter extends BaseAdapter {
	Context context;
	LayoutInflater inflater;

	public FeedbackAdapter(Context context) {
		this.context = context;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return MyApplication.feedbackThread.getCommentsList().size();
	}

	@Override
	public Object getItem(int position) {
		return MyApplication.feedbackThread.getCommentsList().get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// Comment comment = (Comment) getItem(position);
		// String content = comment.getContent();
		// TextView textView = null;
		//
		// if (comment.getCommentType().equals(Comment.CommentType.USER)) {
		// convertView =
		// LayoutInflater.from(context).inflate(R.layout.chatlist_user, parent,
		// false);
		// textView = (TextView)
		// convertView.findViewById(R.id.chatlist_text_user);
		// } else if (comment.getCommentType().equals(Comment.CommentType.DEV))
		// {
		// convertView =
		// LayoutInflater.from(context).inflate(R.layout.chatlist_dev, parent,
		// false);
		// textView = (TextView)
		// convertView.findViewById(R.id.chatlist_text_dev);
		// }
		// textView.setText(content);

		return convertView;
	}

}
