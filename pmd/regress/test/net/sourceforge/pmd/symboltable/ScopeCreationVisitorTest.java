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
package test.net.sourceforge.pmd.symboltable;

import junit.framework.TestCase;
import net.sourceforge.pmd.ast.ASTClassBodyDeclaration;
import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.ASTIfStatement;
import net.sourceforge.pmd.ast.ASTTryStatement;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.symboltable.BasicScopeCreationVisitor;
import net.sourceforge.pmd.symboltable.BasicScopeFactory;
import net.sourceforge.pmd.symboltable.GlobalScope;
import net.sourceforge.pmd.symboltable.LocalScope;
import net.sourceforge.pmd.symboltable.ScopeFactory;

import java.util.Stack;

public class ScopeCreationVisitorTest extends TestCase {

    private class MyCB extends ASTClassBodyDeclaration {
        public MyCB() {
            super(1);
        }
        public boolean isAnonymousInnerClass() {
            return true;
        }
    }

    private class MySF implements ScopeFactory {
        public boolean gotCalled;
        public void openScope(Stack scopes, SimpleNode node) {
            this.gotCalled = true;
            scopes.add(new Object());
        }
    }

    public void testScopesAreCreated() {
        BasicScopeCreationVisitor sc = new BasicScopeCreationVisitor(new BasicScopeFactory());

        ASTCompilationUnit acu = new ASTCompilationUnit(1);
        acu.setScope(new GlobalScope());

        ASTTryStatement tryNode = new ASTTryStatement(2);
        tryNode.setScope(new LocalScope());
        tryNode.jjtSetParent(acu);

        ASTIfStatement ifNode = new ASTIfStatement(3);
        ifNode.jjtSetParent(tryNode);

        sc.visit(acu, null);

        assertTrue(ifNode.getScope() instanceof LocalScope);
    }

    public void testAnonymousInnerClassIsCreated() {
        MySF sf = new MySF();
        BasicScopeCreationVisitor sc = new BasicScopeCreationVisitor(sf);
        ASTClassBodyDeclaration cb = new MyCB();
        sc.visit(cb, null);
        assertTrue(sf.gotCalled);
    }

    public void testAnonymousInnerClassIsNotCreated() {
        MySF sf = new MySF();
        new BasicScopeCreationVisitor(sf).visit(new ASTClassBodyDeclaration(1), null);
        assertFalse(sf.gotCalled);
    }

}
