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

package ru.touchin.templates.validation;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.Serializable;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by Ilia Kurtov on 24/01/2017.
 * TODO: fill
 */
public abstract class TwoWayValidationController
        <TWrapperModel extends Serializable, TModel extends Serializable, TValidationWrapper extends Validator<TWrapperModel, TModel>>
        extends ValidationController<TValidationWrapper> {

    public TwoWayValidationController(@NonNull final TValidationWrapper validationWrapper) {
        super(validationWrapper);
    }

    @NonNull
    public Observable<?> modelAndViewUpdating(@Nullable final Observable<TWrapperModel> viewStateObservable,
                                              @NonNull final Action1<TWrapperModel> updateViewAction,
                                              @NonNull final ViewWithError viewWithError) {
        final Observable<?> stateObservable = viewStateObservable != null
                ? viewStateObservable.doOnNext(flag -> getValidationWrapper().getWrapperModel().set(flag))
                : Observable.empty();
        return Observable
                .merge(getValidationWrapper().getWrapperModel().observe()
                                .observeOn(AndroidSchedulers.mainThread())
                                .doOnNext(updateViewAction),
                        getValidationWrapper().getValidationState().observe()
                                .observeOn(AndroidSchedulers.mainThread())
                                .doOnNext(validationState -> {
                                    if (!showError(validationState)) {
                                        viewWithError.hideError();
                                    } else {
                                        viewWithError.showError(validationState);
                                    }
                                }),
                        stateObservable);
    }

    protected boolean showError(@NonNull final ValidationState validationState) {
        return validationState != ValidationState.VALID && validationState != ValidationState.INITIAL;
    }

}
