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
package net.sourceforge.pmd.rules.junit;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.ast.ASTArguments;
import net.sourceforge.pmd.ast.ASTName;
import net.sourceforge.pmd.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.ast.ASTPrimaryPrefix;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class JUnitAssertionsShouldIncludeMessageRule extends AbstractRule implements Rule {

    private static class AssertionCall {
        public int args;
        public String name;

        public AssertionCall(int args, String name) {
            this.args = args;
            this.name = name;
        }
    }

    private List checks = new ArrayList();

    public JUnitAssertionsShouldIncludeMessageRule() {
        checks.add(new AssertionCall(2, "assertEquals"));
        checks.add(new AssertionCall(1, "assertTrue"));
        checks.add(new AssertionCall(1, "assertNull"));
        checks.add(new AssertionCall(2, "assertSame"));
        checks.add(new AssertionCall(1, "assertNotNull"));
    }

    public Object visit(ASTArguments node, Object data) {
        for (Iterator i = checks.iterator(); i.hasNext();) {
            AssertionCall call = (AssertionCall) i.next();
            check((RuleContext) data, node, call.args, call.name);
        }
        return super.visit(node, data);
    }

    private void check(RuleContext ctx, ASTArguments node, int args, String targetMethodName) {
        if (node.getArgumentCount() == args && node.jjtGetParent().jjtGetParent() instanceof ASTPrimaryExpression) {
            ASTPrimaryExpression primary = (ASTPrimaryExpression) node.jjtGetParent().jjtGetParent();
            if (primary.jjtGetChild(0) instanceof ASTPrimaryPrefix && primary.jjtGetChild(0).jjtGetNumChildren() > 0 && primary.jjtGetChild(0).jjtGetChild(0) instanceof ASTName) {
                ASTName name = (ASTName) primary.jjtGetChild(0).jjtGetChild(0);
                if (name.getImage().equals(targetMethodName)) {
                    ctx.getReport().addRuleViolation(createRuleViolation(ctx, name.getBeginLine()));
                }
            }
        }
    }
}
