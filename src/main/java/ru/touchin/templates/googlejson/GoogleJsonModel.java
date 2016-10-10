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

package ru.touchin.templates.googlejson;

import android.support.annotation.Nullable;

import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Data;

import ru.touchin.templates.ApiModel;

/**
 * Created by Gavriil Sitnikov on 13/11/2015.
 * Simple class with helpers inside to work with models generated by {@link GoogleJsonFactory}.
 * Mostly used to validate models returned from server.
 */
public abstract class GoogleJsonModel extends ApiModel {

    /**
     * Just a simple default Google JSON factory to create model parers and generators.
     */
    public static final JsonFactory DEFAULT_JSON_FACTORY = new JacksonFactory();

    /**
     * Returns if this object not responded from server (no parameter in JSON file).
     *
     * @param object Value of field;
     * @return True if missed.
     */
    protected static boolean isMissed(@Nullable final Object object) {
        return object == null;
    }

    /**
     * Returns if this object is responded from server as null (parameter in JSON file equals null).
     *
     * @param object Value of field;
     * @return True if null.
     */
    protected static boolean isNull(@Nullable final Object object) {
        return Data.isNull(object);
    }

    /**
     * Returns if this object is responded from server as null (parameter in JSON file equals null)
     * or if this object not responded from server (no parameter in JSON file).
     *
     * @param object Value of field;
     * @return True if null or missed.
     */
    protected static boolean isNullOrMissed(@Nullable final Object object) {
        return isMissed(object) || isNull(object);
    }

    /**
     * Throws exception if object is missed or null.
     *
     * @param object Value of field to check;
     * @throws ValidationException Exception of validation.
     */
    protected static void validateNotNull(@Nullable final Object object) throws ValidationException {
        if (isNullOrMissed(object)) {
            throw new ValidationException("Not nullable object is null or missed");
        }
    }

    /**
     * Throws exception if object is missed.
     *
     * @param object Value of field to check;
     * @throws ValidationException Exception of validation.
     */
    protected static void validateNotMissed(@Nullable final Object object) throws ValidationException {
        if (isMissed(object)) {
            throw new ValidationException("Object missed");
        }
    }

    /**
     * Throws exception if object is null.
     *
     * @param object Value of field to check;
     * @throws ValidationException Exception of validation.
     */
    protected static void validateMissedOrNotNull(@Nullable final Object object) throws ValidationException {
        if (isNull(object)) {
            throw new ValidationException("Not null or not missed object is null");
        }
    }

    protected GoogleJsonModel() {
        super();
    }

}