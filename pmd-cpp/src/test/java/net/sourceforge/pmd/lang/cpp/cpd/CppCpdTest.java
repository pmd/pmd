/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.cpp.cpd;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.cpd.CPDConfiguration;
import net.sourceforge.pmd.cpd.CpdAnalysis;
import net.sourceforge.pmd.internal.util.IOUtil;
import net.sourceforge.pmd.lang.cpp.CppLanguageModule;

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
}
