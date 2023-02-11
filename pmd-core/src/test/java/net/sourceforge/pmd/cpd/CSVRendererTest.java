/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.cpd.renderer.CPDReportRenderer;

class CSVRendererTest {
    private final Tokens tokens = new Tokens();
    @Test
    void testLineCountPerFile() throws IOException {
        CPDReportRenderer renderer = new CSVRenderer(true);
        List<Match> list = new ArrayList<>();
        Mark mark1 = createMark("public", "/var/Foo.java", 48);
        Mark mark2 = createMark("stuff", "/var/Bar.java", 73);
        Match match = new Match(75, mark1, mark2);

        list.add(match);
        StringWriter sw = new StringWriter();
        renderer.render(CPDReportTest.makeReport(list), sw);
        String report = sw.toString();
        String expectedReport = "tokens,occurrences" + PMD.EOL + "75,2,48,10,/var/Foo.java,73,20,/var/Bar.java"
                + PMD.EOL;

        assertEquals(expectedReport, report);
    }

    @Test
    void testFilenameEscapes() throws IOException {
        CPDReportRenderer renderer = new CSVRenderer();
        List<Match> list = new ArrayList<>();
        Mark mark1 = createMark("public", "/var,with,commas/Foo.java", 48);
        Mark mark2 = createMark("stuff", "/var,with,commas/Bar.java", 73);
        Match match = new Match(75, mark1, mark2);
        list.add(match);

        StringWriter sw = new StringWriter();
        renderer.render(CPDReportTest.makeReport(list), sw);
        String report = sw.toString();
        String expectedReport = "lines,tokens,occurrences" + PMD.EOL
                + "10,75,2,48,\"/var,with,commas/Foo.java\",73,\"/var,with,commas/Bar.java\"" + PMD.EOL;
        assertEquals(expectedReport, report);
    }

    private Mark createMark(String image, String tokenSrcID, int beginLine) {
        TokenEntry tok = tokens.addToken(image, tokenSrcID, beginLine, beginLine, beginLine, beginLine);
        return new Mark(tok);
    }
}
