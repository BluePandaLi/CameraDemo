package com.binary.one.camera.demo;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.Log;

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

	private float mCurrentZoom = 1f, mTargetZoom;
	private Direction mZoomDir;

	private float mCurrentX = 0f, mTargetX;
	private Direction mDirX;

	private float mCurrentY = 0f, mTargetY;
	private Direction mDirY;

	public Camera(int screenWidth, int screenHeight) {
		mScreenWidth = screenWidth;
		mScreenHeight = screenHeight;

		mCurrentZoom = mTargetZoom = Math.max(screenHeight / World.Constants.HEIGHT,
				screenWidth / World.Constants.WIDTH);

		setX(200);
	}

	public void applyTransform(Canvas canvas) {
		mBaseMatrix.setTranslate(mCurrentX, mCurrentY);
		mBaseMatrix.postScale(mCurrentZoom, mCurrentZoom);
		canvas.setMatrix(mBaseMatrix);
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
	}

	private void checkWithinBoundsX() {
		float boundryCheck = -mCurrentX;

		if (boundryCheck < 0) {
			mCurrentX = 0;
		} else if (boundryCheck > World.Constants.WIDTH - mScreenWidth) {
			mCurrentX = -1 * (World.Constants.WIDTH - mScreenWidth);
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
