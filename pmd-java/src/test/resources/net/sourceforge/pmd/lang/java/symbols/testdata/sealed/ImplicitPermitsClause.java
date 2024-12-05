package net.sourceforge.pmd.lang.java.symbols.testdata.sealed;

/*
 * This class is in resources because it uses Java 17 features.
 * It must be compiled manually, the generated class files and
 * this source file are put into the test/resources directory.
 */


sealed class ImplicitPermitsClause { }
sealed interface ImplicitPermitsClauseItf { }

class Foo {
    non-sealed class Bar extends ImplicitPermitsClause implements ImplicitPermitsClauseItf {}
}

final class Qux extends ImplicitPermitsClause {
    static {
        new Foo().new Bar() {};
    }
}

non-sealed interface SubItf extends ImplicitPermitsClauseItf {}

sealed interface SubItf2 extends SubItf {}

record FooRecord() implements SubItf2 {}

enum FooEnum implements SubItf2 {
    A { },
    B
}