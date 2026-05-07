/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.design;

import static net.sourceforge.pmd.lang.apex.multifile.ApexMultifileTestSupport.assertViolation;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import net.sourceforge.pmd.lang.apex.multifile.ApexMultifileTestSupport;
import net.sourceforge.pmd.reporting.Report;

class UnusedMethodTest {
    @TempDir
    private Path tempDir;

    @Test
    void findUnusedMethodsWithSfdxProject() throws Exception {
        Path testProjectDir = Paths.get("src/test/resources/net/sourceforge/pmd/lang/apex/rule/design/UnusedMethod/project1");
        Report report = runRule(testProjectDir);
        assertEquals(1, report.getViolations().size());
        assertViolation(report.getViolations().get(0), "Foo.cls", 10); // line 10 is method unusedMethod()
    }

    private Report runRule(Path testProjectDir) throws IOException {
        return ApexMultifileTestSupport.runRule(tempDir, testProjectDir, "design", "UnusedMethod");
    }
}
