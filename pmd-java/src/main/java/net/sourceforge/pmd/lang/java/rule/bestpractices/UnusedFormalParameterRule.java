/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import static net.sourceforge.pmd.properties.PropertyFactory.booleanProperty;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameter;
import net.sourceforge.pmd.lang.java.ast.ASTMarkerAnnotation;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclarator;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTNameList;
import net.sourceforge.pmd.lang.java.ast.ASTType;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.symboltable.JavaNameOccurrence;
import net.sourceforge.pmd.lang.java.symboltable.VariableNameDeclaration;
import net.sourceforge.pmd.lang.symboltable.NameOccurrence;
import net.sourceforge.pmd.properties.PropertyDescriptor;


public class UnusedFormalParameterRule extends AbstractJavaRule {

    private static final PropertyDescriptor<Boolean> CHECKALL_DESCRIPTOR = booleanProperty("checkAll").desc("Check all methods, including non-private ones").defaultValue(false).build();

    public UnusedFormalParameterRule() {
        definePropertyDescriptor(CHECKALL_DESCRIPTOR);
    }

    @Override
    public Object visit(ASTConstructorDeclaration node, Object data) {
        check(node, data);
        return data;
    }

    @Override
    public Object visit(ASTMethodDeclaration node, Object data) {
        if (!node.isPrivate() && !getProperty(CHECKALL_DESCRIPTOR)) {
            return data;
        }
        if (!node.isNative() && !node.isAbstract() && !isSerializationMethod(node) && !hasOverrideAnnotation(node)) {
            check(node, data);
        }
        return data;
    }

    private boolean isSerializationMethod(ASTMethodDeclaration node) {
        ASTMethodDeclarator declarator = node.getFirstDescendantOfType(ASTMethodDeclarator.class);
        List<ASTFormalParameter> parameters = declarator.findDescendantsOfType(ASTFormalParameter.class);
        if (node.isPrivate() && "readObject".equals(node.getName()) && parameters.size() == 1
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
        if (throwsList != null && throwsList.getNumChildren() == 1) {
            ASTName n = (ASTName) throwsList.getChild(0);
            if (n.getType() == exception || exception.getSimpleName().equals(n.getImage())
                    || exception.getName().equals(n.getImage())) {
                return true;
            }
        }
        return false;
    }

    private void check(Node node, Object data) {
        Node parent = node.getParent().getParent().getParent();
        if (parent instanceof ASTClassOrInterfaceDeclaration
                && !((ASTClassOrInterfaceDeclaration) parent).isInterface()) {
            Map<VariableNameDeclaration, List<NameOccurrence>> vars = ((JavaNode) node).getScope()
                    .getDeclarations(VariableNameDeclaration.class);
            for (Map.Entry<VariableNameDeclaration, List<NameOccurrence>> entry : vars.entrySet()) {
                VariableNameDeclaration nameDecl = entry.getKey();

                ASTVariableDeclaratorId declNode = nameDecl.getDeclaratorId();
                if (!declNode.isFormalParameter() || declNode.isExplicitReceiverParameter()) {
                    continue;
                }

                if (actuallyUsed(nameDecl, entry.getValue())) {
                    continue;
                }
                addViolation(data, nameDecl.getNode(), new Object[] {
                    node instanceof ASTMethodDeclaration ? "method" : "constructor", nameDecl.getImage(), });
            }
        }
    }

    private boolean actuallyUsed(VariableNameDeclaration nameDecl, List<NameOccurrence> usages) {
        for (NameOccurrence occ : usages) {
            JavaNameOccurrence jocc = (JavaNameOccurrence) occ;
            if (jocc.isOnLeftHandSide()) {
                if (nameDecl.isArray() && jocc.getLocation().getParent().getParent().getNumChildren() > 1) {
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

    private boolean hasOverrideAnnotation(ASTMethodDeclaration node) {
        int childIndex = node.getIndexInParent();
        for (int i = 0; i < childIndex; i++) {
            Node previousSibling = node.getParent().getChild(i);
            List<ASTMarkerAnnotation> annotations = previousSibling.findDescendantsOfType(ASTMarkerAnnotation.class);
            for (ASTMarkerAnnotation annotation : annotations) {
                ASTName name = annotation.getFirstChildOfType(ASTName.class);
                if (name != null && (name.hasImageEqualTo("Override") || name.hasImageEqualTo("java.lang.Override"))) {
                    return true;
                }
            }
        }
        return false;
    }
}
