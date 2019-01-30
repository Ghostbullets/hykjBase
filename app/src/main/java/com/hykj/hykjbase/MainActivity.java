package com.hykj.hykjbase;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.hykj.base.listener.SingleOnClickListener;
import com.hykj.base.utils.bitmap.BitmapUtils;
import com.hykj.base.utils.storage.FileUtil;
import com.hykj.base.view.activity.PickerImageActivity;

public class MainActivity extends AppCompatActivity {
    private static final int REQ_PHOTO = 0x01;
    private ImageView ivImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ivImg = findViewById(R.id.iv_img);
        findViewById(R.id.tv_picker).setOnClickListener(new SingleOnClickListener() {
            @Override
            public void onClickSub(View v) {
                PickerImageActivity.start(MainActivity.this, REQ_PHOTO, null, false);
            }
        });
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
