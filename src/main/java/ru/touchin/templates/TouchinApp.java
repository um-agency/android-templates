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
import android.os.Looper;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.multidex.MultiDex;

import com.crashlytics.android.Crashlytics;

import net.danlew.android.joda.JodaTimeAndroid;

import java.util.concurrent.TimeUnit;

import io.fabric.sdk.android.Fabric;
import ru.touchin.roboswag.components.navigation.fragments.ViewControllerFragment;
import ru.touchin.roboswag.components.views.TypefacedEditText;
import ru.touchin.roboswag.components.views.TypefacedTextView;
import ru.touchin.roboswag.core.log.ConsoleLogProcessor;
import ru.touchin.roboswag.core.log.Lc;
import ru.touchin.roboswag.core.log.LcGroup;
import ru.touchin.roboswag.core.log.LcLevel;
import ru.touchin.roboswag.core.log.LogProcessor;
import ru.touchin.roboswag.core.utils.ShouldNotHappenException;
import rx.Scheduler;
import rx.Subscription;
import rx.android.plugins.RxAndroidPlugins;
import rx.android.plugins.RxAndroidSchedulersHook;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.subscriptions.Subscriptions;

/**
 * Created by Gavriil Sitnikov on 10/03/16.
 * Base class of application to extends for Touch Instinct related projects.
 */
public abstract class TouchinApp extends Application {

    /**
     * Returns if application runs in debug mode or not.
     * In most cases it should return BuildConfig.DEBUG.
     * If it will returns true then debug options for RoboSwag components will be enabled.
     * In non-debug mode it will also enables Crashlitycs.
     *
     * @return True if application runs in debug mode.
     */
    protected abstract boolean isDebug();

    @Override
    protected void attachBaseContext(@NonNull final Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        RxAndroidPlugins.getInstance().registerSchedulersHook(new RxAndroidSchedulersHook() {
            @Override
            public Scheduler getMainThreadScheduler() {
                return new MainThreadScheduler();
            }
        });
        JodaTimeAndroid.init(this);
        if (isDebug()) {
            enableStrictMode();
            ViewControllerFragment.setInDebugMode();
            TypefacedEditText.setInDebugMode();
            TypefacedTextView.setInDebugMode();
            Lc.initialize(new ConsoleLogProcessor(LcLevel.VERBOSE), true);
        } else {
            final Crashlytics crashlytics = new Crashlytics();
            Fabric.with(this, crashlytics);
            Lc.initialize(new CrashlyticsLogProcessor(crashlytics), false);
        }
    }

    private void enableStrictMode() {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .permitDiskReads()
                .permitDiskWrites()
                .penaltyLog()
                .build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build());
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

    /**
     * This hacky class is needed to execute actions immediately on main thread but not schedule on main thread handler with 0 delay instead.
     */
    private static class MainThreadScheduler extends Scheduler {

        @Override
        public Worker createWorker() {
            return new WrapperMainThreadWorker();
        }

        private class WrapperMainThreadWorker extends Worker {

            @NonNull
            private final Worker parentWorker = AndroidSchedulers.from(Looper.getMainLooper()).createWorker();

            @Override
            public Subscription schedule(@NonNull final Action0 action) {
                if (Looper.getMainLooper().equals(Looper.myLooper())) {
                    action.call();
                    return Subscriptions.unsubscribed();
                }
                return parentWorker.schedule(action);
            }

            @Override
            public Subscription schedule(@NonNull final Action0 action, final long delayTime, @NonNull final TimeUnit unit) {
                return parentWorker.schedule(action, delayTime, unit);
            }

            @Override
            public void unsubscribe() {
                parentWorker.unsubscribe();
            }

            @Override
            public boolean isUnsubscribed() {
                return parentWorker.isUnsubscribed();
            }

        }

    }

}
