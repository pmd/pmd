/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.lang.java.ast.ASTPrimarySuffix;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.symboltable.MethodScope;
import net.sourceforge.pmd.lang.symboltable.ScopedNode;

public class AvoidCallingFinalizeRule extends AbstractJavaRule {

    private Set<MethodScope> checked = new HashSet<>();

    @Override
    public Object visit(ASTCompilationUnit acu, Object ctx) {
        checked.clear();
        return super.visit(acu, ctx);
    }

    @Override
    public Object visit(ASTName name, Object ctx) {
        if (name.getImage() == null || !name.getImage().endsWith("finalize")) {
            return ctx;
        }
        if (!checkForViolation(name)) {
            return ctx;
        }
        addViolation(ctx, name);
        return ctx;
    }

    @Override
    public Object visit(ASTPrimaryPrefix pp, Object ctx) {
        List<ASTPrimarySuffix> primarySuffixes = pp.getParent().findChildrenOfType(ASTPrimarySuffix.class);
        ASTPrimarySuffix firstSuffix = null;
        if (!primarySuffixes.isEmpty()) {
            firstSuffix = primarySuffixes.get(0);
        }
        if (firstSuffix == null || firstSuffix.getImage() == null || !firstSuffix.getImage().endsWith("finalize")) {
            return super.visit(pp, ctx);
        }
        if (!checkForViolation(pp)) {
            return super.visit(pp, ctx);
        }
        addViolation(ctx, pp);
        return super.visit(pp, ctx);
    }

    private boolean checkForViolation(ScopedNode node) {
        MethodScope meth = node.getScope().getEnclosingScope(MethodScope.class);
        if (meth != null && "finalize".equals(meth.getName())) {
            return false;
        }
        if (meth != null && checked.contains(meth)) {
            return false;
        }
        if (meth != null) {
            checked.add(meth);
        }
        return true;
    }
}
