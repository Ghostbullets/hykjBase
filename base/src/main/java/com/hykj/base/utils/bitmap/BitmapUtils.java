package com.hykj.base.utils.bitmap;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.FloatRange;
import android.support.annotation.IntDef;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.hykj.base.utils.ContextKeep;
import com.hykj.base.utils.storage.FileUtil;

import java.io.ByteArrayOutputStream;

/**
 * 存取头像工具类---以文件形式存放
 * Created by Administrator on 2018/1/24.
 */

public class BitmapUtils {


    //获取需要缩小的比例
    public static int calculateInSampleSize(BitmapFactory.Options options, int refWidth, int refHeight) {
        int outWidth = options.outWidth;
        int outHeight = options.outHeight;
        int SampleSize = 1;
        if (outWidth > refWidth || outHeight > refHeight) {//获取的图片高度或者宽度大于你想要的高宽2倍以上才需要缩放
            final int width = outWidth / 2;
            final int height = outHeight / 2;
            while ((width / SampleSize) > refWidth && (height / SampleSize) > height) {
                SampleSize *= 2;
            }
        }
        return SampleSize;
    }

    /**
     * @param res       资源
     * @param resId     资源id
     * @param reqWidth  想要的宽
     * @param reqHeight 想要的高
     * @return 缩小后的图片
     */
    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        //设置为true可以在解码的时候避免内存的分配，它会返回一个null的Bitmap，但是可以获取到 outWidth
        BitmapFactory.decodeResource(res, resId, options);//获取到图片宽高类型等信息放到options中
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);//得到缩放比
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    /**
     * @param path      本地路径
     * @param reqWidth  想要的宽
     * @param reqHeight 想要的高
     * @return 缩小后的图片
     */
    public static Bitmap decodeSampledBitmapFromPath(String path, int reqWidth, int reqHeight) {
        if (TextUtils.isEmpty(path))
            return null;
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);//获取到图片宽高类型等信息放到options中
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);//得到缩放比
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);
    }

    /**
     * @param bitmap         原图
     * @param isPngFormat    是否压缩成PNG格式图片
     * @param targetByteSize 目标byte大小，单位B
     * @param recycle        是否释放原图资源
     * @return
     */
    public static byte[] decodeQualityByteForBitmap(Bitmap bitmap, boolean isPngFormat, int targetByteSize, boolean recycle) {
        if (bitmap == null)
            return null;
        if (targetByteSize <= 0)
            targetByteSize = 10 * 1024;
        int quality = 100;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        do {
            baos.reset();
            bitmap.compress(isPngFormat ? Bitmap.CompressFormat.PNG : Bitmap.CompressFormat.JPEG, quality, baos);
            Log.e(BitmapUtils.class.getSimpleName(), "当前压缩质量为" + quality + "，压缩后的大小为" + baos.toByteArray().length / 1024 + "KB");
            quality -= 10;
            if (quality <= 0)
                break;
        } while (baos.toByteArray().length > targetByteSize);
        if (recycle && !bitmap.isRecycled())
            bitmap.recycle();
        return baos.toByteArray();
    }

    public static byte[] decodeQualityByteForPath(String path, int targetByteSize, boolean recycle) {
        if (TextUtils.isEmpty(path))
            return null;
        try {
            return decodeQualityByteForBitmap(BitmapFactory.decodeFile(path), "png".equals(path.substring(path.lastIndexOf(".") + 1)), targetByteSize, recycle);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 创建 bitmap宽、bitmap高的一个空的Bitmap对象，将bitmap的rectF矩阵内的内容书写到空的Bitmap对象的rectF矩阵中，
     * 此时新的Bitmap宽高跟原来一样，需要使用Bitmap.createBitmap(bitmap,(int)rectF.left,(int)rectF.top,(int)rectF.width(),(int)rectF.height())来裁剪
     *
     * @param bitmap 原图
     * @param rectF  要截取的圆角矩形位置信息
     * @param rx     X轴方向圆角大小
     * @param ry     Y轴方向圆角大小
     * @return
     */
    public static Bitmap getRoundRectBitmap(Bitmap bitmap, RectF rectF, @FloatRange(from = 0) float rx, @FloatRange(from = 0) float ry) {
        if (bitmap == null)
            return null;
        Bitmap roundRectBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(roundRectBitmap);
        Paint bitmapPaint = new Paint();
        BitmapShader bitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        bitmapPaint.setShader(bitmapShader);
        if (rectF == null)
            rectF = new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight());
        canvas.drawRoundRect(rectF, rx, ry, bitmapPaint);
        return roundRectBitmap;
    }

    /**
     * 按尺寸大小缩放图片并返回
     *
     * @param bitmap        原图
     * @param fitSize       想要的尺寸大小
     * @param fitBitmapType 希望以长还是宽为准缩放比例
     * @param recycle       是否需要重新刷新图片
     * @return
     */
    public static Bitmap scaleImage(Bitmap bitmap, int fitSize, FitBitmapType fitBitmapType, boolean recycle) {
        if (bitmap == null)
            return null;

        int oriWidth = bitmap.getWidth();
        int oriHeight = bitmap.getHeight();
        float scaleSize = 1;
        switch (fitBitmapType) {
            case WIDTH:
                scaleSize = (float) fitSize / oriWidth;
                break;
            case HEIGHT:
                scaleSize = (float) fitSize / oriHeight;
                break;
        }
        Matrix matrix = new Matrix();
        matrix.postScale(scaleSize, scaleSize);

        Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, oriWidth, oriHeight, matrix, true);

        if (recycle && !bitmap.equals(newBitmap) && !bitmap.isRecycled())
            bitmap.recycle();

        return newBitmap;
    }

    public enum FitBitmapType {
        WIDTH//以宽度为准
        , HEIGHT//以高度为准
    }

    /**
     * 按尺寸大小缩放图片并返回
     *
     * @param bitmap       原图
     * @param targetWidth  想要的尺寸宽
     * @param targetHeight 想要的尺寸高
     * @param recycle      是否释放原图资源
     * @return
     */
    public static Bitmap scaleImage(Bitmap bitmap, float targetWidth, float targetHeight, boolean recycle) {
        if (bitmap == null)
            return null;
        // 获取图片宽和高
        int oriWidth = bitmap.getWidth();
        int oriHeight = bitmap.getHeight();
        Matrix matrix = new Matrix();
        if (oriWidth != targetWidth || oriHeight != targetHeight) {
            // 计算宽高缩放率
            float scaleWidth = targetWidth / oriWidth;
            float scaleHeight = targetHeight / oriHeight;
            matrix.postScale(scaleWidth, scaleHeight);
        }
        Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, oriWidth, oriHeight, matrix, true);

        if (recycle && !bitmap.equals(newBitmap) && !bitmap.isRecycled())
            bitmap.recycle();

        return newBitmap;
    }

    /**
     * 缩放图像
     *
     * @param drawable   图像
     * @param scaleWidth 希望缩放的尺寸
     * @param zoomType
     * @return
     */
    public static Drawable zoomImage(Drawable drawable, int scaleWidth, @ZoomType int zoomType) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        float scale = 1.0f;
        switch (zoomType) {
            case ZoomType.ZOOM:
                scale = scaleWidth / (float) width;
                break;
            case ZoomType.MAGNIFY:
                if (width < scaleWidth) {
                    scale = scaleWidth / (float) width;
                }
                break;
            case ZoomType.SHRINK:
                if (width > scaleWidth) {
                    scale = scaleWidth / (float) width;
                }
                break;
        }
        if (scale == 1) {
            return drawable;
        }
        //将drawable的图像设置绘制范围，绘制到canvas的bitmap上
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);

        //设置缩放比例，返回缩放后的drawable图像
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        Bitmap scaleBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        if (!bitmap.equals(scaleBitmap) && !bitmap.isRecycled())
            bitmap.recycle();
        return new BitmapDrawable(ContextKeep.getContext().getResources(), scaleBitmap);
    }

    @IntDef({ZoomType.ZOOM, ZoomType.SHRINK, ZoomType.MAGNIFY})
    public @interface ZoomType {//缩放类型，缩放、缩小、放大
        int ZOOM = 0;
        int SHRINK = 1;
        int MAGNIFY = 2;
    }


    public static Bitmap getScreenCapture(Activity activity) {
        //找到当前页面的跟布局
        View decorView = activity.getWindow().getDecorView();
        //设置缓存
        decorView.setDrawingCacheEnabled(true);
        //启用DrawingCache并创建位图
        decorView.buildDrawingCache();
        //从缓存中获取当前屏幕的图片
        Bitmap cache = Bitmap.createBitmap(decorView.getDrawingCache());
        //禁用DrawingCahce否则会影响性能
        decorView.setDrawingCacheEnabled(false);
        return cache;
    }

    //截屏
    public static Bitmap drawScreenCapture(Fragment fragment) {
        return drawScreenCapture(fragment.getView());
    }

    //截屏
    public static Bitmap drawScreenCapture(Activity activity) {
        return drawScreenCapture(activity.getWindow().getDecorView());
    }

    //截屏
    public static Bitmap drawScreenCapture(View decorView) {
        return drawScreenCapture(decorView, 0, 0);
    }

    /**
     * 截屏
     *
     * @param decorView 需要截屏的View
     * @param width     bitmap的宽 传0则为View的宽
     * @param height    bitmap的高 传0则为View的高
     * @return
     */
    public static Bitmap drawScreenCapture(View decorView, int width, int height) {
        if (decorView == null)
            return null;
        decorView.measure(0, 0);
        if (width <= 0 || height <= 0) {
            width = decorView.getMeasuredWidth();
            height = decorView.getMeasuredHeight();
        }
        float sx = width / (float) decorView.getMeasuredWidth();
        float sy = height / (float) decorView.getMeasuredHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        decorView.draw(canvas);
        if (sx == 1 && sy == 1)
            return bitmap;
        Matrix matrix = new Matrix();
        matrix.postScale(sx, sy);
        Bitmap scaleBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
        if (!bitmap.equals(scaleBitmap) && !bitmap.isRecycled())
            bitmap.recycle();
        return scaleBitmap;
    }

    /**
     * 保存图片到本地，并且插入到相册中,需要读写存储权限
     *
     * @param bitmap     图像资源
     * @param fileName   要保存的文件名
     * @param isRecycled 是否在保存成功后释放资源
     */
    public static boolean saveBitmapToSDCard(Bitmap bitmap, String fileName, boolean isRecycled) {
        String filePath = FileUtil.saveBitmapToFile(bitmap, fileName);
        try {
            if (!TextUtils.isEmpty(filePath)) {
                String result = MediaStore.Images.Media.insertImage(ContextKeep.getContext().getContentResolver(), filePath, fileName, null);
                if (!TextUtils.isEmpty(result)) {
                    ContextKeep.getContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse(result)));
                    if (isRecycled && !bitmap.isRecycled())
                        bitmap.recycle();
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
