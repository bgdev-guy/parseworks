package io.github.parseworks.impl.inputs.impl;


import io.github.parseworks.IList;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class NonEmptyList<T> implements IList<T> {

    private final T head;
    private final IList<T> tail;

    public NonEmptyList(T head, IList<T> tail) {
        this.head = Objects.requireNonNull(head);
        this.tail = Objects.requireNonNull(tail);
    }

    @Override
    public NonEmptyList<T> add(T head) {
        return new NonEmptyList<>(head, this);
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public T head() {
        return head;
    }

    @Override
    public IList<T> tail() {
        return tail;
    }

    @Override
    public T get(int index) {
        if (index < 0) {
            throw new IndexOutOfBoundsException("Index " + index + " out of bounds");
        } else if (index == 0) {
            return head();
        } else {
            return tail().get(index - 1);
        }
    }

    @Override
    public IList<T> reverse() {
        IList<T> reversed = IList.empty();
        IList<T> current = this;
        while (!current.isEmpty()) {
            reversed = reversed.add(current.head());
            current = current.tail();
        }
        return reversed;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<>() {
            private IList<T> current = NonEmptyList.this;

            @Override
            public boolean hasNext() {
                return !current.isEmpty();
            }

            @Override
            public T next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                T value = current.head();
                current = current.tail();
                return value;
            }
        };
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IList<?> iList = (IList<?>) o;
        return Objects.equals(head, iList.head()) &&
                Objects.equals(tail, iList.tail());
    }

    @Override
    public int hashCode() {
        return Objects.hash(head, tail);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        IList<T> current = this;
        while (!current.isEmpty()) {
            sb.append(current.head()).append(", ");
            current = current.tail();
        }
        if (sb.length() > 1) {
            sb.setLength(sb.length() - 2); // Remove the last ", "
        }
        sb.append("]");
        return sb.toString();
    }

    @Override
    public <R> IList<R> map(Function<T, R> mapper) {
        IList<R> result = IList.empty();
        IList<T> current = this;
        while (!current.isEmpty()) {
            result = result.add(mapper.apply(current.head()));
            current = current.tail();
        }
        return result.reverse();
    }

    @Override
    public <R> R match(Function<IList<T>, R> nonEmptyCase, Supplier<R> emptyCase) {
        return nonEmptyCase.apply(this);
    }

    @Override
    public <R> R foldLeft(R identity, BiFunction<R, T, R> operator) {
        R result = identity;
        for (T element : this) {
            result = operator.apply(result, element);
        }
        return result;
    }

    @Override
    public T foldLeft1(BinaryOperator<T> operator) {
        T result = head();
        IList<T> current = tail();
        while (!current.isEmpty()) {
            result = operator.apply(result, current.head());
            current = current.tail();
        }
        return result;
    }

    @Override
    public <R> R foldRight(R identity, BiFunction<T, R, R> operator) {
        return operator.apply(head(), tail().foldRight(identity, operator));
    }

    @Override
    public Stream<T> stream() {
        return StreamSupport.stream(spliterator(), false);
    }

    public int size() {
        return 1 + tail.size();
    }

}