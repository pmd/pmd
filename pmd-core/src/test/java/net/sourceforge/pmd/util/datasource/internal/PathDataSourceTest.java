/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.datasource.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

import net.sourceforge.pmd.internal.util.IOUtil;

class PathDataSourceTest {
    @TempDir
    private Path tempDir;

    @Test
    void testZipFileNiceName() throws Exception {
        Path zipArchive = tempDir.resolve("sources.zip");
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(zipArchive.toFile()))) {
            ZipEntry zipEntry = new ZipEntry("path/inside/someSource.dummy");
            zipOutputStream.putNextEntry(zipEntry);
            zipOutputStream.write("dummy text".getBytes(StandardCharsets.UTF_8));
            zipOutputStream.closeEntry();
        }
        try (FileSystem fileSystem = FileSystems.newFileSystem(URI.create("jar:" + zipArchive.toUri()), Collections.<String, Object>emptyMap())) {
            Path path = fileSystem.getPath("path/inside/someSource.dummy");
            PathDataSource ds = new PathDataSource(path);
            assertEquals(zipArchive.toAbsolutePath() + "!" + IOUtil.normalizePath("/path/inside/someSource.dummy"),
                    ds.getNiceFileName(false, null));
            assertEquals("sources.zip!someSource.dummy", ds.getNiceFileName(true, null));
            assertEquals("sources.zip!" + IOUtil.normalizePath("/path/inside/someSource.dummy"),
                    ds.getNiceFileName(true, zipArchive.toAbsolutePath().getParent().toString()));
        }
    }
}
