/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import net.sourceforge.pmd.lang.document.FileLocation;
import net.sourceforge.pmd.lang.document.TextDocument;

public interface TokenFactory extends AutoCloseable {

    void recordToken(String image, int startLine, int startCol, int endLine, int endCol);

    default void recordToken(String image, FileLocation location) {
        recordToken(image, location.getStartLine(), location.getStartColumn(), location.getEndLine(), location.getEndColumn());
    }

    void setImage(TokenEntry entry, String newImage);

    TokenEntry peekLastToken();

    @Override
    void close();

    static TokenFactory forFile(TextDocument file, Tokens sink) {
        return new TokenFactory() {
            final String name = file.getPathId();

            @Override
            public void recordToken(String image, int startLine, int startCol, int endLine, int endCol) {
                sink.addToken(image, name, startLine, startCol, endLine, endCol);
            }

            @Override
            public void setImage(TokenEntry entry, String newImage) {
                sink.setImage(entry, newImage);
            }

            @Override
            public TokenEntry peekLastToken() {
                return sink.peekLastToken();
            }

            @Override
            public void close() {
                sink.addEof();
            }
        };
    }
}
