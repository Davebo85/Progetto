package com.dabgroup;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

public class Numbers extends View {
	public String value = "";
	public int spaces = 0;
	public Bitmap distance_sfondo;
	private int[] R_nums = { R.drawable.numbers_0, R.drawable.numbers_1,
			R.drawable.numbers_2, R.drawable.numbers_3, R.drawable.numbers_4,
			R.drawable.numbers_5, R.drawable.numbers_6, R.drawable.numbers_7,
			R.drawable.numbers_8, R.drawable.numbers_9, R.drawable.numbers_null};
	public Bitmap[] nums = new Bitmap[R_nums.length];
	public Numbers(DriveDroid activity) {
		super(activity);
		for (int i = 0; i < R_nums.length; i++) {
			
			nums[i] = BitmapFactory.decodeResource(activity.getResources(),
					R_nums[i]);
		}
		distance_sfondo = BitmapFactory.decodeResource(activity.getResources(), R.drawable.numbers_sfondo);
		setDistance("0");
	}

	public void setDistance(String d) {
		int i = d.length();
		switch (i) {
		case 1:
			value = " " + " " + d.charAt(0);
			break;
		case 2:
			value = " " + d.charAt(0) + d.charAt(1);
			break;
		case 3:
			value = d;
			break;
		}
		spaces = 3-i;
		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		Paint paintY = new Paint();
		int startx = 200;
		int starty = 0;
		canvas.drawBitmap(distance_sfondo, startx, starty, paintY);
		int[] original = new int[value.length()];
		 for (int i = original.length - 1; i >= 0 ; i--)
		 {
			 original[i] = Character.digit(value.charAt(i), 10);
			 if (original[i] == -1)
			 {
				 canvas.drawBitmap(nums[nums.length-1], startx + 13 + (i * 69), starty + 7, paintY);
			 } else {
			 canvas.drawBitmap(nums[original[i]], startx + 13 + (i * 69), starty + 7, paintY);
			 }
			 }
	}
}
