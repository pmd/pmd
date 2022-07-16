/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.util.IOUtil;

/**
 * @author Philippe T'Seyen
 */
class FileReporterTest {

    @Test
    void testCreation() {
        new FileReporter((String) null);
        new FileReporter((File) null);
    }

    @Test
    void testEmptyReport() throws ReportException {
        File reportFile = new File("report.tmp");
        FileReporter fileReporter = new FileReporter(reportFile);
        fileReporter.report("");
        assertTrue(reportFile.exists());
        assertEquals(0L, reportFile.length());
        assertTrue(reportFile.delete());
    }

    @Test
    void testReport() throws ReportException, IOException {
        String testString = "first line\nsecond line";
        File reportFile = new File("report.tmp");
        FileReporter fileReporter = new FileReporter(reportFile);

        fileReporter.report(testString);
        assertEquals(testString, readFile(reportFile));
        assertTrue(reportFile.delete());
    }

    @Test
    void testInvalidFile() throws ReportException {
        File reportFile = new File("/invalid_folder/report.tmp");
        FileReporter fileReporter = new FileReporter(reportFile);
        assertThrows(ReportException.class, () -> fileReporter.report(""));
    }

    private String readFile(File file) throws IOException {
        try (Reader reader = new FileReader(file)) {
            String text = IOUtil.readToString(reader);
            return text.replaceAll("\\R", "\n");
        }
    }
}
