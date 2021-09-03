/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.impl;

import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.lang.ast.GenericToken;
import net.sourceforge.pmd.lang.ast.SourceCodePositioner;
import net.sourceforge.pmd.util.StringUtil;

/**
 * Token layer of a parsed file.
 */
@Experimental
public abstract class TokenDocument<T extends GenericToken> {

    private final String fullText;
    private final SourceCodePositioner positioner;

    public TokenDocument(String fullText) {
        this.fullText = fullText;
        positioner = new SourceCodePositioner(fullText);
    }

    /** Returns the original text of the file (without escaping). */
    public String getFullText() {
        return fullText;
    }

    public int lineNumberFromOffset(int offset) {
        return positioner.lineNumberFromOffset(offset);
    }

    public int columnFromOffset(int offsetInclusive) {
        return StringUtil.columnNumberAt(fullText, offsetInclusive);
    }

    /**
     * Returns the first token of the token chain.
     *
     * @throws IllegalStateException If the document has not been parsed yet
     */
    public abstract T getFirstToken();


}
