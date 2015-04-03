package com.babieta.activity;

import com.babieta.R;

import android.app.AlertDialog;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class AboutActivity extends SwipeBackActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);

		TextView titleTextView = (TextView) findViewById(R.id.header_textview);
		titleTextView.setText("关于巴别塔");

		this.initEventsRegister();
	}

	private void initEventsRegister() {
		final ImageButton backButton = (ImageButton) findViewById(R.id.back_button);
		final TextView about_license = (TextView) findViewById(R.id.about_license);

		about_license.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);

		backButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
				overridePendingTransition(0, R.anim.base_slide_right_out);
			}
		});

		about_license.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				ContextThemeWrapper themedContext;
				themedContext = new ContextThemeWrapper(AboutActivity.this,
						android.R.style.Theme_Holo_Light_Dialog_NoActionBar);
				AlertDialog.Builder builder = new AlertDialog.Builder(themedContext);
				builder.setMessage(R.string.open_source_licenses).setPositiveButton("确定", null)
						.show();
			}
		});
	}
}
