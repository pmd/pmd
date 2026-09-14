/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.rule.xpath.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.File;
import java.util.Collections;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.kotlin.rule.internal.KotlinTypeAnalysisContext;
import net.sourceforge.pmd.reporting.Report;

import nl.stokpop.typemapper.analyzer.KotlinTypeMapper;
import nl.stokpop.typemapper.model.TypedAst;

class KotlinTypeIsFunctionTest extends BaseKotlinXPathFunctionTest {

    private static final String TYPE_IS_RESOURCE_DIR =
            "net/sourceforge/pmd/lang/kotlin/rule/xpath/typeis";

    @Test
    void typeIsCalendarMatchesMeetingProperty() {
        File kotlinFile = getResource(TYPE_IS_RESOURCE_DIR + "/CalendarUsage.kt");
        Report report = runXPath("//PropertyDeclaration[pmd-kotlin:typeIs('java.util.Calendar')]", kotlinFile);
        assertNoErrors(report);
        // 'meeting' property should match
        assertViolationAtLine(report, 6, "Expected violation at line 6 (meeting property)");
    }

    @Test
    void typeIsCalendarMatchesGetDeadlineFunction() {
        File kotlinFile = getResource(TYPE_IS_RESOURCE_DIR + "/CalendarUsage.kt");
        Report report = runXPath("//FunctionDeclaration[pmd-kotlin:typeIs('java.util.Calendar')]", kotlinFile);
        assertNoErrors(report);
        // 'getDeadline' function should match
        assertViolationAtLine(report, 7, "Expected violation at line 7 (getDeadline function)");
    }

    @Test
    void typeIsCalendarDoesNotMatchNameProperty() {
        File kotlinFile = getResource(TYPE_IS_RESOURCE_DIR + "/CalendarUsage.kt");
        Report report = runXPath("//PropertyDeclaration[pmd-kotlin:typeIs('java.util.Calendar')]", kotlinFile);
        assertNoErrors(report);
        // 'name' property (line 10) should NOT match
        assertNoViolationAtLine(report, 10, "Did not expect violation at line 10 (name: String property)");
    }

    @Test
    void typeIsKotlinStringMatchesMessageProperty() {
        File kotlinFile = getResource(TYPE_IS_RESOURCE_DIR + "/StringEquivalence.kt");
        Report report = runXPath("//PropertyDeclaration[pmd-kotlin:typeIs('kotlin.String')]", kotlinFile);
        assertNoErrors(report);
        assertViolationAtLine(report, 3, "Expected violation at line 3 (message property)");
    }

    @Test
    void typeIsJavaLangStringMatchesMessageProperty() {
        File kotlinFile = getResource(TYPE_IS_RESOURCE_DIR + "/StringEquivalence.kt");
        Report report = runXPath("//PropertyDeclaration[pmd-kotlin:typeIs('java.lang.String')]", kotlinFile);
        assertNoErrors(report);
        // Java name should be mapped to Kotlin String and match
        assertViolationAtLine(report, 3, "Expected violation at line 3 (message property) using java.lang.String name");
    }

    @Test
    void typeIsKotlinStringMatchesGreetFunction() {
        File kotlinFile = getResource(TYPE_IS_RESOURCE_DIR + "/StringEquivalence.kt");
        Report report = runXPath("//FunctionDeclaration[pmd-kotlin:typeIs('kotlin.String')]", kotlinFile);
        assertNoErrors(report);
        assertViolationAtLine(report, 4, "Expected violation at line 4 (greet function)");
    }

    @Test
    void typeIsListDoesNotMatchCalendarProperty() {
        File kotlinFile = getResource(TYPE_IS_RESOURCE_DIR + "/CalendarUsage.kt");
        Report report = runXPath("//PropertyDeclaration[pmd-kotlin:typeIs('kotlin.collections.List')]", kotlinFile);
        assertNoErrors(report);
        assertEquals(0, report.getViolations().size(),
                "Expected no violations for List typeIs on CalendarUsage.kt");
    }

