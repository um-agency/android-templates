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

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.api.client.http.json.JsonHttpContent;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import ru.touchin.roboswag.components.utils.storables.PreferenceStore;
import ru.touchin.roboswag.core.observables.storable.SafeConverter;
import ru.touchin.roboswag.core.observables.storable.Storable;
import ru.touchin.roboswag.core.observables.storable.concrete.NonNullSafeStorable;
import ru.touchin.roboswag.core.observables.storable.concrete.SafeStorable;
import ru.touchin.roboswag.core.utils.ShouldNotHappenException;

/**
 * Created by Gavriil Sitnikov on 23/08/2016.
 * Utility class to get {@link Storable} that is storing Google Json generated object into preferences.
 */
public final class GoogleJsonPreferences {

    @NonNull
    public static <T> SafeStorable<String, T, String> jsonStorable(@NonNull final String name,
                                                                   @NonNull final Class<T> jsonClass,
                                                                   @NonNull final SharedPreferences preferences) {
        return new Storable.Builder<String, T, String>(name, jsonClass, Storable.ObserveStrategy.CACHE_ACTUAL_VALUE)
                .setSafeStore(String.class, new PreferenceStore<>(preferences), new JsonConverter<>())
                .build();
    }

    @NonNull
    public static <T> NonNullSafeStorable<String, T, String> jsonStorable(@NonNull final String name,
                                                                          @NonNull final Class<T> jsonClass,
                                                                          @NonNull final SharedPreferences preferences,
                                                                          final T defaultValue) {
        return new Storable.Builder<String, T, String>(name, jsonClass, Storable.ObserveStrategy.CACHE_ACTUAL_VALUE)
                .setSafeStore(String.class, new PreferenceStore<>(preferences), new JsonConverter<>())
                .setDefaultValue(defaultValue)
                .build();
    }

    private GoogleJsonPreferences() {
    }

    public static class JsonConverter<TJsonObject> implements SafeConverter<TJsonObject, String> {

        @Nullable
        @Override
        public String toStoreObject(@NonNull final Class<TJsonObject> jsonObjectClass, @NonNull final Class<String> stringClass,
                                    @Nullable final TJsonObject object) {
            if (object == null) {
                return null;
            }
            final JsonHttpContent content = new JsonHttpContent(GoogleJsonModel.DEFAULT_JSON_FACTORY, object);
            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            try {
                content.writeTo(byteArrayOutputStream);
            } catch (final IOException exception) {
                throw new ShouldNotHappenException(exception);
            }
            return new String(byteArrayOutputStream.toByteArray());
        }

        @Nullable
        @Override
        public TJsonObject toObject(@NonNull final Class<TJsonObject> jsonObjectClass, @NonNull final Class<String> stringClass,
                                    @Nullable final String storeValue) {
            if (storeValue == null) {
                return null;
            }
            try {
                return GoogleJsonModel.DEFAULT_JSON_FACTORY.createJsonParser(storeValue).parse(jsonObjectClass);
            } catch (final IOException exception) {
                throw new ShouldNotHappenException(exception);
            }
        }

    }

}
