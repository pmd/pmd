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
import java.text.MessageFormat;

public class DuplicateImportsRule extends AbstractRule {

    private Set singleTypeImports;
    private Set importOnDemandImports;

    public Object visit(ASTCompilationUnit node, Object data) {
        singleTypeImports = new HashSet();
        importOnDemandImports = new HashSet();
        super.visit(node,data);
        singleTypeImports = new HashSet();
        importOnDemandImports = new HashSet();
        return data;
    }

    // TODO - look for:
    // java.lang.ref.*
    // java.lang.ref.WeakReference
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
