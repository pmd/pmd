/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.impl;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.ast.GenericToken;
import net.sourceforge.pmd.lang.document.Chars;
import net.sourceforge.pmd.lang.document.TextDocument;
import net.sourceforge.pmd.lang.document.TextRegion;

/**
 * Token layer of a parsed file.
 * This object is used to store state global to all tokens of a single file,
 * e.g. the text document. Not all languages currently have an implementation
 * of a token document.
 *
 * @see net.sourceforge.pmd.lang.ast.impl.javacc.JavaccTokenDocument
 */
public abstract class TokenDocument<T extends GenericToken<T>> {

    private final TextDocument textDocument;

    public TokenDocument(TextDocument textDocument) {
        this.textDocument = textDocument;
    }

    /** Returns the original text of the file (without escaping). */
    public Chars getFullText() {
        return textDocument.getText();
    }

    public TextDocument getTextDocument() {
        return textDocument;
    }

    /**
     * Returns the first token of the token chain.
     *
     * @throws IllegalStateException If the document has not been parsed yet
     */
    public abstract T getFirstToken();

    /**
     * Translate a region into the source coordinates of the document.
     *
     * @see TextDocument#inputRegion(TextRegion)
     */
    public final @NonNull TextRegion inputRegion(TextRegion outputRegion) {
        return getTextDocument().inputRegion(outputRegion);
    }

}
