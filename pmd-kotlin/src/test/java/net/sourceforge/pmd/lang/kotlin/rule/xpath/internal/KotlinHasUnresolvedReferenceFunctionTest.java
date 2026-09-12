/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.rule.xpath.internal;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.reporting.Report;

class KotlinHasUnresolvedReferenceFunctionTest extends BaseKotlinXPathFunctionTest {

    private static final String RESOURCE_DIR =
            "net/sourceforge/pmd/lang/kotlin/rule/xpath/hasUnresolvedReference";

    @Test
    void unresolvedImportFiresOnMissingPackage() {
        Report report = runXPath(
                "//ImportHeader[pmd-kotlin:hasUnresolvedReference()]",
                getResource(RESOURCE_DIR + "/UnresolvedImports.kt"));
        assertNoErrors(report);
        assertViolationAtLine(report, 7, "Expected unresolved import at line 7 (com.example.external.MissingClass)");
        assertViolationAtLine(report, 8, "Expected unresolved import at line 8 (com.example.external.AnotherMissing)");
    }

    @Test
    void resolvedImportDoesNotFire() {
        Report report = runXPath(
                "//ImportHeader[pmd-kotlin:hasUnresolvedReference()]",
                getResource(RESOURCE_DIR + "/NoImports.kt"));
        assertNoErrors(report);
        assertTrue(report.getViolations().isEmpty(),
                "A file with no imports should have no UnresolvedType violations");
    }

    @Test
    void resolvedLocalReferenceDoesNotFire() {
        Report report = runXPath(
                "//FunctionDeclaration[pmd-kotlin:hasUnresolvedReference()]",
                getResource(RESOURCE_DIR + "/LocalClass.kt"));
        assertNoErrors(report);
        assertTrue(report.getViolations().isEmpty(),
                "A reference resolvable from source should not be reported as unresolved");
    }
}
