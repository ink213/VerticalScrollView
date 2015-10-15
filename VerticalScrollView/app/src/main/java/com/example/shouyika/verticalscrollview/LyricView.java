package com.example.shouyika.verticalscrollview;

import android.animation.ArgbEvaluator;
import android.animation.FloatEvaluator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.OverScroller;

/**
 * Created by shouyika on 15/10/15.
 */
public class LyricView extends View {
    private CharSequence[] seq = new CharSequence[]{"one", "two", "three", "four", "five", "six"};


    /**
     * determines speed during touch scrolling
     */
    private VelocityTracker mVelocityTracker;
    private OverScroller mFlingScrollerY;
    private OverScroller mAdjustScrollerY;
    private int mItemGap = 20;
    private int mItemHeight = 80;
    //    private TextPaint mSelectedTextPaint;
//    private TextPaint mNormalTextPaint;
    private TextPaint mTextPaint;
    private float mNormalTextSize;
    private float mSelectedTextSize;
    //private ColorStateList mTextColor;

    private float mLastDownEventY;
    private static final int SELECTOR_ADJUSTMENT_DURATION_MILLIS = 800;
    private int mPreviousScrollerY;
    /**
     * The coefficient by which to adjust (divide) the max fling velocity.
     */
    private static final int SELECTOR_MAX_FLING_VELOCITY_ADJUSTMENT = 4;

    /**
     * @see android.view.ViewConfiguration#getScaledMinimumFlingVelocity()
     */
    private int mMinimumFlingVelocity;
    /**
     * @see android.view.ViewConfiguration#getScaledMinimumFlingVelocity()
     */
    private int mMaximumFlingVelocity;

    private int mOverscrollDistance;

    private int mSelectedItem;

    private boolean mScrollingY;

    private int mTouchSlop;
    public LyricView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
//        mSelectedTextPaint = new TextPaint();
//        mSelectedTextPaint.setColor(Color.WHITE);
//        mSelectedTextPaint.setTextAlign(Paint.Align.CENTER);
//        mSelectedTextPaint.setTextSize(50);
//        mNormalTextPaint = new TextPaint();
        mTextPaint = new TextPaint();
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTextSize(50);
        setWillNotDraw(false);
        mPreviousScrollerY = Integer.MIN_VALUE;
        mFlingScrollerY = new OverScroller(context);
        mAdjustScrollerY = new OverScroller(context);

