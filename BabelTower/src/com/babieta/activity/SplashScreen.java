package com.babieta.activity;

import com.babieta.R;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;

//��������
public class SplashScreen extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_screen);
		
		//Display the current version number
        PackageManager pm = getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo("com.babieta", 0);
            System.out.println(pi.versionName);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        
		Handler x = new Handler();  
        x.postDelayed(new splashhandler(), 1000);  
	}
	
	class splashhandler implements Runnable{    
        public void run() {  
            startActivity(new Intent(getApplication(),MainActivity.class));  
            SplashScreen.this.finish();  
        }    
    }  
}
