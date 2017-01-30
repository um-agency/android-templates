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

package ru.touchin.templates.validation.validators;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.Serializable;

import ru.touchin.roboswag.core.observables.Changeable;
import ru.touchin.roboswag.core.observables.NonNullChangeable;
import ru.touchin.roboswag.core.utils.pairs.HalfNullablePair;
import ru.touchin.templates.validation.ValidationFunc;
import ru.touchin.templates.validation.ValidationState;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by Ilia Kurtov on 24/01/2017.
 * TODO: fill
 */
public abstract class EditTextValidator<TModel extends Serializable> extends Validator<String, TModel> {

    @NonNull
    private final NonNullChangeable<Boolean> showFullCheck = new NonNullChangeable<>(false);
    @NonNull
    private final Changeable<ValidationFunc<TModel, HalfNullablePair<ValidationState, TModel>>> finalCheck = new Changeable<>(null);
    @NonNull
    private final Changeable<ValidationFunc<String, HalfNullablePair<ValidationState, TModel>>> primaryCheck = new Changeable<>(null);

    @NonNull
    public NonNullChangeable<Boolean> getShowFullCheck() {
        return showFullCheck;
    }

    @NonNull
    public Changeable<ValidationFunc<TModel, HalfNullablePair<ValidationState, TModel>>> getFinalCheck() {
        return finalCheck;
    }

    @NonNull
    public Changeable<ValidationFunc<String, HalfNullablePair<ValidationState, TModel>>> getPrimaryCheck() {
        return primaryCheck;
    }

    @NonNull
    private HalfNullablePair<ValidationState, TModel> validateText(
            @Nullable final ValidationFunc<TModel, HalfNullablePair<ValidationState, TModel>> finalCheck,
            @Nullable final ValidationFunc<String, HalfNullablePair<ValidationState, TModel>> primaryCheck,
            @NonNull final String text, final boolean fullCheck)
            throws Throwable {
        if (primaryCheck == null && finalCheck == null) {
            return new HalfNullablePair<>(ValidationState.VALID, convertWrapperModelToModel(text));
        }
        if (primaryCheck != null) {
            final HalfNullablePair<ValidationState, TModel> primaryPair = primaryCheck.call(text);
            if (finalCheck == null || primaryPair.getFirst() != ValidationState.VALID || primaryPair.getSecond() == null || !fullCheck) {
                return primaryPair;
            }
            return finalCheck.call(primaryPair.getSecond());
        }
        return finalCheck.call(convertWrapperModelToModel(text));
    }

    @NonNull
    @SuppressWarnings("PMD.AvoidCatchingThrowable")
    // It's intended
    private Observable<HalfNullablePair<ValidationState, TModel>> createValidationObservable(@NonNull final String text, final boolean fullCheck) {
        return Observable
                .combineLatest(finalCheck.observe().observeOn(Schedulers.computation()),
                        primaryCheck.observe().observeOn(Schedulers.computation()),
                        (finalCheck, primaryCheck) -> {
                            try {
                                return validateText(finalCheck, primaryCheck, text, fullCheck);
                            } catch (final Throwable exception) {
                                return new HalfNullablePair<>(ValidationState.ERROR_CONVERSION, null);
                            }
                        });
    }

    @NonNull
    private Observable<ValidationState> processChecks(@NonNull final String text, final boolean fullCheck) {
        return createValidationObservable(text, fullCheck)
                .map(HalfNullablePair::getFirst);
    }

    @NonNull
    public Observable<ValidationState> primaryValidate(@NonNull final String text) {
        return processChecks(text, false);
    }

    @NonNull
    public Observable<ValidationState> fullValidate(@NonNull final String text) {
        return processChecks(text, true);
    }

    @NonNull
    public Observable<HalfNullablePair<ValidationState, TModel>> fullValidateAndGetModel(@NonNull final String text) {
        return createValidationObservable(text, true)
                .first();
    }

}