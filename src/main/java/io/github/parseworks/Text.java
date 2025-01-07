package io.github.parseworks;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static io.github.parseworks.Combinators.choice;

/**
 * The `Text` class provides a set of parsers for common text parsing tasks,
 * such as parsing specific characters, digits, letters, whitespace, and strings.
 */
public class Text {

    /**
     * Parses a single character that satisfies the given predicate.
     *
     * @param predicate the predicate that the character must satisfy
     * @return a parser that parses a single character satisfying the predicate
     */
    public static Parser<Character, Character> chr(Predicate<Character> predicate) {
        return new Parser<>(input -> {
            if (input.isEof()) {
                return Result.failure(input, "End of input");
            }
            char c = input.get();
            if (predicate.test(c)) {
                return Result.success(input.next(), c);
            } else {
                return Result.failure(input, "Unexpected character: " + c);
            }
        });
    }

    /**
     * Parses a specific character.
     *
     * @param c the character to parse
     * @return a parser that parses the specified character
     */
    public static Parser<Character, Character> chr(char c) {
        return chr(ch -> ch == c);
    }

    /**
     * Parses a single digit character.
     *
     * @return a parser that parses a single digit character
     */
    public static Parser<Character, Character> digit() {
        return chr(Character::isDigit);
    }

    /**
     * Parses a number.
     *
     * @return a parser that parses a number and returns it as an integer
     */
    public static Parser<Character, Integer> number() {
        return digit().oneOrMore().map(chars -> Integer.parseInt(chars.stream()
                .map(String::valueOf)
                .collect(Collectors.joining())));
    }

    /**
     * Parses a single letter character.
     *
     * @return a parser that parses a single letter character
     */
    public static Parser<Character, Character> letter() {
        return chr(Character::isLetter);
    }

    /**
     * Parses a single whitespace character.
     *
     * @return a parser that parses a single whitespace character
     */
    public static Parser<Character, Character> whitespace() {
        return chr(Character::isWhitespace);
    }

    /**
     * Parses a specific string.
     *
     * @param str the string to parse
     * @return a parser that parses the specified string
     */
    public static Parser<Character, String> string(String str) {
        return Combinators.sequence(str.chars()
                        .mapToObj(c -> chr((char) c))
                        .toList())
                .map(chars -> chars.stream()
                        .map(String::valueOf)
                        .collect(Collectors.joining()));
    }


    /**
     * Parses a single alphanumeric character.
     *
     * @return a parser that parses a single alphanumeric character
     */
    public static Parser<Character, Character> alphaNum() {
        return chr(Character::isLetterOrDigit);
    }

    /**
     * Parses one or more whitespace characters.
     *
     * @return a parser that parses one or more whitespace characters
     */
    public static Parser<Character, String> space() {
        return whitespace().oneOrMore().map(chars -> chars.stream()
                .map(String::valueOf)
                .collect(Collectors.joining()));
    }

    /**
     * Parses a sequence of letters.
     *
     * @return a parser that parses a sequence of letters
     */
    public static Parser<Character, String> word() {
        return letter().oneOrMore().map(chars -> chars.stream()
                .map(String::valueOf)
                .collect(Collectors.joining()));
    }

    /**
     * Parses an integer, including optional leading sign.
     *
     * @return a parser that parses an integer
     */
    public static Parser<Character, Integer> integer() {
        return choice(List.of(
                chr('+').andR(Parser.pure(true)),
                chr('-').andR(Parser.pure(false)),
                Parser.pure(true))
        ).and(number()).map(s -> value -> {
            String sign = s ? "" : "-";
            return Integer.parseInt(sign + value);
        });
    }
}