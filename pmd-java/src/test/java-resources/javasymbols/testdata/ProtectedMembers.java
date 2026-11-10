/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package javasymbols.testdata;

/**
 * Has overloads that differ in their access, and a subclass in another
 * package: {@link javasymbols.testdata.impls.SubclassOfProtectedMembers}.
 * For an Integer argument, the protected overload is selected only where
 * it is accessible (JLS 6.6.2), and the public one otherwise.
 */
public class ProtectedMembers {

    public ProtectedMembers(int x) { }

    protected ProtectedMembers(Object o) { }

    public void m(int x) { }

    /** Keeps a reference to m inexact, whether m(Object) is accessible or not. */
    public void m(String s) { }

    protected void m(Object o) { }

    public static void s(int x) { }

    protected static void s(Object o) { }

    public String r(int x) {
        return "";
    }

    protected Integer r(Object o) {
        return 0;
    }

    /**
     * Has two public constructors, so that a reference to its constructors
     * is inexact, whether the protected one is accessible or not.
     */
    public static class Constructors {

        public Constructors(int x) { }

        public Constructors(String s) { }

        protected Constructors(Object o) { }
    }
}
