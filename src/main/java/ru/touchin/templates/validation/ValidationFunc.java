package ru.touchin.templates.validation;

import android.support.annotation.NonNull;

import java.io.Serializable;

/**
 * Created by Ilia Kurtov on 30/01/2017.
 * Simple interface that gets one parameter {@link TInput} as input and returns other type {@link TReturn}.
 * Interface extends {@link Serializable} to survive after {@link ru.touchin.roboswag.components.navigation.AbstractState} recreation.
 * Created as a replace for {@link rx.functions.Func1} because it needed to be {@link Serializable}
 * @param <TInput> input type.
 * @param <TReturn> return type.
 */
public interface ValidationFunc<TInput, TReturn> extends Serializable  {

    /**
     *
     * @param input
     * @return
     * @throws Throwable
     */
    @NonNull
    TReturn call(@NonNull final TInput input) throws Throwable;

}