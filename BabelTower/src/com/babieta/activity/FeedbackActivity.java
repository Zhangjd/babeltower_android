package com.babieta.activity;

import java.util.List;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.LogUtil;
import com.avos.avoscloud.LogUtil.log;
import com.avos.avoscloud.feedback.Comment;
import com.avos.avoscloud.feedback.Comment.CommentType;
import com.avos.avoscloud.feedback.FeedbackAgent;
import com.avos.avoscloud.feedback.FeedbackThread;
import com.avos.avoscloud.feedback.FeedbackThread.SyncCallback;
import com.avos.avoscloud.feedback.ThreadActivity.ImageCache;
import com.babieta.R;
import com.babieta.adapter.FeedbackAdapter;
import com.babieta.base.S;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class FeedbackActivity extends SwipeBackActivity {

	private ImageButton backButton;
	private Button feedback_submit;
	private TextView feedback_content;
	private TextView feedback_contact;
	private TextView feedback_log;

	public static FeedbackThread feedbackThread;
	private FeedbackAgent feedbackAgent;
	private FeedbackAdapter feedbackListAdapter;

	public static final ImageCache cache = new ImageCache(AVOSCloud.applicationContext);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feedback);

		TextView titleTextView = (TextView) findViewById(R.id.header_textview);
		titleTextView.setText("意见反馈");

		this.initFeedback();
		this.initEventsRegister();

		feedbackThread.sync(syncCallback);
	}

	private void initEventsRegister() {
		backButton = (ImageButton) findViewById(R.id.back_button);
		feedback_submit = (Button) findViewById(R.id.feedback_submit);
		feedback_contact = (TextView) findViewById(R.id.feedback_contact);
		feedback_content = (TextView) findViewById(R.id.feedback_content);
		feedback_log = (TextView) findViewById(R.id.feedback_textview);

		feedback_contact.setText(S.getString(getApplicationContext(), "feedback_contact"));

		backButton.setOnClickListener(new View.OnClickListener() {

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

				if (!contact.isEmpty()) {
					feedback_content.setText("");
					feedbackThread.setContact(contact);
					S.put(getApplicationContext(), "feedback_contact", contact);
					Comment userComment = new Comment(comment);
					feedbackThread.add(userComment);
					feedbackThread.sync(syncCallback);
					Toast.makeText(FeedbackActivity.this, "感谢你的反馈!", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(FeedbackActivity.this, "请输入反馈内容", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	protected void initFeedback() {
		feedbackAgent = new FeedbackAgent(FeedbackActivity.this);
		feedbackThread = feedbackAgent.getDefaultThread();
		feedbackListAdapter = new FeedbackAdapter(FeedbackActivity.this);
		// feedbackListView = (ListView)
		// findViewById(Resources.id.avoscloud_feedback_thread_list(this));
		// feedbackListView.setAdapter(adapter);
	}

	private SyncCallback syncCallback = new SyncCallback() {

		@Override
		public void onCommentsSend(List<Comment> comments, AVException e) {
			LogUtil.avlog.d("send new comments");
			feedbackListAdapter.notifyDataSetChanged();
		}

		@Override
		public void onCommentsFetch(List<Comment> comments, AVException e) {
			LogUtil.avlog.d("fetch new comments");

			String output = "";
			for (int i = 0; i < comments.size(); i++) {
				Comment comment = comments.get(i);
				if (comment.getCommentType().equals(CommentType.USER)) {
					output += " 我:" + comment.getContent() + "\n";
				} else {
					output += " 回复:" + comment.getContent() + "\n";
				}

			}
			feedback_log.setText(output);

			feedbackListAdapter.notifyDataSetChanged();
		}

	};

}
