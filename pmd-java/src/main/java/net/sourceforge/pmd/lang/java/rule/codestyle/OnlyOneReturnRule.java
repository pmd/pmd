/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import java.util.Iterator;
import java.util.List;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;

public class OnlyOneReturnRule extends AbstractJavaRulechainRule {

    public OnlyOneReturnRule() {
        super(ASTMethodDeclaration.class);
    }

    @Override
    public Object visit(ASTMethodDeclaration node, Object data) {
        if (node.getBody() == null) {
            return data;
        }

        node.getBody().descendants(ASTReturnStatement.class)

        List<ASTReturnStatement> returnNodes = node.findDescendantsOfType(ASTReturnStatement.class);

        if (returnNodes.size() > 1) {
            for (Iterator<ASTReturnStatement> i = returnNodes.iterator(); i.hasNext();) {
                Node problem = i.next();
                // skip the last one, it's OK
                if (!i.hasNext()) {
                    continue;
                }
                addViolation(data, problem);
            }
        }
        return data;
    }
}
