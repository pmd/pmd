/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.DummyLanguageModule;
import net.sourceforge.pmd.lang.document.TextFile;
import net.sourceforge.pmd.util.CollectionUtil;

class CPDFilelistTest {

    @Test
    void testFilelist() throws IOException {
        CPDConfiguration arguments = new CPDConfiguration();
        arguments.setOnlyRecognizeLanguage(DummyLanguageModule.getInstance());
        arguments.setInputFilePath(Paths.get("src/test/resources/net/sourceforge/pmd/cpd/cli/filelist.txt"));
        List<String> paths;
        try (CpdAnalysis cpd = CpdAnalysis.create(arguments)) {
            paths = CollectionUtil.map(cpd.files().getCollectedFiles(), TextFile::getPathId);
        }

        assertEquals(2, paths.size());
        Set<String> simpleNames = new HashSet<>();
        for (String path : paths) {
            simpleNames.add(FilenameUtils.getName(path));
        }
        assertTrue(simpleNames.contains("anotherfile.dummy"));
        assertTrue(simpleNames.contains("somefile.dummy"));
    }

    @Test
    void testFilelistMultipleLines() throws IOException {
        CPDConfiguration arguments = new CPDConfiguration();
        arguments.setOnlyRecognizeLanguage(DummyLanguageModule.getInstance());
        arguments.setInputFilePath(Paths.get("src/test/resources/net/sourceforge/pmd/cpd/cli/filelist2.txt"));
        List<String> paths;
        try (CpdAnalysis cpd = CpdAnalysis.create(arguments)) {
            paths = CollectionUtil.map(cpd.files().getCollectedFiles(), TextFile::getPathId);
        }

        assertEquals(2, paths.size());
        Set<String> simpleNames = new HashSet<>();
        for (String path : paths) {
            simpleNames.add(FilenameUtils.getName(path));
        }
        assertTrue(simpleNames.contains("anotherfile.dummy"));
        assertTrue(simpleNames.contains("somefile.dummy"));
    }
}