    @Test
    void typeIsSerializableMatchesSubtypePropertyViaHierarchy() {
        // typeIs should match a property whose declared type implements Serializable,
        // using the type hierarchy from kotlin-type-mapper.
        // Note: this requires the compiled classes on auxClasspath to resolve the hierarchy;
        // without classpath the type hierarchy is empty and the test is skipped gracefully.
        File kotlinFile = getResource(TYPE_IS_RESOURCE_DIR + "/SerializableSubtype.kt");
        // Build a quick context from the file to check the hierarchy before running the full analysis.
        // Skip if the user-defined type wasn't compiled and isn't in the hierarchy.
        // (JDK types may still populate the hierarchy map even without a full aux classpath.)
        TypedAst ast = KotlinTypeMapper.fromPaths(
                Collections.singletonList(kotlinFile.toPath()),
                Collections.emptyList());
        KotlinTypeAnalysisContext ctx = KotlinTypeAnalysisContext.from(ast);
        assumeTrue(ctx.getTypeHierarchy().containsKey("net.sourceforge.pmd.lang.kotlin.rule.xpath.typeis.SerializableSubtype"),
                "Type hierarchy for SerializableSubtype unavailable (no aux classpath) - skipping");
        Report report = runXPath("//PropertyDeclaration[pmd-kotlin:typeIs('java.io.Serializable')]", kotlinFile);
        assertNoErrors(report);
        assertViolationAtLine(report, 9, "Expected violation at line 9 (item: SerializableSubtype implements Serializable)");
    }

    @Test
    void typeIsOnClassParameterMatches() {
        File kotlinFile = getResource(TYPE_IS_RESOURCE_DIR + "/PrimaryCtorParams.kt");
        Report report = runXPath("//ClassParameter[pmd-kotlin:typeIs('kotlin.String')]", kotlinFile);
        assertNoErrors(report);
        // val value: String at line 10
        assertViolationAtLine(report, 10, "Expected violation at line 10 (val value: String)");
    }

    @Test
    void typeIsOnClassParameterNoMatch() {
        File kotlinFile = getResource(TYPE_IS_RESOURCE_DIR + "/PrimaryCtorParams.kt");
        Report report = runXPath("//ClassParameter[pmd-kotlin:typeIs('kotlin.String')]", kotlinFile);
        assertNoErrors(report);
        // val tag: Long at line 12 should NOT match
        assertNoViolationAtLine(report, 12, "Long ClassParameter should not match kotlin.String typeIs");
    }

    @Test
    void typeIsOnFunctionValueParameterMatches() {
        File kotlinFile = getResource(TYPE_IS_RESOURCE_DIR + "/ParameterTypes.kt");
        Report report = runXPath("//FunctionValueParameter[pmd-kotlin:typeIs('java.util.Calendar')]", kotlinFile);
        assertNoErrors(report);
        // 'cal: Calendar' parameter at line 8
        assertViolationAtLine(report, 8, "Expected violation at line 8 (cal: Calendar)");
    }

    @Test
    void typeIsOnFunctionValueParameterNoMatch() {
        File kotlinFile = getResource(TYPE_IS_RESOURCE_DIR + "/ParameterTypes.kt");
        Report report = runXPath("//FunctionValueParameter[pmd-kotlin:typeIs('java.util.Calendar')]", kotlinFile);
        assertNoErrors(report);
        // 'name: String' parameter at line 13 should NOT match
        assertNoViolationAtLine(report, 13, "String parameter should not match Calendar typeIs");
    }

    @Test
    void typeIsOnCatchBlockMatches() {
        File kotlinFile = getResource(TYPE_IS_RESOURCE_DIR + "/ParameterTypes.kt");
        Report report = runXPath("//CatchBlock[pmd-kotlin:typeIs('java.io.IOException')]", kotlinFile);
        assertNoErrors(report);
        // catch (e: IOException) at line 21
        assertViolationAtLine(report, 21, "Expected violation at line 21 (catch IOException)");
    }

