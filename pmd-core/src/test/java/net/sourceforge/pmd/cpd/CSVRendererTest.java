/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.cpd.renderer.CPDRenderer;

public class CSVRendererTest {
    @Test
    public void testLineCountPerFile() throws IOException {
        CPDRenderer renderer = new CSVRenderer(true);
        List<Match> list = new ArrayList<>();
        String codeFragment = "code\nfragment";
        Mark mark1 = createMark("public", "/var/Foo.java", 48, 10, codeFragment);
        Mark mark2 = createMark("stuff", "/var/Bar.java", 73, 20, codeFragment);
        Match match = new Match(75, mark1, mark2);

        list.add(match);
        StringWriter sw = new StringWriter();
        renderer.render(list.iterator(), sw);
        String report = sw.toString();
        String expectedReport = "tokens,occurrences" + PMD.EOL + "75,2,48,10,/var/Foo.java,73,20,/var/Bar.java"
                + PMD.EOL;

        assertEquals(expectedReport, report);
    }

    @Test
    public void testFilenameEscapes() throws IOException {
        CPDRenderer renderer = new CSVRenderer();
        List<Match> list = new ArrayList<>();
        String codeFragment = "code\nfragment";
        Mark mark1 = createMark("public", "/var,with,commas/Foo.java", 48, 10, codeFragment);
        Mark mark2 = createMark("stuff", "/var,with,commas/Bar.java", 73, 20, codeFragment);
        Match match = new Match(75, mark1, mark2);
        list.add(match);

        StringWriter sw = new StringWriter();
        renderer.render(list.iterator(), sw);
        String report = sw.toString();
        String expectedReport = "lines,tokens,occurrences" + PMD.EOL
                + "10,75,2,48,\"/var,with,commas/Foo.java\",73,\"/var,with,commas/Bar.java\"" + PMD.EOL;
        assertEquals(expectedReport, report);
    }

    private Mark createMark(String image, String tokenSrcID, int beginLine, int lineCount, String code) {
        Mark result = new Mark(new TokenEntry(image, tokenSrcID, beginLine));

        result.setLineCount(lineCount);
        result.setSourceCode(new SourceCode(new SourceCode.StringCodeLoader(code)));
        return result;
    }
}
