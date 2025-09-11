/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.StringWriter;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.document.FileId;

class MarkdownRendererTest {

    @Test
    void testMultipleDuplicates() throws IOException {
        CPDReportRenderer renderer = new MarkdownRenderer();
        CpdTestUtils.CpdReportBuilder builder = new CpdTestUtils.CpdReportBuilder();

        FileId foo = CpdTestUtils.FOO_FILE_ID;
        FileId bar = CpdTestUtils.BAR_FILE_ID;

        int lineCount1 = 6;
        Mark mark1 = builder.createMark("public", foo, 48, lineCount1);
        Mark mark2 = builder.createMark("void", foo, 73, lineCount1);
        builder.addMatch(new Match(75, mark1, mark2));

        int lineCount2 = 5;
        Mark mark3 = builder.createMark("void", bar, 49, lineCount2);
        Mark mark4 = builder.createMark("stuff", bar, 74, lineCount2);
        builder.addMatch(new Match(50, mark3, mark4));

        StringWriter sw = new StringWriter();
        renderer.render(builder.build(), sw);
        String report = sw.toString();
        String expectedReport = "Found a 6 line (75 tokens) duplication in the following files:\n"
                + "* Starting at line 48 of " + foo.getAbsolutePath() + "\n"
                + "* Starting at line 73 of " + foo.getAbsolutePath() + "\n"
                + "\n"
                + "```java\n"
                + "47_47_47_47_47_47_47_47_47_47_\n"
                + "48_48_48_48_48_48_48_48_48_48_\n"
                + "49_49_49_49_49_49_49_49_49_49_\n"
                + "50_50_50_50_50_50_50_50_50_50_\n"
                + "51_51_51_51_51_51_51_51_51_51_\n"
                + "52_52_52_52_52_52_52_52_52_52_\n"
                + "```\n"
                + "\n"
                + "---\n"
                + "\n"
                + "Found a 5 line (50 tokens) duplication in the following files:\n"
                + "* Starting at line 49 of " + bar.getAbsolutePath() + "\n"
                + "* Starting at line 74 of " + bar.getAbsolutePath() + "\n"
                + "\n"
                + "```java\n"
                + "48_48_48_48_48_48_48_48_48_48_\n"
                + "49_49_49_49_49_49_49_49_49_49_\n"
                + "50_50_50_50_50_50_50_50_50_50_\n"
                + "51_51_51_51_51_51_51_51_51_51_\n"
                + "52_52_52_52_52_52_52_52_52_52_\n"
                + "```\n";

        assertEquals(expectedReport, report);
    }

}
