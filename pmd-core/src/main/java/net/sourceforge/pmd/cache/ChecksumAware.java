/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cache;

/**
 * Interface defining an object that has a checksum The checksum is a
 * fingerprint of the object's configuration, and *MUST* change if anything
 * changed on the object. It differs from a {@code hashCode()} in that a
 * {@code hashCode()} may not take all fields into account, but a checksum must
 * do so.
 */
public interface ChecksumAware {
    /**
     * Retrieves the current instance checksum
     * 
     * @return The current checksum
     */
    long getChecksum();
}
