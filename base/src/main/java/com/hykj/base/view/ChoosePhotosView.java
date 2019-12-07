package com.hykj.base.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.hykj.base.R;
import com.hykj.base.listener.SingleOnClickListener;
import com.hykj.base.utils.DateUtils;
import com.hykj.base.utils.DisplayUtils;
import com.hykj.base.utils.bitmap.BitmapUtils;
import com.hykj.base.utils.storage.FileUtil;
import com.hykj.base.view.activity.PickerImageActivity;
import com.hykj.base.view.activity.PreviewImgActivity;


import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;

/**
 * created by cjf
 * on: 2019/10/24
 * 自定义选择照片
 * 设置android_layout_width、android_layout_height时，请要么都设置wrap，要么设置一样的固定大小
 */
public class ChoosePhotosView extends LinearLayout {
    private static final int REQ_PICKER_IMAGE = 0x20;//选择图片
    private RequestManager manager;
    private WeakReference<Activity> mContext;
    private WeakReference<Fragment> mFragment;

    private RelativeLayout layoutPhoto;
    private ImageView ivPhoto;//照片
    private ImageView ivDel;//删除照片
    private boolean isCanShowBigPhoto = true;//是否可以查看大图
    private boolean isCanEditPhoto = true;//是否可以编辑照片

    private Drawable defaultAddDrawable;//默认照片
    private Drawable defaultDelDrawable;//默认删除图标
    private Drawable placeholderDrawable;//默认占位符图片

    private String photo;//当前显示的url、path图片地址
    private String outPathFileName;//拍照时的保存的图片名 注意.jpg或者png结尾


    public ChoosePhotosView(Context context) {
        this(context, null);
    }

