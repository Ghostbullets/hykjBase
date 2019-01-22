package com.hykj.hykjbase;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.hykj.base.listener.SingleOnClickListener;
import com.hykj.base.view.activity.PickerImageActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.tv_picker).setOnClickListener(new SingleOnClickListener() {
            @Override
            public void onClickSub(View v) {
                PickerImageActivity.start(MainActivity.this,1,getCacheDir().getAbsolutePath()+"picket_img.png");
            }
        });
    }
}
