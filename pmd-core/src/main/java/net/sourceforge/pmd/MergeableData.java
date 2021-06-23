/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

/**
 * A data object that can be merged with another of the same type.
 *
 */
public interface MergeableData {

    /**
     * Creates an empty instance of the same type as this one.
     *
     * @return A new MergeableData object
     */
    MergeableData create();

    /**
     * Merge another data object of the same type into this.
     *
     * @param other The other data object.
     */
    void merge(MergeableData other);
}