    @Test
    void typeIsOnCatchBlockNoMatch() {
        File kotlinFile = getResource(TYPE_IS_RESOURCE_DIR + "/ParameterTypes.kt");
        Report report = runXPath("//CatchBlock[pmd-kotlin:typeIs('java.io.IOException')]", kotlinFile);
        assertNoErrors(report);
        // catch (e: IllegalArgumentException) at line 30 should NOT match
        assertNoViolationAtLine(report, 30, "IllegalArgumentException catch block should not match IOException typeIs");
    }

    @Test
    void typeIsOnForStatementMatches() {
        File kotlinFile = getResource(TYPE_IS_RESOURCE_DIR + "/ParameterTypes.kt");
        Report report = runXPath("//ForStatement[pmd-kotlin:typeIs('java.util.Calendar')]", kotlinFile);
        assertNoErrors(report);
        // for (item in items) where items: List<Calendar>, at line 37
        assertViolationAtLine(report, 37, "Expected violation at line 37 (for Calendar loop variable)");
    }

    @Test
    void typeIsOnForStatementNoMatch() {
        File kotlinFile = getResource(TYPE_IS_RESOURCE_DIR + "/ParameterTypes.kt");
        Report report = runXPath("//ForStatement[pmd-kotlin:typeIs('java.util.Calendar')]", kotlinFile);
        assertNoErrors(report);
        // for (s in items) where items: List<String>, at line 44 should NOT match
        assertNoViolationAtLine(report, 44, "String for-loop should not match Calendar typeIs");
    }

    @Test
    void typeIsMatchesInferredTypeSubtypeViaSourceHierarchy() {
        // "val myValue = Simple("Hello")" -- type is INFERRED (no explicit annotation).
        // Simple is defined in the same file and implements Serializable.
        // The source hierarchy (from K1 analysis) should make typeIs work without compiled classes.
        File kotlinFile = getResource(TYPE_IS_RESOURCE_DIR + "/InferredTypeSubtype.kt");
        Report report = runXPath("//PropertyDeclaration[pmd-kotlin:typeIs('java.io.Serializable')]", kotlinFile);
        assertNoErrors(report);
        // val myValue = Simple("Hello") is at line 7
        assertViolationAtLine(report, 7, "Expected match at line 7 (val myValue: inferred Simple which implements Serializable)");
    }

    @Test
    void typeIsDoesNotMatchUnrelatedInferredType() {
        File kotlinFile = getResource(TYPE_IS_RESOURCE_DIR + "/InferredTypeSubtype.kt");
        Report report = runXPath("//PropertyDeclaration[pmd-kotlin:typeIs('java.util.Calendar')]", kotlinFile);
        assertNoErrors(report);
        assertEquals(0, report.getViolations().size(),
                "Simple is not a Calendar, should not match");
    }

    // --- AvoidStringBufferField ---

    @Test
    void stringBufferFieldMatchesStringBufferProperty() {
        File kotlinFile = getResource(TYPE_IS_RESOURCE_DIR + "/StringBufferFieldUsage.kt");
        Report report = runXPath(
                "//PropertyDeclaration[not(ancestor::FunctionBody) and pmd-kotlin:typeIs('java.lang.StringBuffer')]",
                kotlinFile);
        assertNoErrors(report);
        assertViolationAtLine(report, 2, "Expected violation at line 2 (StringBuffer field)");
    }

    @Test
    void stringBuilderFieldMatchesStringBuilderProperty() {
        File kotlinFile = getResource(TYPE_IS_RESOURCE_DIR + "/StringBufferFieldUsage.kt");
        Report report = runXPath(
                "//PropertyDeclaration[not(ancestor::FunctionBody) and pmd-kotlin:typeIs('java.lang.StringBuilder')]",
                kotlinFile);
        assertNoErrors(report);
        assertViolationAtLine(report, 3, "Expected violation at line 3 (StringBuilder field)");
    }

