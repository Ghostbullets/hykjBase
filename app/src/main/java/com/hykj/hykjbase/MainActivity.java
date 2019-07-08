package com.hykj.hykjbase;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.hykj.base.adapter.recyclerview2.BaseViewHolder;
import com.hykj.base.adapter.recyclerview2.SimpleRecycleViewAdapter;
import com.hykj.base.bean.AppVersionInfo;
import com.hykj.base.bean.UpdateTransInfo;
import com.hykj.base.dialog.UpdateVersionDialogFragment;
import com.hykj.base.listener.OnConfirmClickListener;
import com.hykj.base.listener.SingleOnClickListener;
import com.hykj.base.service.UpdateService;
import com.hykj.base.utils.DateUtils;
import com.hykj.base.utils.DisplayUtils;
import com.hykj.base.utils.IntentUtils;
import com.hykj.base.utils.bitmap.BitmapUtils;
import com.hykj.base.utils.storage.FileUtil;
import com.hykj.base.utils.view.DividerGridItemDecoration;
import com.hykj.base.utils.view.DividerGridSpacingItemDecoration;
import com.hykj.base.view.activity.PickerImageActivity;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.functions.Consumer;

public class MainActivity extends AppCompatActivity {
    private static final int REQ_PHOTO = 0x01;
    private ImageView ivImg;
    private SimpleRecycleViewAdapter<String> contentAdapter;
    private List<String> contentList = new ArrayList<>();
    private AppVersionInfo appVersionInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ivImg = findViewById(R.id.iv_img);
        findViewById(R.id.tv_picker).setOnClickListener(new SingleOnClickListener() {
            @Override
            public void onClickSub(View v) {
            /*    String outPath = FileUtil.getCacheFilePath(DateUtils.getFormatDate(null, DateUtils.DateFormatType.DF_NORMAL) + ".png", FileUtil.FileType.IMG);
                PickerImageActivity.start(MainActivity.this, REQ_PHOTO, outPath, false);
*/
                new RxPermissions(MainActivity.this)
                        .request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                        .subscribe(new Consumer<Boolean>() {
                            @Override
                            public void accept(Boolean aBoolean) throws Exception {
                                if (aBoolean) {
                                    Matisse.from(MainActivity.this)
                                            .choose(MimeType.ofAll())
                                            .maxSelectable(9)
                                            .originalEnable(true)
                                            .spanCount(4)
                                            .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                                            .thumbnailScale(0.5f)
                                            .theme(com.hykj.base.R.style.Matisse_Zhihu)
                                            .imageEngine(new MyGlideEngine())
                                            .forResult(2);
                                }
                            }
                        });
            }
        });
        for (int i = 0; i < 97; i++) {
            contentList.add(i + "");
        }
        contentAdapter = createContentAdapter(contentList);
        RecyclerView rvContent = findViewById(R.id.rv_content);
        DisplayUtils displayUtils = new DisplayUtils();
        int size = (int) ((displayUtils.screenWidth() - displayUtils.dp2px(35 * 5 + 15 * 2)) / 4.0f);//设置水平间隔为(屏幕宽度-5个item宽度-2个padding)/4
        rvContent.addItemDecoration(new DividerGridSpacingItemDecoration(this, R.drawable.divider_yellow_h_10dp, R.drawable.divider_yellow_v_10dp, size, size));
        //rvContent.addItemDecoration(new DividerGridItemDecoration(this,R.drawable.divider_yellow_h_10dp, R.drawable.divider_yellow_v_10dp,displayUtils.dp2px(10),displayUtils.sp2px(10)));
        rvContent.setLayoutManager(new GridLayoutManager(this, 5));
        rvContent.setAdapter(contentAdapter);
        final File apkFile = new File(FileUtil.getCacheFilePath("智建无忧1.0.0.apk", FileUtil.FileType.FILE));
        appVersionInfo = new AppVersionInfo("智建无忧1.0.0", "1.0.0", "基础库更新", "24MB",
                "1.更新基础库，2更新UI", "2019-04-04 11:11:11", "http://pic32.nipic.com/20130810/7772606_170808427000_2.jpg", true);
        new UpdateVersionDialogFragment()
                .setData(appVersionInfo, apkFile.exists() ? "立即安装" : "立即更新", null)
                .setOnSelectClickListener(new OnConfirmClickListener() {
                    @Override
                    public void onConfirm(View v) {
                        new RxPermissions(MainActivity.this)
                                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                .subscribe(new Consumer<Boolean>() {
                                    @Override
                                    public void accept(Boolean aBoolean) throws Exception {
                                        if (aBoolean) {
                                            if (apkFile.exists()) {
                                                IntentUtils.installApk(MainActivity.this, apkFile);
                                            } else {
                                                UpdateService.start(MainActivity.this, new UpdateTransInfo("智建无忧", "http://121.40.86.51:8088/project/zhijianwuyou/android.apk",
                                                        null, false, "智建无忧1.0.0.apk", FileUtil.getFileTypePath(FileUtil.FileType.FILE)));
                                            }
                                        } else {
                                            finish();
                                        }
                                    }
                                });

                    }
                })
                .show(getSupportFragmentManager(), "UpdateVersionDialogFragment");
    }

    private SimpleRecycleViewAdapter<String> createContentAdapter(List<String> list) {
        return new SimpleRecycleViewAdapter<String>(this, list, R.layout.item_subject_result) {
            @Override
            public void BindData(BaseViewHolder holder, String s, int position, @NonNull List<Object> payloads) {
                holder.setText(R.id.tv_choose, s);
            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_PHOTO && resultCode == RESULT_OK && data != null) {
            String outPath = data.getStringExtra(PickerImageActivity.OUT_PATH);
            Bitmap bitmap = BitmapUtils.decodeSampledBitmapFromPath(outPath, 200, 200);
            ivImg.setImageBitmap(bitmap);
        }
    }
}
