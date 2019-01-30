package com.hykj.base.utils.view;

import android.content.Context;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.LevelListDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.widget.TextView;

import com.hykj.base.utils.DisplayUtils;
import com.hykj.base.utils.bitmap.BitmapUtils;

/**
 * textView显示富文本
 */
public class MImageGetter implements Html.ImageGetter {
    private Context c;
    private TextView container;
    private int scaleWidth;

    public MImageGetter(Context c, TextView container) {
        this.c = c;
        this.container = container;
        this.scaleWidth = new DisplayUtils().screenWidth();
    }

    public MImageGetter(Context c, TextView container, int scaleWidth) {
        this.c = c;
        this.container = container;
        this.scaleWidth = scaleWidth;
    }

    @Override
    public Drawable getDrawable(String source) {
        final LevelListDrawable drawable = new LevelListDrawable();
        Glide.with(c).load(source).into(new SimpleTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                Drawable zoomDrawable = BitmapUtils.zoomImage(resource, scaleWidth, BitmapUtils.ZoomType.ZOOM);
                drawable.addLevel(1, 1, zoomDrawable);
                drawable.setBounds(0, 0, zoomDrawable.getIntrinsicWidth(), zoomDrawable.getIntrinsicHeight());
                drawable.setLevel(1);

                container.invalidate();
                container.setText(container.getText());
            }
        });

        return drawable;
    }
}
