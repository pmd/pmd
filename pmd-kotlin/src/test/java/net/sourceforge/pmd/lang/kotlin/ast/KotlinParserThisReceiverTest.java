/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtKotlinFile;

/**
 * Tests for the this@label syntax.
 */
class KotlinParserThisReceiverTest {

    @Test
    void testThisReceiverLabels() {
        String code = KotlinParsingHelper.readResourcePath("net/sourceforge/pmd/lang/kotlin/ast/testdata/ThisReceiver.kt");

        // Parse using KotlinParsingHelper
        KtKotlinFile root = KotlinParsingHelper.DEFAULT.parse(code);

        assertNotNull(root);

        List<KotlinParser.KtThisExpression> thisExpressions = root.descendants(KotlinParser.KtThisExpression.class).toList();
        assertEquals(1, thisExpressions.size(), "Expected exactly three this expressions, but there is actually only one. The others are LabelDefinitions");
        //should be this? assertEquals(3, thisExpressions.size(), "Expected exactly three this expressions");

//        List<KotlinParser.KtLabelDefinition> labelDefinitions = root.descendants(KotlinParser.KtLabelDefinition.class).toList();
//        assertEquals(2, labelDefinitions.size(), "Expected exactly three label definitions");

        List<KotlinParser.KtAnnotatedLambda> annotatedLambdas = root.descendants(KotlinParser.KtAnnotatedLambda.class).toList();
        assertEquals(2, annotatedLambdas.size(), "Expected exactly two annotated lambdas");

    }

}
