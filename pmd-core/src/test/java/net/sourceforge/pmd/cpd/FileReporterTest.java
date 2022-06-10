/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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
        Assertions.assertTrue(reportFile.exists());
        Assertions.assertEquals(0L, reportFile.length());
        Assertions.assertTrue(reportFile.delete());
    }

    @Test
    void testReport() throws ReportException, IOException {
        String testString = "first line\nsecond line";
        File reportFile = new File("report.tmp");
        FileReporter fileReporter = new FileReporter(reportFile);

        fileReporter.report(testString);
        Assertions.assertEquals(testString, readFile(reportFile));
        Assertions.assertTrue(reportFile.delete());
    }

    @Test
    void testInvalidFile() throws ReportException {
        File reportFile = new File("/invalid_folder/report.tmp");
        FileReporter fileReporter = new FileReporter(reportFile);
        Assertions.assertThrows(ReportException.class, () -> fileReporter.report(""));
    }

    private String readFile(File file) throws IOException {
        try (Reader reader = new FileReader(file)) {
            String text = IOUtils.toString(reader);
            return text.replaceAll("\\R", "\n");
        }
    }
}
