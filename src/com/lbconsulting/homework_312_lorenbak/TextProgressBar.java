package com.lbconsulting.homework_312_lorenbak;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.ProgressBar;

public class TextProgressBar extends ProgressBar {

	private String text = "";
	private int textColor = Color.BLACK;
	private int textSize_px = 50;
	private int textSize_dp = 15;
	private int textPaddingLeft_dp = 2;
	private int textPaddingLeftPlusRight_px = 2;
	private boolean flag_isFirstTime = true;

	private ProgressBar mProgressBar;
	private Context mContext;

	private Paint textPaint = new Paint();
	private Rect textBounds = new Rect();

	public TextProgressBar(Context context) {
		super(context);
		mContext = context;
	}

	public TextProgressBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
	}

	public TextProgressBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
	}

	@Override
	protected void onAttachedToWindow() {
		// TODO Auto-generated method stub
		MyLog.i("TextProgressBar", "onAttachedToWindow()");
		textSize_px = dpToPx(mContext, textSize_dp);
		textPaddingLeftPlusRight_px = 2 * dpToPx(mContext, textPaddingLeft_dp);

		super.onAttachedToWindow();
	}

	private int resizeText(int textSize) {
		if (flag_isFirstTime) {
			Paint txtPaint = new Paint();
			txtPaint.setAntiAlias(true);
			txtPaint.setTextSize(textSize);
			// get the text size
			Rect textRectangle = new Rect();
			txtPaint.getTextBounds(text, 0, text.length(), textRectangle);
			setPadding(0, 0, 0, textRectangle.height() + 1);

			if (getWidth() - 2 * (textRectangle.height()) < textRectangle.width() + textPaddingLeftPlusRight_px) {
				// the text size is too large ... so reduce it
				while (getWidth() < textRectangle.width() + textPaddingLeftPlusRight_px) {
					textSize--;
					txtPaint.setTextSize(textSize);
					txtPaint.getTextBounds(text, 0, text.length(), textRectangle);
				}

			} else if (getWidth() - 2 * (textRectangle.height()) > textRectangle.width() + textPaddingLeftPlusRight_px) {
				// the text size is too small ... so increase it
				while (getWidth() > textRectangle.width() + textPaddingLeftPlusRight_px) {
					textSize++;
					txtPaint.setTextSize(textSize);
					txtPaint.getTextBounds(text, 0, text.length(), textRectangle);
				}
			}
			flag_isFirstTime = false;
		}
		return textSize;
	}

	@Override
	protected synchronized void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// create an instance of class Paint, set color and font size
		// Paint textPaint = new Paint();
		textPaint.setAntiAlias(true);
		textPaint.setColor(textColor);

		textSize_px = resizeText(textSize_px);

		textPaint.setTextSize(textSize_px);
		// get the text size
		// Rect textBounds = new Rect();
		textPaint.getTextBounds(text, 0, text.length(), textBounds);
		setPadding(0, 0, 0, 0);

		// show the text in the center of the progress bar
		int x = getWidth() / 2 - textBounds.centerX();
		int y = getHeight() / 2 - textBounds.centerY();
		// drawing text
		canvas.drawText(text, x, y, textPaint);
	}

	public String getText() {
		return text;
	}

	public synchronized void setText(String text) {
		if (text != null) {
			this.text = text;
		} else {
			this.text = "";
		}
		postInvalidate();
	}

	public int getTextColor() {
		return textColor;
	}

	public synchronized void setTextColor(int textColor) {
		this.textColor = textColor;
		postInvalidate();
	}

	public float getTextSize_dp() {
		return textSize_dp;
	}

	public synchronized void setTextSize_dp(int textSize) {
		this.textSize_dp = textSize;
		postInvalidate();
	}

	private int dpToPx(Context context, int dp) {
		DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
		int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
		return px;
	}

	private int pxToDp(Context context, int px) {
		DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
		int dp = Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
		return dp;
	}

}
