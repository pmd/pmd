/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.Arrays;
import java.util.HashSet;

import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.lang.ast.test.BaseParsingHelper;
import net.sourceforge.pmd.lang.ast.test.BaseTreeDumpTest;
import net.sourceforge.pmd.lang.ast.test.RelevantAttributePrinter;
import net.sourceforge.pmd.lang.java.JavaParsingHelper;

public class Java16PreviewTreeDumpTest extends BaseTreeDumpTest {
    private final JavaParsingHelper java16p =
            JavaParsingHelper.WITH_PROCESSING.withDefaultVersion("16-preview")
                    .withResourceContext(Java16PreviewTreeDumpTest.class, "jdkversiontests/java16p/");
    private final JavaParsingHelper java16 = java16p.withDefaultVersion("16");

    public Java16PreviewTreeDumpTest() {
        super(new RelevantAttributePrinter(), ".java");
    }

    @Override
    public BaseParsingHelper<?, ?> getParser() {
        return java16p;
    }

    @Test(expected = ParseException.class)
    public void sealedClassBeforeJava16Preview() {
        java16.parseResource("geometry/Shape.java");
    }

    @Test
    public void sealedClass() {
        doTest("geometry/Shape");

        ASTCompilationUnit compilationUnit = java16p.parseResource("geometry/Shape.java");
        ASTClassOrInterfaceDeclaration sealedClass = compilationUnit.descendants(ASTClassOrInterfaceDeclaration.class).first();
        Assert.assertEquals(new HashSet<>(Arrays.asList(JModifier.SEALED, JModifier.PUBLIC)),
                sealedClass.getModifiers().getExplicitModifiers());
    }

    @Test
    public void nonSealedClass() {
        doTest("geometry/Square");

        ASTCompilationUnit compilationUnit = java16p.parseResource("geometry/Square.java");
        ASTClassOrInterfaceDeclaration sealedClass = compilationUnit.descendants(ASTClassOrInterfaceDeclaration.class).first();
        Assert.assertEquals(new HashSet<>(Arrays.asList(JModifier.NON_SEALED, JModifier.PUBLIC)),
                sealedClass.getModifiers().getExplicitModifiers());
    }

    @Test(expected = ParseException.class)
    public void sealedInterfaceBeforeJava15Preview() {
        java16.parseResource("expression/Expr.java");
    }

    @Test
    public void sealedInterface() {
        doTest("expression/Expr");

        ASTCompilationUnit compilationUnit = java16p.parseResource("expression/Expr.java");
        ASTClassOrInterfaceDeclaration sealedClass = compilationUnit.descendants(ASTClassOrInterfaceDeclaration.class).first();
        Assert.assertEquals(new HashSet<>(Arrays.asList(JModifier.SEALED, JModifier.PUBLIC)),
                sealedClass.getModifiers().getExplicitModifiers());
    }
}
