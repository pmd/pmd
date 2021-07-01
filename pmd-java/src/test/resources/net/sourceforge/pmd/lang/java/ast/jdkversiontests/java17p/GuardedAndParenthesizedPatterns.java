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
            default                               -> System.out.println("default case");
        }
    }


    public static void main(String[] args) {
        test("a");
        test("fooo");
        test(1);
        test(1L);
        test(null);
    }
}