        // initialize constants
        ViewConfiguration configuration = ViewConfiguration.get(context);
        mTouchSlop = configuration.getScaledTouchSlop();
        mMinimumFlingVelocity = configuration.getScaledMinimumFlingVelocity();
        mMaximumFlingVelocity = configuration.getScaledMaximumFlingVelocity()
                / SELECTOR_MAX_FLING_VELOCITY_ADJUSTMENT;
        mOverscrollDistance = configuration.getScaledOverscrollDistance();

    }

    public LyricView(Context context, AttributeSet attrs) {
        super(context, attrs);
//        mSelectedTextPaint = new TextPaint();
//        mSelectedTextPaint.setColor(Color.WHITE);
//        mSelectedTextPaint.setTextAlign(Paint.Align.CENTER);
//        mSelectedTextPaint.setTextSize(50);
//        mNormalTextPaint = new TextPaint();
        mTextPaint = new TextPaint();
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTextSize(50);
        mNormalTextSize = 40;
        mSelectedTextSize = 60;
        setWillNotDraw(false);

        mFlingScrollerY = new OverScroller(context);
        mAdjustScrollerY = new OverScroller(context);

        // initialize constants
        ViewConfiguration configuration = ViewConfiguration.get(context);
        mTouchSlop = configuration.getScaledTouchSlop();
        mMinimumFlingVelocity = configuration.getScaledMinimumFlingVelocity();
        mMaximumFlingVelocity = configuration.getScaledMaximumFlingVelocity()
                / SELECTOR_MAX_FLING_VELOCITY_ADJUSTMENT;
        mOverscrollDistance = configuration.getScaledOverscrollDistance();
    }

    public LyricView(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int saveCount = canvas.getSaveCount();
        int itemDisY = mItemHeight + mItemGap;
        for(int i = 0; i < seq.length; ++i){
            mTextPaint.setTextSize(getTextSize(i));
            mTextPaint.setColor(getTextColor(i));
            canvas.drawText(seq[i].toString(), getWidth() / 2, itemDisY * i + mItemHeight, mTextPaint);
        }
        canvas.restoreToCount(saveCount);
    }

    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        super.scrollTo(scrollX, scrollY);
        if(!mFlingScrollerY.isFinished() && clampedY){

        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(!isEnabled()){
            return false;
        }

        if(mVelocityTracker == null){
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);

        int action = event.getActionMasked();
        switch (action){
            case MotionEvent.ACTION_MOVE:
                final float currentMoveY = event.getY();
                int deltaMoveY = (int)(mLastDownEventY - currentMoveY);
                if(mScrollingY ||
                        (Math.abs(deltaMoveY)) > mTouchSlop && seq != null && seq.length > 0){
                    if(!mScrollingY){
                        deltaMoveY = 0;
                        mScrollingY = true;
                    }
                    final int range = getScrollRange();
                    if(overScrollBy(0, deltaMoveY, 0, getScrollY(), 0, range,
                            0, mOverscrollDistance, true)){
                        mVelocityTracker.clear();
                    }

                    // Log.d("lyric", "" + deltaMoveY);
                    mLastDownEventY = currentMoveY;
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_DOWN:
                if(!mAdjustScrollerY.isFinished()){
                    mAdjustScrollerY.forceFinished(true);
                } else if(!mFlingScrollerY.isFinished()){
                    mFlingScrollerY.forceFinished(true);
                } else{
                    mScrollingY = false;
                }

                mLastDownEventY = event.getY();

                invalidate();
                break;
            case MotionEvent.ACTION_UP:

//                VelocityTracker velocityTracker = mVelocityTracker;
//                velocityTracker.computeCurrentVelocity(1000, mMaximumFlingVelocity);
//                int initialVelocityY = (int) velocityTracker.getYVelocity();
//
//                if(mScrollingY && Math.abs(initialVelocityY) > mMinimumFlingVelocity){
//                    fling(initialVelocityY);
//                }else if(seq != null){
//                    float positionY = event.getY();
//                    if(!mScrollingY){
//
//                    } else{
//
//                    }
//                }
                finishScrolling();
                mVelocityTracker.recycle();
                mVelocityTracker = null;
            case MotionEvent.ACTION_CANCEL:

                invalidate();
                break;
        }
        return true;
    }


    private int getScrollRange() {
        int scrollRange = 0;
        if(seq != null && seq.length != 0) {
            scrollRange = Math.max(0, ((mItemHeight + mItemGap) * (seq.length - 1)));
        }
        return scrollRange;
    }

    private void fling(int velocityY){

        mPreviousScrollerY = Integer.MIN_VALUE;
        mFlingScrollerY.fling(getScrollX(), getScrollY(), -velocityY, 0, 0,
                (int) (mItemHeight + mItemGap) * (seq.length - 1), 0, 0, getHeight() / 2, 0);

        invalidate();
    }

    private void finishScrolling() {

        adjustToNearestItemY();
        mScrollingY = false;

        // post to the UI Thread to avoid potential interference with the OpenGL Thread
//        if (mOnItemSelected != null) {
//            post(new Runnable() {
//                @Override
//                public void run() {
//                    mOnItemSelected.onItemSelected(getPositionFromCoordinates(getScrollX()));
//                }
//            });
//        }
    }
    private void adjustToNearestItemY(){
        final int y = getScrollY();
        int item = Math.round(y / (mItemHeight + mItemGap * 1f));

        if(item < 0) {
            item = 0;
        } else if(item >= seq.length) {
            item = seq.length - 1;
        }

        mSelectedItem = item;

        int itemY = (mItemHeight + (int) mItemGap) * item;

        int deltaY = itemY - y;
        //scrollToItem(item);
        mAdjustScrollerY.startScroll(0, getScrollY(), 0, deltaY, SELECTOR_ADJUSTMENT_DURATION_MILLIS);
        invalidate();
    }
    private int getPositionFromCoordinates(int x){
        return Math.round(x / (mItemHeight + mItemGap));
    }
    private void scrollToItem(int index){
        scrollTo(0, index * (mItemHeight + mItemGap));
    }

    @Override
    public void computeScroll() {
        //super.computeScroll();
        computeScrollY();
    }

    private void computeScrollY() {
        OverScroller scroller = mFlingScrollerY;
        if(scroller.isFinished()) {
            scroller = mAdjustScrollerY;
            if(scroller.isFinished()) {
                return;
            }
        }

        if(scroller.computeScrollOffset()) {

            int currentScrollerY = scroller.getCurrY();
            if(mPreviousScrollerY == Integer.MIN_VALUE) {
                mPreviousScrollerY = scroller.getStartY();
            }

            //            int range = getScrollRange();
            //            if(mPreviousScrollerX >= 0 && currentScrollerX < 0) {
            //                mLeftEdgeEffect.onAbsorb((int) scroller.getCurrVelocity());
            //            } else if(mPreviousScrollerX <= range && currentScrollerX > range) {
            //                mRightEdgeEffect.onAbsorb((int) scroller.getCurrVelocity());
            //            }

            overScrollBy(0, currentScrollerY - mPreviousScrollerY, getScrollX(), mPreviousScrollerY,
                    0, getScrollRange(), 0, mOverscrollDistance, false);
            mPreviousScrollerY = currentScrollerY;

            if(scroller.isFinished()) {
                onScrollerFinishedY(scroller);
            }

            postInvalidate();
        }
    }
    private void onScrollerFinishedY(OverScroller scroller) {
        if(scroller == mFlingScrollerY) {
            finishScrolling();
        }
    }

    private float getTextSize(int item) {

        int scrollY = getScrollY();

        // set color of text
        float size = mNormalTextSize == -1 ? 50 : mNormalTextSize;

        int itemHeightPadding = (int) (mItemHeight + mItemGap);
        float proportion = Math.abs(((1f * scrollY % itemHeightPadding) / 2) / (itemHeightPadding / 2f));
        if (proportion > .5) {
            proportion = (proportion - .5f);
        } else {
            proportion = .5f - proportion;
        }
        proportion *= 2;
        if (scrollY > itemHeightPadding * item - itemHeightPadding / 2 &&
                scrollY < itemHeightPadding * (item + 1) - itemHeightPadding / 2) {
            //int position = scrollY - itemHeightPadding / 2;
            return new FloatEvaluator().evaluate(proportion, mNormalTextSize, mSelectedTextSize);
        }

        return size;
    }


    private int getTextColor(int item) {

        int scrollY = getScrollY();

        // set color of text
        int selectedColor;
        int defaultColor;
        selectedColor = Color.WHITE;
        defaultColor = Color.GRAY;
        int itemHeightPadding = (int) (mItemHeight + mItemGap);
        float proportion = Math.abs(((1f * scrollY % itemHeightPadding) / 2) / (itemHeightPadding / 2f));
        if(proportion > .5) {
            proportion = (proportion - .5f);
        } else {
            proportion = .5f - proportion;
        }
        proportion *= 2;
        if (scrollY > itemHeightPadding * item - itemHeightPadding / 2 &&
                scrollY < itemHeightPadding * (item + 1) - itemHeightPadding / 2) {
            return (Integer) new ArgbEvaluator().evaluate(proportion, defaultColor, selectedColor);
        }
        return defaultColor;

    }
}
