/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import static net.sourceforge.pmd.util.CollectionUtil.listOf;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.DummyLanguageModule;
import net.sourceforge.pmd.lang.document.FileId;
import net.sourceforge.pmd.lang.document.TextFile;
import net.sourceforge.pmd.util.CollectionUtil;

class CPDFilelistTest {

    @Test
    void testFilelist() throws IOException {
        testFileList("src/test/resources/net/sourceforge/pmd/cpd/cli/filelist.txt");
    }

    @Test
    void testFilelistMultipleLines() throws IOException {
        testFileList("src/test/resources/net/sourceforge/pmd/cpd/cli/filelist2.txt");
    }

    private static void testFileList(String first) throws IOException {
        CPDConfiguration arguments = new CPDConfiguration();
        arguments.setOnlyRecognizeLanguage(DummyLanguageModule.getInstance());
        arguments.setInputFilePath(Paths.get(first));
        List<FileId> paths;
        try (CpdAnalysis cpd = CpdAnalysis.create(arguments)) {
            paths = CollectionUtil.map(cpd.files().getCollectedFiles(), TextFile::getFileId);
        }

        assertEquals(2, paths.size());
        List<String> simpleNames = CollectionUtil.map(paths, FileId::getFileName);
        assertEquals(
            listOf("anotherfile.dummy", "somefile.dummy"),
            simpleNames
        );
    }
}
