/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.eclipse.ui.quickfix;

public interface Fix {
    // FIXME PMD 5.0

    String fix(String code, int lineNumber);

    String getLabel();
}