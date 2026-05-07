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
        KotlinParsingHelper kotlinParsingHelper = KotlinParsingHelper.DEFAULT.withResourceContext(getClass());

        KtKotlinFile root = kotlinParsingHelper.parseResource("testdata/ThisReceiver.kt");

        assertNotNull(root);

        List<KotlinParser.KtThisExpression> thisExpressions = root.descendants(KotlinParser.KtThisExpression.class).toList();
        assertEquals(3, thisExpressions.size(), "Expected exactly three this expressions");

        List<KotlinParser.KtAnnotatedLambda> annotatedLambdas = root.descendants(KotlinParser.KtAnnotatedLambda.class).toList();
        assertEquals(2, annotatedLambdas.size(), "Expected exactly two annotated lambdas");

    }

}
