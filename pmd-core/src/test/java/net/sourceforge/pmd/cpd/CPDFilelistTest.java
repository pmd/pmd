/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

public class CPDFilelistTest {

    @Test
    public void testFilelist() {
        CPDConfiguration arguments = new CPDConfiguration();
        arguments.setLanguage(new CpddummyLanguage());
        arguments.setFileListPath("src/test/resources/net/sourceforge/pmd/cpd/cli/filelist.txt");
        CPD cpd = new CPD(arguments);
        CPDCommandLineInterface.addSourceFilesToCPD(cpd, arguments);

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
    public void testFilelistMultipleLines() {
        CPDConfiguration arguments = new CPDConfiguration();
        arguments.setLanguage(new CpddummyLanguage());
        arguments.setFileListPath("src/test/resources/net/sourceforge/pmd/cpd/cli/filelist2.txt");
        CPD cpd = new CPD(arguments);
        CPDCommandLineInterface.addSourceFilesToCPD(cpd, arguments);

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
