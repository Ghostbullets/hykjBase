package com.hykj.hykjbase;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.request.RequestOptions;
import com.zhihu.matisse.engine.ImageEngine;

public class MyGlideEngine implements ImageEngine {
    @Override
    public void loadThumbnail(Context context, int resize, Drawable placeholder, ImageView imageView, Uri uri) {
        RequestOptions requestOptions = new RequestOptions().placeholder(placeholder)
                .override(100, 100)
                .centerCrop();
        Glide.with(context)
                .asBitmap()  // some .jpeg files are actually gif
                .load(uri)
                .apply(requestOptions)
                .into(imageView);
    }

    @Override
    public void loadGifThumbnail(Context context, int resize, Drawable placeholder, ImageView imageView,
                                 Uri uri) {
        RequestOptions requestOptions = new RequestOptions().placeholder(placeholder)
                .centerCrop()
                .override(resize, resize);
        Glide.with(context)
                .asBitmap()
                .load(uri)
                .apply(requestOptions)
                .into(imageView);
    }

    @Override
    public void loadImage(Context context, int resizeX, int resizeY, ImageView imageView, Uri uri) {
        RequestOptions requestOptions = new RequestOptions().priority(Priority.HIGH)
                .fitCenter()
                .override(resizeX, resizeY);

        Glide.with(context)
                .load(uri)
                .apply(requestOptions)
                .into(imageView);
    }

    @Override
    public void loadGifImage(Context context, int resizeX, int resizeY, ImageView imageView, Uri uri) {
        RequestOptions requestOptions = new RequestOptions().priority(Priority.HIGH)
                .fitCenter()
                .override(resizeX, resizeY);
        Glide.with(context)
                .asGif()
                .load(uri)
                .apply(requestOptions)
                .into(imageView);
    }

    @Override
    public boolean supportAnimatedGif() {
        return true;
    }
}