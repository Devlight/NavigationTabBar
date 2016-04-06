/*
 * Copyright (C) 2015 Basil Miller
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.ViewPager;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
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
    private final static String PREVIEW_BADGE = "0";
    private final static String PREVIEW_TITLE = "Title";
    private final static int INVALID_INDEX = -1;

    private final static int DEFAULT_BADGE_ANIMATION_DURATION = 200;
    private final static int DEFAULT_BADGE_REFRESH_ANIMATION_DURATION = 100;
    private final static int DEFAULT_ANIMATION_DURATION = 300;
    private final static int DEFAULT_INACTIVE_COLOR = Color.parseColor("#9f90af");
    private final static int DEFAULT_ACTIVE_COLOR = Color.WHITE;

    private final static float MIN_FRACTION = 0.0f;
    private final static float MAX_FRACTION = 1.0f;

    private final static int MIN_ALPHA = 0;
    private final static int MAX_ALPHA = 255;

    private final static float ACTIVE_ICON_SCALE_BY = 0.35f;
    private final static float ICON_SIZE_FRACTION = 0.4f;

    private final static float TITLE_ACTIVE_ICON_SCALE_BY = 0.25f;
    private final static float TITLE_ICON_SIZE_FRACTION = 0.4f;
    private final static float TITLE_ACTIVE_SCALE_BY = 0.2f;
    private final static float TITLE_SIZE_FRACTION = 0.2f;
    private final static float TITLE_MARGIN_FRACTION = 0.15f;

    private final static float BADGE_HORIZONTAL_FRACTION = 0.5f;
    private final static float BADGE_VERTICAL_FRACTION = 0.75f;
    private final static float BADGE_TITLE_SIZE_FRACTION = 0.85f;

    private final static int ALL_INDEX = 0;
    private final static int ACTIVE_INDEX = 1;

    private final static int LEFT_INDEX = 0;
    private final static int CENTER_INDEX = 1;
    private final static int RIGHT_INDEX = 2;

    private final static int TOP_INDEX = 0;
    private final static int BOTTOM_INDEX = 1;

    private final static float LEFT_FRACTION = 0.25f;
    private final static float CENTER_FRACTION = 0.5f;
    private final static float RIGHT_FRACTION = 0.75f;

    private final static Interpolator DECELERATE_INTERPOLATOR = new DecelerateInterpolator();
    private final static Interpolator ACCELERATE_INTERPOLATOR = new AccelerateInterpolator();

    // NTP and pointer bounds
    private RectF mBounds = new RectF();
    private RectF mPointerBounds = new RectF();
    // Badge bounds and bg badge bounds
    final Rect mBadgeBounds = new Rect();
    final RectF mBgBadgeBounds = new RectF();

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

    // Paint for model title
    final Paint mModelTitlePaint = new TextPaint(Paint.ANTI_ALIAS_FLAG) {
        {
            setDither(true);
            setColor(Color.WHITE);
            setTextAlign(Align.CENTER);
        }
    };

    // Paint for badge
    final Paint mBadgePaint = new TextPaint(Paint.ANTI_ALIAS_FLAG) {
        {
            setDither(true);
            setTextAlign(Align.CENTER);
            setFakeBoldText(true);
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
    private ViewPager.OnPageChangeListener mOnPageChangeListener;
    private int mScrollState;

    // Tab listener
    private OnTabBarSelectedIndexListener mOnTabBarSelectedIndexListener;
    private ValueAnimator.AnimatorListener mAnimatorListener;

    // Variables for sizes
    private float mModelSize;
    private int mIconSize;
    // Corners radius for rect mode
    private float mCornersRadius;

    // Model title size and margin
    private float mModelTitleSize;
    private float mTitleMargin;

    // Model badge title size and margin
    private float mBadgeMargin;
    private float mBadgeTitleSize;

    // Model title mode: active ar all
    private TitleMode mTitleMode;
    // Model badge position: left, center or right
    private BadgePosition mBadgePosition;
    // Model badge gravity: top or bottom
    private BadgeGravity mBadgeGravity;

    // Indexes
    private int mLastIndex = INVALID_INDEX;
    private int mIndex = INVALID_INDEX;
    // General fraction value
    private float mFraction;

    // Coordinates of pointer
    private float mStartPointerX;
    private float mEndPointerX;
    private float mPointerLeftTop;
    private float mPointerRightBottom;

    // Detect if model has title
    private boolean mIsTitled;
    // Detect if model has badge
    private boolean mIsBadged;
    // Detect if model badge have custom typeface
    private boolean mIsBadgeUseTypeface;
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

    // Custom typeface
    private Typeface mTypeface;

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
            setIsTitled(
                    typedArray.getBoolean(R.styleable.NavigationTabBar_ntb_titled, false)
            );
            setIsBadged(
                    typedArray.getBoolean(R.styleable.NavigationTabBar_ntb_badged, false)
            );
            setIsBadgeUseTypeface(
                    typedArray.getBoolean(R.styleable.NavigationTabBar_ntb_badge_use_typeface, false)
            );
            setTitleMode(
                    typedArray.getInt(R.styleable.NavigationTabBar_ntb_title_mode, ALL_INDEX)
            );
            setBadgePosition(
                    typedArray.getInt(R.styleable.NavigationTabBar_ntb_badge_position, RIGHT_INDEX)
            );
            setBadgeGravity(
                    typedArray.getInt(R.styleable.NavigationTabBar_ntb_badge_gravity, TOP_INDEX)
            );
            setTypeface(
                    typedArray.getString(R.styleable.NavigationTabBar_ntb_typeface)
            );
            setInactiveColor(
                    typedArray.getColor(
                            R.styleable.NavigationTabBar_ntb_inactive_color, DEFAULT_INACTIVE_COLOR
                    )
            );
            setActiveColor(
                    typedArray.getColor(
                            R.styleable.NavigationTabBar_ntb_active_color, DEFAULT_ACTIVE_COLOR
                    )
            );
            setAnimationDuration(
                    typedArray.getInteger(
                            R.styleable.NavigationTabBar_ntb_animation_duration, DEFAULT_ANIMATION_DURATION
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
        //Set update listeners to badge model animation
        for (final Model model : models) {
            model.mBadgeAnimator.removeAllUpdateListeners();
            model.mBadgeAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(final ValueAnimator animation) {
                    model.mBadgeFraction = (float) animation.getAnimatedValue();
                    postInvalidate();
                }
            });
        }

        mModels.clear();
        mModels = models;
        requestLayout();
    }

    public boolean isTitled() {
        return mIsTitled;
    }

    public void setIsTitled(final boolean isTitled) {
        mIsTitled = isTitled;
        requestLayout();
    }

    public boolean isBadged() {
        return mIsBadged;
    }

    public void setIsBadged(final boolean isBadged) {
        mIsBadged = isBadged;
        requestLayout();
    }

    public boolean isBadgeUseTypeface() {
        return mIsBadgeUseTypeface;
    }

    public void setIsBadgeUseTypeface(final boolean isBadgeUseTypeface) {
        mIsBadgeUseTypeface = isBadgeUseTypeface;
        setBadgeTypeface();
        postInvalidate();
    }

    public TitleMode getTitleMode() {
        return mTitleMode;
    }

    private void setTitleMode(final int index) {
        switch (index) {
            case ACTIVE_INDEX:
                setTitleMode(TitleMode.ACTIVE);
                break;
            case ALL_INDEX:
            default:
                setTitleMode(TitleMode.ALL);
        }
    }

    public void setTitleMode(final TitleMode titleMode) {
        mTitleMode = titleMode;
        postInvalidate();
    }

    public BadgePosition getBadgePosition() {
        return mBadgePosition;
    }

    private void setBadgePosition(final int index) {
        switch (index) {
            case LEFT_INDEX:
                setBadgePosition(BadgePosition.LEFT);
                break;
            case CENTER_INDEX:
                setBadgePosition(BadgePosition.CENTER);
                break;
            case RIGHT_INDEX:
            default:
                setBadgePosition(BadgePosition.RIGHT);
        }
    }

    public void setBadgePosition(final BadgePosition badgePosition) {
        mBadgePosition = badgePosition;
        postInvalidate();
    }

    public BadgeGravity getBadgeGravity() {
        return mBadgeGravity;
    }

    private void setBadgeGravity(final int index) {
        switch (index) {
            case BOTTOM_INDEX:
                setBadgeGravity(BadgeGravity.BOTTOM);
                break;
            case TOP_INDEX:
            default:
                setBadgeGravity(BadgeGravity.TOP);
        }
    }

    public void setBadgeGravity(final BadgeGravity badgeGravity) {
        mBadgeGravity = badgeGravity;
        requestLayout();
    }

    public Typeface getTypeface() {
        return mTypeface;
    }

    public void setTypeface(final String typeface) {
        Typeface tempTypeface;
        try {
            tempTypeface = Typeface.createFromAsset(getContext().getAssets(), typeface);
        } catch (Exception e) {
            tempTypeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL);
            e.printStackTrace();
        }

        setTypeface(tempTypeface);
    }

    public void setTypeface(final Typeface typeface) {
        mTypeface = typeface;
        mModelTitlePaint.setTypeface(typeface);
        setBadgeTypeface();
        postInvalidate();
    }

    private void setBadgeTypeface() {
        mBadgePaint.setTypeface(
                mIsBadgeUseTypeface ? mTypeface : Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        );
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
        mIconPaint.setColorFilter(new PorterDuffColorFilter(inactiveColor, PorterDuff.Mode.SRC_IN));
        mModelTitlePaint.setColor(mInactiveColor);
        postInvalidate();
    }

    public float getCornersRadius() {
        return mCornersRadius;
    }

    public void setCornersRadius(final float cornersRadius) {
        mCornersRadius = cornersRadius;
        postInvalidate();
    }

    public float getBadgeMargin() {
        return mBadgeMargin;
    }

    public float getBarHeight() {
        return mBounds.height();
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
                        mOnTabBarSelectedIndexListener.onStartTabSelected(mModels.get(mIndex), mIndex);
                }

                @Override
                public void onAnimationEnd(final Animator animation) {
                    if (mOnTabBarSelectedIndexListener != null && !mIsViewPagerMode)
                        mOnTabBarSelectedIndexListener.onEndTabSelected(mModels.get(mIndex), mIndex);
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
        if (mIsViewPagerMode) mViewPager.setCurrentItem(index, true);
        postInvalidate();
    }

    // Reset scroller and reset scroll duration equals to animation duration
    private void resetScroller() {
        if (mViewPager == null) return;
        try {
            final Field scrollerField = ViewPager.class.getDeclaredField("mScroller");
            scrollerField.setAccessible(true);
            final ResizeViewPagerScroller scroller = new ResizeViewPagerScroller(getContext());
            scrollerField.set(mViewPager, scroller);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setOnPageChangeListener(final ViewPager.OnPageChangeListener listener) {
        mOnPageChangeListener = listener;
    }

    public int getModelIndex() {
        return mIndex;
    }

    public void setModelIndex(int index) {
        setModelIndex(index, false);
    }

    // Set model index from touch or programmatically
    public void setModelIndex(int index, boolean force) {
        if (mAnimator.isRunning()) return;
        if (mModels.isEmpty()) return;

        // This check gives us opportunity to have an non selected model
        if (mIndex == INVALID_INDEX) force = true;

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
    private void notifyDataSetChanged() {
        postInvalidate();
    }

    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        // Return if animation is running
        if (mAnimator.isRunning()) return true;
        // If is not idle state, return
        if (mScrollState != ViewPager.SCROLL_STATE_IDLE) return true;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // Action down touch
                mIsActionDown = true;
                if (!mIsViewPagerMode) break;
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
                if (mIsActionDown) break;
            case MotionEvent.ACTION_UP:
                // Press up and set model index relative to current coordinate
                if (mIsActionDown) {
                    if (mIsHorizontalOrientation) setModelIndex((int) (event.getX() / mModelSize));
                    else setModelIndex((int) (event.getY() / mModelSize));
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

        // Get measure size
        final int width = MeasureSpec.getSize(widthMeasureSpec);
        final int height = MeasureSpec.getSize(heightMeasureSpec);

        if (mModels.isEmpty() || width == 0 || height == 0) return;

        // Detect orientation and calculate icon size
        if (width > height) {
            mIsHorizontalOrientation = true;

            // Get smaller side
            float side = mModelSize > height ? height : mModelSize;
            if (mIsBadged) side -= side * TITLE_SIZE_FRACTION;

            mModelSize = (float) width / (float) mModels.size();
            mIconSize = (int) (side * (mIsTitled ? TITLE_ICON_SIZE_FRACTION : ICON_SIZE_FRACTION));

            mModelTitleSize = side * TITLE_SIZE_FRACTION;
            mTitleMargin = side * TITLE_MARGIN_FRACTION;

            // If is badged mode, so get vars and set paint with default bounds
            if (mIsBadged) {
                mBadgeTitleSize = mModelTitleSize * BADGE_TITLE_SIZE_FRACTION;

                final Rect badgeBounds = new Rect();
                mBadgePaint.setTextSize(mBadgeTitleSize);
                mBadgePaint.getTextBounds(PREVIEW_BADGE, 0, 1, badgeBounds);
                mBadgeMargin = (badgeBounds.height() * 0.5f) +
                        (mBadgeTitleSize * BADGE_HORIZONTAL_FRACTION * BADGE_VERTICAL_FRACTION);
            }
        } else {
            mIsHorizontalOrientation = false;
            mIsTitled = false;
            mIsBadged = false;

            mModelSize = (float) height / (float) mModels.size();
            mIconSize = (int) ((mModelSize > width ? width : mModelSize) * ICON_SIZE_FRACTION);
        }

        // Set bounds for NTB
        mBounds.set(0.0f, 0.0f, width, height - mBadgeMargin);

        // Set main bitmap
        mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);

        // Set pointer canvas
        mPointerBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        mPointerCanvas = new Canvas(mPointerBitmap);

        // Set icons canvas
        mIconsBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        mIconsCanvas = new Canvas(mIconsBitmap);

        // Set scale fraction for icons
        for (Model model : mModels) {
            final float originalIconSize = model.mIcon.getWidth() > model.mIcon.getHeight() ?
                    model.mIcon.getWidth() : model.mIcon.getHeight();
            model.mInactiveIconScale = (float) mIconSize / originalIconSize;
            model.mActiveIconScaleBy = model.mInactiveIconScale *
                    (mIsTitled ? TITLE_ACTIVE_ICON_SCALE_BY : ACTIVE_ICON_SCALE_BY);
        }

        // Set start position of pointer for preview or on start
        if (isInEditMode() || !mIsViewPagerMode) {
            mIsSetIndexFromTabBar = true;

            // Set random in preview mode
            if (isInEditMode()) {
                mIndex = new Random().nextInt(mModels.size());

                if (mIsBadged)
                    for (int i = 0; i < mModels.size(); i++) {
                        final Model model = mModels.get(i);

                        if (i == mIndex) {
                            model.mBadgeFraction = MAX_FRACTION;
                            model.showBadge();
                        } else {
                            model.mBadgeFraction = MIN_FRACTION;
                            model.hideBadge();
                        }
                    }
            }

            mStartPointerX = mIndex * mModelSize;
            mEndPointerX = mStartPointerX;
            updateIndicatorPosition(MAX_FRACTION);
        }
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        if (mCanvas == null || mPointerCanvas == null || mIconsCanvas == null) return;

        // Reset and clear canvases
        mCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        mPointerCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        mIconsCanvas.drawColor(0, PorterDuff.Mode.CLEAR);

        // Get pointer badge margin for gravity
        final float pointerBadgeMargin = mBadgeGravity == BadgeGravity.TOP ? mBadgeMargin : 0.0f;

        // Draw our model colors
        for (int i = 0; i < mModels.size(); i++) {
            mPaint.setColor(mModels.get(i).getColor());

            if (mIsHorizontalOrientation) {
                final float left = mModelSize * i;
                final float right = left + mModelSize;
                mCanvas.drawRect(
                        left, pointerBadgeMargin, right, mBounds.height() + pointerBadgeMargin, mPaint
                );
            } else {
                final float top = mModelSize * i;
                final float bottom = top + mModelSize;
                mCanvas.drawRect(0.0f, top, mBounds.width(), bottom, mPaint);
            }
        }

        // Set bound of pointer
        if (mIsHorizontalOrientation)
            mPointerBounds.set(
                    mPointerLeftTop, pointerBadgeMargin,
                    mPointerRightBottom, mBounds.height() + pointerBadgeMargin
            );
        else mPointerBounds.set(0.0f, mPointerLeftTop, mBounds.width(), mPointerRightBottom);

        // Draw pointer for model colors
        if (mCornersRadius == 0) mPointerCanvas.drawRect(mPointerBounds, mPaint);
        else mPointerCanvas.drawRoundRect(mPointerBounds, mCornersRadius, mCornersRadius, mPaint);

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

            // Set vars for icon when model with title or without
            final float iconMarginTitleHeight = mIconSize + mTitleMargin + mModelTitleSize;
            final float leftTitleOffset = (mModelSize * i) + (mModelSize * 0.5f);
            final float topTitleOffset =
                    mBounds.height() - (mBounds.height() - iconMarginTitleHeight) * 0.5f;

            if (mIsHorizontalOrientation) {
                leftOffset = (mModelSize * i) + (mModelSize - model.mIcon.getWidth()) * 0.5f;
                topOffset = (mBounds.height() - model.mIcon.getHeight()) * 0.5f;

                matrixCenterX = leftOffset + model.mIcon.getWidth() * 0.5f;
                matrixCenterY = topOffset + model.mIcon.getHeight() * 0.5f +
                        (mIsTitled && mTitleMode == TitleMode.ALL ? mTitleMargin * 0.5f : 0.0f);
            } else {
                leftOffset = (mBounds.width() - model.mIcon.getWidth()) * 0.5f;
                topOffset = (mModelSize * i) + (mModelSize - model.mIcon.getHeight()) * 0.5f;

                matrixCenterX = leftOffset + model.mIcon.getWidth() * 0.5f;
                matrixCenterY = topOffset + model.mIcon.getHeight() * 0.5f;
            }

            // Title translate position
            final float titleTranslate = -model.mIcon.getHeight() + topTitleOffset - mTitleMargin * 0.5f;

            // Translate icon to model center
            model.mIconMatrix.setTranslate(
                    leftOffset,
                    (mIsTitled && mTitleMode == TitleMode.ALL) ? titleTranslate : topOffset
            );

            // Get interpolated fraction for left last and current models
            final float interpolation = mResizeInterpolator.getResizeInterpolation(mFraction, true);
            final float lastInterpolation = mResizeInterpolator.getResizeInterpolation(mFraction, false);

            // Scale value relative to interpolation
            final float matrixScale = model.mActiveIconScaleBy * interpolation;
            final float matrixLastScale = model.mActiveIconScaleBy * lastInterpolation;

            // Get title alpha relative to interpolation
            final int titleAlpha = (int) (MAX_ALPHA * interpolation);
            final int titleLastAlpha = MAX_ALPHA - (int) (MAX_ALPHA * lastInterpolation);
            // Get title scale relative to interpolation
            final float titleScale = MAX_FRACTION + (interpolation * TITLE_ACTIVE_SCALE_BY);
            final float titleLastScale = (MAX_FRACTION + TITLE_ACTIVE_SCALE_BY) -
                    (lastInterpolation * TITLE_ACTIVE_SCALE_BY);

            // Check if we handle models from touch on NTP or from ViewPager
            // There is a strange logic of ViewPager onPageScrolled method, so it is
            if (mIsSetIndexFromTabBar) {
                if (mIndex == i)
                    updateCurrentModel(
                            model, leftOffset, topOffset, titleTranslate, interpolation,
                            matrixCenterX, matrixCenterY, matrixScale, titleScale, titleAlpha
                    );
                else if (mLastIndex == i)
                    updateLastModel(
                            model, leftOffset, topOffset, titleTranslate, lastInterpolation,
                            matrixCenterX, matrixCenterY, matrixLastScale, titleLastScale, titleLastAlpha
                    );
                else
                    updateInactiveModel(
                            model, leftOffset, topOffset, matrixCenterX, matrixCenterY
                    );
            } else {
                if (i != mIndex && i != mIndex + 1)
                    updateInactiveModel(
                            model, leftOffset, topOffset, matrixCenterX, matrixCenterY
                    );
                else if (i == mIndex + 1)
                    updateCurrentModel(
                            model, leftOffset, topOffset, titleTranslate, interpolation,
                            matrixCenterX, matrixCenterY, matrixScale, titleScale, titleAlpha
                    );
                else if (i == mIndex)
                    updateLastModel(
                            model, leftOffset, topOffset, titleTranslate, lastInterpolation,
                            matrixCenterX, matrixCenterY, matrixLastScale, titleLastScale, titleLastAlpha
                    );
            }

            // Draw model icon
            mIconsCanvas.drawBitmap(model.mIcon, model.mIconMatrix, mIconPaint);
            if (mIsTitled)
                mIconsCanvas.drawText(
                        isInEditMode() ? PREVIEW_TITLE : model.getTitle(),
                        leftTitleOffset, topTitleOffset, mModelTitlePaint
                );
        }

        // Draw pointer with active color to wrap out active icon
        if (mCornersRadius == 0) mIconsCanvas.drawRect(mPointerBounds, mIconPointerPaint);
        else mIconsCanvas.drawRoundRect(mPointerBounds, mCornersRadius, mCornersRadius, mIconPointerPaint);

        // Draw general bitmap
        canvas.drawBitmap(mBitmap, 0.0f, 0.0f, null);
        // Draw icons bitmap on top
        canvas.drawBitmap( mIconsBitmap, 0.0f, pointerBadgeMargin, null);

        // If is not badged, exit
        if (!mIsBadged) return;

        // Model badge margin and offset relative to gravity mode
        final float modelBadgeMargin =
                mBadgeGravity == BadgeGravity.TOP ? mBadgeMargin : mBounds.height();
        final float modelBadgeOffset =
                mBadgeGravity == BadgeGravity.TOP ? 0.0f : mBounds.height() - mBadgeMargin;

        for (int i = 0; i < mModels.size(); i++) {
            final Model model = mModels.get(i);

            // Set preview badge title
            if (isInEditMode() || TextUtils.isEmpty(model.getBadgeTitle()))
                model.setBadgeTitle(PREVIEW_BADGE);

            // Set badge title bounds
            mBadgePaint.setTextSize(mBadgeTitleSize * model.mBadgeFraction);
            mBadgePaint.getTextBounds(
                    model.getBadgeTitle(), 0, model.getBadgeTitle().length(), mBadgeBounds
            );

            // Get horizontal and vertical padding for bg
            final float horizontalPadding = mBadgeTitleSize * BADGE_HORIZONTAL_FRACTION;
            final float verticalPadding = horizontalPadding * BADGE_VERTICAL_FRACTION;

            // Set horizontal badge offset
            final float badgeBoundsHorizontalOffset =
                    (mModelSize * i) + (mModelSize * mBadgePosition.mPositionFraction);

            // If is badge title only one char, so create circle else round rect
            if (model.getBadgeTitle().length() == 1) {
                final float badgeMargin = mBadgeMargin * model.mBadgeFraction;
                mBgBadgeBounds.set(
                        badgeBoundsHorizontalOffset - badgeMargin, modelBadgeMargin - badgeMargin,
                        badgeBoundsHorizontalOffset + badgeMargin, modelBadgeMargin + badgeMargin
                );
            } else
                mBgBadgeBounds.set(
                        badgeBoundsHorizontalOffset - mBadgeBounds.centerX() - horizontalPadding,
                        modelBadgeMargin - (mBadgeMargin * model.mBadgeFraction),
                        badgeBoundsHorizontalOffset + mBadgeBounds.centerX() + horizontalPadding,
                        modelBadgeOffset + (verticalPadding * 2.0f) + mBadgeBounds.height()
                );

            // Set color and alpha for badge bg
            if (model.mBadgeFraction == MIN_FRACTION) mBadgePaint.setColor(Color.TRANSPARENT);
            else mBadgePaint.setColor(mActiveColor);
            mBadgePaint.setAlpha((int) (MAX_ALPHA * model.mBadgeFraction));

            // Set corners to round rect for badge bg and draw
            final float cornerRadius = mBgBadgeBounds.height() * 0.5f;
            canvas.drawRoundRect(mBgBadgeBounds, cornerRadius, cornerRadius, mBadgePaint);

            // Set color and alpha for badge title
            if (model.mBadgeFraction == MIN_FRACTION) mBadgePaint.setColor(Color.TRANSPARENT);
            else mBadgePaint.setColor(model.getColor());
            mBadgePaint.setAlpha((int) (MAX_ALPHA * model.mBadgeFraction));

            // Set badge title center position and draw title
            final float badgeHalfHeight = mBadgeBounds.height() * 0.5f;
            float badgeVerticalOffset = (mBgBadgeBounds.height() * 0.5f) + badgeHalfHeight -
                    mBadgeBounds.bottom + modelBadgeOffset;
            canvas.drawText(
                    model.getBadgeTitle(), badgeBoundsHorizontalOffset, badgeVerticalOffset +
                            mBadgeBounds.height() - (mBadgeBounds.height() * model.mBadgeFraction),
                    mBadgePaint
            );
        }
    }

    // Method to transform current fraction of NTB and position
    private void updateCurrentModel(
            final Model model,
            final float leftOffset,
            final float topOffset,
            final float titleTranslate,
            final float interpolation,
            final float matrixCenterX,
            final float matrixCenterY,
            final float matrixScale,
            final float textScale,
            final int textAlpha
    ) {
        if (mIsTitled && mTitleMode == TitleMode.ACTIVE)
            model.mIconMatrix.setTranslate(
                    leftOffset, topOffset - (interpolation * (topOffset - titleTranslate))
            );

        model.mIconMatrix.postScale(
                model.mInactiveIconScale + matrixScale, model.mInactiveIconScale + matrixScale,
                matrixCenterX, matrixCenterY + (mIsTitled && mTitleMode == TitleMode.ACTIVE ?
                                mTitleMargin * 0.5f * interpolation : 0.0f)
        );

        mModelTitlePaint.setTextSize(mModelTitleSize * textScale);
        if (mTitleMode == TitleMode.ACTIVE) mModelTitlePaint.setAlpha(textAlpha);
    }

    // Method to transform last fraction of NTB and position
    private void updateLastModel(
            final Model model,
            final float leftOffset,
            final float topOffset,
            final float titleTranslate,
            final float lastInterpolation,
            final float matrixCenterX,
            final float matrixCenterY,
            final float matrixLastScale,
            final float textLastScale,
            final int textLastAlpha
    ) {
        if (mIsTitled && mTitleMode == TitleMode.ACTIVE)
            model.mIconMatrix.setTranslate(
                    leftOffset, titleTranslate + (lastInterpolation * (topOffset - titleTranslate))
            );

        model.mIconMatrix.postScale(
                model.mInactiveIconScale + model.mActiveIconScaleBy - matrixLastScale,
                model.mInactiveIconScale + model.mActiveIconScaleBy - matrixLastScale,
                matrixCenterX, matrixCenterY + (mIsTitled && mTitleMode == TitleMode.ACTIVE ?
                                mTitleMargin * 0.5f - (mTitleMargin * 0.5f * lastInterpolation) : 0.0f)
        );

        mModelTitlePaint.setTextSize(mModelTitleSize * textLastScale);
        if (mTitleMode == TitleMode.ACTIVE) mModelTitlePaint.setAlpha(textLastAlpha);
    }

    // Method to transform others fraction of NTB and position
    private void updateInactiveModel(
            final Model model,
            final float leftOffset,
            final float topOffset,
            final float matrixCenterX,
            final float matrixCenterY
    ) {
        if (mIsTitled && mTitleMode == TitleMode.ACTIVE)
            model.mIconMatrix.setTranslate(leftOffset, topOffset);

        model.mIconMatrix.postScale(
                model.mInactiveIconScale, model.mInactiveIconScale, matrixCenterX, matrixCenterY
        );

        mModelTitlePaint.setTextSize(mModelTitleSize);
        if (mTitleMode == TitleMode.ACTIVE) mModelTitlePaint.setAlpha(MIN_ALPHA);
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

        if (mOnPageChangeListener != null)
            mOnPageChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
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
    }

    @Override
    public void onPageScrollStateChanged(final int state) {
        // If VP idle, reset to MIN_FRACTION
        if (state == ViewPager.SCROLL_STATE_IDLE) {
            mFraction = MIN_FRACTION;
            mIsSetIndexFromTabBar = false;

            if (mOnPageChangeListener != null) mOnPageChangeListener.onPageSelected(mIndex);
            else {
                if (mOnTabBarSelectedIndexListener != null)
                    mOnTabBarSelectedIndexListener.onEndTabSelected(mModels.get(mIndex), mIndex);
            }
        }
        mScrollState = state;

        if (mOnPageChangeListener != null) mOnPageChangeListener.onPageScrollStateChanged(state);
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

        private String mTitle = "";
        private int mColor;

        private Bitmap mIcon;
        private Matrix mIconMatrix = new Matrix();

        private String mBadgeTitle = "";
        private String mTempBadgeTitle = "";
        private float mBadgeFraction;

        private boolean mIsBadgeShowed;
        private boolean mIsBadgeUpdated;

        private ValueAnimator mBadgeAnimator = new ValueAnimator();

        private float mInactiveIconScale;
        private float mActiveIconScaleBy;

        public Model(final Drawable icon, final int color) {
            mColor = color;
            mIcon = icon != null ? ((BitmapDrawable) icon).getBitmap() :
                    Bitmap.createBitmap(1, 1, Bitmap.Config.RGB_565);

            mBadgeAnimator.addListener(new Animator.AnimatorListener() {

                @Override
                public void onAnimationStart(final Animator animation) {
                }

                @Override
                public void onAnimationEnd(final Animator animation) {
                    // Detect whether we just update text and don`t reset show state
                    if (!mIsBadgeUpdated) mIsBadgeShowed = !mIsBadgeShowed;
                    else mIsBadgeUpdated = false;
                }

                @Override
                public void onAnimationCancel(final Animator animation) {

                }

                @Override
                public void onAnimationRepeat(final Animator animation) {
                    // Change title when we update and don`t see the title
                    if (mIsBadgeUpdated) mBadgeTitle = mTempBadgeTitle;
                }
            });
        }

        public Model(final Drawable icon, final int color, final String title) {
            this(icon, color);
            mTitle = title;
        }

        public Model(final Drawable icon, final int color, final String title, final String badgeTitle) {
            this(icon, color, title);
            mBadgeTitle = badgeTitle;
        }

        public String getTitle() {
            return mTitle;
        }

        public void setTitle(final String title) {
            mTitle = title;
        }

        public int getColor() {
            return mColor;
        }

        public void setColor(final int color) {
            mColor = color;
        }

        public boolean isBadgeShowed() {
            return mIsBadgeShowed;
        }

        public String getBadgeTitle() {
            return mBadgeTitle;
        }

        public void setBadgeTitle(final String badgeTitle) {
            mBadgeTitle = badgeTitle;
        }

        // If your badge is visible on screen, so you can update title with animation
        public void updateBadgeTitle(final String badgeTitle) {
            if (!mIsBadgeShowed) return;
            if (mBadgeAnimator.isRunning()) mBadgeAnimator.end();

            mTempBadgeTitle = badgeTitle;
            mIsBadgeUpdated = true;

            mBadgeAnimator.setFloatValues(MAX_FRACTION, MIN_FRACTION);
            mBadgeAnimator.setDuration(DEFAULT_BADGE_REFRESH_ANIMATION_DURATION);
            mBadgeAnimator.setRepeatMode(ValueAnimator.REVERSE);
            mBadgeAnimator.setRepeatCount(1);
            mBadgeAnimator.start();
        }

        public void toggleBadge() {
            if (mBadgeAnimator.isRunning()) mBadgeAnimator.end();
            if (mIsBadgeShowed) hideBadge();
            else showBadge();
        }

        public void showBadge() {
            mIsBadgeUpdated = false;

            if (mBadgeAnimator.isRunning()) mBadgeAnimator.end();
            if (mIsBadgeShowed) return;

            mBadgeAnimator.setFloatValues(MIN_FRACTION, MAX_FRACTION);
            mBadgeAnimator.setInterpolator(DECELERATE_INTERPOLATOR);
            mBadgeAnimator.setDuration(DEFAULT_BADGE_ANIMATION_DURATION);
            mBadgeAnimator.setRepeatMode(ValueAnimator.RESTART);
            mBadgeAnimator.setRepeatCount(0);
            mBadgeAnimator.start();
        }

        public void hideBadge() {
            mIsBadgeUpdated = false;

            if (mBadgeAnimator.isRunning()) mBadgeAnimator.end();
            if (!mIsBadgeShowed) return;

            mBadgeAnimator.setFloatValues(MAX_FRACTION, MIN_FRACTION);
            mBadgeAnimator.setInterpolator(ACCELERATE_INTERPOLATOR);
            mBadgeAnimator.setDuration(DEFAULT_BADGE_ANIMATION_DURATION);
            mBadgeAnimator.setRepeatMode(ValueAnimator.RESTART);
            mBadgeAnimator.setRepeatCount(0);
            mBadgeAnimator.start();
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
            if (mResizeIn) return (float) (1.0f - Math.pow((1.0f - input), 2.0f * mFactor));
            else return (float) (Math.pow(input, 2.0f * mFactor));
        }

        public float getResizeInterpolation(final float input, final boolean resizeIn) {
            mResizeIn = resizeIn;
            return getInterpolation(input);
        }
    }

    // Model title mode
    public enum TitleMode {
        ALL, ACTIVE
    }

    // Model badge position
    public enum BadgePosition {

        LEFT(LEFT_FRACTION), CENTER(CENTER_FRACTION), RIGHT(RIGHT_FRACTION);

        private float mPositionFraction;

        BadgePosition() {
            mPositionFraction = RIGHT_FRACTION;
        }

        BadgePosition(final float positionFraction) {
            mPositionFraction = positionFraction;
        }
    }

    // Model badge gravity
    public enum BadgeGravity {
        TOP, BOTTOM
    }

    // Out listener for selected index
    public interface OnTabBarSelectedIndexListener {
        void onStartTabSelected(final Model model, final int index);

        void onEndTabSelected(final Model model, final int index);
    }
}
