package ui.views;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import java.util.Calendar;

public class ExtendedViewPager extends ViewPager {
    private float mStartDragX;
    private OnSwipeOutListener mListener;
    private long mLastCallTimestamp;

    public ExtendedViewPager(Context context) {
        super(context);
    }

    public ExtendedViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            float x = ev.getX();
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mStartDragX = x;
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (mStartDragX < x && getCurrentItem() == 0) {
                        mListener.onSwipeOutAtStart();
                    } else if (mStartDragX > x && getCurrentItem() == getAdapter().getCount() - 1) {
                        long eventTimestamp = Calendar.getInstance().getTime().getTime();
                        if ((eventTimestamp - mLastCallTimestamp) > 500) {
                            mLastCallTimestamp = eventTimestamp;
                            mListener.onSwipeOutAtEnd();
                        }
                    }
                    break;
            }
            return super.onInterceptTouchEvent(ev);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public void setOnSwipeOutListener(OnSwipeOutListener listener) {
        mListener = listener;
    }

    public interface OnSwipeOutListener {
        public void onSwipeOutAtStart();
        public void onSwipeOutAtEnd();
    }
}
