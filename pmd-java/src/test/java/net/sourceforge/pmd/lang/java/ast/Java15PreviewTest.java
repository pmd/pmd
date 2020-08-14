/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.List;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.lang.java.JavaParsingHelper;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeBodyDeclaration.DeclarationKind;

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

    @Test
    public void recordPoint() {
        ASTCompilationUnit compilationUnit = java15p.parseResource("Point.java");
        ASTRecordDeclaration recordDecl = compilationUnit.getFirstDescendantOfType(ASTRecordDeclaration.class);
        Assert.assertEquals("Point", recordDecl.getImage());
        Assert.assertFalse(recordDecl.isNested());
        Assert.assertFalse(recordDecl.isLocal());
        Assert.assertTrue("Records are implicitly always final", recordDecl.isFinal());
        List<ASTRecordComponent> components = recordDecl.getFirstChildOfType(ASTRecordComponentList.class)
                                                        .findChildrenOfType(ASTRecordComponent.class);
        Assert.assertEquals(2, components.size());
        Assert.assertEquals("x", components.get(0).getVarId().getImage());
        Assert.assertEquals("y", components.get(1).getVarId().getImage());
        Assert.assertNull(components.get(0).getVarId().getNameDeclaration().getAccessNodeParent());
        Assert.assertEquals(Integer.TYPE, components.get(0).getVarId().getNameDeclaration().getType());
        Assert.assertEquals("int", components.get(0).getVarId().getNameDeclaration().getTypeImage());
    }

    @Test(expected = ParseException.class)
    public void recordPointBeforeJava15PreviewShouldFail() {
        java15.parseResource("Point.java");
    }

    @Test(expected = ParseException.class)
    public void recordCtorWithThrowsShouldFail() {
        java15p.parse("  record R {"
                          + "   R throws IOException {}"
                          + "  }");
    }

    @Test(expected = ParseException.class)
    public void recordMustNotExtend() {
        java15p.parse("record RecordEx(int x) extends Number { }");
    }

    @Test(expected = ParseException.class)
    @Ignore("Should we check this?")
    public void recordCannotBeAbstract() {
        java15p.parse("abstract record RecordEx(int x) { }");
    }

    @Test(expected = ParseException.class)
    @Ignore("Should we check this?")
    public void recordCannotHaveInstanceFields() {
        java15p.parse("record RecordFields(int x) { private int y = 1; }");
    }

    @Test
    public void innerRecords() {
        ASTCompilationUnit compilationUnit = java15p.parseResource("Records.java");
        List<ASTRecordDeclaration> recordDecls = compilationUnit.findDescendantsOfType(ASTRecordDeclaration.class, true);
        Assert.assertEquals(7, recordDecls.size());

        ASTRecordDeclaration complex = recordDecls.get(0);
        Assert.assertEquals("MyComplex", complex.getSimpleName());
        Assert.assertTrue(complex.isNested());
        Assert.assertEquals(0, getComponent(complex, 0).findChildrenOfType(ASTAnnotation.class).size());
        Assert.assertEquals(1, getComponent(complex, 1).findChildrenOfType(ASTAnnotation.class).size());
        Assert.assertEquals(3, complex.getDeclarations().size());
        Assert.assertTrue(complex.getDeclarations().get(0).getChild(1) instanceof ASTConstructorDeclaration);
        Assert.assertTrue(complex.getDeclarations().get(1).getChild(0) instanceof ASTRecordDeclaration);
        Assert.assertTrue(complex.getDeclarations().get(2) instanceof ASTClassOrInterfaceBodyDeclaration);
        Assert.assertTrue(complex.getParent() instanceof ASTClassOrInterfaceBodyDeclaration);
        ASTClassOrInterfaceBodyDeclaration complexParent = complex.getFirstParentOfType(ASTClassOrInterfaceBodyDeclaration.class);
        Assert.assertEquals(DeclarationKind.RECORD, complexParent.getKind());
        Assert.assertSame(complex, complexParent.getDeclarationNode());

        ASTRecordDeclaration nested = recordDecls.get(1);
        Assert.assertEquals("Nested", nested.getSimpleName());
        Assert.assertTrue(nested.isNested());

        ASTRecordDeclaration range = recordDecls.get(2);
        Assert.assertEquals("Range", range.getSimpleName());
        Assert.assertEquals(2, range.getComponentList().size());
        List<ASTRecordConstructorDeclaration> rangeConstructors = range.findDescendantsOfType(ASTRecordConstructorDeclaration.class);
        Assert.assertEquals(1, rangeConstructors.size());
        Assert.assertEquals("Range", rangeConstructors.get(0).getImage());
        Assert.assertTrue(rangeConstructors.get(0).getChild(0) instanceof ASTAnnotation);
        Assert.assertEquals(2, range.getDeclarations().size());

        ASTRecordDeclaration varRec = recordDecls.get(3);
        Assert.assertEquals("VarRec", varRec.getSimpleName());
        Assert.assertEquals("x", getComponent(varRec, 0).getVarId().getImage());
        Assert.assertTrue(getComponent(varRec, 0).isVarargs());
        Assert.assertEquals(2, getComponent(varRec, 0).findChildrenOfType(ASTAnnotation.class).size());
        Assert.assertEquals(1, getComponent(varRec, 0).getTypeNode().findDescendantsOfType(ASTAnnotation.class).size());

        ASTRecordDeclaration arrayRec = recordDecls.get(4);
        Assert.assertEquals("ArrayRec", arrayRec.getSimpleName());
        Assert.assertEquals("x", getComponent(arrayRec, 0).getVarId().getImage());
        Assert.assertTrue(getComponent(arrayRec, 0).getVarId().hasArrayType());

        ASTRecordDeclaration emptyRec = recordDecls.get(5);
        Assert.assertEquals("EmptyRec", emptyRec.getSimpleName());
        Assert.assertEquals(0, emptyRec.getComponentList().size());

        ASTRecordDeclaration personRec = recordDecls.get(6);
        Assert.assertEquals("PersonRecord", personRec.getSimpleName());
        ASTImplementsList impl = personRec.getFirstChildOfType(ASTImplementsList.class);
        Assert.assertEquals(2, impl.findChildrenOfType(ASTClassOrInterfaceType.class).size());
    }

    private ASTRecordComponent getComponent(ASTRecordDeclaration arrayRec, int index) {
        return (ASTRecordComponent) arrayRec.getComponentList().getChild(index);
    }


    @Test(expected = ParseException.class)
    public void recordIsARestrictedIdentifier() {
        java15p.parse("public class record {}");
    }

    @Test
    public void localRecords() {
        ASTCompilationUnit compilationUnit = java15p.parseResource("LocalRecords.java");
        List<ASTRecordDeclaration> records = compilationUnit.findDescendantsOfType(ASTRecordDeclaration.class);
        Assert.assertEquals(1, records.size());
        Assert.assertEquals("MerchantSales", records.get(0).getSimpleName());
        Assert.assertTrue(records.get(0).isLocal());
    }
}
