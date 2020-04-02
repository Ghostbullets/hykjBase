package com.hykj.base.view.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import androidx.annotation.IntDef;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.text.TextUtils;
import android.view.View;

import com.hykj.base.R;
import com.hykj.base.base.BaseActivity;
import com.hykj.base.dialog.BottomListMenuDialog;
import com.hykj.base.dialog.CommonDialog;
import com.hykj.base.dialog.json.MenuGroup;
import com.hykj.base.dialog.json.MenuItem;
import com.hykj.base.permission.RxPermissions;
import com.hykj.base.utils.DateUtils;
import com.hykj.base.utils.auth.FileProviderUtils;
import com.hykj.base.utils.storage.FileUtil;

import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;

/**
 * 选择相册、拍摄
 */
public class PickerImageActivity extends BaseActivity {
    private static final int REQ_ALBUM = 1000; // 相册
    private static final int REQ_CAMERA = 1001;// 拍照
    private static final int REQ_CROP = 1002;//裁剪

    public static final String OUT_PATH = "outPath";
    private static final String IS_CROP = "isCrop";

    private String outPath;//拍摄的照片输出路径，当选择拍摄时必传，并且返回的也是该值
    private File outFile;
    private boolean isCrop;//是否需要裁剪

    private CompositeDisposable disposable = new CompositeDisposable();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_picker_image;
    }

    @Override
    protected void init() {
        outPath = getIntent().getStringExtra(OUT_PATH);
        isCrop = getIntent().getBooleanExtra(IS_CROP, false);
        if (checkTransStatus()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

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
                        initPic();
                        break;
                    case PhotoType.FROM_LOCAL:
                        initChoose();
                        break;
                }
            }
        }).create().show();
    }

    //打开相机
    public void initPic() {
        disposable.add(new RxPermissions(mActivity).request(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            // 拍照
                            checkOutPath();
                            FileProviderUtils.startOpenCamera(mActivity, REQ_CAMERA, outFile);
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
    public void initChoose() {
        disposable.add(new RxPermissions(mActivity).request(Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            FileProviderUtils.startOpenAlbum(mActivity, REQ_ALBUM, FileProviderUtils.ImageType.ALL);
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
        localIntent.setData(Uri.fromParts("package", getPackageName(), null));
        startActivity(localIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CAMERA) {//拍照
            if (resultCode == RESULT_OK) {
                if (isCrop)
                    FileProviderUtils.startCropPicture(mActivity, REQ_CROP, outFile, outFile);
                else
                    setResultData();
            }
            if (!isCrop)
                finish();
        } else if (requestCode == REQ_ALBUM) {//相册选取
            if (resultCode == RESULT_OK && data != null) {
                if (isCrop) {
                    checkOutPath();
                    FileProviderUtils.startCropPicture(mActivity, REQ_CROP, data.getData(), FileProviderUtils.getUriForFile(mActivity, outFile, true));
                } else {
                    outPath = FileUtil.getImageFilePath(data.getData());//根据Uri获取图片本地路径
                    setResultData();
                }
            }
            if (!isCrop)
                finish();
        } else if (requestCode == REQ_CROP) {//裁剪
            if (resultCode == RESULT_OK) {
                setResultData();
            }
            finish();
        }
    }

    //判断outPath是否为空，为空则设置路径
    private void checkOutPath() {
        File dir = FileUtil.isExternalStorageWritable() ? getExternalCacheDir() : getCacheDir();
        if (TextUtils.isEmpty(outPath)) {
            if (dir != null) {
                outPath = dir.getAbsolutePath() + "/" + DateUtils.getFormatDate(null, DateUtils.DateFormatType.DF_NORMAL) + ".png";
            }
        }
        outFile = FileUtil.createNewFile(outPath);
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

    public static void start(Activity activity, int requestCode, String outPath) {
        start(activity, requestCode, outPath, false);
    }

    /**
     * 开启画面
     *
     * @param activity    活动
     * @param requestCode 请求码
     * @param outPath     拍摄、裁剪的照片的输出目录
     * @param isCrop      是否裁剪
     */
    public static void start(Activity activity, int requestCode, String outPath, boolean isCrop) {
        Intent intent = new Intent(activity, PickerImageActivity.class);
        intent.putExtra(OUT_PATH, outPath);
        intent.putExtra(IS_CROP, isCrop);
        activity.startActivityForResult(intent, requestCode);
    }

    public static void start(Fragment fragment, int requestCode, String outPath) {
        start(fragment, requestCode, outPath, false);
    }

    public static void start(Fragment fragment, int requestCode, String outPath, boolean isCrop) {
        Intent intent = new Intent(fragment.getContext(), PickerImageActivity.class);
        intent.putExtra(OUT_PATH, outPath);
        intent.putExtra(IS_CROP, isCrop);
        fragment.startActivityForResult(intent, requestCode);
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({PhotoType.FROM_CAMERA, PhotoType.FROM_LOCAL})
    public @interface PhotoType {//选择照片类型  0 去拍照，1从本地相册中选择
        int FROM_CAMERA = 0;
        int FROM_LOCAL = 1;
    }
}
