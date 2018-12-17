package com.zichri.ilearning;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;


import java.util.ArrayList;
import java.util.List;

public class ChartView extends View {

    private static int DEFAULT_WIDHT;
    private static int DEFAULT_HEIGHT;
    private int mWidth;
    private int mHeight;

    private float paddingBottom;
    private float paddingLeft;

    private Path mPath = new Path();

    private List<ChartDataBean> exponentBeans = new ArrayList<>();


    private Paint mPaint;
    private Paint mCirclePaint;
    private Paint mMarkBackPaint;
    private Paint mTextContentPaint;

    private float cicleRadius;

    private List<PointF> mValuePointFs = new ArrayList<>();

    private int mPointInternal;
    private int leftRightExtra; //x轴左右向外延伸的长度

    private int mMarkViewWidth;
    private int mMarkViewHeight;
    private int mInvertedTriangleWidth;
    private int mInvertedTriangleHeight;

    private GestureDetector gestureDetector;
    private Paint mPaintBack;
    private Paint mDateTextPaint;

    private Bitmap mNoticeBgBitmap;
    private Bitmap mRectangleBitmap;


    public ChartView(Context context) {
        super(context);
        init(context);
    }

    public ChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        DEFAULT_WIDHT = DensityUtils.dip2px(context, 150);
        DEFAULT_HEIGHT = DensityUtils.dip2px(context, 100);
        cicleRadius = DensityUtils.dip2px(context, 6);
        mMarkViewWidth = DensityUtils.dip2px(context, 28);
        mMarkViewHeight = DensityUtils.dip2px(context, 20);
        mInvertedTriangleHeight = DensityUtils.dip2px(context, 10);
        mInvertedTriangleWidth = DensityUtils.dip2px(context, 10);
        mPointInternal = DensityUtils.dip2px(getContext(), 80);
        paddingBottom = DensityUtils.dip2px(getContext(), 30);
        paddingLeft = DensityUtils.dip2px(context, 20);
        leftRightExtra = mPointInternal / 3;

