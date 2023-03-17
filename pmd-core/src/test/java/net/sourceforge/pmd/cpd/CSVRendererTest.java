/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.StringWriter;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.cpd.CpdTestUtils.CpdReportBuilder;
import net.sourceforge.pmd.cpd.renderer.CPDReportRenderer;

class CSVRendererTest {
    @Test
    void testLineCountPerFile() throws IOException {
        CPDReportRenderer renderer = new CSVRenderer(true);
        CpdReportBuilder builder = new CpdReportBuilder();
        Mark mark1 = builder.createMark("public", "/var/Foo.java", 48, 10);
        Mark mark2 = builder.createMark("stuff", "/var/Bar.java", 73, 20);
        builder.addMatch(new Match(75, mark1, mark2));

        StringWriter sw = new StringWriter();
        renderer.render(builder.build(), sw);
        String report = sw.toString();
        String expectedReport = "tokens,occurrences" + System.lineSeparator()
            + "75,2,48,10,/var/Foo.java,73,20,/var/Bar.java" + System.lineSeparator();

        assertEquals(expectedReport, report);
    }

    @Test
    void testFilenameEscapes() throws IOException {
        CPDReportRenderer renderer = new CSVRenderer();
        CpdReportBuilder builder = new CpdReportBuilder();
        Mark mark1 = builder.createMark("public", "/var,with,commas/Foo.java", 48, 10);
        Mark mark2 = builder.createMark("stuff", "/var,with,commas/Bar.java", 73, 20);
        builder.addMatch(new Match(75, mark1, mark2));

        StringWriter sw = new StringWriter();
        renderer.render(builder.build(), sw);
        String report = sw.toString();
        String expectedReport = "lines,tokens,occurrences" + System.lineSeparator()
            + "10,75,2,48,\"/var,with,commas/Foo.java\",73,\"/var,with,commas/Bar.java\"" + System.lineSeparator();
        assertEquals(expectedReport, report);
    }

}
