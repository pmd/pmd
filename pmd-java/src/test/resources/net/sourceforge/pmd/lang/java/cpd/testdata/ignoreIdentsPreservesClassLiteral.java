package foo.bar.baz;
public class Foo {
    Foo() {
    }
    public void bar() {
        Bar.baz(Foo.class, () -> {});

    }
}
