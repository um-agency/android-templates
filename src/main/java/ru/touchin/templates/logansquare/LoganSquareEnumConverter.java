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

package ru.touchin.templates.logansquare;

import android.support.annotation.NonNull;

import com.bluelinelabs.logansquare.typeconverters.StringBasedTypeConverter;

import ru.touchin.roboswag.core.utils.ShouldNotHappenException;

/**
 * Created by Gavriil Sitnikov.
 * LoganSquare converter from String to Enum.
 */
@SuppressWarnings("PMD.UseVarargs")
public class LoganSquareEnumConverter<T extends Enum & LoganSquareEnum> extends StringBasedTypeConverter<T> {

    @NonNull
    private final T[] enumValues;

    public LoganSquareEnumConverter(@NonNull final T[] enumValues) {
        super();
        this.enumValues = enumValues;
    }

    @Override
    public T getFromString(@NonNull final String string) {
        for (final T value : enumValues) {
            if (value.getValueName().equals(string)) {
                return value;
            }
        }
        throw new ShouldNotHappenException();
    }

    @Override
    public String convertToString(final T object) {
        return object.getValueName();
    }

}
