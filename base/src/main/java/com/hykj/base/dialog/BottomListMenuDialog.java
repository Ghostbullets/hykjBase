package com.hykj.base.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.view.Gravity;

import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.hykj.base.R;
import com.hykj.base.adapter.listview.CommonAdapter;
import com.hykj.base.adapter.listview.ViewHolder;
import com.hykj.base.dialog.json.MenuGroup;
import com.hykj.base.dialog.json.MenuItem;

import java.util.List;


/**
 * 底部菜单列表,通用
 *
 * @author LZR 2016年8月5日
 * @version 1.0
 */
public class BottomListMenuDialog extends CustomBaseDialog {
    private MenuListListener mListener;

    private BottomListMenuDialog(Context context, int theme) {
        super(context, theme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutParams params = getWindow().getAttributes();
        params.width = LayoutParams.MATCH_PARENT;
        params.height = LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.BOTTOM;
        getWindow().setWindowAnimations(R.style.dialog_vertical_anim);
        getWindow().setAttributes(params);

        setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
                    dialog.dismiss();
                    if (mListener != null)
                        mListener.onDismiss();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float x = event.getX();
            float y = event.getY();
            int slop = ViewConfiguration.get(getContext()).getScaledWindowTouchSlop();
            View decorView = getWindow().getDecorView();
            if ((x < -slop) || (y < -slop) || (x > decorView.getWidth() + slop) || (y > decorView.getHeight() + slop)) {
                dismiss();
                if (mListener != null) {
                    mListener.onDismiss();
                }
                return true;
            }
        }
        return super.onTouchEvent(event);
    }

    /**
     * 创建参数
     *
     * @author LZR 2016年6月17日
     * @version 1.0
     */
    static class Parameter {

        Context context;

        CharSequence mTitle;// 标题
        CharSequence mCancel;//取消
        MenuGroup mMenuGroup;// 菜单组
        MenuListListener mListener;// 监听

    }

    /**
     * 创建对象
     *
     * @author LZR 2016年6月17日
     * @version 1.0
     */
    public static class Builder {
        Parameter P;

        public Builder(Context context) {
            P = new Parameter();
            P.context = context;
        }

        /**
         * @param title
         * @return
         */
        public Builder setTitle(CharSequence title) {
            P.mTitle = title;
            return this;
        }

        public Builder setCancel(CharSequence cancel) {
            P.mCancel = cancel;
            return this;
        }

        /**
         * 设置菜单组
         *
         * @param group
         * @return
         */
        public Builder setMenuGroup(MenuGroup group) {
            P.mMenuGroup = group;
            return this;
        }

        /**
         * 设置监听
         *
         * @param listener
         * @return
         */
        public Builder setListener(MenuListListener listener) {
            P.mListener = listener;
            return this;
        }

        /**
         * 创建
         *
         * @return
         */
        public BottomListMenuDialog create() {
            final BottomListMenuDialog dialog = new BottomListMenuDialog(P.context, R.style.CustomDialog);
            View view = View.inflate(P.context, R.layout.layout_dialog_bottom_list_menu, null);
            // 标题
            TextView title = view.findViewById(R.id.tv_title);
            if (P.mTitle != null)
                title.setText(P.mTitle);
            else {
                title.setVisibility(View.GONE);
            }

            // 取消事件
            TextView tvCancel = view.findViewById(R.id.tv_cancel);
            if (P.mCancel != null) {
                tvCancel.setText(P.mCancel);
            }
            tvCancel.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    if (P.mListener != null)
                        P.mListener.onDismiss();
                }
            });

            // 列表
            ListView lvMenu = view.findViewById(R.id.lv_menu);
            CommonAdapter<MenuItem> menuAdapter = createMenuAdapter(P.context, P.mMenuGroup.getItems());
            lvMenu.setAdapter(menuAdapter);

            // 单项点击
            lvMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (P.mListener != null)
                        P.mListener.onMenuClick(position, P.mMenuGroup.getItem(position), view, dialog);
                    dialog.dismiss();

                }

            });

            //屏幕高度
            WindowManager wm = (WindowManager) P.context.getSystemService(Context.WINDOW_SERVICE);
            Point screenSize = new Point();
            wm.getDefaultDisplay().getSize(screenSize);

            //计算列表高度
            int totalHeight = 0;
            for (int i = 0; i < menuAdapter.getCount(); i++) { // listAdapter.getCount()返回数据项的数目
                View listItem = menuAdapter.getView(i, null, lvMenu);
                listItem.measure(0, 0); // 计算子项View 的宽高
                totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
            }

            //判断是否大于半屏
            if (totalHeight > screenSize.y / 2) {
                ViewGroup.LayoutParams params = lvMenu.getLayoutParams();
                params.height = screenSize.y / 2;
                lvMenu.setLayoutParams(params);
            }

            dialog.mListener = P.mListener;
            dialog.setContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            return dialog;
        }

        /**
         * 菜单数据适配器
         *
         * @param context
         * @param list
         * @return
         */
        private CommonAdapter<MenuItem> createMenuAdapter(Context context, List<MenuItem> list) {
            int layout = R.layout.item_bottom_list_menu;
            return new CommonAdapter<MenuItem>(context, list, layout) {

                @Override
                public void convert(ViewHolder helper, MenuItem item, int position) {
                    TextView tvMenu = helper.getView(R.id.tv_menu);
                    tvMenu.setText(item.getName());
                }

            };
        }
    }

    /**
     * 菜单列表监听
     *
     * @author LZR 2016年7月6日
     * @version 1.0
     */
    public interface MenuListListener {

        /**
         * 弹窗隐藏
         */
        void onDismiss();

        /**
         * 菜单点击
         *
         * @param position
         * @param v
         */
        void onMenuClick(int position, MenuItem item, View v, BottomListMenuDialog dialog);
    }

    public static abstract class OnMenuClickListener implements MenuListListener {
        @Override
        public void onDismiss() {

        }
    }
}
