package com.gigamole.library;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v4.view.ViewPropertyAnimatorUpdateListener;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;

/**
 *
 */
public class BottomNavigationTabBarBehavior extends VerticalScrollingBehavior<NavigationTabBar> {
    private static final class ViewPropertyAnimatorCompat2 {
        private ViewPropertyAnimatorCompat va1;
        private ViewPropertyAnimatorCompat va2;

        public ViewPropertyAnimatorCompat2(NavigationTabBar ntb) {
            va1 = ViewCompat.animate(ntb);
            va2 = null;
            View bg = ntb.getBackgroundView();
            if (bg != null) {
                va2 = ViewCompat.animate(bg);
            }
        }

        public void setDuration(long d) {
            va1.setDuration(d);
            if(va2!=null) va2.setDuration(d);
        }

        public void setUpdateListener(ViewPropertyAnimatorUpdateListener viewPropertyAnimatorUpdateListener) {
            va1.setUpdateListener(viewPropertyAnimatorUpdateListener);
            //DON'T SET listener for va2!! we just need one updatelistener!
        }

        public void setInterpolator(Interpolator interpolator) {
            va1.setInterpolator(interpolator);
            if(va2!=null) va2.setInterpolator(interpolator);
        }

        public void cancel() {
            va1.cancel();
            if(va2!=null) va2.cancel();
        }


        public void translationY_start(int offset) {
            va1.translationY(offset).start();
            if(va2!=null) va2.translationY(offset).start();
        }
    }
    private static final class ObjectAnimator2 {
        private ObjectAnimator va1;
        private ObjectAnimator va2;
        public ObjectAnimator2(ObjectAnimator va1,ObjectAnimator va2) {
            this.va1=va1;
            this.va2=va2;
        }

        public void cancel() {
            va1.cancel();
            if(va2!=null) va2.cancel();
        }

        public void start() {
            va1.start();
            if(va2!=null) va2.start();
        }

        public void setDuration(int d) {
            va1.setDuration(d);
            if(va2!=null) va2.setDuration(d);
        }

        public void setInterpolator(Interpolator interpolator) {
            va1.setInterpolator(interpolator);
            if(va2!=null) va2.setInterpolator(interpolator);
        }

        public void addUpdateListener(ValueAnimator.AnimatorUpdateListener animatorUpdateListener) {
            va1.addUpdateListener(animatorUpdateListener);
        }
    }

    private static final Interpolator INTERPOLATOR = new LinearOutSlowInInterpolator();
    private static final int ANIM_DURATION = 300;

    private boolean hidden = false;
    private ViewPropertyAnimatorCompat2 translationAnimator;
    private ObjectAnimator2 translationObjectAnimator;
    private Snackbar.SnackbarLayout snackbarLayout;
    private FloatingActionButton floatingActionButton;
    private int mSnackbarHeight = -1;
    private boolean fabBottomMarginInitialized = false;
    private float targetOffset = 0, fabTargetOffset = 0, fabDefaultBottomMargin = 0, snackBarY = 0;
    private boolean behaviorTranslationEnabled = true;

    /**
     * Constructor
     */
    public BottomNavigationTabBarBehavior() {
        super();
    }

    public BottomNavigationTabBarBehavior(boolean behaviorTranslationEnabled) {
        super();
        this.behaviorTranslationEnabled = behaviorTranslationEnabled;
    }

