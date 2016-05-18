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

package ru.touchin.templates.model;

import android.support.annotation.Nullable;

import com.google.api.client.util.Data;

import ru.touchin.roboswag.core.data.Model;
import ru.touchin.roboswag.core.data.exceptions.ValidationException;

/**
 * Created by Gavriil Sitnikov on 13/11/2015.
 */
public abstract class GoogleJsonModel extends Model {

    protected static boolean isNullOrEmpty(@Nullable final Object object) {
        return object == null || Data.isNull(object);
    }

    @Nullable
    protected static <T> T makeNullable(@Nullable final T data) {
        return !isNullOrEmpty(data) ? data : null;
    }

    protected static void validateNonNull(@Nullable final Object object) throws ValidationException {
        if (isNullOrEmpty(object)) {
            throw new ValidationException("Not nullable object is null or empty");
        }
    }

    protected static void validateNonEmpty(@Nullable final Object object) throws ValidationException {
        if (object == null) {
            throw new ValidationException("Not empty object is empty");
        }
    }

    protected static void validateEmptyOrNonNull(@Nullable final Object object) throws ValidationException {
        if (object != null && isNullOrEmpty(object)) {
            throw new ValidationException("Nullable or not empty object is empty");
        }
    }

}