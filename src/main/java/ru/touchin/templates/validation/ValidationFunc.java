package ru.touchin.templates.validation;

import android.support.annotation.NonNull;

import java.io.Serializable;

public interface ValidationFunc<TInput, TReturn> extends Serializable  {

    @NonNull
    TReturn call(@NonNull final TInput input) throws Throwable;

}