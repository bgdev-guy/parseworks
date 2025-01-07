package io.github.parseworks;

import java.util.List;
import java.util.function.BinaryOperator;

public class Utils {

    public static <I, A> Result<I, A> failure(Input<I> input) {
        return Result.failure(input, "Parsing failed");
    }


    static <A> A reduce(A a, List<Pair<BinaryOperator<A>, A>> lopA) {
        return lopA.stream().reduce(a, (acc, pair) -> pair.first().apply(acc, pair.second()), (a1, a2) -> a1);
    }
}