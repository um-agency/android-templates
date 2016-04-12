package ru.touchin.templates.model;

import android.support.annotation.Nullable;

import com.google.api.client.util.Data;

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

}