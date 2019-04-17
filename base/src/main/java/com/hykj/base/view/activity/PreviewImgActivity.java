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

    private BasePagerAdapter<String> pagerAdapter;
    private ArrayList<String> imgList = new ArrayList<>();
    private ViewPager viewPager;
    private int currentPosition;

    private TextView tvPage;
    private RequestManager manager;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_preview_img;
    }

    @Override
    protected void init(TitleView title) {
        title.setTitle("预览");
        imgList = getIntent().getStringArrayListExtra(IMG_LIST);
        boolean isSave = getIntent().getBooleanExtra(SAVE, false);
        int index = getIntent().getIntExtra(INDEX, 0);
        if (imgList.size() == 0) {
            finish();
            return;
        }
        manager = Glide.with(mActivity);

        findViewById(R.id.tv_save).setVisibility(isSave ? View.VISIBLE : View.GONE);
        findViewById(R.id.tv_save).setOnClickListener(onClickListener);
        tvPage = findViewById(R.id.tv_page);
        setPageNumber(index);

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
                manager.load(url).into(itemView);
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
                itemView.resetScale();
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
            ZoomImageView view = viewPager.findViewWithTag(currentPosition);
            if (view != null) view.resetScale();
            setPageNumber(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    private View.OnLongClickListener onLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            return true;
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
        ZoomImageView zoomImageView = (ZoomImageView) pagerAdapter.getPrimaryItem();
        Bitmap.CompressFormat compressFormat = Bitmap.CompressFormat.JPEG;
        try {
            String strFileName = imgList.get(currentPosition);
            if (strFileName.contains(".png")) {
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

    /**
     * 保存图片到本地
     *
     * @param bitmap
     * @param compressFormat
     */
    private void saveBitmapFile(Bitmap bitmap, Bitmap.CompressFormat compressFormat) {
        String suffix = compressFormat == Bitmap.CompressFormat.PNG ? "png" : "jpg";
        File file = FileUtil.createNewFile(DateUtils.getFormatDate(null, DateUtils.DateFormatType.DF_NORMAL) + suffix, FileUtil.FileType.IMG);

        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(compressFormat, 100, bos);
            bos.flush();
            bos.close();

            //刷新图库
            scanAsync(file, Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            scanAsync(new File(FileUtil.getFileTypePath(FileUtil.FileType.IMG)), "android.intent.action.MEDIA_SCANNER_SCAN_DIR");
            Tip.showShort(String.format("图片已经保存至:%s", file.getAbsolutePath()));
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            data = FileProvider.getUriForFile(mActivity, "com.example.dexlibs.base.fileprovider", file);
        } else {
            data = Uri.fromFile(file);
        }
        intent.setData(data);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        sendBroadcast(intent);
    }

    /**
     * 开启画面
     *
     * @param context
     * @param list    图片列表
     * @param isSave  是否显示保存按钮
     */
    public static void start(Context context, ArrayList<String> list, int index, boolean isSave) {
        Intent intent = new Intent(context, PreviewImgActivity.class);
        intent.putStringArrayListExtra(IMG_LIST, list);
        intent.putExtra(INDEX, index);
        intent.putExtra(SAVE, isSave);
        context.startActivity(intent);
    }
}
