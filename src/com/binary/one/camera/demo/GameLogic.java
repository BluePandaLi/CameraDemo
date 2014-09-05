package com.binary.one.camera.demo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

public class GameLogic implements Runnable {

	private SurfaceHolder mHolder = null;
	private boolean mRunning = true;

	private Camera mCamera = null;

	private final Paint mColorPaint = new Paint();
	private final RectF mBackgroundRect = new RectF();

	private DebugCallback mDebugCallback = null;
	private long mFrameCount = 0l, mLastLogUpdate = 0l;

	public static GameLogic newInstance(Context context, SurfaceHolder holder) {
		return new GameLogic(context, holder);
	}

	private GameLogic(Context context, SurfaceHolder holder) {
		mHolder = holder;

		Rect screen = mHolder.getSurfaceFrame();
		int screenWidth = screen.width();
		int screenHeight = screen.height();

		mCamera = new Camera(context, screenWidth, screenHeight);

	}

	public interface DebugCallback {
		public void updateDebugInfo(int screenWidth, int screenHeight, long frameCount);
	}

	@Override
	public void run() {
		while (mRunning) { performGameLoop(); }
	}

	private void performGameLoop() {
		Canvas canvas = mHolder.lockCanvas();

		if (canvas != null) {
			int width = canvas.getWidth();
			int height = canvas.getHeight();

			mCamera.applyTransform(canvas);

//			int red = (int) (Math.random() * Integer.MAX_VALUE);
//			int green = (int) (Math.random() * Integer.MAX_VALUE);
//			int blue = (int) (Math.random() * Integer.MAX_VALUE);
//			mColorPaint.setColor(Color.rgb(red, green, blue));

			canvas.drawColor(Color.BLACK);

			drawDebugBackground(canvas, World.Constants.WIDTH, World.Constants.HEIGHT);

			mColorPaint.setColor(Color.YELLOW);
			canvas.drawCircle(World.Constants.WIDTH / 2,
					World.Constants.HEIGHT / 2,
					100,
					mColorPaint);

			mCamera.update();

			mHolder.unlockCanvasAndPost(canvas);
		}

		logDebugInfo();
	}

	private void drawDebugBackground(Canvas canvas, float screenWidth, float screenHeight) {
		final int[] colors = new int[] { Color.RED, Color.GREEN, Color.BLUE };
		final float padding = 0f;

		float barWidth = screenWidth / colors.length;

		for (int i=0; i<colors.length; ++i) {
			float start = padding + i * barWidth;
			float end = start + barWidth;

			mBackgroundRect.set(start, 0, end, screenHeight);
			mColorPaint.setColor(colors[i]);

			canvas.drawRect(mBackgroundRect, mColorPaint);
		}
	}

	public void handleTouchEvent(MotionEvent event) {
		mCamera.handleTouchEvent(event);
	}

	public void stop() {
		mRunning = false;
	}

	public void addDebugCallback(DebugCallback callback) {
		mDebugCallback = callback;
	}

	private void logDebugInfo() {
		if (mDebugCallback == null) {
			return;
		}

		long currentTime = System.currentTimeMillis();
		long deltaTime = currentTime - mLastLogUpdate;

		if (deltaTime > 1000l) {
			Rect bounds = mHolder.getSurfaceFrame();
			mDebugCallback.updateDebugInfo(bounds.width(), bounds.height(), mFrameCount);
			mLastLogUpdate = currentTime;
			mFrameCount = 0;
		} else {
			++mFrameCount;
		}
	}
}
