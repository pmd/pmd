/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.document;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersionDiscoverer;

class NioTextFileTest {

    @TempDir
    private Path tempDir;

    @Test
    void zipFileDisplayName() throws Exception {
        Path zipArchive = tempDir.resolve("sources.zip");
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(zipArchive.toFile()))) {
            ZipEntry zipEntry = new ZipEntry("path/inside/someSource.dummy");
            zipOutputStream.putNextEntry(zipEntry);
            zipOutputStream.write("dummy text".getBytes(StandardCharsets.UTF_8));
            zipOutputStream.closeEntry();
        }

        LanguageVersionDiscoverer discoverer = new LanguageVersionDiscoverer(LanguageRegistry.PMD, null);
        try (FileCollector collector = FileCollector.newCollector(discoverer, new TestMessageReporter())) {
            collector.addZipFileWithContent(zipArchive);
            List<TextFile> collectedFiles = collector.getCollectedFiles();
            assertEquals(1, collectedFiles.size());
            TextFile textFile = collectedFiles.get(0);
            assertEquals(zipArchive.toAbsolutePath() + "!/path/inside/someSource.dummy",
                    textFile.getDisplayName());
        }
    }
}
