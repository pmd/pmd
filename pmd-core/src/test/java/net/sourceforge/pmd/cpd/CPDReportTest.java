/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.cpd.CpdTestUtils.CpdReportBuilder;

class CPDReportTest {

    @Test
    void testFilterMatches() {
        CpdReportBuilder reportBuilder = new CpdReportBuilder();
        reportBuilder.addMatch(createMatch(reportBuilder, "file1.java", "file2.java", 1));
        reportBuilder.addMatch(createMatch(reportBuilder, "file1.java", "file3.java", 2));
        reportBuilder.addMatch(createMatch(reportBuilder, "file2.java", "file3.java", 3));
        reportBuilder.recordNumTokens("file1.java", 10);
        reportBuilder.recordNumTokens("file2.java", 15);
        reportBuilder.recordNumTokens("file3.java", 20);
        CPDReport original = reportBuilder.build();

        assertEquals(3, original.getMatches().size());

        CPDReport filtered = original.filterMatches(
            match -> {
                // only keep file1.java
                for (Mark mark : match.getMarkSet()) {
                    if (mark.getLocation().getFileName().equals("file1.java")) {
                        return true;
                    }
                }
                return false;
            });
        assertEquals(2, filtered.getMatches().size());
        for (Match match : filtered.getMatches()) {
            Set<String> filenames = new HashSet<>();
            for (Mark mark : match.getMarkSet()) {
                filenames.add(mark.getLocation().getFileName());
            }
            assertTrue(filenames.contains("file1.java"));
        }

        // note: number of tokens per file is not changed
        assertEquals(original.getNumberOfTokensPerFile(), filtered.getNumberOfTokensPerFile());
    }

    private Match createMatch(CpdReportBuilder builder, String file1, String file2, int line) {
        return new Match(5,
                         builder.tokens.addToken("firstToken", file1, line, 1, line, 1),
                         builder.tokens.addToken("secondToken", file2, line, 2, line, 2));
    }
}
