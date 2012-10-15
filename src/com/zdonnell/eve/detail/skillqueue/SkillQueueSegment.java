package com.zdonnell.eve.detail.skillqueue;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Typeface;
import android.graphics.Paint.Align;
import android.util.Log;
import android.view.View;

import com.zdonnell.eve.Tools;
import com.zdonnell.eve.api.character.QueuedSkill;

public class SkillQueueSegment extends View
{    	
	private static final long DAY_IN_MILLIS = 24 * 60 * 60 * 1000;
	
	private static final int keyColor = Color.DKGRAY;
		
	private Context context;
	
	int width, height;
	
	private Paint paint;
	
	private int manual_padding = 10;
	
	private boolean queueObtained = false;
	
	private ArrayList<QueuedSkill> skillQueue;
	
	private int skillNumber;
	
	int[] colors;
	
	public SkillQueueSegment(Context context, int[] colors) 
	{
		super(context);
		this.colors = colors;
		this.context = context;
				
		manual_padding = Tools.dp2px(10, context);
		
		paint = new Paint();
		paint.setStyle(Paint.Style.FILL);
	}
	
	/**
	 * Sets th skillQueue to draw from. Forces a redraw.
	 * 
	 * @param skillQueue
	 */
	public void setQueue(ArrayList<QueuedSkill> skillQueue, int skillNumber)
	{
		this.skillQueue = skillQueue;
		this.skillNumber = skillNumber;
		
		queueObtained = true;
		
		invalidate();
	}
	
	/**
	 * Draws the bar, only refreshed when {@link setQueue} is called
	 * 
	 * @param canvas
	 */
	@Override
	protected void onDraw(Canvas canvas)
	{		
		paint.setColor(Color.parseColor("#AFAFAF"));
		//canvas.drawRect(0, 0, width, height, paint);
		
		long timeUntilStart, timeUntilEnd;
		
		timeUntilStart = Tools.timeUntilUTCTime(skillQueue.get(skillNumber).startTime);
		timeUntilEnd = Tools.timeUntilUTCTime(skillQueue.get(skillNumber).endTime);
		
		if (timeUntilStart < 0) timeUntilStart = 0;
		
		paint.setColor(colors[skillNumber % 2]);
		
		/* If the skill fits in the 24 hour period */
		if (timeUntilStart < DAY_IN_MILLIS)
		{
			double percentOfBar = (double) (timeUntilEnd - timeUntilStart) / DAY_IN_MILLIS;
			int widthOfSegment = (int) (percentOfBar * (width - (manual_padding * 2)));
			
			int startOfSegment = manual_padding + (int) (((double) timeUntilStart / (double) DAY_IN_MILLIS) * (width - (manual_padding * 2)));
			int endOfSegment = startOfSegment + widthOfSegment;
			
			if (endOfSegment > width) endOfSegment = width;
			
			canvas.drawRect(startOfSegment, 0, endOfSegment, height, paint);
		}
	}
	
	@Override
    public void onSizeChanged (int w, int h, int oldw, int oldh){
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
    }
}