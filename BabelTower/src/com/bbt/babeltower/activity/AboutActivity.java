package com.bbt.babeltower.activity;

import com.bbt.babeltower.R;
import com.bbt.babeltower.base.Util;
import com.gc.materialdesign.widgets.Dialog;

import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class AboutActivity extends SwipeBackActivity {
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		Util.setStatusBarColor(AboutActivity.this);

		this.initEventsRegister();

		// 动态设置Logo上下边距
		WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		int screenHeight = wm.getDefaultDisplay().getHeight();
		int screenWidth = wm.getDefaultDisplay().getWidth();
		ImageView logo = (ImageView) findViewById(R.id.about_logo);
		LayoutParams lp = (LayoutParams) logo.getLayoutParams();
		lp.setMargins(
				0,
				(int) (screenHeight * 186 / 1280.0) + Util.dip2px(AboutActivity.this, (float) 48.4),
				0, (int) (screenHeight * 94 / 1280.0));
		lp.height = (int) (178 / 1280.0 * screenHeight);
		lp.width = (int) (158 / 720.0 * screenWidth);
		logo.setLayoutParams(lp);

		TextView about_rights_reserved = (TextView) findViewById(R.id.about_rights_reserved);
		LayoutParams lp2 = (LayoutParams) about_rights_reserved.getLayoutParams();
		lp2.setMargins(0, 0, 0, (int) (screenHeight * 46 / 1280.0));
		about_rights_reserved.setLayoutParams(lp2);

		TextView about_copyright = (TextView) findViewById(R.id.about_copyright);
		LayoutParams lp3 = (LayoutParams) about_copyright.getLayoutParams();
		lp3.setMargins(0, (int) (screenHeight * 58 / 1280.0), 0, 0);
		about_copyright.setLayoutParams(lp3);
	}

	private void initEventsRegister() {
		final ImageButton backButton = (ImageButton) findViewById(R.id.back_button);
		final TextView about_license = (TextView) findViewById(R.id.about_license);
		final TextView about_version = (TextView) findViewById(R.id.about_version);
		final TextView titleTextView = (TextView) findViewById(R.id.header_textview);

		titleTextView.setText("关于巴别塔");
		titleTextView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
				overridePendingTransition(0, R.anim.base_slide_right_out);
			}
		});

		// open source licenses 加下划线
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
				Dialog materialDialog = new Dialog(AboutActivity.this, "Open Source Licenses",
						getString(R.string.open_source_licenses));
				materialDialog.show();
			}
		});

		about_version.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Dialog materialDialog = new Dialog(AboutActivity.this, "Version history",
						getString(R.string.version_history));
				materialDialog.show();

			}
		});
	}
}
