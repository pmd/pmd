/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.document;

/**
 * Represents a region in a {@link Document} with the tuple (offset, length).
 */
public interface RegionByOffset {

    int getOffset();

    int getLength();

    int getOffsetAfterEnding();
}
