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
import net.sourceforge.pmd.ast.ASTName;
import net.sourceforge.pmd.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.symboltable.LocalScope;
import net.sourceforge.pmd.symboltable.NameOccurrence;
import net.sourceforge.pmd.symboltable.VariableNameDeclaration;

public class LocalScopeTest extends TestCase {

    private class MyASTVariableDeclaratorId extends ASTVariableDeclaratorId {
        public MyASTVariableDeclaratorId(int x) {
            super(x);
        }

        public boolean isExceptionBlockParameter() {
            return true;
        }
    }

    public void testNameWithThisOrSuperIsNotFlaggedAsUnused() {
        LocalScope scope = new LocalScope();
        ASTName name = new ASTName(1);
        name.setImage("foo");
        ASTPrimaryPrefix prefix = new ASTPrimaryPrefix(2);
        prefix.setUsesThisModifier();
        name.jjtAddChild(prefix, 1);
        NameOccurrence occ = new NameOccurrence(name, "foo");
        scope.addVariableNameOccurrence(occ);
        assertTrue(!scope.getVariableDeclarations(false).keySet().iterator().hasNext());
    }

    public void testNameWithSuperIsNotFlaggedAsUnused() {
        LocalScope scope = new LocalScope();
        ASTName name = new ASTName(1);
        name.setImage("foo");
        ASTPrimaryPrefix prefix = new ASTPrimaryPrefix(2);
        prefix.setUsesSuperModifier();
        name.jjtAddChild(prefix, 1);
        NameOccurrence occ = new NameOccurrence(name, "foo");
        scope.addVariableNameOccurrence(occ);
        assertTrue(!scope.getVariableDeclarations(false).keySet().iterator().hasNext());
    }

    public void testExceptionParamNameIsDiscarded() {
        ASTVariableDeclaratorId node = new MyASTVariableDeclaratorId(1);
        VariableNameDeclaration decl = new VariableNameDeclaration(node);
        LocalScope scope = new LocalScope();
        scope.addDeclaration(decl);
        assertTrue(!scope.getVariableDeclarations(false).keySet().iterator().hasNext());
    }

}
