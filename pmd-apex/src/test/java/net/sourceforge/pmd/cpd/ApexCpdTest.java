/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.internal.util.IOUtil;
import net.sourceforge.pmd.lang.apex.ApexLanguageModule;

class ApexCpdTest {

    private Path testdir;

    @BeforeEach
    void setUp() {
        String path = IOUtil.normalizePath("src/test/resources/net/sourceforge/pmd/cpd/issue427");
        testdir = Paths.get(path);
    }

    @Test
    void testIssue427() throws Exception {
        CPDConfiguration configuration = new CPDConfiguration();
        configuration.setMinimumTileSize(10);
        configuration.setLanguage(ApexLanguageModule.getInstance());
        try (CpdAnalysis cpd = new CpdAnalysis(configuration)) {
            cpd.files().addFile(testdir.resolve("SFDCEncoder.cls"));
            cpd.files().addFile(testdir.resolve("SFDCEncoderConstants.cls"));

            cpd.performAnalysis(matches -> {
                assertEquals(1, matches.getMatches().size());
                Match firstDuplication = matches.getMatches().get(0);
                assertTrue(matches.getSourceCodeSlice(firstDuplication).startsWith("global with sharing class SFDCEncoder"));
            });
        }
    }
}
