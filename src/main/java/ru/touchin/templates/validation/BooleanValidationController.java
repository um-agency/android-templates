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

import rx.Observable;

/**
 * Created by Ilia Kurtov on 24/01/2017.
 * TODO: fill
 */
public class BooleanValidationController extends TwoWayValidationController<Boolean, Boolean, SameTypeValidator<Boolean>> {

    public BooleanValidationController(@NonNull final SameTypeValidator<Boolean> validationWrapper) {
        super(validationWrapper);
    }

    @NonNull
    public Observable<?> validation(@NonNull final Observable<Boolean> activatedObservable) {
        return Observable.combineLatest(activatedObservable, getValidationWrapper().getWrapperModel().observe(),
                (activated, flag) -> {
                    final boolean selected = flag == null ? false : flag;
                    if (activated && !selected) {
                        return ValidationState.ERROR_NO_DESCRIPTION;
                    } else if (!activated && !selected) {
                        return ValidationState.INITIAL;
                    }
                    return ValidationState.VALID;
                })
                .doOnNext(validationState -> getValidationWrapper().getValidationState().set(validationState));
    }

}