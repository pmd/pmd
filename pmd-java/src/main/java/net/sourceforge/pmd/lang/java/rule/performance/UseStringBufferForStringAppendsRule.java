/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.performance;

import java.util.Objects;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTArgumentList;
import net.sourceforge.pmd.lang.java.ast.ASTAssignmentOperator;
import net.sourceforge.pmd.lang.java.ast.ASTConditionalExpression;
import net.sourceforge.pmd.lang.java.ast.ASTDoStatement;
import net.sourceforge.pmd.lang.java.ast.ASTEqualityExpression;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTForStatement;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameter;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTStatementExpression;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.ast.ASTWhileStatement;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;
import net.sourceforge.pmd.lang.symboltable.NameOccurrence;

public class UseStringBufferForStringAppendsRule extends AbstractJavaRule {

    public UseStringBufferForStringAppendsRule() {
        addRuleChainVisit(ASTVariableDeclaratorId.class);
    }

    /**
     * This method is used to check whether user appends string directly instead of using StringBuffer or StringBuilder
     * @param node This is the expression of part of java code to be checked.
     * @param data This is the data to return.
     * @return Object This returns the data passed in. If violation happens, violation is added to data.
     */
    @Override
    public Object visit(ASTVariableDeclaratorId node, Object data) {
        if (!TypeTestUtil.isA(String.class, node) || node.isArray()
                || node.getNthParent(3) instanceof ASTForStatement) {
            return data;
        }

        // Remember how often we the variable has been used
        int usageCounter = 0;

        for (NameOccurrence no : node.getUsages()) {
            Node name = no.getLocation();
            ASTStatementExpression statement = name.getFirstParentOfType(ASTStatementExpression.class);
            if (statement == null) {
                continue;
            }
            ASTArgumentList argList = name.getFirstParentOfType(ASTArgumentList.class);
            if (argList != null && argList.getFirstParentOfType(ASTStatementExpression.class) == statement) {
                // used in method call
                continue;
            }
            ASTEqualityExpression equality = name.getFirstParentOfType(ASTEqualityExpression.class);
            if (equality != null && equality.getFirstParentOfType(ASTStatementExpression.class) == statement) {
                // used in condition
                continue;
            }
            if ((node.getNthParent(2) instanceof ASTFieldDeclaration
                    || node.getParent() instanceof ASTFormalParameter)
                    && isNotWithinLoop(name)) {
                // ignore if the field or formal parameter is *not* used within loops
                continue;
            }
            ASTConditionalExpression conditional = name.getFirstParentOfType(ASTConditionalExpression.class);

            if (conditional != null) {
                Node thirdParent = name.getNthParent(3);
                Node fourthParent = name.getNthParent(4);
                if ((Objects.equals(thirdParent, conditional) || Objects.equals(fourthParent, conditional))
                        && conditional.getFirstParentOfType(ASTStatementExpression.class) == statement) {
                    // is used in ternary as only option (not appended to other
                    // string)
                    continue;
                }
            }
            if (statement.getNumChildren() > 0 && statement.getChild(0) instanceof ASTPrimaryExpression) {
                ASTName astName = statement.getChild(0).getFirstDescendantOfType(ASTName.class);
                if (astName != null) {
                    ASTAssignmentOperator assignmentOperator = statement
                            .getFirstDescendantOfType(ASTAssignmentOperator.class);
                    if (astName.equals(name)) {
                        if (assignmentOperator != null && assignmentOperator.isCompound()) {
                            if (isWithinLoop(name)) {
                                // always report within a loop
                                addViolation(data, assignmentOperator);
                            } else {
                                usageCounter++;
                                if (usageCounter > 1) {
                                    // only report, if it is not the first time
                                    addViolation(data, assignmentOperator);
                                }
                            }
                        }
                    } else if (astName.hasImageEqualTo(name.getImage())) {
                        if (assignmentOperator != null && !assignmentOperator.isCompound()) {
                            if (isWithinLoop(name)) {
                                // always report within a loop
                                addViolation(data, assignmentOperator);
                            } else {
                                usageCounter++;
                                if (usageCounter > 1) {
                                    // only report, if it is not the first time
                                    addViolation(data, assignmentOperator);
                                }
                            }
                        } else if (assignmentOperator != null && assignmentOperator.isCompound()
                                && usageCounter >= 1) {
                            addViolation(data, assignmentOperator);
                        }
                    }
                }
            }
        }
        return data;
    }

    private boolean isNotWithinLoop(Node name) {
        return name.getFirstParentOfType(ASTForStatement.class) == null
                && name.getFirstParentOfType(ASTWhileStatement.class) == null
                && name.getFirstParentOfType(ASTDoStatement.class) == null;
    }

    private boolean isWithinLoop(Node name) {
        return !isNotWithinLoop(name);
    }
}
