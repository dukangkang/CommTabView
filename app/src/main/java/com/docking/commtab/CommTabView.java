package com.docking.commtab;

import android.content.Context;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by docking on 16/1/11.
 */
public class CommTabView extends RelativeLayout {

    // 可见Tab的个数
    private int COLUMN = 5;
    // 中间位置
    private int MIDLE_POSITION = 3;
    // 选项卡之间的margin
    private int MARGIN_SIZE = 16;

    // tab的中个数
    private int mCount = 0;

    // 容器宽度
    private int width = 0;

    // 设备高度
    private int height = 0;

    // 指示器宽度
    private int indicatorWidth = 0;

    // 当前上一次指示器左边的位置
    private int lastLeftPosition = 0;

    // 设备密度
    private float density = 0;

    // 默认选中项索引
    private int position = 0;

    // 可见tab加起来的长度
    private int len = 0;

    // 标志是否超过容器宽度
    private boolean flag = false;

    // 是否固定tab栏个数
    private boolean isEnableFix = false;

    // 是否首次进入tabview, 执行一次默认选项
    private boolean isFirst = false;

    // 选项卡列表
    private List<String> mTabList = new ArrayList<String>();

    private View mContainer = null;

    private Context mContext = null;
    // 左剪头指示器
    private ImageView mNavLeft = null;
    // 右剪头指示器
    private ImageView mNavRight = null;
    // 底部滑动指示器
    private ImageView mBtmIndicator = null;
    // 添加选项卡的父容器
    private RadioGroup mRadioGroup = null;
    // 水平滑动ScrollView
    private HorizontalScrollView mHScrollView = null;
    // Tab监听器
    private OnChangedListener mOnChangedListener = null;


    public CommTabView(Context context) {
        super(context);
        this.mContext = context;
        init();
    }

