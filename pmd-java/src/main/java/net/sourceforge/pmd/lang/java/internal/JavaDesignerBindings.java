/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.internal;

import java.util.Collection;
import java.util.Collections;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.xpath.Attribute;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTRecordConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.ast.JavaParserVisitor;
import net.sourceforge.pmd.lang.java.ast.JavaParserVisitorReducedAdapter;
import net.sourceforge.pmd.lang.java.ast.TypeNode;
import net.sourceforge.pmd.util.designerbindings.DesignerBindings.DefaultDesignerBindings;

public final class JavaDesignerBindings extends DefaultDesignerBindings {

    public static final JavaDesignerBindings INSTANCE = new JavaDesignerBindings();

    private JavaDesignerBindings() {

    }

    @Override
    public Attribute getMainAttribute(Node node) {
        if (node instanceof JavaNode) {
            Attribute attr = (Attribute) ((JavaNode) node).jjtAccept(MainAttrVisitor.INSTANCE, null);
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
        } else if (node instanceof ASTAnyTypeDeclaration) {
            return TreeIconId.CLASS;
        } else if (node instanceof ASTMethodDeclaration) {
            return TreeIconId.METHOD;
        } else if (node instanceof ASTConstructorDeclaration
            || node instanceof ASTRecordConstructorDeclaration) {
            return TreeIconId.CONSTRUCTOR;
        } else if (node instanceof ASTVariableDeclaratorId) {
            return TreeIconId.VARIABLE;
        }
        return super.getIcon(node);
    }

    @Override
    public Collection<AdditionalInfo> getAdditionalInfo(Node node) {
        if (node instanceof TypeNode) {
            Class<?> type = ((TypeNode) node).getType();
            if (type != null) {
                return Collections.singletonList(new AdditionalInfo("Type: " + type));
            }
        }
        return super.getAdditionalInfo(node);
    }

    private static final class MainAttrVisitor extends JavaParserVisitorReducedAdapter {

        private static final JavaParserVisitor INSTANCE = new MainAttrVisitor();

        @Override
        public Object visit(JavaNode node, Object data) {
            return null; // don't recurse
        }

        @Override
        public Object visit(ASTAnyTypeDeclaration node, Object data) {
            return new Attribute(node, "SimpleName", node.getSimpleName());
        }

        @Override
        public Object visit(ASTMethodDeclaration node, Object data) {
            return new Attribute(node, "Name", node.getName());
        }
    }
}
