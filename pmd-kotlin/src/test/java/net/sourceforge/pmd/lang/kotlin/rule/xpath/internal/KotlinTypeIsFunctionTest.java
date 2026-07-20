/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.rule.xpath.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.File;
import java.net.URL;
import java.util.Collections;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.PmdAnalysis;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.kotlin.rule.internal.KotlinTypeAnalysisContext;
import net.sourceforge.pmd.lang.rule.Rule;
import net.sourceforge.pmd.lang.rule.RuleSet;
import net.sourceforge.pmd.lang.rule.xpath.XPathRule;
import net.sourceforge.pmd.lang.rule.xpath.XPathVersion;
import net.sourceforge.pmd.reporting.Report;

import nl.stokpop.typemapper.analyzer.KotlinTypeMapper;
import nl.stokpop.typemapper.model.TypedAst;

class KotlinTypeIsFunctionTest {

    private static final String TYPE_IS_RESOURCE_DIR =
            "net/sourceforge/pmd/lang/kotlin/rule/xpath/typeIs";

    @Test
    void typeIsCalendarMatchesMeetingProperty() {
        File kotlinFile = getResource(TYPE_IS_RESOURCE_DIR + "/CalendarUsage.kt");
        Report report = runXPath("//PropertyDeclaration[pmd-kotlin:typeIs('java.util.Calendar')]", kotlinFile);
        assertTrue(report.getProcessingErrors().isEmpty(), "No processing errors expected");
        // 'meeting' property should match
        assertTrue(report.getViolations().stream().anyMatch(v -> v.getBeginLine() == 6),
                "Expected violation at line 6 (meeting property)");
    }

    @Test
    void typeIsCalendarMatchesGetDeadlineFunction() {
        File kotlinFile = getResource(TYPE_IS_RESOURCE_DIR + "/CalendarUsage.kt");
        Report report = runXPath("//FunctionDeclaration[pmd-kotlin:typeIs('java.util.Calendar')]", kotlinFile);
        assertTrue(report.getProcessingErrors().isEmpty(), "No processing errors expected");
        // 'getDeadline' function should match
        assertTrue(report.getViolations().stream().anyMatch(v -> v.getBeginLine() == 7),
                "Expected violation at line 7 (getDeadline function)");
    }

    @Test
    void typeIsCalendarDoesNotMatchNameProperty() {
        File kotlinFile = getResource(TYPE_IS_RESOURCE_DIR + "/CalendarUsage.kt");
        Report report = runXPath("//PropertyDeclaration[pmd-kotlin:typeIs('java.util.Calendar')]", kotlinFile);
        assertTrue(report.getProcessingErrors().isEmpty(), "No processing errors expected");
        // 'name' property (line 10) should NOT match
        assertTrue(report.getViolations().stream().noneMatch(v -> v.getBeginLine() == 10),
                "Did not expect violation at line 10 (name: String property)");
    }

    @Test
    void typeIsKotlinStringMatchesMessageProperty() {
        File kotlinFile = getResource(TYPE_IS_RESOURCE_DIR + "/StringEquivalence.kt");
        Report report = runXPath("//PropertyDeclaration[pmd-kotlin:typeIs('kotlin.String')]", kotlinFile);
        assertTrue(report.getProcessingErrors().isEmpty(), "No processing errors expected");
        assertTrue(report.getViolations().stream().anyMatch(v -> v.getBeginLine() == 3),
                "Expected violation at line 3 (message property)");
    }

    @Test
    void typeIsJavaLangStringMatchesMessageProperty() {
        File kotlinFile = getResource(TYPE_IS_RESOURCE_DIR + "/StringEquivalence.kt");
        Report report = runXPath("//PropertyDeclaration[pmd-kotlin:typeIs('java.lang.String')]", kotlinFile);
        assertTrue(report.getProcessingErrors().isEmpty(), "No processing errors expected");
        // Java name should be mapped to Kotlin String and match
        assertTrue(report.getViolations().stream().anyMatch(v -> v.getBeginLine() == 3),
                "Expected violation at line 3 (message property) using java.lang.String name");
    }