    public CommTabView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init();

    }

    public CommTabView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init();
    }

    private void init() {
        initScreen();
        initView();
        initData();
        initListener();
    }

    private void initView() {
        mContainer = LayoutInflater.from(mContext).inflate(R.layout.comm_tab_layout, null);
        this.addView(mContainer);
        this.mNavLeft = (ImageView) this.findViewById(R.id.comm_tab_nav_left);
        this.mNavRight = (ImageView) findViewById(R.id.comm_tab_nav_right);
        this.mBtmIndicator = (ImageView) findViewById(R.id.comm_tab_indicator);
        this.mRadioGroup = (RadioGroup) findViewById(R.id.comm_tab_radiogroup);
        this.mHScrollView = (HorizontalScrollView) findViewById(R.id.comm_tab_hslv);
    }

    /**
     * 初始化设备信息
     *  width在onSizeChanged之后修改成容器的宽度
     */
    private void initScreen() {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(dm);
        width = dm.widthPixels;
        height = dm.heightPixels;
        density = dm.density;
        Log.e("info", "width = " + width);
    }

    private void initData() {
        // px 转化 dp
        MARGIN_SIZE = (int) (density * MARGIN_SIZE);

    }

    private void initListener() {

    }

    /**
     * 替换选项卡内容
     * @param list
     */
    public void replaceTabs(List<String> list) {
        mTabList = list;
        notifyChange();
    }

    /**
     * 添加tab
     */
    public void notifyChange() {
        if(null != mTabList && mTabList.size() > 0) {
            int size = mTabList.size();
            if(isEnableFix) {
                // 计算宽度
                for (int i = 0; i < size; i++) {
                    addFixTab(mTabList.get(i), i);
                }
            } else {
                for (int i = 0; i < size; i++) {
                    addTab(mTabList.get(i), i);
                }
            }
        }
    }

    /**
     * 更新文字的颜色
     * @param position
     */
    private void updateTextColor(int position) {
        for (int i = 0; i < mCount; i++) {
            View view = mRadioGroup.getChildAt(i);
            if(i == position) {
                view.setSelected(true);
            } else {
                view.setSelected(false);
            }
        }
    }

    /**
     * 添加一个tab
     *
     * @param txt
     * @param id
     */
    private void addTab(String txt, int id) {
        TextView tv = (TextView) LayoutInflater.from(mContext).inflate(R.layout.comm_tab_item_view, null);
        tv.setId(id);
        tv.setText(txt);
        tv.setPadding(MARGIN_SIZE, 0, MARGIN_SIZE, 0);

        TextPaint paint = tv.getPaint();
        int txtWidth = (int) paint.measureText(txt);
        RadioGroup.LayoutParams lp = new RadioGroup.LayoutParams(txtWidth + MARGIN_SIZE * 2, RadioGroup.LayoutParams.MATCH_PARENT);
        lp.gravity = Gravity.CENTER;
        tv.setGravity(Gravity.CENTER);
        tv.setLayoutParams(lp);
        tv.setCompoundDrawables(null, null, null, null);
        tv.setBackgroundResource(R.color.black);
        tv.setOnClickListener(mItemClickListener);

        mRadioGroup.addView(tv);
    }

    /**
     * 添加固定tab
     *
     * @param txt
     * @param id
     */
    private void addFixTab(String txt, int id) {
        TextView tv = (TextView) LayoutInflater.from(mContext).inflate(R.layout.comm_tab_item_view, null);
        tv.setId(id);
        tv.setText(txt);
//        tv.setLayoutParams(new RadioGroup.LayoutParams(indicatorWidth, RadioGroup.LayoutParams.MATCH_PARENT));
        tv.setCompoundDrawables(null, null, null, null);
        tv.setBackgroundResource(R.color.black);
        tv.setOnClickListener(mItemClickListener);
        mRadioGroup.addView(tv);
    }

    /**
     * 刷新固定tab布局
     */
    private void refreshLayout() {
        if(isEnableFix) {
            changeIndicatorFixWidth();
            int count = mRadioGroup.getChildCount();
            for (int i = 0; i < count; i++) {
                TextView tv = (TextView) mRadioGroup.getChildAt(i);
                RadioGroup.LayoutParams rLp =  new RadioGroup.LayoutParams(indicatorWidth, RadioGroup.LayoutParams.MATCH_PARENT);
                tv.setLayoutParams(rLp);
            }
        }
    }

    /**
     * 计算中间位置和屏幕可见tab的个数
     */
    private void calculateMiddlePosition() {
        if(isEnableFix) {
            MIDLE_POSITION = (int) Math.floor(COLUMN / 2);
        } else {
            int count = mRadioGroup.getChildCount();
            COLUMN = count;
            for (int i = 0; i < count; i++) {
                TextView tv = (TextView) mRadioGroup.getChildAt(i);
                String txt = tv.getText().toString();
                TextPaint paint = tv.getPaint();
                int txtWidth = (int) paint.measureText(txt);

                len += txtWidth + MARGIN_SIZE * 2;
                if (len >= width && false == flag) {
                    flag = true;
                    COLUMN = i;
                }
            }
            MIDLE_POSITION = (int) Math.floor(COLUMN / 2);
        }
        Log.e("info", "COLUMN x MIDLE_POSITION = " + COLUMN +  " x " + MIDLE_POSITION);

    }

    /**
     * 改变指示器宽度
     *
     * @param position
     */
    private void changeIndicatorWidth(int position) {
        if (false == isEnableFix) {
            if (position < mRadioGroup.getChildCount()) {
                TextView tv = (TextView) mRadioGroup.getChildAt(position);
                TextPaint paint = tv.getPaint();
                int mTextWidth = (int) paint.measureText(tv.getText().toString());
                indicatorWidth = mTextWidth + MARGIN_SIZE * 2;
                ViewGroup.LayoutParams cursor_Params = mBtmIndicator.getLayoutParams();
                cursor_Params.width = indicatorWidth;// 初始化滑动下标的宽
                mBtmIndicator.setLayoutParams(cursor_Params);
            }
//            Log.e("info", "计算指示器宽度：" + indicatorWidth);
        }
    }

    /**
     * 改变指示器固定宽度
     *
     */
    private void changeIndicatorFixWidth() {
        indicatorWidth = (width-dip2px(mContext, 26)) / COLUMN;
        ViewGroup.LayoutParams cursor_Params = mBtmIndicator.getLayoutParams();
        cursor_Params.width = indicatorWidth;// 初始化滑动下标的
        mBtmIndicator.setLayoutParams(cursor_Params);
    }

    /**
     * 将dip值转换为px值
     *
     * @param context
     * @param value
     * @return
     */
    private int dip2px(Context context, int value) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, context.getResources().getDisplayMetrics());
    }

    /**
     * 默认选中状态
     */
    private void defaultTrans(int position) {
        int distance = 0;
        if(isEnableFix) {
            distance = indicatorWidth*position;
        } else {
            distance = mRadioGroup.getChildAt(position).getLeft();
        }
        TranslateAnimation animation = new TranslateAnimation(lastLeftPosition, distance, 0f, 0f);
        animation.setInterpolator(new LinearInterpolator());
        animation.setDuration(5);
        animation.setFillAfter(true);

        // 执行位移动画
        mBtmIndicator.startAnimation(animation);
        lastLeftPosition = distance;
    }

    /**
     * 指示器动画
     *
     * @param position
     */
    private void transAnim(int position) {
        int distance = mRadioGroup.getChildAt(position).getLeft();
        TranslateAnimation animation = new TranslateAnimation(lastLeftPosition, distance, 0f, 0f);
        animation.setInterpolator(new LinearInterpolator());
        animation.setDuration(80);
        animation.setFillAfter(true);

        // 执行位移动画
        mBtmIndicator.startAnimation(animation);
        lastLeftPosition = distance;
    }

    /**
     * 选中tab滚动到中间项
     * @param position
     */
    private void scrollCenter(int position) {
        if(mRadioGroup.getChildCount() > MIDLE_POSITION) {
            TextView curTab = (TextView) mRadioGroup.getChildAt(position);
            TextView centerTab = (TextView) mRadioGroup.getChildAt(MIDLE_POSITION);
            if(null != curTab && null != centerTab) {
                if(position > MIDLE_POSITION) {
                    int distance = curTab.getLeft() - centerTab.getLeft();
                    mHScrollView.smoothScrollTo(distance, 0);
                } else {
                    mHScrollView.smoothScrollTo(- centerTab.getLeft(), 0);
                }
            }
        }
    }

    /**
     * 选中默认项
     * @param position
     */
    private void performDefault(int position) {
        Log.e("info", "--default--");
        if(mCount > position) {
            changeIndicatorWidth(position);
            mRadioGroup.getChildAt(position).setSelected(true);
            if(0 != position) {
                defaultTrans(position);
            }
            if(null != mOnChangedListener) {
                mOnChangedListener.onDefaultItem(position);
            }
        }
    }

    /**
     * 选中一项
     * @param position
     */
    private void performItem(int position) {
        Log.e("info", "--click--");
        changeIndicatorWidth(position);
        scrollCenter(position);
        transAnim(position);
        changeNavStatus(mCount, position);
        updateTextColor(position);
        if(null != mOnChangedListener) {
            mOnChangedListener.onChangedItem(position);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
//        Log.e("info", "onSizeChanged w x h = " + w + " x " + h );
        width = w;
        refreshLayout();
        mCount = mRadioGroup.getChildCount();
        calculateMiddlePosition();
        changeNavStatus(mCount, 0);
        lastLeftPosition = indicatorWidth;
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if(!isFirst) {
            isFirst = true;
            performDefault(position);
        }
    }

    /**
     * 设置默认选中项
     *
     * @param position
     *  默认选中项位置
     */
    public void setDefaultPosition(int position) {
        this.position = position;
    }

    /**
     * 是否使用固定tab
     *
     * @param enable
     * true: 开启 false: 关闭
     */
    public void setFixTab(boolean enable) {
        setFixTab(true, COLUMN);
    }

    /**
     * 是否使用固定tab
     *
     * @param enable
     * true: 开启 false: 关闭
     * @param column
     * 几列tab
     */
    public void setFixTab(boolean enable, int column) {
        this.isEnableFix = enable;
        this.COLUMN = column;
    }

    /**
     * 左边指示可见
     */
    private void setLeftNavVisible() {
        mNavLeft.setVisibility(View.VISIBLE);
        mNavRight.setVisibility(View.INVISIBLE);
    }

    /**
     * 右边指示可见
     */
    private void setRightNavVisible() {
        mNavLeft.setVisibility(View.INVISIBLE);
        mNavRight.setVisibility(View.VISIBLE);
    }

    /**
     * 左右指示可见
     */
    private void setAllNavVisible() {
        mNavLeft.setVisibility(View.VISIBLE);
        mNavRight.setVisibility(View.VISIBLE);
    }

    /**
     * 左右指示不可见
     */
    private void setAllNavGone() {
        mNavLeft.setVisibility(View.INVISIBLE);
        mNavRight.setVisibility(View.INVISIBLE);
    }

    /**
     * 替换指示器显示状态
     *
     * @param count
     * @param pos
     */
    private void changeNavStatus(int count, int pos) {
        if (count > COLUMN) {
            if (pos == count - 1) {
                setLeftNavVisible();
            } else if ((pos > MIDLE_POSITION) && pos <= count - 2) {
                setAllNavVisible();
            } else if (pos >= 0 && pos < COLUMN) {
                setRightNavVisible();
            }
        } else {
            setAllNavGone();
        }
    }

    private OnClickListener mItemClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = v.getId();
            performItem(position);
        }
    };

    /**
     * 添加监听事件
     * @param listener
     */
    public void setOnChangedListener(OnChangedListener listener) {
        this.mOnChangedListener = listener;
    }

    public interface OnChangedListener {

        /**
         * 切换item
         * @param checkedId
         */
        public void onChangedItem(int checkedId);

        /**
         * 默认选中item
         * @param position
         */
        public void onDefaultItem(int position);
    }

}
