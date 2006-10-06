/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package test.net.sourceforge.pmd.cpd;

import junit.framework.TestCase;
import net.sourceforge.pmd.cpd.FileReporter;
import net.sourceforge.pmd.cpd.ReportException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author Philippe T'Seyen
 */
public class FileReporterTest extends TestCase {
    public void testCreation() {
        try {
            new FileReporter(null);
            fail("expected NullPointerException");
        } catch (NullPointerException npe) {
        }
    }

    public void testEmptyReport() throws ReportException {
        File reportFile = new File("report.tmp");
        FileReporter fileReporter = new FileReporter(reportFile);
        fileReporter.report("");
        assertTrue(reportFile.exists());
        assertEquals(0, reportFile.length());
        assertTrue(reportFile.delete());
    }

    public void testReport() throws ReportException, IOException {
        String testString = "first line\nsecond line";
        File reportFile = new File("report.tmp");
        FileReporter fileReporter = new FileReporter(reportFile);

        fileReporter.report(testString);
        assertEquals(testString, readFile(reportFile));
        assertTrue(reportFile.delete());
    }

    public void testInvalidFile() {
        File reportFile = new File("/invalid_folder/report.tmp");
        FileReporter fileReporter = new FileReporter(reportFile);
        try {
            fileReporter.report("");
            fail("expected ReportException");
        } catch (ReportException re) {
        }
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
            if (reader != null) reader.close();
        }
    }
}