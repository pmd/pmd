/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

public @interface Foo {
    static final ThreadLocal<Interner<Integer>> interner =
        ThreadLocal.withInitial(Interners::newStrongInterner);
}
