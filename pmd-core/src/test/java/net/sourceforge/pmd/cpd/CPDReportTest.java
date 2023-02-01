/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.util.Predicate;

class CPDReportTest {

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
        CPDReport original = new CPDReport(originalMatches, numberOfTokensPerFile);

        assertEquals(3, original.getMatches().size());

        CPDReport filtered = original.filterMatches(
                new Predicate<Match>() {
                    @Override
                    public boolean test(Match match) {
                        // only keep file1.java
                        for (Mark mark : match.getMarkSet()) {
                            if (mark.getFilename().equals("file1.java")) {
                                return true;
                            }
                        }
                        return false;
                    }
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
                         new TokenEntry("firstToken", file1, 1, 1, 1),
                         new TokenEntry("secondToken", file2, 1, 2, 2));
    }
}
