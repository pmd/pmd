/**
 * <copyright>
 *  Copyright 1997-2002 InfoEther, LLC
 *  under sponsorship of the Defense Advanced Research Projects Agency
(DARPA).
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the Cougaar Open Source License as published
by
 *  DARPA on the Cougaar Open Source Website (www.cougaar.org).
 *
 *  THE COUGAAR SOFTWARE AND ANY DERIVATIVE SUPPLIED BY LICENSOR IS
 *  PROVIDED 'AS IS' WITHOUT WARRANTIES OF ANY KIND, WHETHER EXPRESS OR
 *  IMPLIED, INCLUDING (BUT NOT LIMITED TO) ALL IMPLIED WARRANTIES OF
 *  MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE, AND WITHOUT
 *  ANY WARRANTIES AS TO NON-INFRINGEMENT.  IN NO EVENT SHALL COPYRIGHT
 *  HOLDER BE LIABLE FOR ANY DIRECT, SPECIAL, INDIRECT OR CONSEQUENTIAL
 *  DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE OF DATA OR PROFITS,
 *  TORTIOUS CONDUCT, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 *  PERFORMANCE OF THE COUGAAR SOFTWARE.
 * </copyright>
 */
package test.net.sourceforge.pmd.ast;

import net.sourceforge.pmd.ast.ASTClassDeclaration;

import java.util.Iterator;
import java.util.Set;

public class ClassDeclTest extends ParserTst {

    public void testPublic() throws Throwable {
        String access[] = {"public"};
        ASTClassDeclaration acd = getClassDecl(access);
        verifyFlags(acd, true, false, false, false);
    }

    public void testAbstract() throws Throwable {
        String access[] = {"abstract"};
        ASTClassDeclaration acd = getClassDecl(access);
        verifyFlags(acd, false, true, false, false);
    }

    public void testFinal() throws Throwable {
        String access[] = {"final"};
        ASTClassDeclaration acd = getClassDecl(access);
        verifyFlags(acd, false, false, true, false);
    }

    public void testStrict() throws Throwable {
        String access[] = {"strictfp"};
        ASTClassDeclaration acd = getClassDecl(access);
        verifyFlags(acd, false, false, false, true);
    }

    public void testPublicFinal() throws Throwable {
        String access[] = {"public", "final"};
        ASTClassDeclaration acd = getClassDecl(access);
        verifyFlags(acd, true, false, true, false);
    }

    public void verifyFlags(ASTClassDeclaration acd, boolean bPublic, boolean bAbstract, boolean bFinal, boolean bStrict) {
        assertEquals("Public: ", bPublic, acd.isPublic());
        assertEquals("Abstract: ", bAbstract, acd.isAbstract());
        assertEquals("Final: ", bFinal, acd.isFinal());
        assertEquals("Strict: ", bStrict, acd.isStrict());
    }

    public ASTClassDeclaration getClassDecl(String access[]) throws Throwable {
        String javaCode = "";

        for (int i = 0; i < access.length; i++) {
            javaCode += access[i] + " ";
        }

        javaCode += " class Test { } ";

        Set classes = getNodes(ASTClassDeclaration.class, javaCode);

        assertEquals("Wrong number of classes", 1, classes.size());
        Iterator i = classes.iterator();
        return (ASTClassDeclaration) i.next();
    }
}
