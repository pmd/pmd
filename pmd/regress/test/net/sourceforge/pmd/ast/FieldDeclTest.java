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

import net.sourceforge.pmd.ast.ASTFieldDeclaration;

import java.util.Iterator;
import java.util.Set;

public class FieldDeclTest extends ParserTst {
    public String makeAccessJavaCode(String access[]) {
        String RC = "public class Test { ";
        for (int i = 0; i < access.length; i++) {
            RC += access[i] + " ";
        }

        RC += " int j;  }";
        return RC;
    }

    public ASTFieldDeclaration getFieldDecl(String access[]) throws Throwable {
        Set fields = getNodes(ASTFieldDeclaration.class, makeAccessJavaCode(access));

        assertEquals("Wrong number of fields", 1, fields.size());
        Iterator i = fields.iterator();
        return (ASTFieldDeclaration) i.next();
    }

    public void testPublic() throws Throwable {
        String access[] = {"public"};
        ASTFieldDeclaration afd = getFieldDecl(access);
        assertTrue("Expecting field to be public.", afd.isPublic());
    }

    public void testProtected() throws Throwable {
        String access[] = {"protected"};
        ASTFieldDeclaration afd = getFieldDecl(access);
        assertTrue("Expecting field to be protected.", afd.isProtected());
    }

    public void testPrivate() throws Throwable {
        String access[] = {"private"};
        ASTFieldDeclaration afd = getFieldDecl(access);
        assertTrue("Expecting field to be private.", afd.isPrivate());
    }

    public void testStatic() throws Throwable {
        String access[] = {"private", "static"};
        ASTFieldDeclaration afd = getFieldDecl(access);
        assertTrue("Expecting field to be static.", afd.isStatic());
        assertTrue("Expecting field to be private.", afd.isPrivate());
    }

    public void testFinal() throws Throwable {
        String access[] = {"public", "final"};
        ASTFieldDeclaration afd = getFieldDecl(access);
        assertTrue("Expecting field to be final.", afd.isFinal());
        assertTrue("Expecting field to be public.", afd.isPublic());
    }

    public void testTransient() throws Throwable {
        String access[] = {"private", "transient"};
        ASTFieldDeclaration afd = getFieldDecl(access);
        assertTrue("Expecting field to be private.", afd.isPrivate());
        assertTrue("Expecting field to be transient.", afd.isTransient());
    }

    public void testVolatile() throws Throwable {
        String access[] = {"private", "volatile"};
        ASTFieldDeclaration afd = getFieldDecl(access);
        assertTrue("Expecting field to be volatile.", afd.isVolatile());
        assertTrue("Expecting field to be private.", afd.isPrivate());
    }
}
