/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.DummyLanguageModule;
import net.sourceforge.pmd.lang.document.TextFile;

class CPDReportTest {

    private Tokens tokens = new Tokens();

    @Test
    void testFilterMatches() {
        List<Match> originalMatches = Arrays.asList(
            createMatch("file1.java", "file2.java", 1),
            createMatch("file1.java", "file3.java", 2),
            createMatch("file2.java", "file3.java", 3));
        Map<String, Integer> numberOfTokensPerFile = new HashMap<>();
        numberOfTokensPerFile.put("file1.java", 10);
        numberOfTokensPerFile.put("file2.java", 15);
        numberOfTokensPerFile.put("file3.java", 20);
        CPDReport original = makeReport(originalMatches, numberOfTokensPerFile);

        assertEquals(3, original.getMatches().size());

        CPDReport filtered = original.filterMatches(
            match -> {
                // only keep file1.java
                for (Mark mark : match.getMarkSet()) {
                    if (mark.getFilename().equals("file1.java")) {
                        return true;
                    }
                }
                return false;
            });
        assertEquals(2, filtered.getMatches().size());
        for (Match match : filtered.getMatches()) {
            Set<String> filenames = new HashSet<>();
            for (Mark mark : match.getMarkSet()) {
                filenames.add(mark.getFilename());
            }
            assertTrue(filenames.contains("file1.java"));
        }

        // note: number of tokens per file is not changed
        assertEquals(original.getNumberOfTokensPerFile(), filtered.getNumberOfTokensPerFile());
    }

    private Match createMatch(String file1, String file2, int line) {
        return new Match(5,
                         tokens.addToken("firstToken", file1, line, 1, line, 1),
                         tokens.addToken("secondToken", file2, line, 2, line, 2));
    }

    static CPDReport makeReport(List<Match> matches) {
        return makeReport(matches, Collections.emptyMap());
    }

    static CPDReport makeReport(List<Match> matches, Map<String, Integer> numTokensPerFile) {
        Set<TextFile> textFiles = new HashSet<>();
        for (Match match : matches) {
            match.iterator().forEachRemaining(
                mark -> textFiles.add(TextFile.forCharSeq("dummy content", mark.getFilename(), DummyLanguageModule.getInstance().getDefaultVersion())));
        }
        return new CPDReport(
            new SourceManager(new ArrayList<>(textFiles)),
            matches,
            numTokensPerFile
        );
    }
}
