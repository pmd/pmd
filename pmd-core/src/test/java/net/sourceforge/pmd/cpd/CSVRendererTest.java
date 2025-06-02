/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.StringWriter;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.cpd.CpdTestUtils.CpdReportBuilder;
import net.sourceforge.pmd.lang.document.FileId;

class CSVRendererTest {

    @Test
    void testLineCountPerFile() throws IOException {
        CPDReportRenderer renderer = new CSVRenderer(true);
        CpdReportBuilder builder = new CpdReportBuilder();
        Mark mark1 = builder.createMark("public", CpdTestUtils.FOO_FILE_ID, 48, 10);
        Mark mark2 = builder.createMark("stuff", CpdTestUtils.BAR_FILE_ID, 73, 20);
        builder.addMatch(new Match(75, mark1, mark2));

        StringWriter sw = new StringWriter();
        renderer.render(builder.build(), sw);
        String report = sw.toString();
        String expectedReport = "tokens,occurrences" + System.lineSeparator()
            + "75,2,48,10," + CpdTestUtils.FOO_FILE_ID.getAbsolutePath() + ",73,20,"
            + CpdTestUtils.BAR_FILE_ID.getAbsolutePath() + System.lineSeparator();

        assertEquals(expectedReport, report);
    }

    @Test
    void testFilenameEscapes() throws IOException {
        CPDReportRenderer renderer = new CSVRenderer();
        CpdReportBuilder builder = new CpdReportBuilder();
        FileId foo = FileId.fromPathLikeString("/var,with,commas/Foo.java");
        FileId bar = FileId.fromPathLikeString("/var,with,commas/Bar.java");
        Mark mark1 = builder.createMark("public", foo, 48, 10);
        Mark mark2 = builder.createMark("stuff", bar, 73, 20);
        builder.addMatch(new Match(75, mark1, mark2));

        StringWriter sw = new StringWriter();
        renderer.render(builder.build(), sw);
        String report = sw.toString();
        String expectedReport = "lines,tokens,occurrences" + System.lineSeparator()
            + "10,75,2,48,\"" + foo.getAbsolutePath() + "\",73,\"" + bar.getAbsolutePath() + "\""
            + System.lineSeparator();
        assertEquals(expectedReport, report);
    }

}
