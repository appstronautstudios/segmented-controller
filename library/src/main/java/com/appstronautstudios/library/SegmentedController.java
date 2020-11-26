package com.appstronautstudios.library;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.StateSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableRow;

public class SegmentedController extends RadioGroup implements RadioGroup.OnCheckedChangeListener {

    private int mStrokeWidth;
    private float mCornerRadius;
    private int mTintColourChecked;
    private int mTintColourUnchecked;
    private OnCheckedChangeListener mCheckListener;
    private int lastCheckedId;

    public SegmentedController(Context context) {
        super(context);
    }

    public SegmentedController(Context context, AttributeSet attrs) {
        super(context, attrs);

        initAttrs(attrs);
    }

    /* Reads the attributes from the layout */
    private void initAttrs(AttributeSet attrs) {
        TypedArray typedArray = getContext().getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.SegmentedController,
                0, 0);

        try {
            mStrokeWidth = (int) typedArray.getDimension(
                    R.styleable.SegmentedController_sc_border_width,
                    getResources().getDimension(R.dimen.border_stroke_width));
            mCornerRadius = typedArray.getDimension(
                    R.styleable.SegmentedController_sc_corner_radius,
                    getResources().getDimension(R.dimen.corner_radius));
            mTintColourChecked = typedArray.getColor(
                    R.styleable.SegmentedController_sc_tint_color_selected,
                    getResources().getColor(R.color.radio_button_selected_color));
            mTintColourUnchecked = typedArray.getColor(
                    R.styleable.SegmentedController_sc_tint_color_unselected,
                    getResources().getColor(R.color.radio_button_unselected_color));
        } finally {
            typedArray.recycle();
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        Log.d("SC", "onFinishInflate");

        configure();
    }

    @Override
    public void onViewRemoved(View child) {
        super.onViewRemoved(child);

        Log.d("SC", "onViewRemoved");
    }

    @Override
    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        super.setOnCheckedChangeListener(this);

        Log.d("SC", "setOnCheckedChangeListener");

        mCheckListener = listener;
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        Log.d("SC", "setOnCheckedChangeListener" + " checkedId: " + checkedId + " lastId: " + lastCheckedId);

        // get the transition drawables for the currently selected and previously selected views
        TransitionDrawable current = ((TransitionDrawable) group.findViewById(checkedId).getTag());

        // reverse the current selection
        current.reverseTransition(0);

        // if there was a previous selection, reverse that drawable too
        if (lastCheckedId != 0) {
            TransitionDrawable previous = ((TransitionDrawable) group.findViewById(lastCheckedId).getTag());
            if (previous != null) {
                previous.reverseTransition(0);
            }
        }

        // now that the state transition is done update the last checked id. We'll need this to
        // transition on buttons to off when user has a previous selection
        lastCheckedId = checkedId;

        // pass the event to the client
        if (mCheckListener != null) {
            mCheckListener.onCheckedChanged(group, checkedId);
        }
    }

    /**
     * core configuration function. Sets button appearance via gravity, layout params and state
     * list drawable. Store the transition drawable as a tag so it can be flipped in response to
     * selection
     */
    private void configure() {
        // set orientation
        super.setOrientation(HORIZONTAL);

        // reset radii
        float r = mCornerRadius;
        float r1 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0.1f, getResources().getDisplayMetrics());
        float[] rLeft = new float[]{r, r, r1, r1, r1, r1, r, r};
        float[] rRight = new float[]{r1, r1, r, r, r, r, r1, r1};
        float[] rMiddle = new float[]{r1, r1, r1, r1, r1, r1, r1, r1};

        // set child styles
        int childCount = super.getChildCount();
        for (int i = 0; i < childCount; i++) {
            RadioButton child = (RadioButton) super.getChildAt(i);
            Drawable[] generatedDrawables;
            TableRow.LayoutParams params = new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
            if (i == 0) {
                // left
                generatedDrawables = generateDrawableGroup(child.isChecked(), rLeft);
                params.setMargins(0, 0, -mStrokeWidth, 0); // fix border overlap
            } else if (i == childCount - 1) {
                // right
                generatedDrawables = generateDrawableGroup(child.isChecked(), rRight);
            } else {
                // middle
                generatedDrawables = generateDrawableGroup(child.isChecked(), rMiddle);
                params.setMargins(0, 0, -mStrokeWidth, 0); // fix border overlap
            }

            child.setButtonDrawable(null);
            child.setGravity(Gravity.CENTER);
            child.setBackground(generatedDrawables[1]);
            child.setTag(generatedDrawables[0]);
            child.setLayoutParams(params);
        }
    }

    /**
     * @param checked are we generating a drawable group for a checked or unchecked button
     * @param radii   the radii matrix for this specific button. See constants at top
     * @return a drawable array containing the transition drawable and state list drawable
     */
    private Drawable[] generateDrawableGroup(boolean checked, float[] radii) {
        GradientDrawable uncheckedDrawable = new OptionDrawable(mTintColourUnchecked, mStrokeWidth, radii);
        GradientDrawable checkedDrawable = new OptionDrawable(mTintColourChecked, mStrokeWidth, radii);
        GradientDrawable maskDrawable = new OptionDrawable(Color.argb(50, Color.red(mTintColourChecked), Color.green(mTintColourChecked), Color.blue(mTintColourChecked)), mStrokeWidth, radii);
        LayerDrawable pressedDrawable = new LayerDrawable(new Drawable[]{uncheckedDrawable, maskDrawable});
        TransitionDrawable transitionDrawable = new TransitionDrawable(new Drawable[]{uncheckedDrawable, checkedDrawable});
        if (checked) {
            transitionDrawable.reverseTransition(0);
        }
        StateListDrawable stateListDrawable = new StateListDrawable();
        // adds an animation for pressed state but looks terrible
//        stateListDrawable.addState(new int[]{-android.R.attr.state_checked, android.R.attr.state_pressed}, pressedDrawable);
        stateListDrawable.addState(StateSet.WILD_CARD, transitionDrawable);
        return new Drawable[]{transitionDrawable, stateListDrawable};
    }

    /**
     * helper class to generate a gradient drawable with our desired config
     */
    private class OptionDrawable extends GradientDrawable {

        OptionDrawable(int color, int strokeWidth, float[] cornerRadii) {
            super();
            setStroke(strokeWidth, mTintColourChecked);
            setCornerRadii(cornerRadii);
            setColor(color);
        }
    }
}
