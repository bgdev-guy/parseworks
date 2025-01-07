import io.github.parseworks.*;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.BinaryOperator;

import static io.github.parseworks.Text.chr;

public class CalculatorParser {

    public static Parser<Character, Integer> number() {
        return Text.digit().map(Character::getNumericValue);
    }

    public static Parser<Character, BinaryOperator<Integer>> operator() {
        return Combinators.choice(List.of(
                chr('+').map(op -> Integer::sum),
                chr('-').map(op -> (a, b) -> a - b),
                chr('*').map(op -> (a, b) -> a * b),
                chr('/').map(op -> (a, b) -> a / b)
                ));
    }

    public static Ref<Character, Integer> term = Parser.ref();
    public static Parser<Character, Integer> expression = term.chainl1(operator());

    public static Parser<Character, Integer> term2 = Combinators.choice(List.of(
            number(),
            chr('(').andR(expression).andL(chr(')'))));

    @Test
    public void calculator() {
        term.set(term2);
        Input<Character> input = Input.of("3+(2*4)-5");
        Result<Character, Integer> result = expression.parse(input);
        if (result.isSuccess()) {
            System.out.println("Result: " + result.getOrThrow());
        } else {
            System.out.println("Parsing failed: " + result.getError());
        }
    }


}