/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

/**
 * @see <a href="https://openjdk.java.net/jeps/406">JEP 406: Pattern Matching for switch (Preview)</a>
 */
public class DealingWithNull {


    static void test(Object o) {
        switch (o) {
            case null     -> System.out.println("null!");
            case String s -> System.out.println("String");
            default       -> System.out.println("Something else");
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
            case null: case String s: 
                System.out.println("String, including null");
                break;
            default:
                System.out.println("default case");
                break;
        }

        switch(o) {
            case null, String s -> System.out.println("String, including null");
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
        test("test");
        test2(2);
        try {
            test2(null);
        } catch (NullPointerException e) {
            System.out.println(e);
        }
        test3(3);
        test3("test");
        test3(null);
    }
}
