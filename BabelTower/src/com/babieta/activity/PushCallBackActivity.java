package com.babieta.activity;

import com.babieta.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class PushCallBackActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_push_callback);
		startActivity(new Intent(PushCallBackActivity.this, MainActivity.class));
		finish();
	}
}
