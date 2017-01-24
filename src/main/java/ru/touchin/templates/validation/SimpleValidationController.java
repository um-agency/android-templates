package ru.touchin.templates.validation;

import android.support.annotation.NonNull;

import java.io.Serializable;

import rx.Observable;

public class SimpleValidationController<TModel extends Serializable, TValidationWrapper extends Validator<TModel, TModel>>
        extends TwoWayValidationController<TModel, TModel, TValidationWrapper> {

    public SimpleValidationController(@NonNull final TValidationWrapper validationWrapper) {
        super(validationWrapper);
    }

    @NonNull
    public Observable<?> validation(@NonNull final Observable<Boolean> activatedObservable) {
        return Observable.combineLatest(activatedObservable,
                getValidationWrapper().getWrapperModel().observe(), (activated, model) -> {
                    if (model == null) {
                        return activated ? ValidationState.ERROR_NO_DESCRIPTION : ValidationState.INITIAL;
                    }
                    return ValidationState.VALID;
                })
                .doOnNext(validationState -> getValidationWrapper().getValidationState().set(validationState));
    }

}