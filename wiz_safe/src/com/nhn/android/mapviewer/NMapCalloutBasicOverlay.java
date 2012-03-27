/* 
 * NMapCalloutBasicOverlay.java $version 2010. 1. 1
 * 
 * Copyright 2010 NHN Corp. All rights Reserved. 
 * NHN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms. 
 */

package com.nhn.android.mapviewer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.Display;
import android.view.WindowManager;

import com.nhn.android.maps.NMapOverlay;
import com.nhn.android.maps.NMapOverlayItem;
import com.nhn.android.maps.NMapView;
import com.nhn.android.mapviewer.overlay.NMapCalloutOverlay;
import com.wiz.Activity.R;

/**
 * Basic callout overlay
 * 
 * @author kyjkim 
 */
public class NMapCalloutBasicOverlay extends NMapCalloutOverlay {

	private static final int CALLOUT_TEXT_PADDING_X = 10;
	private static final int CALLOUT_TEXT_PADDING_Y = 10;
	private static final int CALLOUT_TAG_WIDTH = 10;
	private static final int CALLOUT_TAG_HEIGHT = 10;
	private static final int CALLOUT_ROUND_RADIUS_X = 5;
	private static final int CALLOUT_ROUND_RADIUS_Y = 5;

	private final Paint mTextPaint;
	private float mOffsetX, mOffsetY;
	//context 추가
	private Context context;

	public NMapCalloutBasicOverlay(NMapOverlay itemOverlay, NMapOverlayItem item, Rect itemBounds, Context con) {
		super(itemOverlay, item, itemBounds);

		context = con;
		
		mTextPaint = new Paint();
		mTextPaint.setARGB(255, 0, 0, 0);
		mTextPaint.setAntiAlias(true);
		mTextPaint.setTextSize(20);
	}

	@Override
	protected boolean isTitleTruncated() {
		return false;
	}

	@Override
	protected int getMarginX() {
		return 10;
	}

	@Override
	protected Rect getBounds(NMapView mapView) {

		adjustTextBounds(mapView);

		mTempRect.set((int)mTempRectF.left, (int)mTempRectF.top, (int)mTempRectF.right, (int)mTempRectF.bottom);
		mTempRect.union(mTempPoint.x, mTempPoint.y);

		return mTempRect;
	}

	@Override
	protected PointF getSclaingPivot() {
		PointF pivot = new PointF();

		pivot.x = mTempRectF.centerX();
		pivot.y = mTempRectF.bottom + CALLOUT_TAG_HEIGHT;

		return pivot;
	}

	@Override
	protected void drawCallout(Canvas canvas, NMapView mapView, boolean shadow, long when) {

		adjustTextBounds(mapView);

		stepAnimations(canvas, mapView, when);

		Bitmap _android = BitmapFactory.decodeResource(context.getResources(), R.drawable.speech_bubble);
		//말풍선 백이미지의 위치를 구한다.
		Display display = ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
	    int viewWidth = display.getWidth();
	    int viewHeight = display.getHeight();
		canvas.drawBitmap(_android, mTempRectF.centerX() - (float)(viewWidth * 0.1), mTempRectF.centerY() - (float)(viewHeight * 0.05), null);
		
		//  Draw title
		canvas.drawText(mOverlayItem.getTitle(), mOffsetX + (float)(viewWidth * 0.07), mOffsetY - (float)(viewHeight * 0.01), mTextPaint);
	}

	/* Internal Functions */

	private void adjustTextBounds(NMapView mapView) {

		//  Determine the screen coordinates of the selected MapLocation
		mapView.getMapProjection().toPixels(mOverlayItem.getPointInUtmk(), mTempPoint);

		final String title = mOverlayItem.getTitle();
		mTextPaint.getTextBounds(title, 0, title.length(), mTempRect);

		//  Setup the callout with the appropriate size & location
		mTempRectF.set(mTempRect);
		mTempRectF.inset(-CALLOUT_TEXT_PADDING_X, -CALLOUT_TEXT_PADDING_Y);
		mOffsetX = mTempPoint.x - mTempRect.width() / 2;
		mOffsetY = mTempPoint.y - mTempRect.height() - mItemBounds.height() - CALLOUT_TEXT_PADDING_Y;
		mTempRectF.offset(mOffsetX, mOffsetY);
	}

	@Override
	protected Drawable getDrawable(int rank, int itemState) {
		// TODO Auto-generated method stub
		return null;
	}
}
