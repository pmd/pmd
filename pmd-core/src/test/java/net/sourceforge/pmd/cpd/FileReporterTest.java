/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import org.apache.commons.io.IOUtils;
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
        try (Reader reader = new FileReader(file)) {
            String text = IOUtils.toString(reader);
            return text.replaceAll("\\R", "\n");
        }
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(FileReporterTest.class);
    }
}
