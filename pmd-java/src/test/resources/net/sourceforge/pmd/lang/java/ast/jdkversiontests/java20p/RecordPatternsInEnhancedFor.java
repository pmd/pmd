/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

/**
 * @see <a href="https://openjdk.org/jeps/432">JEP 432: Record Patterns (Second Preview)</a>
 */
public class RecordPatternsInEnhancedFor {
    record Point(int x, int y) {}
    enum Color { RED, GREEN, BLUE }
    record ColoredPoint(Point p, Color c) {}
    record Rectangle(ColoredPoint upperLeft, ColoredPoint lowerRight) {}

    // record patterns in for-each loop (enhanced for statement)
    static void dump(Point[] pointArray) {
        for (Point(var x, var y) : pointArray) {        // Record Pattern in header!
            System.out.println("(" + x + ", " + y + ")");
        }
    }

    // nested record patterns in enhanced for statement
    static void printUpperLeftColors(Rectangle[] r) {
        for (Rectangle(ColoredPoint(Point p, Color c), ColoredPoint lr): r) {
            System.out.println(c);
        }
    }

    record Pair(Object fst, Object snd){}
    static void exceptionTest() {
        Pair[] ps = new Pair[]{
                new Pair(1,2),
                null,
                new Pair("hello","world")
        };
        for (Pair(var f, var s): ps) {  // Run-time MatchException
            System.out.println(f + " -> " + s);
        }
    }

    public static void main(String[] args) {
        exceptionTest();
    }
}
