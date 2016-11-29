/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.typeresolution.rules;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTBlock;
import net.sourceforge.pmd.lang.java.ast.ASTBlockStatement;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTExtendsList;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameters;
import net.sourceforge.pmd.lang.java.ast.ASTImplementsList;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclarator;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

/**
 * The method clone() should only be implemented if the class implements the
 * Cloneable interface with the exception of a final method that only throws
 * CloneNotSupportedException. This version uses PMD's type resolution
 * facilities, and can detect if the class implements or extends a Cloneable
 * class
 *
 * @author acaplan
 */
public class CloneMethodMustImplementCloneable extends AbstractJavaRule {

    @Override
    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        if (extendsOrImplementsCloneable(node)) {
            return data;
        }
        return super.visit(node, data);
    }

    private boolean extendsOrImplementsCloneable(ASTClassOrInterfaceDeclaration node) {
        ASTImplementsList impl = node.getFirstChildOfType(ASTImplementsList.class);
        if (impl != null && impl.jjtGetParent().equals(node)) {
            for (int ix = 0; ix < impl.jjtGetNumChildren(); ix++) {
                Node child = impl.jjtGetChild(ix);

                if (child.getClass() != ASTClassOrInterfaceType.class) {
                    continue;
                }

                ASTClassOrInterfaceType type = (ASTClassOrInterfaceType) child;
                if (type.getType() == null) {
                    if ("Cloneable".equals(type.getImage())) {
                        return true;
                    }
                } else if (type.getType().equals(Cloneable.class)) {
                    return true;
                } else if (Cloneable.class.isAssignableFrom(type.getType())) {
                    return true;
                } else {
                    List<Class<?>> implementors = Arrays.asList(type.getType().getInterfaces());
                    if (implementors.contains(Cloneable.class)) {
                        return true;
                    }
                }
            }
        }
        if (node.jjtGetNumChildren() != 0 && node.jjtGetChild(0) instanceof ASTExtendsList) {
            ASTClassOrInterfaceType type = (ASTClassOrInterfaceType) node.jjtGetChild(0).jjtGetChild(0);
            Class<?> clazz = type.getType();
            if (clazz != null && clazz.equals(Cloneable.class)) {
                return true;
            }
            while (clazz != null && !Object.class.equals(clazz)) {
                if (Arrays.asList(clazz.getInterfaces()).contains(Cloneable.class)) {
                    return true;
                }
                clazz = clazz.getSuperclass();
            }
        }
        return false;
    }

    @Override
    public Object visit(ASTMethodDeclaration node, Object data) {
        ASTClassOrInterfaceDeclaration classOrInterface = node.getFirstParentOfType(ASTClassOrInterfaceDeclaration.class);
        if (classOrInterface != null && //Don't analyze enums, which cannot subclass clone()
            (node.isFinal() || classOrInterface.isFinal())) {
            if (node.findDescendantsOfType(ASTBlock.class).size() == 1) {
                List<ASTBlockStatement> blocks = node.findDescendantsOfType(ASTBlockStatement.class);
                if (blocks.size() == 1) {
                    ASTBlockStatement block = blocks.get(0);
                    ASTClassOrInterfaceType type = block.getFirstDescendantOfType(ASTClassOrInterfaceType.class);
                    if (type != null && type.getType() != null && type.getNthParent(9).equals(node)
                            && type.getType().equals(CloneNotSupportedException.class)) {
                        return data;
                    } else if (type != null && type.getType() == null
                            && "CloneNotSupportedException".equals(type.getImage())) {
                        return data;
                    }
                }
            }
        }

        // Now check other whether implemented or extended classes are defined inside the same file
        if (classOrInterface != null) {
            Set<String> classesNames = determineTopLevelCloneableClasses(classOrInterface);

            ASTImplementsList implementsList = classOrInterface.getFirstChildOfType(ASTImplementsList.class);
            if (implementsList != null) {
                List<ASTClassOrInterfaceType> types = implementsList.findChildrenOfType(ASTClassOrInterfaceType.class);
                for (ASTClassOrInterfaceType t : types) {
                    if (classesNames.contains(t.getImage())) {
                        return data;
                    }
                }
            }

            ASTExtendsList extendsList = classOrInterface.getFirstChildOfType(ASTExtendsList.class);
            if (extendsList != null) {
                ASTClassOrInterfaceType type = extendsList.getFirstChildOfType(ASTClassOrInterfaceType.class);
                if (classesNames.contains(type.getImage())) {
                    return data;
                }
            }
        }

        return super.visit(node, data);
    }

    /**
     * Determines all the class/interface declarations inside this compilation unit,
     * which implement Cloneable
     * @param currentClass the node of the class, that is currently analyzed (inside this compilation unit)
     * @return a Set of class/interface names
     */
    private Set<String> determineTopLevelCloneableClasses(ASTClassOrInterfaceDeclaration currentClass) {
        List<ASTClassOrInterfaceDeclaration> classes = currentClass.getFirstParentOfType(ASTCompilationUnit.class)
                .findDescendantsOfType(ASTClassOrInterfaceDeclaration.class);
        Set<String> classesNames = new HashSet<String>();
        for (ASTClassOrInterfaceDeclaration c : classes) {
            if (c != currentClass && extendsOrImplementsCloneable(c)) {
                classesNames.add(c.getImage());
            }
        }
        return classesNames;
    }

    @Override
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