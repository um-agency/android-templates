package ru.touchin.templates.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.api.client.util.Data;

import java.util.List;

import ru.touchin.roboswag.core.data.Model;

/**
 * Created by Gavriil Sitnikov on 13/11/2015.
 */
public abstract class JsonModel extends Model {

    protected static boolean isNullOrEmpty(@Nullable final Object object) {
        return object == null || Data.isNull(object);
    }

    @Nullable
    protected static <T> T makeNullable(@Nullable final T data) {
        return !isNullOrEmpty(data) ? data : null;
    }

    protected static boolean isListValid(@NonNull final List list) {
        for (final Object item : list) {
            if (item == null || ((item instanceof JsonModel) && !((JsonModel) item).isValid())) {
                return false;
            }
        }
        return true;
    }

}