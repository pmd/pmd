package net.sourceforge.pmd.lang.java.symbols.testdata.sealed;

/*
 * This class is in resources because it uses Java 17 features.
 * It must be compiled manually, the generated class files and
 * this source file are put into the test/resources directory.
 */

sealed interface SealedTypesTestData permits A, B, C {
}

sealed interface A extends SealedTypesTestData permits X {
}

non-sealed interface B extends SealedTypesTestData {
}

final class C implements SealedTypesTestData {
}

final class X implements A {
}