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

    private Set allImports;

    public Object visit(ASTCompilationUnit node, Object data) {
        allImports = new HashSet();
        super.visit(node,data);
        allImports = new HashSet();
        return data;
    }

    public Object visit(ASTImportDeclaration node, Object data) {
        SimpleNode importNameNode = (SimpleNode)node.jjtGetChild(0);
        if (allImports.contains(importNameNode.getImage())) {
            RuleContext ctx = (RuleContext)data;
            String msg = MessageFormat.format(getMessage(), new Object[] {importNameNode.getImage()});
            ctx.getReport().addRuleViolation(createRuleViolation(ctx, importNameNode.getBeginLine(), msg));
        } else {
            // TODO - look for:
            // java.lang.ref.*
            // java.lang.ref.WeakReference
            //
            allImports.add(importNameNode.getImage());
        }
        return data;
    }
}
