package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.ASTName;
import net.sourceforge.pmd.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.symboltable.MethodScope;

import java.util.HashSet;
import java.util.Set;

public class AvoidCallingFinalize extends AbstractRule {

    private Set checked = new HashSet();

    public Object visit(ASTCompilationUnit acu, Object ctx) {
        checked.clear();
        return super.visit(acu, ctx);
    }

    public Object visit(ASTName name, Object ctx) {
        if (name.getImage() == null ||  !name.getImage().endsWith("finalize")) {
            return ctx;
        }
        MethodScope meth = name.getScope().getEnclosingMethodScope();
        if (meth.getName().equals("finalize")) {
            return ctx;
        }
        if (checked.contains(meth)) {
            return ctx;
        }
        checked.add(meth);
        addViolation(ctx, name);
        return ctx;
    }

    public Object visit(ASTPrimaryPrefix pp, Object ctx) {
        if (pp.getImage() == null || !pp.getImage().endsWith("finalize")) {
            return super.visit(pp, ctx);
        }
        MethodScope meth = pp.getScope().getEnclosingMethodScope();
        if (meth.getName().equals("finalize")) {
            return super.visit(pp, ctx);
        }
        if (checked.contains(meth)) {
            return super.visit(pp, ctx);
        }
        checked.add(meth);
        addViolation(ctx, pp);
        return super.visit(pp, ctx);
    }
}
