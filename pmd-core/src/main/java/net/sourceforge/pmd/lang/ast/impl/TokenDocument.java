/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.impl;

/**
 * TODO double link tokens and stuff.
 *
 * @author Clément Fournier
 */
public class TokenDocument {

    private final RichCharSequence fullText;

    public TokenDocument(RichCharSequence fullText) {
        this.fullText = fullText;
    }

    public RichCharSequence getFullText() {
        return fullText;
    }
}
