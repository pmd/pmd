/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cache.internal;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.Checksum;

/**
 * Catch-all {@link ClasspathEntryFingerprinter} that ignores all files.
 */
public class NoopFingerprinter implements ClasspathEntryFingerprinter {
    private static final Logger LOG = Logger.getLogger(NoopFingerprinter.class.getName());

    @Override
    public boolean appliesTo(String fileExtension) {
        return true;
    }

    @Override
    public void fingerprint(URL entry, Checksum checksum) throws IOException {
        // noop
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Ignoring classpath entry " + entry);
        }
    }
}
