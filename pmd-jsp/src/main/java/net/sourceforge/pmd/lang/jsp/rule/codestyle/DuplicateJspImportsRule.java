/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.jsp.rule.codestyle;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.jsp.ast.ASTJspDirectiveAttribute;
import net.sourceforge.pmd.lang.jsp.rule.AbstractJspRule;
import net.sourceforge.pmd.lang.rule.ImportWrapper;

public class DuplicateJspImportsRule extends AbstractJspRule {

    private Set<ImportWrapper> imports = new HashSet<>();

    @Override
    public void apply(List<? extends Node> nodes, RuleContext ctx) {
        /*
         * TODO: This method is a hack! It's overriding the parent's method
         * because the JSP parsing doesn't seem to hit ASTCompilationUnit
         * properly
         */
        imports.clear();
        super.apply(nodes, ctx);
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
            ImportWrapper wrapper = new ImportWrapper(token, token, node);
            if (imports.contains(wrapper)) {
                addViolation(data, node, node.getImage());
            } else {
                imports.add(wrapper);
            }
        }
        return super.visit(node, data);
    }

}
