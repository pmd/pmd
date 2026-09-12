/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.errorprone;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import net.sourceforge.pmd.lang.apex.multifile.ApexMultifileTestSupport;
import net.sourceforge.pmd.lang.rule.RuleSet;
import net.sourceforge.pmd.lang.rule.RuleSetLoader;
import net.sourceforge.pmd.reporting.Report;
import net.sourceforge.pmd.reporting.RuleViolation;

class ApexSemanticErrorsTest {

    private static final Path TEST_RESOURCES = Paths.get(
            "src/test/resources/net/sourceforge/pmd/lang/apex/rule/errorprone/ApexSemanticErrors");

    @TempDir
    private Path tempDir;

    @Test
    void standaloneRulesetExposesRule() {
        RuleSet ruleSet = new RuleSetLoader().loadFromResource("rulesets/apex/apex-semantic-errors.xml");
        assertNotNull(ruleSet.getRuleByName("ApexSemanticErrors"));
    }

    @Test
    void reportsSupportedErrorsWithNativeMessagesAndLocations() throws IOException {
        Report report = runRule(TEST_RESOURCES.resolve("project1"), "project1-cache");

        assertEquals(5, report.getViolations().size());
        assertViolation(report, "FieldTypeMismatch.cls", 4, 9, 43,
                "Incompatible types in assignment, from 'System.String' to 'System.Decimal'");
        assertViolation(report, "LocalVariableTypeMismatch.cls", 3, 17, 33,
                "Incompatible types in assignment, from 'System.String' to 'System.Integer'");
        assertViolation(report, "MissingType.cls", 3, 9, 22,
                "No variable or type found for 'MissingClass' on 'MissingType'");
        assertViolation(report, "WrongMethodArgument.cls", 6, 9, 31,
                "No matching method found for 'takesInteger' on 'WrongMethodArgument' taking arguments "
                        + "'System.String', wrong argument types for calling "
                        + "'private static void takesInteger(System.Integer value)'");
        assertViolation(report, "InvalidReturnType.cls", 3, 9, 25,
                "Incompatible return type, 'System.String' is not assignable to 'System.Integer'");

        assertNoViolation(report, "ValidField.cls");
        assertNoViolation(report, "ValidCrossClassReference.cls");
        assertNoViolation(report, "UnknownField.cls");
    }

    @Test
    void reportsUnknownFieldWithoutLocalStandardObjectExtension() throws IOException {
        Report report = runRule(TEST_RESOURCES.resolve("project2"), "project2-cache");

        assertEquals(1, report.getViolations().size());
        RuleViolation violation = report.getViolations().get(0);
        assertEquals("UnknownField.cls", violation.getFileId().getFileName());
        assertTrue(violation.getDescription().contains("Does_Not_Exist__c"));
    }

    @Test
    void localFieldMetadataMakesTheDiagnosticDisappear() throws IOException {
        Path sourceProject = TEST_RESOURCES.resolve("project3");
        Report withMetadata = runRule(sourceProject, "metadata-present-cache");
        assertEquals(0, withMetadata.getViolations().size());

        Path projectWithoutMetadata = tempDir.resolve("project-without-metadata");
        copyDirectory(sourceProject, projectWithoutMetadata);
        Files.delete(projectWithoutMetadata.resolve(
                "force-app/main/default/objects/Account/fields/Known_Text__c.field-meta.xml"));

        Report withoutMetadata = runRule(projectWithoutMetadata, "metadata-absent-cache");
        assertEquals(1, withoutMetadata.getViolations().size());
        assertTrue(withoutMetadata.getViolations().get(0).getDescription().contains("Known_Text__c"));
    }

    private Report runRule(Path project, String cacheDirectory) throws IOException {
        Path cache = Files.createDirectories(tempDir.resolve(cacheDirectory));
        return ApexMultifileTestSupport.runRule(cache, project, "errorprone", "ApexSemanticErrors");
    }

    private static void assertViolation(Report report, String fileName, int line, int startColumn, int endColumn,
                                        String message) {
        List<RuleViolation> matches = report.getViolations().stream()
                .filter(violation -> fileName.equals(violation.getFileId().getFileName()))
                .collect(Collectors.toList());
        assertEquals(1, matches.size(), "Expected exactly one violation in " + fileName);

        RuleViolation violation = matches.get(0);
        assertEquals(line, violation.getBeginLine());
        assertEquals(line, violation.getEndLine());
        assertEquals(startColumn, violation.getBeginColumn());
        assertEquals(endColumn, violation.getEndColumn());
        assertEquals(message, violation.getDescription());
    }

    private static void assertNoViolation(Report report, String fileName) {
        assertTrue(report.getViolations().stream()
                .noneMatch(violation -> fileName.equals(violation.getFileId().getFileName())));
    }

    private static void copyDirectory(Path source, Path target) throws IOException {
        try (Stream<Path> files = Files.walk(source)) {
            Iterator<Path> iterator = files.iterator();
            while (iterator.hasNext()) {
                Path path = iterator.next();
                Path destination = target.resolve(source.relativize(path));
                if (Files.isDirectory(path)) {
                    Files.createDirectories(destination);
                } else {
                    Files.copy(path, destination);
                }
            }
        }
    }
}
