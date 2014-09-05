package com.binary.one.camera.demo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

public class Camera {

	private static final boolean DEBUG = true;
	private static final String TAG = "CameraDemo.Camera";

	private static enum Direction {
		INCREASE, DECREASE
	}

	private static final float X_DELTA = 2.5f;
	private static final float Y_DELTA = 2.5f;
	private static final float ZOOM_DELTA = 0.01f;

	private int mScreenWidth, mScreenHeight;

	private Matrix mBaseMatrix = new Matrix();

	private float mCurrentZoom = 1f, mTargetZoom, mMinZoom;
	private Direction mZoomDir;

	private float mCurrentX = 0f, mTargetX;
	private Direction mDirX = null;

	private float mCurrentY = 0f, mTargetY;
	private Direction mDirY = null;

	private float mLastTouchX = 0l, mLastTouchY;

	private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mCurrentZoom *= detector.getScaleFactor();

            mCurrentZoom = mTargetZoom =
            		Math.max(mMinZoom, Math.min(mCurrentZoom, mMinZoom + 2.0f));

            mZoomDir = null;
            return true;
        }
    }

	private ScaleGestureDetector mScaleDetector = null;

	public Camera(Context context, int screenWidth, int screenHeight) {
		mScreenWidth = screenWidth;
		mScreenHeight = screenHeight;

		mCurrentZoom = mTargetZoom = mMinZoom = screenHeight / World.Constants.HEIGHT;

		mScaleDetector = new ScaleGestureDetector(context, new  ScaleListener());

		float zoomWidth = screenWidth / World.Constants.WIDTH;
		float zoomHeight = screenHeight / World.Constants.HEIGHT;

		if (DEBUG) Log.d(TAG, String.format("Zooms: %.2f (Width) %.2f (Height) \n" +
				"Screen: %d %d \n" +
				"World: %.2f %.2f \n" +
				"World (W-Zoom): %.2f %.2f \n" +
				"World (H-Zoom): %.2f %.2f",
				zoomWidth, zoomHeight,
				screenWidth, screenHeight,
				World.Constants.WIDTH, World.Constants.HEIGHT,
				World.Constants.WIDTH * zoomWidth, World.Constants.HEIGHT * zoomWidth,
				World.Constants.WIDTH * zoomHeight, World.Constants.HEIGHT * zoomHeight));

		setX(World.Constants.WIDTH / 3);
		setZoom(0.5f);
	}

	public void applyTransform(Canvas canvas) {
		mBaseMatrix.setTranslate(mCurrentX, mCurrentY);
		mBaseMatrix.postScale(mCurrentZoom, mCurrentZoom);
		canvas.setMatrix(mBaseMatrix);
	}

	public void handleTouchEvent(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();

		mScaleDetector.onTouchEvent(event);

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mLastTouchX = x;
			mLastTouchY = y;

			mDirX = mDirY = null;
			return;
		default:
			if (mScaleDetector.isInProgress()) {
				return;
			}

			mCurrentX += (x - mLastTouchX) / mCurrentZoom;
			mCurrentY += (y - mLastTouchY) / mCurrentZoom;
			mLastTouchX = x;
			mLastTouchY = y;

			preformBoundsCheck();
			return;
		}
	}

	public void setX(float newValue) {
		mTargetX = -1 * newValue;
		mDirX = mTargetX - mCurrentX > 0 ? Direction.INCREASE : Direction.DECREASE;
	}

	public void setY(float newValue) {
		mTargetY = newValue;
		mDirY = mTargetY - mCurrentY > 0 ? Direction.INCREASE : Direction.DECREASE;
	}

	public void setZoom(float newValue) {
		mTargetZoom = newValue;
		mZoomDir = mTargetZoom - mCurrentZoom > 0 ? Direction.INCREASE : Direction.DECREASE;
	}

	public void update() {
		updateX();
//		updateY();
		updateZoom();

		preformBoundsCheck();
	}

	private void preformBoundsCheck() {
		checkWithinBoundsX();
		checkWithinBoundsY();
		checkWithinBoundsZoom();
	}

	private void checkWithinBoundsX() {
		if (World.Constants.WIDTH * mCurrentZoom <= mScreenWidth) {
			mCurrentX = 0;
			return;
		}

		if (mCurrentX > 0) {
			mCurrentX = 0;
			return;
		}

		float lastViewablePortion = World.Constants.WIDTH - (mScreenWidth / mCurrentZoom);

		if (DEBUG) Log.d(TAG, String.format("Bound mCurrentX: %.2f", lastViewablePortion));

		if (mCurrentX < -lastViewablePortion) {
			mCurrentX = -lastViewablePortion;
			return;
		}
	}

	private void checkWithinBoundsY() {
		if (World.Constants.HEIGHT * mCurrentZoom <= mScreenHeight) {
			mCurrentY = 0;
			return;
		}

		if (mCurrentY > 0) {
			mCurrentY = 0;
			return;
		}

		float lastViewablePortion = World.Constants.HEIGHT - (mScreenHeight / mCurrentZoom);

		if (DEBUG) Log.d(TAG, String.format("Bound mCurrentY: %.2f", lastViewablePortion));

		if (mCurrentY < -lastViewablePortion) {
			mCurrentY = -lastViewablePortion;
			return;
		}
	}

	private void checkWithinBoundsZoom() {
		if (mCurrentZoom < mMinZoom) {
			mCurrentZoom = mMinZoom;
		}
	}

	private void updateX() {
		if ((mCurrentX < mTargetX && mDirX == Direction.INCREASE) ||
				(mCurrentX > mTargetX && mDirX == Direction.DECREASE)) {

			int direction = getDirection(mTargetX, mCurrentX);
			mCurrentX += direction * X_DELTA;
		}
	}

	private void updateY() {
		if ((mCurrentY < mTargetY && mDirY == Direction.INCREASE) ||
				(mCurrentY > mTargetY && mDirY == Direction.DECREASE)) {

			int direction = getDirection(mTargetY, mCurrentY);
			mCurrentY += direction * Y_DELTA;
		}
	}

	private void updateZoom() {
		if ((mCurrentZoom < mTargetZoom && mZoomDir == Direction.INCREASE) ||
				(mCurrentZoom > mTargetZoom && mZoomDir == Direction.DECREASE)) {

			int direction = getDirection(mTargetZoom, mCurrentZoom);
			mCurrentZoom += direction * ZOOM_DELTA;

			if (DEBUG) Log.d(TAG,
					String.format("Current: %.2f Target: %.2f Direction: %d",
							mCurrentZoom,
							mTargetZoom,
							direction));
		}
	}

	private static int getDirection(float to, float from) {
		float delta = to - from;
		return (int) (delta / Math.abs(delta));
	}
}
