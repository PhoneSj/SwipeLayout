package com.phone.swipelayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;

/**
 * Created by Phone on 2017/5/2.
 */

public class SwipeListView extends ListView {

    private SwipeLayout mTouchView;
    private boolean isCloseEvent = false;

    public SwipeListView(Context context) {
        this(context,null);
    }

    public SwipeListView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public SwipeListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isCloseEvent = false;
                //上次点击的item是SwipeGroup，且SwipeGroup时打开状态，若这次点击的不是主体控件则判断该事件为关闭菜单
                if (mTouchView != null && !mTouchView.isClose()) {
                    int mTouchPosition = pointToPosition((int) event.getX(), (int) event.getY());
                    View child = getChildAt(mTouchPosition - getFirstVisiblePosition());
                    if (child instanceof SwipeLayout) {
                        //上次与上次点击的是同一个SwipeGroup，此时不知道点击的是否是菜单项，所以交个SwipeGroup自己处理
                        if (child == mTouchView) {
                            isCloseEvent = false;
                        } else {
                            isCloseEvent = true;
                        }
                    } else {
                        isCloseEvent = true;
                    }
                }
                if (isCloseEvent) {
                    mTouchView.closeMenu();
                }
                int mTouchPosition = pointToPosition((int) event.getX(), (int) event.getY());
                View child = getChildAt(mTouchPosition - getFirstVisiblePosition());
                if (child instanceof SwipeLayout) {
                    mTouchView = (SwipeLayout) child;
                }
                break;

            default:
                break;
        }
        return isCloseEvent ? true : super.dispatchTouchEvent(event);
    }
}
