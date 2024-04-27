/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import net.sourceforge.pmd.lang.document.FileId;

public class TokenEntry implements Comparable<TokenEntry> {

    private static final int EOF = 0;

    private final FileId fileId;
    private final int beginLine;
    private final int beginColumn;
    private final int endColumn;
    private final int endLine;
    private final int fileIdInternal;
    private int index;
    private int identifier;

    /** constructor for EOF entries. */
    TokenEntry(FileId fileId, int line, int column) {
        assert isOk(line) && isOk(column) : "Coordinates are 1-based";
        this.identifier = EOF;
        this.fileId = fileId;
        this.beginLine = line;
        this.beginColumn = column;
        this.endLine = line;
        this.endColumn = column;
        this.fileIdInternal = 0;
    }

    TokenEntry(int imageId, FileId fileId, int beginLine, int beginColumn, int endLine, int endColumn, int index, int fileIdInternal) {
        assert isOk(beginLine) && isOk(beginColumn) && isOk(endLine) && isOk(endColumn) : "Coordinates are 1-based";
        assert imageId != EOF;
        this.fileId = fileId;
        this.beginLine = beginLine;
        this.beginColumn = beginColumn;
        this.endLine = endLine;
        this.endColumn = endColumn;
        this.identifier = imageId;
        this.index = index;
        this.fileIdInternal = fileIdInternal;
    }

    public boolean isEof() {
        return this.identifier == EOF;
    }

    private boolean isOk(int coord) {
        return coord >= 1;
    }

    int getFileIdInternal() {
        return fileIdInternal;
    }

    FileId getFileId() {
        return fileId;
    }


    /** The line number where this token starts. */
    public int getBeginLine() {
        return beginLine;
    }

    /** The line number where this token ends. */
    public int getEndLine() {
        return endLine;
    }

    /** The column number where this token starts, inclusive. */
    public int getBeginColumn() {
        return beginColumn;
    }

    /** The column number where this token ends, exclusive. */
    public int getEndColumn() {
        return endColumn;
    }

    int getIdentifier() {
        return this.identifier;
    }

    long getIndex() {
        return (long) this.fileIdInternal << 32 | (long) this.index;
    }

    @Override
    public int hashCode() {
        return 31 * this.fileIdInternal + this.index;
    }

    @SuppressWarnings("PMD.CompareObjectsWithEquals")
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (!(o instanceof TokenEntry)) {
            return false;
        }
        TokenEntry other = (TokenEntry) o;
        return other.getIndex() == this.getIndex();
    }

    @Override
    public int compareTo(TokenEntry other) {
        int cmp = getFileId().compareTo(other.getFileId());
        cmp = cmp != 0 ? cmp : Integer.compare(getLocalIndex(), other.getLocalIndex());
        return cmp;
    }

    final void setImageIdentifier(int identifier) {
        this.identifier = identifier;
    }

    public String getImage(Tokens tokens) {
        if (this.isEof()) {
            return "EOF";
        }
        String image = tokens.imageFromId(this.identifier);
        return image == null ? "--unknown--" : image;
    }

    @Override
    public String toString() {
        return "Token(file=" + fileIdInternal + ", index=" + index + ")";
    }

    public int getLocalIndex() {
        return index;
    }
}
