package com.hykj.base.utils.storage;

import java.io.Closeable;
import java.io.IOException;

public class CloseableUtils {
    /**
     * 关闭文件流
     *
     * @param closeables 文件流数组
     */
    public static void close(Closeable... closeables) {
        if (closeables != null) {
            try {
                for (Closeable closeable : closeables) {
                    if (closeable != null)
                        closeable.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
