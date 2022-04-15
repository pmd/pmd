/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import static net.sourceforge.pmd.lang.java.types.TestUtilitiesForTypesKt.hasType;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.lang.ast.test.BaseParsingHelper;
import net.sourceforge.pmd.lang.java.BaseJavaTreeDumpTest;
import net.sourceforge.pmd.lang.java.JavaParsingHelper;
import net.sourceforge.pmd.lang.java.symbols.JElementSymbol;
import net.sourceforge.pmd.lang.java.types.JPrimitiveType;

public class Java16TreeDumpTest extends BaseJavaTreeDumpTest {
    private final JavaParsingHelper java16 =
            JavaParsingHelper.DEFAULT.withDefaultVersion("16")
                                     .withResourceContext(Java16TreeDumpTest.class, "jdkversiontests/java16/");
    private final JavaParsingHelper java15 = java16.withDefaultVersion("15");


    @Override
    public BaseParsingHelper<?, ?> getParser() {
        return java16;
    }

    @Test
    public void patternMatchingInstanceof() {
        doTest("PatternMatchingInstanceof");

        // extended tests for type resolution etc.
        ASTCompilationUnit ast = java16.parseResource("PatternMatchingInstanceof.java");
        NodeStream<ASTTypePattern> patterns = ast.descendants(ASTTypePattern.class);

        assertThat(patterns.toList(), not(empty()));

        for (ASTTypePattern expr : patterns) {
            assertThat(expr.getVarId(), hasType(String.class));
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
    }
}
