package com.dabgroup;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;

class MySensorListener implements SensorEventListener {
	public float valueOfRotation;
	
	public void onAccuracyChanged(Sensor sensor, int accurancy) {
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		float[] f = event.values;
		for (int i = 0; i < 1; i++) {
			Log.d("SENSORE","Valore " + i + " :" + " " + f[i]);
			if (f[i] > 110 && f[i] <= 150) {
				valueOfRotation = 0;
			} else if (f[i] > 80 && f[i] <= 110) {
				valueOfRotation = -45;
			} else if (f[i] > 30 && f[i] <= 80) {
				valueOfRotation = -90;
			} else if (f[i] > 150 && f[i] <= 200) {
				valueOfRotation = 45;
			} else if (f[i] > 200 && f[i] <= 250) {
				valueOfRotation = 90;
			}
		}
	}
	
	public float value_return() {
		return valueOfRotation;
	}

}