    @Test
    void typeIsKotlinStringMatchesGreetFunction() {
        File kotlinFile = getResource(TYPE_IS_RESOURCE_DIR + "/StringEquivalence.kt");
        Report report = runXPath("//FunctionDeclaration[pmd-kotlin:typeIs('kotlin.String')]", kotlinFile);
        assertTrue(report.getProcessingErrors().isEmpty(), "No processing errors expected");
        assertTrue(report.getViolations().stream().anyMatch(v -> v.getBeginLine() == 4),
                "Expected violation at line 4 (greet function)");
    }

    @Test
    void typeIsListDoesNotMatchCalendarProperty() {
        File kotlinFile = getResource(TYPE_IS_RESOURCE_DIR + "/CalendarUsage.kt");
        Report report = runXPath("//PropertyDeclaration[pmd-kotlin:typeIs('kotlin.collections.List')]", kotlinFile);
        assertTrue(report.getProcessingErrors().isEmpty(), "No processing errors expected");
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
        assumeTrue(ctx.getTypeHierarchy().containsKey("nl.stokpop.kotlin.SerializableSubtype"),
                "Type hierarchy for SerializableSubtype unavailable (no aux classpath) - skipping");
        Report report = runXPath("//PropertyDeclaration[pmd-kotlin:typeIs('java.io.Serializable')]", kotlinFile);
        assertTrue(report.getProcessingErrors().isEmpty(), "No processing errors expected");
        assertTrue(report.getViolations().stream().anyMatch(v -> v.getBeginLine() == 9),
                "Expected violation at line 9 (item: SerializableSubtype implements Serializable)");
    }

    @Test
    void typeIsExactlyMatchesExactType() {
        File kotlinFile = getResource(TYPE_IS_RESOURCE_DIR + "/CalendarUsage.kt");
        Report report = runXPath("//PropertyDeclaration[pmd-kotlin:typeIsExactly('java.util.Calendar')]", kotlinFile);
        assertTrue(report.getProcessingErrors().isEmpty(), "No processing errors expected");
        assertTrue(report.getViolations().stream().anyMatch(v -> v.getBeginLine() == 6),
                "Expected violation at line 6 (meeting: Calendar)");
    }

    @Test
    void typeIsExactlyDoesNotMatchSubtype() {
        // typeIsExactly('java.io.Serializable') must NOT match a property of type
        // SerializableSubtype (which implements Serializable but is not exactly it).
        File kotlinFile = getResource(TYPE_IS_RESOURCE_DIR + "/SerializableSubtype.kt");
        Report report = runXPath("//PropertyDeclaration[pmd-kotlin:typeIsExactly('java.io.Serializable')]", kotlinFile);
        assertTrue(report.getProcessingErrors().isEmpty(), "No processing errors expected");
        assertEquals(0, report.getViolations().size(),
                "typeIsExactly should not match properties of SerializableSubtype");
    }

    @Test
    void typeIsOnClassParameterMatches() {
        File kotlinFile = getResource(TYPE_IS_RESOURCE_DIR + "/PrimaryCtorParams.kt");
        Report report = runXPath("//ClassParameter[pmd-kotlin:typeIs('kotlin.String')]", kotlinFile);
        assertTrue(report.getProcessingErrors().isEmpty(), "No processing errors expected");
        // val value: String at line 10
        assertTrue(report.getViolations().stream().anyMatch(v -> v.getBeginLine() == 10),
                "Expected violation at line 10 (val value: String)");
    }

    @Test
    void typeIsOnClassParameterNoMatch() {
        File kotlinFile = getResource(TYPE_IS_RESOURCE_DIR + "/PrimaryCtorParams.kt");
        Report report = runXPath("//ClassParameter[pmd-kotlin:typeIs('kotlin.String')]", kotlinFile);
        assertTrue(report.getProcessingErrors().isEmpty(), "No processing errors expected");
        // val tag: Long at line 12 should NOT match
        assertTrue(report.getViolations().stream().noneMatch(v -> v.getBeginLine() == 12),
                "Long ClassParameter should not match kotlin.String typeIs");
    }

    @Test
    void typeIsOnFunctionValueParameterMatches() {
        File kotlinFile = getResource(TYPE_IS_RESOURCE_DIR + "/ParameterTypes.kt");
        Report report = runXPath("//FunctionValueParameter[pmd-kotlin:typeIs('java.util.Calendar')]", kotlinFile);
        assertTrue(report.getProcessingErrors().isEmpty(), "No processing errors expected");
        // 'cal: Calendar' parameter at line 8
        assertTrue(report.getViolations().stream().anyMatch(v -> v.getBeginLine() == 8),
                "Expected violation at line 8 (cal: Calendar)");
    }

