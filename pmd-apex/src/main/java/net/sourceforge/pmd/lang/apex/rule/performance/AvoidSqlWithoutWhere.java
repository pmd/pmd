package net.sourceforge.pmd.lang.apex.rule.performance;

import net.sourceforge.pmd.lang.apex.ast.ASTSoqlExpression;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;

public class AvoidSQLWithoutWhere extends AbstractApexRule{
	@Override
    public Object visit(ASTSoqlExpression node, Object data) {
        String query=node.getQuery().toLowerCase();
        if (query.contains("select")&&!query.contains("where")){
            addViolation(data, node);
        }

        // this calls back to the default implementation, which recurses further down the subtree
        return super.visit(node, data);
    }
}
