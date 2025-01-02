package io.github.parseworks.impl.inputs.impl;


import io.github.parseworks.IList;

import java.util.Collections;
import java.util.Iterator;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class EmptyList<T> implements IList<T> {

    public static final EmptyList<?> EMPTY = new EmptyList<Void>();

    @Override
    public IList<T> add(T head) {
        return new NonEmptyList<>(head, this);
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public T head() {
        throw new UnsupportedOperationException("Cannot take the head of an empty list");
    }

    @Override
    public IList<T> tail() {
        throw new UnsupportedOperationException("Cannot take the tail of an empty list");
    }

    @Override
    public T get(int index) {
        throw new IndexOutOfBoundsException("Index " + index + " out of bounds for an empty list");
    }

    @Override
    public IList<T> reverse() {
        return this;
    }

    @Override
    public Iterator<T> iterator() {
        return Collections.emptyIterator();
    }

    @Override
    public <R> IList<R> map(Function<T, R> mapper) {
        return IList.empty();
    }

    @Override
    public <R> R match(Function<IList<T>, R> nonEmptyCase, Supplier<R> emptyCase) {
        return emptyCase.get();
    }

    @Override
    public <R> R foldLeft(R identity, BiFunction<R, T, R> operator) {
        return identity;
    }

    @Override
    public T foldLeft1(BinaryOperator<T> operator) {
        throw new UnsupportedOperationException("Cannot fold an empty list");
    }

    @Override
    public <R> R foldRight(R identity, BiFunction<T, R, R> operator) {
        return identity;
    }

    @Override
    public Stream<T> stream() {
        return Stream.empty();
    }

    public int size() {
        return 0;
    }
}