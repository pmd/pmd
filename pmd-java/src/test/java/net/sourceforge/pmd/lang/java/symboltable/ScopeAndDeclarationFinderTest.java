/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.symboltable;

import java.util.List;
import java.util.Set;

import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.java.JavaLanguageModule;
import net.sourceforge.pmd.lang.java.ast.ASTLambdaExpression;
import net.sourceforge.pmd.lang.symboltable.NameDeclaration;

import org.junit.Assert;
import org.junit.Test;

public class ScopeAndDeclarationFinderTest extends STBBaseTst {

    /**
     * Unit test for https://sourceforge.net/p/pmd/bugs/1317/
     */
    @Test
    public void testJava8LambdaScoping() {
        String source = "public class MultipleLambdas {\n" +
                "  Observer a = (o, arg) -> System.out.println(\"a:\" + arg);\n" +
                "  Observer b = (o, arg) -> System.out.println(\"b:\" + arg);\n" +
                "}";
        parseCode(source, LanguageRegistry.getLanguage(JavaLanguageModule.NAME).getVersion("1.8"));

        List<ASTLambdaExpression> lambdas = acu.findDescendantsOfType(ASTLambdaExpression.class);
        Assert.assertEquals(2, lambdas.size());
        LocalScope scope1 = (LocalScope)lambdas.get(0).getScope();
        LocalScope scope2 = (LocalScope)lambdas.get(1).getScope();
        Assert.assertNotSame(scope1, scope2);

        for (ASTLambdaExpression l : lambdas) {
            LocalScope scope = (LocalScope)l.getScope();
            Assert.assertEquals(2, scope.getVariableDeclarations().size());
            Assert.assertTrue(scope.contains(new JavaNameOccurrence(null, "o")));
            Assert.assertTrue(scope.contains(new JavaNameOccurrence(null, "arg")));
            Set<NameDeclaration> declarations = scope.findVariableHere(new JavaNameOccurrence(null, "arg"));
            Assert.assertEquals(1, declarations.size());
            NameDeclaration decl = declarations.iterator().next();
            Assert.assertEquals(1, scope.getVariableDeclarations().get(decl).size());
        }
    }
}
