package com.gigamole.library;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Scroller;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by GIGAMOLE on 24.03.2016.
 */
public class NavigationTabBar extends View implements ViewPager.OnPageChangeListener {

    // NTP constants
    private final static int INVALID_INDEX = -1;

    private final static int DEFAULT_ANIMATION_DURATION = 300;
    private final static int DEFAULT_INACTIVE_COLOR = Color.parseColor("#9f90af");
    private final static int DEFAULT_ACTIVE_COLOR = Color.WHITE;

    private final static float MIN_FRACTION = 0.0f;
    private final static float MAX_FRACTION = 1.0f;

    private final static float ACTIVE_ICON_SCALE_BY = 0.4f;
    private final static float ICON_SIZE_FRACTION = 0.35f;

    // NTP and pointer bounds
    private RectF mBounds = new RectF();
    private RectF mPointerBounds = new RectF();

    // Canvas, where all of other canvas will be merged
    private Bitmap mBitmap;
    private Canvas mCanvas;

    // Canvas with icons
    private Bitmap mIconsBitmap;
    private Canvas mIconsCanvas;

    // Canvas for our rect pointer
    private Bitmap mPointerBitmap;
    private Canvas mPointerCanvas;

    // Main paint
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG) {
        {
            setDither(true);
            setStyle(Style.FILL);
        }
    };

    // Pointer paint
    private Paint mPointerPaint = new Paint(Paint.ANTI_ALIAS_FLAG) {
        {
            setDither(true);
            setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        }
    };

    // Icons paint
    private Paint mIconPaint = new Paint(Paint.ANTI_ALIAS_FLAG) {
        {
            setDither(true);
        }
    };

    // Paint for icon mask pointer
    final Paint mIconPointerPaint = new Paint(Paint.ANTI_ALIAS_FLAG) {
        {
            setStyle(Style.FILL);
            setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        }
    };

    // Variables for animator
    private ValueAnimator mAnimator = new ValueAnimator();
    private ResizeInterpolator mResizeInterpolator = new ResizeInterpolator();
    private int mAnimationDuration;

    // NTP models
    private ArrayList<Model> mModels = new ArrayList<>();

    // Variables for ViewPager
    private ViewPager mViewPager;
    private ViewPager.OnPageChangeListener mListener;
    private int mScrollState;

    // Tab listener
    private OnTabBarSelectedIndexListener mOnTabBarSelectedIndexListener;
    private ValueAnimator.AnimatorListener mAnimatorListener;

    // Variables for sizes
    private float mModelSize;
    private int mIconSize;
    // Corners radius for rect mode
    private float mCornersRadius;

    // Indexes
    private int mLastIndex = INVALID_INDEX;
    private int mIndex;
    // General fraction value
    private float mFraction;

    // Coordinates of pointer
    private float mStartPointerX;
    private float mEndPointerX;
    private float mPointerLeftTop;
    private float mPointerRightBottom;

    // Detect if is bar mode or indicator pager mode
    private boolean mIsViewPagerMode;
    // Detect whether the horizontal orientation
    private boolean mIsHorizontalOrientation;
    // Detect if we move from left to right
    private boolean mIsResizeIn;
    // Detect if we get action down event
    private boolean mIsActionDown;
    // Detect if we get action down event on pointer
    private boolean mIsPointerActionDown;
    // Detect when we set index from tab bar nor from ViewPager
    private boolean mIsSetIndexFromTabBar;

    // Color variables
    private int mInactiveColor;
    private int mActiveColor;

    public NavigationTabBar(final Context context) {
        this(context, null);
    }

    public NavigationTabBar(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NavigationTabBar(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //Init NTB

        // Always draw
        setWillNotDraw(false);
        // More speed!
        setLayerType(LAYER_TYPE_HARDWARE, null);

        final TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.NavigationTabBar);
        try {
            setInactiveColor(
                    typedArray.getColor(
                            R.styleable.NavigationTabBar_ntb_inactive_color,
                            DEFAULT_INACTIVE_COLOR
                    )
            );
            setActiveColor(
                    typedArray.getColor(
                            R.styleable.NavigationTabBar_ntb_active_color,
                            DEFAULT_ACTIVE_COLOR
                    )
            );
            setAnimationDuration(
                    typedArray.getInteger(
                            R.styleable.NavigationTabBar_ntb_animation_duration,
                            DEFAULT_ANIMATION_DURATION
                    )
            );
            setCornersRadius(
                    typedArray.getDimension(R.styleable.NavigationTabBar_ntb_corners_radius, 0.0f)
            );

            // Init animator
            mAnimator.setFloatValues(MIN_FRACTION, MAX_FRACTION);
            mAnimator.setInterpolator(new LinearInterpolator());
            mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(final ValueAnimator animation) {
                    updateIndicatorPosition((Float) animation.getAnimatedValue());
                }
            });

            // Set preview models
            if (isInEditMode()) {
                // Get preview colors
                String[] previewColors = null;
                try {
                    final int previewColorsId = typedArray.getResourceId(
                            R.styleable.NavigationTabBar_ntb_preview_colors, 0
                    );
                    previewColors = previewColorsId == 0 ? null :
                            typedArray.getResources().getStringArray(previewColorsId);
                } catch (Exception exception) {
                    previewColors = null;
                    exception.printStackTrace();
                } finally {
                    if (previewColors == null)
                        previewColors = typedArray.getResources().getStringArray(R.array.default_preview);

                    for (String previewColor : previewColors)
                        mModels.add(new Model(null, Color.parseColor(previewColor)));
                    requestLayout();
                }
            }
        } finally {
            typedArray.recycle();
        }
    }

    public int getAnimationDuration() {
        return mAnimationDuration;
    }

    public void setAnimationDuration(final int animationDuration) {
        mAnimationDuration = animationDuration;
        mAnimator.setDuration(mAnimationDuration);
        resetScroller();
    }

    public ArrayList<Model> getModels() {
        return mModels;
    }

    public void setModels(final ArrayList<Model> models) {
        mModels.clear();
        mModels = models;
        requestLayout();
    }

    public int getActiveColor() {
        return mActiveColor;
    }

    public void setActiveColor(final int activeColor) {
        mActiveColor = activeColor;
        mIconPointerPaint.setColor(activeColor);
        postInvalidate();
    }

    public int getInactiveColor() {
        return mInactiveColor;
    }

    public void setInactiveColor(final int inactiveColor) {
        mInactiveColor = inactiveColor;

        // Set color filter to wrap icons with inactive color
        mIconPaint.setColorFilter(
                new PorterDuffColorFilter(inactiveColor, PorterDuff.Mode.SRC_IN)
        );
        postInvalidate();
    }

    public float getCornersRadius() {
        return mCornersRadius;
    }

    public void setCornersRadius(final float cornersRadius) {
        mCornersRadius = cornersRadius;
        postInvalidate();
    }

    public OnTabBarSelectedIndexListener getOnTabBarSelectedIndexListener() {
        return mOnTabBarSelectedIndexListener;
    }

    // Set on tab bar selected index listener where you can trigger action onStart or onEnd
    public void setOnTabBarSelectedIndexListener(final OnTabBarSelectedIndexListener onTabBarSelectedIndexListener) {
        mOnTabBarSelectedIndexListener = onTabBarSelectedIndexListener;

        if (mAnimatorListener == null)
            mAnimatorListener = new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(final Animator animation) {
                    if (mOnTabBarSelectedIndexListener != null && !mIsViewPagerMode)
                        mOnTabBarSelectedIndexListener.onStartTabSelected(mIndex);
                }

                @Override
                public void onAnimationEnd(final Animator animation) {
                    if (mOnTabBarSelectedIndexListener != null && !mIsViewPagerMode)
                        mOnTabBarSelectedIndexListener.onEndTabSelected(mIndex);
                }

                @Override
                public void onAnimationCancel(final Animator animation) {

                }

                @Override
                public void onAnimationRepeat(final Animator animation) {

                }
            };
        mAnimator.removeListener(mAnimatorListener);
        mAnimator.addListener(mAnimatorListener);
    }

    public void setViewPager(final ViewPager viewPager) {
        // Detect whether ViewPager mode
        if (viewPager == null) {
            mIsViewPagerMode = false;
            return;
        }

        if (mViewPager == viewPager) return;
        if (mViewPager != null) mViewPager.setOnPageChangeListener(null);
        if (viewPager.getAdapter() == null)
            throw new IllegalStateException("ViewPager does not provide adapter instance.");

        mIsViewPagerMode = true;
        mViewPager = viewPager;
        mViewPager.addOnPageChangeListener(this);

        resetScroller();
        postInvalidate();
    }

    public void setViewPager(final ViewPager viewPager, int index) {
        setViewPager(viewPager);

        mIndex = index;
        if (mIsViewPagerMode)
            mViewPager.setCurrentItem(index, true);
        postInvalidate();
    }

    // Reset scroller and reset scroll duration equals to animation duration
    private void resetScroller() {
        if (mViewPager == null) return;
        try {
            final Field scrollerField = ViewPager.class.getDeclaredField("mScroller");
            scrollerField.setAccessible(true);
            final ResizeViewPagerScroller scroller = new ResizeViewPagerScroller(mViewPager.getContext());
            scrollerField.set(mViewPager, scroller);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setOnPageChangeListener(final ViewPager.OnPageChangeListener listener) {
        mListener = listener;
    }

    public int getModelIndex() {
        return mIndex;
    }

    public void setModelIndex(int index) {
        setModelIndex(index, false);
    }

    // Set model index from touch or programmatically
    public void setModelIndex(int index, final boolean force) {
        if (mAnimator.isRunning()) return;
        if (mModels.isEmpty()) return;

        // Detect if last is the same
        if (index == mIndex) return;

        // Snap index to models size
        index = Math.max(0, Math.min(index, mModels.size() - 1));

        mIsResizeIn = index < mIndex;
        mLastIndex = mIndex;
        mIndex = index;

        mIsSetIndexFromTabBar = true;
        if (mIsViewPagerMode) {
            if (mViewPager == null) throw new IllegalStateException("ViewPager is null.");
            mViewPager.setCurrentItem(index, true);
        }

        // Set startX and endX for animation, where we animate two sides of rect with different interpolation
        mStartPointerX = mPointerLeftTop;
        mEndPointerX = mIndex * mModelSize;

        // If it force, so update immediately, else animate
        // This happens if we set index onCreate or something like this
        // You can use force param or call this method in some post()
        if (force) updateIndicatorPosition(MAX_FRACTION);
        else mAnimator.start();
    }

    private void updateIndicatorPosition(final float fraction) {
        // Update general fraction
        mFraction = fraction;

        // Set the pointer left top side coordinate
        mPointerLeftTop =
                mStartPointerX + (mResizeInterpolator.getResizeInterpolation(fraction, mIsResizeIn) *
                        (mEndPointerX - mStartPointerX));
        // Set the pointer right bottom side coordinate
        mPointerRightBottom =
                (mStartPointerX + mModelSize) +
                        (mResizeInterpolator.getResizeInterpolation(fraction, !mIsResizeIn) *
                                (mEndPointerX - mStartPointerX));

        // Update pointer
        postInvalidate();
    }

    // Update NTP
    public void notifyDataSetChanged() {
        postInvalidate();
    }

    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        // Return if animation is running
        if (mAnimator.isRunning())
            return true;
        // If is not idle state, return
        if (mScrollState != ViewPager.SCROLL_STATE_IDLE)
            return true;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // Action down touch
                mIsActionDown = true;
                if (!mIsViewPagerMode)
                    break;
                // Detect if we touch down on pointer, later to move
                if (mIsHorizontalOrientation)
                    mIsPointerActionDown = (int) (event.getX() / mModelSize) == mIndex;
                else
                    mIsPointerActionDown = (int) (event.getY() / mModelSize) == mIndex;
                break;
            case MotionEvent.ACTION_MOVE:
                // If pointer touched, so move
                if (mIsPointerActionDown) {
                    if (mIsHorizontalOrientation)
                        mViewPager.setCurrentItem((int) (event.getX() / mModelSize), true);
                    else
                        mViewPager.setCurrentItem((int) (event.getY() / mModelSize), true);
                    break;
                }
                if (mIsActionDown)
                    break;
            case MotionEvent.ACTION_UP:
                // Press up and set model index relative to current coordinate
                if (mIsActionDown) {
                    if (mIsHorizontalOrientation)
                        setModelIndex((int) (event.getX() / mModelSize));
                    else
                        setModelIndex((int) (event.getY() / mModelSize));
                }
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_OUTSIDE:
            default:
                // Reset action touch variables
                mIsPointerActionDown = false;
                mIsActionDown = false;
                break;
        }

        return true;
    }

    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        final int width = MeasureSpec.getSize(widthMeasureSpec);
        final int height = MeasureSpec.getSize(heightMeasureSpec);

        if (mModels.isEmpty() || width == 0 || height == 0)
            return;

        // Set bounds for NTB
        mBounds.set(0.0f, 0.0f, width, height);

        // Set main bitmap
        mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);

        // Set pointer canvas
        mPointerBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        mPointerCanvas = new Canvas(mPointerBitmap);

        // Set icons canvas
        mIconsBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        mIconsCanvas = new Canvas(mIconsBitmap);

        // Detect orientation and calculate icon size
        if (width > height) {
            mIsHorizontalOrientation = true;
            mModelSize = (float) width / (float) mModels.size();
            mIconSize = (int) ((mModelSize > height ? height : mModelSize) * ICON_SIZE_FRACTION);
        } else {
            mIsHorizontalOrientation = false;
            mModelSize = (float) height / (float) mModels.size();
            mIconSize = (int) ((mModelSize > width ? width : mModelSize) * ICON_SIZE_FRACTION);
        }

        // Set scale fraction for icons
        for (Model model : mModels) {
            final float originalIconSize = model.mIcon.getWidth() > model.mIcon.getHeight() ?
                    model.mIcon.getWidth() : model.mIcon.getHeight();
            model.mInactiveIconScale = (float) mIconSize / originalIconSize;
            model.mActiveIconScaleBy = model.mInactiveIconScale * ACTIVE_ICON_SCALE_BY;
        }

        // Set start position of pointer for preview or on start
        if (isInEditMode() || !mIsViewPagerMode) {
            mIsSetIndexFromTabBar = true;

            // Set random in preview mode
            if (isInEditMode())
                mIndex = new Random().nextInt(mModels.size());

            mStartPointerX = mIndex * mModelSize;
            mEndPointerX = mStartPointerX;
            updateIndicatorPosition(MAX_FRACTION);
        }

    }

    @Override
    protected void onDraw(final Canvas canvas) {
        if (mCanvas == null || mPointerCanvas == null || mIconsCanvas == null)
            return;

        // Reset and clear canvases
        mCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        mPointerCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        mIconsCanvas.drawColor(0, PorterDuff.Mode.CLEAR);

        // Draw our model colors
        for (int i = 0; i < mModels.size(); i++) {
            mPaint.setColor(mModels.get(i).mColor);

            if (mIsHorizontalOrientation) {
                final float left = mModelSize * i;
                final float right = left + mModelSize;
                mCanvas.drawRect(left, 0.0f, right, mBounds.height(), mPaint);
            } else {
                final float top = mModelSize * i;
                final float bottom = top + mModelSize;
                mCanvas.drawRect(0.0f, top, mBounds.width(), bottom, mPaint);
            }
        }

        // Set bound of pointer
        if (mIsHorizontalOrientation)
            mPointerBounds.set(mPointerLeftTop, 0.0f, mPointerRightBottom, mBounds.height());
        else
            mPointerBounds.set(0.0f, mPointerLeftTop, mBounds.width(), mPointerRightBottom);

        // Draw pointer for model colors
        if (mCornersRadius == 0)
            mPointerCanvas.drawRect(mPointerBounds, mPaint);
        else
            mPointerCanvas.drawRoundRect(mPointerBounds, mCornersRadius, mCornersRadius, mPaint);


        // Draw pointer into main canvas
        mCanvas.drawBitmap(mPointerBitmap, 0.0f, 0.0f, mPointerPaint);

        // Draw model icons
        for (int i = 0; i < mModels.size(); i++) {
            final Model model = mModels.get(i);

            // Variables to center our icons
            final float leftOffset;
            final float topOffset;
            final float matrixCenterX;
            final float matrixCenterY;

            if (mIsHorizontalOrientation) {
                leftOffset = (mModelSize * i) + (mModelSize - model.mIcon.getWidth()) * 0.5f;
                topOffset = (mBounds.height() - model.mIcon.getHeight()) * 0.5f;

                matrixCenterX = leftOffset + model.mIcon.getWidth() * 0.5f;
                matrixCenterY = topOffset + model.mIcon.getHeight() * 0.5f;
            } else {
                leftOffset = (mBounds.width() - model.mIcon.getWidth()) * 0.5f;
                topOffset = (mModelSize * i) + (mModelSize - model.mIcon.getHeight()) * 0.5f;

                matrixCenterX = leftOffset + model.mIcon.getWidth() * 0.5f;
                matrixCenterY = topOffset + model.mIcon.getHeight() * 0.5f;
            }

            // Translate icon to model center
            model.mIconMatrix.setTranslate(
                    leftOffset, topOffset
            );

            // Scale value relative to interpolation
            final float matrixScale =
                    model.mActiveIconScaleBy * mResizeInterpolator.getResizeInterpolation(mFraction, true);
            final float matrixLastScale =
                    model.mActiveIconScaleBy * mResizeInterpolator.getResizeInterpolation(mFraction, false);

            // Check if we handle models from touch on NTP or from ViewPager
            // There is a strange logic of ViewPager onPageScrolled method, so it is
            if (mIsSetIndexFromTabBar) {
                if (mIndex == i)
                    model.mIconMatrix.postScale(
                            model.mInactiveIconScale + matrixScale,
                            model.mInactiveIconScale + matrixScale,
                            matrixCenterX, matrixCenterY
                    );
                else if (mLastIndex == i)
                    model.mIconMatrix.postScale(
                            model.mInactiveIconScale + model.mActiveIconScaleBy - matrixLastScale,
                            model.mInactiveIconScale + model.mActiveIconScaleBy - matrixLastScale,
                            matrixCenterX, matrixCenterY
                    );
                else
                    model.mIconMatrix.postScale(
                            model.mInactiveIconScale, model.mInactiveIconScale,
                            matrixCenterX, matrixCenterY
                    );
            } else {
                if (i != mIndex && i != mIndex + 1)
                    model.mIconMatrix.postScale(
                            model.mInactiveIconScale, model.mInactiveIconScale,
                            matrixCenterX, matrixCenterY
                    );
                else if (i == mIndex + 1)
                    model.mIconMatrix.postScale(
                            model.mInactiveIconScale + matrixScale,
                            model.mInactiveIconScale + matrixScale,
                            matrixCenterX, matrixCenterY
                    );
                else if (i == mIndex)
                    model.mIconMatrix.postScale(
                            model.mInactiveIconScale + model.mActiveIconScaleBy - matrixLastScale,
                            model.mInactiveIconScale + model.mActiveIconScaleBy - matrixLastScale,
                            matrixCenterX, matrixCenterY
                    );
            }

            // Draw model icon
            mIconsCanvas.drawBitmap(model.mIcon, model.mIconMatrix, mIconPaint);
        }

        // Draw pointer with active color to wrap out active icon
        if (mCornersRadius == 0)
            mIconsCanvas.drawRect(mPointerBounds, mIconPointerPaint);
        else
            mIconsCanvas.drawRoundRect(mPointerBounds, mCornersRadius, mCornersRadius, mIconPointerPaint);

        // Draw general bitmap
        canvas.drawBitmap(mBitmap, 0.0f, 0.0f, null);
        // Draw icons bitmap on top
        canvas.drawBitmap(mIconsBitmap, 0.0f, 0.0f, null);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, final int positionOffsetPixels) {
        // If we animate, don`t call this
        if (!mIsSetIndexFromTabBar) {
            mIsResizeIn = position < mIndex;
            mLastIndex = mIndex;
            mIndex = position;

            mStartPointerX = position * mModelSize;
            mEndPointerX = mStartPointerX + mModelSize;
            updateIndicatorPosition(positionOffset);
        }

        if (mListener != null)
            mListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
    }

    @Override
    public void onPageSelected(final int position) {
        // If VP idle, so update
        if (mScrollState == ViewPager.SCROLL_STATE_IDLE) {
            mIsResizeIn = position < mIndex;
            mLastIndex = mIndex;
            mIndex = position;
            postInvalidate();
        }

        if (mListener != null)
            mListener.onPageSelected(position);
    }

    @Override
    public void onPageScrollStateChanged(final int state) {
        // If VP idle, reset to MIN_FRACTION
        if (state == ViewPager.SCROLL_STATE_IDLE) {
            mFraction = MIN_FRACTION;
            mIsSetIndexFromTabBar = false;
        }
        mScrollState = state;

        if (mListener != null)
            mListener.onPageScrollStateChanged(state);
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        final SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        mIndex = savedState.index;
        requestLayout();
    }

    @Override
    public Parcelable onSaveInstanceState() {
        final Parcelable superState = super.onSaveInstanceState();
        final SavedState savedState = new SavedState(superState);
        savedState.index = mIndex;
        return savedState;
    }

    static class SavedState extends BaseSavedState {
        int index;

        public SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            index = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(index);
        }

        @SuppressWarnings("UnusedDeclaration")
        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

    // Model class
    public static class Model {

        private int mColor;

        private Bitmap mIcon;
        private Matrix mIconMatrix = new Matrix();

        private float mInactiveIconScale;
        private float mActiveIconScaleBy;

        public Model(final Drawable icon, final int color) {
            mColor = color;
            mIcon = icon != null ? ((BitmapDrawable) icon).getBitmap() :
                    Bitmap.createBitmap(1, 1, Bitmap.Config.RGB_565);
        }
    }

    // Custom scroller with custom scroll duration
    private class ResizeViewPagerScroller extends Scroller {

        public ResizeViewPagerScroller(Context context) {
            super(context, new AccelerateDecelerateInterpolator());
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duration) {
            super.startScroll(startX, startY, dx, dy, mAnimationDuration);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy) {
            super.startScroll(startX, startY, dx, dy, mAnimationDuration);
        }
    }

    // Resize interpolator to create smooth effect on pointer according to inspiration design
    // This is like improved accelerated and decelerated interpolator
    private class ResizeInterpolator implements Interpolator {

        // Spring factor
        private final float mFactor = 1.0f;
        // Check whether side we move
        private boolean mResizeIn;

        @Override
        public float getInterpolation(final float input) {
            if (mResizeIn)
                return (float) (1.0f - Math.pow((1.0f - input), 2.0f * mFactor));
            else
                return (float) (Math.pow(input, 2.0f * mFactor));
        }

        public float getResizeInterpolation(final float input, final boolean resizeIn) {
            mResizeIn = resizeIn;
            return getInterpolation(input);
        }
    }

    // Out listener for selected index
    public interface OnTabBarSelectedIndexListener {
        void onStartTabSelected(final int index);
        void onEndTabSelected(final int index);
    }
}
