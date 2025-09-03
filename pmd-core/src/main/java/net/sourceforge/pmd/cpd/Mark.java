/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.util.Objects;

import org.checkerframework.checker.nullness.qual.NonNull;

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
    private final @NonNull TokenEntry endToken;

    Mark(@NonNull TokenEntry token) {
        this(token, token);
    }

    Mark(@NonNull TokenEntry token, @NonNull TokenEntry endToken) {
        assert endToken.getFileId().equals(token.getFileId())
            : "Tokens are not from the same file";
        this.token = token;
        this.endToken = endToken;
    }

    @NonNull TokenEntry getToken() {
        return this.token;
    }

    @NonNull TokenEntry getEndToken() {
        return endToken;
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

    /** The number of lines spanned by this mark. At least 1. */
    public int getLineCount() {
        return endToken.getEndLine() - token.getBeginLine() + 1;
    }

    FileId getFileId() {
        return token.getFileId();
    }

    /** Return the index in the file of the first token in this mark. */
    public int getBeginTokenIndex() {
        return this.token.getLocalIndex();
    }

    /** Return the index in the file of the last token in this mark. */
    public int getEndTokenIndex() {
        return getEndToken().getLocalIndex();
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
        return Objects.equals(token, other.token)
            && Objects.equals(endToken, other.endToken);
    }

    @Override
    public int compareTo(Mark other) {
        int cmp = token.compareTo(other.token);
        cmp = cmp != 0 ? cmp : endToken.compareTo(other.endToken);
        return cmp;
    }

}
