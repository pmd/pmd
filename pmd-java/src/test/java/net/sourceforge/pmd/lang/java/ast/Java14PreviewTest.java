/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.ast;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.lang.java.JavaParsingHelper;

/**
 * Tests new java14 preview features.
 */
public class Java14PreviewTest {
    private final JavaParsingHelper java14 =
            JavaParsingHelper.WITH_PROCESSING.withDefaultVersion("14")
                                             .withResourceContext(Java14Test.class, "jdkversiontests/java14/");

    private final JavaParsingHelper java14p = java14.withDefaultVersion("14-preview");
    private final JavaParsingHelper java13 = java14.withDefaultVersion("13");

    @Test
    public void textBlocks() {
        ASTCompilationUnit compilationUnit = java14p.parseResource("TextBlocks.java");
        List<ASTLiteral> literals = compilationUnit.findDescendantsOfType(ASTLiteral.class);
        Assert.assertEquals(22, literals.size());
        Assert.assertFalse(literals.get(2).isTextBlock());
        Assert.assertFalse(literals.get(12).isTextBlock());
        Assert.assertFalse(literals.get(17).isTextBlock());
        Assert.assertFalse(literals.get(18).isTextBlock());
        Assert.assertFalse(literals.get(20).isTextBlock());
        Assert.assertFalse(literals.get(21).isTextBlock());

        List<ASTLiteral> textBlocks = new ArrayList<>();
        for (ASTLiteral literal : literals) {
            if (literal.isTextBlock()) {
                textBlocks.add(literal);
            }
        }
        Assert.assertEquals(16, textBlocks.size());
        Assert.assertEquals("\"\"\"\n"
                                + "                      <html>   \n"
                                + "                          <body>\n"
                                + "                              <p>Hello, world</p>    \n"
                                + "                          </body> \n"
                                + "                      </html>   \n"
                                + "                      \"\"\"",
                            textBlocks.get(0).getImage());
        Assert.assertEquals("<html>\n"
                          + "    <body>\n"
                          + "        <p>Hello, world</p>\n"
                          + "    </body>\n"
                          + "</html>\n", textBlocks.get(0).getTextBlockContent());

        // Note: More tests are in ASTLiteralTest.
    }

    @Test(expected = ParseException.class)
    public void textBlocksBeforeJava14PreviewShouldFail() {
        java13.parseResource("TextBlocks.java");
    }

    @Test(expected = ParseException.class)
    public void stringEscapeSequenceShouldFail() {
        java14.parse("class Foo { String s =\"a\\sb\"; }");
    }

    @Test
    public void patternMatchingInstanceof() {
        ASTCompilationUnit compilationUnit = java14p.parseResource("PatternMatchingInstanceof.java");
        List<ASTInstanceOfExpression> instanceOfExpressions = compilationUnit.findDescendantsOfType(ASTInstanceOfExpression.class);
        Assert.assertEquals(4, instanceOfExpressions.size());
        for (ASTInstanceOfExpression expr : instanceOfExpressions) {
            Assert.assertTrue(expr.getChild(1) instanceof ASTTypeTestPattern);
            ASTVariableDeclaratorId variable = expr.getChild(1).getFirstChildOfType(ASTVariableDeclaratorId.class);
            Assert.assertEquals(String.class, variable.getType());
            Assert.assertEquals("s", variable.getVariableName());
            Assert.assertTrue(variable.isPatternBinding());
            Assert.assertTrue(variable.isFinal());
        }
    }

    @Test(expected = ParseException.class)
    public void patternMatchingInstanceofBeforeJava14PreviewShouldFail() {
        java14.parseResource("PatternMatchingInstanceof.java");
    }

    @Test
    public void recordPoint() {
        ASTCompilationUnit compilationUnit = java14p.parseResource("Point.java");
        ASTRecordDeclaration recordDecl = compilationUnit.getFirstDescendantOfType(ASTRecordDeclaration.class);
        Assert.assertEquals("Point", recordDecl.getImage());
        List<ASTRecordComponent> components = recordDecl.getFirstChildOfType(ASTRecordComponentList.class)
                .findChildrenOfType(ASTRecordComponent.class);
        Assert.assertEquals(2, components.size());
        Assert.assertEquals("x", components.get(0).getImage());
        Assert.assertEquals("y", components.get(1).getImage());
    }

    @Test(expected = ParseException.class)
    public void recordPointBeforeJava14PreviewShouldFail() {
        java14.parseResource("Point.java");
    }

    @Test
    public void innerRecords() {
        ASTCompilationUnit compilationUnit = java14p.parseResource("Records.java");
        List<ASTRecordDeclaration> recordDecls = compilationUnit.findDescendantsOfType(ASTRecordDeclaration.class);
        Assert.assertEquals(2, recordDecls.size());
    }

    @Test(expected = ParseException.class)
    public void recordIsARestrictedIdentifier() {
        java14p.parse("public class record {}");
    }
}
