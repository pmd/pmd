/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package javasymbols.testdata;

/**
 * Has static and protected methods with the same name, for calls through
 * a type name from a subclass in another package:
 * {@link javasymbols.testdata.impls.SubclassOfProtectedOverloads}.
 */
public class ProtectedOverloads {

    public static void m(int x) { }

    protected void m(Object o) { }

    public static void s(int x) { }

    protected static void s(Object o) { }
}
