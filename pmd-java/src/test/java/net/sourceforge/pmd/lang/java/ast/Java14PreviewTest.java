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
        Assert.assertFalse(recordDecl.isNested());
        List<ASTRecordComponent> components = recordDecl.getFirstChildOfType(ASTRecordComponentList.class)
                .findChildrenOfType(ASTRecordComponent.class);
        Assert.assertEquals(2, components.size());
        Assert.assertEquals("x", components.get(0).getVariableDeclaratorId().getImage());
        Assert.assertEquals("y", components.get(1).getVariableDeclaratorId().getImage());
    }

    @Test(expected = ParseException.class)
    public void recordPointBeforeJava14PreviewShouldFail() {
        java14.parseResource("Point.java");
    }

    @Test
    public void innerRecords() {
        ASTCompilationUnit compilationUnit = java14p.parseResource("Records.java");
        List<ASTRecordDeclaration> recordDecls = compilationUnit.findDescendantsOfType(ASTRecordDeclaration.class, true);
        Assert.assertEquals(6, recordDecls.size());

        ASTRecordDeclaration complex = recordDecls.get(0);
        Assert.assertEquals("MyComplex", complex.getName());
        Assert.assertTrue(complex.isNested());
        Assert.assertEquals(0, complex.getRecordComponents().get(0).findChildrenOfType(ASTAnnotation.class).size());
        Assert.assertEquals(1, complex.getRecordComponents().get(1).findChildrenOfType(ASTAnnotation.class).size());
        Assert.assertEquals(2, complex.getDeclarations().size());
        Assert.assertTrue(complex.getDeclarations().get(0).getChild(1) instanceof ASTConstructorDeclaration);
        Assert.assertTrue(complex.getDeclarations().get(1).getChild(0) instanceof ASTRecordDeclaration);

        ASTRecordDeclaration nested = recordDecls.get(1);
        Assert.assertEquals("Nested", nested.getName());
        Assert.assertTrue(nested.isNested());

        ASTRecordDeclaration range = recordDecls.get(2);
        Assert.assertEquals("Range", range.getName());
        Assert.assertEquals(2, range.getRecordComponents().size());
        List<ASTRecordConstructorDeclaration> rangeConstructors = range.findDescendantsOfType(ASTRecordConstructorDeclaration.class);
        Assert.assertEquals(1, rangeConstructors.size());
        Assert.assertEquals("Range", rangeConstructors.get(0).getImage());
        Assert.assertEquals(2, range.getDeclarations().size());

        ASTRecordDeclaration varRec = recordDecls.get(3);
        Assert.assertEquals("VarRec", varRec.getName());
        Assert.assertEquals("x", varRec.getRecordComponents().get(0).getVariableDeclaratorId().getImage());
        Assert.assertTrue(varRec.getRecordComponents().get(0).isVarargs());
        Assert.assertEquals(2, varRec.getRecordComponents().get(0).findChildrenOfType(ASTAnnotation.class).size());
        Assert.assertEquals(1, varRec.getRecordComponents().get(0).getTypeNode().findDescendantsOfType(ASTAnnotation.class).size());

        ASTRecordDeclaration arrayRec = recordDecls.get(4);
        Assert.assertEquals("ArrayRec", arrayRec.getName());
        Assert.assertEquals("x", arrayRec.getRecordComponents().get(0).getVariableDeclaratorId().getImage());
        Assert.assertTrue(arrayRec.getRecordComponents().get(0).getVariableDeclaratorId().hasArrayType());

        ASTRecordDeclaration emptyRec = recordDecls.get(5);
        Assert.assertEquals("EmptyRec", emptyRec.getName());
        Assert.assertEquals(0, emptyRec.getRecordComponents().size());
    }

    @Test(expected = ParseException.class)
    public void recordIsARestrictedIdentifier() {
        java14p.parse("public class record {}");
    }
}
