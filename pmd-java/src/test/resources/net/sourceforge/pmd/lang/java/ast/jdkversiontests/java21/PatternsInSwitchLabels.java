/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

/**
 * @see <a href="https://openjdk.org/jeps/441">JEP 441: Pattern Matching for switch</a>
 */
public class PatternsInSwitchLabels {


    public static void main(String[] args) {
        Object o = 123L;
        String formatted = switch (o) {
            case Integer i -> "int %d".formatted(i);
            case Long l    -> "long %d".formatted(l);
            case Double d  -> "double %f".formatted(d);
            case String s  -> "String %s".formatted(s);
            default        -> o.toString();
        };
        System.out.println(formatted);
    }
}
