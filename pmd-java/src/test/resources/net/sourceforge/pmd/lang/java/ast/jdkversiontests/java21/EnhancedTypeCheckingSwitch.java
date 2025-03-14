/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

/**
 * @see <a href="https://openjdk.org/jeps/441">JEP 441: Pattern Matching for switch</a>
 */
public class EnhancedTypeCheckingSwitch {

    // As of Java 21
    // Selector expression typing
    record Point(int i, int j) {}
    enum Color { RED, GREEN, BLUE; }

    static void typeTester(Object obj) {
        switch (obj) {
            case null     -> System.out.println("null");
            case String s -> System.out.println("String");
            case Color c  -> System.out.println("Color: " + c.toString());
            case Point p  -> System.out.println("Record class: " + p.toString());
            case int[] ia -> System.out.println("Array of ints of length" + ia.length);
            default       -> System.out.println("Something else");
        }
    }

    public static void main(String[] args) {
        Object o = "test";
        typeTester(o);
        typeTester(Color.BLUE);

        o = new int[] {1, 2, 3, 4};
        typeTester(o);

        o = new Point(7, 8);
        typeTester(o);

        o = new Object();
        typeTester(o);
    }
}
