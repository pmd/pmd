/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.typeresolution.rules;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.ast.ASTBlock;
import net.sourceforge.pmd.ast.ASTBlockStatement;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.ast.ASTExtendsList;
import net.sourceforge.pmd.ast.ASTFormalParameters;
import net.sourceforge.pmd.ast.ASTImplementsList;
import net.sourceforge.pmd.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.ast.ASTMethodDeclarator;
import net.sourceforge.pmd.ast.SimpleNode;

import java.util.Arrays;
import java.util.List;

/**
 * The method clone() should only be implemented if the class implements the
 * Cloneable interface with the exception of a final method that only throws
 * CloneNotSupportedException. This version uses PMD's type resolution
 * facilities, and can detect if the class implements or extends a Cloneable
 * class
 * 
 * @author acaplan
 * 
 */
public class CloneMethodMustImplementCloneable extends AbstractRule {

    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        ASTImplementsList impl = (ASTImplementsList) node.getFirstChildOfType(ASTImplementsList.class);
        if (impl != null && impl.jjtGetParent().equals(node)) {
            for (int ix = 0; ix < impl.jjtGetNumChildren(); ix++) {
                ASTClassOrInterfaceType type = (ASTClassOrInterfaceType) impl.jjtGetChild(ix);
                if (type.getType() == null) {
                    if ("Cloneable".equals(type.getImage())) {
                        return data;
                    }
                } else if (type.getType().equals(Cloneable.class)) {
                    return data;
                } else {
                    List implementors = Arrays.asList(type.getType().getInterfaces());
                    if (implementors.contains(Cloneable.class)) {
                        return data;
                    }
                }
            }
        }
        if (node.jjtGetNumChildren() != 0 && node.jjtGetChild(0).getClass().equals(ASTExtendsList.class)) {
            ASTClassOrInterfaceType type = (ASTClassOrInterfaceType) ((SimpleNode) node.jjtGetChild(0)).jjtGetChild(0);
            Class clazz = type.getType();
            if (clazz != null && clazz.equals(Cloneable.class)) {
                return data;
            }
            while (clazz != null && !Object.class.equals(clazz)) {
                if (Arrays.asList(clazz.getInterfaces()).contains(Cloneable.class)) {
                    return data;
                }
                clazz = clazz.getSuperclass();
            }
        }

        return super.visit(node, data);
    }

    public Object visit(ASTMethodDeclaration node, Object data) {

        if (node.isFinal()) {
            List blocks = node.findChildrenOfType(ASTBlock.class);
            if (blocks.size() == 1) {
                blocks = node.findChildrenOfType(ASTBlockStatement.class);
                if (blocks.size() == 1) {
                    ASTBlockStatement block = (ASTBlockStatement) blocks.get(0);
                    ASTClassOrInterfaceType type = (ASTClassOrInterfaceType) block.getFirstChildOfType(ASTClassOrInterfaceType.class);
                    if (type != null && type.getType() != null && type.getNthParent(9).equals(node) && type.getType().equals(CloneNotSupportedException.class)) {
                        return data;
                    } else if (type != null && type.getType() == null && "CloneNotSupportedException".equals(type.getImage())) {
                        return data;
                    }
                }
            }
        }
        return super.visit(node, data);
    }

    public Object visit(ASTMethodDeclarator node, Object data) {
        if (!"clone".equals(node.getImage())) {
            return data;
        }
        int countParams = ((ASTFormalParameters) node.jjtGetChild(0)).jjtGetNumChildren();
        if (countParams != 0) {
            return data;
        }
        addViolation(data, node);
        return data;
    }
}