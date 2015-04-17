package com.bbt.babeltower.activity;

import com.bbt.babeltower.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class PushCallBackActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_push_callback);
		startActivity(new Intent(PushCallBackActivity.this, MainActivity.class));
		finish();
	}
}
