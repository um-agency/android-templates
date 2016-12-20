package ru.touchin.templates.logansquare;

import android.support.annotation.NonNull;

import com.bluelinelabs.logansquare.typeconverters.StringBasedTypeConverter;

import ru.touchin.roboswag.core.utils.ShouldNotHappenException;

@SuppressWarnings("PMD.UseVarargs")
public class LoganSquareEnumConverter<T extends Enum & LoganSquareEnum> extends StringBasedTypeConverter<T> {

    @NonNull
    private final T[] enumValues;

    public LoganSquareEnumConverter(@NonNull final T[] enumValues) {
        super();
        this.enumValues = enumValues;
    }

    @Override
    public T getFromString(@NonNull final String string) {
        for (final T value : enumValues) {
            if (value.getValueName().equals(string)) {
                return value;
            }
        }
        throw new ShouldNotHappenException();
    }

    @Override
    public String convertToString(final T object) {
        return object.getValueName();
    }

}
