/*
 *  Copyright (c) 2016 Touch Instinct
 *
 *  This file is part of RoboSwag library.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package ru.touchin.templates;

import android.app.ActivityManager;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;

import ru.touchin.roboswag.components.navigation.activities.ViewControllerActivity;
import ru.touchin.roboswag.components.utils.Logic;
import ru.touchin.roboswag.components.utils.UiUtils;

/**
 * Created by Gavriil Sitnikov on 11/03/16.
 * TODO: description
 */
public abstract class TouchinActivity<TLogic extends Logic> extends ViewControllerActivity<TLogic> {

    @Override
    protected void onPostCreate(@Nullable final Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        onConfigureActivityScreen(savedInstanceState);
    }

    private void onConfigureActivityScreen(@Nullable final Bundle savedInstanceState) {
        if (isActivityUnderSystemBars() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (UiUtils.hasSoftKeys(this)) {
                getWindow().getDecorView()
                        .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
            } else {
                getWindow().getDecorView()
                        .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            }

            configureActivityPaddings(savedInstanceState, UiUtils.getStatusBarHeight(this), UiUtils.getNavigationBarHeight(this));
        }
    }

    /**
     * paddings are used to configure an activity size. By default, {@code #topPadding} is statusBar height and {@code #bottomPadding}
     * is NavigationBar height
     */
    protected void configureActivityPaddings(@Nullable final Bundle savedInstanceState,
                                             final int suggestedTopPadding, final int suggestedBottomPadding) {
        // do nothing
    }

    protected void setupTaskDescriptor(@NonNull final String label, @DrawableRes final int iconRes, @ColorRes final int primaryColorRes) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            final ActivityManager.TaskDescription taskDescription = new ActivityManager.TaskDescription(label,
                    ((BitmapDrawable) ContextCompat.getDrawable(this, iconRes)).getBitmap(),
                    ContextCompat.getColor(this, primaryColorRes));
            setTaskDescription(taskDescription);
        }
    }

    public boolean isActivityUnderSystemBars() {
        return false;
    }
}
