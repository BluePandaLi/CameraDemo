package com.binary.one.camera.demo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

public class GameFragment extends Fragment implements SurfaceHolder.Callback {

	private static final String TAG = "CameraDemo.GameFragment";
	private static final boolean DEBUG = true;

	private SurfaceView mSurfaceView = null;
	private GameLogic mGameLogic = null;
	private Thread mGameThread = null;

	public static GameFragment newInstance() {
		return new GameFragment();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View root = inflater.inflate(R.layout.fragment_game, container, false);
		mSurfaceView = (SurfaceView) root.findViewById(R.id.game_surface);
		mSurfaceView.getHolder().addCallback(this);

		return root;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		if (DEBUG) Log.d(TAG, "Surface changed...");
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (DEBUG) Log.d(TAG, "Surface created!");

		mGameLogic = GameLogic.newInstance(holder);
		mGameThread = new Thread(mGameLogic);
		mGameThread.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		if (DEBUG) Log.d(TAG, "Surface destroyed!");

        boolean retry = true;
        mGameLogic.stop();
        while (retry) {
            try {
                mGameThread.join();
                retry = false;
            } catch (InterruptedException e) {}
        }
	}

}
