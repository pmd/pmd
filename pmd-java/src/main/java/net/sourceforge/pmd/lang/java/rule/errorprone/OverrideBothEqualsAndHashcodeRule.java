/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameter;
import net.sourceforge.pmd.lang.java.ast.ASTImplementsList;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;

public class OverrideBothEqualsAndHashcodeRule extends AbstractJavaRule {

    private boolean implementsComparable = false;

    private boolean containsEquals = false;

    private boolean containsHashCode = false;

    private Node nodeFound = null;

    @Override
    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        if (node.isInterface()) {
            return data;
        }
        super.visit(node, data);
        if (!implementsComparable && containsEquals ^ containsHashCode) {
            if (nodeFound == null) {
                nodeFound = node;
            }
            addViolation(data, nodeFound);
        }
        implementsComparable = false;
        containsEquals = false;
        containsHashCode = false;
        nodeFound = null;
        return data;
    }

    @Override
    public Object visit(ASTImplementsList node, Object data) {
        implementsComparable = node.children().filter(child -> TypeTestUtil.isA(Comparable.class, child)).nonEmpty();
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTMethodDeclaration node, Object data) {
        if (implementsComparable) {
            return data;
        }

        int formalParamsCount = node.getFormalParameters().size();
        ASTFormalParameter formalParam = null;
        if (formalParamsCount > 0) {
            formalParam = node.getFormalParameters().get(0);
        }

        if (formalParamsCount == 0 && "hashCode".equals(node.getName())) {
            containsHashCode = true;
            nodeFound = node;
        } else if (formalParamsCount == 1 && "equals".equals(node.getName())
                && TypeTestUtil.isExactlyA(Object.class, formalParam)) {
            containsEquals = true;
            nodeFound = node;
        }
        return super.visit(node, data);
    }
}
