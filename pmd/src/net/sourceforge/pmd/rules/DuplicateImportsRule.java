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

import java.util.Set;
import java.util.HashSet;
import java.text.MessageFormat;

public class DuplicateImportsRule extends AbstractRule {

    private Set allImports = new HashSet();
    private boolean inImportCtx;

    public Object visit(ASTImportDeclaration node, Object data) {
        inImportCtx = true;
        super.visit(node,data);
        inImportCtx = false;
        return data;
    }

    public Object visit(ASTName node, Object data) {
        if (inImportCtx) {
            if (allImports.contains(node.getImage())) {
                RuleContext ctx = (RuleContext)data;
                String msg = MessageFormat.format(getMessage(), new Object[] {node.getImage()});
                ctx.getReport().addRuleViolation(createRuleViolation(ctx, node.getBeginLine(), msg));
            } else {
                // TODO - look for:
                // java.lang.ref.*
                // java.lang.ref.WeakReference
                //
                allImports.add(node.getImage());
            }
        }
        return data;
    }
}
