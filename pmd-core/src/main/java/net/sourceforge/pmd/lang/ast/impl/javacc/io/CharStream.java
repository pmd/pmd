/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.impl.javacc.io;


import java.io.EOFException;
import java.io.IOException;

import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccTokenDocument;
import net.sourceforge.pmd.util.document.Chars;
import net.sourceforge.pmd.util.document.FileLocation;
import net.sourceforge.pmd.util.document.TextDocument;
import net.sourceforge.pmd.util.document.TextRegion;

/**
 * PMD flavour of character streams used by JavaCC parsers.
 */
public final class CharStream {

    private static final EOFException EOF = new EOFException();

    private final JavaccTokenDocument tokenDoc;
    private final TextDocument textDoc;
    private final Chars chars;
    private final boolean useMarkSuffix;
    private int curOffset;
    private int markOffset = -1;

    private CharStream(JavaccTokenDocument tokenDoc, TextDocument textDoc) {
        this.tokenDoc = tokenDoc;
        this.textDoc = textDoc;
        this.chars = textDoc.getText();
        this.useMarkSuffix = tokenDoc.useMarkSuffix();
    }

    /**
     * Create a new char stream for the given document.
     */
    public static CharStream create(JavaccTokenDocument doc) throws IOException, MalformedSourceException {
        try (EscapeAwareReader reader = doc.newReader(doc.getTextDocument().getText())) {
            return new CharStream(doc, reader.translate(doc.getTextDocument()));
        }
    }

    /**
     * Returns the next character from the input. After a {@link #backup(int)},
     * some of the already read chars must be spit out again.
     *
     * @return The next character
     *
     * @throws EOFException Upon EOF
     */
    public char readChar() throws EOFException {
        if (curOffset == chars.length()) {
            throw EOF;
        }
        return chars.charAt(curOffset++);
    }


    /**
     * Calls {@link #readChar()} and returns its value, marking its position
     * as the beginning of the next token. All characters must remain in
     * the buffer between two successive calls to this method to implement
     * backup correctly.
     */
    public char markTokenStart() throws EOFException {
        markOffset = curOffset;
        return readChar();
    }


    /**
     * Returns a string made up of characters from the token mark up to
     * to the current buffer position.
     */
    public String getTokenImage() {
        return getTokenImageCs().toString();
    }

    /**
     * Returns a string made up of characters from the token mark up to
     * to the current buffer position.
     */
    public Chars getTokenImageCs() {
        assert markOffset >= 0;
        return chars.slice(markOffset, markLen());
    }

    private int markLen() {
        return curOffset - markOffset;
    }


    /**
     * Appends the suffix of length 'len' of the current token to the given
     * string builder. This is used to build up the matched string
     * for use in actions in the case of MORE.
     *
     * @param len Length of the returned array
     *
     * @throws IndexOutOfBoundsException If len is greater than the length of the current token
     */
    public void appendSuffix(StringBuilder sb, int len) {
        if (useMarkSuffix) {
            assert len <= markLen() : "Suffix is greater than the mark length? " + len + " > " + markLen();
            chars.appendChars(sb, curOffset - len, len);
        } // otherwise dead code, kept because Javacc's argument expressions do side effects
    }


    /**
     * Pushes a given number of already read chars into the buffer.
     * Subsequent calls to {@link #readChar()} will read those characters
     * before proceeding to read the underlying char stream.
     *
     * <p>A lexer calls this method if it has already read some characters,
     * but cannot use them to match a (longer) token. So, they will
     * be used again as the prefix of the next token.
     *
     * @throws AssertionError If the requested amount is greater than the
     *                        number of read chars
     */
    public void backup(int amount) {
        curOffset -= amount;
    }

    /**
     * Returns the column number of the last character for the current token.
     * This is only used for parse exceptions and is very inefficient.
     */
    public int getEndColumn() {
        return endLocation().getEndColumn();
    }


    /**
     * Returns the line number of the last character for current token.
     * This is only used for parse exceptions and is very inefficient.
     */
    public int getEndLine() {
        return endLocation().getEndLine();
    }


    private FileLocation endLocation() {
        return textDoc.toLocation(TextRegion.caretAt(getEndOffset()));
    }


    /** Returns the start offset of the current token (in the original source), inclusive. */
    public int getStartOffset() {
        return textDoc.translateOffset(markOffset);
    }


    /** Returns the end offset of the current token (in the original source), exclusive. */
    public int getEndOffset() {
        return textDoc.translateOffset(curOffset);
    }


    /**
     * Returns the token document for the tokens being built. Having it
     * here is the most convenient place for the time being.
     */
    public JavaccTokenDocument getTokenDocument() {
        return tokenDoc;
    }

}
