/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.autofixes.operations;

import net.sourceforge.pmd.autofixes.Document;
import net.sourceforge.pmd.autofixes.Region;
import net.sourceforge.pmd.autofixes.RegionImp;


/**
 * A Text Operation is a representation of a generic manipulation of a text over a document.
 */
public abstract class TextOperation {

    private final Region region;

    /**
     * Default constructor for a text operation.
     * @param offset the offset from which the operation will start
     * @param length the length of the region
     */
    public TextOperation(final int offset, final int length) {
        region = new RegionImp(offset, length);
    }

    public int getOffsetAfterEnding() {
        return region.getOffsetAfterEnding();
    }

    /**
     * Get the offset of the text operation.
     * @return the offset
     */
    public int getOffset() {
        return region.getOffset();
    }

    /**
     * Get the length of the text operation.
     * @return the length
     */
    public int getLength() {
        return region.getLength();
    }

    /* package-private */
    Region getRegion() {
        return new RegionImp(region);
    }

    /* package-private */
    abstract int applyTextOperationToDocument(Document document);
}
