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

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;

import ru.touchin.roboswag.components.navigation.activities.ViewControllerActivity;
import ru.touchin.roboswag.components.utils.Logic;
import ru.touchin.roboswag.components.utils.UiUtils;
import ru.touchin.roboswag.core.log.Lc;
import ru.touchin.roboswag.core.utils.ServiceBinder;
import ru.touchin.roboswag.core.utils.ShouldNotHappenException;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Actions;
import rx.subjects.BehaviorSubject;

/**
 * Created by Gavriil Sitnikov on 10/01/17.
 * Base class of service to extends for Touch Instinct related projects.
 * It contains {@link Logic} and could bind to it through {@link #untilDestroy(Observable)} methods.
 *
 * @param <TLogic> Type of application's {@link Logic}.
 */
public abstract class TouchinService<TLogic extends Logic> extends Service {

    //it is needed to hold strong reference to logic
    private TLogic reference;
    @NonNull
    private final Handler postHandler = new Handler();
    @NonNull
    private final BehaviorSubject<Boolean> isCreatedSubject = BehaviorSubject.create();

    /**
     * It should return specific class where all logic will be.
     *
     * @return Returns class of specific {@link Logic}.
     */
    @NonNull
    protected abstract Class<TLogic> getLogicClass();

    @Override
    public void onCreate() {
        super.onCreate();
        UiUtils.UI_LIFECYCLE_LC_GROUP.i(Lc.getCodePoint(this));
        postHandler.post(() -> isCreatedSubject.onNext(true));
    }

    /**
     * Returns (and creates if needed) application's logic.
     *
     * @return Object which represents application's logic.
     */
    @NonNull
    protected TLogic getLogic() {
        synchronized (ViewControllerActivity.class) {
            if (reference == null) {
                reference = Logic.getInstance(this, getLogicClass());
            }
        }
        return reference;
    }

    @SuppressWarnings("CPD-START")
    //CPD: it is same as in other implementation based on BaseLifecycleBindable
    /**
     * Method should be used to guarantee that observable won't be subscribed after onDestroy.
     * It is automatically subscribing to observable.
     * Don't forget to process errors if observable can emit them.
     *
     * @param observable {@link Observable} to subscribe until onDestroy;
     * @param <T>        Type of emitted by observable items;
     * @return {@link Observable} which is wrapping source observable to unsubscribe from it onDestroy.
     */
    @NonNull
    public <T> Subscription untilDestroy(@NonNull final Observable<T> observable) {
        final String codePoint = Lc.getCodePoint(this, 1);
        return untilDestroy(observable, Actions.empty(),
                throwable -> Lc.assertion(new ShouldNotHappenException("Unexpected error on untilDestroy at " + codePoint, throwable)),
                Actions.empty());
    }

    /**
     * Method should be used to guarantee that observable won't be subscribed after onDestroy.
     * It is automatically subscribing to observable and calls onNextAction on every emitted item.
     * Don't forget to process errors if observable can emit them.
     *
     * @param observable   {@link Observable} to subscribe until onDestroy;
     * @param onNextAction Action which will raise on every {@link Subscriber#onNext(Object)} item;
     * @param <T>          Type of emitted by observable items;
     * @return {@link Observable} which is wrapping source observable to unsubscribe from it onDestroy.
     */
    @NonNull
    public <T> Subscription untilDestroy(@NonNull final Observable<T> observable,
                                         @NonNull final Action1<T> onNextAction) {
        final String codePoint = Lc.getCodePoint(this, 1);
        return untilDestroy(observable, onNextAction,
                throwable -> Lc.assertion(new ShouldNotHappenException("Unexpected error on untilDestroy at " + codePoint, throwable)),
                Actions.empty());
    }

    /**
     * Method should be used to guarantee that observable won't be subscribed after onDestroy.
     * It is automatically subscribing to observable and calls onNextAction, onErrorAction on observable events.
     * Don't forget to process errors if observable can emit them.
     *
     * @param observable    {@link Observable} to subscribe until onDestroy;
     * @param onNextAction  Action which will raise on every {@link Subscriber#onNext(Object)} item;
     * @param onErrorAction Action which will raise on every {@link Subscriber#onError(Throwable)} throwable;
     * @param <T>           Type of emitted by observable items;
     * @return {@link Observable} which is wrapping source observable to unsubscribe from it onDestroy.
     */
    @NonNull
    public <T> Subscription untilDestroy(@NonNull final Observable<T> observable,
                                         @NonNull final Action1<T> onNextAction,
                                         @NonNull final Action1<Throwable> onErrorAction) {
        return untilDestroy(observable, onNextAction, onErrorAction, Actions.empty());
    }

    /**
     * Method should be used to guarantee that observable won't be subscribed after onDestroy.
     * It is automatically subscribing to observable and calls onNextAction, onErrorAction, onCompletedAction on observable events.
     * Don't forget to process errors if observable can emit them.
     *
     * @param observable        {@link Observable} to subscribe until onDestroy;
     * @param onNextAction      Action which will raise on every {@link Subscriber#onNext(Object)} item;
     * @param onErrorAction     Action which will raise on every {@link Subscriber#onError(Throwable)} throwable;
     * @param onCompletedAction Action which will raise at {@link Subscriber#onCompleted()} on completion of observable;
     * @param <T>               Type of emitted by observable items;
     * @return {@link Observable} which is wrapping source observable to unsubscribe from it onDestroy.
     */
    @NonNull
    public <T> Subscription untilDestroy(@NonNull final Observable<T> observable,
                                         @NonNull final Action1<T> onNextAction,
                                         @NonNull final Action1<Throwable> onErrorAction,
                                         @NonNull final Action0 onCompletedAction) {
        return until(observable, isCreatedSubject.map(created -> !created), onNextAction, onErrorAction, onCompletedAction);
    }

    @NonNull
    private <T> Subscription until(@NonNull final Observable<T> observable,
                                   @NonNull final Observable<Boolean> conditionSubject,
                                   @NonNull final Action1<T> onNextAction,
                                   @NonNull final Action1<Throwable> onErrorAction,
                                   @NonNull final Action0 onCompletedAction) {
        return isCreatedSubject.first()
                .switchMap(created -> created
                        ? observable.observeOn(AndroidSchedulers.mainThread()).doOnCompleted(onCompletedAction)
                        : Observable.empty())
                .takeUntil(conditionSubject.filter(condition -> condition))
                .subscribe(onNextAction, onErrorAction);
    }

    @SuppressWarnings("CPD-END")
    //CPD: it is same as in other implementation based on BaseLifecycleBindable
    @NonNull
    @Override
    public IBinder onBind(@NonNull final Intent intent) {
        return new ServiceBinder<>(this);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        UiUtils.UI_LIFECYCLE_LC_GROUP.i(Lc.getCodePoint(this));
    }

    @Override
    public void onDestroy() {
        UiUtils.UI_LIFECYCLE_LC_GROUP.i(Lc.getCodePoint(this));
        postHandler.removeCallbacksAndMessages(null);
        isCreatedSubject.onNext(false);
        super.onDestroy();
    }

}

