/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.xml.antlr4;

import org.antlr.v4.runtime.CharStream;

/**
 * Backwards compatible bridge. The XMLLexer was moved to align it with other PMD modules.
 * This class will be removed in PMD 8.0.0.
 * Use {@link net.sourceforge.pmd.lang.xml.ast.XMLLexer} directly instead.
 *
 * @deprecated
 */
@Deprecated
public class XMLLexer extends net.sourceforge.pmd.lang.xml.ast.XMLLexer {
    public XMLLexer(CharStream input) {
        super(input);
    }
}
