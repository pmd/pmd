/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.document;

/**
 * Represents an operation in a document which will be managed by
 * {@link DocumentOperationsApplierForNonOverlappingRegions}.
 */
public abstract class DocumentOperation {

    /**
     * The region to which this operations belongs
     */
    private final TextRegion.RegionByLine regionByLine;

    public DocumentOperation(final int beginLine, final int endLine, final int beginColumn, final int endColumn) {
        regionByLine = TextRegion.newRegionByLine(beginLine, beginColumn, endLine, endColumn);
    }

    /**
     * Apply this operation to the specified document
     * @param document the document to which apply the operation
     */
    public abstract void apply(Document document);

    public TextRegion.RegionByLine getRegionByLine() {
        return regionByLine;
    }
}
