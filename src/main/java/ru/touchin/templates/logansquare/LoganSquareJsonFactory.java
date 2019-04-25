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

import androidx.annotation.NonNull;

import com.bluelinelabs.logansquare.LoganSquare;
import com.google.api.client.json.jackson2.JacksonFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;
import ru.touchin.templates.retrofit.JsonRequestBodyConverter;
import ru.touchin.templates.retrofit.JsonResponseBodyConverter;

/**
 * Created by Gavriil Sitnikov on 2/06/2016.
 * LoganSquareConverter class to use with {@link Retrofit} to parse and generate models based on Google Jackson library {@link JacksonFactory}.
 */
public class LoganSquareJsonFactory extends Converter.Factory {

    @NonNull
    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(@NonNull final Type type,
                                                            @NonNull final Annotation[] annotations,
                                                            @NonNull final Retrofit retrofit) {
        return new LoganSquareJsonResponseBodyConverter<>(type);
    }

    @NonNull
    @Override
    public Converter<?, RequestBody> requestBodyConverter(@NonNull final Type type,
                                                          @NonNull final Annotation[] parameterAnnotations,
                                                          @NonNull final Annotation[] methodAnnotations,
                                                          @NonNull final Retrofit retrofit) {
        return new LoganSquareRequestBodyConverter<>();
    }

    public static class LoganSquareJsonResponseBodyConverter<T> extends JsonResponseBodyConverter<T> {

        @NonNull
        private final Type type;

        public LoganSquareJsonResponseBodyConverter(@NonNull final Type type) {
            super();
            this.type = type;
        }

        @SuppressWarnings("unchecked")
        @NonNull
        @Override
        protected T parseResponse(@NonNull final ResponseBody value) throws IOException {
            return (T) LoganSquare.parse(value.byteStream(), (Class) type);
        }

    }

    public static class LoganSquareRequestBodyConverter<T> extends JsonRequestBodyConverter<T> {

        @Override
        protected void writeValueToByteArray(@NonNull final T value, @NonNull final ByteArrayOutputStream byteArrayOutputStream)
                throws IOException {
            LoganSquare.serialize(value, byteArrayOutputStream);
        }

    }

}