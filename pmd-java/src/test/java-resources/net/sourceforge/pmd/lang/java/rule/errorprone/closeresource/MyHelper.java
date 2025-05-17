/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone.closeresource;

public final class MyHelper {

    private MyHelper() { }

    public static void close(Statement s) { }

    public static void myClose(Statement s) { }
}
