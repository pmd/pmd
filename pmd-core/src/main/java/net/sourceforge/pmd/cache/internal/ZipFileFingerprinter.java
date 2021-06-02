/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cache.internal;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.Checksum;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Specialized fingerprinter for Zip files.
 */
public class ZipFileFingerprinter implements ClasspathEntryFingerprinter {

    private static final Logger LOG = Logger.getLogger(ZipFileFingerprinter.class.getName());

    private static final Set<String> SUPPORTED_EXTENSIONS;
    private static final Set<String> SUPPORTED_ENTRY_EXTENSIONS;

    private static final Comparator<ZipEntry> FILE_NAME_COMPARATOR = new Comparator<ZipEntry>() {

        @Override
        public int compare(ZipEntry o1, ZipEntry o2) {
            return o1.getName().compareTo(o2.getName());
        }
    };

    static {
        final Set<String> extensions = new HashSet<>();
        extensions.add("jar");
        extensions.add("zip");
        SUPPORTED_EXTENSIONS = Collections.unmodifiableSet(extensions);

        final Set<String> entryExtensions = new HashSet<>();
        entryExtensions.add("class");
        SUPPORTED_ENTRY_EXTENSIONS = Collections.unmodifiableSet(entryExtensions);
    }

    @Override
    public boolean appliesTo(String fileExtension) {
        return SUPPORTED_EXTENSIONS.contains(fileExtension);
    }

    @Override
    public void fingerprint(URL entry, Checksum checksum) throws IOException {
        try (ZipFile zip = new ZipFile(new File(entry.toURI()))) {
            final List<ZipEntry> meaningfulEntries = getMeaningfulEntries(zip);

            /*
             *  Make sure the order of entries in the zip do not matter.
             *  Duplicates are technically possible, but shouldn't exist in classpath entries
             */
            Collections.sort(meaningfulEntries, FILE_NAME_COMPARATOR);

            final ByteBuffer buffer = ByteBuffer.allocate(4); // Size of an int

            for (final ZipEntry zipEntry : meaningfulEntries) {
                /*
                 * The CRC actually uses 4 bytes, but as it's unsigned Java uses a long…
                 * the cast changes the sign, but not the underlying byte values themselves
                 */
                buffer.putInt(0, (int) zipEntry.getCrc());
                checksum.update(buffer.array(), 0, 4);
            }
        } catch (final FileNotFoundException | NoSuchFileException ignored) {
            LOG.warning("Classpath entry " + entry.toString() + " doesn't exist, ignoring it");
        } catch (final URISyntaxException e) {
            // Should never happen?
            LOG.log(Level.WARNING, "Malformed classpath entry doesn't refer to zip in filesystem.", e);
        }
    }

    /**
     * Retrieve a filtered list of entries discarding those that do not matter for classpath computation
     * @param zip The zip file whose entries to retrieve
     * @return The filtered list of zip entries
     */
    private List<ZipEntry> getMeaningfulEntries(ZipFile zip) {
        final List<ZipEntry> meaningfulEntries = new ArrayList<>();
        final Enumeration<? extends ZipEntry> entries = zip.entries();

        while (entries.hasMoreElements()) {
            final ZipEntry zipEntry = entries.nextElement();

            if (SUPPORTED_ENTRY_EXTENSIONS.contains(getFileExtension(zipEntry))) {
                meaningfulEntries.add(zipEntry);
            }
        }

        return meaningfulEntries;
    }

    private String getFileExtension(final ZipEntry entry) {
        if (entry.isDirectory()) {
            return null;
        }

        final String file = entry.getName();
        final int lastDot = file.lastIndexOf('.');

        if (lastDot == -1) {
            return "";
        }

        return file.substring(lastDot + 1);
    }
}
