/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.rule.xpath.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.reporting.Report;

class KotlinTypeIsExactlyFunctionTest extends BaseKotlinXPathFunctionTest {

    private static final String TYPE_IS_RESOURCE_DIR =
            "net/sourceforge/pmd/lang/kotlin/rule/xpath/typeis";

    @Test
    void typeIsExactlyMatchesExactType() {
        File kotlinFile = getResource(TYPE_IS_RESOURCE_DIR + "/CalendarUsage.kt");
        Report report = runXPath("//PropertyDeclaration[pmd-kotlin:typeIsExactly('java.util.Calendar')]", kotlinFile);
        assertNoErrors(report);
        assertViolationAtLine(report, 6, "Expected violation at line 6 (meeting: Calendar)");
    }

    @Test
    void typeIsExactlyDoesNotMatchSubtype() {
        // typeIsExactly('java.io.Serializable') must NOT match a property of type
        // SerializableSubtype (which implements Serializable but is not exactly it).
        File kotlinFile = getResource(TYPE_IS_RESOURCE_DIR + "/SerializableSubtype.kt");
        Report report = runXPath("//PropertyDeclaration[pmd-kotlin:typeIsExactly('java.io.Serializable')]", kotlinFile);
        assertNoErrors(report);
        assertEquals(0, report.getViolations().size(),
                "typeIsExactly should not match properties of SerializableSubtype");
    }

    @Test
    void typeIsExactlyMatchesNullableType() {
        // typeIsExactly must ignore the nullable marker: String? still matches 'kotlin.String'.
        File kotlinFile = getResource(TYPE_IS_RESOURCE_DIR + "/StringEquivalence.kt");
        Report report = runXPath("//PropertyDeclaration[pmd-kotlin:typeIsExactly('kotlin.String')]", kotlinFile);
        assertNoErrors(report);
        assertViolationAtLine(report, 10, "Expected violation at line 10 (nickname: String?)");
    }

    @Test
    void typeIsExactlyMatchesConstructorCallOnPostfixUnaryExpression() {
        // typeIsExactly on a throw's PostfixUnaryExpression (constructor call) must fire
        File kotlinFile = getResource(TYPE_IS_RESOURCE_DIR + "/ConstructorCallTypeCheck.kt");
        Report report = runXPath(
                "//JumpExpression[T-THROW and .//PostfixUnaryExpression["
                        + "pmd-kotlin:typeIsExactly('java.lang.Exception')]]",
                kotlinFile);
        assertNoErrors(report);
        assertViolationAtLine(report, 12, "Expected violation at line 12 (throw Exception)");
        assertNoViolationAtLine(report, 16, "Line 16 (throw RuntimeException) must NOT match typeIsExactly('java.lang.Exception')");
        assertNoViolationAtLine(report, 21, "Line 21 (throw IllegalArgumentException) must NOT match typeIsExactly('java.lang.Exception')");
    }

    @Test
    void typeIsExactlyDoesNotMatchInterfaceTypedPropertyWithConcreteInitializer() {
        // PropertyDeclaration val items: List<String> = ArrayList()
        // typeIsExactly('java.util.ArrayList') must NOT fire -- declared type is List, not ArrayList
        File kotlinFile = getResource(TYPE_IS_RESOURCE_DIR + "/ConstructorCallTypeCheck.kt");
        Report report = runXPath(
                "//PropertyDeclaration[pmd-kotlin:typeIsExactly('java.util.ArrayList')]",
                kotlinFile);
        assertNoErrors(report);
        assertTrue(report.getViolations().isEmpty(),
                "No PropertyDeclaration should match typeIsExactly('java.util.ArrayList') "
                        + "when declared type is List interface");
    }

}
