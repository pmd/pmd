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

import net.sourceforge.pmd.ast.SimpleNode;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;

public class BasicScopeFactory implements ScopeFactory {

    private Set scopeEvaluators = new HashSet();

    public BasicScopeFactory() {
        scopeEvaluators.add(new GlobalScopeEvaluator());
        scopeEvaluators.add(new ClassScopeEvaluator());
        scopeEvaluators.add(new MethodScopeEvaluator());
        scopeEvaluators.add(new LocalScopeEvaluator());
    }

    public void openScope(Stack scopes, SimpleNode node) {
        for (Iterator i = scopeEvaluators.iterator(); i.hasNext();) {
            ScopeEvaluator ev = (ScopeEvaluator)i.next();
            if  (ev.isScopeCreatedBy(node)) {
                Scope scope = ev.getScopeFor(node);
                if (!(scope instanceof GlobalScope)) {
                    scope.setParent((Scope) scopes.peek());
                }
                scopes.add(scope);
                node.setScope((Scope)scopes.peek());
                break;
            }
        }
    }

}
