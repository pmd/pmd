/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.autofixes;

/**
 * Represents a text source which may be read or edited. This interface is intended to be used by a
 * TextOperation.
 */
public interface Document {

    /**
     * Inserts the text at the given offset and shifts to the right everything that was to the right
     * of the offset before inserting the text. The offset should be no bigger than the current size
     * of the document.
     *
     * @param offset       the index from which the text will be inserted.
     * @param textToInsert the text to insert.
     */
    void insert(int offset, String textToInsert);

    /**
     * Replaces the text in the region designated by the region parameter.
     *
     * @param region        the region in which the textToReplace will be inserted and the older text removed
     * @param textToReplace the text to put in that region.
     */
    void replace(Region region, String textToReplace);

    /**
     * Deletes the text at the given region.
     *
     * @param region the region in which the text will be removed
     */
    void delete(Region region);

    /**
     * Get the representation of the document as a string.
     *
     * @return the string which represents the document.
     */
    String getAsString();

    /**
     * Get the current length of the document.
     *
     * @return the length of the document.
     */
    int getLength();

}
