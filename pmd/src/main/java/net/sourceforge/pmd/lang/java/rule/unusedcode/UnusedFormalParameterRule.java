/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.rule.unusedcode;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameter;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclarator;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTNameList;
import net.sourceforge.pmd.lang.java.ast.ASTType;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.symboltable.JavaNameOccurrence;
import net.sourceforge.pmd.lang.java.symboltable.VariableNameDeclaration;
import net.sourceforge.pmd.lang.rule.properties.BooleanProperty;
import net.sourceforge.pmd.lang.symboltable.NameOccurrence;

public class UnusedFormalParameterRule extends AbstractJavaRule {

    private static final BooleanProperty CHECKALL_DESCRIPTOR = new BooleanProperty("checkAll",
            "Check all methods, including non-private ones", false, 1.0f);

    public UnusedFormalParameterRule() {
        definePropertyDescriptor(CHECKALL_DESCRIPTOR);
    }

    public Object visit(ASTConstructorDeclaration node, Object data) {
        check(node, data);
        return data;
    }

    public Object visit(ASTMethodDeclaration node, Object data) {
        if (!node.isPrivate() && !getProperty(CHECKALL_DESCRIPTOR)) {
            return data;
        }
        if (!node.isNative() && !node.isAbstract() && !isSerializationMethod(node)) {
            check(node, data);
        }
        return data;
    }

    private boolean isSerializationMethod(ASTMethodDeclaration node) {
        ASTMethodDeclarator declarator = node.getFirstDescendantOfType(ASTMethodDeclarator.class);
        List<ASTFormalParameter> parameters = declarator.findDescendantsOfType(ASTFormalParameter.class);
        if (node.isPrivate()
            && "readObject".equals(node.getMethodName())
            && parameters.size() == 1
            && throwsOneException(node, InvalidObjectException.class)) {
            ASTType type = parameters.get(0).getTypeNode();
            if (type.getType() == ObjectInputStream.class
                    || ObjectInputStream.class.getSimpleName().equals(type.getTypeImage())
                    || ObjectInputStream.class.getName().equals(type.getTypeImage())) {
                return true;
            }
        }
        return false;
    }

    private boolean throwsOneException(ASTMethodDeclaration node, Class<? extends Throwable> exception) {
        ASTNameList throwsList = node.getThrows();
        if (throwsList != null && throwsList.jjtGetNumChildren() == 1) {
            ASTName n = (ASTName)throwsList.jjtGetChild(0);
            if (n.getType() == exception
                || exception.getSimpleName().equals(n.getImage())
                || exception.getName().equals(n.getImage())) {
                return true;
            }
        }
        return false;
    }

    private void check(Node node, Object data) {
        Node parent = node.jjtGetParent().jjtGetParent().jjtGetParent();
        if (parent instanceof ASTClassOrInterfaceDeclaration
                && !((ASTClassOrInterfaceDeclaration) parent).isInterface()) {
            Map<VariableNameDeclaration, List<NameOccurrence>> vars = ((JavaNode) node).getScope().getDeclarations(
                    VariableNameDeclaration.class);
            for (Map.Entry<VariableNameDeclaration, List<NameOccurrence>> entry : vars.entrySet()) {
                VariableNameDeclaration nameDecl = entry.getKey();
                if (actuallyUsed(nameDecl, entry.getValue())) {
                    continue;
                }
                addViolation(data, nameDecl.getNode(), new Object[] {
                        node instanceof ASTMethodDeclaration ? "method" : "constructor", nameDecl.getImage() });
            }
        }
    }

    private boolean actuallyUsed(VariableNameDeclaration nameDecl, List<NameOccurrence> usages) {
        for (NameOccurrence occ : usages) {
            JavaNameOccurrence jocc = (JavaNameOccurrence) occ;
            if (jocc.isOnLeftHandSide()) {
                if (nameDecl.isArray() && jocc.getLocation().jjtGetParent().jjtGetParent().jjtGetNumChildren() > 1) {
                    // array element access
                    return true;
                }
                continue;
            } else {
                return true;
            }
        }
        return false;
    }
}
