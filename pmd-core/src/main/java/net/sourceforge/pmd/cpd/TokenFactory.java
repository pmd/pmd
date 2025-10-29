/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.LexException;
import net.sourceforge.pmd.lang.document.FileLocation;
import net.sourceforge.pmd.lang.document.TextDocument;
import net.sourceforge.pmd.lang.document.TextRegion;

/**
 * Proxy to record tokens from within {@link CpdLexer#tokenize(TextDocument, TokenFactory)}.
 */
public interface TokenFactory extends AutoCloseable {

    /**
     * Record a token given its coordinates. Coordinates must match the
     * requirements of {@link FileLocation}, ie, be 1-based and ordered
     * properly.
     *
     * @param image     Image of the token. This will be taken into account
     *                  to determine the hash value of the token.
     * @param startLine Start line of the token
     * @param startCol  Start column of the token
     * @param endLine   End line of the token
     * @param endCol    End column of the token
     */
    void recordToken(@NonNull String image, int startLine, int startCol, int endLine, int endCol);

    /**
     * Record a token given its offset coordinates. This is more
     * space-efficient as start/end line/col coordinates, so it is
     * recommended your lexer use this overload. You cannot mix
     * calls to this method and calls to {@link #recordToken(String, int, int, int, int)},
     * you need to pick one of the two coordinate modes.
     *
     * @implNote The default implementation throws an exception, please
     * implement it. This method will be made abstract in a future version of CPD.
     *
     * @param image       Image of the token. This will be taken into account
     *                    to determine the hash value of the token.
     * @param startOffset Start offset
     * @param endOffset   End offset
     */
    default void recordToken(@NonNull String image, int startOffset, int endOffset) {
        throw new UnsupportedOperationException("TODO next major version: make this abstract.");
    }

    /**
     * Record a token using its offset coordinates.
     *
     * @param image  Image of the token. This will be taken into account
     *               to determine the hash value of the token.
     * @param region Text region for the coordinates
     * @see #recordToken(String, int, int)
     */
    default void recordToken(@NonNull String image, TextRegion region) {
        recordToken(image, region.getStartOffset(), region.getEndOffset());
    }

    /**
     * Record a token given its coordinates. Coordinates must match the
     * requirements of {@link FileLocation}, ie, be 1-based and ordered
     * properly.
     *
     * @param image    Image of the token. This will be taken into account
     *                 to determine the hash value of the token.
     * @param location Location of the token.
     */
    default void recordToken(@NonNull String image, @NonNull FileLocation location) {
        recordToken(image, location.getStartLine(), location.getStartColumn(), location.getEndLine(), location.getEndColumn());
    }

    LexException makeLexException(int line, int column, String message, @Nullable Throwable cause);

    /**
     * Sets the image of an existing token entry.
     */
    void setImage(TokenEntry entry, @NonNull String newImage);

    /**
     * Returns the last token that has been recorded in this file.
     */
    @Nullable TokenEntry peekLastToken();

    /**
     * This adds the EOF token, it must be called when
     * {@link CpdLexer#tokenize(TextDocument, TokenFactory)} is done.
     */
    @Override
    void close();

}
