package com.yuqirong.draggridview.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.provider.Settings;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.yuqirong.draggridview.R;
import com.yuqirong.draggridview.adapter.DragGridAdapter;

/**
 * Created by Administrator on 2016/2/19.
 */
public class DragGridView extends GridView {
    private static final String TAG = "DragGridView";
    private WindowManager mWindowManager;

    private static final int MODE_DRAG = 1;
    public static final int MODE_NORMAL = 2;
    public static final int MODE_FORBID = 3;

    public int mode = MODE_FORBID;
    private View dragView;
    // 要移动的item原先位置
    public int position = 0;

    private int tempPosition;

    private WindowManager.LayoutParams layoutParams;
    // view的x差值
    private float mX;
    // view的y差值
    private float mY;
    // 手指按下时的x坐标(相对于整个屏幕)
    private float mWindowX;
    // 手指按下时的y坐标(相对于整个屏幕)
    private float mWindowY;

    public DragGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    }

    public DragGridView(Context context) {
        super(context);
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    }

    public DragGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mWindowX = ev.getRawX();
                mWindowY = ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    /*@Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }*/

   /* @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        if (mode == MODE_DRAG) {
            return false;
        }
        this.view = view;
        this.position = position;
        this.tempPosition = position;
        mX = mWindowX - view.getLeft() - this.getLeft();
        mY = mWindowY - view.getTop() - this.getTop();
        // 如果是Android 6.0 要动态申请权限
        if (Build.VERSION.SDK_INT >= 23) {
            if (Settings.canDrawOverlays(getContext())) {
                initWindow();
            } else {
                // 跳转到悬浮窗权限管理界面
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                getContext().startActivity(intent);
            }
        } else {
            // 如果小于Android 6.0 则直接执行
            initWindow();
        }
        return true;
    }*/


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                if (mode == MODE_DRAG) {
                    updateWindow(ev);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mode == MODE_DRAG) {
                    closeWindow(ev.getX(), ev.getY());
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    /**
     * 触摸移动时，window更新
     *
     * @param ev
     */
    private void updateWindow(MotionEvent ev) {
        if (mode == MODE_DRAG) {
            float x = ev.getRawX() - mX;
            float y = ev.getRawY() - mY;
            if (layoutParams != null) {
                layoutParams.x = (int) x;
                layoutParams.y = (int) y;
                mWindowManager.updateViewLayout(dragView, layoutParams);
            }
            float mx = ev.getX();
            float my = ev.getY();
            int dropPosition = pointToPosition((int) mx, (int) my);
            Log.i(TAG, "dropPosition : " + dropPosition + " , tempPosition : " + tempPosition);
            if (dropPosition == tempPosition || dropPosition == GridView.INVALID_POSITION) {
                return;
            }
            itemMove(tempPosition,dropPosition);
        }
    }
    /**
     * 完整的移动动画
     *
     *
     */
    public void itemMove(int tempPosition,int dropPosition){
        if (mode == MODE_FORBID) {
            return;
        }
        if(tempPosition==dropPosition) return;
        itemMoveMain(tempPosition,dropPosition);
        itemMoveBack(dropPosition,tempPosition);
    }

    /**
     * 选中item的移动动画
     *
     *
     */
    private void itemMoveMain(int dropPosition,int tempPosition) {//第一个参数表示焦点所在item当前位置，第二个参数表示目标位置
        TranslateAnimation translateAnimation;
        if(tempPosition-dropPosition>0) {
            View view = getChildAt(dropPosition-getFirstVisiblePosition());
            view.clearAnimation();
            View nextView = getChildAt(tempPosition-getFirstVisiblePosition());
            float xValue = (nextView.getLeft() - view.getLeft()) * 1f / view.getWidth();
            float yValue = (nextView.getTop() - view.getTop()) * 1f / view.getHeight();
            translateAnimation =
                    new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, xValue, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, yValue);
            translateAnimation.setInterpolator(new LinearInterpolator());
            translateAnimation.setFillBefore(true);
            translateAnimation.setFillAfter(false);
            translateAnimation.setDuration(300);
            translateAnimation.setAnimationListener(animationListener);
            view.startAnimation(translateAnimation);
        }else{
            View view = getChildAt(dropPosition-getFirstVisiblePosition());
            view.clearAnimation();
            View prevView = getChildAt(tempPosition-getFirstVisiblePosition());
            float xValue = (prevView.getLeft() - view.getLeft()) * 1f / view.getWidth();
            float yValue = (prevView.getTop() - view.getTop()) * 1f / view.getHeight();
            translateAnimation =
                    new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, xValue, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, yValue);
            translateAnimation.setInterpolator(new LinearInterpolator());
            translateAnimation.setFillBefore(true);
            translateAnimation.setFillAfter(false);
            translateAnimation.setDuration(300);
            translateAnimation.setAnimationListener(animationListener);
            view.startAnimation(translateAnimation);
        }
    }

    /**
     *选中的item移动后其他item位置移动动画
     *
     *
     */
    private void itemMoveBack(int dropPosition,int tempPosition) {//第一个参数表示焦点所在item的目标位置，第二个参数表示当前位置
        TranslateAnimation translateAnimation;
        if (dropPosition < tempPosition) {
            for (int i = dropPosition; i < tempPosition; i++) {
                Log.e(">>>>>>>>>>>",i+"  "+getFirstVisiblePosition());
                View view = getChildAt(i-getFirstVisiblePosition());
                view.clearAnimation();
                View nextView = getChildAt(i-getFirstVisiblePosition() + 1);
                float xValue = (nextView.getLeft() - view.getLeft()) * 1f / view.getWidth();
                float yValue = (nextView.getTop() - view.getTop()) * 1f / view.getHeight();
                translateAnimation =
                        new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, xValue, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, yValue);
                translateAnimation.setInterpolator(new LinearInterpolator());
                translateAnimation.setFillBefore(true);
                translateAnimation.setFillAfter(false);
                translateAnimation.setDuration(300);
                if (i == tempPosition - 1) {
                   // translateAnimation.setAnimationListener(animationListener);
                }
                view.startAnimation(translateAnimation);
            }
        } else {
            for (int i = tempPosition + 1; i <= dropPosition; i++) {
                View view = getChildAt(i-getFirstVisiblePosition());
                view.clearAnimation();
                View prevView = getChildAt(i-getFirstVisiblePosition() - 1);
                float xValue = (prevView.getLeft() - view.getLeft()) * 1f / view.getWidth();
                float yValue = (prevView.getTop() - view.getTop()) * 1f / view.getHeight();
                translateAnimation =
                        new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, xValue, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, yValue);
                translateAnimation.setInterpolator(new LinearInterpolator());
                translateAnimation.setFillBefore(true);
                translateAnimation.setFillAfter(false);
                translateAnimation.setDuration(300);
                if (i == dropPosition) {
                   // translateAnimation.setAnimationListener(animationListener);
                }
                view.startAnimation(translateAnimation);
            }
        }
        this.tempPosition =  dropPosition;
    }

    /**
     * 动画监听器
     */
    Animation.AnimationListener animationListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            // 在动画完成时将adapter里的数据交换位置
            int big;
            int small;
            if(position<tempPosition){
                small = position;
                big = tempPosition;
            }else{
                small = tempPosition;
                big = position;
            }

            for(;small<=big;small++){
                getChildAt(small-getFirstVisiblePosition()).clearAnimation();
            }
            ListAdapter adapter = getAdapter();
            if (adapter != null && adapter instanceof DragGridAdapter) {
                ((DragGridAdapter) adapter).exchangePosition(position, tempPosition, true);
            }
            position = tempPosition;
            int lastP = getLastVisiblePosition();
            Log.e("position",""+position);
            if(lastP+((lastP+1)%5==0?0:5-(lastP+1)%5)-position<5) {
                smoothScrollBy(getChildAt(position-getFirstVisiblePosition()).getHeight()+getVerticalSpacing(),300);

            }else if(position-getFirstVisiblePosition()<5){
                smoothScrollBy(-(getChildAt(position-getFirstVisiblePosition()).getHeight()+getVerticalSpacing()),300);
            }
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };



    /**
     * 关闭window
     *
     * @param x
     * @param y
     */
    private void closeWindow(float x, float y) {
        if (dragView != null) {
            mWindowManager.removeView(dragView);
            dragView = null;
            layoutParams = null;
        }
        itemDrop();
        mode = MODE_NORMAL;
    }

    /**
     * 手指抬起时，item下落
     */
    private void itemDrop() {
        if (tempPosition == position || tempPosition == GridView.INVALID_POSITION) {
            getChildAt(position).setVisibility(VISIBLE);
        } else {
            ListAdapter adapter = getAdapter();
            if (adapter != null && adapter instanceof DragGridAdapter) {
                ((DragGridAdapter) adapter).exchangePosition(position, tempPosition, false);
            }
        }
    }

}
