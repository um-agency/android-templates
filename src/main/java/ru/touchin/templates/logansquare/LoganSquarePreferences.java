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

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.bluelinelabs.logansquare.LoganSquare;

import java.io.IOException;
import java.util.List;

import ru.touchin.roboswag.components.utils.storables.PreferenceStore;
import ru.touchin.roboswag.core.observables.storable.SafeConverter;
import ru.touchin.roboswag.core.observables.storable.Storable;
import ru.touchin.roboswag.core.observables.storable.concrete.NonNullSafeListStorable;
import ru.touchin.roboswag.core.observables.storable.concrete.NonNullSafeStorable;
import ru.touchin.roboswag.core.observables.storable.concrete.SafeListStorable;
import ru.touchin.roboswag.core.observables.storable.concrete.SafeStorable;
import ru.touchin.roboswag.core.utils.ShouldNotHappenException;

/**
 * Created by Gavriil Sitnikov on 26/12/2016.
 * Utility class to get {@link Storable} that is storing LoganSquare (Json) generated object into preferences.
 */
@SuppressWarnings("CPD-START")
//CPD: it is same code as in GoogleJsonPreferences
public final class LoganSquarePreferences {

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
                                                                          @NonNull final T defaultValue) {
        return new Storable.Builder<String, T, String>(name, jsonClass, Storable.ObserveStrategy.CACHE_ACTUAL_VALUE)
                .setSafeStore(String.class, new PreferenceStore<>(preferences), new JsonConverter<>())
                .setDefaultValue(defaultValue)
                .build();
    }

    @NonNull
    public static <T> SafeListStorable<String, T, String> jsonListStorable(@NonNull final String name,
                                                                           @NonNull final Class<T> jsonClass,
                                                                           @NonNull final SharedPreferences preferences) {
        return new SafeListStorable<>(new Storable.Builder<String, List, String>(name, List.class, Storable.ObserveStrategy.CACHE_ACTUAL_VALUE)
                .setSafeStore(String.class, new PreferenceStore<>(preferences), new JsonListConverter<>(jsonClass))
                .build());
    }

    @NonNull
    public static <T> NonNullSafeListStorable<String, T, String> jsonListStorable(@NonNull final String name,
                                                                                  @NonNull final Class<T> jsonClass,
                                                                                  @NonNull final SharedPreferences preferences,
                                                                                  @NonNull final List<T> defaultValue) {
        return new NonNullSafeListStorable<>(new Storable.Builder<String, List, String>(name, List.class, Storable.ObserveStrategy.CACHE_ACTUAL_VALUE)
                .setSafeStore(String.class, new PreferenceStore<>(preferences), new JsonListConverter<>(jsonClass))
                .setDefaultValue(defaultValue)
                .build());
    }

    private LoganSquarePreferences() {
    }

    public static class JsonListConverter<T> implements SafeConverter<List, String> {

        @NonNull
        private final Class<T> itemClass;

        public JsonListConverter(@NonNull final Class<T> itemClass) {
            this.itemClass = itemClass;
        }

        @Nullable
        @Override
        @SuppressWarnings("unchecked")
        public String toStoreObject(@NonNull final Class<List> jsonObjectClass, @NonNull final Class<String> stringClass,
                                    @Nullable final List object) {
            if (object == null) {
                return null;
            }
            try {
                return LoganSquare.serialize(object, itemClass);
            } catch (final IOException exception) {
                throw new ShouldNotHappenException(exception);
            }
        }

        @Nullable
        @Override
        public List toObject(@NonNull final Class<List> jsonObjectClass, @NonNull final Class<String> stringClass,
                             @Nullable final String storeValue) {
            if (storeValue == null) {
                return null;
            }
            try {
                return LoganSquare.parseList(storeValue, itemClass);
            } catch (final IOException exception) {
                throw new ShouldNotHappenException(exception);
            }
        }

    }

    public static class JsonConverter<TJsonObject> implements SafeConverter<TJsonObject, String> {

        @Nullable
        @Override
        public String toStoreObject(@NonNull final Class<TJsonObject> jsonObjectClass, @NonNull final Class<String> stringClass,
                                    @Nullable final TJsonObject object) {
            if (object == null) {
                return null;
            }
            try {
                return LoganSquare.serialize(object);
            } catch (final IOException exception) {
                throw new ShouldNotHappenException(exception);
            }
        }

        @Nullable
        @Override
        public TJsonObject toObject(@NonNull final Class<TJsonObject> jsonObjectClass, @NonNull final Class<String> stringClass,
                                    @Nullable final String storeValue) {
            if (storeValue == null) {
                return null;
            }
            try {
                return LoganSquare.parse(storeValue, jsonObjectClass);
            } catch (final IOException exception) {
                throw new ShouldNotHappenException(exception);
            }
        }

    }

}
