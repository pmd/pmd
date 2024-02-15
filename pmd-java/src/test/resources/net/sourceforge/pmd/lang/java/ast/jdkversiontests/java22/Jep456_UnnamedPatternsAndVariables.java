/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

/**
 * @see <a href="https://openjdk.org/jeps/443">JEP 443: Unnamed Patterns and Variables (Preview)</a> (Java 21)
 * @see <a href="https://openjdk.org/jeps/456">JEP 456: Unnamed Variables & Patterns</a> (Java 22)
 */
class Jep456_UnamedPatternsAndVariables {
    record Point(int x, int y) { }
    enum Color { RED, GREEN, BLUE }
    record ColoredPoint(Point p, Color c) { }

    void unnamedPatterns1() {
        ColoredPoint r = new ColoredPoint(new Point(3,4), Color.GREEN);

        if (r instanceof ColoredPoint(Point p, Color _)) {
            System.out.println(p.x() + " " + p.y());
        }

        if (r instanceof ColoredPoint(Point(int x, int y), _)) {
            System.out.println(x + " " + y);
        }
    }

    sealed abstract class Ball permits RedBall, BlueBall, GreenBall { }
    final  class RedBall   extends Ball { }
    final  class BlueBall  extends Ball { }
    final  class GreenBall extends Ball { }

    record Box<T extends Ball>(T content) { }

    void unnamedPatterns2() {
        Box<? extends Ball> b = new Box<>(new RedBall());
        switch (b) {
            case Box(RedBall   _) -> processBox(b);
            case Box(BlueBall  _) -> processBox(b);
            case Box(GreenBall _) -> stopProcessing();
        }

        switch (b) {
            case Box(RedBall _), Box(BlueBall _) -> processBox(b);
            case Box(GreenBall _)                -> stopProcessing();
            case Box(_)                          -> pickAnotherBox();
        }

        int x = 42;
        switch (b) {
            // multiple patterns guarded by one guard
            case Box(RedBall _), Box(BlueBall _) when x == 42 -> processBox(b);
            case Box(_)                          -> pickAnotherBox();
        }
    }

    private void processBox(Box<? extends Ball> b) {}
    private void stopProcessing() {}
    private void pickAnotherBox() {}

    class Order {}
    private static final int LIMIT = 10;
    private int sideEffect() {
        return 0;
    }

    void unnamedVariables(List<Order> orders) {
        int total = 0;
        for (Order _ : orders) {
            if (total < LIMIT) {
                total++;
            }
        }
        System.out.println("total: " + total);

        for (int i = 0, _ = sideEffect(); i < 10; i++) {
            System.out.println(i);
        }

        Queue<Integer> q = new ArrayDeque<>(); // x1, y1, z1, x2, y2, z2 ..
        while (q.size() >= 3) {
            int x = q.remove();
            int y = q.remove();
            int _ = q.remove(); // z is unused
            Point p = new Point(x, y);
        }
        while (q.size() >= 3) {
            var x = q.remove();
            var _ = q.remove();
            var _ = q.remove();
            Point p = new Point(x, 0);
        }
    }

    static class ScopedContext implements AutoCloseable {
        @Override
        public void close() { }
        public static ScopedContext acquire() {
            return new ScopedContext();
        }
    }

    void unusedVariables2() {
        try (var _ = ScopedContext.acquire()) {
            //... acquiredContext not used ...
        }

        String s = "123";
        try {
            int i = Integer.parseInt(s);
            System.out.println(i);
        } catch (NumberFormatException _) {
            System.out.println("Bad number: " + s);
        } catch (Exception _) {
            System.out.println("error...");
        }

        List.of("a", "b").stream().collect(Collectors.toMap(String::toUpperCase, _ -> "NO_DATA"));
    }
}
