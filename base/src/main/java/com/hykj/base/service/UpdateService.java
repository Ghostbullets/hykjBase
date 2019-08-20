package com.hykj.base.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.hykj.base.R;
import com.hykj.base.bean.UpdateTransInfo;
import com.hykj.base.utils.ContextKeep;
import com.hykj.base.utils.auth.FileProviderUtils;
import com.hykj.base.utils.text.Tip;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import zlc.season.rxdownload3.RxDownload;
import zlc.season.rxdownload3.core.Downloading;
import zlc.season.rxdownload3.core.Failed;
import zlc.season.rxdownload3.core.Mission;
import zlc.season.rxdownload3.core.Status;
import zlc.season.rxdownload3.core.Succeed;

/**
 * 用于下载apk文件
 */
public class UpdateService extends Service {
    public static final String UPDATE_TRANS_INFO = "updateTransInfo";
    private static final int notificationId = 0x1357;
    private static final String id = "static";
    private static final String name = "下载消息";

    private UpdateTransInfo info;//携带数据
    //通知栏
    private NotificationManager notificationManager;
    private Notification.Builder builder;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String json = intent.getStringExtra(UPDATE_TRANS_INFO);
        info = new Gson().fromJson(json, UpdateTransInfo.class);

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_HIGH);
            //channel.enableVibration(true);
            notificationManager.createNotificationChannel(channel);
            builder = new Notification.Builder(this, id);
        } else {
            builder = new Notification.Builder(this);
            //.setDefaults(Notification.DEFAULT_ALL);//设置默认的提示音，振动方式，灯光
        }
        builder.setSmallIcon(info.getIcon())//设置图标
                .setTicker(String.format("%s%s", info.appName, ContextKeep.getContext().getResources().getString(R.string.Start_the_download)))
                .setContentTitle(info.appName)//设置标题
                .setContentText(ContextKeep.getContext().getResources().getString(R.string.Waiting_for_download))//消息内容
                .setWhen(System.currentTimeMillis())//发送时间
                .setAutoCancel(false)//打开程序后图标不消失
                .setProgress(100, 0, false);
        notificationManager.notify(notificationId, builder.build());

        if (info.saveFile.exists()) {
            info.isAutoInstall = true;
            downLoadSuccess();
        } else {
          /*  File download = new File(FileUtil.getCacheFilePath(appName + ".apk.download", FileUtil.getFileTypePath(FileUtil.FileType.FILE)));
            if (download.exists()) {
                download.delete();
            }*/
            downLoad(info.downloadUrl);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 下载
     *
     * @param downloadUrl
     */
    private void downLoad(String downloadUrl) {
        Mission mission = new Mission(downloadUrl, info.saveName, info.savePath);
        RxDownload.INSTANCE.create(mission, true).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Status>() {
                    @Override
                    public void accept(Status status) throws Exception {
                        if (status instanceof Failed) {
                            Tip.showShort(((Failed) status).getThrowable().getMessage());
                            stopSelf();
                        } else if (status instanceof Succeed) {
                            downLoadSuccess();
                        } else if (status instanceof Downloading) {
                            int progress = (int) (100 * status.getDownloadSize() / status.getTotalSize());
                            builder.setProgress(100, progress, false)
                                    .setContentText(ContextKeep.getContext().getResources().getString(R.string.has_downloading, progress));
                            notificationManager.notify(notificationId, builder.build());
                        }
                    }
                });
    }

    private void downLoadSuccess() {
        Intent installIntent = new Intent(Intent.ACTION_VIEW);
        installIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            installIntent.setDataAndType(FileProviderUtils.getUriForFile(UpdateService.this, info.saveFile), "application/vnd.android.package-archive");
            installIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            installIntent.setDataAndType(Uri.fromFile(info.saveFile), "application/vnd.android.package-archive");
        }
        if (info.isAutoInstall) {
            startActivity(installIntent);
            notificationManager.cancel(notificationId);
        } else {
            PendingIntent pendingIntent = PendingIntent.getActivity(UpdateService.this, 0, installIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentText(ContextKeep.getContext().getResources().getString(R.string.When_the_download_is_complete_click_install))
                    .setContentIntent(pendingIntent);
            notificationManager.notify(notificationId, builder.build());
        }
        stopSelf();
    }

    /**
     * 开启service
     *
     * @param context         上下文
     * @param updateTransInfo 跳转携带数据
     */
    public static void start(Context context, UpdateTransInfo updateTransInfo) {
        if (updateTransInfo == null || TextUtils.isEmpty(updateTransInfo.downloadUrl))
            return;
        Intent intent = new Intent(context, UpdateService.class);
        intent.putExtra(UPDATE_TRANS_INFO, new Gson().toJson(updateTransInfo));
        context.startService(intent);
    }
}
