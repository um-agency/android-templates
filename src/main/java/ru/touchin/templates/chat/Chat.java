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

package ru.touchin.templates.chat;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ru.touchin.roboswag.core.observables.RxAndroidUtils;
import rx.Observable;
import rx.Scheduler;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;

/**
 * Created by Gavriil Sitnikov on 12/05/16.
 */
public abstract class Chat<TOutgoingMessage> {

    @NonNull
    private final PublishSubject<TOutgoingMessage> messageToSendEvent = PublishSubject.create();
    private final BehaviorSubject<List<TOutgoingMessage>> sendingMessages = BehaviorSubject.create(new ArrayList<>());
    @NonNull
    private final BehaviorSubject<Boolean> isActivated = BehaviorSubject.create(false);
    @NonNull
    private final PublishSubject<Void> retrySendingEvent = PublishSubject.create();
    @NonNull
    private final BehaviorSubject<Boolean> isSendingInError = BehaviorSubject.create(false);

    public Chat(@Nullable final Collection<TOutgoingMessage> messagesToSend) {
        final Scheduler sendingScheduler = RxAndroidUtils.createLooperScheduler();
        messageToSendEvent
                .observeOn(sendingScheduler)
                .doOnNext(message -> {
                    final List<TOutgoingMessage> messages = new ArrayList<>(sendingMessages.getValue());
                    messages.add(0, message);
                    sendingMessages.onNext(messages);
                })
                .concatMap(message -> isActivated
                        .filter(activated -> activated)
                        .first()
                        .switchMap(ignored -> Observable
                                .combineLatest(isMessageInCacheObservable(message), isMessageInActualObservable(message),
                                        (messageInCache, messageInActual) -> !messageInCache && !messageInActual)
                                .observeOn(sendingScheduler)
                                .switchMap(shouldSendMessage -> {
                                    if (!shouldSendMessage) {
                                        return Observable.empty();
                                    }
                                    return sendMessageObservable(message)
                                            .doOnSubscribe(() -> isSendingInError.onNext(false))
                                            .retryWhen(attempts -> attempts.map(throwable -> {
                                                isSendingInError.onNext(true);
                                                return retrySendingEvent;
                                            }))
                                            .doOnCompleted(() -> {
                                                final List<TOutgoingMessage> messages = new ArrayList<>(sendingMessages.getValue());
                                                messages.remove(message);
                                                sendingMessages.onNext(messages);
                                            });
                                })))
                .subscribe();

        if (messagesToSend != null) {
            for (final TOutgoingMessage message : messagesToSend) {
                messageToSendEvent.onNext(message);
            }
        }
    }

    @NonNull
    public Observable<List<TOutgoingMessage>> observeSendingMessages() {
        return sendingMessages;
    }

    @NonNull
    protected abstract Observable<Boolean> isMessageInCacheObservable(@NonNull final TOutgoingMessage message);

    @NonNull
    protected abstract Observable<Boolean> isMessageInActualObservable(@NonNull final TOutgoingMessage message);

    @NonNull
    protected abstract Observable<?> sendMessageObservable(@NonNull final TOutgoingMessage message);

    public void sendMessage(@NonNull final TOutgoingMessage message) {
        messageToSendEvent.onNext(message);
    }

    public void activate() {
        isActivated.onNext(true);
    }

    public void retrySend() {
        isSendingInError.onNext(false);
    }

    public void deactivate() {
        isActivated.onNext(false);
    }

}
