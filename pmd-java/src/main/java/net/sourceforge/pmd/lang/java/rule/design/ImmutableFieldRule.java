/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.design;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceBody;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceBodyDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTDoStatement;
import net.sourceforge.pmd.lang.java.ast.ASTForStatement;
import net.sourceforge.pmd.lang.java.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.java.ast.ASTLambdaExpression;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTTryStatement;
import net.sourceforge.pmd.lang.java.ast.ASTVariableInitializer;
import net.sourceforge.pmd.lang.java.ast.ASTWhileStatement;
import net.sourceforge.pmd.lang.java.ast.AccessNode;
import net.sourceforge.pmd.lang.java.ast.Annotatable;
import net.sourceforge.pmd.lang.java.rule.AbstractLombokAwareRule;
import net.sourceforge.pmd.lang.java.symboltable.JavaNameOccurrence;
import net.sourceforge.pmd.lang.java.symboltable.VariableNameDeclaration;
import net.sourceforge.pmd.lang.symboltable.NameOccurrence;

/**
 * @author Olander
 */
public class ImmutableFieldRule extends AbstractLombokAwareRule {

    private enum FieldImmutabilityType {
        /** Variable is changed in methods and/or in lambdas */
        MUTABLE,
        /** Variable is not changed outside the constructor. */
        IMMUTABLE,
        /** Variable is only written during declaration, if at all. */
        CHECKDECL
    }

    @Override
    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        Object result = super.visit(node, data);

        Map<VariableNameDeclaration, List<NameOccurrence>> vars = node.getScope()
                .getDeclarations(VariableNameDeclaration.class);
        List<ASTConstructorDeclaration> constructors = findAllConstructors(node);
        for (Map.Entry<VariableNameDeclaration, List<NameOccurrence>> entry : vars.entrySet()) {
            VariableNameDeclaration field = entry.getKey();
            AccessNode accessNodeParent = field.getAccessNodeParent();
            if (accessNodeParent.isStatic() || !accessNodeParent.isPrivate() || accessNodeParent.isFinal()
                    || accessNodeParent.isVolatile()
                    || hasLombokAnnotation(node)
                    || hasIgnoredAnnotation((Annotatable) accessNodeParent)) {
                continue;
            }

            FieldImmutabilityType type = initializedInConstructor(field, entry.getValue(), new HashSet<>(constructors));
            if (type == FieldImmutabilityType.MUTABLE) {
                continue;
            }
            if (type == FieldImmutabilityType.IMMUTABLE || type == FieldImmutabilityType.CHECKDECL && initializedWhenDeclared(field)) {
                addViolation(data, field.getNode(), field.getImage());
            }
        }
        return result;
    }

    private boolean initializedWhenDeclared(VariableNameDeclaration field) {
        return field.getAccessNodeParent().hasDescendantOfType(ASTVariableInitializer.class);
    }

    private FieldImmutabilityType initializedInConstructor(VariableNameDeclaration field, List<NameOccurrence> usages, Set<ASTConstructorDeclaration> allConstructors) {
        FieldImmutabilityType result = FieldImmutabilityType.MUTABLE;
        int methodInitCount = 0;
        int lambdaUsage = 0;
        Set<ASTConstructorDeclaration> consSet = new HashSet<>(); // set of constructors accessing the field
        for (NameOccurrence occ : usages) {
            JavaNameOccurrence jocc = (JavaNameOccurrence) occ;
            if (jocc.isOnLeftHandSide() || jocc.isSelfAssignment()) {
                Node node = jocc.getLocation();
                ASTConstructorDeclaration constructor = node.getFirstParentOfType(ASTConstructorDeclaration.class);
                if (constructor != null && isSameClass(field, constructor)) {
                    if (inLoopOrTry(node)) {
                        methodInitCount++;
                        continue;
                    }
                    // Check for assigns in if-statements, which can depend on
                    // constructor
                    // args or other runtime knowledge and can be a valid reason
                    // to instantiate
                    // in one constructor only
                    if (node.getFirstParentOfType(ASTIfStatement.class) != null) {
                        methodInitCount++;
                    }
                    if (inAnonymousInnerClass(node)) {
                        methodInitCount++;
                    } else if (node.getFirstParentOfType(ASTLambdaExpression.class) != null) {
                        lambdaUsage++;
                    } else {
                        consSet.add(constructor);
                    }
                } else {
                    if (node.getFirstParentOfType(ASTMethodDeclaration.class) != null) {
                        methodInitCount++;
                    } else if (node.getFirstParentOfType(ASTLambdaExpression.class) != null) {
                        lambdaUsage++;
                    }
                }
            }
        }
        if (usages.isEmpty() || methodInitCount == 0 && lambdaUsage == 0 && consSet.isEmpty()) {
            result = FieldImmutabilityType.CHECKDECL;
        } else {
            allConstructors.removeAll(consSet);
            if (allConstructors.isEmpty() && methodInitCount == 0 && lambdaUsage == 0) {
                result = FieldImmutabilityType.IMMUTABLE;
            }
        }
        return result;
    }

    /**
     * Checks whether the given constructor belongs to the class, in which the field is declared.
     * This might not be the case for inner classes, which accesses the fields of the outer class.
     */
    private boolean isSameClass(VariableNameDeclaration field, ASTConstructorDeclaration constructor) {
        return constructor.getFirstParentOfType(ASTClassOrInterfaceBody.class) == field.getNode().getFirstParentOfType(ASTClassOrInterfaceBody.class);
    }

    private boolean inLoopOrTry(Node node) {
        return node.getFirstParentOfType(ASTTryStatement.class) != null
                || node.getFirstParentOfType(ASTForStatement.class) != null
                || node.getFirstParentOfType(ASTWhileStatement.class) != null
                || node.getFirstParentOfType(ASTDoStatement.class) != null;
    }

    private boolean inAnonymousInnerClass(Node node) {
        ASTClassOrInterfaceBodyDeclaration parent = node.getFirstParentOfType(ASTClassOrInterfaceBodyDeclaration.class);
        return parent != null && parent.isAnonymousInnerClass();
    }

    private List<ASTConstructorDeclaration> findAllConstructors(ASTClassOrInterfaceDeclaration node) {
        return node.getFirstChildOfType(ASTClassOrInterfaceBody.class)
                .findDescendantsOfType(ASTConstructorDeclaration.class);
    }
}
