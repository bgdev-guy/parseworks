

import io.github.parseworks.*;

import java.util.function.BinaryOperator;

import static io.github.parseworks.Text.chr;

public class CalculatorParser {

    public static Parser<Character, Integer> number() {
        return Text.number().map(Integer::valueOf);
    }

    public static Parser<Character, BinaryOperator<Integer>> operator() {
        return Combinators.or(
            chr('+').map(op -> Integer::sum),
            chr('-').map(op -> (a, b) -> a - b),
            chr('*').map(op -> (a, b) -> (Integer) a * (Integer) b),
            chr('/').map(op -> (a, b) -> (Integer) a / (Integer) b)
        );
    }

    public static Parser<Character, Integer> expression() {
        return term().chainl1(operator());
    }

    public static Parser<Character, Integer> term() {
        return Combinators.or(
            number(),
            chr('(').andR(expression()).andL(chr(')'))
        );
    }

    public static void main(String[] args) {
        Parser<Character, Integer> parser = expression();
        Input<Character> input = Input.of("3+(2*4)-5");
        Result<Character, Integer> result = parser.parse(input);
        if (result.isSuccess()) {
            System.out.println("Result: " + result.getOrThrow());
        } else {
            System.out.println("Parsing failed: " + result.getError());
        }
    }
}