package com.kukung.app.pk200;

import com.kukung.app.pk200.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

public class SecretNoteForPharmacyActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        
        Thread showingSplashThread = new Thread() {
        	public void run() {
        		super.run();
        		
        		try {
					sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
					Intent intent = new Intent(SecretNoteForPharmacyActivity.this, ViewTocActivity.class);
					startActivity(intent);
					finish();
				}
        	}
        };
        showingSplashThread.start();
    }
}