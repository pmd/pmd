/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.apex.ast;

public interface TrailingCommaNode {
    boolean isTrailingComma();

    void setTrailingComma(boolean tailingComma);
}
