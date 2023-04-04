/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.internal.util.IOUtil;
import net.sourceforge.pmd.lang.apex.ApexLanguageModule;

class ApexCpdTest {
    private File testdir;

    @BeforeEach
    void setUp() {
        String path = IOUtil.normalizePath("src/test/resources/net/sourceforge/pmd/cpd/issue427");
        testdir = new File(path);
    }

    @Test
    void testIssue427() throws IOException {
        CPDConfiguration configuration = new CPDConfiguration();
        configuration.setMinimumTileSize(10);
        configuration.setLanguage(LanguageFactory.createLanguage(ApexLanguageModule.TERSE_NAME));
        CPD cpd = new CPD(configuration);
        cpd.add(new File(testdir, "SFDCEncoder.cls"));
        cpd.add(new File(testdir, "SFDCEncoderConstants.cls"));

        cpd.go();

        Iterator<Match> matches = cpd.getMatches();
        int duplications = 0;
        while (matches.hasNext()) {
            matches.next();
            duplications++;
        }
        assertEquals(1, duplications);
        Match firstDuplication = cpd.getMatches().next();
        assertTrue(firstDuplication.getSourceCodeSlice().startsWith("global with sharing class SFDCEncoder"));
    }
}
