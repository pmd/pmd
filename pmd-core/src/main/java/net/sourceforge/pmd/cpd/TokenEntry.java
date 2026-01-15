/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import net.sourceforge.pmd.lang.document.FileId;
import net.sourceforge.pmd.lang.document.FileLocation;
import net.sourceforge.pmd.lang.document.TextRange2d;

/**
 * A token of a lexed source file (for CPD usage only).
 */
public class TokenEntry implements Comparable<TokenEntry> {
    // note: in CPD, these are only built for tokens that will be reported, otherwise we use SmallTokenEntry

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

    public FileLocation getLocation() {
        return FileLocation.range(fileId, TextRange2d.range2d(beginLine, beginColumn, endLine, endColumn));
    }

    /** The line number where this token starts, inclusive. */
    public int getBeginLine() {
        return beginLine;
    }

    /** The line number where this token ends, inclusive. */
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

    @Override
    public int hashCode() {
        return 31 * this.fileIdInternal + this.index;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (!(o instanceof TokenEntry)) {
            return false;
        }
        TokenEntry other = (TokenEntry) o;
        return other.fileIdInternal == this.fileIdInternal && other.index == this.index;
    }

    @Override
    public int compareTo(TokenEntry other) {
        /* Note that here we compare the file id, not the internal ID, so that this ordering is stable across runs. */
        int cmp = getFileId().compareTo(other.getFileId());
        cmp = cmp != 0 ? cmp : Integer.compare(getLocalIndex(), other.getLocalIndex());
        return cmp;
    }

    final void setImageIdentifier(int identifier) {
        this.identifier = identifier;
    }

    final int getIdentifier() {
        return identifier;
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

    int getLocalIndex() {
        return index;
    }
}