    @Test
    void typeIsOnFunctionValueParameterNoMatch() {
        File kotlinFile = getResource(TYPE_IS_RESOURCE_DIR + "/ParameterTypes.kt");
        Report report = runXPath("//FunctionValueParameter[pmd-kotlin:typeIs('java.util.Calendar')]", kotlinFile);
        assertTrue(report.getProcessingErrors().isEmpty(), "No processing errors expected");
        // 'name: String' parameter at line 13 should NOT match
        assertTrue(report.getViolations().stream().noneMatch(v -> v.getBeginLine() == 13),
                "String parameter should not match Calendar typeIs");
    }

    @Test
    void typeIsOnCatchBlockMatches() {
        File kotlinFile = getResource(TYPE_IS_RESOURCE_DIR + "/ParameterTypes.kt");
        Report report = runXPath("//CatchBlock[pmd-kotlin:typeIs('java.io.IOException')]", kotlinFile);
        assertTrue(report.getProcessingErrors().isEmpty(), "No processing errors expected");
        // catch (e: IOException) at line 21
        assertTrue(report.getViolations().stream().anyMatch(v -> v.getBeginLine() == 21),
                "Expected violation at line 21 (catch IOException)");
    }

    @Test
    void typeIsOnCatchBlockNoMatch() {
        File kotlinFile = getResource(TYPE_IS_RESOURCE_DIR + "/ParameterTypes.kt");
        Report report = runXPath("//CatchBlock[pmd-kotlin:typeIs('java.io.IOException')]", kotlinFile);
        assertTrue(report.getProcessingErrors().isEmpty(), "No processing errors expected");
        // catch (e: IllegalArgumentException) at line 30 should NOT match
        assertTrue(report.getViolations().stream().noneMatch(v -> v.getBeginLine() == 30),
                "IllegalArgumentException catch block should not match IOException typeIs");
    }

    @Test
    void typeIsOnForStatementMatches() {
        File kotlinFile = getResource(TYPE_IS_RESOURCE_DIR + "/ParameterTypes.kt");
        Report report = runXPath("//ForStatement[pmd-kotlin:typeIs('java.util.Calendar')]", kotlinFile);
        assertTrue(report.getProcessingErrors().isEmpty(), "No processing errors expected");
        // for (item in items) where items: List<Calendar>, at line 37
        assertTrue(report.getViolations().stream().anyMatch(v -> v.getBeginLine() == 37),
                "Expected violation at line 37 (for Calendar loop variable)");
    }

    @Test
    void typeIsOnForStatementNoMatch() {
        File kotlinFile = getResource(TYPE_IS_RESOURCE_DIR + "/ParameterTypes.kt");
        Report report = runXPath("//ForStatement[pmd-kotlin:typeIs('java.util.Calendar')]", kotlinFile);
        assertTrue(report.getProcessingErrors().isEmpty(), "No processing errors expected");
        // for (s in items) where items: List<String>, at line 44 should NOT match
        assertTrue(report.getViolations().stream().noneMatch(v -> v.getBeginLine() == 44),
                "String for-loop should not match Calendar typeIs");
    }

    @Test
    void typeIsMatchesInferredTypeSubtypeViaSourceHierarchy() {
        // "val myValue = Simple("Hello")" -- type is INFERRED (no explicit annotation).
        // Simple is defined in the same file and implements Serializable.
        // The source hierarchy (from K1 analysis) should make typeIs work without compiled classes.
        File kotlinFile = getResource(TYPE_IS_RESOURCE_DIR + "/InferredTypeSubtype.kt");
        Report report = runXPath("//PropertyDeclaration[pmd-kotlin:typeIs('java.io.Serializable')]", kotlinFile);
        assertTrue(report.getProcessingErrors().isEmpty(), "No processing errors expected");
        // val myValue = Simple("Hello") is at line 7
        assertTrue(report.getViolations().stream().anyMatch(v -> v.getBeginLine() == 7),
                "Expected match at line 7 (val myValue: inferred Simple which implements Serializable)");
    }

