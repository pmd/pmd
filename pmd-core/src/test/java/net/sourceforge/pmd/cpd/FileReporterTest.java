/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.junit.Test;

/**
 * @author Philippe T'Seyen
 */
public class FileReporterTest {

    @Test
    public void testCreation() {
        new FileReporter((String) null);
        new FileReporter((File) null);
    }

    @Test
    public void testEmptyReport() throws ReportException {
        File reportFile = new File("report.tmp");
        FileReporter fileReporter = new FileReporter(reportFile);
        fileReporter.report("");
        assertTrue(reportFile.exists());
        assertEquals(0L, reportFile.length());
        assertTrue(reportFile.delete());
    }

    @Test
    public void testReport() throws ReportException, IOException {
        String testString = "first line\nsecond line";
        File reportFile = new File("report.tmp");
        FileReporter fileReporter = new FileReporter(reportFile);

        fileReporter.report(testString);
        assertEquals(testString, readFile(reportFile));
        assertTrue(reportFile.delete());
    }

    @Test(expected = ReportException.class)
    public void testInvalidFile() throws ReportException {
        File reportFile = new File("/invalid_folder/report.tmp");
        FileReporter fileReporter = new FileReporter(reportFile);
        fileReporter.report("");
    }

    private String readFile(File file) throws IOException {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            StringBuffer buffer = new StringBuffer();
            String line = reader.readLine();
            while (line != null) {
                buffer.append(line);
                line = reader.readLine();
                if (line != null) {
                    buffer.append('\n');
                }
            }
            return buffer.toString();
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(FileReporterTest.class);
    }
}
