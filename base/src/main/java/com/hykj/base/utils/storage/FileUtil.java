package com.hykj.base.utils.storage;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.StringDef;
import android.text.TextUtils;
import android.util.Log;

import com.hykj.base.utils.ContextKeep;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;

/**
 * 文件工具类
 * <p>
 * Context.getExternalFilesDir(type);//SDCard/Android/data/你的应用的包名/files/ 目录/type类型文件夹，一般放一些长时间保存的数据
 * Context.getExternalCacheDir()方法可以获取到 SDCard/Android/data/你的应用包名/cache/目录，一般存放临时缓存数据
 * Context.getCacheDir()方法用于获取/data/data/<application package>/cache目录
 * Context.getFilesDir()方法用于获取/data/data/<application package>/files目录
 */
public class FileUtil {

    private static final String TAG = "FileUtil";
    private static final String pathDiv = "/";
    private static File cacheDir = !isExternalStorageWritable() ? ContextKeep.getContext().getFilesDir() : ContextKeep.getContext().getExternalCacheDir();

    private FileUtil() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * 初始化不同类型的文件夹
     */
    public static void init() {
        Field[] fields = FileType.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                @FileType String fileType = (String) field.get(FileType.class);
                createClassFile(fileType);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @param fileType 文件夹类型名
     * @return 是否创建成功
     */
    private static boolean createClassFile(@FileType String fileType) {
        File file = new File(getFileTypePath(fileType));
        boolean success = true;
        if (!file.exists()) {
            success = file.mkdir();
        }
        return success;
    }

    /**
     * 根据文件名、文件夹类型创建文件
     *
     * @param fileName 文件名
     * @param fileType 文件类型
     * @return
     */
    public static File createNewFile(String fileName, @FileType String fileType) {
        return createNewFile(getCacheFilePath(fileType, fileName));
    }

    public static File createNewFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            try {
                if (!file.createNewFile()) {
                    throw new RuntimeException("创建文件失败");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    public static File getTempFile(@FileType String type) {
        return getTempFile(type, null);
    }

    /**
     * 创建临时文件，当退出程序时删除该临时文件
     *
     * @param type   文件类型
     * @param suffix 文件后缀名
     */
    public static File getTempFile(@FileType String type, String suffix) {
        try {
            File file = File.createTempFile(type, suffix, new File(cacheDir, type));
            file.deleteOnExit();
            return file;
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * 根据文件夹类型获取缓存文件夹地址
     *
     * @param fileType 文件夹类型
     * @return
     */
    public static String getFileTypePath(@FileType String fileType) {
        return cacheDir.getAbsolutePath() + pathDiv + fileType + pathDiv;
    }

    /**
     * 获取缓存文件地址
     *
     * @param fileName 文件名
     * @param fileType 文件夹类型
     */
    public static String getCacheFilePath(String fileName, @FileType String fileType) {
        return getFileTypePath(fileType) + fileName;
    }

    /**
     * 判断缓存文件是否存在
     *
     * @param fileName 文件名
     * @param fileType 文件夹类型
     */
    public static boolean isCacheFileExist(String fileName, @FileType String fileType) {
        File file = new File(getCacheFilePath(fileName, fileType));
        return file.exists();
    }

    public static String saveBitmapToFile(Bitmap bitmap, String filename) {
        return saveBitmapToFile(bitmap, Bitmap.CompressFormat.PNG, filename);
    }

    /**
     * 将图片存储为文件,有损压缩
     *
     * @param bitmap   图片
     * @param filename 文件名
     * @param format   压缩图像的格式
     * @return 保存成功返回保存路径，否则返回null
     */
    public static String saveBitmapToFile(Bitmap bitmap, Bitmap.CompressFormat format, String filename) {
        if (bitmap == null || TextUtils.isEmpty(filename))
            return null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(format, 100, bos);
        byte[] bytes = bos.toByteArray();
        CloseableUtils.close(bos);
        return saveBytesToFile(bytes, filename, FileType.IMG);
    }

    /**
     * 将数据存储为文件
     *
     * @param data     数据
     * @param filename 文件名
     * @return 保存成功返回保存全路径，否则返回null
     */
    public static String saveBytesToFile(byte[] data, String filename, @FileType String fileType) {
        File f = new File(getFileTypePath(fileType), filename);
        try {
            if (f.createNewFile()) {
                FileOutputStream fos = new FileOutputStream(f);
                fos.write(data);
                fos.flush();
                fos.close();
            }
        } catch (IOException e) {
            Log.e(TAG, "create file error" + e);
        }
        if (f.exists()) {
            return f.getAbsolutePath();
        }
        return null;
    }

    /**
     * 将数据存储为文件
     *
     * @param data     数据
     * @param fileName 文件名
     * @param type     要保存的文件所在文件夹类型   例如  Environment.DIRECTORY_MUSIC 音乐
     */
    public static String createFile(byte[] data, String fileName, String type) {
        if (isExternalStorageWritable()) {
            File dir = ContextKeep.getContext().getExternalFilesDir(type);//SDCard/Android/data/你的应用的包名/files/ 目录，一般放一些长时间保存的数据
            if (dir != null) {
                File f = new File(dir, fileName);
                try {
                    if (f.createNewFile()) {
                        FileOutputStream fos = new FileOutputStream(f);
                        fos.write(data);
                        fos.flush();
                        fos.close();
                        return f.getAbsolutePath();
                    }
                } catch (IOException e) {
                    Log.e(TAG, "create file error" + e);
                }
            }
        }
        return null;
    }

    /**
     * 判断缓存文件是否存在
     *
     * @param fileName 文件名
     * @param type     文件类型
     * @return 要保存的文件所在文件夹类型   例如  Environment.DIRECTORY_MUSIC 音乐
     */
    public static boolean isFileExist(String fileName, String type) {
        if (isExternalStorageWritable()) {
            File dir = ContextKeep.getContext().getExternalFilesDir(type);
            if (dir != null) {
                File f = new File(dir, fileName);
                return f.exists();
            }
        }
        return false;
    }

    /**
     * 将assets文件copy到app/data/cache目录
     *
     * @param fileName asset文件路径
     * @param desFile  缓存本地路径
     */
    public static void copyAssetsToFiles(String fileName, File desFile) {
        InputStream in = null;
        OutputStream out = null;
        try {
            in = ContextKeep.getContext().getAssets().open(fileName);
            out = new FileOutputStream(desFile.getAbsolutePath());
            byte[] bytes = new byte[1024];
            int len;
            while ((len = in.read(bytes)) != -1)
                out.write(bytes, 0, len);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            CloseableUtils.close(in, out);
        }
    }

    /**
     * 从URI获取图片文件地址
     *
     * @param uri 文件uri
     */
    public static String getImageFilePath(Uri uri) {
        if (uri == null) {
            return null;
        }
        String path = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (!isMediaDocument(uri)) {
                try {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    Uri contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    final String selection = "_id=?";
                    final String[] selectionArgs = new String[]{
                            split[1]
                    };
                    path = getDataColumn(ContextKeep.getContext(), contentUri, selection, selectionArgs);
                } catch (IllegalArgumentException e) {
                    path = null;
                }
            }
        }
        if (path == null) {
            Cursor cursor = ContextKeep.getContext().getContentResolver().query(uri, new String[]{MediaStore.Images.Media.DATA}, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                path = cursor.getString(index);
                cursor.close();
            }
        }
        return path;
    }

    /**
     * 从URI获取文件地址
     *
     * @param context 上下文
     * @param uri     文件uri
     */
    public static String getFilePath(Context context, Uri uri) {
        // DocumentProvider
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }


    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    private static String getDataColumn(Context context, Uri uri, String selection,
                                        String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * 判断外部存储是否可用
     */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) || !Environment.isExternalStorageRemovable();
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    @StringDef({FileType.IMG, FileType.AUDIO, FileType.VIDEO, FileType.FILE})
    public @interface FileType {
        String IMG = "IMG";
        String AUDIO = "AUDIO";
        String VIDEO = "VIDEO";
        String FILE = "FILE";
    }

}
