/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.impl.antlr4;

import org.antlr.v4.runtime.Token;

final class AntlrUtils {

    private AntlrUtils() {
        // utility class
    }

    static int getEndColumn(Token tok) {
        return tok.getCharPositionInLine() + tok.getStopIndex() - tok.getStartIndex() + 1;
    }

    static int getBeginColumn(Token tok) {
        return tok.getCharPositionInLine() + 1;
    }

}
