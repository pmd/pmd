/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast.internal;

import static net.sourceforge.pmd.lang.java.ast.internal.JavaAstUtils.flattenOperands;
import static net.sourceforge.pmd.lang.java.ast.internal.JavaAstUtils.isStringConcatExpr;
import static net.sourceforge.pmd.util.CollectionUtil.listOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.java.BaseParserTest;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorCall;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTInfixExpression;
import net.sourceforge.pmd.lang.java.ast.ASTLambdaExpression;
import net.sourceforge.pmd.lang.java.ast.ASTVariableAccess;

class JavaAstUtilTest extends BaseParserTest {

    @Test
    void testFlattenConcatOperands() {
        ASTExpression e = parseExpr("s1+s2+s3");

        assertTrue(isStringConcatExpr(e));
        assertEquals(e.descendants(ASTVariableAccess.class).toList(),
                     flattenOperands(e).toList());
    }

    @Test
    void testFlattenConcatOperandsRespectsTyping() {
        ASTInfixExpression e = (ASTInfixExpression) parseExpr("i+j+s2+s3");
        assertTrue(isStringConcatExpr(e));
        ASTInfixExpression left = (ASTInfixExpression) e.getLeftOperand();
        assertTrue(isStringConcatExpr(left));

        //                      This is (i+j)
        //                  vvvvvvvvvvvvvvvvvvvvv
        assertEquals(listOf(left.getLeftOperand(), left.getRightOperand(), e.getRightOperand()),
                     flattenOperands(e).toList());
    }

    @Test
    void testGetTopLevelExprThroughArgumentList() {
        ASTExpression e = parseExpr("foo(bar)");
        ASTVariableAccess bar = e.descendants(ASTVariableAccess.class).first();

        ASTExpression topLevel = JavaAstUtils.getTopLevelExpr(bar);
        assertSame(topLevel, e);
    }

    @Test
    void testGetTopLevelExprThroughLambda() {
        ASTCompilationUnit acu = java.parse("class Foo { { new Thread(() -> new Object()); } }");
        ASTLambdaExpression lambda = acu.descendants(ASTLambdaExpression.class).first();
        ASTExpression inner = lambda.descendants(ASTConstructorCall.class).first();
        ASTExpression top = JavaAstUtils.getTopLevelExpr(inner);
        assertSame(inner, top); // should not go up to `new Thread(...)`
    }
}
