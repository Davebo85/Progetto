package com.dabgroup;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class MyView2 extends View implements SensorEventListener {

	private DriveDroid superActivity;
	private String TAG = "PIPPO";
	private int[] x = { 650, 50, 335 };
	private int[] y = { 190, 0, 430 };
	private int NUMBER_IMAGES = 3;
	private Immagine[] img = new Immagine[3];
	private int cambio_value = 4;
	private int valueOfRotation = 0;

	private Paint[] myPaint = new Paint[3];

	private Bitmap cruscotto;
	private Bitmap luci;
	private boolean led = false;

	public SensorManager manager;
	public Sensor s;
	public SensorEventListener sens = this;

	public MyView2(DriveDroid activity) {
		super(activity);
		superActivity = activity;
		// Inizializzo sensore
		manager = (SensorManager) activity
				.getSystemService(Context.SENSOR_SERVICE);
		List<Sensor> list = manager.getSensorList(Sensor.TYPE_ORIENTATION);
		s = list.get(0);
		manager.registerListener(sens, s, SensorManager.SENSOR_DELAY_FASTEST);

		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;

		cruscotto = BitmapFactory.decodeResource(getResources(),
				R.drawable.pro_cruscotto2);
		luci = BitmapFactory.decodeResource(getResources(),
				R.drawable.pro_ledoff);
		img[0] = new Immagine(superActivity, R.drawable.pro_cambio, x[0], y[0], 150,
				120, false, -1, 0, true);
		img[1] = new Immagine(superActivity, R.drawable.ic_launcher, x[1], y[1], 100,
				100, false, -1, 0, false);
		img[2] = new Immagine(superActivity, R.drawable.pro_volante, x[2], y[2], 0,
				0, false, -1, 0, false);
		square_color();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawRect(700, 0, 800, 480, myPaint[0]);
		canvas.drawRect(0, 0, 200, 100, myPaint[1]);
		canvas.drawRect(0, 380, 700, 480, myPaint[2]);

		canvas.drawBitmap(cruscotto, 0, 0, null);
		// canvas.drawBitmap(immagine, 100,100, null);
		// canvas.drawBitmap(luci, 0, 0, null);
		for (int i = 0; i < NUMBER_IMAGES; i++) {
			Bitmap imm = img[i].getImage(-1);
			if (i == 0) {
				imm = img[i].getImage(cambio_value);
			}
			if (i != 2) {
				canvas.drawBitmap(imm, img[i].x, img[i].y, null);
			} else {
				img[i].rot = valueOfRotation;
				canvas.rotate(img[i].rot, x[i], y[i] + 10);
				canvas.drawBitmap(imm, img[i].x - imm.getWidth() / 2, img[i].y
						- imm.getHeight() / 2, null);
			}
		}
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
						// Log.d(TAG, "id DOWN " + id);
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
						// Log.d(TAG,
						// "id MOVE --> evento = " + event.getPointerId(i)
						// + " poi = " + img[j].poi);
						switch (j) {
						case 0:
							img[0].y = y_p - img[0].getImage(-1).getWidth() / 2;
							Log.d("TTTTTTTTTTTT", "    x = " + img[0].x);
							if (y_p > 212 && y_p < 268) {
								cambio_value = 4;
							} else if (y_p <= 212 && y_p > 156) {
								cambio_value = 5;
							} else if (y_p <= 156 && y_p > 100) {
								cambio_value = 6;
							} else if (y_p <= 100 && y_p > 44) {
								cambio_value = 7;
							} else if (y_p <= 44) {
								cambio_value = 8;
							} else if (y_p >= 268 && y_p < 324) {
								cambio_value = 3;
							} else if (y_p >= 324 && y_p < 380) {
								cambio_value = 2;
							} else if (y_p >= 380 && y_p < 436) {
								cambio_value = 1;
							} else if (y_p >= 436) {
								cambio_value = 0;
							}
							break;
						case 1:
							img[j].x = x_p - img[j].getImage(-1).getHeight()
									/ 2;
							break;
						case 2:
							/*
							 * if (y_p > 70 && y_p < 600) { int tty = y_p - 335;
							 * img[j].rot = (float) (tty / 6 * (Math.PI));
							 * Log.d(TAG, " ANGOLO = " + img[j].rot); } else {
							 * img[j].rot = 0; }
							 */
							break;
						default:
							break;
						}
						superActivity.sendMessage("");
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

	@Override
	protected void onDetachedFromWindow() {
		// TODO Auto-generated method stub
		manager.unregisterListener(sens);
		super.onDetachedFromWindow();
	}

	@Override
	protected void onAttachedToWindow() {
		// TODO Auto-generated method stub
		manager.registerListener(sens, s, SensorManager.SENSOR_DELAY_NORMAL);
		super.onAttachedToWindow();
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		float[] f = event.values;
		for (int i = 0; i < 3; i++) {
			Log.d("SENSORE", "Valore " + i + " :" + " " + f[i]);
			if (i == 1) {
				valueOfRotation = (int) -((1.5 * f[i]));
			}
			invalidate();
		}
	}
}
