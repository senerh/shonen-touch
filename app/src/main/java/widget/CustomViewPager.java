package widget;

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

    public void setOnSwipeOutListener(OnSwipeOutListener listener) {
        mListener = listener;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        float x = ev.getX();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mStartDragX = x;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mStartDragX < x && getCurrentItem() == 0) {
                    if (!isSwipingOut) {
                        isSwipingOut = true;
                        mListener.onSwipeOutAtStart();
                    }
                } else if (isLoaded && mStartDragX > x && getCurrentItem() == getAdapter().getCount() - 1) {
                    if (!isSwipingOut) {
                        isSwipingOut = true;
                        mListener.onSwipeOutAtEnd();
                    }
                } else if (isSwipingOut){
                    isSwipingOut = false;
                }
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    public void setIsLoaded(boolean isLoaded) {
        this.isLoaded = isLoaded;
    }

    public interface OnSwipeOutListener {
        public void onSwipeOutAtStart();
        public void onSwipeOutAtEnd();
    }
}