    public BottomNavigationTabBarBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.NavigationTabBar);
        a.recycle();
    }

    @Override
    public boolean onLayoutChild(CoordinatorLayout parent, NavigationTabBar child, int layoutDirection) {
        boolean layoutChild = super.onLayoutChild(parent, child, layoutDirection);
        return layoutChild;
    }


    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent,NavigationTabBar child, View dependency) {
        return super.onDependentViewChanged(parent, child, dependency);
    }

    @Override
    public void onDependentViewRemoved(CoordinatorLayout parent, NavigationTabBar child, View dependency) {
        super.onDependentViewRemoved(parent, child, dependency);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, NavigationTabBar child, View dependency) {
        updateSnackbar(child, dependency);
        updateFloatingActionButton(dependency);
        return super.layoutDependsOn(parent, child, dependency);
    }

    @Override
    public void onNestedVerticalOverScroll(CoordinatorLayout coordinatorLayout, NavigationTabBar child, @ScrollDirection int direction, int currentOverScroll, int totalOverScroll) {
    }

    @Override
    public void onDirectionNestedPreScroll(CoordinatorLayout coordinatorLayout, NavigationTabBar child, View target, int dx, int dy, int[] consumed, @ScrollDirection int scrollDirection) {
    }

    @Override
    protected boolean onNestedDirectionFling(CoordinatorLayout coordinatorLayout, NavigationTabBar child, View target, float velocityX, float velocityY, @ScrollDirection int scrollDirection) {
        return false;
    }

    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, NavigationTabBar child, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
        if (dyConsumed < 0) {
            handleDirection(child, ScrollDirection.SCROLL_DIRECTION_DOWN);
        } else if (dyConsumed > 0) {
            handleDirection(child, ScrollDirection.SCROLL_DIRECTION_UP);
        }
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, NavigationTabBar child, View directTargetChild, View target, int nestedScrollAxes) {
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL || super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, nestedScrollAxes);
    }

    /**
     * Handle scroll direction
     * @param child
     * @param scrollDirection
     */
    private void handleDirection(NavigationTabBar child, int scrollDirection) {
        if (!behaviorTranslationEnabled) {
            return;
        }
        if (scrollDirection == ScrollDirection.SCROLL_DIRECTION_DOWN && hidden) {
            hidden = false;
            animateOffset(child, 0, false, true);
        } else if (scrollDirection == ScrollDirection.SCROLL_DIRECTION_UP && !hidden) {
            hidden = true;
            animateOffset(child, child.getHeight(), false, true);
        }
    }

    /**
     * Animate offset
     *
     * @param child
     * @param offset
     */
    private void animateOffset(final NavigationTabBar child, final int offset, boolean forceAnimation, boolean withAnimation) {
        if (!behaviorTranslationEnabled && !forceAnimation) {
            return;
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            ensureOrCancelObjectAnimation(child, offset, withAnimation);
            translationObjectAnimator.start();
        } else {
            ensureOrCancelAnimator(child, withAnimation);
            translationAnimator.translationY_start(offset);
        }
    }

    /**
     * Manage animation for Android >= KITKAT
     *
     * @param child
     */
    private void ensureOrCancelAnimator(NavigationTabBar child, boolean withAnimation) {
        if (translationAnimator == null) {
            translationAnimator = new ViewPropertyAnimatorCompat2(child);
            translationAnimator.setDuration(withAnimation ? ANIM_DURATION : 0);
            translationAnimator.setUpdateListener(new ViewPropertyAnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(View view) {
                    // Animate snackbar
                    if (snackbarLayout != null && snackbarLayout.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                        targetOffset = view.getMeasuredHeight() - view.getTranslationY();
                        ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) snackbarLayout.getLayoutParams();
                        p.setMargins(p.leftMargin, p.topMargin, p.rightMargin, (int) targetOffset);
                        snackbarLayout.requestLayout();
                    }
                    // Animate Floating Action Button
                    if (floatingActionButton != null && floatingActionButton.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                        ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) floatingActionButton.getLayoutParams();
                        fabTargetOffset = fabDefaultBottomMargin - view.getTranslationY() + snackBarY;
                        p.setMargins(p.leftMargin, p.topMargin, p.rightMargin, (int) fabTargetOffset);
                        floatingActionButton.requestLayout();
                    }
                }
            });
            translationAnimator.setInterpolator(INTERPOLATOR);
        } else {
            translationAnimator.setDuration(withAnimation ? ANIM_DURATION : 0);
            translationAnimator.cancel();
        }
    }

    private static ObjectAnimator objectAnimatorOfTranslationY(View target, int offset) {
        ObjectAnimator res;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            res = ObjectAnimator.ofFloat(target, View.TRANSLATION_Y, offset);
        } else {
            res = new ObjectAnimator();
            res.setTarget(target);
            res.setPropertyName("translationY");
            res.setFloatValues(offset);
        }
        return res;
    }


    /**
     * Manage animation for Android < KITKAT
     *
     * @param child
     */
    private void ensureOrCancelObjectAnimation(final NavigationTabBar child, final int offset, boolean withAnimation) {

        if (translationObjectAnimator != null) {
            translationObjectAnimator.cancel();
        }

        ObjectAnimator ta1= objectAnimatorOfTranslationY(child,offset);
        ObjectAnimator ta2=null;
        View bg= child.getBackgroundView();
        if(bg!=null) {
            ta2= objectAnimatorOfTranslationY(bg,offset);
        }
        translationObjectAnimator=new ObjectAnimator2(ta1,ta2);
        translationObjectAnimator.setDuration(withAnimation ? ANIM_DURATION : 0);
        translationObjectAnimator.setInterpolator(INTERPOLATOR);
        translationObjectAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (snackbarLayout != null && snackbarLayout.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                    targetOffset = child.getMeasuredHeight() - child.getTranslationY();
                    ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) snackbarLayout.getLayoutParams();
                    p.setMargins(p.leftMargin, p.topMargin, p.rightMargin, (int) targetOffset);
                    snackbarLayout.requestLayout();
                }
                // Animate Floating Action Button
                if (floatingActionButton != null && floatingActionButton.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                    ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) floatingActionButton.getLayoutParams();
                    fabTargetOffset = fabDefaultBottomMargin - child.getTranslationY() + snackBarY;
                    p.setMargins(p.leftMargin, p.topMargin, p.rightMargin, (int) fabTargetOffset);
                    floatingActionButton.requestLayout();
                }
            }
        });
    }


    public static  BottomNavigationTabBarBehavior from(NavigationTabBar view) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (!(params instanceof CoordinatorLayout.LayoutParams)) {
            throw new IllegalArgumentException("The view is not a child of CoordinatorLayout");
        }
        CoordinatorLayout.Behavior behavior = ((CoordinatorLayout.LayoutParams) params)
                .getBehavior();
        if (!(behavior instanceof BottomNavigationTabBarBehavior)) {
            throw new IllegalArgumentException(
                    "The view is not associated with BottomNavigationTabBarBehavior");
        }
        return (BottomNavigationTabBarBehavior) behavior;
    }

    /**
     * Enable or not the behavior translation
     * @param behaviorTranslationEnabled
     */
    public void setBehaviorTranslationEnabled(boolean behaviorTranslationEnabled) {
        this.behaviorTranslationEnabled = behaviorTranslationEnabled;
    }

    /**
     * Hide AHBottomNavigation with animation
     * @param view
     * @param offset
     */
    public void hideView(NavigationTabBar view, int offset, boolean withAnimation) {
        if (!hidden) {
            hidden = true;
            animateOffset(view, offset, true, withAnimation);
        }
    }

    /**
     * Reset AHBottomNavigation position with animation
     * @param view
     */
    public void resetOffset(NavigationTabBar view, boolean withAnimation) {
        if (hidden) {
            hidden = false;
            animateOffset(view, 0, true, withAnimation);
        }
    }

    /**
     * Update Snackbar bottom margin
     */
    public void updateSnackbar(final View child, View dependency) {

        if (dependency != null && dependency instanceof Snackbar.SnackbarLayout) {

            snackbarLayout = (Snackbar.SnackbarLayout) dependency;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                snackbarLayout.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                    @Override
                    public void onLayoutChange(View v, int left, int top, int right, int bottom,
                                               int oldLeft, int oldTop, int oldRight, int oldBottom) {
                        if (floatingActionButton != null &&
                                floatingActionButton.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) floatingActionButton.getLayoutParams();
                            snackBarY = bottom - v.getY();
                            fabTargetOffset = fabDefaultBottomMargin - child.getTranslationY() + snackBarY;
                            p.setMargins(p.leftMargin, p.topMargin, p.rightMargin, (int) fabTargetOffset);
                            floatingActionButton.requestLayout();
                        }
                    }
                });
            }

            if (mSnackbarHeight == -1) {
                mSnackbarHeight = dependency.getHeight();
            }

            int targetMargin = (int) (child.getMeasuredHeight() - child.getTranslationY());
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                child.bringToFront();
            }

            if (dependency.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) dependency.getLayoutParams();
                p.setMargins(p.leftMargin, p.topMargin, p.rightMargin, targetMargin);
                dependency.requestLayout();
            }
        }
    }

    /**
     * Update floating action button bottom margin
     */
    public void updateFloatingActionButton(View dependency) {
        if (dependency != null && dependency instanceof FloatingActionButton) {
            floatingActionButton = (FloatingActionButton) dependency;
            if (!fabBottomMarginInitialized && dependency.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                fabBottomMarginInitialized = true;
                ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) dependency.getLayoutParams();
                fabDefaultBottomMargin = p.bottomMargin;
            }
        }
    }
}