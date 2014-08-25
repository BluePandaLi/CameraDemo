package com.binary.one.camera.demo;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.SurfaceHolder;

public class GameLogic implements Runnable {

	private SurfaceHolder mHolder = null;
	private boolean mRunning = true;

	Paint mColorPaint = new Paint();
	private long mLastDrawUpdate = 0l;

	public static GameLogic newInstance(SurfaceHolder holder) {
		return new GameLogic(holder);
	}

	private GameLogic(SurfaceHolder holder) {
		mHolder = holder;
	}

	@Override
	public void run() {
		while (mRunning) { performGameLoop(); }
	}

	private void performGameLoop() {
		long currentTime = System.currentTimeMillis();
		if (mLastDrawUpdate == 0l || currentTime - mLastDrawUpdate > 2000l) {

			Canvas canvas = mHolder.lockCanvas();

			if (canvas != null) {
				int width = canvas.getWidth();
				int height = canvas.getHeight();

				int red = (int) (Math.random() * Integer.MAX_VALUE);
				int green = (int) (Math.random() * Integer.MAX_VALUE);
				int blue = (int) (Math.random() * Integer.MAX_VALUE);
				mColorPaint.setColor(Color.rgb(red, green, blue));

				canvas.drawCircle(width / 2, height / 2, width / 6, mColorPaint);
				mHolder.unlockCanvasAndPost(canvas);
			}

			mLastDrawUpdate = currentTime;
		}
	}

	public void stop() {
		mRunning = false;
	}
}
