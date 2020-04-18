/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.document;

import java.io.IOException;
import java.io.Reader;

import net.sourceforge.pmd.internal.util.BaseCloseable;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.util.document.io.TextFile;


final class TextDocumentImpl extends BaseCloseable implements TextDocument {

    private final TextFile backend;

    private long curStamp;

    private SourceCodePositioner positioner;
    private Chars text;

    private final LanguageVersion langVersion;

    private final String fileName;

    TextDocumentImpl(TextFile backend, LanguageVersion langVersion) throws IOException {
        this.backend = backend;
        this.curStamp = backend.fetchStamp();
        this.text = backend.readContents().toReadOnly();
        this.langVersion = langVersion;
        this.positioner = null;
        this.fileName = backend.getDisplayName();
    }

    @Override
    public LanguageVersion getLanguageVersion() {
        return langVersion;
    }

    @Override
    public String getDisplayName() {
        return fileName;
    }

    @Override
    protected void doClose() throws IOException {
        backend.close();
    }

    @Override
    public FileLocation toLocation(TextRegion region) {
        checkInRange(region.getStartOffset(), region.getLength());

        if (positioner == null) {
            // if nobody cares about lines, this is not computed
            positioner = new SourceCodePositioner(text);
        }

        int bline = positioner.lineNumberFromOffset(region.getStartOffset());
        int bcol = positioner.columnFromOffset(bline, region.getStartOffset());
        int eline = positioner.lineNumberFromOffset(region.getEndOffset());
        int ecol = positioner.columnFromOffset(eline, region.getEndOffset());

        return new FileLocation(
            fileName,
            bline, bcol,
            eline, ecol
        );
    }

    @Override
    public TextRegion createRegion(int startOffset, int length) {
        checkInRange(startOffset, length);
        return TextRegionImpl.fromOffsetLength(startOffset, length);
    }

    @Override
    public TextRegion createLineRange(int startLineInclusive, int endLineInclusive) {
        if (!positioner.isValidLine(startLineInclusive)
            || !positioner.isValidLine(endLineInclusive)
            || startLineInclusive > endLineInclusive) {
            throw InvalidRegionException.invalidLineRange(startLineInclusive, endLineInclusive, positioner.getLastLine());
        }

        int first = positioner.offsetFromLineColumn(startLineInclusive, 1);
        int last = positioner.offsetOfEndOfLine(endLineInclusive);
        return TextRegionImpl.fromBothOffsets(first, last);
    }

    void checkInRange(int startOffset, int length) {
        if (startOffset < 0) {
            throw InvalidRegionException.negativeQuantity("Start offset", startOffset);
        } else if (length < 0) {
            throw InvalidRegionException.negativeQuantity("Region length", length);
        } else if (startOffset + length > getLength()) {
            throw InvalidRegionException.regionOutOfBounds(startOffset, startOffset + length, getLength());
        }
    }

    @Override
    public int getLength() {
        return text.length();
    }

    @Override
    public Chars getText() {
        return text;
    }

    @Override
    public Reader newReader() {
        return text.newReader();
    }

    long getCurStamp() {
        return curStamp;
    }


    @Override
    public Chars slice(TextRegion region) {
        if (region.getLength() == 0) {
            return Chars.EMPTY;
        }
        return text.subSequence(region.getStartOffset(), region.getEndOffset());
    }

}
