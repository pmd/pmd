/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

class CPDFilelistTest {

    @Test
    void testFilelist() {
        CPDConfiguration arguments = new CPDConfiguration();
        arguments.setLanguage(new CpddummyLanguage());
        arguments.setFileListPath("src/test/resources/net/sourceforge/pmd/cpd/cli/filelist.txt");
        CPD cpd = new CPD(arguments);

        List<String> paths = cpd.getSourcePaths();
        assertEquals(2, paths.size());
        Set<String> simpleNames = new HashSet<>();
        for (String path : paths) {
            simpleNames.add(Paths.get(path).getFileName().toString());
        }
        assertTrue(simpleNames.contains("anotherfile.dummy"));
        assertTrue(simpleNames.contains("somefile.dummy"));
    }

    @Test
    void testFilelistMultipleLines() {
        CPDConfiguration arguments = new CPDConfiguration();
        arguments.setLanguage(new CpddummyLanguage());
        arguments.setFileListPath("src/test/resources/net/sourceforge/pmd/cpd/cli/filelist2.txt");
        CPD cpd = new CPD(arguments);

        List<String> paths = cpd.getSourcePaths();
        assertEquals(2, paths.size());
        Set<String> simpleNames = new HashSet<>();
        for (String path : paths) {
            simpleNames.add(Paths.get(path).getFileName().toString());
        }
        assertTrue(simpleNames.contains("anotherfile.dummy"));
        assertTrue(simpleNames.contains("somefile.dummy"));
    }
}
