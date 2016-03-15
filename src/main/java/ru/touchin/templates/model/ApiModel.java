package ru.touchin.templates.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.api.client.util.Data;

import java.util.List;

/**
 * Created by Gavriil Sitnikov on 13/11/2015.
 */
public class ApiModel {

    public static boolean isNullOrEmpty(@Nullable final Object object) {
        return object == null || Data.isNull(object);
    }

    @Nullable
    public static <T> T makeNullable(@Nullable final T data) {
        return !isNullOrEmpty(data) ? data : null;
    }

    public static boolean isListValid(@NonNull final List list) {
        for (final Object item : list) {
            if (item == null || ((item instanceof ApiModel) && !((ApiModel) item).isValid())) {
                return false;
            }
        }
        return true;
    }

    public void validate() {
        //do nothing
    }

    public boolean isValid() {
        return true;
    }

}