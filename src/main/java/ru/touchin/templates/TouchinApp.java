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
import android.support.multidex.MultiDex;
import android.util.Log;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;
import ru.touchin.roboswag.components.views.TypefacedViewHelper;
import ru.touchin.roboswag.core.log.ConsoleLogProcessor;
import ru.touchin.roboswag.core.log.LcHelper;
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
        if (isDebug()) {
            TypefacedViewHelper.setAllowEmptyCustomTypeface(false);

            LcHelper.setCrashOnAssertions(true);
            LcHelper.initialize(Log.VERBOSE);
        } else {
            TypefacedViewHelper.setAllowEmptyCustomTypeface(true);

            LcHelper.setCrashOnAssertions(false);
            final Crashlytics crashlytics = new Crashlytics();
            Fabric.with(this, crashlytics);
            LcHelper.initialize(Log.ERROR, new CrashlyticsLogProcessor(crashlytics));
        }
    }

    private static class CrashlyticsLogProcessor extends ConsoleLogProcessor {

        @NonNull
        private final Crashlytics crashlytics;

        public CrashlyticsLogProcessor(@NonNull final Crashlytics crashlytics) {
            super();
            this.crashlytics = crashlytics;
        }

        @Override
        public void processLogMessage(final int logLevel, @NonNull final String tag, @NonNull final String message) {
            super.processLogMessage(logLevel, tag, message);
            if (logLevel >= Log.ASSERT) {
                crashlytics.core.logException(new ShouldNotHappenException(tag + ':' + message));
            }
        }

        @Override
        public void processLogMessage(final int logLevel, @NonNull final String tag, @NonNull final String message, @NonNull final Throwable ex) {
            super.processLogMessage(logLevel, tag, message, ex);
            if (logLevel >= Log.ASSERT) {
                crashlytics.core.log(tag + ':' + message);
                crashlytics.core.logException(ex);
            }
        }

    }

}
