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