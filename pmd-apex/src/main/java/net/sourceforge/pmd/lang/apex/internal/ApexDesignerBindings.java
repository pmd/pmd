/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.internal;

import net.sourceforge.pmd.lang.apex.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.ast.ASTMethodCallExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClass;
import net.sourceforge.pmd.lang.apex.ast.ASTVariableDeclaration;
import net.sourceforge.pmd.lang.apex.ast.ApexNode;
import net.sourceforge.pmd.lang.apex.ast.ApexVisitorBase;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.xpath.Attribute;
import net.sourceforge.pmd.util.designerbindings.DesignerBindings.DefaultDesignerBindings;

public class ApexDesignerBindings extends DefaultDesignerBindings {

    public static final ApexDesignerBindings INSTANCE = new ApexDesignerBindings();

    @Override
    public Attribute getMainAttribute(Node node) {
        if (node instanceof ApexNode) {
            Attribute attr = (Attribute) node.acceptVisitor(MainAttrVisitor.INSTANCE, null);
            if (attr != null) {
                return attr;
            }
        }

        return super.getMainAttribute(node);
    }

    @Override
    public TreeIconId getIcon(Node node) {
        if (node instanceof ASTFieldDeclaration) {
            return TreeIconId.FIELD;
        } else if (node instanceof ASTUserClass) {
            return TreeIconId.CLASS;
        } else if (node instanceof ASTMethod) {
            return TreeIconId.METHOD;
        } else if (node instanceof ASTVariableDeclaration) {
            return TreeIconId.VARIABLE;
        }
        return super.getIcon(node);
    }


    private static final class MainAttrVisitor extends ApexVisitorBase<Object, Object> {

        private static final MainAttrVisitor INSTANCE = new MainAttrVisitor();

        @Override
        public Object visitApexNode(ApexNode<?> node, Object data) {
            return null; // don't recurse
        }

        @Override
        public Object visit(ASTMethodCallExpression node, Object data) {
            return new Attribute(node, "MethodName", node.getMethodName());
        }
    }

}
