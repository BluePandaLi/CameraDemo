package com.binary.one.camera.demo;

import android.graphics.Rect;

/**
 * Stores Entities and holds constants pertaining to physics and the screen.
 */
public class World {

	private Rect mBoundingBox = null;

	public World(Rect boundingBox) {
		mBoundingBox = boundingBox;
	}

	public static class Constants {
		public static final float GRAVITY = 3.0f;

		public static final float WIDTH = 1920.0f;
		public static final float HEIGHT = 1080.0f;
	}
}
