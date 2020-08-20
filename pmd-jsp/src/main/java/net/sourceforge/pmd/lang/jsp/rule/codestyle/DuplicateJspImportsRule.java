/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.jsp.rule.codestyle;

import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.jsp.ast.ASTJspDirectiveAttribute;
import net.sourceforge.pmd.lang.jsp.rule.AbstractJspRule;

public class DuplicateJspImportsRule extends AbstractJspRule {

    private Set<String> imports = new HashSet<>();

    @Override
    public void start(RuleContext ctx) {
        imports.clear();
    }

    @Override
    public Object visit(ASTJspDirectiveAttribute node, Object data) {

        if (!"import".equals(node.getName())) {
            return super.visit(node, data);
        }
        String values = node.getValue();
        StringTokenizer st = new StringTokenizer(values, ",");
        int count = st.countTokens();
        for (int ix = 0; ix < count; ix++) {
            String token = st.nextToken();
            if (imports.contains(token)) {
                addViolation(data, node, node.getImage());
            } else {
                imports.add(token);
            }
        }
        return super.visit(node, data);
    }

}
