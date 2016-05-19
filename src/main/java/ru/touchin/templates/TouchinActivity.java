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

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.view.View;

import ru.touchin.roboswag.components.navigation.activities.ViewControllerActivity;
import ru.touchin.roboswag.components.utils.Logic;
import ru.touchin.roboswag.components.utils.UiUtils;

/**
 * Created by Gavriil Sitnikov on 11/03/16.
 * TODO: description
 */
public abstract class TouchinActivity<TLogic extends Logic> extends ViewControllerActivity<TLogic> {

    protected TouchinActivity() {
        super();
    }

    @Override
    protected void onPostCreate(@Nullable final Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        makeActivityFullScreen();
    }

    private void makeActivityFullScreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && (isActivityUnderNavigationBar() || isActivityUnderStatusBar())) {
            final View activityView = findViewById(getContainerId());
            int topPadding = activityView.getPaddingTop();
            int bottomPadding = activityView.getPaddingBottom();
            if (isActivityUnderStatusBar()) {
                topPadding += UiUtils.getStatusBarHeight(this);
            }
            if (UiUtils.hasSoftKeys(this)) {
                getWindow().getDecorView()
                        .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
                if (isActivityUnderNavigationBar()) {
                    bottomPadding += UiUtils.getNavigationBarHeight(this);
                }
            } else {
                getWindow().getDecorView()
                        .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            }
            activityView.setPadding(activityView.getPaddingLeft(),
                    topPadding,
                    activityView.getPaddingRight(),
                    bottomPadding);

        }
    }

    protected boolean isActivityUnderStatusBar() {
        return false;
    }

    protected boolean isActivityUnderNavigationBar() {
        return false;
    }

    @IdRes
    protected abstract int getContainerId();

}
