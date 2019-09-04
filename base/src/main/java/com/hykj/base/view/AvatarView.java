package com.hykj.base.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.hykj.base.R;

/**
 * 矩形、圆形、椭圆控件
 */
public class AvatarView extends AppCompatImageView {
    private static final ScaleType SCALE_TYPE = ScaleType.CENTER_CROP;
    protected @ShapeType
    int mShapeType = ShapeType.CIRCLE;

    protected Bitmap mBitmap;//图片
    protected BitmapShader mBitmapShader;//图片著色器
    protected int mBitmapHeight;
    protected int mBitmapWidth;
    private Matrix mBitmapMatrix;//图片矩阵变化

    private Paint mBitmapPaint;//图片画笔
    private Paint mBorderPaint;//边框画笔
    private RectF mDrawableRect;//图片区域
    private RectF mBorderRect;//边框区域

    private @ColorInt
    int mBorderColor;//边框颜色
    private int mBorderWidth;//边框粗细

    protected Path mCornerPath;//路径,用于绘制圆角矩形

    private float mCircleRadius;//圆的半径
    protected int mRadius = 0;//圆角矩形 圆角半径,四个角都相同
    protected float[] mCornerRadii;//不规则图形的4个角、每个角又分为X轴方向跟Y轴，一共8个弧形,使用下列4个属性
    protected int mRadiusLeftTop = 0;
    protected int mRadiusRightTop = 0;
    protected int mRadiusRightBottom = 0;
    protected int mRadiusLeftBottom = 0;
    protected boolean mReady;//是否可以开始绘制


    public AvatarView(Context context) {
        this(context, null);
    }

