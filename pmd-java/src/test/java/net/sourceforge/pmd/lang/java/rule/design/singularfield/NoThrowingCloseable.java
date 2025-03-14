/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.design.singularfield;

public interface NoThrowingCloseable extends AutoCloseable {
    @Override
    void close();
}
