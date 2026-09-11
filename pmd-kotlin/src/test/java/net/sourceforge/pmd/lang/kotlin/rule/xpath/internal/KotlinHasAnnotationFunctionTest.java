/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.rule.xpath.internal;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.reporting.Report;

class KotlinHasAnnotationFunctionTest extends BaseKotlinXPathFunctionTest {

    private static final String RESOURCE_DIR =
            "net/sourceforge/pmd/lang/kotlin/rule/xpath/hasAnnotation";

    @Test
    void hasAnnotationSimpleNameMatchesProperty() {
        Report report = runXPath(
                "//PropertyDeclaration[pmd-kotlin:hasAnnotation('Column')]",
                getResource(RESOURCE_DIR + "/AnnotatedEntities.kt"));
        assertNoErrors(report);
        assertViolationAtLine(report, 11, "Expected @Column on name property (line 11)");
        assertViolationAtLine(report, 14, "Expected @Column on email property (line 14)");
        assertNoViolationAtLine(report, 17, "Did not expect @Column on id (line 17)");
    }

    @Test
    void hasAnnotationFqnMatchesFunction() {
        Report report = runXPath(
                "//FunctionDeclaration[pmd-kotlin:hasAnnotation('kotlin.Deprecated')]",
                getResource(RESOURCE_DIR + "/AnnotatedEntities.kt"));
        assertNoErrors(report);
        assertViolationAtLine(report, 20, "FQN match should find kotlin.Deprecated on oldMethod (line 20)");
        assertNoViolationAtLine(report, 22, "normalMethod (line 22) has no annotation");
    }

    @Test
    void hasAnnotationSimpleNameMatchesFunction() {
        Report report = runXPath(
                "//FunctionDeclaration[pmd-kotlin:hasAnnotation('Deprecated')]",
                getResource(RESOURCE_DIR + "/AnnotatedEntities.kt"));
        assertNoErrors(report);
        assertViolationAtLine(report, 20, "Expected @Deprecated on oldMethod (line 20)");
        assertNoViolationAtLine(report, 22, "Did not expect @Deprecated on normalMethod (line 22)");
    }

    @Test
    void hasAnnotationSimpleNameMatchesClass() {
        Report report = runXPath(
                "//ClassDeclaration[pmd-kotlin:hasAnnotation('Entity')]",
                getResource(RESOURCE_DIR + "/AnnotatedEntities.kt"));
        assertNoErrors(report);
        assertViolationAtLine(report, 6,
                "Expected @Entity on UserEntity class (starts at line 6, before annotation on line 7)");
    }

    @Test
    void hasAnnotationDoesNotMatchWrongName() {
        Report report = runXPath(
                "//PropertyDeclaration[pmd-kotlin:hasAnnotation('Entity')]",
                getResource(RESOURCE_DIR + "/AnnotatedEntities.kt"));
        assertNoErrors(report);
        assertTrue(report.getViolations().isEmpty(), "No property should match @Entity");
    }

    @Test
    void hasAnnotationDoesNotMatchWrongFqn() {
        Report report = runXPath(
                "//PropertyDeclaration[pmd-kotlin:hasAnnotation('com.other.Column')]",
                getResource(RESOURCE_DIR + "/AnnotatedEntities.kt"));
        assertNoErrors(report);
        assertNoViolationAtLine(report, 11,
                "Should not match com.other.Column when actual annotation is javax.persistence.Column");
    }

    @Test
    void hasAnnotationSimpleNameMatchesFqnWrittenAnnotation() {
        Report report = runXPath(
                "//ClassDeclaration[pmd-kotlin:hasAnnotation('Service')]",
                getResource(RESOURCE_DIR + "/AnnotatedEntities.kt"));
        assertNoErrors(report);
        assertFalse(report.getViolations().isEmpty(),
                "Expected hasAnnotation('Service') to match @org.springframework.stereotype.Service");
        assertNoViolationAtLine(report, 6, "UserEntity must not match hasAnnotation('Service')");
    }

    @Test
    void hasAnnotationDoesNotMatchWrongFqnJunit() {
        // With auxClasspath configured, org.junit.jupiter.api.Test resolves to its real FQN,
        // so a query for a different FQN sharing only the simple name ('Test') must NOT match.
        Report report = runXPath(
                "//FunctionDeclaration[pmd-kotlin:hasAnnotation('com.other.Test')]",
                getResource(RESOURCE_DIR + "/TestCaseExample.kt"),
                true);
        assertNoErrors(report);
        assertTrue(report.getViolations().isEmpty(),
                "hasAnnotation('com.other.Test') must not match @org.junit.jupiter.api.Test");
    }
}
