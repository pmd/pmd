/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

/**
 * @see <a href="https://openjdk.java.net/jeps/406">JEP 406: Pattern Matching for switch (Preview)</a>
 */
public class GuardedAndParenthesizedPatterns {


    static void test(Object o) {
        switch (o) {
            case String s && (s.length() == 1)    -> System.out.println("single char string");
            case String s                         -> System.out.println("string");
            case (Integer i && i.intValue() == 1) -> System.out.println("integer 1");
            case (((Long l && l.longValue() == 1L))) -> System.out.println("long 1 with parens");
            case (((Double d)))                   -> System.out.println("double with parens");
            default                               -> System.out.println("default case");
        }
    }

    static void instanceOfPattern(Object o) {
        if (o instanceof String s && s.length() > 2) {
            System.out.println("A string containing at least two characters");
        }
        if (o != null && (o instanceof String s && s.length() > 3)) {
            System.out.println("A string containing at least three characters");
        }
        if (o instanceof (String s && s.length() > 4)) {
            System.out.println("A string containing at least four characters");
        }
    }

    public static void main(String[] args) {
        test("a");
        test("fooo");
        test(1);
        test(1L);
        instanceOfPattern("abcde");
        test(null);
    }
}
