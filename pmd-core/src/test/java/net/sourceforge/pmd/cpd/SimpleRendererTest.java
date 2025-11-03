/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.StringWriter;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.document.FileId;

class SimpleRendererTest {

    private static String getMultipleRepetitionsCode() {
        return "var x = [\n"
               + "  1, 1, 1, 1, 1, 1, 1, 1,\n"
               + "  0, 0, 0, 0, 0, 0, 0, 0,\n"
               + "  2, 2, 2, 2, 2, 2, 2, 2,\n"
               + "  0, 0, 0, 0, 0, 0, 0, 0,\n"
               + "  3, 3, 3, 3, 3, 3, 3, 3,\n"
               + "  0, 0, 0, 0, 0, 0, 0, 0,\n"
               + "  4, 4, 4, 4, 4, 4, 4, 4\n"
               + "];";
    }


    @Test
    void testWithOneDuplicationThreeMarks() throws Exception {
        CPDReportRenderer renderer = new SimpleRenderer();
        CpdTestUtils.CpdReportBuilder builder = new CpdTestUtils.CpdReportBuilder();
        FileId foo1 = CpdTestUtils.FOO_FILE_ID;

        builder.setFileContent(foo1, getMultipleRepetitionsCode());


        Mark mark1 = builder.createMark(",", foo1, 2, 2, 25, 26);
        Mark mark2 = builder.createMark(",", foo1, 4, 2, 25, 26);
        Mark mark3 = builder.createMark(",", foo1, 6, 2, 25, 26);

        builder.addMatch(mark1, mark2, mark3);

        StringWriter sw = new StringWriter();
        renderer.render(builder.build(), sw);
        String report = sw.toString();
        assertEquals("Found a 2 line (2 tokens) duplication in the following files: \n"
                     + "Starting at line 2 of /var/Foo.java\n"
                     + "Starting at line 4 of /var/Foo.java\n"
                     + "Starting at line 6 of /var/Foo.java\n"
                     + "\n"
                     + "------------------------v starting from here (col 25)\n"
                     + "  1, 1, 1, 1, 1, 1, 1, 1,\n"
                     + "  0, 0, 0, 0, 0, 0, 0, 0,\n"
                     + "------------------------^ ending here (col 25)\n",
            report
        );
    }
}
