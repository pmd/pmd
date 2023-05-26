/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.document;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

/**
 * @author Clément Fournier
 */
class FileIdTest {
    // note we can't hardcode the expected paths because they look different on win and nix

    @Test
    void testFromPath() {
        Path path = Paths.get("/a");
        Path absPath = path.toAbsolutePath();
        FileId fileId = FileId.fromPath(path);
        checkId(fileId, absPath.toString(), "a", path.toUri().toString(), path.toString());
    }


    @Test
    void testFromUri() {
        Path absPath = Paths.get("/a/b.c");
        String uriStr = absPath.toUri().toString();
        FileId fileId = FileId.fromURI(uriStr);
        checkId(fileId, absPath.toAbsolutePath().toString(), "b.c", uriStr, absPath.toAbsolutePath().toString());
    }

    @Test
    void testFromUriForJar() {
        Path zipPath = Paths.get("/a/b.zip");
        String uriStr = "jar:" + zipPath.toUri() + "!/x/c.d";
        FileId fileId = FileId.fromURI(uriStr);
        String absLocalPath = "/x/c.d".replace('/', File.separatorChar);
        checkId(fileId, absLocalPath, "c.d", uriStr, "/x/c.d");
        checkId(fileId.getParentFsPath(), zipPath.toAbsolutePath().toString(), "b.zip", zipPath.toUri().toString(), zipPath.toAbsolutePath().toString());
    }


    private static void checkId(FileId fileId, String absPath, String fileName, String uri, String originalPath) {
        assertNotNull(fileId);
        assertEquals(absPath, fileId.getAbsolutePath(), "absolute path");
        assertEquals(fileName, fileId.getFileName(), "file name");
        assertEquals(uri, fileId.getUriString(), "uri");
        assertEquals(originalPath, fileId.getOriginalPath(), "original path");
    }
}
