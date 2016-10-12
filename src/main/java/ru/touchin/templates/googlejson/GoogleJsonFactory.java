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

import android.support.annotation.NonNull;

import com.google.api.client.http.AbstractHttpContent;
import com.google.api.client.http.json.JsonHttpContent;
import com.google.api.client.json.jackson2.JacksonFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.net.SocketException;

import javax.net.ssl.SSLException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.internal.framed.StreamResetException;
import retrofit2.Converter;
import retrofit2.Retrofit;
import ru.touchin.roboswag.core.log.Lc;

/**
 * Created by Gavriil Sitnikov on 2/06/2016.
 * Converter class to use with {@link Retrofit} to parse and generate models based on Google Jackson library {@link JacksonFactory}.
 */
public class GoogleJsonFactory extends Converter.Factory {

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(@NonNull final Type type,
                                                            @NonNull final Annotation[] annotations,
                                                            @NonNull final Retrofit retrofit) {
        return new GoogleJsonResponseBodyConverter<>(type);
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(@NonNull final Type type,
                                                          @NonNull final Annotation[] parameterAnnotations,
                                                          @NonNull final Annotation[] methodAnnotations,
                                                          @NonNull final Retrofit retrofit) {
        return new GoogleJsonRequestBodyConverter<>();
    }

    public static class GoogleJsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {

        @NonNull
        private final Type type;

        public GoogleJsonResponseBodyConverter(@NonNull final Type type) {
            this.type = type;
        }

        @SuppressWarnings({"unchecked", "PMD.AvoidInstanceofChecksInCatchClause"})
        //AvoidInstanceofChecksInCatchClause: we just don't need assertion on specific exceptions
        @NonNull
        @Override
        public T convert(@NonNull final ResponseBody value) throws IOException {
            final T result;
            try {
                result = (T) GoogleJsonModel.DEFAULT_JSON_FACTORY.createJsonParser(value.charStream()).parse(type, true);
            } catch (final IOException exception) {
                if (!(exception instanceof SocketException)
                        && !(exception instanceof InterruptedIOException)
                        && !(exception instanceof SSLException)
                        && !(exception instanceof StreamResetException)) {
                    Lc.assertion(exception);
                }
                throw exception;
            }

            if (result instanceof GoogleJsonModel) {
                try {
                    ((GoogleJsonModel) result).validate();
                } catch (final GoogleJsonModel.ValidationException validationException) {
                    Lc.assertion(validationException);
                    throw validationException;
                }
            }

            return result;
        }

    }

    public static class GoogleJsonRequestBodyConverter<T> implements Converter<T, RequestBody> {

        private static final MediaType MEDIA_TYPE = MediaType.parse("application/json; charset=UTF-8");

        @NonNull
        @Override
        public RequestBody convert(@NonNull final T value) throws IOException {
            final AbstractHttpContent content = new JsonHttpContent(GoogleJsonModel.DEFAULT_JSON_FACTORY, value);
            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            content.writeTo(byteArrayOutputStream);
            return RequestBody.create(MEDIA_TYPE, byteArrayOutputStream.toByteArray());
        }

    }

}