/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.impl;

import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.lang.ast.SourceCodePositioner;
import net.sourceforge.pmd.util.StringUtil;

/**
 * Maybe this can be used to eg double link tokens, provide an identity
 * for them, idk.
 */
@Experimental
public class TokenDocument {

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

}
