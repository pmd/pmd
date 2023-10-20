/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

/**
 * @see <a href="https://openjdk.org/jeps/443">JEP 443: Unnamed Patterns and Variables (Preview)</a>
 */
class Jep443_UnamedPatternsAndVariables2 {
    record Point(int x, int y) { }
    enum Color { RED, GREEN, BLUE }
    record ColoredPoint(Point p, Color c) { }

    void unnamedPatterns1() {
        ColoredPoint r = new ColoredPoint(new Point(3,4), Color.GREEN);

        if (r instanceof ColoredPoint(Point(int x, int y), _)) {
            System.out.println(x + " " + y);
        }
    }
}
