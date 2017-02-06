package ru.touchin.templates.validation.validators;


import android.support.annotation.NonNull;

import java.io.Serializable;

/**
 * Created by Ilia Kurtov on 24/01/2017.
 * Class that simplifies work with {@link Validator}'s that have the same wrapper model and model type.
 * @param <TModel> model that should be bounded with a view.
 */
public class SameTypeValidator<TModel extends Serializable> extends Validator<TModel, TModel> {

    /**
     * Simply returns the same model without any converting.
     * @param wrapperModel input model.
     * @return the same model as input parameter.
     * @throws Throwable - in this case no throwable would be thrown.
     */
    @NonNull
    @Override
    protected TModel convertWrapperModelToModel(@NonNull final TModel wrapperModel)
            throws Throwable {
        return wrapperModel;
    }

}
