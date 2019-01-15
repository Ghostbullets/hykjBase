package com.hykj.base.utils.auth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.StringDef;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;

import java.io.File;
import java.io.IOException;

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

    /**
     * @param context
     * @param file
     * @return
     */
    private static Uri getUriForFile(Context context, File file) {
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return FileProvider.getUriForFile(context, context.getPackageName() + ".FileProvider", file);
        } else {
            return Uri.fromFile(file);
        }
    }
}
