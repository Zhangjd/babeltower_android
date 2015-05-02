package com.bbt.babeltower.activity;

import java.util.List;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.LogUtil;
import com.avos.avoscloud.LogUtil.log;
import com.avos.avoscloud.feedback.Comment;
import com.avos.avoscloud.feedback.FeedbackAgent;
import com.avos.avoscloud.feedback.FeedbackThread;
import com.avos.avoscloud.feedback.FeedbackThread.SyncCallback;
import com.avos.avoscloud.feedback.ThreadActivity.ImageCache;
import com.bbt.babeltower.R;
import com.bbt.babeltower.base.MyApplication;
import com.bbt.babeltower.base.S;
import com.bbt.babeltower.base.Util;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class FeedbackActivity extends SwipeBackActivity {

	private RelativeLayout area_switch;
	// private ListView feedbackListView;
	private TextView feedback_submit;
	private TextView feedback_content;
	private TextView feedback_contact;

	private FeedbackThread feedbackThread;
	// private FeedbackAdapter feedbackListAdapter;

	public static final ImageCache cache = new ImageCache(AVOSCloud.applicationContext);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feedback_v2);
		Util.setStatusBarColor(FeedbackActivity.this);

		this.initFeedback();
		this.initEventsRegister();

		feedbackThread.sync(syncCallback);
	}

	private void initEventsRegister() {
		area_switch = (RelativeLayout) findViewById(R.id.header_switch_area);
		feedback_submit = (TextView) findViewById(R.id.feedback_submit);
		feedback_contact = (TextView) findViewById(R.id.feedback_contact);
		feedback_content = (TextView) findViewById(R.id.feedback_content);

		feedback_submit.setVisibility(View.VISIBLE);
		
		TextView titleTextView = (TextView) findViewById(R.id.header_textview);
		titleTextView.setText("意见反馈");

		feedback_contact.setText(S.getString(getApplicationContext(), "feedback_contact"));

		area_switch.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
				overridePendingTransition(0, R.anim.base_slide_right_out);
			}
		});

		feedback_submit.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				log.d("FeedbackActivity", "submit onclick");

				String comment = feedback_content.getText().toString();
				String contact = feedback_contact.getText().toString();

				if (!comment.isEmpty()) {
					feedback_content.setText("");
					feedbackThread.setContact(contact);
					S.put(getApplicationContext(), "feedback_contact", contact);
					Comment userComment = new Comment(comment);
					feedbackThread.add(userComment);
					feedbackThread.sync(syncCallback);
					// feedbackListView.setSelection(feedbackListView.getBottom());
					Util.showToast(FeedbackActivity.this, "感谢你的反馈!");
				} else {
					Util.showToast(FeedbackActivity.this, "请输入反馈内容");
				}
			}
		});
	}

	protected void initFeedback() {
		feedbackThread = MyApplication.feedbackThread;
		// feedbackListAdapter = new FeedbackAdapter(FeedbackActivity.this);
		// feedbackListView = (ListView) findViewById(R.id.feedback_listview);
		// feedbackListView.setAdapter(feedbackListAdapter);
		// feedbackListView.setSelection(feedbackListView.getBottom());
	}

	private SyncCallback syncCallback = new SyncCallback() {

		@Override
		public void onCommentsSend(List<Comment> comments, AVException e) {
			LogUtil.avlog.d("send new comments");
			// feedbackListAdapter.notifyDataSetChanged();
		}

		@Override
		public void onCommentsFetch(List<Comment> comments, AVException e) {
			LogUtil.avlog.d("fetch new comments");
			// feedbackListAdapter.notifyDataSetChanged();
		}
	};
}
