package io.github.parseworks;


import io.github.parseworks.impl.inputs.impl.EmptyList;
import io.github.parseworks.impl.inputs.impl.NonEmptyList;

import java.util.Iterator;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public interface IList<T> extends Iterable<T> {

    /**
     * Construct an empty list.
     *
     * @param <R> the element type
     * @return an empty list
     */
    @SuppressWarnings("unchecked")
    static <R> IList<R> empty() {
        return (IList<R>) EmptyList.EMPTY;
    }

    /**
     * Construct a list with one element.
     *
     * @param elem element
     * @param <T>  element type
     * @return the new list with one element
     */
    static <T> IList<T> of(T elem) {
        return new NonEmptyList<>(elem, empty());
    }

    /**
     * Construct a list with one or more elements.
     *
     * @param elem  the first element
     * @param elems the remaining elements
     * @param <T>   the element type
     * @return the new list with one or more element
     */
    @SafeVarargs
    static <T> IList<T> of(T elem, T... elems) {
        IList<T> list = ofArray(elems);
        return list.add(elem);
    }

    /**
     * Construct a list from an array.
     *
     * @param elems the array of elements
     * @param <T>   the element type
     * @return the new list with multiple elements
     */
    static <T> IList<T> ofArray(T[] elems) {
        IList<T> list = empty();
        for (int i = elems.length - 1; i >= 0; --i) {
            list = list.add(elems[i]);
        }
        return list;
    }

    /**
     * Convert a list of {@link Character}s into a {@link String}.
     *
     * @param l the list of {@code Character}s
     * @return a {@code String}
     */
    static String listToString(IList<Character> l) {
        return l.foldLeft(new StringBuilder(), StringBuilder::append).toString();
    }

    /**
     * Create a new list by adding an element to the head of this list.
     *
     * @param head the element to add onto head of this list
     * @return the new list
     */
    IList<T> add(T head);

    /**
     * Return true if this list is empty otherwise false
     *
     * @return true if this list is empty otherwise false
     */
    boolean isEmpty();

    /**
     * Return the head element of this list.
     *
     * @return the head of this list.
     * @throws UnsupportedOperationException if the list is empty.
     */
    T head();

    /**
     * Return the tail of this list.
     *
     * @return the tail of this list.
     * @throws UnsupportedOperationException if the list is empty.
     */
    IList<T> tail();

    /**
     * Returns the element at the specified position in this list.
     *
     * @param index the position of the element to return
     * @return the element of this list at the specified position.
     * @throws IndexOutOfBoundsException if the index is out of bounds.
     */
    T get(int index);

    /**
     * Reverse the list.
     *
     * @return a new list with the elements in reverse order
     */
    IList<T> reverse();

    @Override
    Iterator<T> iterator();

    @Override
    boolean equals(Object o);

    @Override
    int hashCode();

    /**
     * Map function over the list.
     *
     * @param mapper the function to apply to each element
     * @param <R>    the result type
     * @return a new list with the mapped elements
     */
    <R> IList<R> map(Function<T, R> mapper);

    /**
     * Match function to handle both non-empty and empty cases.
     *
     * @param nonEmptyCase the function to apply if the list is non-empty
     * @param emptyCase    the supplier to provide a result if the list is empty
     * @param <R>          the result type
     * @return the result of applying the appropriate function
     */
    <R> R match(Function<IList<T>, R> nonEmptyCase, Supplier<R> emptyCase);

    /**
     * Fold left function over the list.
     *
     * @param identity the initial value
     * @param operator the binary operator to apply
     * @param <R>      the result type
     * @return the result of folding the list
     */
    <R> R foldLeft(R identity, BiFunction<R, T, R> operator);

    /**
     * Fold left function over the list with at least one element.
     *
     * @param operator the binary operator to apply
     * @return the result of folding the list
     */
    T foldLeft1(BinaryOperator<T> operator);

    /**
     * Fold right function over the list.
     *
     * @param identity the initial value
     * @param operator the binary operator to apply
     * @param <R>      the result type
     * @return the result of folding the list
     */
    <R> R foldRight(R identity, BiFunction<T, R, R> operator);

    /**
     * Convert the list to a stream.
     *
     * @return a stream of the list elements
     */
    Stream<T> stream();

    int size();

}
