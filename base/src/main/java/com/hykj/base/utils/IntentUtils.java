package com.hykj.base.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import com.hykj.base.utils.auth.FileProviderUtils;

import java.io.File;

public class IntentUtils {

    /**
     * 安装apk
     *
     * @param context 上下文
     * @param file    apk文件
     */
    public static void installApk(Context context, File file) {
        Intent installIntent = new Intent(Intent.ACTION_VIEW);
        installIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
            installIntent.setDataAndType(FileProviderUtils.getUriForFile(context, file), "application/vnd.android.package-archive");
            installIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            installIntent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        }
        context.startActivity(installIntent);
    }
}
