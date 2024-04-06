/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.impl;

import java.util.Objects;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.ast.AstInfo;
import net.sourceforge.pmd.lang.ast.GenericToken;
import net.sourceforge.pmd.lang.document.Chars;
import net.sourceforge.pmd.lang.document.TextDocument;

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
    private AstInfo<?> astInfo;

    public TokenDocument(TextDocument textDocument) {
        this.textDocument = textDocument;
    }

    /**
     * Returns the object holding information about the
     * parse tree. Filled-in after parsing.
     */
    public @NonNull AstInfo<?> getAstInfo() {
        return Objects.requireNonNull(astInfo);
    }

    /**
     * Set the ast info after parsing. Only meant to be used by a {@link Parser}
     * implementation.
     */
    protected void setAstInfo(AstInfo<?> astInfo) {
        this.astInfo = Objects.requireNonNull(astInfo);
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


}
