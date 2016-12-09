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

package devlight.io.library.ntb;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
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
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Scroller;

import com.gigamole.navigationtabbar.R;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import devlight.io.library.behavior.NavigationTabBarBehavior;

/**
 * Created by GIGAMOLE on 24.03.2016.
 */
@SuppressWarnings({"unused", "DefaultFileTemplate"})
public class NavigationTabBar extends View implements ViewPager.OnPageChangeListener {

    // NTB constants
    protected final static int FLAGS =
            Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG | Paint.FILTER_BITMAP_FLAG;

    protected final static String PREVIEW_BADGE = "0";
    protected final static String PREVIEW_TITLE = "Title";

    protected final static int INVALID_INDEX = -1;
    public final static int AUTO_SIZE = -2;
    public final static int AUTO_COLOR = -3;
    public final static int AUTO_SCALE = -4;

    protected final static int DEFAULT_BADGE_ANIMATION_DURATION = 200;
    protected final static int DEFAULT_BADGE_REFRESH_ANIMATION_DURATION = 100;
    protected final static int DEFAULT_ANIMATION_DURATION = 300;
    protected final static float DEFAULT_ICON_SIZE_FRACTION = 0.5F;
    protected final static float DEFAULT_TITLE_ICON_SIZE_FRACTION = 0.5F;

    protected final static int DEFAULT_INACTIVE_COLOR = Color.parseColor("#9f90af");
    protected final static int DEFAULT_ACTIVE_COLOR = Color.WHITE;
    protected final static int DEFAULT_BG_COLOR = Color.parseColor("#605271");

    protected final static float MIN_FRACTION = 0.0F;
    protected final static float MAX_FRACTION = 1.0F;

    protected final static int MIN_ALPHA = 0;
    protected final static int MAX_ALPHA = 255;

    protected final static float SCALED_FRACTION = 0.3F;
    protected final static float TITLE_ACTIVE_ICON_SCALE_BY = 0.2F;
    protected final static float TITLE_ACTIVE_SCALE_BY = 0.2F;
    protected final static float TITLE_SIZE_FRACTION = 0.2F;
    protected final static float TITLE_MARGIN_FRACTION = 0.15F;
    protected final static float TITLE_MARGIN_SCALE_FRACTION = 0.25F;

    protected final static float BADGE_HORIZONTAL_FRACTION = 0.5F;
    protected final static float BADGE_VERTICAL_FRACTION = 0.75F;
    protected final static float BADGE_TITLE_SIZE_FRACTION = 0.9F;

    protected final static float LEFT_FRACTION = 0.25F;
    protected final static float CENTER_FRACTION = 0.5F;
    protected final static float RIGHT_FRACTION = 0.75F;

    protected final static Interpolator DECELERATE_INTERPOLATOR = new DecelerateInterpolator();
    protected final static Interpolator ACCELERATE_INTERPOLATOR = new AccelerateInterpolator();
    protected final static Interpolator OUT_SLOW_IN_INTERPOLATOR = new LinearOutSlowInInterpolator();

    // NTB and pointer bounds
    protected final RectF mBounds = new RectF();
    protected final RectF mBgBounds = new RectF();
    protected final RectF mPointerBounds = new RectF();
    // Badge bounds and bg badge bounds
    protected final Rect mBadgeBounds = new Rect();
    protected final RectF mBgBadgeBounds = new RectF();

    // Canvas, where all of other canvas will be merged
    protected Bitmap mBitmap;
    protected final Canvas mCanvas = new Canvas();

    // Canvas with icons
    protected Bitmap mIconsBitmap;
    protected final Canvas mIconsCanvas = new Canvas();

    // Canvas with titles
    protected Bitmap mTitlesBitmap;
    protected final Canvas mTitlesCanvas = new Canvas();

    // Canvas for our rect pointer
    protected Bitmap mPointerBitmap;
    protected final Canvas mPointerCanvas = new Canvas();

    // External background view for the NTB
    protected NavigationTabBarBehavior mBehavior;

    // Detect if behavior already set
    protected boolean mIsBehaviorSet;
    // Detect if behavior enabled
    protected boolean mBehaviorEnabled;
    // Detect if need to hide NTB
    protected boolean mNeedHide;
    // Detect if need animate animate or force hide
    protected boolean mAnimateHide;

    // Main paint
    protected final Paint mPaint = new Paint(FLAGS) {
        {
            setStyle(Style.FILL);
        }
    };
    // Background color paint
    protected final Paint mBgPaint = new Paint(FLAGS) {
        {
            setStyle(Style.FILL);
        }
    };
    // Pointer paint
    protected final Paint mPointerPaint = new Paint(FLAGS) {
        {
            setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        }
    };

    // Icons paint
    protected final Paint mIconPaint = new Paint(FLAGS);
    protected final Paint mSelectedIconPaint = new Paint(FLAGS);

    // Paint for icon mask pointer
    protected final Paint mIconPointerPaint = new Paint(FLAGS) {
        {
            setStyle(Style.FILL);
            setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        }
    };

    // Paint for model title
    protected final Paint mModelTitlePaint = new TextPaint(FLAGS) {
        {
            setColor(Color.WHITE);
            setTextAlign(Align.CENTER);
        }
    };

    // Paint for badge
    protected final Paint mBadgePaint = new TextPaint(FLAGS) {
        {
            setTextAlign(Align.CENTER);
            setFakeBoldText(true);
        }
    };

    // Variables for animator
    protected final ValueAnimator mAnimator = new ValueAnimator();
    protected final ResizeInterpolator mResizeInterpolator = new ResizeInterpolator();
    protected int mAnimationDuration;

    // NTB models
    protected final List<Model> mModels = new ArrayList<>();

    // Variables for ViewPager
    protected ViewPager mViewPager;
    protected ViewPager.OnPageChangeListener mOnPageChangeListener;
    protected int mScrollState;

    // Tab listener
    protected OnTabBarSelectedIndexListener mOnTabBarSelectedIndexListener;
    protected ValueAnimator.AnimatorListener mAnimatorListener;

    // Variables for sizes
    protected float mModelSize;
    protected float mIconSize;
    protected float mIconSizeFraction;
    // Corners radius for rect mode
    protected float mCornersRadius;

    // Model title size and margin
    protected float mModelTitleSize = AUTO_SIZE;
    protected float mTitleMargin;

    // Model badge title size and margin
    protected float mBadgeMargin;
    protected float mBadgeTitleSize = AUTO_SIZE;

    // Model title mode: active ar all
    protected TitleMode mTitleMode;
    // Model badge position: left, center or right
    protected BadgePosition mBadgePosition;
    // Model badge gravity: top or bottom
    protected BadgeGravity mBadgeGravity;

