package com.hykj.base.utils.storage;

import android.text.TextUtils;
import android.util.Base64;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * created by cjf
 * on:2019/5/5 15:48
 */
public class EncryptUtils {

    private static final char[] DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public static String getMd5Encrypt(String plaintext) {
        if (TextUtils.isEmpty(plaintext))
            return "";
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] bytes = digest.digest(plaintext.getBytes());
            int j = bytes.length;
            int k = 0;
            char str[] = new char[j * 2];
            for (int i = 0; i < j; i++) {
                str[k++] = DIGITS[bytes[i] >> 4 & 0xF];
                str[k++] = DIGITS[bytes[i] & 0xF];
            }
            return new String(str);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 将图片转换成Base64编码的字符串
     *
     * @param path
     * @return base64编码的字符串
     */
    public static String imageToBase64(String path) {
        if (TextUtils.isEmpty(path))
            return null;
        InputStream is = null;
        try {
            is = new FileInputStream(path);
            byte[] data = new byte[is.available()];
            is.read(data);
            return Base64.encodeToString(data, Base64.NO_WRAP);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * base64编码字符集转化成图片文件。
     *
     * @param base64Str
     * @param path      文件存储路径
     * @return 是否成功
     */
    public static boolean base64ToFile(String base64Str, String path) {
        if (TextUtils.isEmpty(base64Str) || TextUtils.isEmpty(path))
            return false;
        byte[] data = Base64.decode(base64Str, Base64.NO_WRAP);
        for (int i = 0; i < data.length; i++) {
            if (data[i] < 0)
                data[i] += 256;
        }
        boolean result;
        OutputStream os = null;
        try {
            File file = new File(path);
            if (!file.exists())
                file.createNewFile();
            os = new FileOutputStream(path);
            os.write(data);
            os.flush();
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
            result = false;
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }
}
