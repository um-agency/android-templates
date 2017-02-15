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

package ru.touchin.templates.retrofit;

import android.support.annotation.NonNull;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.SocketException;

import javax.net.ssl.SSLException;

import okhttp3.ResponseBody;
import okhttp3.internal.http2.StreamResetException;
import retrofit2.Converter;
import ru.touchin.roboswag.core.log.Lc;
import ru.touchin.templates.ApiModel;

/**
 * Created by Gavriil Sitnikov on 14/02/2017.
 * Object to deserialize responses of remote requests from Retrofit.
 *
 * @param <T> Type of response object.
 */
public abstract class JsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {

    @SuppressWarnings("PMD.AvoidInstanceofChecksInCatchClause")
    //AvoidInstanceofChecksInCatchClause: we just don't need assertion on specific exceptions
    @NonNull
    @Override
    public T convert(@NonNull final ResponseBody value) throws IOException {
        final T result;
        try {
            result = parseResponse(value);
        } catch (final IOException exception) {
            if (!(exception instanceof SocketException)
                    && !(exception instanceof InterruptedIOException)
                    && !(exception instanceof SSLException)
                    && !(exception instanceof StreamResetException)) {
                Lc.assertion(exception);
            }
            throw exception;
        }

        if (result instanceof ApiModel) {
            try {
                ((ApiModel) result).validate();
            } catch (final ApiModel.ValidationException validationException) {
                Lc.assertion(validationException);
                throw validationException;
            }
            checkForRequestException(result);
        }

        return result;
    }

    /**
     * Parses response to specific object.
     *
     * @param value Response to parse;
     * @return Parsed object;
     * @throws IOException Throws during parsing.
     */
    @NonNull
    protected abstract T parseResponse(@NonNull ResponseBody value) throws IOException;

    /**
     * Check response object for error if need.
     *
     * @param result Response object;
     * @throws IOException Throws during checking.
     */
    protected void checkForRequestException(final T result) throws IOException {
        //nothing
    }

}