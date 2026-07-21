/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.rule.xpath.internal;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.reporting.Report;

class KotlinIsNullableFunctionTest extends BaseKotlinXPathFunctionTest {

    private static final String RESOURCE_DIR =
            "net/sourceforge/pmd/lang/kotlin/rule/xpath/isNullable";

    @Test
    void isNullableMatchesNullableListProperty() {
        Report report = runXPath("//PropertyDeclaration[pmd-kotlin:isNullable()]",
                getResource(RESOURCE_DIR + "/NullableTypes.kt"));
        assertNoErrors(report);
        assertViolationAtLine(report, 3, "Expected violation at line 3 (items: List<String>?)");
    }

    @Test
    void isNullableMatchesNullableStringProperty() {
        Report report = runXPath("//PropertyDeclaration[pmd-kotlin:isNullable()]",
                getResource(RESOURCE_DIR + "/NullableTypes.kt"));
        assertNoErrors(report);
        assertViolationAtLine(report, 15, "Expected violation at line 15 (tag: String?)");
    }

    @Test
    void isNullableDoesNotMatchNonNullableListProperty() {
        Report report = runXPath("//PropertyDeclaration[pmd-kotlin:isNullable()]",
                getResource(RESOURCE_DIR + "/NullableTypes.kt"));
        assertNoErrors(report);
        assertNoViolationAtLine(report, 6, "Did not expect violation at line 6 (names: List<String>)");
    }

    @Test
    void isNullableMatchesNullableReturnType() {
        Report report = runXPath("//FunctionDeclaration[pmd-kotlin:isNullable()]",
                getResource(RESOURCE_DIR + "/NullableTypes.kt"));
        assertNoErrors(report);
        assertViolationAtLine(report, 9, "Expected violation at line 9 (findItem(): String?)");
    }

    @Test
    void isNullableDoesNotMatchNonNullableReturnType() {
        Report report = runXPath("//FunctionDeclaration[pmd-kotlin:isNullable()]",
                getResource(RESOURCE_DIR + "/NullableTypes.kt"));
        assertNoErrors(report);
        assertNoViolationAtLine(report, 12, "Did not expect violation at line 12 (getItem(): String)");
    }

    @Test
    void isNullableMatchesNullableFunctionParameter() {
        Report report = runXPath("//FunctionValueParameter[pmd-kotlin:isNullable()]",
                getResource(RESOURCE_DIR + "/NullableTypes.kt"));
        assertNoErrors(report);
        assertViolationAtLine(report, 18, "Expected violation at line 18 (input: String?)");
    }

    @Test
    void isNullableDoesNotMatchNonNullableFunctionParameter() {
        Report report = runXPath("//FunctionValueParameter[pmd-kotlin:isNullable()]",
                getResource(RESOURCE_DIR + "/NullableTypes.kt"));
        assertNoErrors(report);
        assertNoViolationAtLine(report, 21, "Did not expect violation at line 21 (input: String)");
    }
}
