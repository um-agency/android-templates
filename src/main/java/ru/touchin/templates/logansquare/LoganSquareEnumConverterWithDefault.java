/*
 *  Copyright (c) 2017 Touch Instinct
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

package ru.touchin.templates.logansquare;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import ru.touchin.roboswag.core.utils.ShouldNotHappenException;

/**
 * Created by Anton Arhipov.
 * LoganSquare converter from String to Enum with default value.
 * Doesn't throw exception for unknown values.
 */
public class LoganSquareEnumConverterWithDefault<T extends Enum & LoganSquareEnum> extends LoganSquareEnumConverter<T> {

    private final T defaultValue;

    public LoganSquareEnumConverterWithDefault(@NonNull final T[] enumValues, @NonNull final T defaultValue) {
        super(enumValues);
        this.defaultValue = defaultValue;
    }

    @Nullable
    @Override
    public T getFromString(@Nullable final String string) {
        try {
            return super.getFromString(string);
        } catch (final ShouldNotHappenException exception) {
            return defaultValue;
        }
    }

}
