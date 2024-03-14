/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.LexException;
import net.sourceforge.pmd.lang.document.FileLocation;
import net.sourceforge.pmd.lang.document.TextDocument;

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
