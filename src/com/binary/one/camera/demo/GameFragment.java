package com.binary.one.camera.demo;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.TextView;

public class GameFragment extends Fragment implements SurfaceHolder.Callback {

	private static final String TAG = "CameraDemo.GameFragment";
	private static final boolean DEBUG = true;

	private SurfaceView mSurfaceView = null;
	private TextView mDebugTextView = null;

	private GameLogic mGameLogic = null;
	private Thread mGameThread = null;

	private OnTouchListener mTouchListener = new OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (mGameLogic != null) {
				mGameLogic.handleTouchEvent(event);
				return true;
			}
			return false;
		}
	};

	public static GameFragment newInstance() {
		return new GameFragment();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View root = inflater.inflate(R.layout.fragment_game, container, false);
		mSurfaceView = (SurfaceView) root.findViewById(R.id.game_surface);
		mSurfaceView.getHolder().addCallback(this);
		mSurfaceView.setOnTouchListener(mTouchListener);

		mDebugTextView = (TextView) root.findViewById(R.id.game_debug_info);

		return root;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		if (DEBUG) Log.d(TAG, "Surface changed...");
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (DEBUG) Log.d(TAG, "Surface created!");

		mGameLogic = GameLogic.newInstance(getActivity(), holder);
		mGameLogic.addDebugCallback(new GameLogic.DebugCallback() {
			@Override
			public void updateDebugInfo(int screenWidth, int screenHeight, long frameCount) {
				Resources res = getActivity().getResources();
				float dpi = res.getDisplayMetrics().density;
				final String debugInfo = res.getString(R.string.debug_info,
						screenWidth,
						screenHeight,
						frameCount,
						dpi);

				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						mDebugTextView.setText(debugInfo);
					}
				});
			}
		});

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
