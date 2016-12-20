package ru.touchin.templates.logansquare;


import android.support.annotation.Nullable;

import ru.touchin.templates.ApiModel;

public abstract class LoganSquareJsonModel extends ApiModel {

    /**
     * Throws exception if object is missed or null.
     *
     * @param object Value of field to check;
     * @throws ValidationException Exception of validation.
     */
    protected static void validateNotNull(@Nullable final Object object) throws ValidationException {
        if (object == null) {
            throw new ValidationException("Not nullable object is null or missed");
        }
    }

}
