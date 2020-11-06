/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symboltable;

import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceBody;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTLambdaExpression;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclarator;
import net.sourceforge.pmd.lang.symboltable.NameDeclaration;

public class ScopeAndDeclarationFinderTest extends BaseNonParserTest {

    /**
     * Unit test for https://sourceforge.net/p/pmd/bugs/1317/
     */
    @Test
    public void testJava8LambdaScoping() {
        String source = "public class MultipleLambdas {\n"
            + "  Observer a = (o, arg) -> System.out.println(\"a:\" + arg);\n"
            + "  Observer b = (o, arg) -> System.out.println(\"b:\" + arg);\n" + "}";
        List<ASTLambdaExpression> lambdas = java.parse(source, "1.8").findDescendantsOfType(ASTLambdaExpression.class);

        Assert.assertEquals(2, lambdas.size());
        LocalScope scope1 = (LocalScope) lambdas.get(0).getScope();
        LocalScope scope2 = (LocalScope) lambdas.get(1).getScope();
        Assert.assertNotSame(scope1, scope2);

        for (ASTLambdaExpression l : lambdas) {
            LocalScope scope = (LocalScope) l.getScope();
            Assert.assertEquals(2, scope.getVariableDeclarations().size());
            Assert.assertTrue(scope.contains(new JavaNameOccurrence(null, "o")));
            Assert.assertTrue(scope.contains(new JavaNameOccurrence(null, "arg")));
            Set<NameDeclaration> declarations = scope.findVariableHere(new JavaNameOccurrence(null, "arg"));
            Assert.assertEquals(1, declarations.size());
            NameDeclaration decl = declarations.iterator().next();
            Assert.assertEquals(1, scope.getVariableDeclarations().get(decl).size());
        }
    }

    @Test
    public void testAnnonInnerClassScoping() {
        String source = "public class Foo {" + PMD.EOL
                + "  public static final Creator<Foo> CREATOR = new Creator<Foo>() {" + PMD.EOL
                + "    @Override public Foo createFromParcel(Parcel source) {" + PMD.EOL
                + "      return new Foo();" + PMD.EOL
                + "    }" + PMD.EOL
                + "    @Override public Foo[] newArray(int size) {" + PMD.EOL
                + "      return new Foo[size];" + PMD.EOL
                + "    }" + PMD.EOL
                + "  };" + PMD.EOL
                + "}" + PMD.EOL;
        ASTCompilationUnit acu = java.parse(source, "1.6");

        ClassScope cs = (ClassScope) acu.getFirstDescendantOfType(ASTClassOrInterfaceDeclaration.class).getScope();
        Assert.assertEquals(1, cs.getClassDeclarations().size()); // There should be 1 anonymous class

        List<ASTMethodDeclarator> methods = acu.getFirstDescendantOfType(ASTClassOrInterfaceBody.class) // outer class
                .getFirstDescendantOfType(ASTClassOrInterfaceBody.class) // inner class
                .findDescendantsOfType(ASTMethodDeclarator.class, true); // inner class methods
        Assert.assertEquals(2, methods.size());
        ClassScope scope1 = methods.get(0).getScope().getEnclosingScope(ClassScope.class);
        ClassScope scope2 = methods.get(1).getScope().getEnclosingScope(ClassScope.class);
        Assert.assertSame(scope1, scope2);
    }
}