        mPaint = new Paint();
        mPaint.setStrokeWidth(6);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.argb(180, 141, 204, 72));

        mCirclePaint = new Paint();
        mCirclePaint.setStyle(Paint.Style.FILL);
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setColor(Color.argb(180, 245, 189, 69));

        mDateTextPaint = new Paint();
        mDateTextPaint.setStrokeWidth(4);
        mDateTextPaint.setStrokeCap(Paint.Cap.ROUND);
        mDateTextPaint.setAntiAlias(true);
        mDateTextPaint.setStyle(Paint.Style.FILL);
        mDateTextPaint.setColor(Color.argb(180, 203, 203, 203));

        mMarkBackPaint = new Paint();
        mMarkBackPaint.setStyle(Paint.Style.FILL);
        mMarkBackPaint.setAntiAlias(true);
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.outWidth = mMarkViewWidth;
//        options.outHeight = mMarkViewHeight;
//        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.qray_mark_bg_selected/*,options*/);
//        BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
//        mMarkBackPaint.setShader(shader);
        mMarkBackPaint.setColor(Color.RED);

        mTextContentPaint = new Paint();
        mTextContentPaint.setStyle(Paint.Style.STROKE);
        mTextContentPaint.setAntiAlias(true);
        mTextContentPaint.setColor(Color.RED);

        mPaintBack = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintBack.setStyle(Paint.Style.FILL);
        mPaintBack.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));


        gestureDetector = new GestureDetector(context, new MyOnGestureListener());
        mBackgroundDrawable = ContextCompat.getDrawable(context, R.drawable.qray_qurychart_bg);

        mNoticeBgBitmap = drawableToBitmap(ContextCompat.getDrawable(context, R.drawable.qray_chart_notice_bg), mMarkViewWidth, mMarkViewHeight);
        mRectangleBitmap = drawableToBitmap(ContextCompat.getDrawable(context, R.drawable.inverted_triangle), mInvertedTriangleWidth, mInvertedTriangleHeight);
    }

    private RectF mViewRect = new RectF();

    private Drawable mBackgroundDrawable;
    private int firstMinX; // 移动时第一个点的最小x值

    private int firstMaxX; //移动时第一个点的最大x值

    private int firstPointX;


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (changed) {
            firstPointX = (int) paddingLeft;
            firstMaxX = (int) paddingLeft;
        }
        mViewRect.bottom = mHeight;
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getDefaultSize(DEFAULT_WIDHT, widthMeasureSpec);
        mHeight = getDefaultSize(DEFAULT_HEIGHT, heightMeasureSpec);
        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mBackgroundDrawable.draw(canvas);
        calculateValuePoint();
        calculateControlPoint();
        if (mValuePointFs == null || mValuePointFs.size() == 0) return;
        mPath.reset();
        PointF firstPoint = mValuePointFs.get(0);
        mPath.moveTo(firstPoint.x, firstPoint.y);

        for (int i = 0; i < mControlPoints.size(); i++) {
            if (i == 0) {
                mPath.moveTo(firstPointX, mValuePointFs.get(i).y);
            }
            //画三价贝塞尔曲线
            mPath.cubicTo(
                    firstPointX + mControlPoints.get(i).pointF1.x, mControlPoints.get(i).pointF1.y,
                    firstPointX + mControlPoints.get(i).pointF2.x, mControlPoints.get(i).pointF2.y,
                    firstPointX + mValuePointFs.get(i + 1).x, mValuePointFs.get(i + 1).y
            );

        }

        canvas.drawPath(mPath, mPaint);


        for (int j = 0; j < mValuePointFs.size(); j++) {//画圆点
            canvas.drawCircle(firstPointX + mValuePointFs.get(j).x, mValuePointFs.get(j).y, cicleRadius, mCirclePaint);
            drawNotice(canvas, mValuePointFs.get(j));
            drawText(canvas, mValuePointFs.get(j), exponentBeans.get(j).getTime());
        }

        //将折线超出x轴坐标的部分截取掉（左边）


        canvas.drawRect(mViewRect, mPaintBack);

    }

    public void setData(List<ChartDataBean> beans) {
        if (beans == null || beans.size() == 0) return;
        this.exponentBeans.clear();
        this.exponentBeans.addAll(beans);
        invalidate();
    }

    private void calculateValuePoint() {
        mValuePointFs.clear();
        for (int i = 0; i < exponentBeans.size(); i++) {
            PointF pointF = new PointF();
            pointF.x = cicleRadius + (i * mPointInternal);
            pointF.y = mHeight - ((mHeight * 1.00f / 4) * exponentBeans.get(i).getQrayExponent()) - paddingBottom;
            mValuePointFs.add(pointF);
        }

    }


    private List<ControlPoint> mControlPoints = new ArrayList<>();

    private void calculateControlPoint() {
        mControlPoints.clear();
        PointF p1;
        PointF p2;
        float conP1x;
        float conP1y;
        float conP2x;
        float conP2y;
        for (int i = 0; i < mValuePointFs.size() - 1; i++) {

            if (i == 0) {
                //第一断1曲线 控制点
                conP1x = mValuePointFs.get(i).x + (mValuePointFs.get(i + 1).x - mValuePointFs.get(i).x) / 4;
                conP1y = mValuePointFs.get(i).y + (mValuePointFs.get(i + 1).y - mValuePointFs.get(i).y) / 4;

                conP2x = mValuePointFs.get(i + 1).x - (mValuePointFs.get(i + 2).x - mValuePointFs.get(i).x) / 4;
                conP2y = mValuePointFs.get(i + 1).y - (mValuePointFs.get(i + 2).y - mValuePointFs.get(i).y) / 4;

            } else if (i == mValuePointFs.size() - 2) {
                //最后一段曲线 控制点
                conP1x = mValuePointFs.get(i).x + (mValuePointFs.get(i + 1).x - mValuePointFs.get(i - 1).x) / 4;
                conP1y = mValuePointFs.get(i).y + (mValuePointFs.get(i + 1).y - mValuePointFs.get(i - 1).y) / 4;

                conP2x = mValuePointFs.get(i + 1).x - (mValuePointFs.get(i + 1).x - mValuePointFs.get(i).x) / 4;
                conP2y = mValuePointFs.get(i + 1).y - (mValuePointFs.get(i + 1).y - mValuePointFs.get(i).y) / 4;
            } else {
                conP1x = mValuePointFs.get(i).x + (mValuePointFs.get(i + 1).x - mValuePointFs.get(i - 1).x) / 4;
                conP1y = mValuePointFs.get(i).y + (mValuePointFs.get(i + 1).y - mValuePointFs.get(i - 1).y) / 4;

                conP2x = mValuePointFs.get(i + 1).x - (mValuePointFs.get(i + 2).x - mValuePointFs.get(i).x) / 4;
                conP2y = mValuePointFs.get(i + 1).y - (mValuePointFs.get(i + 2).y - mValuePointFs.get(i).y) / 4;
            }

            p1 = new PointF(conP1x, conP1y);
            p2 = new PointF(conP2x, conP2y);

            ControlPoint controlPoint = new ControlPoint(p1, p2);
            mControlPoints.add(controlPoint);
            firstMinX = (int) (mWidth - (mControlPoints.size()) * mPointInternal - leftRightExtra);

        }

    }


    public static int getDefaultSize(int size, int measureSpec) {
        int result = size;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        switch (specMode) {
            case MeasureSpec.UNSPECIFIED:
                result = size;
                break;
            case MeasureSpec.AT_MOST:
            case MeasureSpec.EXACTLY:
                result = specSize;
                break;
        }
        return result;
    }

    private void drawNotice(Canvas canvas, PointF point) {

        RectF rectF = null;
        //画红点旁边的提示框和文字，有四个区域，然后提示框的小三角指标方位不同
        //left-bottom
        //画矩形背景
        float left = firstPointX + point.x - mMarkViewWidth / 2.00f;
        float top = point.y - mMarkViewHeight - mInvertedTriangleHeight;
        float right = firstPointX + point.x + mMarkViewWidth / 2.00f;
        float bottom = point.y - mInvertedTriangleHeight;
        rectF = new RectF(left, top, right, bottom);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.outWidth = mMarkViewWidth;
        options.outHeight = mMarkViewHeight;

        if (mNoticeBgBitmap != null) {
            float bgleft = firstPointX + point.x - mMarkViewWidth / 2;
            canvas.drawBitmap(mNoticeBgBitmap, bgleft, top, mMarkBackPaint);
        }
        if (mRectangleBitmap != null) {
            float rectleft = firstPointX + point.x - mInvertedTriangleWidth / 2;
            float rectTop = point.y - mInvertedTriangleHeight;
            canvas.drawBitmap(mRectangleBitmap, rectleft, rectTop, mMarkBackPaint);
        }

        String content = "4.7";
        Rect textRect = new Rect();
        mTextContentPaint.setTextSize(30);
        mTextContentPaint.setColor(Color.WHITE);
        mTextContentPaint.setTextAlign(Paint.Align.CENTER);
        Paint.FontMetrics fontMetrics = mTextContentPaint.getFontMetrics();
        float distance = (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom;
        float baseline = rectF.centerY() + distance;
        canvas.drawText(content, rectF.centerX(), baseline, mTextContentPaint);


    }


    private Bitmap drawableToBitmap(Drawable drawable, int width, int height) {
        // 取 drawable 的颜色格式
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                : Bitmap.Config.RGB_565;
        // 建立对应 bitmap
        Bitmap bitmap = Bitmap.createBitmap(width, height, config);
        // 建立对应 bitmap 的画布
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, width, height);
        // 把 drawable 内容画到画布中
        drawable.draw(canvas);
        return bitmap;
    }

    private void drawText(Canvas canvas, PointF point, String dateStr) {// 训练日期 格式 yyyy-MM-dd
        if (dateStr.length() == 10) {//TODO 按照不同的时间格式生成不同的时间

            dateStr = dateStr.substring(5, dateStr.length());
        }
        mDateTextPaint.setTextSize(40);
        mDateTextPaint.setTextAlign(Paint.Align.CENTER);
        Paint.FontMetrics fontMetrics = mDateTextPaint.getFontMetrics();
        float distance = (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom;
        Rect textRect = new Rect();
        mDateTextPaint.getTextBounds(dateStr, 0, dateStr.length() - 1, textRect);
        int textWidth = textRect.width();
        int textHeight = textRect.height();
        RectF rectF = new RectF(point.x - textWidth / 2 + firstPointX, textHeight, point.x + textWidth / 2 + firstPointX, 0);
        canvas.drawText(dateStr, rectF.centerX(), mHeight, mDateTextPaint);

    }

    class ControlPoint {
        PointF pointF1;
        PointF pointF2;

        ControlPoint(PointF pointF1, PointF pointF2) {
            this.pointF1 = pointF1;
            this.pointF2 = pointF2;
        }
    }


    /**
     * 手势事件
     */
    class MyOnGestureListener implements GestureDetector.OnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) { // 按下事件
            return false;
        }

        // 按下停留时间超过瞬时，并且按下时没有松开或拖动，就会执行此方法
        @Override
        public void onShowPress(MotionEvent motionEvent) {
        }

        @Override
        public boolean onSingleTapUp(MotionEvent motionEvent) { // 单击抬起
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (e1.getX() > 0 && e1.getX() < mWidth) {
                //注意：这里的distanceX是e1.getX()-e2.getX()
                distanceX = -distanceX;
                if (firstPointX + distanceX > firstMaxX) {
                    firstPointX = firstMaxX;
                } else if (firstPointX + distanceX < firstMinX) {
                    firstPointX = firstMinX;
                } else {
                    firstPointX = (int) (firstPointX + distanceX);
                }
                invalidate();
            }
            return false;
        }

        @Override
        public void onLongPress(MotionEvent motionEvent) {
        } // 长按事件

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if ((mControlPoints.size()) * mPointInternal <= mWidth) {
            return false;
        }
        gestureDetector.onTouchEvent(event);
        return true;
    }


}

