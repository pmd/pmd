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
package net.sourceforge.pmd.symboltable;

import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.ast.JavaParserVisitorAdapter;

import java.util.Iterator;

public class SymbolFacade extends JavaParserVisitorAdapter {

    public void initializeWith(ASTCompilationUnit node) {
        // first, traverse the AST and create all the scopes
        BasicScopeCreationVisitor sc = new BasicScopeCreationVisitor(new BasicScopeFactory());
        node.jjtAccept(sc, null);

        // traverse the AST and pick up all the declarations
        DeclarationFinder df = new DeclarationFinder();
        node.jjtAccept(df, null);

        // finally, traverse the AST and pick up all the name occurrences
        node.jjtAccept(this, null);
    }

    public Object visit(ASTPrimaryExpression node, Object data) {
        NameOccurrences qualifiedNames = new NameOccurrences(node);
        NameDeclaration decl = null;
        for (Iterator i = qualifiedNames.iterator(); i.hasNext();) {
            NameOccurrence occ = (NameOccurrence) i.next();
            Search search = new Search(occ);
            if (decl == null) {
                // doing the first name lookup
                search.execute();
                decl = search.getResult();
                if (decl == null) {
                    // we can't find it, so just give up
                    // when we decide searches across compilation units like a compiler would, we'll
                    // force this to either find a symbol or throw a "cannot resolve symbol" Exception
                    break;
                }
            } else {
                // now we've got a scope we're starting with, so work from there
                search.execute(decl.getScope());
                decl = search.getResult();
            }
        }
        return super.visit(node, data);
    }

}
