/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

/**
 * @see <a href="https://openjdk.org/jeps/433">JEP 433: Pattern Matching for switch (Fourth Preview)</a>
 */
public class ExhaustiveSwitch {

    static int coverage(Object o) {
        return switch (o) {
            case String s  -> s.length();
            case Integer i -> i;
            default -> 0;
        };
    }

    static void coverageStatement(Object o) {
        switch (o) {
            case String s:
                System.out.println(s);
                break;
            case Integer i:
                System.out.println("Integer");
                break;
            default:    // Now exhaustive!
                break;
        }
    }
    
    sealed interface S permits A, B, C {}
    final static class A implements S {}
    final static class B implements S {}
    record C(int i) implements S {}  // Implicitly final

    static int testSealedExhaustive(S s) {
        return switch (s) {
            case A a -> 1;
            case B b -> 2;
            case C c -> 3;
        };
    }

    static void switchStatementExhaustive(S s) {
        switch (s) {
            case A a :
                System.out.println("A");
                break;
            case C c :
                System.out.println("C");
                break;
            default:
                System.out.println("default case, should be B");
                break;
        };
    }
    sealed interface I<T> permits E, F {}
    final static class E<X> implements I<String> {}
    final static class F<Y> implements I<Y> {}

    static int testGenericSealedExhaustive(I<Integer> i) {
        return switch (i) {
            // Exhaustive as no E case possible!  
            case F<Integer> bi -> 42;
        };
    }

    public static void main(String[] args) {
        System.out.println(coverage("a string"));
        System.out.println(coverage(42));
        System.out.println(coverage(new Object()));

        coverageStatement("a string");
        coverageStatement(21);
        coverageStatement(new Object());

        System.out.println("A:" + testSealedExhaustive(new A()));
        System.out.println("B:" + testSealedExhaustive(new B()));
        System.out.println("C:" + testSealedExhaustive(new C(1)));

        switchStatementExhaustive(new A());
        switchStatementExhaustive(new B());
        switchStatementExhaustive(new C(2));

        System.out.println("F:" + testGenericSealedExhaustive(new F<Integer>()));
    }
}
