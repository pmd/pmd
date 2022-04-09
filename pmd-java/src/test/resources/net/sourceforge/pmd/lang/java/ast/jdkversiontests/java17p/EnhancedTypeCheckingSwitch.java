/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

/**
 * @see <a href="https://openjdk.java.net/jeps/406">JEP 406: Pattern Matching for switch (Preview)</a>
 */
public class EnhancedTypeCheckingSwitch {


    static void typeTester(Object o) {
        switch (o) {
            case null     -> System.out.println("null");
            case String s -> System.out.println("String");
            case Color c  -> System.out.println("Color with " + c.values().length + " values");
            case Point p  -> System.out.println("Record class: " + p.toString());
            case int[] ia -> System.out.println("Array of ints of length" + ia.length);
            default       -> System.out.println("Something else");
        }
    }

    public static void main(String[] args) {
        Object o = "test";
        typeTester(o);
    }
}

record Point(int i, int j) {}
enum Color { RED, GREEN, BLUE; }
