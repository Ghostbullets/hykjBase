package com.hykj.base.rxjava.base;

import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.hykj.base.R;
import com.hykj.base.base.BaseActivity;
import com.hykj.base.view.TitleView;

/**
 * 使用rxjava+retrofit网络请求时使用的带标题的Activity
 */
public abstract class RxTitleActivity extends RxBaseActivity {
    protected TitleView mTitle;
    protected View vDivider;

    @Override
    protected void onCreateSub() {
        ViewGroup layout = (ViewGroup) View.inflate(mActivity, R.layout.layout_title_content, null);
        mTitle = layout.findViewById(R.id.title);
        vDivider = layout.findViewById(R.id.v_divider);
        View contentView = View.inflate(this, getLayoutId(), null);
        contentView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        layout.addView(contentView);
        setContentView(layout);
        View left = createTitleLeft();
        if (left != null) {
            mTitle.getLayoutLeft().removeAllViews();
            mTitle.getLayoutLeft().addView(left);
        }
        View right = createTitleRight();
        if (right != null) {
            mTitle.getLayoutRight().removeAllViews();
            mTitle.getLayoutRight().addView(right);
        }
        mTitle.setBackClickListener(new TitleView.BackClickListener() {
            @Override
            public void onBackClick(View v) {
                onTitleBackClick(v);
            }
        });

        if (checkTransStatus()) {
            setTranslucentStatus(true);
        }
        init(mTitle);
    }

    @Override
    protected void setTranslucentStatus(boolean on) {
        super.setTranslucentStatus(on);
        mTitle.setTranslucentStatus(on);
        mTitle.setStatusBarBackgroundColor(on ? getResources().getColor(R.color.gray_a8) : Color.TRANSPARENT);
    }

    @Override
    protected void init() {

    }

    //隐藏分割线
    protected void hideDivider() {
        vDivider.setVisibility(View.GONE);
    }

    //设置分割线高度
    protected void setDividerHeight(int dividerHeight) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) vDivider.getLayoutParams();
        params.height = dividerHeight;
        vDivider.setLayoutParams(params);
    }

    /**
     * 获取布局
     *
     * @return
     */
    protected abstract int getLayoutId();

    /**
     * 初始化
     */
    protected abstract void init(TitleView title);

    /**
     * 标题控件点击
     */
    protected void onTitleBackClick(View v) {
        onBackPressed();
    }

    /**
     * 创建标题左侧
     *
     * @return
     */
    protected View createTitleLeft() {
        return null;
    }

    /**
     * 创建标题右侧
     *
     * @return
     */
    protected View createTitleRight() {
        return null;
    }
}
