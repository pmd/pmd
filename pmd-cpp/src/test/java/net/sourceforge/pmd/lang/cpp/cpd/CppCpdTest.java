/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.cpp.cpd;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import net.sourceforge.pmd.cpd.CPDConfiguration;
import net.sourceforge.pmd.cpd.CpdAnalysis;
import net.sourceforge.pmd.cpd.Match;
import net.sourceforge.pmd.internal.util.IOUtil;
import net.sourceforge.pmd.lang.cpp.CppLanguageModule;
import net.sourceforge.pmd.lang.document.FileLocation;

class CppCpdTest {
    private Path testdir;

    @BeforeEach
    void setUp() {
        String path = IOUtil.normalizePath("src/test/resources/net/sourceforge/pmd/lang/cpp/cpd/testdata");
        testdir = Paths.get(path);
    }

    @Test
    void testIssue2438() throws Exception {
        CPDConfiguration configuration = new CPDConfiguration();
        configuration.setMinimumTileSize(50);
        configuration.setOnlyRecognizeLanguage(CppLanguageModule.getInstance());
        try (CpdAnalysis cpd = CpdAnalysis.create(configuration)) {
            cpd.files().addFile(testdir.resolve("ctype.c"));

            cpd.performAnalysis(matches -> {
                // There should only be 1 duplication, and it should be maximal
                assertEquals(1, matches.getMatches().size());
                assertEquals(128, matches.getMatches().get(0).getTokenCount());
            });
        }
    }

    @Test
    void issue6641FileStartsWithBom(@TempDir Path tempdir) throws Exception {
        Path file1 = tempdir.resolve("utf8-bom_1.cpp");
        Path file2 = tempdir.resolve("utf8-bom_2.cpp");
        final String BOM = "\ufeff";
        String duplication = ";\n";
        String fileContent = BOM + duplication;
        Files.write(file1, fileContent.getBytes(StandardCharsets.UTF_8));
        Files.write(file2, fileContent.getBytes(StandardCharsets.UTF_8));
        assertArrayEquals(fileContent.getBytes(StandardCharsets.UTF_8), Files.readAllBytes(file1));
        assertArrayEquals(Files.readAllBytes(file1), Files.readAllBytes(file2));

        CPDConfiguration configuration = new CPDConfiguration();
        configuration.setMinimumTileSize(1);
        configuration.setOnlyRecognizeLanguage(CppLanguageModule.getInstance());
        try (CpdAnalysis cpd = CpdAnalysis.create(configuration)) {
            cpd.files().addFile(file1);
            cpd.files().addFile(file2);

            cpd.performAnalysis(matches -> {
                assertEquals(1, matches.getMatches().size());
                Match firstDuplication = matches.getMatches().get(0);
                FileLocation location = firstDuplication.getFirstMark().getLocation();
                assertEquals(1, location.getStartLine());
                assertEquals(1, location.getEndLine());
                assertEquals(duplication, matches.getSourceCodeSlice(firstDuplication.getFirstMark()).toString());
            });
        }
    }
}