    @Test
    void typeIsDoesNotMatchUnrelatedInferredType() {
        File kotlinFile = getResource(TYPE_IS_RESOURCE_DIR + "/InferredTypeSubtype.kt");
        Report report = runXPath("//PropertyDeclaration[pmd-kotlin:typeIs('java.util.Calendar')]", kotlinFile);
        assertTrue(report.getProcessingErrors().isEmpty(), "No processing errors expected");
        assertEquals(0, report.getViolations().size(),
                "Simple is not a Calendar, should not match");
    }

    @Test
    void classDeclarationHasTypeNameAttribute() {
        // ClassDeclaration nodes should have @TypeName set to the class's own FQN
        File kotlinFile = getResource(TYPE_IS_RESOURCE_DIR + "/InferredTypeSubtype.kt");
        Report report = runXPath(
                "//ClassDeclaration[@TypeName='nl.stokpop.kotlin.Simple']", kotlinFile);
        assertTrue(report.getProcessingErrors().isEmpty(), "No processing errors expected");
        assertTrue(!report.getViolations().isEmpty(),
                "Expected ClassDeclaration[@TypeName='nl.stokpop.kotlin.Simple'] to match");
    }

    // --- AvoidStringBufferField ---

    @Test
    void stringBufferFieldMatchesStringBufferProperty() {
        File kotlinFile = getResource(TYPE_IS_RESOURCE_DIR + "/StringBufferFieldUsage.kt");
        Report report = runXPath(
                "//PropertyDeclaration[not(ancestor::FunctionBody) and pmd-kotlin:typeIs('java.lang.StringBuffer')]",
                kotlinFile);
        assertTrue(report.getProcessingErrors().isEmpty(), "No processing errors expected");
        assertTrue(report.getViolations().stream().anyMatch(v -> v.getBeginLine() == 2),
                "Expected violation at line 2 (StringBuffer field)");
    }

    @Test
    void stringBuilderFieldMatchesStringBuilderProperty() {
        File kotlinFile = getResource(TYPE_IS_RESOURCE_DIR + "/StringBufferFieldUsage.kt");
        Report report = runXPath(
                "//PropertyDeclaration[not(ancestor::FunctionBody) and pmd-kotlin:typeIs('java.lang.StringBuilder')]",
                kotlinFile);
        assertTrue(report.getProcessingErrors().isEmpty(), "No processing errors expected");
        assertTrue(report.getViolations().stream().anyMatch(v -> v.getBeginLine() == 3),
                "Expected violation at line 3 (StringBuilder field)");
    }

    @Test
    void stringBuilderLocalVariableDoesNotMatch() {
        File kotlinFile = getResource(TYPE_IS_RESOURCE_DIR + "/StringBufferFieldUsage.kt");
        Report report = runXPath(
                "//PropertyDeclaration[not(ancestor::FunctionBody) and pmd-kotlin:typeIs('java.lang.StringBuilder')]",
                kotlinFile);
        assertTrue(report.getProcessingErrors().isEmpty(), "No processing errors expected");
        assertTrue(report.getViolations().stream().noneMatch(v -> v.getBeginLine() == 6),
                "Local variable at line 6 should NOT match (inside FunctionBody)");
    }

    // --- AvoidMessageDigestField ---

    @Test
    void messageDigestFieldMatchesFieldProperty() {
        File kotlinFile = getResource(TYPE_IS_RESOURCE_DIR + "/MessageDigestFieldUsage.kt");
        Report report = runXPath(
                "//PropertyDeclaration[not(ancestor::FunctionBody) and pmd-kotlin:typeIs('java.security.MessageDigest')]",
                kotlinFile);
        assertTrue(report.getProcessingErrors().isEmpty(), "No processing errors expected");
        assertTrue(report.getViolations().stream().anyMatch(v -> v.getBeginLine() == 4),
                "Expected violation at line 4 (MessageDigest field)");
    }

