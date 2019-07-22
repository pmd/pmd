/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.impl;

import net.sourceforge.pmd.annotation.Experimental;

/**
 * Maybe this can be used to eg double link tokens, provide an identity
 * for them, idk.
 */
@Experimental
public class TokenDocument {

    private final String fullText;

    public TokenDocument(String fullText) {
        this.fullText = fullText;
    }

    /** Returns the original text of the file (without escaping). */
    public String getFullText() {
        return fullText;
    }
}
