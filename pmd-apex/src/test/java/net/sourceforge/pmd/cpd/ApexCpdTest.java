/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.apache.commons.io.FilenameUtils;
import org.junit.Before;
import org.junit.Test;

public class ApexCpdTest {
    private File testdir;

    @Before
    public void setUp() {
        String path = FilenameUtils.normalize("src/test/resources/net/sourceforge/pmd/cpd/issue427");
        testdir = new File(path);
    }

    @Test
    public void testIssue427() throws IOException {
        CPDConfiguration configuration = new CPDConfiguration();
        configuration.setMinimumTileSize(50);
        configuration.setLanguage(LanguageFactory.createLanguage("apex"));
        CPD cpd = new CPD(configuration);
        cpd.add(new File(testdir, "SFDCEncoder.cls"));
        cpd.add(new File(testdir, "SFDCEncoderConstants.cls"));

        cpd.go();

        Iterator<Match> matches = cpd.getMatches();
        int duplications = 0;
        while (matches.hasNext()) {
            Match match = matches.next();
            duplications++;
            assertTrue(match.getSourceCodeSlice().startsWith("/*"));
        }
        assertEquals(1, duplications);
    }
}
