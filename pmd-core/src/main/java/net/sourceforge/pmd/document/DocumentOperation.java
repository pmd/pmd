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
    private final TextRegion regionByLine;

    public DocumentOperation(TextRegion region) {
        regionByLine = region;
    }

    /**
     * Apply this operation to the specified document
     * @param document the document to which apply the operation
     */
    public abstract void apply(MutableDocument document);

    public TextRegion getRegion() {
        return regionByLine;
    }

    public static DocumentOperation createInsert(final int beginLine, final int beginColumn, final String textToInsert) {
        return createReplace(TextRegion.newRegionByLine(beginLine, beginColumn, beginLine, beginColumn), textToInsert);
    }

    public static DocumentOperation createReplace(final int beginLine, final int endLine, final int beginColumn, final int endColumn, final String textToReplace) {
        return createReplace(TextRegion.newRegionByLine(beginLine, beginColumn, endLine, endColumn), textToReplace);
    }

    public static DocumentOperation createReplace(TextRegion region, String text) {
        return new ReplaceDocumentOperation(region, text);
    }

    public static DocumentOperation createDelete(final int beginLine, final int endLine, final int beginColumn, final int endColumn) {
        return createReplace(TextRegion.newRegionByLine(beginLine, endLine, beginColumn, endColumn), "");
    }
}
