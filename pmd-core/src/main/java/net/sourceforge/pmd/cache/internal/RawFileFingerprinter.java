/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cache.internal;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.CheckedInputStream;
import java.util.zip.Checksum;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.pmd.internal.util.IOUtil;

/**
 * Base fingerprinter for raw files.
 */
public class RawFileFingerprinter implements ClasspathEntryFingerprinter {
    
    private static final Logger LOG = LoggerFactory.getLogger(RawFileFingerprinter.class);
    
    private static final Set<String> SUPPORTED_EXTENSIONS;
    
    static {
        final Set<String> extensions = new HashSet<>();
        extensions.add("class"); // Java class files
        SUPPORTED_EXTENSIONS = Collections.unmodifiableSet(extensions);
    }

    @Override
    public boolean appliesTo(String fileExtension) {
        return SUPPORTED_EXTENSIONS.contains(fileExtension);
    }

    @Override
    public void fingerprint(URL entry, Checksum checksum) throws IOException {
        try (CheckedInputStream inputStream = new CheckedInputStream(entry.openStream(), checksum)) {
            // Just read it, the CheckedInputStream will update the checksum on it's own
            while (IOUtil.skipFully(inputStream, Long.MAX_VALUE) == Long.MAX_VALUE) {
                // just loop
            }
        } catch (final FileNotFoundException ignored) {
            LOG.warn("Classpath entry {} doesn't exist, ignoring it", entry);
        }
    }

}
