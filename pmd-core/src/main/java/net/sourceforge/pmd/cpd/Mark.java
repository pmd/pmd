/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.util.Objects;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.document.FileId;
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

    Mark(@NonNull TokenEntry token) {
        this.token = token;
    }

    Mark(@NonNull TokenEntry token, @NonNull TokenEntry endToken) {
        this.token = token;
        this.endToken = endToken;
    }

    @NonNull TokenEntry getToken() {
        return this.token;
    }

    @NonNull TokenEntry getEndToken() {
        return endToken == null ? token : endToken;
    }

    /** Length in tokens. */
    public int getLength() {
        return getEndToken().getLocalIndex() - getToken().getLocalIndex() + 1;
    }

    /**
     * Return the location of this source range in the source file.
     */
    public FileLocation getLocation() {
        TokenEntry endToken = getEndToken();
        return FileLocation.range(
            getFileId(),
            TextRange2d.range2d(token.getBeginLine(), token.getBeginColumn(),
                                endToken.getEndLine(), endToken.getEndColumn()));
    }

    FileId getFileId() {
        return token.getFileId();
    }

    public int getBeginTokenIndex() {
        return this.token.getLocalIndex();
    }

    public int getEndTokenIndex() {
        return getEndToken().getLocalIndex();
    }

    void setEndToken(@NonNull TokenEntry endToken) {
        assert endToken.getFileId().equals(token.getFileId())
            : "Tokens are not from the same file";
        this.endToken = endToken;
    }

    @Override
    public String toString() {
        return "Mark [token=" + token + ", endToken=" + endToken + "]";
    }

    /***
     * Return -1 if this mark contains the given other mark, 1 if the other contains this one,
     * zero otherwise.
     */
    int contains(Mark that) {
        int thisFile = this.token.getFileIdInternal();
        int thatFile = that.token.getFileIdInternal();
        if (thisFile != thatFile) {
            return 0;
        }
        int thisStart = this.token.getLocalIndex();
        int thatStart = that.token.getLocalIndex();
        if (thisStart <= thatStart && thatStart + that.getLength() <= thisStart + this.getLength()) {
            // this contains that
            return -1;
        } else if (thatStart <= thisStart && thisStart + this.getLength() <= thatStart + that.getLength()) {
            // that contains this
            return 1;
        }
        return 0;
    }

    @Override
    public int hashCode() {
        return token.hashCode();
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
        return Objects.equals(token, other.token);
    }

    @Override
    public int compareTo(Mark other) {
        return getToken().compareTo(other.getToken());
        // cmp = cmp != 0 ? cmp : getEndToken().compareTo(other.getEndToken());
        // return cmp;
    }

}
