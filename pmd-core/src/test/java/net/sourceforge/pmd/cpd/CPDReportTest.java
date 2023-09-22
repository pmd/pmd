/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.cpd.CpdTestUtils.CpdReportBuilder;
import net.sourceforge.pmd.lang.document.FileId;
import net.sourceforge.pmd.util.CollectionUtil;

class CPDReportTest {

    @Test
    void testFilterMatches() {
        CpdReportBuilder reportBuilder = new CpdReportBuilder();
        FileId file1 = FileId.fromPathLikeString("file1.java");
        FileId file2 = FileId.fromPathLikeString("file2.java");
        FileId file3 = FileId.fromPathLikeString("file3.java");
        reportBuilder.addMatch(createMatch(reportBuilder, file1, file2, 1));
        reportBuilder.addMatch(createMatch(reportBuilder, file1, file3, 2));
        reportBuilder.addMatch(createMatch(reportBuilder, file2, file3, 3));
        reportBuilder.recordNumTokens(file1, 10);
        reportBuilder.recordNumTokens(file2, 15);
        reportBuilder.recordNumTokens(file3, 20);
        CPDReport original = reportBuilder.build();

        assertEquals(3, original.getMatches().size());

        CPDReport filtered = original.filterMatches(
            // only keep file1.java
            match -> CollectionUtil.any(match, mark -> mark.getLocation().getFileId().equals(file1))
        );

        assertEquals(2, filtered.getMatches().size());
        for (Match match : filtered.getMatches()) {
            boolean containsFile1 =
                match.getMarkSet().stream().map(Mark::getFileId)
                     .anyMatch(file1::equals);
            assertTrue(containsFile1);
        }

        // note: number of tokens per file is not changed
        assertEquals(original.getNumberOfTokensPerFile(), filtered.getNumberOfTokensPerFile());
    }

    private Match createMatch(CpdReportBuilder builder, FileId file1, FileId file2, int line) {
        return new Match(5,
                         builder.tokens.addToken("firstToken", file1, line, 1, line, 1),
                         builder.tokens.addToken("secondToken", file2, line, 2, line, 2));
    }
}
