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

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.multidex.MultiDex;

import com.crashlytics.android.Crashlytics;

import net.danlew.android.joda.JodaTimeAndroid;

import io.fabric.sdk.android.Fabric;
import ru.touchin.roboswag.components.views.TypefacedViewHelper;
import ru.touchin.roboswag.core.log.ConsoleLogProcessor;
import ru.touchin.roboswag.core.log.Lc;
import ru.touchin.roboswag.core.log.LcGroup;
import ru.touchin.roboswag.core.log.LcLevel;
import ru.touchin.roboswag.core.log.LogProcessor;
import ru.touchin.roboswag.core.utils.ShouldNotHappenException;

/**
 * Created by Gavriil Sitnikov on 10/03/16.
 * TODO: description
 */
public abstract class TouchinApp extends Application {

    protected abstract boolean isDebug();

    @Override
    protected void attachBaseContext(@NonNull final Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        JodaTimeAndroid.init(this);
        if (isDebug()) {
            TypefacedViewHelper.setAllowEmptyCustomTypeface(false);
            Lc.initialize(new ConsoleLogProcessor(LcLevel.VERBOSE), true);
        } else {
            TypefacedViewHelper.setAllowEmptyCustomTypeface(true);
            final Crashlytics crashlytics = new Crashlytics();
            Fabric.with(this, crashlytics);
            Lc.initialize(new CrashlyticsLogProcessor(crashlytics), false);
        }
    }

    private static class CrashlyticsLogProcessor extends LogProcessor {

        @NonNull
        private final Crashlytics crashlytics;

        public CrashlyticsLogProcessor(@NonNull final Crashlytics crashlytics) {
            super(LcLevel.ASSERT);
            this.crashlytics = crashlytics;
        }

        @Override
        public void processLogMessage(@NonNull final LcGroup group,
                                      @NonNull final LcLevel level,
                                      @NonNull final String tag,
                                      @NonNull final String message,
                                      @Nullable final Throwable throwable) {
            if (!level.lessThan(LcLevel.ASSERT)) {
                if (throwable != null) {
                    crashlytics.core.log(tag + ':' + message);
                    crashlytics.core.logException(throwable);
                } else {
                    crashlytics.core.logException(new ShouldNotHappenException(tag + ':' + message));
                }
            }
        }

    }

}
