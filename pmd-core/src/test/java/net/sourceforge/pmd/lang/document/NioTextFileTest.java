/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.document;

import static org.junit.Assert.assertEquals;

import java.io.FileOutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Collections;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import net.sourceforge.pmd.lang.DummyLanguageModule;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.util.IOUtil;

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
        try (FileSystem fileSystem = FileSystems.newFileSystem(URI.create("jar:" + zipArchive.toUri()), Collections.<String, Object>emptyMap())) {
            Path path = fileSystem.getPath("path/inside/someSource.dummy");
            LanguageRegistry.PMD.getLanguageById("dummy");
            LanguageVersion languageVersion = DummyLanguageModule.getInstance().getDefaultVersion();
            TextFile textFile = TextFile.builderForPath(path, StandardCharsets.UTF_8, languageVersion).build();
            assertEquals(zipArchive.toAbsolutePath() + "!" + IOUtil.normalizePath("/path/inside/someSource.dummy"),
                    textFile.getDisplayName());
        }
    }
}