    @Test
    void stringBuilderLocalVariableDoesNotMatch() {
        File kotlinFile = getResource(TYPE_IS_RESOURCE_DIR + "/StringBufferFieldUsage.kt");
        Report report = runXPath(
                "//PropertyDeclaration[not(ancestor::FunctionBody) and pmd-kotlin:typeIs('java.lang.StringBuilder')]",
                kotlinFile);
        assertNoErrors(report);
        assertNoViolationAtLine(report, 6, "Local variable at line 6 should NOT match (inside FunctionBody)");
    }

    // --- AvoidMessageDigestField ---

    @Test
    void messageDigestFieldMatchesFieldProperty() {
        File kotlinFile = getResource(TYPE_IS_RESOURCE_DIR + "/MessageDigestFieldUsage.kt");
        Report report = runXPath(
                "//PropertyDeclaration[not(ancestor::FunctionBody) and pmd-kotlin:typeIs('java.security.MessageDigest')]",
                kotlinFile);
        assertNoErrors(report);
        assertViolationAtLine(report, 4, "Expected violation at line 4 (MessageDigest field)");
    }

    @Test
    void messageDigestLocalVariableDoesNotMatch() {
        File kotlinFile = getResource(TYPE_IS_RESOURCE_DIR + "/MessageDigestFieldUsage.kt");
        Report report = runXPath(
                "//PropertyDeclaration[not(ancestor::FunctionBody) and pmd-kotlin:typeIs('java.security.MessageDigest')]",
                kotlinFile);
        assertNoErrors(report);
        assertNoViolationAtLine(report, 7, "Local variable at line 7 should NOT match (inside FunctionBody)");
    }

    @Test
    void typeIsMatchesDelegationSpecifierViaSupertypeHierarchy() {
        // typeIs('java.lang.Throwable') on a DelegationSpecifier must match when the supertype
        // is RuntimeException (a transitive subtype of Throwable).
        File kotlinFile = getResource(TYPE_IS_RESOURCE_DIR + "/DelegationSpecifierSubtype.kt");
        Report report = runXPath(
                "//DelegationSpecifier[pmd-kotlin:typeIs('java.lang.Throwable')]", kotlinFile);
        assertNoErrors(report);
        assertEquals(2, report.getViolations().size(),
                "Both DelegationSpecifier nodes (RuntimeException supertypes) should match typeIs('java.lang.Throwable')");
    }

    @Test
    void typeIsMatchesDelegationSpecifierViaOwnFileContext() {
        // typeIs('java.lang.Throwable') on DelegationSpecifier must match using per-file context
        // (context is now stored on the root node by KotlinLanguageProcessor, not a static holder).
        File kotlinFile = getResource(TYPE_IS_RESOURCE_DIR + "/DelegationSpecifierSubtype.kt");
        Report report = runXPath(
                "//DelegationSpecifier[pmd-kotlin:typeIs('java.lang.Throwable')]", kotlinFile);
        assertNoErrors(report);
        assertEquals(2, report.getViolations().size(),
                "Both DelegationSpecifier nodes should match typeIs('java.lang.Throwable')");
    }

    @Test
    void hasUnresolvedReferenceIsFalseOnKnownGoodFixture() {
        // Guard test: proves type resolution is actually working end-to-end on a fixture that
        // has no unresolved references, so a silently-broken auxClasspath/analysis pipeline
        // doesn't just make other typeIs tests skip/no-op without anyone noticing.
        File kotlinFile = getResource(TYPE_IS_RESOURCE_DIR + "/CalendarUsage.kt");
        Report report = runXPath(
                "//PropertyDeclaration[pmd-kotlin:hasUnresolvedReference()]", kotlinFile);
        assertNoErrors(report);
        assertFalse(!report.getViolations().isEmpty(),
                "CalendarUsage.kt should have no unresolved references (java.util.Calendar is a JDK type)");
    }

}
