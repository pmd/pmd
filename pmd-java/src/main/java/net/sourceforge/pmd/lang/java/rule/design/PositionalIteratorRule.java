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

    @Override
    public Object visit(ASTWhileStatement node, Object data) {
        if (hasNameAsChild(node.getChild(0))) {
            String exprName = getName(node.getChild(0));
            if (exprName.indexOf(".hasNext") != -1 && node.getNumChildren() > 1) {

                Node loopBody = node.getChild(1);
                List<String> names = new ArrayList<>();
                collectNames(getVariableName(exprName), names, loopBody);
                int nextCount = 0;
                for (String name : names) {
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
        for (int i = 0; i < node.getNumChildren(); i++) {
            Node child = node.getChild(i);
            if (child.getNumChildren() > 0) {
                collectNames(target, names, child);
            } else {
                if (child instanceof ASTName && isQualifiedName(child)
                        && target.equals(getVariableName(child.getImage()))) {
                    names.add(child.getImage());
                }
            }
        }
    }

    private boolean hasNameAsChild(Node node) {
        if (node.getNumChildren() > 0) {
            if (node.getChild(0) instanceof ASTName) {
                return true;
            } else {
                return hasNameAsChild(node.getChild(0));
            }
        }
        return false;
    }

    private String getName(Node node) {
        if (node.getNumChildren() > 0) {
            if (node.getChild(0) instanceof ASTName) {
                return ((ASTName) node.getChild(0)).getImage();
            } else {
                return getName(node.getChild(0));
            }
        }
        throw new IllegalArgumentException("Check with hasNameAsChild() first!");
    }
}
