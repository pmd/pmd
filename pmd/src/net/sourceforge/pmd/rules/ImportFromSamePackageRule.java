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
import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.ASTImportDeclaration;
import net.sourceforge.pmd.ast.ASTName;
import net.sourceforge.pmd.ast.ASTPackageDeclaration;

public class ImportFromSamePackageRule extends AbstractRule {

    private String packageName;

    public Object visit(ASTCompilationUnit node, Object data) {
        packageName = null;
        return super.visit(node, data);
    }

    public Object visit(ASTPackageDeclaration node, Object data) {
        packageName = ((ASTName) node.jjtGetChild(0)).getImage();
        return data;
    }

    public Object visit(ASTImportDeclaration node, Object data) {
        ASTName nameNode = node.getImportedNameNode();
        RuleContext ctx = (RuleContext) data;
        if (packageName != null && !node.isImportOnDemand() && packageName.equals(getPackageName(nameNode.getImage()))) {
            addViolation(ctx, node);
        }

        // special case
        if (packageName == null && getPackageName(nameNode.getImage()).equals("")) {
            addViolation(ctx, node);
        }
        return data;
    }

    private void addViolation(RuleContext ctx, ASTImportDeclaration node) {
        ctx.getReport().addRuleViolation(createRuleViolation(ctx, node.getBeginLine()));
    }

    private String getPackageName(String importName) {
        if (importName.indexOf('.') == -1) {
            return "";
        }
        int lastDot = importName.lastIndexOf('.');
        return importName.substring(0, lastDot);
    }

}
