/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.it;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.io.IOUtils;

/**
 * Extracts a zip file with preserving the unix file permissions.
 *
 * @author Andreas Dangel
 */
public class ZipFileExtractor {
    // unix file permission for executable flag by owner
    private static final int OWNER_EXECUTABLE = 0x40;

    private ZipFileExtractor() {
        // Helper class
    }

    /**
     * Extracts the given zip file into the tempDir.
     * @param zipPath the zip file to extract
     * @param tempDir the target directory
     * @throws Exception if any error happens during extraction
     */
    public static void extractZipFile(Path zipPath, Path tempDir) throws Exception {
        ZipFile zip = new ZipFile(zipPath.toFile());
        try {
            Enumeration<ZipArchiveEntry> entries = zip.getEntries();
            while (entries.hasMoreElements()) {
                ZipArchiveEntry entry = entries.nextElement();
                File file = tempDir.resolve(entry.getName()).toFile();
                if (entry.isDirectory()) {
                    assertTrue(file.mkdirs());
                } else {
                    try (InputStream data = zip.getInputStream(entry);
                         OutputStream fileOut = new FileOutputStream(file);) {
                        IOUtils.copy(data, fileOut);
                    }
                    if ((entry.getUnixMode() & OWNER_EXECUTABLE) == OWNER_EXECUTABLE) {
                        file.setExecutable(true);
                    }
                }
            }
        } finally {
            zip.close();
        }
    }

    /**
     * Compiles a list of all the files/directories contained in the given zip file.
     * @param zipPath the zip file to look into
     * @return list of all entries
     * @throws Exception if any error happens during read of the zip file
     */
    public static List<String> readZipFile(Path zipPath) throws Exception {
        List<String> result = new ArrayList<>();
        ZipFile zip = new ZipFile(zipPath.toFile());
        try {
            Enumeration<ZipArchiveEntry> entries = zip.getEntries();
            while (entries.hasMoreElements()) {
                ZipArchiveEntry entry = entries.nextElement();
                result.add(entry.getName());
            }
        } finally {
            zip.close();
        }
        return result;
    }
}
