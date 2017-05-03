package com.phone.swipelayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.OverScroller;

import com.phone.swipelayout.mode.LayDownMode;
import com.phone.swipelayout.mode.PullOutMode;
import com.phone.swipelayout.mode.StretchMode;
import com.phone.swipelayout.mode.SwipeMode;

import java.util.LinkedHashMap;

import static com.phone.swipelayout.SwipeLayout.DragEdge.Empty;
import static com.phone.swipelayout.SwipeLayout.DragEdge.Left;
import static com.phone.swipelayout.SwipeLayout.DragEdge.Right;

public class SwipeLayout extends FrameLayout {

	private final String TAG = "SwipeLayout";
	private static final boolean TAG_ENABLE = true;
	/** 默认触发Move事件的最小滑动距离 **/
	private int mTouchSlop;
	/** 动画时长 **/
	private final int Duration = 200;
	private final int DelayMillis = 0;
	/** 当前侧拉方式、默认为没有侧拉 **/
	private static final DragEdge DefaultDragEdge = DragEdge.Empty;
	private DragEdge mCurrentDragEdge = DefaultDragEdge;
	/** 目标控件的默认显示方式：跟随主控件一起滑动方式 **/
	private SwipeMode mSwipeMode;
	/** 当前菜单状态 **/
	private static final Status DefaultStatus = Status.Close;
	private Status mStatus = DefaultStatus;
	/** 侧拉的控件集合 **/
	private LinkedHashMap<DragEdge, View> mDragEdges = new LinkedHashMap<DragEdge, View>();
	/** 主体子控件 **/
	private View mMainView;
	/** 该控件是否使能了侧拉功能 **/
	private boolean mSwipeEnabled = true;
	/** 正在拉动 **/
	private boolean mIsBeingDragged;
	/** 拽动菜单控件之前的状态 **/
	private boolean isCloseBeforeDragged = true;
	private int mFirstMotionX;
	private int mFirstMotionY;
	/** 上次事件的x、y坐标值 **/
	private int mLastMotionX;
	private int mLastMotionY;
	/** 当前滑动偏移总量 **/
	public int mCurrentOffset = 0;
	/** 当前事件是否是关闭菜单动作 **/
	private boolean isCloseEvent = false;
	/** 指定main、left、right为哪个子控件 **/
	private int mainViewIndex = -1;
	private int leftViewIndex = -1;
	private int rightViewIndex = -1;

	/** 位移动画线程 **/
	private SmoothScrollRunnable mCurrentSmoothScrollRunnable;

	private OnSmoothScrollFinishedListener onSmoothScrollFinishedListener = new OnSmoothScrollFinishedListener() {

		@Override
		public void onSmoothScrollFinished(Status status) {
			mStatus = status;
			if (status == Status.Open) {
				isCloseBeforeDragged = false;
			} else {
				isCloseBeforeDragged = true;
				mCurrentDragEdge = DragEdge.Empty;
			}
			LogUtils.showD(TAG, "status:" + mStatus, TAG_ENABLE);
		}
	};

	/** 菜单拽出的两个方向：Empty为当前没有判定操作左右View **/
	public enum DragEdge {
		Left, Right, Empty
	}

	/** 该控件的三种状态：其中Middle为过渡状态（滑动、动画执行时） **/
	public enum Status {
		Middle, Open, Close
	}

	public SwipeLayout(Context context) {
		this(context, null);
	}

