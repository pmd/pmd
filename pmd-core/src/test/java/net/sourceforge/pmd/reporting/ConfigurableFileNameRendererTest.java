/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.reporting;

import static net.sourceforge.pmd.reporting.ConfigurableFileNameRenderer.getDisplayName;
import static net.sourceforge.pmd.util.CollectionUtil.listOf;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.document.FileId;

class ConfigurableFileNameRendererTest {

    @Test
    void testRelativize() {
        FileId file = FileId.fromPath(Paths.get("a", "b", "c"));
        String displayName = getDisplayName(file, listOf(Paths.get("a")));
        assertEquals(displayName, Paths.get("b", "c").toString());
    }

    @Test
    void testRelativizeOutOfDir() {
        FileId file = FileId.fromPath(Paths.get("a", "b", "c"));
        String displayName = getDisplayName(file, listOf(Paths.get("d")));
        assertEquals(displayName, Paths.get("..", "a", "b", "c").toString());
    }


    @Test
    void testRelativizeWithRoot() {
        Path path = Paths.get("a", "b", "c");
        FileId file = FileId.fromPath(path);
        String displayName = getDisplayName(file, listOf(Paths.get("/")));
        assertEquals(path.toAbsolutePath().toString(),
                     displayName);
    }

}
