/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import net.sourceforge.pmd.lang.swift.antlr4.SwiftLexer;

/**
 * SwiftTokenizer
 */

public class SwiftTokenizer extends AntlrTokenizer {
    public SwiftTokenizer() {
        super(new SwiftLexer(null));
    }
}
