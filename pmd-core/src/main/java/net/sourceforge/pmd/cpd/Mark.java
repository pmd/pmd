/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.util.Objects;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.document.FileLocation;
import net.sourceforge.pmd.lang.document.TextRange2d;

/**
 * A range of tokens in a source file, identified by a start and end
 * token (both included in the range). The start and end token may be
 * the same token.
 */
public final class Mark implements Comparable<Mark> {

    private final @NonNull TokenEntry token;
    private @Nullable TokenEntry endToken;
    private String fileDisplayName;

    Mark(@NonNull TokenEntry token) {
        this.token = token;
    }

    @NonNull TokenEntry getToken() {
        return this.token;
    }

    @NonNull TokenEntry getEndToken() {
        return endToken == null ? token : endToken;
    }

    /**
     * Return the location of this source range in the source file.
     */
    public FileLocation getLocation() {
        TokenEntry endToken = getEndToken();
        return FileLocation.range(
            getFileName(),
            TextRange2d.range2d(token.getBeginLine(), token.getBeginColumn(),
                                endToken.getEndLine(), endToken.getEndColumn()));
    }

    String getFileName() {
        if (fileDisplayName == null) {
            return token.getFilePathId();
        }
        return fileDisplayName;
    }

    public int getBeginTokenIndex() {
        return this.token.getIndex();
    }

    public int getEndTokenIndex() {
        return getEndToken().getIndex();
    }

    void setEndToken(@NonNull TokenEntry endToken) {
        assert endToken.getFilePathId().equals(token.getFilePathId())
            : "Tokens are not from the same file";
        this.endToken = endToken;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + token.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Mark other = (Mark) obj;
        return Objects.equals(token, other.token)
            && Objects.equals(endToken, other.endToken);
    }

    @Override
    public int compareTo(Mark other) {
        return getToken().compareTo(other.getToken());
    }

    public void setFileDisplayName(String fileDisplayName) {
        this.fileDisplayName = fileDisplayName;
    }
}
