package io.github.parseworks;

import java.util.Optional;
import java.util.function.Predicate;

public class Combinators {
    public static <I> Parser<I, Unit> eof() {
        return new Parser<>(() -> true, in -> {
            if (in.isEof()) {
                return Trampoline.done(Result.success(Unit.UNIT, in));
            } else {
                return Trampoline.done(Result.failure(in, "Expected end of input"));
            }
        });
    }

    public static <I> Parser<I, Unit> fail(String message) {
        return new Parser<>(() -> true, in -> Trampoline.done(Result.failure(in, message)));
    }

    public static <I, A> Parser<I, A> pure(A a) {
        return new Parser<>(() -> true, in -> Trampoline.done(Result.success(a, in)));
    }

    public static <I, A> Parser<I, A> satisfy(String errorMessage,Predicate<I> predicate) {
        return new Parser<>(() -> false, in -> {
            if (in.isEof()) {
                return Trampoline.done(Result.failure(in, "End of input"));
            }
            I i = in.get();
            if (predicate.test(i)) {
                return Trampoline.done((Result<I, A>) Result.success(i, in.next()));
            } else {
                return Trampoline.done(Result.failure(in, "Unexpected input: " + i));
            }
        });
    }

    public static <I, A> Parser<I, A> or(Parser<I, A> p1, Parser<I, A> p2) {
        return new Parser<>(() -> p1.acceptsEOF() || p2.acceptsEOF(), in ->
                p1.apply(in).flatMap(result1 ->
                        result1.isSuccess() ? Trampoline.done(result1) : p2.apply(in)
                )
        );
    }

    public static <I, A> Parser<I, A> or(Parser<I, A> p1, Parser<I, A> p2) {
        return new Parser<>(() -> p1.acceptsEOF() || p2.acceptsEOF(), in ->
                p1.apply(in).flatMap(result1 ->
                        result1.isSuccess() ? Trampoline.done(result1) : p2.apply(in)
                )
        );
    }

    public static <I, A, B> Parser<I, B> and(Parser<I, A> p1, Parser<I, B> p2) {
        return new Parser<>(() -> p1.acceptsEOF() && p2.acceptsEOF(), in ->
                p1.apply(in).flatMap(result1 ->
                        result1.isSuccess() ? p2.apply(result1.next()) : Trampoline.done(Result.failure(result1.next(), "First parser failed"))
                )
        );
    }

    public static <I, A> Parser<I, IList<A>> many(Parser<I, A> p) {
        return new Parser<>(p::acceptsEOF, in -> {
            IList<A> results = IList.empty();
            Input<I> current = in;
            while (true) {
                Result<I, A> result = p.apply(current).get();
                if (result.isSuccess()) {
                    results = results.add(result.getOrThrow());
                    current = result.next();
                } else {
                    break;
                }
            }
            return Trampoline.done(Result.success(results, current));
        });
    }

    public static <I, A> Parser<I, IList<A>> many1(Parser<I, A> p) {
        return new Parser<>(p::acceptsEOF, in ->
                p.apply(in).flatMap(result1 ->
                        result1.isSuccess() ? many(p).apply(result1.next()).map(result2 ->
                                Result.success(result2.getOrThrow().add(result1.getOrThrow()), result2.next())
                        ) : Trampoline.done(Result.failure(result1.next(), "Expected at least one match"))
                )
        );
    }

    public static <I, A> Parser<I, Optional<A>> optional(Parser<I, A> p) {
        return new Parser<>(p::acceptsEOF, in ->
                p.apply(in).map(result ->
                        result.isSuccess() ? Result.success(Optional.of(result.getOrThrow()), result.next()) : Result.success(Optional.empty(), in)
                )
        );
    }

    public static <I, A, B> Parser<I, IList<A>> sepBy(Parser<I, A> p, Parser<I, B> sep) {
        return new Parser<>(p::acceptsEOF, in -> {
            IList<A> results = IList.empty();
            Input<I> current = in;
            boolean first = true;
            while (true) {
                if (!first) {
                    Result<I, B> sepResult = sep.apply(current).get();
                    if (!sepResult.isSuccess()) {
                        break;
                    }
                    current = sepResult.next();
                }
                Result<I, A> result = p.apply(current).get();
                if (result.isSuccess()) {
                    results = results.add(result.getOrThrow());
                    current = result.next();
                } else {
                    break;
                }
                first = false;
            }
            return Trampoline.done(Result.success(results, current));
        });
    }

    public static <I, A, B, C> Parser<I, C> between(Parser<I, A> open, Parser<I, B> close, Parser<I, C> content) {
        return new Parser<>(content::acceptsEOF, in ->
                open.apply(in).flatMap(result1 ->
                        result1.isSuccess() ? content.apply(result1.next()).flatMap(result2 ->
                                result2.isSuccess() ? close.apply(result2.next()).map(result3 ->
                                        result3.isSuccess() ? Result.success(result2.getOrThrow(), result3.next()) : Result.failure(result3.next(), "Expected closing parser to succeed")
                                ) : Trampoline.done(Result.failure(result2.next(), "Expected content parser to succeed"))
                        ) : Trampoline.done(Result.failure(result1.next(), "Expected opening parser to succeed"))
                )
        );
    }
}