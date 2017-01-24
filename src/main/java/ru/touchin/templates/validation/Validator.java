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

import java.io.Serializable;

import ru.touchin.roboswag.core.observables.Changeable;
import ru.touchin.roboswag.core.observables.NonNullChangeable;

/**
 * Created by Ilia Kurtov on 24/01/2017.
 * TODO: fill
 */
public abstract class Validator<TWrapperModel extends Serializable, TModel extends Serializable>
        implements Serializable {

    private static final long serialVersionUID = 1L;

    @NonNull
    private final NonNullChangeable<ValidationState> validationState = new NonNullChangeable<>(ValidationState.INITIAL);
    @NonNull
    private final Changeable<TWrapperModel> wrapperModel = new Changeable<>(null);
    @NonNull
    private final Changeable<ValidationState> validationStateWhenEmpty = new Changeable<>(null);

    @NonNull
    protected abstract TModel convertWrapperModelToModel(@NonNull final TWrapperModel wrapperModel) throws ConversionException;

    @NonNull
    public Changeable<TWrapperModel> getWrapperModel() {
        return wrapperModel;
    }

    @NonNull
    public NonNullChangeable<ValidationState> getValidationState() {
        return validationState;
    }

    @NonNull
    public Changeable<ValidationState> getValidationStateWhenEmpty() {
        return validationStateWhenEmpty;
    }

}