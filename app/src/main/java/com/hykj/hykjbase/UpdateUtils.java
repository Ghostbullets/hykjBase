package com.hykj.hykjbase;

import android.Manifest;
import android.view.View;

import com.hykj.base.base.BaseActivity;
import com.hykj.base.bean.AppVersionInfo;
import com.hykj.base.bean.UpdateTransInfo;
import com.hykj.base.dialog.UpdateVersionDialogFragment;
import com.hykj.base.listener.OnSelectClickListener;
import com.hykj.base.service.UpdateService;
import com.hykj.base.utils.IntentUtils;
import com.hykj.base.utils.storage.FileUtil;
import com.tbruyelle.rxpermissions2.RxPermissions;


import java.io.File;

import io.reactivex.functions.Consumer;

/**
 * 更新工具类
 */
public class UpdateUtils {

    /**
     * 更新app
     *
     * @param activity 活动
     */
    public static void updateAPP(final BaseActivity activity) {
      /*  //显示版本升级弹窗
        final String saveName = AppUtils.getAppName(activity) + "AGT9601v1.0.0" + ".apk";
        final String savePath = FileUtil.getFileTypePath(FileUtil.FileType.FILE);
        final boolean isForceUpdate = true;
        final File file = new File(savePath + saveName);

        new UpdateVersionDialogFragment()
                .setData(new AppVersionInfo(AppUtils.getAppName(activity) + "AGT9601v1.0.0", "v1.0.0"
                        , null, "11MB", "1.更新app", "2018-08-11 11:11:11", "http://121.40.86.51:8088/project/anyutechnology/android.apk",
                        isForceUpdate), file.exists() ? "立即安装" : "立即更新", null)
                .setOnSelectClickListener(new OnSelectClickListener() {
                    @Override
                    public void onConfirm(View v) {
                        new RxPermissions(activity)
                                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                .subscribe(new Consumer<Boolean>() {
                                    @Override
                                    public void accept(Boolean aBoolean) throws Exception {
                                        if (aBoolean) {
                                            if (file.exists()) {
                                                IntentUtils.installApk(activity, file);
                                            } else {
                                                if (isForceUpdate) {//如果是强制更新，则显示更新中弹窗并且无法取消
                                                    new UpdatingDialogFragment()
                                                            .show(activity.getSupportFragmentManager(), "UpdatingDialogFragment");
                                                }
                                                UpdateService.start(activity, new UpdateTransInfo(AppUtils.getAppName(activity),
                                                        "http://121.40.86.51:8088/project/anyutechnology/android.apk", R.mipmap.ic_launcher, true, saveName, savePath));
                                            }
                                        }
                                    }
                                });
                    }

                    @Override
                    public void onCancel(View v) {
                        if (isForceUpdate) {//强制升级情况下不升级退出APP
                            activity.finishAffinity();
                        }
                    }
                })
                .show(activity.getSupportFragmentManager(), "UpdateVersionDialogFragment");*/
      /*  CommandReq req = new CommandReq("update");
        RxJavaHelper.getInstance().toSubscribe(req.init().getLaterAppVersion(req.getParams()), true, activity, ActivityEvent.DESTROY, new MyProgressSubscribe<AppVersionJSON>(activity) {
            @Override
            protected void onResponse(final AppVersionJSON item) {
                if (listener != null)
                    listener.reqUpdateVersionInfoSuccess();
                if (item.getAppNum() != null && item.getAppNum() > AppUtils.getVersionCode(activity)) {
                    //显示版本升级弹窗
                    final String saveName = AppUtils.getAppName(activity) + item.getAppName() + ".apk";
                    final String savePath = FileUtil.getFileTypePath(FileUtil.FileType.FILE);
                    final boolean isForceUpdate = item.getIsForceUpdate() == 1;
                    final File file = new File(savePath + saveName);

                    new UpdateVersionDialogFragment()
                            .setData(new AppVersionInfo(AppUtils.getAppName(activity) + item.getAppName(), item.getAppName()
                                    , item.getDescribe(), item.getAppSize(), item.getMsg(), item.getUpdateTime(), item.getUrl(),
                                    isForceUpdate), file.exists() ? "立即安装" : "立即更新", null)
                            .setOnSelectClickListener(new OnSelectClickListener() {
                                @Override
                                public void onConfirm(View v) {
                                    new RxPermissions(activity)
                                            .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                            .subscribe(new Consumer<Boolean>() {
                                                @Override
                                                public void accept(Boolean aBoolean) throws Exception {
                                                    if (aBoolean) {
                                                        if (file.exists()) {
                                                            IntentUtils.installApk(activity, file);
                                                        } else {
                                                            if (isForceUpdate) {//如果是强制更新，则显示更新中弹窗并且无法取消
                                                                new UpdatingDialogFragment()
                                                                        .show(activity.getSupportFragmentManager(), "UpdatingDialogFragment");
                                                            }
                                                            UpdateService.start(activity, new UpdateTransInfo(AppUtils.getAppName(activity),
                                                                    item.getUrl(), R.mipmap.ic_launcher, true, saveName, savePath));
                                                        }
                                                    }
                                                }
                                            });
                                }

                                @Override
                                public void onCancel(View v) {
                                    if (isForceUpdate) {//强制升级情况下不升级退出APP
                                        activity.finishAffinity();
                                    }
                                }
                            })
                            .show(activity.getSupportFragmentManager(), "UpdateVersionDialogFragment");
                }
            }
        });*/
    }
}
