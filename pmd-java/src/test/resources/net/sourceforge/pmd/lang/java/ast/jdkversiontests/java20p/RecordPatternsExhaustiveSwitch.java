/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

/**
 * @see <a href="https://openjdk.org/jeps/432">JEP 432: Record Patterns (Second Preview)</a>
 */
public class RecordPatternsExhaustiveSwitch {
    class A {}
    class B extends A {}
    sealed interface I permits C, D {}
    final class C implements I {}
    final class D implements I {}
    record Pair<T>(T x, T y) {}

    static void test() {
        Pair<A> p1 = null;
        Pair<I> p2 = null;

        switch (p1) {                 // Error!
            case Pair<A>(A a, B b) -> System.out.println("a");
            case Pair<A>(B b, A a) -> System.out.println("a");
            case Pair<A>(A a1, A a2) -> System.out.println("exhaustive now"); // without this case, compile error
        }

        switch (p2) {
            case Pair<I>(I i, C c) -> System.out.println("a");
            case Pair<I>(I i, D d) -> System.out.println("a");
        }

        switch (p2) {
            case Pair<I>(C c, I i) -> System.out.println("a");
            case Pair<I>(D d, C c) -> System.out.println("a");
            case Pair<I>(D d1, D d2) -> System.out.println("a");
        }
    }
}
