/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.lang.ast.test.BaseParsingHelper;
import net.sourceforge.pmd.lang.ast.test.BaseTreeDumpTest;
import net.sourceforge.pmd.lang.ast.test.RelevantAttributePrinter;
import net.sourceforge.pmd.lang.java.JavaParsingHelper;

public class Java15PreviewTreeDumpTest extends BaseTreeDumpTest {
    private final JavaParsingHelper java15p =
            JavaParsingHelper.WITH_PROCESSING.withDefaultVersion("15-preview")
                    .withResourceContext(Java15PreviewTreeDumpTest.class, "jdkversiontests/java15p/");
    private final JavaParsingHelper java15 = java15p.withDefaultVersion("15");

    public Java15PreviewTreeDumpTest() {
        super(new RelevantAttributePrinter(), ".java");
    }

    @Override
    public BaseParsingHelper<?, ?> getParser() {
        return java15p;
    }

    @Test
    public void patternMatchingInstanceof() {
        doTest("PatternMatchingInstanceof");

        // extended tests for type resolution etc.
        ASTCompilationUnit compilationUnit = java15p.parseResource("PatternMatchingInstanceof.java");
        List<ASTInstanceOfExpression> instanceOfExpressions = compilationUnit.findDescendantsOfType(ASTInstanceOfExpression.class);
        for (ASTInstanceOfExpression expr : instanceOfExpressions) {
            ASTVariableDeclaratorId variable = expr.getChild(1).getFirstChildOfType(ASTVariableDeclaratorId.class);
            Assert.assertEquals(String.class, variable.getType());
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
        doTest("Point");

        // extended tests for type resolution etc.
        ASTCompilationUnit compilationUnit = java15p.parseResource("Point.java");
        ASTRecordDeclaration recordDecl = compilationUnit.getFirstDescendantOfType(ASTRecordDeclaration.class);
        List<ASTRecordComponent> components = recordDecl.getFirstChildOfType(ASTRecordComponentList.class)
                .findChildrenOfType(ASTRecordComponent.class);
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

    @Test
    public void innerRecords() {
        doTest("Records");
    }

    @Test(expected = ParseException.class)
    public void recordIsARestrictedIdentifier() {
        java15p.parse("public class record {}");
    }

    @Test
    public void localRecords() {
        doTest("LocalRecords");
    }

    @Test(expected = ParseException.class)
    public void sealedClassBeforeJava15Preview() {
        java15.parseResource("geometry/Shape.java");
    }

    @Test
    public void sealedClass() {
        doTest("geometry/Shape");
    }

    @Test
    public void nonSealedClass() {
        doTest("geometry/Square");
    }

    @Test(expected = ParseException.class)
    public void sealedInterfaceBeforeJava15Preview() {
        java15.parseResource("expression/Expr.java");
    }

    @Test
    public void sealedInterface() {
        doTest("expression/Expr");
    }

    @Test
    public void localInterfaceAndEnums() {
        doTest("LocalInterfacesAndEnums");
    }

    @Test(expected = ParseException.class)
    public void localInterfacesAndEnumsBeforeJava15PreviewShouldFail() {
        java15.parseResource("LocalInterfacesAndEnums.java");
    }
}
