/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.document;

/**
 * Represents a file which contains programming code that will be fixed.
 */
public interface Document {

    /**
     * Insert a text at a line at the position/column specified. If there is any text to the right of the insertion,
     * that text is shifted by the length of the text to insert, which means that it is not replaced.
     * @param beginLine the line in which to insert the text
     * @param beginColumn the position in the line in which to insert the text
     * @param textToInsert the text to be added
     */
    void insert(int beginLine, int beginColumn, String textToInsert);

    /**
     * Replace a specific region in the document which contains text by another text, which not necessarily is the same
     * length as the region's one.
     * @param regionByOffset the region in which a text will be inserted to replace the current document's contents
     * @param textToReplace the text to insert
     */
    void replace(RegionByLine regionByOffset, String textToReplace);

    /**
     * Delete a region in the document, removing all text which contains it. If there is any text to the right of this
     * region, it will be shifted to the left by the length of the region to delete.
     * @param regionByOffset the region in which to erase all the text
     */
    void delete(RegionByLine regionByOffset);
}
