/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.lang.ast.test.BaseParsingHelper;
import net.sourceforge.pmd.lang.java.BaseJavaTreeDumpTest;
import net.sourceforge.pmd.lang.java.JavaParsingHelper;
import net.sourceforge.pmd.lang.java.symbols.JElementSymbol;
import net.sourceforge.pmd.lang.java.types.JPrimitiveType;

public class Java16TreeDumpTest extends BaseJavaTreeDumpTest {
    private final JavaParsingHelper java16 =
            JavaParsingHelper.WITH_PROCESSING.withDefaultVersion("16")
                    .withResourceContext(Java15TreeDumpTest.class, "jdkversiontests/java16/");
    private final JavaParsingHelper java16p = java16.withDefaultVersion("16-preview");
    private final JavaParsingHelper java15 = java16.withDefaultVersion("15");


    @Override
    public BaseParsingHelper<?, ?> getParser() {
        return java16;
    }

    @Test
    public void patternMatchingInstanceof() {
        doTest("PatternMatchingInstanceof");

        // extended tests for type resolution etc.
        ASTCompilationUnit compilationUnit = java16.parseResource("PatternMatchingInstanceof.java");
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
    public void patternMatchingInstanceofBeforeJava16ShouldFail() {
        java15.parseResource("PatternMatchingInstanceof.java");
    }

    @Test
    public void localClassAndInterfaceDeclarations() {
        doTest("LocalClassAndInterfaceDeclarations");
    }

    @Test(expected = ParseException.class)
    public void localClassAndInterfaceDeclarationsBeforeJava16ShouldFail() {
        java15.parseResource("LocalClassAndInterfaceDeclarations.java");
    }

    @Test(expected = ParseException.class)
    public void localAnnotationsAreNotAllowed() {
        java16.parse("public class Foo { { @interface MyLocalAnnotation {} } }");
    }

    @Test
    public void localRecords() {
        doTest("LocalRecords");
    }

    @Test
    public void recordPoint() {
        doTest("Point");

        // extended tests for type resolution etc.
        ASTCompilationUnit compilationUnit = java16.parseResource("Point.java");
        ASTRecordDeclaration recordDecl = compilationUnit.descendants(ASTRecordDeclaration.class).first();
        List<ASTRecordComponent> components = recordDecl.descendants(ASTRecordComponentList.class)
                .children(ASTRecordComponent.class).toList();

        ASTVariableDeclaratorId varId = components.get(0).getVarId();
        JElementSymbol symbol = varId.getSymbol();
        Assert.assertEquals("x", symbol.getSimpleName());
        Assert.assertTrue(varId.getTypeMirror().isPrimitive(JPrimitiveType.PrimitiveTypeKind.INT));
    }

    @Test(expected = ParseException.class)
    public void recordPointBeforeJava16ShouldFail() {
        java15.parseResource("Point.java");
    }

    @Test(expected = ParseException.class)
    public void recordCtorWithThrowsShouldFail() {
        java16.parse("  record R {"
                + "   R throws IOException {}"
                + "  }");
    }

    @Test(expected = ParseException.class)
    public void recordMustNotExtend() {
        java16.parse("record RecordEx(int x) extends Number { }");
    }

    @Test
    public void innerRecords() {
        doTest("Records");
    }

    @Test(expected = ParseException.class)
    public void recordIsARestrictedIdentifier() {
        java16.parse("public class record {}");
    }

    @Test
    public void sealedAndNonSealedIdentifiers() {
        doTest("NonSealedIdentifier");
        java16p.parseResource("NonSealedIdentifier.java"); // make sure we can parse it with preview as well
    }
}
