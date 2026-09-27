/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.rule.xpath.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.kotlin.ast.KotlinNode;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtClassDeclaration;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtClassParameter;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtFunctionDeclaration;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtKotlinFile;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtPropertyDeclaration;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtUnescapedAnnotation;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParsingHelper;

/**
 * Verifies the fixed AST shape that {@link KotlinHasAnnotationFunction#directAnnotationsOf}
 * relies on: {@code declNode -> Modifiers -> Annotation -> SingleAnnotation|MultiAnnotation
 * -> UnescapedAnnotation}. This shape is the same for every kind of declaration that
 * carries modifiers, which is why a fixed-path lookup can replace an unbounded recursive
 * search with a body-boundary guard (see PR #6893, discussion_r3979811856).
 */
class KotlinHasAnnotationFunctionShapeTest {

    @Test
    void annotationIsNotADirectChildOfTheDeclaration() {
        KtKotlinFile file = KotlinParsingHelper.DEFAULT.parse("@Deprecated\nfun foo() {}\n");
        KtFunctionDeclaration decl = file.descendants(KtFunctionDeclaration.class).firstOrThrow();

        List<KotlinNode> directChildren = decl.children().toList();
        assertFalse(
                directChildren.stream().anyMatch(KtUnescapedAnnotation.class::isInstance),
                "UnescapedAnnotation should NOT be a direct child of FunctionDeclaration -- "
                        + "it is reached via Modifiers -> Annotation -> SingleAnnotation");
    }

    @Test
    void directAnnotationsOfFindsSingleAnnotationOnFunDeclaration() {
        KtKotlinFile file = KotlinParsingHelper.DEFAULT.parse("@Deprecated\nfun foo() {}\n");
        KtFunctionDeclaration decl = file.descendants(KtFunctionDeclaration.class).firstOrThrow();
        assertEquals(1, KotlinHasAnnotationFunction.directAnnotationsOf(decl).size());
    }

    @Test
    void directAnnotationsOfFindsBothAnnotationsInMultiAnnotationOnFunDeclaration() {
        KtKotlinFile file = KotlinParsingHelper.DEFAULT.parse("@[Deprecated Suppress]\nfun foo() {}\n");
        KtFunctionDeclaration decl = file.descendants(KtFunctionDeclaration.class).firstOrThrow();
        assertEquals(2, KotlinHasAnnotationFunction.directAnnotationsOf(decl).size());
    }

    @Test
    void directAnnotationsOfFindsAnnotationOnClass() {
        KtKotlinFile file = KotlinParsingHelper.DEFAULT.parse("@Deprecated\nclass Foo\n");
        KtClassDeclaration decl = file.descendants(KtClassDeclaration.class).firstOrThrow();
        assertEquals(1, KotlinHasAnnotationFunction.directAnnotationsOf(decl).size());
    }

    @Test
    void directAnnotationsOfFindsUseSiteAnnotationOnConstructorParameter() {
        KtKotlinFile file = KotlinParsingHelper.DEFAULT.parse("class Foo(@field:Deprecated val x: Int)\n");
        KtClassParameter decl = file.descendants(KtClassParameter.class).firstOrThrow();
        assertEquals(1, KotlinHasAnnotationFunction.directAnnotationsOf(decl).size());
    }

    @Test
    void directAnnotationsOfFindsAnnotationOnPropertyInClassBody() {
        KtKotlinFile file = KotlinParsingHelper.DEFAULT.parse("class Foo {\n@Deprecated\nval x: Int = 1\n}\n");
        KtPropertyDeclaration decl = file.descendants(KtPropertyDeclaration.class).firstOrThrow();
        assertEquals(1, KotlinHasAnnotationFunction.directAnnotationsOf(decl).size());
    }

    @Test
    void directAnnotationsOfDoesNotReachIntoNestedClassMember() {
        // Outer has no annotation of its own; Inner's method() does. Since the fixed path
        // never touches ClassBody, Outer must not pick up Inner.method()'s annotation.
        KtKotlinFile file = KotlinParsingHelper.DEFAULT.parse(
                "class Outer {\n"
                        + "    class Inner {\n"
                        + "        @Deprecated\n"
                        + "        fun method() {}\n"
                        + "    }\n"
                        + "}\n");
        KtClassDeclaration outer = file.descendants(KtClassDeclaration.class).firstOrThrow();
        KtFunctionDeclaration method = file.descendants(KtFunctionDeclaration.class).firstOrThrow();

        assertTrue(KotlinHasAnnotationFunction.directAnnotationsOf(outer).isEmpty(),
                "Outer must not see Inner.method()'s annotation");
        assertEquals(1, KotlinHasAnnotationFunction.directAnnotationsOf(method).size());
    }

    @Test
    void directAnnotationsOfReturnsEmptyWhenNoModifiers() {
        KtKotlinFile file = KotlinParsingHelper.DEFAULT.parse("fun foo() {}\n");
        KtFunctionDeclaration decl = file.descendants(KtFunctionDeclaration.class).firstOrThrow();
        assertTrue(KotlinHasAnnotationFunction.directAnnotationsOf(decl).isEmpty());
    }

    /**
     * The grammar attaches an annotation on a <em>local</em> declaration (one nested
     * inside a function body) to the enclosing {@code statement} rule as a preceding
     * sibling, not as a descendant of the declaration itself ({@code statement :
     * (label | annotation)* (declaration | ...)}). {@code directAnnotationsOf} also
     * checks that sibling via {@code annotationsOnEnclosingStatement}, so it still
     * finds it.
     */
    @Test
    void directAnnotationsOfFindsAnnotationOnLocalDeclaration() {
        KtKotlinFile file = KotlinParsingHelper.DEFAULT.parse(
                "fun outer() {\n"
                        + "    @Deprecated\n"
                        + "    fun nested() {}\n"
                        + "}\n");
        List<KtFunctionDeclaration> functions = file.descendants(KtFunctionDeclaration.class).toList();
        KtFunctionDeclaration outer = functions.get(0);
        KtFunctionDeclaration nested = functions.get(1);
        assertTrue(KotlinHasAnnotationFunction.directAnnotationsOf(outer).isEmpty(),
                "outer() has no annotation of its own");
        assertEquals(1, KotlinHasAnnotationFunction.directAnnotationsOf(nested).size(),
                "nested()'s annotation sits on the enclosing 'statement' node as a sibling, "
                        + "but directAnnotationsOf still finds it");
    }
}
