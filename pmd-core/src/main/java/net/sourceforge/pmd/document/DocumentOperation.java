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
    private final RegionByLine regionByLine;

    public DocumentOperation(final int beginLine, final int endLine, final int beginColumn, final int endColumn) {
        regionByLine = new RegionByLineImp(beginLine, endLine, beginColumn, endColumn);
    }

    /**
     * Apply this operation to the specified document
     * @param document the document to which apply the operation
     */
    public abstract void apply(Document document);

    public RegionByLine getRegionByLine() {
        return regionByLine;
    }
}
