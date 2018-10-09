/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.rule.codestyle;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.plsql.ast.ASTBulkCollectIntoClause;
import net.sourceforge.pmd.lang.plsql.ast.ASTFromClause;
import net.sourceforge.pmd.lang.plsql.ast.ASTSelectList;
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
}
