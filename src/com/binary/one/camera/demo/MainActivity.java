package com.binary.one.camera.demo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;


public class MainActivity extends FragmentActivity {

	private Fragment mGameFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        switchToFragment();
    }

    private void switchToFragment() {
    	if (mGameFragment == null) {
    		mGameFragment = GameFragment.newInstance();
    	}

    	getSupportFragmentManager()
    		.beginTransaction()
    		.replace(R.id.main_content_frame, mGameFragment)
    		.commit();
    }
}
