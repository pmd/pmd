/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.apex.rule.basic;

import java.util.List;

import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;
import apex.jorje.semantic.ast.member.Parameter;
import apex.jorje.semantic.symbol.member.method.MethodInfo;

public class SimpleRule extends AbstractApexRule {

    public SimpleRule() {
        addRuleChainVisit(ASTMethod.class);
    }

    @Override
    public Object visit(ASTMethod method, Object data) {
        // The visitEnd(...) method is called *after* the visit(...) method if
        // the visit(...) method returns true;
        MethodInfo methodInfo = method.getNode().getMethodInfo();
        List<Parameter> parameters = methodInfo.getParameters();

        // In this example, this is called several times because we have several
        // "hidden" methods that are defined.
        System.out.println(String.format("Saw %d parameters for method: %s",
                parameters.size(), methodInfo.getName()));

        return data;
    }
}
