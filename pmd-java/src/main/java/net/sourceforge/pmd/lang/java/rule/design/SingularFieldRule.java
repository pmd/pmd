/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.design;

import static net.sourceforge.pmd.properties.PropertyFactory.booleanProperty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTAssignmentOperator;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.java.ast.ASTInitializer;
import net.sourceforge.pmd.lang.java.ast.ASTLambdaExpression;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTStatementExpression;
import net.sourceforge.pmd.lang.java.ast.ASTSynchronizedStatement;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclarator;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.rule.AbstractLombokAwareRule;
import net.sourceforge.pmd.lang.symboltable.NameOccurrence;
import net.sourceforge.pmd.properties.PropertyDescriptor;


/**
 * @author Eric Olander
 * @author Wouter Zelle
 * @since Created on April 17, 2005, 9:49 PM
 */
public class SingularFieldRule extends AbstractLombokAwareRule {

    /**
     * Restore old behavior by setting both properties to true, which will
     * result in many false positives
     */
    private static final PropertyDescriptor<Boolean> CHECK_INNER_CLASSES = booleanProperty("checkInnerClasses").defaultValue(false).desc("Check inner classes").build();
    private static final PropertyDescriptor<Boolean> DISALLOW_NOT_ASSIGNMENT = booleanProperty("disallowNotAssignment").defaultValue(false).desc("Disallow violations where the first usage is not an assignment").build();


    public SingularFieldRule() {
        definePropertyDescriptor(CHECK_INNER_CLASSES);
        definePropertyDescriptor(DISALLOW_NOT_ASSIGNMENT);
    }

    @Override
    protected Collection<String> defaultSuppressionAnnotations() {
        Collection<String> defaultValues = new ArrayList<>();
        defaultValues.addAll(super.defaultSuppressionAnnotations());
        defaultValues.add("lombok.experimental.Delegate");
        return defaultValues;
    }

    @SuppressWarnings("PMD.CompareObjectsWithEquals")
    @Override
    public Object visit(ASTFieldDeclaration node, Object data) {
        boolean checkInnerClasses = getProperty(CHECK_INNER_CLASSES);
        boolean disallowNotAssignment = getProperty(DISALLOW_NOT_ASSIGNMENT);

        if (node.isPrivate() && !node.isStatic() && !hasClassLombokAnnotation() && !hasIgnoredAnnotation(node)) {
            for (ASTVariableDeclarator declarator : node.findChildrenOfType(ASTVariableDeclarator.class)) {
                ASTVariableDeclaratorId declaration = (ASTVariableDeclaratorId) declarator.getChild(0);
                List<NameOccurrence> usages = declaration.getUsages();
                Node decl = null;
                boolean violation = true;
                for (int ix = 0; ix < usages.size(); ix++) {
                    NameOccurrence no = usages.get(ix);
                    Node location = no.getLocation();

                    ASTPrimaryExpression primaryExpressionParent = location
                            .getFirstParentOfType(ASTPrimaryExpression.class);
                    if (ix == 0 && !disallowNotAssignment) {
                        if (primaryExpressionParent.getFirstParentOfType(ASTIfStatement.class) != null) {
                            // the first usage is in an if, so it may be skipped
                            // on
                            // later calls to the method. So this might be legit
                            // code
                            // that simply stores an object for later use.
                            violation = false;
                            break; // Optimization
                        }

                        // Is the first usage in an assignment?
                        Node potentialStatement = primaryExpressionParent.getParent();
                        // Check that the assignment is not to a field inside
                        // the field object
                        boolean assignmentToField = no.getImage().equals(location.getImage());
                        if (!assignmentToField || !isInAssignment(potentialStatement)) {
                            violation = false;
                            break; // Optimization
                        } else {
                            if (usages.size() > ix + 1) {
                                Node secondUsageLocation = usages.get(ix + 1).getLocation();

                                List<ASTStatementExpression> parentStatements = secondUsageLocation
                                        .getParentsOfType(ASTStatementExpression.class);
                                for (ASTStatementExpression statementExpression : parentStatements) {
                                    if (statementExpression != null && statementExpression.equals(potentialStatement)) {
                                        // The second usage is in the assignment
                                        // of the first usage, which is allowed
                                        violation = false;
                                        break; // Optimization
                                    }
                                }

                            }
                        }
                    }

                    if (!checkInnerClasses) {
                        // Skip inner classes because the field can be used in
                        // the outer class and checking this is too difficult
                        ASTClassOrInterfaceDeclaration clazz = location
                                .getFirstParentOfType(ASTClassOrInterfaceDeclaration.class);
                        if (clazz != null && clazz.getFirstParentOfType(ASTClassOrInterfaceDeclaration.class) != null) {
                            violation = false;
                            break; // Optimization
                        }
                    }

                    if (primaryExpressionParent.getParent() instanceof ASTSynchronizedStatement) {
                        // This usage is directly in an expression of a
                        // synchronized block
                        violation = false;
                        break; // Optimization
                    }

                    if (location.getFirstParentOfType(ASTLambdaExpression.class) != null) {
                        // This usage is inside a lambda expression
                        violation = false;
                        break; // Optimization
                    }

                    Node method = location.getFirstParentOfType(ASTMethodDeclaration.class);
                    if (method == null) {
                        method = location.getFirstParentOfType(ASTConstructorDeclaration.class);
                        if (method == null) {
                            method = location.getFirstParentOfType(ASTInitializer.class);
                            if (method == null) {
                                continue;
                            }
                        }
                    }

                    if (decl == null) {
                        decl = method;
                        continue;
                    } else if (decl != method
                            // handle inner classes
                            && decl.getFirstParentOfType(ASTClassOrInterfaceDeclaration.class) == method
                                    .getFirstParentOfType(ASTClassOrInterfaceDeclaration.class)) {
                        violation = false;
                        break; // Optimization
                    }

                }

                if (violation && !usages.isEmpty()) {
                    addViolation(data, node, new Object[] { declaration.getImage() });
                }
            }
        }
        return data;
    }

    private boolean isInAssignment(Node potentialStatement) {
        if (potentialStatement instanceof ASTStatementExpression) {
            ASTStatementExpression statement = (ASTStatementExpression) potentialStatement;
            List<ASTAssignmentOperator> assignments = statement.findDescendantsOfType(ASTAssignmentOperator.class);
            return !assignments.isEmpty() && "=".equals(assignments.get(0).getImage());
        } else {
            return false;
        }
    }
}
