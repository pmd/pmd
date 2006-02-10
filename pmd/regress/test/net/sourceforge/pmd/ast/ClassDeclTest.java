/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package test.net.sourceforge.pmd.ast;

import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;
import test.net.sourceforge.pmd.testframework.ParserTst;

import java.util.Iterator;
import java.util.Set;

public class ClassDeclTest extends ParserTst {

    public void testPublic() throws Throwable {
        String access[] = {"public"};
        ASTClassOrInterfaceDeclaration acd = getClassDecl(access);
        verifyFlags(acd, true, false, false, false);
    }

    public void testAbstract() throws Throwable {
        String access[] = {"abstract"};
        ASTClassOrInterfaceDeclaration acd = getClassDecl(access);
        verifyFlags(acd, false, true, false, false);
    }

    public void testFinal() throws Throwable {
        String access[] = {"final"};
        ASTClassOrInterfaceDeclaration acd = getClassDecl(access);
        verifyFlags(acd, false, false, true, false);
    }

    public void testStrict() throws Throwable {
        String access[] = {"strictfp"};
        ASTClassOrInterfaceDeclaration acd = getClassDecl(access);
        verifyFlags(acd, false, false, false, true);
    }

    public void testPublicFinal() throws Throwable {
        String access[] = {"public", "final"};
        ASTClassOrInterfaceDeclaration acd = getClassDecl(access);
        verifyFlags(acd, true, false, true, false);
    }

    public void verifyFlags(ASTClassOrInterfaceDeclaration acd, boolean bPublic, boolean bAbstract, boolean bFinal, boolean bStrict) {
        assertEquals("Public: ", bPublic, acd.isPublic());
        assertEquals("Abstract: ", bAbstract, acd.isAbstract());
        assertEquals("Final: ", bFinal, acd.isFinal());
        assertEquals("Strict: ", bStrict, acd.isStrictfp());
    }

    public ASTClassOrInterfaceDeclaration getClassDecl(String access[]) throws Throwable {
        String javaCode = "";

        for (int i = 0; i < access.length; i++) {
            javaCode += access[i] + " ";
        }

        javaCode += " class Test { } ";

        Set classes = getNodes(ASTClassOrInterfaceDeclaration.class, javaCode);

        assertEquals("Wrong number of classes", 1, classes.size());
        Iterator i = classes.iterator();
        return (ASTClassOrInterfaceDeclaration) i.next();
    }
}
