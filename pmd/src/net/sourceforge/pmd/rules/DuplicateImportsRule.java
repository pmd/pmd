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
import net.sourceforge.pmd.ast.SimpleNode;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class DuplicateImportsRule extends AbstractRule {

    private Set singleTypeImports;
    private Set importOnDemandImports;

    public Object visit(ASTCompilationUnit node, Object data) {
        RuleContext ctx = (RuleContext) data;
        singleTypeImports = new HashSet();
        importOnDemandImports = new HashSet();
        super.visit(node, data);

        // this checks for things like:
        // import java.io.*;
        // import java.io.File;
        for (Iterator i = importOnDemandImports.iterator(); i.hasNext();) {
            ImportWrapper thisImportOnDemand = (ImportWrapper) i.next();
            for (Iterator j = singleTypeImports.iterator(); j.hasNext();) {
                ImportWrapper thisSingleTypeImport = (ImportWrapper) j.next();
                String singleTypePkg = thisSingleTypeImport.getName().substring(0, thisSingleTypeImport.getName().lastIndexOf("."));
                if (thisImportOnDemand.getName().equals(singleTypePkg)) {
                    String msg = MessageFormat.format(getMessage(), new Object[]{thisSingleTypeImport.getName()});
                    ctx.getReport().addRuleViolation(createRuleViolation(ctx, thisSingleTypeImport.getLine(), msg));
                }
            }
        }
        singleTypeImports.clear();
        importOnDemandImports.clear();
        return data;
    }

    public Object visit(ASTImportDeclaration node, Object data) {
        ImportWrapper wrapper = new ImportWrapper(node.getImportedNameNode().getImage(), node.getImportedNameNode().getBeginLine());

        // blahhhh... this really wants to be ASTImportDeclaration to be polymorphic...
        if (node.isImportOnDemand()) {
            if (importOnDemandImports.contains(wrapper)) {
                createRV((RuleContext) data, node.getImportedNameNode());
            } else {
                importOnDemandImports.add(wrapper);
            }
        } else {
            if (singleTypeImports.contains(wrapper)) {
                createRV((RuleContext) data, node.getImportedNameNode());
            } else {
                singleTypeImports.add(wrapper);
            }
        }
        return data;
    }

    private void createRV(RuleContext ctx, SimpleNode importNameNode) {
        String msg = MessageFormat.format(getMessage(), new Object[]{importNameNode.getImage()});
        ctx.getReport().addRuleViolation(createRuleViolation(ctx, importNameNode.getBeginLine(), msg));
    }
}
