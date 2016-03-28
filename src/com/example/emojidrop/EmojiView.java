package com.example.emojidrop;

import java.util.List;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.BounceInterpolator;

public class EmojiView extends View {

	private PathMeasure pathMeasure;
	private Path path;
	private Paint mPaint;
	private List<Point> points;
	private float[] mCurrentPosition = new float[2];
	private boolean flag = true;

	public EmojiView(Context context) {
		this(context, null);
	}

	public EmojiView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public EmojiView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	private void init() {
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaint.setStyle(Style.STROKE);
		mPaint.setColor(Color.RED);

		pathMeasure = new PathMeasure();
		mCurrentPosition = new float[2];
	}

	public void setPoint(List<Point> pointList,int location) {
		this.points = pointList;
		path = new Path();
		
			Point point1 = pointList.get(0);
			Point point2 = pointList.get(1);
			if(flag){
				if(0==point1.getLocation()){
					path.moveTo(100, -10);
					path.quadTo(100, -10,point1.getX(), point1.getY());
				}else{
					path.moveTo(600, -10);
					path.quadTo(600, -10,point1.getX(), point1.getY());
				}
				flag = false;
			}else{
				path.moveTo(point1.getX(), point1.getY());
			}
			
		if (point1.getLocation() == point2.getLocation()) {
			if (0 == point2.getLocation()) {
				path.cubicTo(point1.getX(), point1.getY(), point1.getX() + 50,
						point1.getY() - 100, point2.getX()+50, point2.getY());
				point2.setX(point2.getX()+50);
			} else {
				path.cubicTo(point1.getX(), point1.getY(), point1.getX() - 50,
						point1.getY() - 100, point2.getX()-50, point2.getY());
				point2.setX(point2.getX()-50);
			}
		} else {
			if (0 == point1.getLocation())
				path.cubicTo(point1.getX(), point1.getY(), point1.getX() + 250,
						point1.getY() - 200, point2.getX(), point2.getY());
			else
				path.cubicTo(point1.getX(), point1.getY(), point1.getX() - 250,
						point1.getY() - 200, point2.getX(), point2.getY());
		}
			
		pathMeasure.setPath(path, false);
	}

	@SuppressLint("DrawAllocation")
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawColor(Color.TRANSPARENT);
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.dog);
		canvas.drawBitmap(bitmap, mCurrentPosition[0], mCurrentPosition[1]-bitmap.getHeight(),
				mPaint);
	}

	public void startPathAnim(long duration) {
		ValueAnimator valueAnimator = ValueAnimator.ofFloat(0,
				pathMeasure.getLength());
		valueAnimator.setDuration(duration);
		valueAnimator.setInterpolator(new AccelerateInterpolator());
		valueAnimator.addUpdateListener(new AnimatorUpdateListener() {

			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				float value = (Float) animation.getAnimatedValue();
				// 获取当前点坐标封装到mCurrentPosition
				pathMeasure.getPosTan(value, mCurrentPosition, null);
				postInvalidate();
			}
		});
		valueAnimator.addListener(new AnimatorListener() {

			@Override
			public void onAnimationStart(Animator animation) {
			}

			@Override
			public void onAnimationRepeat(Animator animation) {
			}

			@Override
			public void onAnimationEnd(Animator animation) {
				if(points.size()<1){
					return;
				}
				path = new Path();
				Point point = points.get(1); 
				path.moveTo(point.getX(), point.getY());
				path.quadTo(point.getX(), point.getY(),point.getX(), point.getY()-30);
				path.quadTo(point.getX(), point.getY()-30,point.getX(), point.getY());
				pathMeasure.setPath(path, false);

				ValueAnimator valueAnimator = ValueAnimator.ofFloat(0,
						pathMeasure.getLength());
				valueAnimator.setDuration(1500);
				valueAnimator.setInterpolator(new BounceInterpolator());
				valueAnimator.addUpdateListener(new AnimatorUpdateListener() {

					@Override
					public void onAnimationUpdate(ValueAnimator animation) {
						float value = (Float) animation.getAnimatedValue();
						// 获取当前点坐标封装到mCurrentPosition
						pathMeasure.getPosTan(value, mCurrentPosition, null);
						postInvalidate();
					}
				});
				valueAnimator.addListener(new AnimatorListener() {
					
					@Override
					public void onAnimationStart(Animator animation) {
					}
					
					@Override
					public void onAnimationRepeat(Animator animation) {
					}
					
					@Override
					public void onAnimationEnd(Animator animation) {
						if(points.size()>=3){
							points.remove(0);
							setPoint(points,points.get(0).getLocation());
							startPathAnim(1500);
						}else{
							setVisibility(View.GONE);
						}
					}
					
					@Override
					public void onAnimationCancel(Animator animation) {
					}
				});
				valueAnimator.start();
			}

			@Override
			public void onAnimationCancel(Animator animation) {
			}
		});
		valueAnimator.start();
	}

}
