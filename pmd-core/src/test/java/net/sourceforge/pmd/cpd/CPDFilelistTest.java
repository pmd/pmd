/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class CPDFilelistTest {

    @Test
    void testFilelist() {
        CPDConfiguration arguments = new CPDConfiguration();
        arguments.setLanguage(new CpddummyLanguage());
        arguments.setFileListPath("src/test/resources/net/sourceforge/pmd/cpd/cli/filelist.txt");
        CPD cpd = new CPD(arguments);
        CPDCommandLineInterface.addSourceFilesToCPD(cpd, arguments);

        List<String> paths = cpd.getSourcePaths();
        Assertions.assertEquals(2, paths.size());
        Set<String> simpleNames = new HashSet<>();
        for (String path : paths) {
            simpleNames.add(FilenameUtils.getName(path));
        }
        Assertions.assertTrue(simpleNames.contains("anotherfile.dummy"));
        Assertions.assertTrue(simpleNames.contains("somefile.dummy"));
    }

    @Test
    void testFilelistMultipleLines() {
        CPDConfiguration arguments = new CPDConfiguration();
        arguments.setLanguage(new CpddummyLanguage());
        arguments.setFileListPath("src/test/resources/net/sourceforge/pmd/cpd/cli/filelist2.txt");
        CPD cpd = new CPD(arguments);
        CPDCommandLineInterface.addSourceFilesToCPD(cpd, arguments);

        List<String> paths = cpd.getSourcePaths();
        Assertions.assertEquals(2, paths.size());
        Set<String> simpleNames = new HashSet<>();
        for (String path : paths) {
            simpleNames.add(FilenameUtils.getName(path));
        }
        Assertions.assertTrue(simpleNames.contains("anotherfile.dummy"));
        Assertions.assertTrue(simpleNames.contains("somefile.dummy"));
    }
}