	public SwipeLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SwipeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
		init(context, attrs);
		setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				perforAdapterViewItemClick();
			}
		});
	}

	private void init(Context context, AttributeSet attrs) {
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SwipeLayout);
		int ordinal = a.getInt(R.styleable.SwipeLayout_show_mode, 0);
		switch (ordinal) {
			case 1:
				mSwipeMode = new LayDownMode(this);
				break;
			case 2:
				mSwipeMode = new StretchMode(this);
				break;
			default:
				mSwipeMode = new PullOutMode(this);
				break;
		}
		mainViewIndex = a.getInt(R.styleable.SwipeLayout_mainViewIndex, -1);
		leftViewIndex = a.getInt(R.styleable.SwipeLayout_leftViewIndex, -1);
		rightViewIndex = a.getInt(R.styleable.SwipeLayout_rightViewIndex, -1);
		a.recycle();
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		mMainView = getViewByIndex(mainViewIndex);
		mDragEdges.put(Left, getViewByIndex(leftViewIndex));
		mDragEdges.put(Right, getViewByIndex(rightViewIndex));
	}

	private View getViewByIndex(int viewIndex) {
		if (viewIndex >= 0 && viewIndex < getChildCount()) {
			return getChildAt(viewIndex);
		}
		return null;
	}

	@Override
	protected void onLayout(boolean b, int i, int i1, int i2, int i3) {
		mSwipeMode.layout();
		if (mMainView != null) {
			mMainView.layout(0, 0, mMainView.getMeasuredWidth(), mMainView.getMeasuredHeight());
			bringChildToFront(mMainView);
		}
	}

	public View getLeftView() {
		return mDragEdges.get(Left);
	}

	public View getRightView() {
		return mDragEdges.get(Right);
	}

	public View getMainView() {
		return mMainView;
	}

	public boolean isCloseBeforeDragged() {
		return isCloseBeforeDragged;
	}

	public DragEdge getCurrentDragEdge() {
		return mCurrentDragEdge;
	}

	/** 判断点在哪个子控件的位置上 **/
	public View pointToChild(int x, int y) {
		Rect frame = null;
		final int count = getChildCount();
		LogUtils.showD("phoneTest", "x:" + x, true);
		LogUtils.showD("phoneTest", "y:" + y, true);
		for (int i = count - 1; i >= 0; i--) {
			final View child = getChildAt(i);
			frame = getHitRect(child);
			LogUtils.showI("phoneTest", "left:" + frame.left + "  right:" + frame.right, true);
			LogUtils.showI("phoneTest", "top:" + frame.top + "  bottom:" + frame.bottom, true);
			if (frame.contains(x, y)) {
				return getChildAt(i);
			}
		}
		return null;
	}

	/** 获得控件的区域 **/
	private Rect getHitRect(View child) {
		int left = child.getLeft();
		int top = child.getTop();
		int right = child.getRight();
		int bottom = child.getBottom();
		return new Rect(left, top, right, bottom);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		LogUtils.showW(TAG, "dispatchTouchEvent", TAG_ENABLE);
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				isCloseEvent = false;
				//如果是打开状态，需要判断点击的是否是在菜单上，若不是直接关闭菜单
				if (isOpen()) {
					View child = pointToChild((int) event.getX(), (int) event.getY());
					View leftChild = mDragEdges.get(DragEdge.Left);
					View rightChild = mDragEdges.get(DragEdge.Right);
					if (child == null) {
						isCloseEvent = true;
					} else if (child == leftChild || child == rightChild) {
						isCloseEvent = false;
					} else {
						isCloseEvent = true;
					}
					if (isCloseEvent) {
						mSwipeMode.closeMenu();
					}
				}
				return isCloseEvent ? true : super.dispatchTouchEvent(event);

			default:
				break;
		}
		return super.dispatchTouchEvent(event);
	}

	private boolean isIntercept=false;

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		LogUtils.showW(TAG, "onInterceptTouchEvent", TAG_ENABLE);
		if (!mSwipeEnabled) {									//没开启侧滑功能时，直接返回，不拦截事件
			return super.onInterceptTouchEvent(event);
		}
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN: {
				LogUtils.showV(TAG, "interceptTouchEvent  ACTION_DOWN", TAG_ENABLE);
				final int x = (int) event.getX();
				final int y = (int) event.getY();
				mLastMotionX = x;
				mLastMotionX = y;
				isIntercept = false;
				LogUtils.showD(TAG, "status:" + mStatus, TAG_ENABLE);
				break;
			}
			case MotionEvent.ACTION_MOVE: {
				LogUtils.showV(TAG, "interceptTouchEvent  ACTION_MOVE", TAG_ENABLE);
				final int x = (int) event.getX();
				final int y = (int) event.getY();
				final int xDiff = x - mLastMotionX;
				final int yDiff = y - mLastMotionY;
				mLastMotionX = x;
				mLastMotionY = y;

				if (Math.abs(xDiff) > mTouchSlop && Math.abs(xDiff) > Math.abs(yDiff)) {
					//在move中判断滑动的方向，只有当判定为横向滑动时，才拦截事件并自己处理
					isIntercept = true;
					final ViewParent parent = getParent();
					if (parent != null) {
						parent.requestDisallowInterceptTouchEvent(true);
					}
				}
				break;
			}
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL:
				final ViewParent parent = getParent();
				if (parent != null) {
					parent.requestDisallowInterceptTouchEvent(false);
				}
				if (mCurrentDragEdge == Left || mCurrentDragEdge == Right) {
					mLastMotionX = 0;
					mLastMotionY = 0;
				}
				break;
			default:
				break;
		}
		return isIntercept;
	}

	/**
	 * 判定触发哪个侧边控件：left、right、empty
	 * 
	 * @param xDiff:x方向的滑动距离
	 */
	private void determineWhichDrageEdge(int xDiff) {
		LogUtils.showW(TAG, "determineWhichDrageEdge", TAG_ENABLE);
		//只有当处于关闭状态时，才能重新设置DragEdge
		if (!mIsBeingDragged && mStatus == Status.Close) {
			//第一次拦截，通过滑动方向判定当前需要控制的菜单控件
			if (mDragEdges.get(DragEdge.Left) != null && xDiff > 0) {
				//只有存在左菜单才能判定操作对象为左
				mCurrentDragEdge = DragEdge.Left;
				mIsBeingDragged = true;
				getLeftView().bringToFront();
			} else if (mDragEdges.get(DragEdge.Right) != null && xDiff < 0) {
				//只有存在右菜单才能判定操作对象为右
				mCurrentDragEdge = DragEdge.Right;
				mIsBeingDragged = true;
				getRightView().bringToFront();
			} else {
				mCurrentDragEdge = DragEdge.Empty;
			}
		}
	}

	private boolean isFirstAction = true;

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		LogUtils.showW(TAG, "onTouchEvent", TAG_ENABLE);
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN: {
				LogUtils.showV(TAG, "onTouchEvent  ACTION_DOWN", TAG_ENABLE);
				if (mMainView == null) {
					return false;
				}
				mFirstMotionX = (int) event.getX();
				mLastMotionX = mFirstMotionX;
				isFirstAction = false;
				break;
			}
			case MotionEvent.ACTION_MOVE: {
				LogUtils.showV(TAG, "onTouchEvent  ACTION_MOVE", TAG_ENABLE);
				final int x = (int) event.getX();
				final int xDiff = x - mLastMotionX;
				mLastMotionX = x;
				if (isFirstAction) {
					mFirstMotionX = (int) event.getX();
					isFirstAction = false;
				} else {
					determineWhichDrageEdge(x - mFirstMotionX);
					LogUtils.showD(TAG, "mIsBeingDragged:" + mIsBeingDragged, TAG_ENABLE);
					LogUtils.showD(TAG, "mCurrentDragEdge:" + mCurrentDragEdge, TAG_ENABLE);
					LogUtils.showD(TAG, "state:" + mStatus, TAG_ENABLE);
					if (mIsBeingDragged || mCurrentDragEdge != Empty) {
						//当可拖拽时进行偏移量累加
						checkOffset(xDiff);
						final ViewParent parent = getParent();
						if (parent != null) {
							parent.requestDisallowInterceptTouchEvent(true);
						}
						mSwipeMode.onPull();
						setStatus(Status.Middle);
						mSwipeMode.offsetLeftAndRight(mCurrentOffset);
					}
				}
				break;
			}
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL: {
				LogUtils.showV(TAG, "onTouchEvent  ACTION_UP", TAG_ENABLE);
				final ViewParent parent = getParent();
				if (parent != null) {
					parent.requestDisallowInterceptTouchEvent(false);
				}
				if (mCurrentDragEdge != DragEdge.Empty) {
					mSwipeMode.onRelease();
				}
				mLastMotionX = 0;
				mLastMotionY = 0;
				mFirstMotionX = 0;
				mIsBeingDragged = false;
				isFirstAction = true;
			}
			default:
				break;
		}
		return super.onTouchEvent(event) || mIsBeingDragged || event.getAction() == MotionEvent.ACTION_DOWN;
	}

	private void checkOffset(int xDiff) {
		switch (mCurrentDragEdge){
			case Left:
				if(Math.abs(mCurrentOffset+xDiff)>getLeftView().getMeasuredWidth()){
					mCurrentOffset=getLeftView().getMeasuredWidth();
				}else {
					mCurrentOffset+=xDiff;
				}
				break;
			case Right:
				if(Math.abs(mCurrentOffset+xDiff)>getRightView().getMeasuredWidth()){
				    mCurrentOffset=-getRightView().getMeasuredWidth();
				}else {
					mCurrentOffset+=xDiff;
				}
				break;
			default:
				mCurrentOffset=0;
				break;
		}
	}

	private void setStatus(Status status) {
		mStatus = status;
	}

	public boolean isOpen() {
		return mStatus == Status.Open;
	}

	public boolean isClose() {
		return mStatus == Status.Close;
	}

	private void postOnAnimation(Runnable action, long delay) {
		this.postDelayed(action, delay);
	}

	public final void smoothScrollTo(Status status, int newScrollValue) {
		smoothScrollTo(status, newScrollValue, Duration, DelayMillis, onSmoothScrollFinishedListener);
	}

	private final void smoothScrollTo(Status status, int newScrollValue, int duration, int delayMillis,
			OnSmoothScrollFinishedListener listener) {
		if (null != mCurrentSmoothScrollRunnable) {
			mCurrentSmoothScrollRunnable.stop();
		}

		final int oldScrollValue = mCurrentOffset;
		LogUtils.showV(TAG, "oldScrollValue:" + oldScrollValue, TAG_ENABLE);
		mCurrentSmoothScrollRunnable = new SmoothScrollRunnable(status, listener, delayMillis);
		int fromX = oldScrollValue;
		int fromY = (int) mMainView.getY();
		int dx = newScrollValue - oldScrollValue;
		int dy = 0;
		mCurrentSmoothScrollRunnable.start(fromX, fromY, dx, dy, duration);
	}

	class SmoothScrollRunnable implements Runnable {

		private final OverScroller mScroller;
		private Status mStatus;
		private OnSmoothScrollFinishedListener mListener;
		private long delayMillis;

		public SmoothScrollRunnable(Status status, OnSmoothScrollFinishedListener listener, long delay) {
			super();
			mScroller = new OverScroller(getContext(), new LinearInterpolator());
			mStatus = status;
			mListener = listener;
			delayMillis = delay;
		}

		void start(final int fromX, final int fromY, final int dx, final int dy, final int duration) {
			mScroller.startScroll(fromX, fromY, dx, dy, duration);
			postOnAnimation(this, delayMillis);
		}

		@Override
		public void run() {
			final OverScroller scroller = mScroller;
			boolean more = scroller.computeScrollOffset();
			mCurrentOffset = scroller.getCurrX();
			mSwipeMode.offsetLeftAndRight(mCurrentOffset);
			setStatus(Status.Middle);
			if (more) {
				postOnAnimation(this, 0);
			} else {
				end();
				if (null != mListener) {
					mListener.onSmoothScrollFinished(mStatus);
				}
			}
		}

		void end() {
			removeCallbacks(this);
			mScroller.abortAnimation();
		}

		public void stop() {
			mScroller.forceFinished(true);
			removeCallbacks(this);
		}

	}

	/** 动画执行完毕监听回调 **/
	interface OnSmoothScrollFinishedListener {
		void onSmoothScrollFinished(Status status);
	}

	OnClickListener clickListener;

	@Override
	public void setOnClickListener(OnClickListener l) {
		super.setOnClickListener(l);
		clickListener = l;
	}

	/** 执行外部控件的点击事件，如：ListView **/
	private void perforAdapterViewItemClick() {
		LogUtils.showV(TAG, "perforAdapterViewItemClick", TAG_ENABLE);
		if (mStatus != Status.Close)
			return;
		ViewParent parent = getParent();
		while (getParent() != null) {
			if (parent instanceof AdapterView) {
				AdapterView adapterView = (AdapterView) parent;
				int position = adapterView.getPositionForView(SwipeLayout.this);
				if (position != AdapterView.INVALID_POSITION) {
					AdapterView.OnItemClickListener listener = adapterView.getOnItemClickListener();
					if (listener != null) {
						listener.onItemClick(adapterView, SwipeLayout.this, position, position);
					}
				}
				break;
			}
		}

	}

	public void closeMenu(){
		if(mSwipeMode!=null){
		    mSwipeMode.closeMenu();
		}
	}

	public void openMenu(){
		if(mSwipeMode!=null){
		    mSwipeMode.openMenu();
		}
	}

}
