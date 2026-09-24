/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.rule.xpath.internal;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.reporting.Report;

class KotlinTypeAttributesTest extends BaseKotlinXPathFunctionTest {

    private static final String TYPE_IS_RESOURCE_DIR =
            "net/sourceforge/pmd/lang/kotlin/rule/xpath/typeis";

    @Test
    void classDeclarationHasTypeNameAttribute() {
        // ClassDeclaration nodes should have @TypeName set to the class's own FQN
        File kotlinFile = getResource(TYPE_IS_RESOURCE_DIR + "/InferredTypeSubtype.kt");
        Report report = runXPath(
                "//ClassDeclaration[@TypeName='net.sourceforge.pmd.lang.kotlin.rule.xpath.typeis.Simple']", kotlinFile);
        assertNoErrors(report);
        assertTrue(!report.getViolations().isEmpty(),
                "Expected ClassDeclaration[@TypeName='net.sourceforge.pmd.lang.kotlin.rule.xpath.typeis.Simple'] to match");
    }

    @Test
    void delegationSpecifierHasTypeNameAttribute() {
        // DelegationSpecifier nodes (supertypes) should have @TypeName set to the supertype FQN
        File kotlinFile = getResource(TYPE_IS_RESOURCE_DIR + "/InferredTypeSubtype.kt");
        Report report = runXPath(
                "//DelegationSpecifier[@TypeName='java.io.Serializable']", kotlinFile);
        assertNoErrors(report);
        assertTrue(!report.getViolations().isEmpty(),
                "Expected DelegationSpecifier[@TypeName='java.io.Serializable'] to match");
    }

    // --- TypeName on CatchBlock, FunctionValueParameter, UnescapedAnnotation ---

    @Test
    void catchBlockHasTypeNameAttribute() {
        // CatchBlock nodes should have @TypeName set to the caught exception's FQN
        File kotlinFile = getResource(TYPE_IS_RESOURCE_DIR + "/TypeAnnotationAttributes.kt");
        Report report = runXPath(
                "//CatchBlock[@TypeName='java.lang.IllegalArgumentException']", kotlinFile);
        assertNoErrors(report);
        assertTrue(!report.getViolations().isEmpty(),
                "Expected CatchBlock[@TypeName='java.lang.IllegalArgumentException'] to match");
    }

    @Test
    void functionParameterHasTypeNameAttribute() {
        // FunctionValueParameter nodes should have @TypeName set to the parameter's FQN
        File kotlinFile = getResource(TYPE_IS_RESOURCE_DIR + "/TypeAnnotationAttributes.kt");
        Report report = runXPath(
                "//FunctionValueParameter[@TypeName='java.util.Calendar']", kotlinFile);
        assertNoErrors(report);
        assertTrue(!report.getViolations().isEmpty(),
                "Expected FunctionValueParameter[@TypeName='java.util.Calendar'] to match");
    }

    @Test
    void annotationNodeHasTypeNameAttribute() {
        // UnescapedAnnotation and SingleAnnotation nodes should have @TypeName set to the annotation FQN
        File kotlinFile = getResource(TYPE_IS_RESOURCE_DIR + "/TypeAnnotationAttributes.kt");
        Report report = runXPath(
                "//UnescapedAnnotation[@TypeName='kotlin.Deprecated']", kotlinFile);
        assertNoErrors(report);
        assertTrue(!report.getViolations().isEmpty(),
                "Expected UnescapedAnnotation[@TypeName='kotlin.Deprecated'] to match");
    }

    // --- AnnotationFqNames attribute on FunctionDeclaration and ClassDeclaration ---

    @Test
    void functionDeclarationAnnotationFqNamesAttributeMatchesDeprecated() {
        File kotlinFile = getResource(TYPE_IS_RESOURCE_DIR + "/TypeAnnotationAttributes.kt");
        Report report = runXPath(
                "//FunctionDeclaration[@AnnotationFqNames = 'kotlin.Deprecated']", kotlinFile);
        assertNoErrors(report);
        assertTrue(!report.getViolations().isEmpty(),
                "Expected FunctionDeclaration[@AnnotationFqNames='kotlin.Deprecated'] to match");
    }

    @Test
    void classDeclarationAnnotationFqNamesAttributeMatchesDeprecated() {
        File kotlinFile = getResource(TYPE_IS_RESOURCE_DIR + "/TypeAnnotationAttributes.kt");
        Report report = runXPath(
                "//ClassDeclaration[@AnnotationFqNames = 'kotlin.Deprecated']", kotlinFile);
        assertNoErrors(report);
        assertTrue(!report.getViolations().isEmpty(),
                "Expected ClassDeclaration[@AnnotationFqNames='kotlin.Deprecated'] to match");
    }

}
