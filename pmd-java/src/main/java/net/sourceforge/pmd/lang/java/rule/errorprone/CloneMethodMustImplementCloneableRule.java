/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTBlock;
import net.sourceforge.pmd.lang.java.ast.ASTBlockStatement;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTExtendsList;
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
public class CloneMethodMustImplementCloneableRule extends AbstractJavaRule {

    @Override
    public Object visit(final ASTClassOrInterfaceDeclaration node, final Object data) {
        if (extendsOrImplementsCloneable(node)) {
            return data;
        }
        return super.visit(node, data);
    }

    private boolean extendsOrImplementsCloneable(final ASTClassOrInterfaceDeclaration node) {
        if (node.getType() != null) {
            return Cloneable.class.isAssignableFrom(node.getType());
        }

        // From this point on, this is a best effort, the auxclasspath is incomplete.

        // TODO : Should we really care about this?
        // Shouldn't the type resolver / symbol table report missing classes and the user
        // know results are dependent on running under proper arguments?
        final ASTImplementsList impl = node.getFirstChildOfType(ASTImplementsList.class);
        if (impl != null) {
            for (int ix = 0; ix < impl.getNumChildren(); ix++) {
                final Node child = impl.getChild(ix);

                if (child.getClass() != ASTClassOrInterfaceType.class) {
                    continue;
                }

                final ASTClassOrInterfaceType type = (ASTClassOrInterfaceType) child;
                if (type.getType() == null) {
                    if ("Cloneable".equals(type.getImage())) {
                        return true;
                    }
                } else if (Cloneable.class.isAssignableFrom(type.getType())) {
                    return true;
                }
            }
        }

        if (node.getNumChildren() != 0 && node.getChild(0) instanceof ASTExtendsList) {
            final ASTClassOrInterfaceType type = (ASTClassOrInterfaceType) node.getChild(0).getChild(0);
            final Class<?> clazz = type.getType();
            if (clazz != null) {
                return Cloneable.class.isAssignableFrom(clazz);
            }
        }

        return false;
    }

    @Override
    public Object visit(final ASTMethodDeclaration node, final Object data) {
        // Is this a clone method?
        final ASTMethodDeclarator methodDeclarator = node.getFirstChildOfType(ASTMethodDeclarator.class);
        if (!isCloneMethod(methodDeclarator)) {
            return data;
        }

        // Is the clone method just throwing CloneNotSupportedException?
        final ASTClassOrInterfaceDeclaration classOrInterface = node.getFirstParentOfType(ASTClassOrInterfaceDeclaration.class);
        if (classOrInterface != null && //Don't analyze enums, which cannot subclass clone()
            (node.isFinal() || classOrInterface.isFinal())) {
            if (node.findDescendantsOfType(ASTBlock.class).size() == 1) {
                final List<ASTBlockStatement> blocks = node.findDescendantsOfType(ASTBlockStatement.class);
                if (blocks.size() == 1) {
                    final ASTBlockStatement block = blocks.get(0);
                    final ASTClassOrInterfaceType type = block.getFirstDescendantOfType(ASTClassOrInterfaceType.class);
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

        // TODO : Should we really care about this? It can only happen with an incomplete auxclasspath
        if (classOrInterface != null && classOrInterface.getType() == null) {
            // Now check other whether implemented or extended classes are defined inside the same file
            final Set<String> classesNames = determineTopLevelCloneableClasses(classOrInterface);

            final ASTImplementsList implementsList = classOrInterface.getFirstChildOfType(ASTImplementsList.class);
            if (implementsList != null) {
                final List<ASTClassOrInterfaceType> types = implementsList.findChildrenOfType(ASTClassOrInterfaceType.class);
                for (final ASTClassOrInterfaceType t : types) {
                    if (classesNames.contains(t.getImage())) {
                        return data;
                    }
                }
            }

            final ASTExtendsList extendsList = classOrInterface.getFirstChildOfType(ASTExtendsList.class);
            if (extendsList != null) {
                final ASTClassOrInterfaceType type = extendsList.getFirstChildOfType(ASTClassOrInterfaceType.class);
                if (classesNames.contains(type.getImage())) {
                    return data;
                }
            }
        }

        // Nothing can save us now
        addViolation(data, node);
        return data;
    }

    /**
     * Determines all the class/interface declarations inside this compilation
     * unit, which implement Cloneable
     *
     * @param currentClass
     *            the node of the class, that is currently analyzed (inside this
     *            compilation unit)
     * @return a Set of class/interface names
     */
    private Set<String> determineTopLevelCloneableClasses(final ASTClassOrInterfaceDeclaration currentClass) {
        final List<ASTClassOrInterfaceDeclaration> classes = currentClass.getFirstParentOfType(ASTCompilationUnit.class)
                .findDescendantsOfType(ASTClassOrInterfaceDeclaration.class);
        final Set<String> classesNames = new HashSet<>();
        for (final ASTClassOrInterfaceDeclaration c : classes) {
            if (!Objects.equals(c, currentClass) && extendsOrImplementsCloneable(c)) {
                classesNames.add(c.getImage());
            }
        }
        return classesNames;
    }

    public boolean isCloneMethod(final ASTMethodDeclarator method) {
        if (!"clone".equals(method.getImage())) {
            return false;
        }
        return method.getParameterCount() == 0;
    }
}
