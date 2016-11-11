package devlight.io.library.behavior;


import android.annotation.TargetApi;
import android.os.Build;
import android.os.Parcelable;
import android.support.annotation.IntDef;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.WindowInsetsCompat;
import android.view.View;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@SuppressWarnings({"unused", "EmptyMethod"})
abstract class VerticalScrollingBehavior<V extends View> extends CoordinatorLayout.Behavior<V> {

    private int mTotalDyUnconsumed = 0;
    private int mTotalDy = 0;

    @ScrollDirection
    private int mOverScrollDirection = ScrollDirection.SCROLL_NONE;
    @ScrollDirection
    private int mScrollDirection = ScrollDirection.SCROLL_NONE;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({ScrollDirection.SCROLL_DIRECTION_UP, ScrollDirection.SCROLL_DIRECTION_DOWN})
    @interface ScrollDirection {
        int SCROLL_DIRECTION_UP = 1;
        int SCROLL_DIRECTION_DOWN = -1;
        int SCROLL_NONE = 0;
    }

    VerticalScrollingBehavior() {
        super();
    }

    @ScrollDirection
    public int getOverScrollDirection() {
        return mOverScrollDirection;
    }

    @ScrollDirection
    public int getScrollDirection() {
        return mScrollDirection;
    }

    // Direction of the over scroll: SCROLL_DIRECTION_UP, SCROLL_DIRECTION_DOWN
    // Unconsumed value, negative or positive based on the direction
    // Cumulative value for current direction
    protected abstract void onNestedVerticalOverScroll();

    // Direction of the over scroll: SCROLL_DIRECTION_UP, SCROLL_DIRECTION_DOWN
    protected abstract void onDirectionNestedPreScroll();

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, V child, View directTargetChild, View target, int nestedScrollAxes) {
        return (nestedScrollAxes & View.SCROLL_AXIS_VERTICAL) != 0;
    }

    @Override
    public void onNestedScrollAccepted(CoordinatorLayout coordinatorLayout, V child, View directTargetChild, View target, int nestedScrollAxes) {
        super.onNestedScrollAccepted(coordinatorLayout, child, directTargetChild, target, nestedScrollAxes);
    }

    @Override
    public void onStopNestedScroll(CoordinatorLayout coordinatorLayout, V child, View target) {
        super.onStopNestedScroll(coordinatorLayout, child, target);
    }

    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, V child, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
        if (dyUnconsumed > 0 && mTotalDyUnconsumed < 0) {
            mTotalDyUnconsumed = 0;
            mOverScrollDirection = ScrollDirection.SCROLL_DIRECTION_UP;
        } else if (dyUnconsumed < 0 && mTotalDyUnconsumed > 0) {
            mTotalDyUnconsumed = 0;
            mOverScrollDirection = ScrollDirection.SCROLL_DIRECTION_DOWN;
        }

        mTotalDyUnconsumed += dyUnconsumed;
        onNestedVerticalOverScroll();
    }

    @Override
    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, V child, View target, int dx, int dy, int[] consumed) {
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed);
        if (dy > 0 && mTotalDy < 0) {
            mTotalDy = 0;
            mScrollDirection = ScrollDirection.SCROLL_DIRECTION_UP;
        } else if (dy < 0 && mTotalDy > 0) {
            mTotalDy = 0;
            mScrollDirection = ScrollDirection.SCROLL_DIRECTION_DOWN;
        }
        mTotalDy += dy;
        onDirectionNestedPreScroll();
    }

    @Override
    public boolean onNestedFling(CoordinatorLayout coordinatorLayout, V child, View target, float velocityX, float velocityY, boolean consumed) {
        super.onNestedFling(coordinatorLayout, child, target, velocityX, velocityY, consumed);
        mScrollDirection = velocityY > 0 ? ScrollDirection.SCROLL_DIRECTION_UP : ScrollDirection.SCROLL_DIRECTION_DOWN;
        return onNestedDirectionFling();
    }

    @SuppressWarnings("SameReturnValue")
    protected abstract boolean onNestedDirectionFling();

    @Override
    public boolean onNestedPreFling(CoordinatorLayout coordinatorLayout, V child, View target, float velocityX, float velocityY) {
        return super.onNestedPreFling(coordinatorLayout, child, target, velocityX, velocityY);
    }

    @Override
    public WindowInsetsCompat onApplyWindowInsets(CoordinatorLayout coordinatorLayout, V child, WindowInsetsCompat insets) {

        return super.onApplyWindowInsets(coordinatorLayout, child, insets);
    }

    @Override
    public Parcelable onSaveInstanceState(CoordinatorLayout parent, V child) {
        return super.onSaveInstanceState(parent, child);
    }

}