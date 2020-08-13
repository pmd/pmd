/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.lang.java.JavaParsingHelper;

public class Java15PreviewTest {
    private final JavaParsingHelper java15p =
            JavaParsingHelper.WITH_PROCESSING.withDefaultVersion("15-preview")
                                             .withResourceContext(Java15PreviewTest.class, "jdkversiontests/java15p/");

    private final JavaParsingHelper java15 = java15p.withDefaultVersion("15");

    @Test
    public void patternMatchingInstanceof() {
        ASTCompilationUnit compilationUnit = java15p.parseResource("PatternMatchingInstanceof.java");
        List<ASTInstanceOfExpression> instanceOfExpressions = compilationUnit.findDescendantsOfType(ASTInstanceOfExpression.class);
        Assert.assertEquals(4, instanceOfExpressions.size());
        for (ASTInstanceOfExpression expr : instanceOfExpressions) {
            Assert.assertTrue(expr.getChild(1) instanceof ASTTypeTestPattern);
            ASTVariableDeclaratorId variable = expr.getChild(1).getFirstChildOfType(ASTVariableDeclaratorId.class);
            Assert.assertEquals(String.class, variable.getType());
            Assert.assertEquals("s", variable.getVariableName());
            Assert.assertTrue(variable.isPatternBinding());
            Assert.assertTrue(variable.isFinal());
            // Note: these variables are not part of the symbol table
            // See ScopeAndDeclarationFinder#visit(ASTVariableDeclaratorId, Object)
            Assert.assertNull(variable.getNameDeclaration());
        }
    }

    @Test(expected = ParseException.class)
    public void patternMatchingInstanceofBeforeJava15PreviewShouldFail() {
        java15.parseResource("PatternMatchingInstanceof.java");
    }

}
