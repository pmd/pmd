/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.rule.design;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTWhileStatement;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

public class PositionalIteratorRule extends AbstractJavaRule {

    public Object visit(ASTWhileStatement node, Object data) {
        if (hasNameAsChild(node.jjtGetChild(0))) {
            String exprName = getName(node.jjtGetChild(0));
            if (exprName.indexOf(".hasNext") != -1 && node.jjtGetNumChildren() > 1) {

        	Node loopBody = node.jjtGetChild(1);
                List<String> names = new ArrayList<String>();
                collectNames(getVariableName(exprName), names, loopBody);
                int nextCount = 0;
                for (String name: names) {
                    if (name.indexOf(".next") != -1) {
                        nextCount++;
                    }
                }

                if (nextCount > 1) {
                    addViolation(data, node);
                }

            }
        }
        return null;
    }

    private String getVariableName(String exprName) {
        return exprName.substring(0, exprName.indexOf('.'));
    }

    private void collectNames(String target, List<String> names, Node node) {
        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            Node child = node.jjtGetChild(i);
            if (child.jjtGetNumChildren() > 0) {
                collectNames(target, names, child);
            } else {
                if (child instanceof ASTName && isQualifiedName(child) && target.equals(getVariableName(child.getImage()))) {
                    names.add(child.getImage());
                }
            }
        }
    }

    private boolean hasNameAsChild(Node node) {
        if (node.jjtGetNumChildren() > 0) {
            if (node.jjtGetChild(0) instanceof ASTName) {
                return true;
            } else {
        	return hasNameAsChild(node.jjtGetChild(0));
            }
        }
        return false;
    }

    private String getName(Node node) {
        if (node.jjtGetNumChildren() > 0) {
            if (node.jjtGetChild(0) instanceof ASTName) {
                return ((ASTName) node.jjtGetChild(0)).getImage();
            } else {
        	return getName(node.jjtGetChild(0));
            }
        }
        throw new IllegalArgumentException("Check with hasNameAsChild() first!");
    }
}