    @Test
    void messageDigestLocalVariableDoesNotMatch() {
        File kotlinFile = getResource(TYPE_IS_RESOURCE_DIR + "/MessageDigestFieldUsage.kt");
        Report report = runXPath(
                "//PropertyDeclaration[not(ancestor::FunctionBody) and pmd-kotlin:typeIs('java.security.MessageDigest')]",
                kotlinFile);
        assertTrue(report.getProcessingErrors().isEmpty(), "No processing errors expected");
        assertTrue(report.getViolations().stream().noneMatch(v -> v.getBeginLine() == 7),
                "Local variable at line 7 should NOT match (inside FunctionBody)");
    }

    @Test
    void delegationSpecifierHasTypeNameAttribute() {
        // DelegationSpecifier nodes (supertypes) should have @TypeName set to the supertype FQN
        File kotlinFile = getResource(TYPE_IS_RESOURCE_DIR + "/InferredTypeSubtype.kt");
        Report report = runXPath(
                "//DelegationSpecifier[@TypeName='java.io.Serializable']", kotlinFile);
        assertTrue(report.getProcessingErrors().isEmpty(), "No processing errors expected");
        assertTrue(!report.getViolations().isEmpty(),
                "Expected DelegationSpecifier[@TypeName='java.io.Serializable'] to match");
    }

    @Test
    void typeIsMatchesDelegationSpecifierViaSupertypeHierarchy() {
        // typeIs('java.lang.Throwable') on a DelegationSpecifier must match when the supertype
        // is RuntimeException (a transitive subtype of Throwable).
        File kotlinFile = getResource(TYPE_IS_RESOURCE_DIR + "/DelegationSpecifierSubtype.kt");
        Report report = runXPath(
                "//DelegationSpecifier[pmd-kotlin:typeIs('java.lang.Throwable')]", kotlinFile);
        assertTrue(report.getProcessingErrors().isEmpty(), "No processing errors expected");
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
        assertTrue(report.getProcessingErrors().isEmpty(), "No processing errors expected");
        assertEquals(2, report.getViolations().size(),
                "Both DelegationSpecifier nodes should match typeIs('java.lang.Throwable')");
    }

    @Test
    void typeIsExactlyMatchesConstructorCallOnPostfixUnaryExpression() {
        // typeIsExactly on a throw's PostfixUnaryExpression (constructor call) must fire
        File kotlinFile = getResource(TYPE_IS_RESOURCE_DIR + "/ConstructorCallTypeCheck.kt");
        Report report = runXPath(
                "//JumpExpression[T-THROW and .//PostfixUnaryExpression["
                        + "pmd-kotlin:typeIsExactly('java.lang.Exception')]]",
                kotlinFile);
        assertTrue(report.getProcessingErrors().isEmpty(), "No processing errors expected");
        assertTrue(report.getViolations().stream().anyMatch(v -> v.getBeginLine() == 12),
                "Expected violation at line 12 (throw Exception)");
        assertTrue(report.getViolations().stream().noneMatch(v -> v.getBeginLine() == 16),
                "Line 16 (throw RuntimeException) must NOT match typeIsExactly('java.lang.Exception')");
        assertTrue(report.getViolations().stream().noneMatch(v -> v.getBeginLine() == 21),
                "Line 21 (throw IllegalArgumentException) must NOT match typeIsExactly('java.lang.Exception')");
    }

    @Test
    void typeIsExactlyDoesNotMatchInterfaceTypedPropertyWithConcreteInitializer() {
        // PropertyDeclaration val items: List<String> = ArrayList()
        // typeIsExactly('java.util.ArrayList') must NOT fire -- declared type is List, not ArrayList
        File kotlinFile = getResource(TYPE_IS_RESOURCE_DIR + "/ConstructorCallTypeCheck.kt");
        Report report = runXPath(
                "//PropertyDeclaration[pmd-kotlin:typeIsExactly('java.util.ArrayList')]",
                kotlinFile);
        assertTrue(report.getProcessingErrors().isEmpty(), "No processing errors expected");
        assertTrue(report.getViolations().isEmpty(),
                "No PropertyDeclaration should match typeIsExactly('java.util.ArrayList') "
                        + "when declared type is List interface");
    }

    // --- TypeName on CatchBlock, FunctionValueParameter, UnescapedAnnotation ---

