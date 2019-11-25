package com.hykj.base.view.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.hykj.base.R;
import com.hykj.base.adapter.pager.BasePagerAdapter;
import com.hykj.base.adapter.pager.ViewHolder;
import com.hykj.base.base.TitleActivity;
import com.hykj.base.listener.SingleOnClickListener;
import com.hykj.base.utils.DateUtils;
import com.hykj.base.utils.bitmap.BitmapUtils;
import com.hykj.base.utils.storage.FileUtil;
import com.hykj.base.utils.text.Tip;
import com.hykj.base.view.TitleView;
import com.hykj.base.view.ZoomImageView;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class PreviewImgActivity extends TitleActivity {
    private static final String IMG_LIST = "img_list";//图片集合
    private static final String SAVE = "isSave";//是否显示保存按钮
    private static final String INDEX = "index";//当前图片位置
    private static final String IS_BASE64 = "isBase64";//是否是base64字符串

    private BasePagerAdapter<String> pagerAdapter;
    private ArrayList<String> imgList = new ArrayList<>();
    private ViewPager viewPager;
    private int currentPosition;
    private boolean isSave;
    private boolean isBase64;

    private TextView tvPage;
    private RequestManager manager;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_preview_img;
    }

    @Override
    protected void init(TitleView title) {
        title.setTitle("预览");
        initData();
        initView();
    }

    private void initData() {
        imgList = getIntent().getStringArrayListExtra(IMG_LIST);
        isSave = getIntent().getBooleanExtra(SAVE, false);
        isBase64 = getIntent().getBooleanExtra(IS_BASE64, false);
        currentPosition = getIntent().getIntExtra(INDEX, 0);
        if (imgList.size() == 0) {
            finish();
            return;
        }
        manager = Glide.with(mActivity);
    }

    private void initView() {
        findViewById(R.id.tv_save).setVisibility(isSave ? View.VISIBLE : View.GONE);
        findViewById(R.id.tv_save).setOnClickListener(onClickListener);
        tvPage = findViewById(R.id.tv_page);
        setPageNumber(currentPosition);

        pagerAdapter = createPagerAdapter(imgList);
        viewPager = findViewById(R.id.vp_view);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(currentPosition);
        viewPager.addOnPageChangeListener(onPageChangeListener);
    }

    /**
     * 适配器
     *
     * @param list
     * @return
     */
    private BasePagerAdapter<String> createPagerAdapter(List<String> list) {
        return new BasePagerAdapter<String>(mActivity, list, R.layout.item_viewpager_zoomview) {
            @Override
            protected void convert(ViewHolder holder, String url, int position) {
                ZoomImageView itemView = holder.getView(R.id.zoom_view);
                itemView.initScale();
                if (isBase64) {
                    manager.load(BitmapUtils.base64ToBitmap(url, url.contains("%"))).into(itemView);
                } else {
                    manager.load(url).into(itemView);
                }
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        return true;
                    }
                });
            }
        };
    }

    /**
     * 滑动监听
     */
    private ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {//选中新页面时会调用该方法
            if (viewPager.getAdapter() != null) {
                View convertView = ((BasePagerAdapter) viewPager.getAdapter()).getPrimaryItem();
                if (convertView != null) {
                    ZoomImageView view = convertView.findViewById(R.id.zoom_view);
                    if (view != null) {
                        view.resetScale();
                    }
                }
            }
            setPageNumber(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    /**
     * 设置页码
     *
     * @param position
     */
    private void setPageNumber(int position) {
        currentPosition = position;
        if (imgList.size() != 0) {
            tvPage.setText(String.format("%s/%s", position + 1, imgList.size()));
        }
    }

    /**
     * 通用点击
     */
    private SingleOnClickListener onClickListener = new SingleOnClickListener() {
        @Override
        public void onClickSub(View view) {
            int i = view.getId();
            if (i == R.id.tv_save) {
                saveImage();
            }
        }
    };

    /**
     * 保存图片
     */
    private void saveImage() {
        View convertView = pagerAdapter.getPrimaryItem();
        if (convertView != null) {
            ZoomImageView zoomImageView = convertView.findViewById(R.id.zoom_view);
            Bitmap.CompressFormat compressFormat = Bitmap.CompressFormat.JPEG;
            try {
                String strFileName = imgList.get(currentPosition);
                if (!isBase64 && strFileName.contains(".png")) {
                    compressFormat = Bitmap.CompressFormat.PNG;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            zoomImageView.buildDrawingCache(true);
            zoomImageView.buildDrawingCache();//启用DrawingCache并创建位图
            Bitmap bitmap = zoomImageView.getDrawingCache();//拷贝，因为DrawingCache得到的位图在禁用后会被回收
            saveBitmapFile(bitmap, compressFormat);
            zoomImageView.setDrawingCacheEnabled(false);//创建一个DrawingCache的拷贝，因为DrawingCache得到的位图在禁用后会被回收
        }
    }

    /**
     * 保存图片到本地
     *
     * @param bitmap
     * @param compressFormat
     */
    private void saveBitmapFile(Bitmap bitmap, Bitmap.CompressFormat compressFormat) {
        String suffix = compressFormat == Bitmap.CompressFormat.PNG ? ".png" : ".jpg";
        String fileName = DateUtils.getFormatDate(null, DateUtils.DateFormatType.DF_NORMAL) + suffix;
        if (BitmapUtils.saveBitmapToSDCard(bitmap, compressFormat, fileName, false)) {
            Tip.showShort(String.format("图片已经保存至:%s", FileUtil.getCacheFilePath(fileName, FileUtil.FileType.IMG)));
        }
        //刷新图库
        //scanAsync(file, Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        //scanAsync(new File(FileUtil.getFileTypePath(FileUtil.FileType.IMG)), "android.intent.action.MEDIA_SCANNER_SCAN_DIR");
    }

    /**
     * 刷新图册，小米手机无反应
     *
     * @param file
     * @param action
     */
    private void scanAsync(File file, String action) {
        Intent intent = new Intent(action);
        Uri data;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            data = FileProvider.getUriForFile(mActivity, getPackageName() + ".FileProvider", file);
        } else {
            data = Uri.fromFile(file);
        }
        intent.setData(data);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        sendBroadcast(intent);
    }

    public static void start(Context context, ArrayList<String> list, int index, boolean isSave) {
        start(context, list, index, isSave, false, PreviewImgActivity.class);
    }

    public static void start(Context context, ArrayList<String> list, int index, boolean isSave, boolean isBase64) {
        start(context, list, index, isSave, isBase64, PreviewImgActivity.class);
    }

    /**
     * @param context  上下文
     * @param list     图片列表
     * @param index    起始显示位置
     * @param isSave   是否显示保存按钮
     * @param isBase64 是否是base64字符串
     * @param cls      可继承该类并传入，用于修改标题颜色等
     */
    public static void start(Context context, ArrayList<String> list, int index, boolean isSave, boolean isBase64, Class<? extends PreviewImgActivity> cls) {
        Intent intent = new Intent(context, cls);
        intent.putStringArrayListExtra(IMG_LIST, list);
        intent.putExtra(INDEX, index);
        intent.putExtra(SAVE, isSave);
        intent.putExtra(IS_BASE64, isBase64);
        context.startActivity(intent);
    }
}
