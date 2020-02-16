/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.dfa;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.lang.dfa.DFAGraphMethod;
import net.sourceforge.pmd.lang.dfa.DFAGraphRule;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

/**
 * @deprecated Only used by the deprecated designer
 */
@Deprecated
public class JavaDFAGraphRule extends AbstractJavaRule implements DFAGraphRule {

    private List<DFAGraphMethod> methods;

    public JavaDFAGraphRule() {
        super.setDfa(true);
    }

    @Override
    public List<DFAGraphMethod> getMethods() {
        return this.methods;
    }

    @Override
    public Object visit(ASTMethodDeclaration node, Object data) {
        methods.add(node);
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTCompilationUnit acu, Object data) {
        methods = new ArrayList<>();
        return super.visit(acu, data);
    }
}
