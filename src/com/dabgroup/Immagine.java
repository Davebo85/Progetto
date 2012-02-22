package com.dabgroup;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;

public class Immagine extends BitmapFactory {
	public int image;
	public int x, y;
	public int scaleX, scaleY;
	public boolean drag;
	public int poi;
	public float rot;
	public boolean is_cambio;
	private Bitmap imm;
	private Bitmap[] marcia = new Bitmap[9];

	public Immagine(Context context, int image, int x, int y, int scaleX,
			int scaleY, boolean drag, int poi, int rot, boolean is_cambio) {
		this.image = image;
		this.x = x;
		this.y = y;
		this.scaleX = scaleX;
		this.scaleY = scaleY;
		this.drag = drag;
		this.poi = poi;
		this.rot = rot;
		this.is_cambio = is_cambio;

		imm = BitmapFactory.decodeResource(context.getResources(), image);

		if (scaleX != 0 && scaleY != 0) {
			imm = Bitmap.createScaledBitmap(imm, scaleX, scaleY, false);
		}

		if (is_cambio) {
			marcia[0] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
					context.getResources(), R.drawable.pro_4), scaleX / 3,
					scaleY / 3, false);
			marcia[1] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
					context.getResources(), R.drawable.pro_3), scaleX / 3,
					scaleY / 3, false);
			marcia[2] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
					context.getResources(), R.drawable.pro_2), scaleX / 3,
					scaleY / 3, false);
			marcia[3] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
					context.getResources(), R.drawable.pro_1), scaleX / 3,
					scaleY / 3, false);
			marcia[4] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
					context.getResources(), R.drawable.pro_0), scaleX / 3,
					scaleY / 3, false);
			marcia[5] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
					context.getResources(), R.drawable.pro1), scaleX / 3,
					scaleY / 3, false);
			marcia[6] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
					context.getResources(), R.drawable.pro2), scaleX / 3,
					scaleY / 3, false);
			marcia[7] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
					context.getResources(), R.drawable.pro3), scaleX / 3,
					scaleY / 3, false);
			marcia[8] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
					context.getResources(), R.drawable.pro4), scaleX / 3,
					scaleY / 3, false);
		}
	}

	private Bitmap overlay(Bitmap bmp1, Bitmap bmp2) {
		Bitmap bmOverlay = Bitmap.createBitmap(bmp1.getWidth(),
				bmp1.getHeight(), bmp1.getConfig());
		Canvas canvas = new Canvas(bmOverlay);
		canvas.drawBitmap(bmp1, new Matrix(), null);
		canvas.translate(40, 6);
		canvas.drawBitmap(bmp2, new Matrix(), null);
		return bmOverlay;
	}

	public Bitmap getImage(int value) {
		if (value == -1) {
			return imm;
		} else {
			return overlay(imm, marcia[value]);
		}
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
}
