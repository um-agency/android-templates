package ru.touchin.templates.validation;


import android.support.annotation.NonNull;

import java.io.Serializable;

public class SameTypeValidator<TModel extends Serializable> extends Validator<TModel, TModel> {

    @NonNull
    @Override
    protected TModel convertWrapperModelToModel(@NonNull final TModel wrapperModel)
            throws ConversionException {
        return wrapperModel;
    }

}
