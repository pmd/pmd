/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cache.internal;

import java.io.IOException;
import java.net.URL;
import java.util.zip.Checksum;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Catch-all {@link ClasspathEntryFingerprinter} that ignores all files.
 */
public class NoopFingerprinter implements ClasspathEntryFingerprinter {
    private static final Logger LOG = LoggerFactory.getLogger(NoopFingerprinter.class);

    @Override
    public boolean appliesTo(String fileExtension) {
        return true;
    }

    @Override
    public void fingerprint(URL entry, Checksum checksum) throws IOException {
        // noop
        LOG.debug("Ignoring classpath entry {}", entry);
    }
}
