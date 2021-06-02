/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cache.internal;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.Adler32;

public class ClasspathFingerprinter {
    private static final Logger LOG = Logger.getLogger(ClasspathFingerprinter.class.getName());
    
    // TODO : With Java 9 we could use List.of()…
    private static final List<ClasspathEntryFingerprinter> FINGERPRINTERS = Collections.unmodifiableList(Arrays.asList(
            new ZipFileFingerprinter(),
            new RawFileFingerprinter(),
            new NoopFingerprinter() // catch-all fingerprinter, MUST be last
        ));
    
    public long fingerprint(final URL... classpathEntry) {
        final Adler32 adler32 = new Adler32();
        
        try {
            for (final URL url : classpathEntry) {
                final String extension = getExtension(url);
                
                for (ClasspathEntryFingerprinter f : FINGERPRINTERS) {
                    if (f.appliesTo(extension)) {
                        f.fingerprint(url, adler32);
                        break;
                    }
                }
            }
        } catch (final IOException e) {
            // Can this even happen?
            LOG.log(Level.SEVERE, "Incremental analysis can't fingerprint classpath contents", e);
            throw new RuntimeException(e);
        }
        
        return adler32.getValue();
    }

    private String getExtension(final URL url) {
        final String file = url.getFile();
        final int lastDot = file.lastIndexOf('.');
        
        if (lastDot == -1) {
            return  "";
        }
        
        return file.substring(lastDot + 1);
    }
}
