/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.cpd;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.PMD;

import org.junit.Test;

public class CSVRendererTest {

    @Test
    public void testLineCountPerFile() {
        Renderer renderer = new CSVRenderer(true);
        List<Match> list = new ArrayList<Match>();
        String codeFragment = "code\nfragment";
        Mark mark1 = createMark("public", "/var/Foo.java", 48, 10, codeFragment);
        Mark mark2 = createMark("stuff", "/var/Bar.java", 73, 20, codeFragment);
        Match match = new Match(75, mark1, mark2);

        list.add(match);
        String report = renderer.render(list.iterator());
        String expectedReport =
            "tokens,occurrences" + PMD.EOL +
            "75,2,48,10,/var/Foo.java,73,20,/var/Bar.java" + PMD.EOL;

        assertEquals(expectedReport, report);
    }

    private Mark createMark(String image, String tokenSrcID, int beginLine, int lineCount, String code) {
        Mark result = new Mark(new TokenEntry(image, tokenSrcID, beginLine));

        result.setLineCount(lineCount);
        result.setSoureCodeSlice(code);
        return result;
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(CSVRendererTest.class);
    }
}

