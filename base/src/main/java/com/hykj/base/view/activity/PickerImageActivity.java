package com.hykj.base.view.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.WindowManager;

import com.hykj.base.R;
import com.hykj.base.base.BaseActivity;
import com.hykj.base.dialog.BottomListMenuDialog;
import com.hykj.base.dialog.CommonDialog;
import com.hykj.base.dialog.ShowPermissionDialog;
import com.hykj.base.dialog.json.MenuGroup;
import com.hykj.base.dialog.json.MenuItem;
import com.hykj.base.utils.ContextKeep;
import com.hykj.base.utils.auth.FileProviderUtils;
import com.hykj.base.utils.storage.FileUtil;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;

/**
 * 选择相册、拍摄
 */
public class PickerImageActivity extends BaseActivity {
    public static final int REQ_ALBUM = 1000; // 相册
    public static final int REQ_CAMERA = 1001;// 拍照

    public static final String OUT_PATH = "outPath";

    private String outPath;//拍摄的照片输出目录，当选择拍摄时必传，并且返回的也是该值

    private CompositeDisposable disposable = new CompositeDisposable();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_picker_image;
    }

    @Override
    protected void init() {
        outPath = getIntent().getStringExtra(OUT_PATH);

        MenuGroup menuGroup = new MenuGroup();
        menuGroup.addMenu(new MenuItem("拍照", PhotoType.FROM_CAMERA));
        menuGroup.addMenu(new MenuItem("相册", PhotoType.FROM_LOCAL));
        new BottomListMenuDialog.Builder(mActivity).setMenuGroup(menuGroup).setListener(new BottomListMenuDialog.MenuListListener() {
            @Override
            public void onDismiss() {
                finish();
            }

            @Override
            public void onMenuClick(int position, MenuItem item, View v, BottomListMenuDialog dialog) {
                @PhotoType int type = (int) item.getTag();
                switch (type) {
                    case PhotoType.FROM_CAMERA:
                        initPic(REQ_CAMERA);
                        break;
                    case PhotoType.FROM_LOCAL:
                        initChoose(REQ_ALBUM);
                        break;
                }
            }
        }).create().show();
    }

    //打开相机
    public void initPic(final int requestCode) {
        disposable.add(new RxPermissions(mActivity).request(Manifest.permission.CAMERA)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            // 拍照
                            FileProviderUtils.startOpenCamera(mActivity, requestCode, new File(outPath));
                        } else {
                            new CommonDialog().setData("提示", "需要获取相机权限用于拍摄凭证", "去设置", "取消").setListener(new CommonDialog.OnSelectClickListener() {
                                @Override
                                public void onConfirm(View v) {
                                    startToDetailsSetting();
                                    finish();
                                }

                                @Override
                                public void onCancel(View v) {
                                    finish();
                                }
                            }).show(mActivity.getSupportFragmentManager(), "CommonDialog");
                        }
                    }
                }));
    }

    //打开相册
    public void initChoose(final int requestCode) {
        disposable.add(new RxPermissions(mActivity).request(Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            FileProviderUtils.startOpenAlbum(mActivity, requestCode, FileProviderUtils.ImageType.ALL);
                        } else {
                            new CommonDialog().setData("提示", "需要获取存储权限用户选择凭证", "去设置", "取消").setListener(new CommonDialog.OnSelectClickListener() {
                                @Override
                                public void onConfirm(View v) {
                                    startToDetailsSetting();
                                    finish();
                                }

                                @Override
                                public void onCancel(View v) {
                                    finish();
                                }
                            }).show(mActivity.getSupportFragmentManager(), "CommonDialog");
                        }
                    }
                }));
    }

    //前往应用设置界面
    public void startToDetailsSetting() {
        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        localIntent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        localIntent.setData(Uri.fromParts("package", ContextKeep.getContext().getPackageName(), null));
        startActivity(localIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CAMERA) {
            if (resultCode == RESULT_OK) {
                setResultData();
            }
            finish();
        } else if (requestCode == REQ_ALBUM) {
            if (resultCode == RESULT_OK && data != null) {
                outPath = FileUtil.getImageFilePath(data.getData());//根据Uri获取图片本地路径
                setResultData();
            }
            finish();
        }
    }

    //设置回调数据
    public void setResultData() {
        Intent intent = new Intent();
        intent.putExtra(OUT_PATH, outPath);
        setResult(RESULT_OK, intent);
    }

    @Override
    protected void onDestroy() {
        if (!disposable.isDisposed())
            disposable.dispose();
        super.onDestroy();
    }

    /**
     * 开启画面
     *
     * @param activity    活动
     * @param requestCode 请求码
     * @param outPath     拍摄的照片的输出目录
     */
    public static void start(Activity activity, int requestCode, String outPath) {
        Intent intent = new Intent(activity, PickerImageActivity.class);
        intent.putExtra(OUT_PATH, outPath);
        activity.startActivityForResult(intent, requestCode);
    }

    public static void start(Fragment fragment, int requestCode, String outPath) {
        Intent intent = new Intent(fragment.getContext(), PickerImageActivity.class);
        intent.putExtra(OUT_PATH, outPath);
        fragment.startActivityForResult(intent, requestCode);
    }

    @IntDef({PhotoType.FROM_CAMERA, PhotoType.FROM_LOCAL})
    public @interface PhotoType {//选择照片类型  0 去拍照，1从本地相册中选择
        int FROM_CAMERA = 0;
        int FROM_LOCAL = 1;
    }
}
