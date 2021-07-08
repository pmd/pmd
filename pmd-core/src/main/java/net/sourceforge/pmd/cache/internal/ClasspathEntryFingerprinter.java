/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cache.internal;

import java.io.IOException;
import java.net.URL;
import java.util.zip.Checksum;

/**
 * A strategy to fingerprint a given classpath entry.
 */
public interface ClasspathEntryFingerprinter {

    /**
     * Checks if the fingerprinter applies to a particular file extension
     * 
     * @param fileExtension The extension of the classpath entry to check
     * @return True if this fingerprinter applies, false otherwise
     */
    boolean appliesTo(String fileExtension);
    
    /**
     * Adds the given entry fingerprint to the current checksum.
     * 
     * @param entry The entry to be fingerprinted
     * @param checksum The {@link Checksum} in which to accumulate fingerprints
     * @throws IOException
     */
    void fingerprint(URL entry, Checksum checksum) throws IOException;
}
