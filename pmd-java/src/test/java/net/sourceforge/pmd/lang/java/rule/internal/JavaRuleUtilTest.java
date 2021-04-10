/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.internal;

import static net.sourceforge.pmd.lang.java.rule.internal.JavaRuleUtil.containsCamelCaseWord;
import static net.sourceforge.pmd.lang.java.rule.internal.JavaRuleUtil.startsWithCamelCaseWord;
import static net.sourceforge.pmd.util.CollectionUtil.listOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.Test;

import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTInfixExpression;
import net.sourceforge.pmd.lang.java.ast.ASTVariableAccess;
import net.sourceforge.pmd.lang.java.symboltable.BaseNonParserTest;

public class JavaRuleUtilTest extends BaseNonParserTest {

    @Test
    public void testCamelCaseWords() {
        assertFalse(startsWithCamelCaseWord("getter", "get"), "no word boundary");
        assertFalse(startsWithCamelCaseWord("get", "get"), "no following word");
        assertTrue(startsWithCamelCaseWord("getX", "get"), "ok prefix");
        assertFalse(startsWithCamelCaseWord("ge", "get"), "shorter word");

        assertThrows(NullPointerException.class, () -> startsWithCamelCaseWord(null, "get"));
        assertThrows(NullPointerException.class, () -> startsWithCamelCaseWord("fnei", null));
    }

    @Test
    public void testContainsCamelCaseWords() {

        assertFalse(containsCamelCaseWord("isABoolean", "Bool"), "no word boundary");
        assertTrue(containsCamelCaseWord("isABoolean", "A"), "ok word");
        assertTrue(containsCamelCaseWord("isABoolean", "Boolean"), "ok word");

        assertThrows(NullPointerException.class, () -> containsCamelCaseWord(null, "get"));
        assertThrows(NullPointerException.class, () -> containsCamelCaseWord("fnei", null));
        assertThrows(AssertionError.class, () -> containsCamelCaseWord("fnei", ""), "empty string");
        assertThrows(AssertionError.class, () -> containsCamelCaseWord("fnei", "a"), "not capitalized");
    }

    @Test
    public void testFlattenConcatOperands() {
        ASTExpression e = parseExpr("s1+s2+s3");

        assertTrue(JavaRuleUtil.isStringConcatExpr(e));
        assertEquals(e.descendants(ASTVariableAccess.class).toList(),
                     JavaRuleUtil.flattenOperands(e).toList());
    }

    @Test
    public void testFlattenConcatOperandsRespectsTyping() {
        ASTInfixExpression e = (ASTInfixExpression) parseExpr("i+j+s2+s3");
        assertTrue(JavaRuleUtil.isStringConcatExpr(e));
        ASTInfixExpression left = (ASTInfixExpression) e.getLeftOperand();
        assertTrue(JavaRuleUtil.isStringConcatExpr(left));

        //                      This is (i+j)
        //                  vvvvvvvvvvvvvvvvvvvvv
        assertEquals(listOf(left.getLeftOperand(), left.getRightOperand(), e.getRightOperand()),
                     JavaRuleUtil.flattenOperands(e).toList());
    }

}
