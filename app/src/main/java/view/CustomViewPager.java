package view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

public class CustomViewPager extends ViewPager {

    private float mStartDragX;
    private OnSwipeOutListener mListener;
    private boolean isSwipingOut = false;
    private boolean isLoaded;

    public CustomViewPager(Context context) {
        super(context);
    }

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        try {
            return super.onTouchEvent(ev);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            Log.e(getClass().getName(), e.getMessage(), e);
        }
        return false;
    }

    public void setOnSwipeOutListener(OnSwipeOutListener listener) {
        mListener = listener;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        float x = ev.getX();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isSwipingOut = false;
                mStartDragX = x;
                break;
            case MotionEvent.ACTION_MOVE:
                if (!isSwipingOut && mStartDragX < x && getCurrentItem() == 0) {
                    isSwipingOut = true;
                    mListener.onSwipeOutAtStart();
                } else if (!isSwipingOut && isLoaded && mStartDragX > x && getCurrentItem() == getAdapter().getCount() - 1) {
                    isSwipingOut = true;
                    mListener.onSwipeOutAtEnd();
                }
                break;
        }
        try {
            return super.onInterceptTouchEvent(ev);
        } catch (IllegalArgumentException e) {
            Log.e(getClass().getName(), e.getMessage(), e);
        }
        return false;
    }

    public void setIsLoaded(boolean isLoaded) {
        this.isLoaded = isLoaded;
    }

    public interface OnSwipeOutListener {
        public void onSwipeOutAtStart();
        public void onSwipeOutAtEnd();
    }
}
