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

package ru.touchin.templates.model.increasing;

import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import ru.touchin.roboswag.components.listing.ItemsProvider;
import ru.touchin.roboswag.core.utils.android.RxAndroidUtils;
import rx.Observable;
import rx.Scheduler;
import rx.exceptions.OnErrorThrowable;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;

/**
 * Created by Gavriil Sitnikov on 13/05/16.
 */
public class IncreasingItemsProvider<TItem extends IncreasingItem> extends ItemsProvider<TItem> {

    private static final long MIN_UPDATE_TIME = TimeUnit.SECONDS.toMillis(5);

    @NonNull
    private final Scheduler scheduler = RxAndroidUtils.createLooperScheduler();
    @NonNull
    private final BehaviorSubject<Boolean> haveNewItems = BehaviorSubject.create(false);
    @NonNull
    private final BehaviorSubject<Boolean> haveHistoryItems = BehaviorSubject.create(true);
    @NonNull
    private final PublishSubject<?> refreshRequestEvent = PublishSubject.create();

    @NonNull
    private final List<TItem> items = new ArrayList<>();
    @NonNull
    private final LoaderRequestCreator<TItem> newItemsLoader;
    @NonNull
    private final LoaderRequestCreator<TItem> historyLoader;
    @NonNull
    private final Observable<Boolean> needRefreshObservable;
    @Nullable
    private Long lastNewItemsUpdate;

    @Nullable
    private Observable<?> newItemsConcreteObservable;
    @Nullable
    private Observable<?> historyItemsConcreteObservable;

    public IncreasingItemsProvider(@NonNull final LoaderRequestCreator<TItem> historyLoader,
                                   @NonNull final LoaderRequestCreator<TItem> newItemsLoader) {
        super();
        this.newItemsLoader = newItemsLoader;
        this.historyLoader = historyLoader;
        this.needRefreshObservable = createNeedRefreshObservable();
    }

    @NonNull
    private Observable<Boolean> createNeedRefreshObservable() {
        return observeIsHaveNewItems()
                .switchMap(haveNewItems -> haveNewItems
                        ? Observable.just(true)
                        : Observable.just(isNeedUpdate())
                        .concatWith(Observable
                                .merge(Observable.interval(MIN_UPDATE_TIME, TimeUnit.MILLISECONDS).map(ignored -> true),
                                        refreshRequestEvent.map(ignored -> true))))
                .replay(1)
                .refCount();
    }

    @NonNull
    public Observable<Boolean> getNeedRefreshObservable() {
        return needRefreshObservable;
    }

    @NonNull
    public Observable<Boolean> observeIsHaveNewItems() {
        return haveNewItems.distinctUntilChanged();
    }

    @NonNull
    public Observable<Boolean> observeIsHaveHistoryItems() {
        return haveHistoryItems.distinctUntilChanged();
    }

    private boolean isNeedUpdate() {
        return lastNewItemsUpdate == null || SystemClock.uptimeMillis() - lastNewItemsUpdate < MIN_UPDATE_TIME;
    }

    @NonNull
    @Override
    public TItem getItem(final int position) {
        return items.get(position);
    }

    @NonNull
    private Observable<?> loadNewItems() {
        return Observable
                .<Observable<?>>create(subscriber -> {
                    if (items.isEmpty()) {
                        subscriber.onNext(loadHistory());
                        subscriber.onCompleted();
                        return;
                    }
                    if (newItemsConcreteObservable == null) {
                        newItemsConcreteObservable = newItemsLoader.getByItemId(items.get(0).getItemId())
                                .subscribeOn(Schedulers.io())
                                .observeOn(scheduler)
                                .doOnNext(page -> {
                                    newItemsConcreteObservable = null;
                                    items.addAll(0, page.getItems());
                                    notifyChanges(Collections.singletonList(new Change(Change.Type.INSERTED, 0, page.getItems().size())));
                                    lastNewItemsUpdate = SystemClock.uptimeMillis();
                                    haveNewItems.onNext(!page.isLast());
                                    if (!page.isLast()) {
                                        throw OnErrorThrowable.from(new NotLastException());
                                    }
                                })
                                .replay(1)
                                .refCount();
                    }
                    subscriber.onNext(newItemsConcreteObservable);
                    subscriber.onCompleted();
                })
                .subscribeOn(scheduler)
                .switchMap(observable -> observable);
    }

    @NonNull
    private Observable<?> loadHistory() {
        return Observable
                .<Observable<?>>create(subscriber -> {
                    if (historyItemsConcreteObservable == null) {
                        final TItem fromMessage = !items.isEmpty() ? items.get(items.size() - 1) : null;
                        historyItemsConcreteObservable = historyLoader.getByItemId(fromMessage != null ? fromMessage.getItemId() : null)
                                .subscribeOn(Schedulers.io())
                                .observeOn(scheduler)
                                .doOnNext(page -> {
                                    historyItemsConcreteObservable = null;
                                    items.addAll(page.getItems());
                                    final int newItemsCount = page.getItems().size();
                                    final Change change = new Change(Change.Type.INSERTED, items.size() - newItemsCount, newItemsCount);
                                    notifyChanges(Collections.singletonList(change));
                                    haveHistoryItems.onNext(!page.isLast());
                                })
                                .replay(1)
                                .refCount();
                    }
                    subscriber.onNext(historyItemsConcreteObservable);
                    subscriber.onCompleted();
                })
                .subscribeOn(scheduler)
                .switchMap(observable -> observable);
    }

    public void requestRefresh() {
        refreshRequestEvent.onNext(null);
    }

    @NonNull
    public Observable<?> refresh() {
        return retryIfNotLast(loadNewItems());
    }

    @NonNull
    private Observable<TItem> loadFromHistory(final int position) {
        if (position < items.size()) {
            return Observable.just(items.get(position));
        }
        if (!haveHistoryItems.getValue()) {
            return Observable.just(null);
        }
        return retryIfNotLast(loadHistory()
                .observeOn(scheduler)
                .map(ignored -> {
                    if (position < items.size()) {
                        return items.get(position);
                    }
                    throw OnErrorThrowable.from(new NotLastException());
                }));
    }

    @SuppressWarnings("PMD.SimplifiedTernary")
    //TODO: looks like PMD thinking that false is part of ternary
    @NonNull
    @Override
    public Observable<TItem> loadItem(final int position) {
        return (position == 0 ? needRefreshObservable : Observable.just(false))
                .observeOn(scheduler)
                .switchMap(needRefresh -> {
                    if (!needRefresh) {
                        return loadFromHistory(position);
                    }
                    return loadNewItems()
                            .observeOn(scheduler)
                            .switchMap(ignored -> loadFromHistory(position));
                });
    }

    @NonNull
    protected <T> Observable<T> retryIfNotLast(@NonNull final Observable<T> observable) {
        return observable
                .retryWhen(attempts -> attempts
                        .map(throwable -> throwable instanceof NotLastException
                                ? Observable.just(null)
                                : Observable.error(throwable)));
    }

    @Override
    public int getSize() {
        return items.size();
    }

    private static class NotLastException extends Exception {
    }

    public interface LoaderRequestCreator<TItem> {

        @NonNull
        Observable<IncreasingItemsPage<TItem>> getByItemId(@Nullable final String itemId);

    }

}
