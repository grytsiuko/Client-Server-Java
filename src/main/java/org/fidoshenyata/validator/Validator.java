package org.fidoshenyata.validator;

public interface Validator<T> {
    boolean isValid(T obj);
}
