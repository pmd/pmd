/*
 * User: tom
 * Date: Jul 12, 2002
 * Time: 10:39:13 PM
 */
package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.ASTImportDeclaration;
import net.sourceforge.pmd.ast.ASTName;
import net.sourceforge.pmd.ast.SimpleNode;

import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import java.text.MessageFormat;

public class DuplicateImportsRule extends AbstractRule {

    private Set singleTypeImports;
    private Set importOnDemandImports;

    public Object visit(ASTCompilationUnit node, Object data) {
        RuleContext ctx = (RuleContext)data;
        singleTypeImports = new HashSet();
        importOnDemandImports = new HashSet();
        super.visit(node,data);

        // check for things like:
        // import java.io.*;
        // import java.io.File;
        // TODO twiddle with this to somehow get a real line number for the single type import
        for (Iterator i = this.importOnDemandImports.iterator(); i.hasNext();) {
            String thisImportOnDemand = (String)i.next();
            for (Iterator j = this.singleTypeImports.iterator(); j.hasNext();) {
                String thisSingleTypeImport = (String)j.next();
                String singleTypePkg = thisSingleTypeImport.substring(0, thisSingleTypeImport.lastIndexOf("."));
                if (thisImportOnDemand.equals(singleTypePkg)) {
                    String msg = MessageFormat.format(getMessage(), new Object[] {thisSingleTypeImport});
                    ctx.getReport().addRuleViolation(createRuleViolation(ctx, 0, msg));
                }
            }
        }
        singleTypeImports = new HashSet();
        importOnDemandImports = new HashSet();
        return data;
    }

    public Object visit(ASTImportDeclaration node, Object data) {
        SimpleNode importNameNode = (SimpleNode)node.jjtGetChild(0);

        // blahhhh... this really wants to be ASTImportDeclaration to be polymorphic...
        if (node.isImportOnDemand()) {
            if (importOnDemandImports.contains(importNameNode.getImage())) {
                createRV((RuleContext)data, importNameNode);
            } else {
                importOnDemandImports.add(importNameNode.getImage());
            }
        } else {
            if (singleTypeImports.contains(importNameNode.getImage())) {
                createRV((RuleContext)data, importNameNode);
            } else {
                singleTypeImports.add(importNameNode.getImage());
            }
        }
        return data;
    }

    private void createRV(RuleContext ctx, SimpleNode importNameNode) {
        String msg = MessageFormat.format(getMessage(), new Object[] {importNameNode.getImage()});
        ctx.getReport().addRuleViolation(createRuleViolation(ctx, importNameNode.getBeginLine(), msg));
    }
}
