package com.dabgroup;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class MyView1 extends View {
	private String TAG = "PIPPO";
	private int DEFAULT_W = 480;
	private int DEFAULT_H = 800;
	private int SCALE_X = 100;
	private int SCALE_Y = 100;
	private int width;
	private int height;
	private int[] x = { 180, 380, 50 };
	private int[] y = { 650, 0, 335 };
	private int NUMBER_IMAGES = 3;
	private Immagine[] img = new Immagine[3];
	private int value = 4;

	private Paint[] myPaint = new Paint[3];

	private Bitmap cruscotto;
	private Bitmap luci;
	private boolean led = false;

	public MyView1(Context context) {
		super(context);
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;

		cruscotto = BitmapFactory.decodeResource(getResources(),
				R.drawable.pro_cruscotto2);
		luci = BitmapFactory.decodeResource(getResources(),
				R.drawable.pro_ledoff);
		img[0] = new Immagine(context, R.drawable.pro_cambio, x[0], y[0], 120,
				150, false, -1, 0, true);
		img[1] = new Immagine(context, R.drawable.ic_launcher, x[1], y[1], 100,
				100, false, -1, 0, false);
		img[2] = new Immagine(context, R.drawable.pro_volante, x[2], y[2], 0,
				0, false, -1, 0, false);
		square_color();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawRect(0, 700, 480, 800, myPaint[0]);
		canvas.drawRect(380, 0, 480, 200, myPaint[1]);
		canvas.drawRect(0, 0, 100, 600, myPaint[2]);

		canvas.drawBitmap(cruscotto, 0, 0, null);
		// canvas.drawBitmap(immagine, 100,100, null);
		// canvas.drawBitmap(luci, 0, 0, null);
		for (int i = 0; i < NUMBER_IMAGES; i++) {
			Bitmap imm = img[i].getImage(-1);
			if (i == 0) {
				imm = img[i].getImage(value);
			}
			if (i != 2) {
				canvas.drawBitmap(imm, img[i].x, img[i].y, null);
			} else {
				canvas.rotate(img[i].rot, x[i], y[i]);
				canvas.drawBitmap(imm, img[i].x - imm.getWidth() / 2, img[i].y
						- imm.getHeight() / 2, null);
			}
		}
		// canvas.drawRect(0, 700, 480, 800, myPaint[0]);
	}

	public boolean isOverImage(int index, int x_p, int y_p) {
		int x = img[index].getX();
		int y = img[index].getY();
		int w = img[index].getImage(-1).getWidth();
		int h = img[index].getImage(-1).getHeight();
		if (index == 2) {
			x = 0;
			y = 70;
			w = 100;
			h = 600;
		}
		if (x_p > x && x_p < x + w && y_p > y && y_p < y + h) {
			return true;
		}
		return false;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		for (int i = 0; i < event.getPointerCount(); ++i) {
			for (int j = 0; j < NUMBER_IMAGES; j++) {
				int action = event.getAction();
				int id = event.getPointerId(i);
				int x_p = (int) event.getX(i);
				int y_p = (int) event.getY(i);

				switch (event.getAction() & event.ACTION_MASK) {
				case MotionEvent.ACTION_DOWN:
				case MotionEvent.ACTION_POINTER_DOWN: {
					if (isOverImage(j, x_p, y_p)) {
						img[j].poi = id;
						Log.d(TAG, "id DOWN " + id);
						img[j].drag = true;
					}
					break;
				}
				case MotionEvent.ACTION_UP:
				case MotionEvent.ACTION_POINTER_UP:
					if (img[j].poi == action >> MotionEvent.ACTION_POINTER_ID_SHIFT) {
						if (j == 2) {
							img[j].rot = 0;
						}
						Log.d(TAG,
								"id UP --> evento = " + event.getPointerId(i)
										+ " poi = " + img[j].poi);
						img[j].poi = -1;
						img[j].drag = false;
						invalidate();
					}
					break;
				case MotionEvent.ACTION_MOVE: {
					if (img[j].drag == true
							&& event.getPointerId(i) == img[j].poi) {
						Log.d(TAG,
								"id MOVE --> evento = " + event.getPointerId(i)
										+ " poi = " + img[j].poi);
						switch (j) {
						case 0:
							img[0].x = x_p - img[0].getImage(-1).getWidth() / 2;
							Log.d("TTTTTTTTTTTT", "    x = " + img[0].x);
							if (x_p > 212 && x_p < 268) {
								value = 4;
							} else if (x_p <= 212 && x_p > 156) {
								value = 3;
							} else if (x_p <= 156 && x_p > 100) {
								value = 2;
							} else if (x_p <= 100 && x_p > 44) {
								value = 1;
							} else if (x_p <= 44) {
								value = 0;
							} else if (x_p >= 268 && x_p < 324) {
								value = 5;
							} else if (x_p >= 324 && x_p < 380) {
								value = 6;
							} else if (x_p >= 380 && x_p < 436) {
								value = 7;
							} else if (x_p >= 436) {
								value = 8;
							}
							break;
						case 1:
							img[j].y = y_p - img[j].getImage(-1).getHeight()
									/ 2;
							break;
						case 2:
							if (y_p > 70 && y_p < 600) {
								int tty = y_p - 335;
								img[j].rot = (float) (tty / 6 * (Math.PI));
								Log.d(TAG, " ANGOLO = " + img[j].rot);
							} else {
								img[j].rot = 0;
							}
							break;
						default:
							break;
						}
					}
				}
					invalidate();
					break;
				}
			}
		}
		return true;
	}

	private void square_color() {
		// TODO Auto-generated method stub
		myPaint[0] = new Paint();
		myPaint[0].setColor(Color.rgb(255, 0, 0));
		myPaint[0].setStrokeWidth(1);
		myPaint[1] = new Paint();
		myPaint[1].setColor(Color.rgb(0, 255, 0));
		myPaint[1].setStrokeWidth(1);
		myPaint[2] = new Paint();
		myPaint[2].setColor(Color.rgb(0, 0, 255));
		myPaint[2].setStrokeWidth(1);
	}
	
}
