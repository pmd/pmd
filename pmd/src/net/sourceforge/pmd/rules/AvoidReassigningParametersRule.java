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
package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.ast.ASTMethodDeclarator;
import net.sourceforge.pmd.ast.ASTType;
import net.sourceforge.pmd.symboltable.NameOccurrence;
import net.sourceforge.pmd.symboltable.Scope;
import net.sourceforge.pmd.symboltable.VariableNameDeclaration;
import net.sourceforge.pmd.symboltable.LocalScope;
import net.sourceforge.pmd.symboltable.MethodScope;
import net.sourceforge.pmd.symboltable.NameDeclaration;

import java.text.MessageFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class AvoidReassigningParametersRule extends AbstractRule {

    public Object visit(ASTMethodDeclarator node, Object data) {
        Scope scope = node.getScope();
        Map params = scope.getVariableDeclarations(true);
        for (Iterator i = params.keySet().iterator(); i.hasNext();) {
            VariableNameDeclaration decl = (VariableNameDeclaration) i.next();
            List usages = (List) params.get(decl);
            for (Iterator j = usages.iterator(); j.hasNext();) {
                NameOccurrence occ = (NameOccurrence) j.next();
                if (occ.isOnLeftHandSide() && occ.getNameForWhichThisIsAQualifier() == null && !decl.isArray()) {
                    RuleContext ctx = (RuleContext) data;
                    String msg = MessageFormat.format(getMessage(), new Object[]{decl.getImage()});
                    ctx.getReport().addRuleViolation(createRuleViolation(ctx, decl.getLine(), msg));
                }
            }
        }
        return super.visit(node, data);
    }
}