    // Model badge bg and title color.
    // By default badge bg color is the active model color and badge title color is the model bg color
    // To reset colors just set bg and title color to AUTO_COLOR
    protected int mBadgeTitleColor = AUTO_COLOR;
    protected int mBadgeBgColor = AUTO_COLOR;

    // Indexes
    protected int mLastIndex = INVALID_INDEX;
    protected int mIndex = INVALID_INDEX;
    // General fraction value
    protected float mFraction;

    // Coordinates of pointer
    protected float mStartPointerX;
    protected float mEndPointerX;
    protected float mPointerLeftTop;
    protected float mPointerRightBottom;

    // Detect if model has title
    protected boolean mIsTitled;
    // Detect if model has badge
    protected boolean mIsBadged;
    // Detect if model icon scaled
    protected boolean mIsScaled;
    // Detect if model icon tinted
    protected boolean mIsTinted;
    // Detect if model can swiped
    protected boolean mIsSwiped;
    // Detect if model badge have custom typeface
    protected boolean mIsBadgeUseTypeface;
    // Detect if is bar mode or indicator pager mode
    protected boolean mIsViewPagerMode;
    // Detect whether the horizontal orientation
    protected boolean mIsHorizontalOrientation;
    // Detect if we move from left to right
    protected boolean mIsResizeIn;
    // Detect if we get action down event
    protected boolean mIsActionDown;
    // Detect if we get action down event on pointer
    protected boolean mIsPointerActionDown;
    // Detect when we set index from tab bar nor from ViewPager
    protected boolean mIsSetIndexFromTabBar;

    // Color variables
    protected int mInactiveColor;
    protected int mActiveColor;
    protected int mBgColor;

    // Custom typeface
    protected Typeface mTypeface;

    public NavigationTabBar(final Context context) {
        this(context, null);
    }

