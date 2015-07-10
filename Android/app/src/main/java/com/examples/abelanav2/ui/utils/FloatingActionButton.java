/*
 * Copyright 2014, The Android Open Source Project
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
/*
 * Copyright 2015 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.examples.abelanav2.ui.utils;

import android.content.Context;
import android.graphics.Outline;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.Checkable;
import android.widget.FrameLayout;

/**
 * A Floating Action Button is a {@link android.widget.Checkable} view
 * distinguished by a circled icon floating above the UI, with special
 * motion behaviors.
 */
public class FloatingActionButton extends FrameLayout implements Checkable {

    /**
     * An array of states.
     */
    private static final int[] CHECKED_STATE_SET = {
            android.R.attr.state_checked
    };
    /**
     * A boolean that tells if the FAB is checked or not.
     */
    private boolean mChecked;
    /**
     * A listener to communicate that the FAB has changed its state.
     */
    private OnCheckedChangeListener mOnCheckedChangeListener;

    /**
     * Constructor.
     * @param context the application context.
     */
    public FloatingActionButton(Context context) {
        this(context, null, 0, 0);
    }

    /**
     * Constructor.
     * @param context the application context.
     * @param attrs attributes.
     */
    public FloatingActionButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0, 0);
    }

    /**
     * Constructor.
     * @param context the application context.
     * @param attrs attributes.
     * @param defStyleAttr attribute in the current theme that contains a
     *                     reference to a style resource that supplies defaults
     *                     values for the StyledAttributes
     */
    public FloatingActionButton(Context context, AttributeSet attrs,
                                int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    /**
     * Constructor.
     * @param context the application context.
     * @param attrs attributes.
     * @param defStyleAttr attribute in the current theme that contains a
     *                     reference to a style resource that supplies defaults
     *                     values for the StyledAttributes
     * @param defStyleRes a resource identifier of a style resource that
     *                    supplies default values for the StyledAttributes,
     *                    used only if defStyleAttr is 0 or can not be found
     *                    in the theme
     */
    public FloatingActionButton(Context context, AttributeSet attrs,
                                int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr);

        setClickable(true);

        // Set the outline provider for this view. The provider is given the
        // outline which it can then modify as needed. In this case we set the
        // outline to be an oval fitting the height and width.
        setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setOval(0, 0, getWidth(), getHeight());
            }
        });

        // Finally, enable clipping to the outline, using the provider we set
        // above
        setClipToOutline(true);
    }

    /**
     * Register a callback to be invoked when the checked state of this button
     * changes.
     * @param listener the callback to call on checked state change
     */
    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        mOnCheckedChangeListener = listener;
    }

    @Override
    public boolean isChecked() {
        return mChecked;
    }

    /**
     * Sets the checked/unchecked state of the FAB.
     * @param checked the state of the button.
     */
    public void setChecked(boolean checked) {
        // If trying to set the current state, ignore.
        if (checked == mChecked) {
            return;
        }
        mChecked = checked;

        // Now refresh the drawable state (so the icon changes)
        refreshDrawableState();

        if (mOnCheckedChangeListener != null) {
            mOnCheckedChangeListener.onCheckedChanged(this, checked);
        }
    }

    @Override
    public void toggle() {
        setChecked(!mChecked);
    }

    /**
     * Override performClick() so that we can toggle the checked state when the
     * view is clicked.
     */
    @Override
    public boolean performClick() {
        toggle();
        return super.performClick();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // As we have changed size, we should invalidate the outline so that is
        // the the correct size.
        invalidateOutline();
    }

    @Override
    protected int[] onCreateDrawableState(int extraSpace) {
        final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (isChecked()) {
            mergeDrawableStates(drawableState, CHECKED_STATE_SET);
        }
        return drawableState;
    }

    /**
     * Interface definition for a callback to be invoked when the checked state
     * of a compound button changes.
     */
    public interface OnCheckedChangeListener {

        /**
         * Called when the checked state of a FAB has changed.
         * @param fabView   The FAB view whose state has changed.
         * @param isChecked The new checked state of buttonView.
         */
        void onCheckedChanged(FloatingActionButton fabView, boolean isChecked);
    }
}
