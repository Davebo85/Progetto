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
	private int[] x = { 650, 0, 335 };
	private int[] y = { 190, 380, 430 };
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
		luci = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
				getResources(), R.drawable.pro_ledon), 104 * 3 / 5,
				152 * 3 / 5, false);
		img[0] = new Immagine(superActivity, R.drawable.pro_cambio, x[0], y[0],
				150, 120, false, -1, 0, true);
		img[1] = new Immagine(superActivity, R.drawable.pro_ledoff, x[1], y[1],
				104 * 3 / 5, 152 * 3 / 5, false, -1, 0, false);
		img[2] = new Immagine(superActivity, R.drawable.pro_volante, x[2],
				y[2], 0, 0, false, -1, 0, false);
		square_color();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawRect(700, 0, 800, 480, myPaint[0]);
		// canvas.drawRect(0, 0, 200, 100, myPaint[1]);
		canvas.drawRect(0, 380, 700, 480, myPaint[2]);

		canvas.drawBitmap(cruscotto, 0, 0, null);
		for (int i = 0; i < NUMBER_IMAGES; i++) {
			Bitmap imm = img[i].getImage(-1);
			switch (i) {
			case 0:
				imm = img[i].getImage(cambio_value);
				canvas.drawBitmap(imm, img[i].x, img[i].y, null);
				break;
			case 1:
				if (led) {
					imm = luci;
					canvas.drawBitmap(imm, img[i].x, img[i].y, null);
				} else {
					canvas.drawBitmap(imm, img[i].x, img[i].y, null);
				}
				break;
			case 2:
				img[i].rot = valueOfRotation;
				canvas.rotate(img[i].rot, x[i], y[i] + 10);
				canvas.drawBitmap(imm, img[i].x - imm.getWidth() / 2, img[i].y
						- imm.getHeight() / 2, null);
				break;
			default:
				break;
			}
		}
	}

	public boolean isOverImage(int index, int x_p, int y_p) {
		int x = img[index].getX();
		int y = img[index].getY();
		int w = img[index].getImage(-1).getWidth();
		int h = img[index].getImage(-1).getHeight();
		if (index == img.length - 1) {
			x = 300;
			y = 380;
			w = 100;
			h = 100;
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
						if (j == img.length - 1) {
							img[j].rot = 0;
						} else if (j == 1) {
							led = !led;
						}						
						if (j == 2) {
							img[j].rot = 0;
						}
						img[j].poi = -1;
						img[j].drag = false;
						invalidate();
					}
					break;
				case MotionEvent.ACTION_MOVE: {
					if (img[j].drag == true
							&& event.getPointerId(i) == img[j].poi) {
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
							break;
						case 2:
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
			valueOfRotation = (valueOfRotation > 100) ? 100 : valueOfRotation;
			valueOfRotation = (valueOfRotation < -100) ? -100 : valueOfRotation;
			invalidate();
		}
	}
}
