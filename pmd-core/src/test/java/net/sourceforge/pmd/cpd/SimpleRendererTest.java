/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

class SimpleRendererTest {

    @Test
    void dupTest() throws IOException, URISyntaxException {
        CPDConfiguration config = new CPDConfiguration();
        Path path1 = Paths.get(getClass().getResource("files/dup1.txt").toURI());
        Path path2 = Paths.get(getClass().getResource("files/dup2.txt").toURI());

        config.addInputPath(path1);
        config.addInputPath(path2);
        try (CpdAnalysis cpd = CpdAnalysis.create(config)) {

            cpd.performAnalysis(report -> {
                StringWriter sw = new StringWriter();
                SimpleRenderer renderer = new SimpleRenderer();
                try {
                    renderer.render(report, sw);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                assertEquals(("Found a 14 line (95 tokens) duplication in the following files: \n"
                             + "Starting at line 5 of " + path1 + "\n"
                             + "Starting at line 5 of " + path2 + "\n"
                             + "\n"
                             + "------------------v starting from here (col 19)\n"
                             + "public class dup1 {\n"
                             + "\n"
                             + "    public static void main(String[] args) {\n"
                             + "        System.out.println(\"Test1\");\n"
                             + "        System.out.println(\"Test2\");\n"
                             + "        System.out.println(\"Test3\");\n"
                             + "        System.out.println(\"Test4\");\n"
                             + "        System.out.println(\"Test5\");\n"
                             + "        System.out.println(\"Test6\");\n"
                             + "        System.out.println(\"Test7\");\n"
                             + "        System.out.println(\"Test8\");\n"
                             + "        System.out.println(\"Test9\");\n"
                             + "    }\n"
                             + "}\n"
                             + "^ ending here (col 1)\n").replace("\n", System.lineSeparator()),
                    sw.toString()
                );
            });
        }
    }

    @Test
    void testWithOneDuplicationThreeMarks() throws Exception {
        CPDConfiguration config = new CPDConfiguration();
        config.setMinimumTileSize(8);
        Path path1 = Paths.get(getClass().getResource("files/dupWithinFile.txt").toURI());
        config.addInputPath(path1);

        try (CpdAnalysis cpd = CpdAnalysis.create(config)) {

            cpd.performAnalysis(report -> {
                StringWriter sw = new StringWriter();
                SimpleRenderer renderer = new SimpleRenderer();
                try {
                    renderer.render(report, sw);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                assertEquals(("Found a 2 line (17 tokens) duplication in the following files: \n"
                             + "Starting at line 2 of " + path1 + "\n"
                             + "Starting at line 4 of " + path1 + "\n"
                             + "Starting at line 6 of " + path1 + "\n"
                             + "\n"
                             + "------------------------v starting from here (col 25)\n"
                             + "  1, 1, 1, 1, 1, 1, 1, 1,\n"
                             + "  0, 0, 0, 0, 0, 0, 0, 0,\n"
                             + "------------------------^ ending here (col 25)\n").replace("\n", System.lineSeparator()),
                    sw.toString()
                );
            });
        }
    }

    @Test
    void testWithOneDuplicationThreeMarksWithDiffMinMax() throws Exception {
        CPDConfiguration config = new CPDConfiguration();
        config.setMinimumTileSize(8);
        Path path1 = Paths.get(getClass().getResource("files/dupWithMinMax.txt").toURI());
        config.addInputPath(path1);

        try (CpdAnalysis cpd = CpdAnalysis.create(config)) {

            cpd.performAnalysis(report -> {
                StringWriter sw = new StringWriter();
                SimpleRenderer renderer = new SimpleRenderer();
                try {
                    renderer.render(report, sw);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                assertEquals(("Found a 2 line (17..23 tokens) duplication in the following files: \n"
                             + "Starting at line 2 of " + path1 + "\n"
                             + "Starting at line 4 of " + path1 + "\n"
                             + "Starting at line 6 of " + path1 + "\n"
                             + "\n"
                             + "---------------v starting from here (col 16)\n"
                             + "1,1,1,1,1,1,1,1,\n"
                             + "0,0,0,0,0,0,0,0,\n"
                             + "---------------^ ending here (col 16)\n").replace("\n", System.lineSeparator()),
                    sw.toString()
                );
            });
        }
    }

    @Test
    void testWithOneDuplicationThreeMarksWithDiffMinMaxPrintAllMarks() throws Exception {
        CPDConfiguration config = new CPDConfiguration();
        config.setMinimumTileSize(8);
        Path path1 = Paths.get(getClass().getResource("files/dupWithMinMax.txt").toURI());
        config.addInputPath(path1);

        try (CpdAnalysis cpd = CpdAnalysis.create(config)) {

            cpd.performAnalysis(report -> {
                StringWriter sw = new StringWriter();
                SimpleRenderer renderer = new SimpleRenderer();
                renderer.setPrintAllMarks(true);
                try {
                    renderer.render(report, sw);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                assertEquals(("Found a 2 line (17..23 tokens) duplication in the following files: \n"
                             + "Starting at line 2 of " + path1 + "\n"
                             + "Starting at line 4 of " + path1 + "\n"
                             + "Starting at line 6 of " + path1 + "\n"
                             + "\n"
                             + path1 + ":2:16\n"
                             + "---------------v starting from here (col 16)\n"
                             + "1,1,1,1,1,1,1,1,\n"
                             + "0,0,0,0,0,0,0,0,\n"
                             + "---------------^ ending here (col 16)\n"
                             + "\n"
                             + path1 + ":4:16\n"
                             + "---------------v starting from here (col 16)\n"
                             + "2,2,2,2,2,2,2,2,\n"
                             + "0,0,0,0,0,0,0,0,0,0,0,\n"
                             + "---------------------^ ending here (col 22)\n"
                             + "\n"
                             + path1 + ":6:16\n"
                             + "---------------v starting from here (col 16)\n"
                             + "3,3,3,3,3,3,3,3,\n"
                             + "0,0,0,0,0,0,0,0,0,0,0,\n"
                             + "---------------------^ ending here (col 22)\n"
                             + "\n").replace("\n", System.lineSeparator()),
                    sw.toString()
                );
            });
        }
    }

}
