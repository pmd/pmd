/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

/**
 * @see <a href="https://openjdk.org/jeps/432">JEP 432: Record Patterns (Second Preview)</a>
 */
public class RecordPatterns {

    record Point(int x, int y) {}
    enum Color { RED, GREEN, BLUE }
    record ColoredPoint(Point p, Color c) {}
    record Rectangle(ColoredPoint upperLeft, ColoredPoint lowerRight) {}

    void printSum1(Object o) {
        if (o instanceof Point p) {
            int x = p.x();
            int y = p.y();
            System.out.println(x+y);
        }
    }

    // record pattern
    void printSum2(Object o) {
        if (o instanceof Point(int x, int y)) {
            System.out.println(x+y);
        }
    }

    void printUpperLeftColoredPoint(Rectangle r) {
        if (r instanceof Rectangle(ColoredPoint ul, ColoredPoint lr)) {
            System.out.println(ul.c());
        }
    }

    // nested record pattern
    void printColorOfUpperLeftPoint(Rectangle r) {
        if (r instanceof Rectangle(ColoredPoint(Point p, Color c),
                ColoredPoint lr)) {
            System.out.println(c);
        }
    }

    Rectangle createRectangle(int x1, int y1, Color c1, int x2, int y2, Color c2) {
        Rectangle r = new Rectangle(new ColoredPoint(new Point(x1, y1), c1),
                new ColoredPoint(new Point(x2, y2), c2));
        return r;
    }

    // fully nested record pattern, also using "var"
    void printXCoordOfUpperLeftPointWithPatterns(Rectangle r) {
        if (r instanceof Rectangle(ColoredPoint(Point(var x, var y), var c),
                var lr)) {
            System.out.println("Upper-left corner: " + x);
        }
    }

    record Pair(Object x, Object y) {}
    void nestedPatternsCanFailToMatch() {
        Pair p = new Pair(42, 42);
        if (p instanceof Pair(String s, String t)) {
            System.out.println(s + ", " + t);
        } else {
            System.out.println("Not a pair of strings");
        }
    }

    // record patterns with generic types
    record Box<T>(T t) {}
    void test1a(Box<Object> bo) {
        if (bo instanceof Box<Object>(String s)) {
            System.out.println("String " + s);
        }
    }
    void test1(Box<String> bo) {
        if (bo instanceof Box<String>(var s)) {
            System.out.println("String " + s);
        }
    }

    // type argument is inferred
    void test2(Box<String> bo) {
        if (bo instanceof Box(var s)) {    // Inferred to be Box<String>(var s)
            System.out.println("String " + s);
        }
    }

    // nested record patterns
    void test3(Box<Box<String>> bo) {
        if (bo instanceof Box<Box<String>>(Box(var s))) {
            System.out.println("String " + s);
        }
    }

    void test4(Box<Box<String>> bo) {
        if (bo instanceof Box(Box(var s))) {
            System.out.println("String " + s);
        }
    }
}
