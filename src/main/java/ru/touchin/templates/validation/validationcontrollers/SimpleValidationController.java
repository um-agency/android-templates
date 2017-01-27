package ru.touchin.templates.validation.validationcontrollers;

import android.support.annotation.NonNull;

import java.io.Serializable;

import ru.touchin.templates.validation.ValidationState;
import ru.touchin.templates.validation.validators.Validator;
import rx.Observable;

public class SimpleValidationController<TModel extends Serializable, TValidator extends Validator<TModel, TModel>>
        extends TwoWayValidationController<TModel, TModel, TValidator> {

    public SimpleValidationController(@NonNull final TValidator validator) {
        super(validator);
    }

    @NonNull
    public Observable<?> validation(@NonNull final Observable<Boolean> activatedObservable) {
        return Observable.combineLatest(activatedObservable,
                getValidator().getWrapperModel().observe(), (activated, model) -> {
                    if (model == null) {
                        return activated ? ValidationState.ERROR_NO_DESCRIPTION : ValidationState.INITIAL;
                    }
                    return ValidationState.VALID;
                })
                .doOnNext(validationState -> getValidator().getValidationState().set(validationState));
    }

}