    public AvatarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AvatarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setScaleType(SCALE_TYPE);
        mReady = true;
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mBitmapPaint = new Paint();
        mBitmapPaint.setAntiAlias(true);
        mBorderPaint = new Paint();
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mCornerPath = new Path();
        mBitmapMatrix = new Matrix();
        mDrawableRect = new RectF();
        mBorderRect = new RectF();
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AvatarView);
            mRadiusLeftTop = a.getDimensionPixelSize(R.styleable.AvatarView_radiusLeftTop, 0);
            mRadiusLeftBottom = a.getDimensionPixelSize(R.styleable.AvatarView_radiusLeftBottom, 0);
            mRadiusRightTop = a.getDimensionPixelSize(R.styleable.AvatarView_radiusRightTop, 0);
            mRadiusRightBottom = a.getDimensionPixelSize(R.styleable.AvatarView_radiusRightBottom, 0);
            mCornerRadii = new float[]{mRadiusLeftTop, mRadiusLeftTop, mRadiusRightTop, mRadiusRightTop, mRadiusRightBottom, mRadiusRightBottom, mRadiusLeftBottom, mRadiusLeftBottom};

            mRadius = a.getDimensionPixelSize(R.styleable.AvatarView_radius, 0);
            mBorderColor = a.getColor(R.styleable.AvatarView_avatarBorderColor, -1);
            mBorderWidth = a.getDimensionPixelSize(R.styleable.AvatarView_avatarBorderWidth, 0);
            mShapeType = a.getInt(R.styleable.AvatarView_shapeType, ShapeType.CIRCLE);
            a.recycle();
        }
        setUp();
    }

    private void setUp() {
        if (!mReady) {

        } else if (mBitmap != null) {
            mBorderPaint.setStrokeWidth(mBorderWidth);
            mBorderPaint.setColor(mBorderColor);

            mBitmapShader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            mBitmapPaint.setShader(mBitmapShader);
            mBitmapHeight = mBitmap.getHeight();
            mBitmapWidth = mBitmap.getWidth();
            mBorderRect.set(mBorderWidth / 2.0F, mBorderWidth / 2.0F, getWidth() - mBorderWidth / 2.0F, getHeight() - mBorderWidth / 2.0F);
            mDrawableRect.set(this.mBorderWidth + getPaddingLeft(), this.mBorderWidth + getPaddingTop(), getWidth() - (this.mBorderWidth + getPaddingRight()), getHeight() - (this.mBorderWidth + getPaddingBottom()));
            if (mShapeType == ShapeType.CIRCLE)
                mCircleRadius = Math.min(mDrawableRect.width() / 2, mDrawableRect.height() / 2);
            updateShaderMatrix();
            invalidate();
        }
    }

    //重新将图片矩阵变化
    private void updateShaderMatrix() {
        mBitmapMatrix.reset();
        float scale;
        float dx = 0.0F;
        float dy = 0.0F;
        if (mBitmapWidth / mDrawableRect.width() >= mBitmapHeight / mDrawableRect.height()) {
            scale = mDrawableRect.height() / mBitmapHeight;
            dx = (mDrawableRect.width() - mBitmapWidth * scale) / 2;
        } else {
            scale = mDrawableRect.width() / mBitmapWidth;
            dy = (mDrawableRect.height() - mBitmapHeight * scale) / 2;
        }
        mBitmapMatrix.setScale(scale, scale);
        mBitmapMatrix.postTranslate(dx, dy);
        mBitmapShader.setLocalMatrix(mBitmapMatrix);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        setUp();
    }

    @Override
    public void setImageDrawable(@Nullable Drawable drawable) {
        super.setImageDrawable(drawable);
        mBitmap = getBitmapByDrawable(drawable);
        setUp();
    }

    //根据drawable获取bitmap
    private Bitmap getBitmapByDrawable(Drawable drawable) {
        if (drawable == null) return null;
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else {
            Bitmap bitmap;
            if (drawable instanceof ColorDrawable) {
                bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
            } else if (drawable.getIntrinsicWidth() <= 0) {
                //测量控件宽高，将其用于位图宽高
                measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                        MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
                int width = getMeasuredWidth();
                int height = getMeasuredHeight();
                bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            } else {
                bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            }
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());
            drawable.draw(canvas);
            return bitmap;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (getDrawable() != null) {
            switch (mShapeType) {
                case ShapeType.CIRCLE:
                    canvas.drawCircle(getWidth() / 2, getHeight() / 2, mCircleRadius, mBitmapPaint);
                    if (mBorderWidth > 0)
                        canvas.drawArc(mBorderRect, -90, 360, false, mBorderPaint);
                    break;
                case ShapeType.ROUND:
                    canvas.drawRoundRect(mDrawableRect, mRadius, mRadius, mBitmapPaint);
                    if (mBorderWidth > 0)
                        canvas.drawRoundRect(mBorderRect, mRadius, mRadius, mBorderPaint);
                    break;
                case ShapeType.CORNER:
                    mCornerPath.reset();
                    mCornerPath.addRoundRect(mDrawableRect, mCornerRadii, Path.Direction.CCW);
                    canvas.drawPath(mCornerPath, mBitmapPaint);
                    if (mBorderWidth > 0) {
                        mCornerPath.reset();
                        mCornerPath.addRoundRect(mBorderRect, mCornerRadii, Path.Direction.CCW);
                        canvas.drawPath(mCornerPath, mBorderPaint);
                    }
                    break;
            }
        }
    }

    public void setBorderValue(@ColorInt int borderColor, int borderWidth) {
        this.mBorderColor = borderColor;
        this.mBorderWidth = borderWidth;
        this.setUp();
    }

    @Override
    public void setScaleType(ScaleType scaleType) {
        if (scaleType != ScaleType.CENTER_CROP)
            throw new RuntimeException("can't set other scaleType");
        super.setScaleType(scaleType);
    }

    @IntDef({ShapeType.CIRCLE, ShapeType.ROUND, ShapeType.CORNER})
    public @interface ShapeType {
        int CIRCLE = 0;
        int ROUND = 1;
        int CORNER = 2;
    }
}
