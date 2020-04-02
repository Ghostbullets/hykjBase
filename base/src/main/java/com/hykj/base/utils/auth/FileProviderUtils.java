package com.hykj.base.utils.auth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import androidx.annotation.StringDef;
import androidx.fragment.app.Fragment;
import androidx.core.content.FileProvider;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Iterator;
import java.util.List;

/**
 * app间文件共享
 */
public class FileProviderUtils {

    /**
     * 打开相册
     *
     * @param activity    活动
     * @param requestCode 请求码
     * @param imageType   希望获取的图片类型时可以直接写如："image/jpeg 、 image/png等的类型" 所有类型则写 "image/*"
     */
    public static void startOpenAlbum(Activity activity, int requestCode, @ImageType String imageType) {
        // 图库获取
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imageType);
        activity.startActivityForResult(intent, requestCode);
    }

    public static void startOpenAlbum(Fragment fragment, int requestCode, @ImageType String imageType) {
        // 图库获取
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imageType);
        fragment.startActivityForResult(intent, requestCode);
    }

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({ImageType.JPEG, ImageType.PNG, ImageType.ALL})
    public @interface ImageType {
        String JPEG = "image/jpeg";
        String PNG = "image/png";
        String ALL = "image/*";
    }

    /**
     * 打开相机
     *
     * @param activity
     * @param file
     */
    public static void startOpenCamera(Activity activity, int requestCode, File file) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //授予目录临时共享权限
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, getUriForFile(activity, file));
        activity.startActivityForResult(intent, requestCode);
    }

    public static void startOpenCamera(Fragment fragment, int requestCode, File file) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //授予目录临时共享权限
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, getUriForFile(fragment.getContext(), file));
        fragment.startActivityForResult(intent, requestCode);
    }

    private static void grantUriPermission(Activity activity, Uri uri, Intent intent) {
        List<ResolveInfo> resolveInfos = activity.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        if (resolveInfos.size() == 0) {
            Toast.makeText(activity, "没有合适的相机应用程序", Toast.LENGTH_SHORT).show();
            return;
        }
        Iterator<ResolveInfo> iterator = resolveInfos.iterator();
        while (iterator.hasNext()) {
            ResolveInfo resolveInfo = iterator.next();
            String packageName = resolveInfo.activityInfo.packageName;
            activity.grantUriPermission(packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }
    }

    /**
     * 裁剪图片
     *
     * @param activity    活动
     * @param requestCode 请求码
     * @param sourceFile  从该文件夹拿取图片去裁剪
     * @param targetFile  裁剪的图片存储文件
     */
    public static void startCropPicture(Activity activity, int requestCode, File sourceFile, File targetFile) {
        activity.startActivityForResult(getCropIntent(getUriForFile(activity, sourceFile), getUriForFile(activity, targetFile, true)), requestCode);
    }

    public static void startCropPicture(Activity activity, int requestCode, Uri sourceUri, Uri targetUri) {
        activity.startActivityForResult(getCropIntent(sourceUri, targetUri), requestCode);
    }

    public static void startCropPicture(Fragment fragment, int requestCode, File sourceFile, File targetFile) {
        fragment.startActivityForResult(getCropIntent(getUriForFile(fragment.getContext(), sourceFile), getUriForFile(fragment.getContext(), targetFile, true)), requestCode);
    }

    public static void startCropPicture(Fragment fragment, int requestCode, Uri sourceUri, Uri targetUri) {
        fragment.startActivityForResult(getCropIntent(sourceUri, targetUri), requestCode);
    }

    private static Intent getCropIntent(Uri sourceUri, Uri targetUri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.setDataAndType(sourceUri, "image/*");
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高方向上的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 200);
        intent.putExtra("outputY", 200);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, targetUri);
        //输出格式，一般设为Bitmap格式：Bitmap.CompressFormat.JPEG.toString()
        intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
        return intent;
    }

    /**
     * @param context
     * @param file
     * @return
     */
    public static Uri getUriForFile(Context context, File file) {
        return getUriForFile(context, file, false);
    }

    /**
     * @param context       上下文
     * @param file          文件
     * @param isNotProvider 是否不需要经过provider
     * @return
     */
    public static Uri getUriForFile(Context context, File file, boolean isNotProvider) {
        if (context == null || file == null)
            throw new NullPointerException();
        if (!file.exists()) {
            try {
                if (!file.createNewFile()) {
                    return null;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        //生成的Uri路径格式为file://xxx,无法在App之间共享的，我们需要生成content://xxx类型的Uri，方法就是通过FileProvider.getUriForFile来实现：
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && !isNotProvider) {
            return FileProvider.getUriForFile(context, context.getPackageName() + ".FileProvider", file);
        } else {
            return Uri.fromFile(file);
        }
    }
}
