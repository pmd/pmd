/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.document;

import net.sourceforge.pmd.document.TextRegion.RegionByLine;
import net.sourceforge.pmd.document.TextRegion.RegionByOffset;
import net.sourceforge.pmd.lang.ast.SourceCodePositioner;


class DocumentImpl implements Document {

    /** The positioner has the original source file. */
    SourceCodePositioner positioner;


    DocumentImpl(final CharSequence source) {
        positioner = new SourceCodePositioner(source);
    }

    @Override
    public MutableDocument newMutableDoc(ReplaceHandler out) {
        return new MutableDocumentImpl(getText(), out);
    }

    @Override
    public RegionByLine mapToLine(RegionByOffset region) {
        int bline = positioner.lineNumberFromOffset(region.getStartOffset());
        int bcol = positioner.columnFromOffset(bline, region.getStartOffset());
        int eline = positioner.lineNumberFromOffset(region.getOffsetAfterEnding());
        int ecol = positioner.columnFromOffset(eline, region.getOffsetAfterEnding());

        return createRegion(bline, bcol, eline, ecol);
    }

    @Override
    public RegionByOffset mapToOffset(RegionByLine region) {
        int offset = positioner.offsetFromLineColumn(region.getBeginLine(), region.getBeginColumn());
        int len = positioner.offsetFromLineColumn(region.getEndLine(), region.getEndColumn())
            - offset;

        return createRegion(offset, len);
    }

    @Override
    public RegionByLine createRegion(int beginLine, int beginColumn, int endLine, int endColumn) {
        // TODO checks, positioner should return -1 if not found
        return new RegionByLineImpl(beginLine, beginColumn, endLine, endColumn);
    }

    @Override
    public RegionByOffset createRegion(int offset, int length) {
        if (offset < 0 || offset + length > positioner.getSourceCode().length()) {
            throw new IndexOutOfBoundsException(
                "Region (" + offset + ",+" + length + ") is not in range of this document");
        }

        return new RegionByOffsetImpl(offset, length);
    }

    @Override
    public CharSequence getText() {
        return positioner.getSourceCode();
    }

    @Override
    public CharSequence subSequence(TextRegion region) {
        RegionByOffset byOffset = region.toOffset(this);
        return getText().subSequence(byOffset.getStartOffset(), byOffset.getOffsetAfterEnding());
    }

}
