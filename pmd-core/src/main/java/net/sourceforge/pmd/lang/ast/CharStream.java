/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast;


import java.io.IOException;

import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccTokenDocument;

/**
 * PMD flavour of character streams used by JavaCC parsers.
 *
 * TODO for when all JavaCC languages are aligned:
 *   * rename methods to match decent naming conventions
 *   * move to impl.javacc package
 */
public interface CharStream {

    /**
     * Returns the next character from the input. After a {@link #backup(int)},
     * some of the already read chars must be spit out again.
     *
     * @return The next character
     *
     * @throws IOException  If the underlying char stream throws
     */
    char readChar() throws IOException;


    /**
     * Calls {@link #readChar()} and returns its value, marking its position
     * as the beginning of the next token. All characters must remain in
     * the buffer between two successive calls to this method to implement
     * backup correctly.
     */
    char BeginToken() throws IOException; // SUPPRESS CHECKSTYLE we'll rename it later


    /**
     * Returns a string made up of characters from the token mark up to
     * to the current buffer position.
     */
    String GetImage(); // SUPPRESS CHECKSTYLE we'll rename it later


    /**
     * Returns an array of characters that make up the suffix of length 'len' for
     * the current token. This is used to build up the matched string
     * for use in actions in the case of MORE. A simple and inefficient
     * implementation of this is as follows :
     *
     * <pre>{@code
     * String t = tokenImage();
     * return t.substring(t.length() - len, t.length()).toCharArray();
     * }</pre>
     *
     * @param len Length of the returned array
     *
     * @return The suffix
     *
     * @throws IndexOutOfBoundsException If len is greater than the length of the
     *                                   current token
     */
    char[] GetSuffix(int len); // SUPPRESS CHECKSTYLE we'll rename it later


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
    void backup(int amount);

    @Deprecated
    int getBeginColumn();

    @Deprecated
    int getBeginLine();


    /** Returns the column number of the last character for the current token. */
    int getEndColumn();


    /** Returns the line number of the last character for current token. */
    int getEndLine();

    // These methods are added by PMD


    /**
     * Returns the token document for the tokens being built. Having it
     * here is the most convenient place for the time being.
     */
    default JavaccTokenDocument getTokenDocument() {
        return null; // for VelocityCharStream
    }


    /** Returns the start offset of the current token (in the original source), inclusive. */
    default int getStartOffset() {
        return -1;
    }


    /** Returns the end offset of the current token (in the original source), exclusive. */
    default int getEndOffset() {
        return -1;
    }

}
