package net.sourceforge.pmd.rules.design;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.ast.ASTMethodDeclaration;

public class UseSingletonRule extends AbstractRule
{
    private boolean isOK;
    private int methodCount;

    public Object visit(ASTConstructorDeclaration decl, Object data) {
        if (decl.isPrivate()) {
            isOK = true;
        }
        return data;
    }

    public Object visit( ASTMethodDeclaration decl, Object data ) {
        methodCount ++;

        if (!isOK && !decl.isStatic()) {
            isOK = true;
        }

        return data;
    }

    public Object visit( ASTCompilationUnit cu, Object data ) {
        methodCount = 0;
        isOK=false;
        Object RC = cu.childrenAccept( this, data );

        if (!isOK && methodCount > 0) {
            RuleContext ctx = (RuleContext)data;
            ctx.getReport().addRuleViolation(createRuleViolation(ctx, cu.getBeginLine() ));
        }

        return RC;
    }
}
