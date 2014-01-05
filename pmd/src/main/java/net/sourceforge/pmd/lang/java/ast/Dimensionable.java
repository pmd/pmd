/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.ast;

public interface Dimensionable {
    boolean isArray();

    int getArrayDepth();
}
