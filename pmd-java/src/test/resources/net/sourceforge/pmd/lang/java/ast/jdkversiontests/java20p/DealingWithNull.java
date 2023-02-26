/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

/**
 * @see <a href="https://openjdk.org/jeps/433">JEP 433: Pattern Matching for switch (Fourth Preview)</a>
 */
public class DealingWithNull {

    static void testFooBar(String s) {
        switch (s) {
            case null         -> System.out.println("Oops");
            case "Foo", "Bar" -> System.out.println("Great"); // CaseConstant
            default           -> System.out.println("Ok");
        }
    }

    static void testStringOrNull(Object o) {
        switch (o) {
            case String s       -> System.out.println("String: " + s); // CasePattern
            case null           -> System.out.println("null");
            default             -> System.out.println("default case");
        }
    }

    static void testStringOrDefaultNull(Object o) {
        switch (o) {
            case String s       -> System.out.println("String: " + s);
            case null, default  -> System.out.println("null or default case");
        }
    }

    static void test2(Object o) {
        switch (o) {
            case null      -> throw new NullPointerException();
            case String s  -> System.out.println("String: "+s);
            case Integer i -> System.out.println("Integer");
            default  -> System.out.println("default");
        }
    }


    static void test3(Object o) {
        switch(o) {
            case null:
                System.out.println("null");
                break; // note: fall-through to a CasePattern is not allowed, as the pattern variable is not initialized
            case String s:
                System.out.println("String");
                break;
            default:
                System.out.println("default case");
                break;
        }

        switch(o) {
            case null -> System.out.println("null");
            case String s -> System.out.println("String");
            default -> System.out.println("default case");
        }

        switch(o) {
            case null: default: 
                System.out.println("The rest (including null)");
        }

        switch(o) {
            case null, default -> 
                System.out.println("The rest (including null)");
        }
    }

    public static void main(String[] args) {
        testStringOrDefaultNull("test");
        test2(2);
        try {
            test2(null);
        } catch (NullPointerException e) {
            System.out.println(e);
        }
        test3(3);
        test3("test");
        test3(null);

        testFooBar(null);
        testFooBar("Foo");
        testFooBar("Bar");
        testFooBar("baz");

        testStringOrNull(null);
        testStringOrNull("some string");
    }
}
