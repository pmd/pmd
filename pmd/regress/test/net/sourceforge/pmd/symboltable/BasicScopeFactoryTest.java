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
import net.sourceforge.pmd.ast.ASTBlock;
import net.sourceforge.pmd.ast.ASTClassBodyDeclaration;
import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.ast.ASTForStatement;
import net.sourceforge.pmd.ast.ASTIfStatement;
import net.sourceforge.pmd.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.ast.ASTTryStatement;
import net.sourceforge.pmd.ast.ASTUnmodifiedClassDeclaration;
import net.sourceforge.pmd.ast.ASTUnmodifiedInterfaceDeclaration;
import net.sourceforge.pmd.symboltable.BasicScopeFactory;
import net.sourceforge.pmd.symboltable.ClassScope;
import net.sourceforge.pmd.symboltable.GlobalScope;
import net.sourceforge.pmd.symboltable.LocalScope;
import net.sourceforge.pmd.symboltable.MethodScope;
import net.sourceforge.pmd.symboltable.ScopeFactory;

import java.util.Stack;

public class BasicScopeFactoryTest extends TestCase {

    public void testGlobalScope() {
        ScopeFactory sf = new BasicScopeFactory();
        Stack s = new Stack();
        sf.openScope(s, new ASTCompilationUnit(1));
        assertEquals(1, s.size());
        assertTrue(s.get(0) instanceof GlobalScope);
    }

    public void testClassScope() {
        ScopeFactory sf = new BasicScopeFactory();
        Stack s = new Stack();
        sf.openScope(s, new ASTCompilationUnit(1));
        sf.openScope(s, new ASTUnmodifiedClassDeclaration(2));
        assertTrue(s.get(1) instanceof ClassScope);
        sf.openScope(s, new ASTUnmodifiedInterfaceDeclaration(1));
        assertTrue(s.get(2) instanceof ClassScope);
        sf.openScope(s, new ASTClassBodyDeclaration(1));
        assertTrue(s.get(3) instanceof ClassScope);
    }

    public void testMethodScope() {
        ScopeFactory sf = new BasicScopeFactory();
        Stack s = new Stack();
        sf.openScope(s, new ASTCompilationUnit(1));
        sf.openScope(s, new ASTMethodDeclaration(2));
        assertTrue(s.get(1) instanceof MethodScope);
        sf.openScope(s, new ASTConstructorDeclaration(1));
        assertTrue(s.get(2) instanceof MethodScope);
    }

    public void testLocalScope() {
        ScopeFactory sf = new BasicScopeFactory();
        Stack s = new Stack();
        sf.openScope(s, new ASTCompilationUnit(1));
        sf.openScope(s, new ASTBlock(2));
        assertTrue(s.get(1) instanceof LocalScope);
        sf.openScope(s, new ASTTryStatement(1));
        assertTrue(s.get(2) instanceof LocalScope);
        sf.openScope(s, new ASTForStatement(1));
        assertTrue(s.get(3) instanceof LocalScope);
        sf.openScope(s, new ASTIfStatement(1));
        assertTrue(s.get(4) instanceof LocalScope);
    }
}
