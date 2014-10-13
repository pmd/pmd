/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.rule.finalizers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.lang.java.ast.ASTPrimarySuffix;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.symboltable.MethodScope;

public class AvoidCallingFinalizeRule extends AbstractJavaRule {

    private Set<MethodScope> checked = new HashSet<MethodScope>();

    public Object visit(ASTCompilationUnit acu, Object ctx) {
        checked.clear();
        return super.visit(acu, ctx);
    }

    public Object visit(ASTName name, Object ctx) {
        if (name.getImage() == null ||  !name.getImage().endsWith("finalize")) {
            return ctx;
        }
        MethodScope meth = name.getScope().getEnclosingScope(MethodScope.class);
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
        List<ASTPrimarySuffix> primarySuffixes = pp.jjtGetParent().findChildrenOfType(ASTPrimarySuffix.class);
        ASTPrimarySuffix firstSuffix = null;
        if (primarySuffixes.size() > 0) {
            firstSuffix = primarySuffixes.get(0);
        }
        if (firstSuffix == null || firstSuffix.getImage() == null || !firstSuffix.getImage().endsWith("finalize")) {
            return super.visit(pp, ctx);
        }
        MethodScope meth = pp.getScope().getEnclosingScope(MethodScope.class);
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
