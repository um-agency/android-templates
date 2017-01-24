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
import android.text.TextUtils;

import java.io.Serializable;

import ru.touchin.roboswag.core.utils.pairs.NonNullPair;
import rx.Observable;

/**
 * Created by Ilia Kurtov on 24/01/2017.
 * TODO: fill
 */
public class EditTextValidationController<TModel extends Serializable>
        extends TwoWayValidationController<String, TModel, EditTextValidator<TModel>> {

    public EditTextValidationController(@NonNull final EditTextValidator<TModel> validationWrapper) {
        super(validationWrapper);
    }

    @NonNull
    public Observable<?> validation(@NonNull final Observable<Boolean> focusOutObservable, @NonNull final Observable<Boolean> activatedObservable) {
        return Observable
                .<Boolean, String, Boolean, Boolean, NonNullPair<Boolean, Observable<ValidationState>>>combineLatest(activatedObservable,
                        getValidationWrapper().getWrapperModel().observe(),
                        focusOutObservable,
                        getValidationWrapper().getShowFullCheck().observe(),
                        (activated, text, focusIn, showError) -> {
                            if (focusIn == null && TextUtils.isEmpty(text) && !activated && !showError) {
                                return null;
                            }
                            final boolean focus = focusIn == null ? false : focusIn;
                            if (TextUtils.isEmpty(text)) {
                                return new NonNullPair<>(focus, (activated || showError)
                                        ? getValidationWrapper().getValidationStateWhenEmpty().observe()
                                        .map(state -> state != null ? state : ValidationState.ERROR_NO_DESCRIPTION)
                                        : Observable.just(ValidationState.INITIAL));
                            }
                            if (!showError && focus) {
                                return new NonNullPair<>(true, getValidationWrapper().primaryValidate(text));
                            }
                            return new NonNullPair<>(focus, getValidationWrapper().fullValidate(text));
                        })
                .switchMap(validationPair -> {
                    if (validationPair == null) {
                        return Observable.empty();
                    }
                    return validationPair.getSecond()
                            .doOnNext(validationState -> {
                                if (!validationPair.getFirst()) {
                                    getValidationWrapper().getShowFullCheck().set(validationState != ValidationState.VALID);
                                }
                                getValidationWrapper().getValidationState().set(validationState);
                            });
                });
    }

}