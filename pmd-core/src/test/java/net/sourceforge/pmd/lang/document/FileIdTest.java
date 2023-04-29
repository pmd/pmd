/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.document;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

/**
 * @author Clément Fournier
 */
class FileIdTest {
    @Test
    void testRootStringPath() {
        FileId fileId = FileId.fromPathLikeString("/");
        assertEquals("/", fileId.getAbsolutePath());
    }

    @Test
    void testFromUri() {
        FileId fileId = FileId.fromURI("file:///a/b.c");
        checkId(fileId, "/a/b.c", "b.c", "file:///a/b.c", "/a/b.c");
    }

    @Test
    void testFromUriForJar() {
        FileId fileId = FileId.fromURI("jar:file:///a/b.zip!/x/c.d");
        checkId(fileId, "/x/c.d", "c.d", "jar:file:///a/b.zip!/x/c.d", "/x/c.d");
        checkId(fileId.getParentFsPath(), "/a/b.zip", "b.zip", "file:///a/b.zip", "/a/b.zip");

    }


    private static void checkId(FileId fileId, String absPath, String fileName, String uri, String originalPath) {
        assertNotNull(fileId);
        assertEquals(absPath, fileId.getAbsolutePath());
        assertEquals(fileName, fileId.getFileName());
        assertEquals(uri, fileId.getUriString());
        assertEquals(originalPath, fileId.getOriginalPath());
    }
}
