/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.document;

import static java.util.Objects.requireNonNull;

/**
 * Represents an insert operation in a {@link Document}.
 */
public class InsertDocumentOperation extends DocumentOperation {

    private final String textToInsert;

    public InsertDocumentOperation(final int beginLine, final int beginColumn, final String textToInsert) {
        super(beginLine, beginLine, beginColumn, beginColumn);
        this.textToInsert = requireNonNull(textToInsert);
    }

    @Override
    public void apply(final Document document) {
        final RegionByLine regionByLine = getRegionByLine();
        document.insert(regionByLine.getBeginLine(), regionByLine.getBeginColumn(), textToInsert);
    }
}