    public NavigationTabBar(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @SuppressWarnings({"ResourceAsColor", "ResourceType"})
    public NavigationTabBar(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //Init NTB

        // Always draw
        setWillNotDraw(false);
        // Speed and fix for pre 17 API
        ViewCompat.setLayerType(this, ViewCompat.LAYER_TYPE_SOFTWARE, null);
        setLayerType(LAYER_TYPE_SOFTWARE, null);

        final TypedArray typedArray =
                context.obtainStyledAttributes(attrs, R.styleable.NavigationTabBar);
        try {
            setIsTitled(typedArray.getBoolean(R.styleable.NavigationTabBar_ntb_titled, false));
            setIsBadged(typedArray.getBoolean(R.styleable.NavigationTabBar_ntb_badged, false));
            setIsScaled(typedArray.getBoolean(R.styleable.NavigationTabBar_ntb_scaled, true));
            setIsTinted(typedArray.getBoolean(R.styleable.NavigationTabBar_ntb_tinted, true));
            setIsSwiped(typedArray.getBoolean(R.styleable.NavigationTabBar_ntb_swiped, true));
            setTitleSize(
                    typedArray.getDimension(R.styleable.NavigationTabBar_ntb_title_size, AUTO_SIZE)
            );
            setIsBadgeUseTypeface(
                    typedArray.getBoolean(
                            R.styleable.NavigationTabBar_ntb_badge_use_typeface,
                            false
                    )
            );

            setTitleMode(
                    typedArray.getInt(
                            R.styleable.NavigationTabBar_ntb_title_mode, TitleMode.ALL_INDEX
                    )
            );
            setBadgeSize(
                    typedArray.getDimension(R.styleable.NavigationTabBar_ntb_badge_size, AUTO_SIZE)
            );
            setBadgePosition(
                    typedArray.getInt(
                            R.styleable.NavigationTabBar_ntb_badge_position,
                            BadgePosition.RIGHT_INDEX
                    )
            );
            setBadgeGravity(
                    typedArray.getInt(
                            R.styleable.NavigationTabBar_ntb_badge_gravity, BadgeGravity.TOP_INDEX
                    )
            );
            setBadgeBgColor(
                    typedArray.getColor(R.styleable.NavigationTabBar_ntb_badge_bg_color, AUTO_COLOR)
            );
            setBadgeTitleColor(
                    typedArray.getColor(
                            R.styleable.NavigationTabBar_ntb_badge_title_color, AUTO_COLOR
                    )
            );

            setTypeface(typedArray.getString(R.styleable.NavigationTabBar_ntb_typeface));
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
            setBgColor(
                    typedArray.getColor(
                            R.styleable.NavigationTabBar_ntb_bg_color, DEFAULT_BG_COLOR
                    )
            );
            setAnimationDuration(
                    typedArray.getInteger(
                            R.styleable.NavigationTabBar_ntb_animation_duration,
                            DEFAULT_ANIMATION_DURATION
                    )
            );
            setCornersRadius(
                    typedArray.getDimension(R.styleable.NavigationTabBar_ntb_corners_radius, 0.0F)
            );
            setIconSizeFraction(
                    typedArray.getFloat(
                            R.styleable.NavigationTabBar_ntb_icon_size_fraction,
                            AUTO_SCALE
                    )
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
                        previewColors =
                                typedArray.getResources().getStringArray(R.array.default_preview);

                    for (String previewColor : previewColors)
                        mModels.add(new Model.Builder(null, Color.parseColor(previewColor)).build());
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

    public List<Model> getModels() {
        return mModels;
    }

    public void setModels(final List<Model> models) {
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
        mModels.addAll(models);
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

    public boolean isScaled() {
        return mIsScaled;
    }

    public void setIsScaled(final boolean isScaled) {
        mIsScaled = isScaled;
        requestLayout();
    }

    public boolean isTinted() {
        return mIsTinted;
    }

    public void setIsTinted(final boolean isTinted) {
        mIsTinted = isTinted;
        updateTint();
    }

    public boolean isSwiped() {
        return mIsSwiped;
    }

    public void setIsSwiped(final boolean swiped) {
        mIsSwiped = swiped;
    }

    public float getTitleSize() {
        return mModelTitleSize;
    }

    // To reset title size to automatic just put in method AUTO_SIZE value
    public void setTitleSize(final float modelTitleSize) {
        mModelTitleSize = modelTitleSize;
        if (modelTitleSize == AUTO_SIZE) requestLayout();
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

    protected void setTitleMode(final int index) {
        switch (index) {
            case TitleMode.ACTIVE_INDEX:
                setTitleMode(TitleMode.ACTIVE);
                break;
            case TitleMode.ALL_INDEX:
            default:
                setTitleMode(TitleMode.ALL);
                break;
        }
    }

    public void setTitleMode(final TitleMode titleMode) {
        mTitleMode = titleMode;
        postInvalidate();
    }

    public BadgePosition getBadgePosition() {
        return mBadgePosition;
    }

    protected void setBadgePosition(final int index) {
        switch (index) {
            case BadgePosition.LEFT_INDEX:
                setBadgePosition(BadgePosition.LEFT);
                break;
            case BadgePosition.CENTER_INDEX:
                setBadgePosition(BadgePosition.CENTER);
                break;
            case BadgePosition.RIGHT_INDEX:
            default:
                setBadgePosition(BadgePosition.RIGHT);
                break;
        }
    }

    public void setBadgePosition(final BadgePosition badgePosition) {
        mBadgePosition = badgePosition;
        postInvalidate();
    }

    public BadgeGravity getBadgeGravity() {
        return mBadgeGravity;
    }

    protected void setBadgeGravity(final int index) {
        switch (index) {
            case BadgeGravity.BOTTOM_INDEX:
                setBadgeGravity(BadgeGravity.BOTTOM);
                break;
            case BadgeGravity.TOP_INDEX:
            default:
                setBadgeGravity(BadgeGravity.TOP);
                break;
        }
    }

    public void setBadgeGravity(final BadgeGravity badgeGravity) {
        mBadgeGravity = badgeGravity;
        requestLayout();
    }

    public int getBadgeBgColor() {
        return mBadgeBgColor;
    }

    public void setBadgeBgColor(final int badgeBgColor) {
        mBadgeBgColor = badgeBgColor;
    }

    public int getBadgeTitleColor() {
        return mBadgeTitleColor;
    }

    public void setBadgeTitleColor(final int badgeTitleColor) {
        mBadgeTitleColor = badgeTitleColor;
    }

    public float getBadgeSize() {
        return mBadgeTitleSize;
    }

    // To reset badge title size to automatic just put in method AUTO_SIZE value
    public void setBadgeSize(final float badgeTitleSize) {
        mBadgeTitleSize = badgeTitleSize;
        if (mBadgeTitleSize == AUTO_SIZE) requestLayout();
    }

    public Typeface getTypeface() {
        return mTypeface;
    }

    public void setTypeface(final String typeface) {
        if (TextUtils.isEmpty(typeface)) return;

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

    protected void setBadgeTypeface() {
        mBadgePaint.setTypeface(
                mIsBadgeUseTypeface ? mTypeface : Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        );
    }

    public int getActiveColor() {
        return mActiveColor;
    }

    public void setActiveColor(final int activeColor) {
        mActiveColor = activeColor;

        // Set icon pointer active color
        mIconPointerPaint.setColor(mActiveColor);
        updateTint();
    }

    public int getInactiveColor() {
        return mInactiveColor;
    }

    public void setInactiveColor(final int inactiveColor) {
        mInactiveColor = inactiveColor;

        // Set inactive color to title
        mModelTitlePaint.setColor(mInactiveColor);
        updateTint();
    }

    public int getBgColor() {
        return mBgColor;
    }

    public void setBgColor(final int bgColor) {
        mBgColor = bgColor;
        mBgPaint.setColor(mBgColor);
        postInvalidate();
    }

    public float getCornersRadius() {
        return mCornersRadius;
    }

    public void setCornersRadius(final float cornersRadius) {
        mCornersRadius = cornersRadius;
        postInvalidate();
    }

    public float getIconSizeFraction() {
        return mIconSizeFraction;
    }

    // To reset scale fraction of icon to automatic just put in method AUTO_SCALE value
    public void setIconSizeFraction(final float iconSizeFraction) {
        mIconSizeFraction = iconSizeFraction;
        requestLayout();
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
    public void setOnTabBarSelectedIndexListener(
            final OnTabBarSelectedIndexListener onTabBarSelectedIndexListener
    ) {
        mOnTabBarSelectedIndexListener = onTabBarSelectedIndexListener;

        if (mAnimatorListener == null)
            mAnimatorListener = new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(final Animator animation) {
                    if (mOnTabBarSelectedIndexListener != null)
                        mOnTabBarSelectedIndexListener.onStartTabSelected(
                                mModels.get(mIndex), mIndex
                        );

                    animation.removeListener(this);
                    animation.addListener(this);
                }

                @Override
                public void onAnimationEnd(final Animator animation) {
                    if (mIsViewPagerMode) return;

                    animation.removeListener(this);
                    animation.addListener(this);

                    if (mOnTabBarSelectedIndexListener != null)
                        mOnTabBarSelectedIndexListener.onEndTabSelected(
                                mModels.get(mIndex), mIndex
                        );
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

        if (viewPager.equals(mViewPager)) return;
        if (mViewPager != null) //noinspection deprecation
            mViewPager.setOnPageChangeListener(null);
        if (viewPager.getAdapter() == null)
            throw new IllegalStateException("ViewPager does not provide adapter instance.");

        mIsViewPagerMode = true;
        mViewPager = viewPager;
        mViewPager.removeOnPageChangeListener(this);
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
    protected void resetScroller() {
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

    // Return if the behavior translation is enabled
    public boolean isBehaviorEnabled() {
        return mBehaviorEnabled;
    }

    // Set the behavior translation value
    public void setBehaviorEnabled(final boolean enabled) {
        mBehaviorEnabled = enabled;

        if (getParent() != null && getParent() instanceof CoordinatorLayout) {
            final ViewGroup.LayoutParams params = getLayoutParams();
            if (mBehavior == null) mBehavior = new NavigationTabBarBehavior(enabled);
            else mBehavior.setBehaviorTranslationEnabled(enabled);

            ((CoordinatorLayout.LayoutParams) params).setBehavior(mBehavior);
            if (mNeedHide) {
                mNeedHide = false;
                mBehavior.hideView(this, (int) getBarHeight(), mAnimateHide);
            }
        }
    }

    public int getModelIndex() {
        return mIndex;
    }

    public void setModelIndex(int index) {
        setModelIndex(index, false);
    }

    // Set model index from touch or programmatically
    public void setModelIndex(final int modelIndex, final boolean isForce) {
        if (mAnimator.isRunning()) return;
        if (mModels.isEmpty()) return;

        int index = modelIndex;
        boolean force = isForce;

        // This check gives us opportunity to have an non selected model
        if (mIndex == INVALID_INDEX) force = true;
        // Detect if last is the same
        if (index == mIndex) force = true;
        // Snap index to models size
        index = Math.max(0, Math.min(index, mModels.size() - 1));

        mIsResizeIn = index < mIndex;
        mLastIndex = mIndex;
        mIndex = index;

        mIsSetIndexFromTabBar = true;
        if (mIsViewPagerMode) {
            if (mViewPager == null) throw new IllegalStateException("ViewPager is null.");
            mViewPager.setCurrentItem(index, !force);
        }

        // Set startX and endX for animation,
        // where we animate two sides of rect with different interpolation
        if (force) {
            mStartPointerX = mIndex * mModelSize;
            mEndPointerX = mStartPointerX;
        } else {
            mStartPointerX = mPointerLeftTop;
            mEndPointerX = mIndex * mModelSize;
        }

        // If it force, so update immediately, else animate
        // This happens if we set index onCreate or something like this
        // You can use force param or call this method in some post()
        if (force) {
            updateIndicatorPosition(MAX_FRACTION);

            if (mOnTabBarSelectedIndexListener != null)
                mOnTabBarSelectedIndexListener.onStartTabSelected(mModels.get(mIndex), mIndex);

            // Force onPageScrolled listener and refresh VP
            if (mIsViewPagerMode) {
                if (!mViewPager.isFakeDragging()) mViewPager.beginFakeDrag();
                if (mViewPager.isFakeDragging()) mViewPager.fakeDragBy(0.0F);
                if (mViewPager.isFakeDragging()) mViewPager.endFakeDrag();
            } else {
                if (mOnTabBarSelectedIndexListener != null)
                    mOnTabBarSelectedIndexListener.onEndTabSelected(mModels.get(mIndex), mIndex);
            }
        } else mAnimator.start();
    }

    // Deselect active index and reset pointer
    public void deselect() {
        mLastIndex = INVALID_INDEX;
        mIndex = INVALID_INDEX;
        mStartPointerX = INVALID_INDEX * mModelSize;
        mEndPointerX = mStartPointerX;
        updateIndicatorPosition(MIN_FRACTION);
    }

    protected void updateIndicatorPosition(final float fraction) {
        // Update general fraction
        mFraction = fraction;

        // Set the pointer left top side coordinate
        mPointerLeftTop = mStartPointerX +
                (mResizeInterpolator.getResizeInterpolation(fraction, mIsResizeIn) *
                        (mEndPointerX - mStartPointerX));
        // Set the pointer right bottom side coordinate
        mPointerRightBottom = (mStartPointerX + mModelSize) +
                (mResizeInterpolator.getResizeInterpolation(fraction, !mIsResizeIn) *
                        (mEndPointerX - mStartPointerX));

        // Update pointer
        postInvalidate();
    }

    // Update NTB
    protected void notifyDataSetChanged() {
        requestLayout();
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
                if (!mIsSwiped) break;
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
                    playSoundEffect(SoundEffectConstants.CLICK);
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

    @SuppressLint("DrawAllocation")
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

            // Get model size
            mModelSize = (float) width / (float) mModels.size();

            // Get smaller side
            float side = mModelSize > height ? height : mModelSize;
            if (mIsBadged) side -= side * TITLE_SIZE_FRACTION;

            mIconSize = side * (mIconSizeFraction != AUTO_SCALE ? mIconSizeFraction :
                    (mIsTitled ? DEFAULT_TITLE_ICON_SIZE_FRACTION : DEFAULT_ICON_SIZE_FRACTION));
            if (mModelTitleSize == AUTO_SIZE) mModelTitleSize = side * TITLE_SIZE_FRACTION;
            mTitleMargin = side * TITLE_MARGIN_FRACTION;

            // If is badged mode, so get vars and set paint with default bounds
            if (mIsBadged) {
                if (mBadgeTitleSize == AUTO_SIZE)
                    mBadgeTitleSize = (side * TITLE_SIZE_FRACTION) * BADGE_TITLE_SIZE_FRACTION;

                final Rect badgeBounds = new Rect();
                mBadgePaint.setTextSize(mBadgeTitleSize);
                mBadgePaint.getTextBounds(PREVIEW_BADGE, 0, 1, badgeBounds);
                mBadgeMargin = (badgeBounds.height() * 0.5F) +
                        (mBadgeTitleSize * BADGE_HORIZONTAL_FRACTION * BADGE_VERTICAL_FRACTION);
            }
        } else {
            // Disable vertical translation in coordinator layout
            mBehaviorEnabled = false;
            // Disable other features
            mIsHorizontalOrientation = false;
//            mIsTitled = false;
            mIsBadged = false;

            mModelSize = (float) height / (float) mModels.size();
            // Get smaller side
            float side = mModelSize > width ? width : mModelSize;

            mIconSize = (int) (side *
                    (mIconSizeFraction == AUTO_SCALE ?
                            DEFAULT_ICON_SIZE_FRACTION : mIconSizeFraction));

            if (mModelTitleSize == AUTO_SIZE) mModelTitleSize = side * TITLE_SIZE_FRACTION;
            mTitleMargin = side * TITLE_MARGIN_FRACTION;
        }

        // Set bounds for NTB
        mBounds.set(0.0F, 0.0F, width, height - mBadgeMargin);

        final float barBadgeMargin = mBadgeGravity == BadgeGravity.TOP ? mBadgeMargin : 0.0F;
        mBgBounds.set(0.0F, barBadgeMargin, mBounds.width(), mBounds.height() + barBadgeMargin);

        // Set scale fraction for icons
        for (Model model : mModels) {
            final float originalIconSize = model.mIcon.getWidth() > model.mIcon.getHeight() ?
                    model.mIcon.getWidth() : model.mIcon.getHeight();
            model.mInactiveIconScale = mIconSize / originalIconSize;
            model.mActiveIconScaleBy = model.mInactiveIconScale *
                    (mIsTitled ? TITLE_ACTIVE_ICON_SCALE_BY : SCALED_FRACTION);
        }

        // Reset bitmap to init it onDraw()
        mBitmap = null;
        mPointerBitmap = null;
        mIconsBitmap = null;
        if (mIsTitled) mTitlesBitmap = null;

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

        //The translation behavior has to be set up after the super.onMeasure has been called
        if (!mIsBehaviorSet) {
            setBehaviorEnabled(mBehaviorEnabled);
            mIsBehaviorSet = true;
        }
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onDraw(final Canvas canvas) {
        // Get height of NTB with badge on nor
        final int mBadgedHeight = (int) (mBounds.height() + mBadgeMargin);

        // Set main canvas
        if (mBitmap == null || mBitmap.isRecycled()) {
            mBitmap = Bitmap.createBitmap(
                    (int) mBounds.width(), mBadgedHeight, Bitmap.Config.ARGB_8888
            );
            mCanvas.setBitmap(mBitmap);
        }
        // Set pointer canvas
        if (mPointerBitmap == null || mPointerBitmap.isRecycled()) {
            mPointerBitmap = Bitmap.createBitmap(
                    (int) mBounds.width(), mBadgedHeight, Bitmap.Config.ARGB_8888
            );
            mPointerCanvas.setBitmap(mPointerBitmap);
        }
        // Set icons canvas
        if (mIconsBitmap == null || mIconsBitmap.isRecycled()) {
            mIconsBitmap = Bitmap.createBitmap(
                    (int) mBounds.width(), mBadgedHeight, Bitmap.Config.ARGB_8888
            );
            mIconsCanvas.setBitmap(mIconsBitmap);
        }
        // Set titles canvas
        if (mIsTitled) {
            if (mTitlesBitmap == null || mTitlesBitmap.isRecycled()) {
                mTitlesBitmap = Bitmap.createBitmap(
                        (int) mBounds.width(), mBadgedHeight, Bitmap.Config.ARGB_8888
                );
                mTitlesCanvas.setBitmap(mTitlesBitmap);
            }
        } else mTitlesBitmap = null;

        // Reset and clear canvases
        mCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        mPointerCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        mIconsCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        if (mIsTitled) mTitlesCanvas.drawColor(0, PorterDuff.Mode.CLEAR);

        if (mCornersRadius == 0) canvas.drawRect(mBgBounds, mBgPaint);
        else canvas.drawRoundRect(mBgBounds, mCornersRadius, mCornersRadius, mBgPaint);

        // Get pointer badge margin for gravity
        final float barBadgeMargin = mBadgeGravity == BadgeGravity.TOP ? mBadgeMargin : 0.0F;

        // Draw our model colors
        for (int i = 0; i < mModels.size(); i++) {
            mPaint.setColor(mModels.get(i).getColor());

            if (mIsHorizontalOrientation) {
                final float left = mModelSize * i;
                final float right = left + mModelSize;
                mCanvas.drawRect(
                        left, barBadgeMargin, right, mBounds.height() + barBadgeMargin, mPaint
                );
            } else {
                final float top = mModelSize * i;
                final float bottom = top + mModelSize;
                mCanvas.drawRect(0.0F, top, mBounds.width(), bottom, mPaint);
            }
        }

        // Set bound of pointer
        if (mIsHorizontalOrientation) mPointerBounds.set(
                mPointerLeftTop,
                barBadgeMargin,
                mPointerRightBottom,
                mBounds.height() + barBadgeMargin
        );
        else mPointerBounds.set(0.0F, mPointerLeftTop, mBounds.width(), mPointerRightBottom);

        // Draw pointer for model colors
        if (mCornersRadius == 0) mPointerCanvas.drawRect(mPointerBounds, mPaint);
        else mPointerCanvas.drawRoundRect(mPointerBounds, mCornersRadius, mCornersRadius, mPaint);

        // Draw pointer into main canvas
        mCanvas.drawBitmap(mPointerBitmap, 0.0F, 0.0F, mPointerPaint);

        // Set vars for icon when model with title or without
        final float iconMarginTitleHeight = mIconSize + mTitleMargin + mModelTitleSize;

        // Draw model icons
        for (int i = 0; i < mModels.size(); i++) {
            final Model model = mModels.get(i);

            // Variables to center our icons
            final float leftOffset;
            final float topOffset;
            final float matrixCenterX;
            final float matrixCenterY;

            // Set offset to titles
            final float leftTitleOffset;
            final float topTitleOffset;
            if (mIsHorizontalOrientation) {
                leftOffset = (mModelSize * i) + (mModelSize - model.mIcon.getWidth()) * 0.5F;
                topOffset = (mBounds.height() - model.mIcon.getHeight()) * 0.5F;

                // Set offset to titles
                leftTitleOffset = (mModelSize * i) + (mModelSize * 0.5F);
                topTitleOffset =
                        mBounds.height() - (mBounds.height() - iconMarginTitleHeight) * 0.5F;

            } else {
                leftOffset = (mBounds.width() - (float) model.mIcon.getWidth()) * 0.5F;
                topOffset = (mModelSize * i) + (mModelSize - (float) model.mIcon.getHeight()) * 0.5F;

                // Set offset to titles
                leftTitleOffset = leftOffset + (float) model.mIcon.getWidth() * 0.5F;
                topTitleOffset = topOffset +  (model.mIcon.getHeight() + iconMarginTitleHeight) * 0.5f;
            }

            matrixCenterX = leftOffset + (float) model.mIcon.getWidth() * 0.5F;
            matrixCenterY = topOffset + (float) model.mIcon.getHeight() * 0.5F;

            // Title translate position
            final float titleTranslate =
                    topOffset - model.mIcon.getHeight() * TITLE_MARGIN_SCALE_FRACTION;

            // Translate icon to model center
            model.mIconMatrix.setTranslate(
                    leftOffset,
                    (mIsTitled && mTitleMode == TitleMode.ALL) ? titleTranslate : topOffset
            );

            // Get interpolated fraction for left last and current models
            final float interpolation =
                    mResizeInterpolator.getResizeInterpolation(mFraction, true);
            final float lastInterpolation =
                    mResizeInterpolator.getResizeInterpolation(mFraction, false);

            // Scale value relative to interpolation
            final float matrixScale = model.mActiveIconScaleBy * interpolation;
            final float matrixLastScale = model.mActiveIconScaleBy * lastInterpolation;

            // Get title alpha relative to interpolation
            final int titleAlpha = (int) (MAX_ALPHA * interpolation);
            final int titleLastAlpha = MAX_ALPHA - (int) (MAX_ALPHA * lastInterpolation);
            // Get title scale relative to interpolation
            final float titleScale = mIsScaled ?
                    MAX_FRACTION + interpolation * TITLE_ACTIVE_SCALE_BY : MAX_FRACTION;
            final float titleLastScale = mIsScaled ? (MAX_FRACTION + TITLE_ACTIVE_SCALE_BY) -
                    (lastInterpolation * TITLE_ACTIVE_SCALE_BY) : titleScale;

            mIconPaint.setAlpha(MAX_ALPHA);
            if (model.mSelectedIcon != null) mSelectedIconPaint.setAlpha(MAX_ALPHA);

            // Check if we handle models from touch on NTB or from ViewPager
            // There is a strange logic
            // of ViewPager onPageScrolled method, so it is
            if (mIsSetIndexFromTabBar) {
                if (mIndex == i)
                    updateCurrentModel(
                            model,
                            leftOffset,
                            topOffset,
                            titleTranslate,
                            interpolation,
                            matrixCenterX,
                            matrixCenterY,
                            matrixScale,
                            titleScale,
                            titleAlpha
                    );
                else if (mLastIndex == i)
                    updateLastModel(
                            model,
                            leftOffset,
                            topOffset,
                            titleTranslate,
                            lastInterpolation,
                            matrixCenterX,
                            matrixCenterY,
                            matrixLastScale,
                            titleLastScale,
                            titleLastAlpha
                    );
                else
                    updateInactiveModel(
                            model,
                            leftOffset,
                            topOffset,
                            titleScale,
                            matrixScale,
                            matrixCenterX,
                            matrixCenterY
                    );
            } else {
                if (i == mIndex + 1)
                    updateCurrentModel(
                            model,
                            leftOffset,
                            topOffset,
                            titleTranslate,
                            interpolation,
                            matrixCenterX,
                            matrixCenterY,
                            matrixScale,
                            titleScale,
                            titleAlpha
                    );
                else if (i == mIndex)
                    updateLastModel(
                            model,
                            leftOffset,
                            topOffset,
                            titleTranslate,
                            lastInterpolation,
                            matrixCenterX,
                            matrixCenterY,
                            matrixLastScale,
                            titleLastScale,
                            titleLastAlpha
                    );
                else updateInactiveModel(
                            model,
                            leftOffset,
                            topOffset,
                            titleScale,
                            matrixScale,
                            matrixCenterX,
                            matrixCenterY
                    );
            }

            // Draw original model icon
            if (model.mSelectedIcon == null) {
                if (model.mIcon != null && !model.mIcon.isRecycled())
                    mIconsCanvas.drawBitmap(model.mIcon, model.mIconMatrix, mIconPaint);
            } else if (mIconPaint.getAlpha() != MIN_ALPHA &&
                    model.mIcon != null && !model.mIcon.isRecycled())
                // Draw original icon when is visible
                mIconsCanvas.drawBitmap(model.mIcon, model.mIconMatrix, mIconPaint);
            // Draw selected icon when exist and visible
            if (mSelectedIconPaint.getAlpha() != MIN_ALPHA &&
                    model.mSelectedIcon != null && !model.mSelectedIcon.isRecycled())
                mIconsCanvas.drawBitmap(model.mSelectedIcon, model.mIconMatrix, mSelectedIconPaint);
            if (mIsTitled) mTitlesCanvas.drawText(
                    isInEditMode() ? PREVIEW_TITLE : model.getTitle(),
                    leftTitleOffset,
                    topTitleOffset,
                    mModelTitlePaint
            );
        }

        // Reset pointer bounds for icons and titles
        if (mIsHorizontalOrientation)
            mPointerBounds.set(mPointerLeftTop, 0.0F, mPointerRightBottom, mBounds.height());
        if (mCornersRadius == 0) {
            if (mIsTinted) mIconsCanvas.drawRect(mPointerBounds, mIconPointerPaint);
            if (mIsTitled) mTitlesCanvas.drawRect(mPointerBounds, mIconPointerPaint);
        } else {
            if (mIsTinted) mIconsCanvas.drawRoundRect(
                    mPointerBounds, mCornersRadius, mCornersRadius, mIconPointerPaint
            );
            if (mIsTitled) mTitlesCanvas.drawRoundRect(
                    mPointerBounds, mCornersRadius, mCornersRadius, mIconPointerPaint
            );
        }

        // Draw general bitmap
        canvas.drawBitmap(mBitmap, 0.0F, 0.0F, null);
        // Draw icons bitmap on top
        canvas.drawBitmap(mIconsBitmap, 0.0F, barBadgeMargin, null);
        // Draw titles bitmap on top
        if (mIsTitled) canvas.drawBitmap(mTitlesBitmap, 0.0F, barBadgeMargin, null);

        // If is not badged, exit
        if (!mIsBadged) return;

        // Model badge margin and offset relative to gravity mode
        final float modelBadgeMargin =
                mBadgeGravity == BadgeGravity.TOP ? mBadgeMargin : mBounds.height();
        final float modelBadgeOffset =
                mBadgeGravity == BadgeGravity.TOP ? 0.0F : mBounds.height() - mBadgeMargin;

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
            final float badgeMargin = mBadgeMargin * model.mBadgeFraction;
            if (model.getBadgeTitle().length() == 1) {
                mBgBadgeBounds.set(
                        badgeBoundsHorizontalOffset - badgeMargin,
                        modelBadgeMargin - badgeMargin,
                        badgeBoundsHorizontalOffset + badgeMargin,
                        modelBadgeMargin + badgeMargin
                );
            } else
                mBgBadgeBounds.set(
                        badgeBoundsHorizontalOffset -
                                Math.max(badgeMargin, mBadgeBounds.centerX() + horizontalPadding),
                        modelBadgeMargin - badgeMargin,
                        badgeBoundsHorizontalOffset +
                                Math.max(badgeMargin, mBadgeBounds.centerX() + horizontalPadding),
                        modelBadgeOffset + (verticalPadding * 2.0F) + mBadgeBounds.height()
                );

            // Set color and alpha for badge bg
            if (model.mBadgeFraction == MIN_FRACTION) mBadgePaint.setColor(Color.TRANSPARENT);
            else mBadgePaint.setColor(mBadgeBgColor == AUTO_COLOR ? mActiveColor : mBadgeBgColor);
            mBadgePaint.setAlpha((int) (MAX_ALPHA * model.mBadgeFraction));

            // Set corners to round rect for badge bg and draw
            final float cornerRadius = mBgBadgeBounds.height() * 0.5F;
            canvas.drawRoundRect(mBgBadgeBounds, cornerRadius, cornerRadius, mBadgePaint);

            // Set color and alpha for badge title
            if (model.mBadgeFraction == MIN_FRACTION) mBadgePaint.setColor(Color.TRANSPARENT);
            else //noinspection ResourceAsColor
                mBadgePaint.setColor(
                        mBadgeTitleColor == AUTO_COLOR ? model.getColor() : mBadgeTitleColor
                );
            mBadgePaint.setAlpha((int) (MAX_ALPHA * model.mBadgeFraction));

            // Set badge title center position and draw title
            final float badgeHalfHeight = mBadgeBounds.height() * 0.5F;
            float badgeVerticalOffset = (mBgBadgeBounds.height() * 0.5F) + badgeHalfHeight -
                    mBadgeBounds.bottom + modelBadgeOffset;
            canvas.drawText(
                    model.getBadgeTitle(), badgeBoundsHorizontalOffset, badgeVerticalOffset +
                            mBadgeBounds.height() - (mBadgeBounds.height() * model.mBadgeFraction),
                    mBadgePaint
            );
        }
    }

    // Method to transform current fraction of NTB and position
    protected void updateCurrentModel(
            final Model model,
            final float leftOffset,
            final float topOffset,
            final float titleTranslate,
            final float interpolation,
            final float matrixCenterX,
            final float matrixCenterY,
            final float matrixScale,
            final float titleScale,
            final int titleAlpha
    ) {
        if (mIsTitled && mTitleMode == TitleMode.ACTIVE) model.mIconMatrix.setTranslate(
                leftOffset, topOffset - (interpolation * (topOffset - titleTranslate))
        );

        final float scale = model.mInactiveIconScale + (mIsScaled ? matrixScale : 0.0F);
        model.mIconMatrix.postScale(scale, scale, matrixCenterX, matrixCenterY);

        mModelTitlePaint.setTextSize(mModelTitleSize * titleScale);
        if (mTitleMode == TitleMode.ACTIVE) mModelTitlePaint.setAlpha(titleAlpha);

        if (model.mSelectedIcon == null) {
            mIconPaint.setAlpha(MAX_ALPHA);
            return;
        }

        // Calculate cross fade alpha between icon and selected icon
        final float iconAlpha;
        final float selectedIconAlpha;
        if (interpolation <= 0.475F) {
            iconAlpha = MAX_FRACTION - interpolation * 2.1F;
            selectedIconAlpha = MIN_FRACTION;
        } else if (interpolation >= 0.525F) {
            iconAlpha = MIN_FRACTION;
            selectedIconAlpha = (interpolation - 0.55F) * 1.9F;
        } else {
            iconAlpha = MIN_FRACTION;
            selectedIconAlpha = MIN_FRACTION;
        }

        mIconPaint.setAlpha((int) (MAX_ALPHA * clampValue(iconAlpha)));
        mSelectedIconPaint.setAlpha((int) (MAX_ALPHA * clampValue(selectedIconAlpha)));
    }

    // Method to transform last fraction of NTB and position
    protected void updateLastModel(
            final Model model,
            final float leftOffset,
            final float topOffset,
            final float titleTranslate,
            final float lastInterpolation,
            final float matrixCenterX,
            final float matrixCenterY,
            final float matrixLastScale,
            final float titleLastScale,
            final int titleLastAlpha
    ) {
        if (mIsTitled && mTitleMode == TitleMode.ACTIVE) model.mIconMatrix.setTranslate(
                leftOffset, titleTranslate + (lastInterpolation * (topOffset - titleTranslate))
        );

        final float scale = model.mInactiveIconScale +
                (mIsScaled ? model.mActiveIconScaleBy - matrixLastScale : 0.0F);
        model.mIconMatrix.postScale(scale, scale, matrixCenterX, matrixCenterY);

        mModelTitlePaint.setTextSize(mModelTitleSize * titleLastScale);
        if (mTitleMode == TitleMode.ACTIVE) mModelTitlePaint.setAlpha(titleLastAlpha);

        if (model.mSelectedIcon == null) {
            mIconPaint.setAlpha(MAX_ALPHA);
            return;
        }

        // Calculate cross fade alpha between icon and selected icon
        final float iconAlpha;
        final float selectedIconAlpha;
        if (lastInterpolation <= 0.475F) {
            iconAlpha = MIN_FRACTION;
            selectedIconAlpha = MAX_FRACTION - lastInterpolation * 2.1F;
        } else if (lastInterpolation >= 0.525F) {
            iconAlpha = (lastInterpolation - 0.55F) * 1.9F;
            selectedIconAlpha = MIN_FRACTION;
        } else {
            iconAlpha = MIN_FRACTION;
            selectedIconAlpha = MIN_FRACTION;
        }

        mIconPaint.setAlpha((int) (MAX_ALPHA * clampValue(iconAlpha)));
        mSelectedIconPaint.setAlpha((int) (MAX_ALPHA * clampValue(selectedIconAlpha)));
    }

    // Method to transform others fraction of NTB and position
    protected void updateInactiveModel(
            final Model model,
            final float leftOffset,
            final float topOffset,
            final float textScale,
            final float matrixScale,
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

        // Reset icons alpha
        if (model.mSelectedIcon == null) {
            mIconPaint.setAlpha(MAX_ALPHA);
            return;
        }

        mSelectedIconPaint.setAlpha(MIN_ALPHA);
    }

    protected void updateTint() {
        if (mIsTinted) {
            // Set color filter to wrap icons with inactive color
            final PorterDuffColorFilter colorFilter =
                    new PorterDuffColorFilter(mInactiveColor, PorterDuff.Mode.SRC_IN);
            mIconPaint.setColorFilter(colorFilter);
            mSelectedIconPaint.setColorFilter(colorFilter);
        } else {
            // Reset active and inactive colors
            mIconPaint.reset();
            mSelectedIconPaint.reset();
        }

        postInvalidate();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, final int positionOffsetPixels) {
        if (mOnPageChangeListener != null)
            mOnPageChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);

        // If we animate, don`t call this
        if (!mIsSetIndexFromTabBar) {
            mIsResizeIn = position < mIndex;
            mLastIndex = mIndex;
            mIndex = position;

            mStartPointerX = position * mModelSize;
            mEndPointerX = mStartPointerX + mModelSize;
            updateIndicatorPosition(positionOffset);
        }

        // Stop scrolling on animation end and reset values
        if (!mAnimator.isRunning() && mIsSetIndexFromTabBar) {
            mFraction = MIN_FRACTION;
            mIsSetIndexFromTabBar = false;
        }
    }

    @Override
    public void onPageSelected(final int position) {
        // This method is empty, because we call onPageSelected() when scroll state is idle
    }

    @Override
    public void onPageScrollStateChanged(final int state) {
        // If VP idle, reset to MIN_FRACTION
        mScrollState = state;
        if (state == ViewPager.SCROLL_STATE_IDLE) {
            if (mOnPageChangeListener != null) mOnPageChangeListener.onPageSelected(mIndex);
            if (mIsViewPagerMode && mOnTabBarSelectedIndexListener != null)
                mOnTabBarSelectedIndexListener.onEndTabSelected(mModels.get(mIndex), mIndex);
        }

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

    protected static class SavedState extends BaseSavedState {

        private int index;

        SavedState(Parcelable superState) {
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

    @Override
    protected void onConfigurationChanged(final Configuration newConfig) {
        // Config view on rotate etc.
        super.onConfigurationChanged(newConfig);
        requestLayout();

        // Refresh pointer and state after config changed to current
        final int tempIndex = mIndex;
        deselect();
        post(new Runnable() {
            @Override
            public void run() {
                setModelIndex(tempIndex, true);
            }
        });
    }

    // Clamp value to max and min bounds
    protected float clampValue(final float value) {
        return Math.max(
                Math.min(value, NavigationTabBar.MAX_FRACTION), NavigationTabBar.MIN_FRACTION
        );
    }

    // Hide NTB with animation
    public void hide() {
        if (mBehavior != null) mBehavior.hideView(this, (int) getBarHeight(), true);
        else if (getParent() != null && getParent() instanceof CoordinatorLayout) {
            mNeedHide = true;
            mAnimateHide = true;
        } else scrollDown();
    }

    // Show NTB with animation
    public void show() {
        if (mBehavior != null) mBehavior.resetOffset(this, true);
        else scrollUp();
    }

    // Hide NTB or bg on scroll down
    protected void scrollDown() {
        ViewCompat.animate(this)
                .translationY(getBarHeight())
                .setInterpolator(new LinearOutSlowInInterpolator())
                .setDuration(DEFAULT_ANIMATION_DURATION)
                .start();
    }

    // Show NTB or bg on scroll up
    protected void scrollUp() {
        ViewCompat.animate(this)
                .translationY(0.0F)
                .setInterpolator(OUT_SLOW_IN_INTERPOLATOR)
                .setDuration(DEFAULT_ANIMATION_DURATION)
                .start();
    }

    // Model class
    public static class Model {

        private int mColor;

        private final Bitmap mIcon;
        private final Bitmap mSelectedIcon;
        private final Matrix mIconMatrix = new Matrix();

        private String mTitle = "";
        private String mBadgeTitle = "";
        private String mTempBadgeTitle = "";
        private float mBadgeFraction;

        private boolean mIsBadgeShowed;
        private boolean mIsBadgeUpdated;

        private final ValueAnimator mBadgeAnimator = new ValueAnimator();

        private float mInactiveIconScale;
        private float mActiveIconScaleBy;

        Model(final Builder builder) {
            mColor = builder.mColor;
            mIcon = builder.mIcon;
            mSelectedIcon = builder.mSelectedIcon;
            mTitle = builder.mTitle;
            mBadgeTitle = builder.mBadgeTitle;

            mBadgeAnimator.addListener(new AnimatorListenerAdapter() {

                @Override
                public void onAnimationStart(final Animator animation) {
                    animation.removeListener(this);
                    animation.addListener(this);
                }

                @Override
                public void onAnimationEnd(final Animator animation) {
                    animation.removeListener(this);
                    animation.addListener(this);

                    // Detect whether we just update text and don`t reset show state
                    if (!mIsBadgeUpdated) mIsBadgeShowed = !mIsBadgeShowed;
                    else mIsBadgeUpdated = false;
                }

                @Override
                public void onAnimationRepeat(final Animator animation) {
                    // Change title when we update and don`t see the title
                    if (mIsBadgeUpdated) mBadgeTitle = mTempBadgeTitle;
                }
            });
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

        public static class Builder {

            private final int mColor;

            private final Bitmap mIcon;
            private Bitmap mSelectedIcon;

            private String mTitle;
            private String mBadgeTitle;

            public Builder(final Drawable icon, final int color) {
                mColor = color;

                if (icon != null) {
                    if (icon instanceof BitmapDrawable) mIcon = ((BitmapDrawable) icon).getBitmap();
                    else {
                        mIcon = Bitmap.createBitmap(
                                icon.getIntrinsicWidth(),
                                icon.getIntrinsicHeight(),
                                Bitmap.Config.ARGB_8888
                        );
                        final Canvas canvas = new Canvas(mIcon);
                        icon.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                        icon.draw(canvas);
                    }
                } else {
                    mIcon = Bitmap.createBitmap(1, 1, Bitmap.Config.RGB_565);
                }
            }

            public Builder selectedIcon(final Drawable selectedIcon) {
                if (selectedIcon != null) {
                    if (selectedIcon instanceof BitmapDrawable)
                        mSelectedIcon = ((BitmapDrawable) selectedIcon).getBitmap();
                    else {
                        mSelectedIcon = Bitmap.createBitmap(
                                selectedIcon.getIntrinsicWidth(),
                                selectedIcon.getIntrinsicHeight(),
                                Bitmap.Config.ARGB_8888
                        );
                        final Canvas canvas = new Canvas(mSelectedIcon);
                        selectedIcon.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                        selectedIcon.draw(canvas);
                    }
                } else mSelectedIcon = null;

                return this;
            }

            public Builder title(final String title) {
                mTitle = title;
                return this;
            }

            public Builder badgeTitle(final String title) {
                mBadgeTitle = title;
                return this;
            }

            public Model build() {
                return new Model(this);
            }
        }
    }

    // Custom scroller with custom scroll duration
    protected class ResizeViewPagerScroller extends Scroller {

        ResizeViewPagerScroller(Context context) {
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
    protected class ResizeInterpolator implements Interpolator {

        // Spring factor
        private final static float FACTOR = 1.0F;
        // Check whether side we move
        private boolean mResizeIn;

        @Override
        public float getInterpolation(final float input) {
            if (mResizeIn) return (float) (1.0F - Math.pow((1.0F - input), 2.0F * FACTOR));
            else return (float) (Math.pow(input, 2.0F * FACTOR));
        }

        private float getResizeInterpolation(final float input, final boolean resizeIn) {
            mResizeIn = resizeIn;
            return getInterpolation(input);
        }
    }

    // Model title mode
    public enum TitleMode {
        ALL, ACTIVE;

        public final static int ALL_INDEX = 0;
        public final static int ACTIVE_INDEX = 1;
    }

    // Model badge position
    public enum BadgePosition {
        LEFT(LEFT_FRACTION), CENTER(CENTER_FRACTION), RIGHT(RIGHT_FRACTION);

        public final static int LEFT_INDEX = 0;
        public final static int CENTER_INDEX = 1;
        public final static int RIGHT_INDEX = 2;

        private final float mPositionFraction;

        BadgePosition(final float positionFraction) {
            mPositionFraction = positionFraction;
        }
    }

    // Model badge gravity
    public enum BadgeGravity {
        TOP, BOTTOM;

        public final static int TOP_INDEX = 0;
        public final static int BOTTOM_INDEX = 1;
    }

    // Out listener for selected index
    public interface OnTabBarSelectedIndexListener {
        void onStartTabSelected(final Model model, final int index);

        void onEndTabSelected(final Model model, final int index);
    }
}
