package foo.bar.baz;
public class Foo extends Bar {

    private Foo notAConstructor;

    public Foo(int i) { super(i); }

    private Foo(int i, String s) { super(i, s); }

    /* default */ Foo(int i, String s, Object o) { super(i, s, o); }

    private static class Inner {

        Inner() { System.out.println("Guess who?"); }

    }
}
