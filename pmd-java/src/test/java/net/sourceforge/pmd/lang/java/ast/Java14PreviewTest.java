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
                                         .withResourceContext(getClass(), "jdkversiontests/java14/");

    private final JavaParsingHelper java14p = java14.withDefaultVersion("14-preview");
    private final JavaParsingHelper java13 = java14.withDefaultVersion("13");

    @Test
    public void textBlocks() {
        ASTCompilationUnit compilationUnit = java14p.parseResource("TextBlocks.java");
        List<ASTStringLiteral> literals = compilationUnit.findDescendantsOfType(ASTStringLiteral.class);
        Assert.assertEquals(22, literals.size());
        Assert.assertFalse(literals.get(2).isTextBlock());
        Assert.assertFalse(literals.get(12).isTextBlock());
        Assert.assertFalse(literals.get(17).isTextBlock());
        Assert.assertFalse(literals.get(18).isTextBlock());
        Assert.assertFalse(literals.get(20).isTextBlock());
        Assert.assertFalse(literals.get(21).isTextBlock());

        List<ASTStringLiteral> textBlocks = new ArrayList<>();
        for (ASTStringLiteral literal : literals) {
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
                          + "</html>\n", textBlocks.get(0).getConstValue());

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
    public void recordPoint() {
        ASTCompilationUnit compilationUnit = java14p.parseResource("Point.java");
        ASTRecordDeclaration recordDecl = compilationUnit.getFirstDescendantOfType(ASTRecordDeclaration.class);
        Assert.assertEquals("Point", recordDecl.getImage());
        Assert.assertFalse(recordDecl.isNested());
        List<ASTRecordComponent> components = recordDecl.getFirstChildOfType(ASTRecordComponentList.class)
                                                        .findChildrenOfType(ASTRecordComponent.class);
        Assert.assertEquals(2, components.size());
        Assert.assertEquals("x", components.get(0).getVarId().getImage());
        Assert.assertEquals("y", components.get(1).getVarId().getImage());
        // TODO: type resolution for record components
        // Assert.assertNull(components.get(0).getVarId().getNameDeclaration().getAccessNodeParent());
        // Assert.assertEquals(Integer.TYPE, components.get(0).getVarId().getNameDeclaration().getType());
        // Assert.assertEquals("int", components.get(0).getVarId().getNameDeclaration().getTypeImage());
    }

    @Test(expected = ParseException.class)
    public void recordPointBeforeJava14PreviewShouldFail() {
        java14.parseResource("Point.java");
    }

    @Test(expected = ParseException.class)
    public void recordCtorWithThrowsShouldFail() {
        java14p.parse("  record R {"
                          + "   R throws IOException {}"
                          + "  }");
    }

    @Test
    public void innerRecords() {
        ASTCompilationUnit compilationUnit = java14p.parseResource("Records.java");
        List<ASTRecordDeclaration> recordDecls = compilationUnit.findDescendantsOfType(ASTRecordDeclaration.class, true);
        Assert.assertEquals(7, recordDecls.size());

        ASTRecordDeclaration complex = recordDecls.get(0);
        Assert.assertEquals("MyComplex", complex.getSimpleName());
        Assert.assertTrue(complex.isNested());
        Assert.assertEquals(0, getComponent(complex, 0).getDeclaredAnnotations().count());
        Assert.assertEquals(1, getComponent(complex, 1).getDeclaredAnnotations().count());
        Assert.assertEquals(2, complex.getDeclarations().count());
        Assert.assertTrue(complex.getDeclarations().get(0) instanceof ASTConstructorDeclaration);
        Assert.assertTrue(complex.getDeclarations().get(1) instanceof ASTRecordDeclaration);

        ASTRecordDeclaration nested = recordDecls.get(1);
        Assert.assertEquals("Nested", nested.getSimpleName());
        Assert.assertTrue(nested.isNested());

        ASTRecordDeclaration range = recordDecls.get(2);
        Assert.assertEquals("Range", range.getSimpleName());
        Assert.assertEquals(2, range.getRecordComponents().size());
        List<ASTRecordConstructorDeclaration> rangeConstructors = range.findDescendantsOfType(ASTRecordConstructorDeclaration.class);
        Assert.assertEquals(1, rangeConstructors.size());
        Assert.assertEquals("Range", rangeConstructors.get(0).getImage());
        JavaNode mods = rangeConstructors.get(0).getChild(0);
        Assert.assertTrue(mods instanceof ASTModifierList);
        Assert.assertEquals(1, mods.getNumChildren());
        Assert.assertEquals(2, range.getDeclarations().count());

        ASTRecordDeclaration varRec = recordDecls.get(3);
        Assert.assertEquals("VarRec", varRec.getSimpleName());
        Assert.assertEquals("x", getComponent(varRec, 0).getVarId().getImage());
        Assert.assertTrue(getComponent(varRec, 0).isVarargs());
        Assert.assertEquals(2, getComponent(varRec, 0).getDeclaredAnnotations().count());
        Assert.assertEquals(1, getComponent(varRec, 0).getTypeNode().descendants(ASTAnnotation.class).count());

        ASTRecordDeclaration arrayRec = recordDecls.get(4);
        Assert.assertEquals("ArrayRec", arrayRec.getSimpleName());
        Assert.assertEquals("x", getComponent(arrayRec, 0).getVarId().getImage());
        Assert.assertTrue(getComponent(arrayRec, 0).getVarId().hasArrayType());

        ASTRecordDeclaration emptyRec = recordDecls.get(5);
        Assert.assertEquals("EmptyRec", emptyRec.getSimpleName());
        Assert.assertEquals(0, emptyRec.getRecordComponents().size());

        ASTRecordDeclaration personRec = recordDecls.get(6);
        Assert.assertEquals("PersonRecord", personRec.getSimpleName());
        ASTImplementsList impl = personRec.getFirstChildOfType(ASTImplementsList.class);
        Assert.assertEquals(2, impl.findChildrenOfType(ASTClassOrInterfaceType.class).size());
    }

    private ASTRecordComponent getComponent(ASTRecordDeclaration arrayRec, int index) {
        return arrayRec.getRecordComponents().getChild(index);
    }


    @Test(expected = ParseException.class)
    public void recordIsARestrictedIdentifier() {
        java14p.parse("public class record {}");
    }
}