    @Test
    void catchBlockHasTypeNameAttribute() {
        // CatchBlock nodes should have @TypeName set to the caught exception's FQN
        File kotlinFile = getResource(TYPE_IS_RESOURCE_DIR + "/TypeAnnotationAttributes.kt");
        Report report = runXPath(
                "//CatchBlock[@TypeName='java.lang.IllegalArgumentException']", kotlinFile);
        assertTrue(report.getProcessingErrors().isEmpty(), "No processing errors expected");
        assertTrue(!report.getViolations().isEmpty(),
                "Expected CatchBlock[@TypeName='java.lang.IllegalArgumentException'] to match");
    }

    @Test
    void functionParameterHasTypeNameAttribute() {
        // FunctionValueParameter nodes should have @TypeName set to the parameter's FQN
        File kotlinFile = getResource(TYPE_IS_RESOURCE_DIR + "/TypeAnnotationAttributes.kt");
        Report report = runXPath(
                "//FunctionValueParameter[@TypeName='java.util.Calendar']", kotlinFile);
        assertTrue(report.getProcessingErrors().isEmpty(), "No processing errors expected");
        assertTrue(!report.getViolations().isEmpty(),
                "Expected FunctionValueParameter[@TypeName='java.util.Calendar'] to match");
    }

    @Test
    void annotationNodeHasTypeNameAttribute() {
        // UnescapedAnnotation and SingleAnnotation nodes should have @TypeName set to the annotation FQN
        File kotlinFile = getResource(TYPE_IS_RESOURCE_DIR + "/TypeAnnotationAttributes.kt");
        Report report = runXPath(
                "//UnescapedAnnotation[@TypeName='kotlin.Deprecated']", kotlinFile);
        assertTrue(report.getProcessingErrors().isEmpty(), "No processing errors expected");
        assertTrue(!report.getViolations().isEmpty(),
                "Expected UnescapedAnnotation[@TypeName='kotlin.Deprecated'] to match");
    }

    // --- AnnotationFqNames attribute on FunctionDeclaration and ClassDeclaration ---

    @Test
    void functionDeclarationAnnotationFqNamesAttributeMatchesDeprecated() {
        File kotlinFile = getResource(TYPE_IS_RESOURCE_DIR + "/TypeAnnotationAttributes.kt");
        Report report = runXPath(
                "//FunctionDeclaration[@AnnotationFqNames = 'kotlin.Deprecated']", kotlinFile);
        assertTrue(report.getProcessingErrors().isEmpty(), "No processing errors expected");
        assertTrue(!report.getViolations().isEmpty(),
                "Expected FunctionDeclaration[@AnnotationFqNames='kotlin.Deprecated'] to match");
    }

    @Test
    void classDeclarationAnnotationFqNamesAttributeMatchesDeprecated() {
        File kotlinFile = getResource(TYPE_IS_RESOURCE_DIR + "/TypeAnnotationAttributes.kt");
        Report report = runXPath(
                "//ClassDeclaration[@AnnotationFqNames = 'kotlin.Deprecated']", kotlinFile);
        assertTrue(report.getProcessingErrors().isEmpty(), "No processing errors expected");
        assertTrue(!report.getViolations().isEmpty(),
                "Expected ClassDeclaration[@AnnotationFqNames='kotlin.Deprecated'] to match");
    }

    private Report runXPath(String xpathExpr, File kotlinFile) {
        PMDConfiguration config = new PMDConfiguration();
        config.setIgnoreIncrementalAnalysis(true);
        config.setDefaultLanguageVersion(
                LanguageRegistry.PMD.getLanguageById("kotlin").getDefaultVersion());

        try (PmdAnalysis pmd = PmdAnalysis.create(config)) {
            pmd.addRuleSet(RuleSet.forSingleRule(buildXPathRule(xpathExpr)));
            pmd.files().addFile(kotlinFile.toPath());
            return pmd.performAnalysisAndCollectReport();
        }
    }

    private Rule buildXPathRule(String xpathExpr) {
        XPathRule rule = new XPathRule(XPathVersion.DEFAULT, xpathExpr);
        rule.setLanguage(LanguageRegistry.PMD.getLanguageById("kotlin"));
        rule.setMessage("test");
        rule.setName("TestRule");
        return rule;
    }

    private File getResource(String path) {
        URL resource = getClass().getClassLoader().getResource(path);
        if (resource == null) {
            throw new IllegalStateException("Cannot find resource: " + path);
        }
        return new File(resource.getFile());
    }
}
