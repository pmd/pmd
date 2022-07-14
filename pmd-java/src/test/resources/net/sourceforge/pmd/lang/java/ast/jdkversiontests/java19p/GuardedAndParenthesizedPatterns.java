/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

/**
 * @see <a href="https://openjdk.org/jeps/427">JEP 427: Pattern Matching for switch (Third Preview)</a>
 */
public class GuardedAndParenthesizedPatterns {


    static void test(Object o) {
        switch (o) {
            case String s when s.length() == 1    -> System.out.println("single char string");
            case String s                         -> System.out.println("string");
            case Integer i when i.intValue() == 1 -> System.out.println("integer 1");
            case (Long l) when l.longValue() == 1L -> System.out.println("long 1 with parens");
            case (((Double d)))                   -> System.out.println("double with parens");
            default                               -> System.out.println("default case");
        }
    }

    // verify that "when" can still be used as an identifier
    void testIdentifierWhen(String when) {
        System.out.println(when);
    }

    // verify that "when" can still be used as an identifier
    void testIdentifierWhen() {
        int when = 1;
        System.out.println(when);
    }

    // verify that "when" can still be used as a type name
    private static class when {}

    static void testWithNull(Object o) {
        switch (o) {
            case String s when (s.length() == 1)    -> System.out.println("single char string");
            case String s                           -> System.out.println("string");
            case (Integer i) when i.intValue() == 1 -> System.out.println("integer 1");
            case ((Long l)) when ((l.longValue() == 1L)) -> System.out.println("long 1 with parens");
            case (((Double d)))                   -> System.out.println("double with parens");
            case null   -> System.out.println("null!");
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

        // note: with this 3rd preview, the following is not allowed anymore:
        // if (o instanceof (String s && s.length() > 4)) {
        //  > An alternative to guarded pattern labels is to support guarded patterns directly as a special pattern form,
        //  > e.g. p && e. Having experimented with this in previous previews, the resulting ambiguity with boolean
        //  > expressions have lead us to prefer when clauses in pattern switches.
        if ((o instanceof String s) && (s.length() > 4)) {
            System.out.println("A string containing at least four characters");
        }
    }

    public static void main(String[] args) {
        test("a");
        test("fooo");
        test(1);
        test(1L);
        instanceOfPattern("abcde");
        try {
            test(null); // throws NPE
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        testWithNull(null);
    }
}