    public ChoosePhotosView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChoosePhotosView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.layout_choose_photos_view, this, true);
        initView();
        initAttr(context, attrs);
    }

    private void initView() {
        manager = Glide.with(getContext());

        layoutPhoto = findViewById(R.id.layout_photo);
        ivPhoto = findViewById(R.id.iv_photo);
        ivPhoto.setOnClickListener(onClickListener);
        ivDel = findViewById(R.id.iv_del_photo);
        ivDel.setOnClickListener(onClickListener);
    }

    private void initAttr(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray t = context.obtainStyledAttributes(attrs, R.styleable.ChoosePhotosView);
            defaultAddDrawable = t.getDrawable(R.styleable.ChoosePhotosView_cpv_default_add_icon);
            defaultDelDrawable = t.getDrawable(R.styleable.ChoosePhotosView_cpv_default_del_icon);
            placeholderDrawable = t.getDrawable(R.styleable.ChoosePhotosView_cpv_default_placeholder);
            isCanShowBigPhoto = t.getBoolean(R.styleable.ChoosePhotosView_cpv_can_show_big_photo, true);
            int width = t.getLayoutDimension(R.styleable.ChoosePhotosView_android_layout_width, -2);
            int height = t.getLayoutDimension(R.styleable.ChoosePhotosView_android_layout_height, -2);
            t.recycle();
        }
        if (defaultAddDrawable == null) {
            defaultAddDrawable = getResources().getDrawable(R.mipmap.ic_choose_photo_add);
        }
        if (defaultDelDrawable == null) {
            defaultDelDrawable = getResources().getDrawable(R.mipmap.ic_choose_photo_del);
        }
        if (placeholderDrawable == null) {
            placeholderDrawable = getResources().getDrawable(R.mipmap.ic_choose_photo_placeholder);
        }
        ivPhoto.setImageDrawable(defaultAddDrawable);
        ivDel.setImageDrawable(defaultDelDrawable);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        ViewGroup.LayoutParams params = ivPhoto.getLayoutParams();
        params.width = w - getPaddingLeft() - getPaddingRight() - layoutPhoto.getPaddingLeft() - layoutPhoto.getPaddingRight();
        params.height = h - getPaddingTop() - getPaddingBottom() - layoutPhoto.getPaddingTop() - layoutPhoto.getPaddingBottom();
        ivPhoto.setLayoutParams(params);
    }

    private SingleOnClickListener onClickListener = new SingleOnClickListener() {
        @Override
        public void onClickSub(View v) {
            int i = v.getId();
            if (i == R.id.iv_photo) {
                if (photo != null) {
                    if (isCanShowBigPhoto) {
                        ArrayList<String> imgList = new ArrayList<>();
                        imgList.add(photo);
                        PreviewImgActivity.start(getContext(), imgList, 0, false);
                    }
                } else {
                    if (mContext == null && mFragment == null)
                        throw new RuntimeException("Call the from() method first");
                    if (!isCanEditPhoto)
                        return;
                    Fragment fragment = mFragment.get();
                    String outPath = getOutPath();
                    if (fragment != null)
                        PickerImageActivity.start(fragment, REQ_PICKER_IMAGE, outPath);
                    else
                        PickerImageActivity.start(mContext.get(), REQ_PICKER_IMAGE, outPath);
                }
            } else if (i == R.id.iv_del_photo) {
                if (!isCanEditPhoto)
                    return;
                ivPhoto.setImageDrawable(defaultAddDrawable);
                ivDel.setVisibility(View.GONE);
                photo = null;
            }
        }
    };

    public ChoosePhotosView setCanShowBigPhoto(boolean canShowBigPhoto) {//是否可以查看照片大图
        isCanShowBigPhoto = canShowBigPhoto;
        return this;
    }

    public ChoosePhotosView setCanEditPhoto(boolean canEditPhoto) {//设置是否可编辑图片
        isCanEditPhoto = canEditPhoto;
        return this;
    }

    public ChoosePhotosView setOutPathFileName(String outPathFileName) {//设置拍照保存的名字
        this.outPathFileName = outPathFileName;
        return this;
    }

    public ChoosePhotosView setPhoto(String photo) {//设置图片(可以是本地、url路径)
        RequestOptions options = new RequestOptions().centerCrop().placeholder(placeholderDrawable);
        manager.load(photo).apply(options).into(ivPhoto);
        this.photo = photo;
        return this;
    }

    public String getPhoto() {//获取图片
        return photo;
    }

    private String getOutPath() {
        String fileName = outPathFileName;
        if (fileName == null) {
            fileName = "choose_photos_" + DateUtils.getFormatDate(new Date(System.currentTimeMillis()), DateUtils.DateFormatType.DF_NORMAL) + ".jpg";
        }
        return FileUtil.getCacheFilePath(fileName, FileUtil.FileType.IMG);
    }

    public ChoosePhotosView from(Activity activity) {
        return from(activity, null);
    }

    //需要初始化调用该方法或者from(Activity activity)方法,否则无法使用该控件
    public ChoosePhotosView from(Fragment fragment) {
        return from(fragment.getActivity(), fragment);
    }

    private ChoosePhotosView from(Activity activity, Fragment fragment) {
        mContext = new WeakReference<>(activity);
        mFragment = new WeakReference<>(fragment);
        return this;
    }

    //需要在fragment或者Activity的onActivityResult方法中调用该方法
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQ_PICKER_IMAGE://选择凭证
                    if (data != null) {
                        String outPath = data.getStringExtra(PickerImageActivity.OUT_PATH);
                        int size = DisplayUtils.size2px(TypedValue.COMPLEX_UNIT_DIP, 80);
                        Bitmap bitmap = BitmapUtils.decodeSampledBitmapFromPath(outPath, size, size);
                        if (bitmap != null) {
                            ivPhoto.setImageBitmap(bitmap);
                            ivDel.setVisibility(View.VISIBLE);
                            this.photo = outPath;
                        } else {
                            Toast.makeText(getContext(), "照片出错", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
            }
        }
    }
}
