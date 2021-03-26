/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.performance;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr.ASTNamedReferenceExpr;
import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr.AccessType;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTForStatement;
import net.sourceforge.pmd.lang.java.ast.ASTForeachStatement;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameter;
import net.sourceforge.pmd.lang.java.ast.ASTLoopStatement;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;

public class UseStringBufferForStringAppendsRule extends AbstractJavaRulechainRule {

    public UseStringBufferForStringAppendsRule() {
        super(ASTVariableDeclaratorId.class);
    }

    /**
     * This method is used to check whether user appends string directly instead of using StringBuffer or StringBuilder
     * @param node This is the expression of part of java code to be checked.
     * @param data This is the data to return.
     * @return Object This returns the data passed in. If violation happens, violation is added to data.
     */
    @Override
    public Object visit(ASTVariableDeclaratorId node, Object data) {
        if (!TypeTestUtil.isA(String.class, node) || node.hasArrayType()
                || node.getNthParent(3) instanceof ASTForStatement
                || node.getNthParent(3) instanceof ASTForeachStatement) {
            return data;
        }

        // Remember how often the variable has been used
        int usageCounter = 0;

        for (ASTNamedReferenceExpr usage : node.getLocalUsages()) {
            if ((node.getNthParent(2) instanceof ASTFieldDeclaration
                    || node.getParent() instanceof ASTFormalParameter)
                    && isNotWithinLoop(usage)) {
                // ignore if the field or formal parameter is *not* used within loops
                continue;
            }

            if (usage.getAccessType() == AccessType.WRITE) {
                if (isWithinLoop(usage)) {
                    // always report within a loop
                    addViolation(data, usage);
                } else {
                    usageCounter++;
                    if (usageCounter > 1) {
                        // only report, if it is not the first time
                        addViolation(data, usage);
                    }
                }
            }
        }
        return data;
    }

    private boolean isNotWithinLoop(Node name) {
        return name.ancestors(ASTLoopStatement.class).isEmpty();
    }

    private boolean isWithinLoop(Node name) {
        return !isNotWithinLoop(name);
    }
}
