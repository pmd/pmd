/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.rule.codestyle;

import java.util.List;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.plsql.ast.ASTBulkCollectIntoClause;
import net.sourceforge.pmd.lang.plsql.ast.ASTDatatype;
import net.sourceforge.pmd.lang.plsql.ast.ASTEqualityExpression;
import net.sourceforge.pmd.lang.plsql.ast.ASTFormalParameter;
import net.sourceforge.pmd.lang.plsql.ast.ASTFormalParameters;
import net.sourceforge.pmd.lang.plsql.ast.ASTFromClause;
import net.sourceforge.pmd.lang.plsql.ast.ASTJoinClause;
import net.sourceforge.pmd.lang.plsql.ast.ASTSelectList;
import net.sourceforge.pmd.lang.plsql.ast.ASTSubqueryOperation;
import net.sourceforge.pmd.lang.plsql.rule.AbstractPLSQLRule;

public class CodeFormatRule extends AbstractPLSQLRule {

    private static final int DEFAULT_INDENT = 2;

    @Override
    public Object visit(ASTSelectList node, Object data) {
        Node parent = node.jjtGetParent();
        checkEachChildOnNextLine(data, node, parent.getBeginLine(), parent.getBeginColumn() + 7);
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTBulkCollectIntoClause node, Object data) {
        Node parent = node.jjtGetParent();
        checkIndentation(data, node, parent.getBeginColumn() + DEFAULT_INDENT, "BULK COLLECT INTO");
        checkEachChildOnNextLine(data, node, node.getBeginLine(), parent.getBeginColumn() + 7);
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTFromClause node, Object data) {
        checkIndentation(data, node, node.jjtGetParent().getBeginColumn() + DEFAULT_INDENT, "FROM");
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTJoinClause node, Object data) {
        // first child is the table reference
        Node tableReference = node.jjtGetChild(0);

        // remaining children are joins
        int lineNumber = tableReference.getBeginLine();
        for (int i = 1; i < node.jjtGetNumChildren(); i++) {
            lineNumber++;
            Node child = node.jjtGetChild(i);
            if (child.getBeginLine() != lineNumber) {
                addViolationWithMessage(data, child, child.getXPathNodeName() + " should be on line " + lineNumber);
            }
            List<ASTEqualityExpression> conditions = child.findDescendantsOfType(ASTEqualityExpression.class);

            if (conditions.size() == 1) {
                // one condition should be on the same line
                ASTEqualityExpression singleCondition = conditions.get(0);
                if (singleCondition.getBeginLine() != lineNumber) {
                    addViolationWithMessage(data, child,
                            "Join condition \"" + singleCondition.getImage() + "\" should be on line " + lineNumber);
                }
            } else {
                // each condition on a separate line
                for (ASTEqualityExpression singleCondition : conditions) {
                    lineNumber++;
                    if (singleCondition.getBeginLine() != lineNumber) {
                        addViolationWithMessage(data, child,
                                "Join condition \"" + singleCondition.getImage() + "\" should be on line "
                                        + lineNumber);
                    }
                }
            }
        }
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTSubqueryOperation node, Object data) {
        // get previous sibling
        int thisIndex = node.jjtGetChildIndex();
        Node prevSibling = node.jjtGetParent().jjtGetChild(thisIndex - 1);

        checkIndentation(data, node, prevSibling.getBeginColumn(), node.getImage());

        // it should also be on the next line
        if (node.getBeginLine() != prevSibling.getEndLine() + 1) {
            addViolationWithMessage(data, node,
                    node.getImage() + " should be on line " + (prevSibling.getEndLine() + 1));
        }

        return super.visit(node, data);
    }

    private int checkEachChildOnNextLine(Object data, Node parent, int firstLine, int indentation) {
        int currentLine = firstLine;
        for (int i = 0; i < parent.jjtGetNumChildren(); i++) {
            Node child = parent.jjtGetChild(i);
            if (child.getBeginLine() != currentLine) {
                addViolationWithMessage(data, child, child.getImage() + " should be on line " + currentLine);
            } else if (i > 0 && child.getBeginColumn() != indentation) {
                addViolationWithMessage(data, child, child.getImage() + " should begin at column " + indentation);
            }
            // next entry needs to be on the next line
            currentLine++;
        }
        return currentLine;
    }

    private void checkIndentation(Object data, Node node, int indentation, String name) {
        if (node.getBeginColumn() != indentation) {
            addViolationWithMessage(data, node, name + " should begin at column " + indentation);
        }
    }

    @Override
    public Object visit(ASTFormalParameters node, Object data) {
        int indentation = node.jjtGetParent().getBeginColumn() + DEFAULT_INDENT;
        checkEachChildOnNextLine(data, node, node.getBeginLine() + 1, indentation);

        // check the data type alignment
        List<ASTFormalParameter> parameters = node.findChildrenOfType(ASTFormalParameter.class);
        if (parameters.size() > 1) {
            ASTDatatype first = parameters.get(0).getFirstChildOfType(ASTDatatype.class);
            for (int i = 1; first != null && i < parameters.size(); i++) {
                ASTDatatype nextType = parameters.get(i).getFirstChildOfType(ASTDatatype.class);
                if (nextType != null) {
                    checkIndentation(data, nextType, first.getBeginColumn(), "Type " + nextType.getImage());
                }
            }
        }
        return super.visit(node, data);
    }
}
