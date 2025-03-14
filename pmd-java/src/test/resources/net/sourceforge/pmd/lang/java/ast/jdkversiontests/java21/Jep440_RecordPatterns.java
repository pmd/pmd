/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

/**
 * @see <a href="https://openjdk.org/jeps/440">JEP 440: Record Patterns</a>
 */
class Jep440_RecordPatterns {

    record Point(int x, int y) {}
    // As of Java 21
    static void printSum(Object obj) {
        if (obj instanceof Point(int x, int y)) {
            System.out.println(x+y);
        }
    }

    enum Color { RED, GREEN, BLUE }
    record ColoredPoint(Point p, Color c) {}
    record Rectangle(ColoredPoint upperLeft, ColoredPoint lowerRight) {}
    // As of Java 21
    static void printColorOfUpperLeftPoint(Rectangle r) {
        if (r instanceof Rectangle(ColoredPoint(Point p, Color c),
                                   ColoredPoint lr)) {
            System.out.println(c);
        }
    }

    // As of Java 21
    static void printXCoordOfUpperLeftPointWithPatterns(Rectangle r) {
        if (r instanceof Rectangle(ColoredPoint(Point(var x, var y), var c),
                                   var lr)) {
            System.out.println("Upper-left corner: " + x);
        }
    }

    // As of Java 21
    record Pair(Object x, Object y) {}

    static void patternsCanFailToMatch() {
        Pair p = new Pair(42, 42);

        if (p instanceof Pair(String s, String t)) {
            System.out.println(s + ", " + t);
        } else {
            System.out.println("Not a pair of strings");
        }
    }

    // As of Java 21
    record MyPair<S,T>(S fst, T snd){};

    static void recordInference(MyPair<String, Integer> pair){
        switch (pair) {
            case MyPair(var f, var s) ->
               // Inferred record pattern MyPair<String,Integer>(var f, var s)
               System.out.println("matched");
        }
    }

    // As of Java 21
    record Box<T>(T t) {}

    static void test1(Box<Box<String>> bbs) {
        if (bbs instanceof Box<Box<String>>(Box(var s))) {
            System.out.println("String " + s);
        }
    }
    // As of Java 21
    static void test2(Box<Box<String>> bbs) {
        if (bbs instanceof Box(Box(var s))) {
            System.out.println("String " + s);
        }
    }
}
