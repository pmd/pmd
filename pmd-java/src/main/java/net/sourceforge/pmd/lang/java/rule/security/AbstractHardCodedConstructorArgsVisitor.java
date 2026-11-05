/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.security;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTAnonymousClassDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTArgumentList;
import net.sourceforge.pmd.lang.java.ast.ASTArrayAccess;
import net.sourceforge.pmd.lang.java.ast.ASTArrayAllocation;
import net.sourceforge.pmd.lang.java.ast.ASTArrayDimExpr;
import net.sourceforge.pmd.lang.java.ast.ASTArrayInitializer;
import net.sourceforge.pmd.lang.java.ast.ASTArrayTypeDim;
import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr;
import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr.ASTNamedReferenceExpr;
import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr.AccessType;
import net.sourceforge.pmd.lang.java.ast.ASTAssignmentExpression;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorCall;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.ast.ASTStringLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTVariableAccess;
import net.sourceforge.pmd.lang.java.ast.ASTVariableId;
import net.sourceforge.pmd.lang.java.ast.InvocationNode;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.ast.internal.JavaAstUtils;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.types.InvocationMatcher;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;

abstract class AbstractHardCodedConstructorArgsVisitor extends AbstractJavaRulechainRule {

    private static final InvocationMatcher SYSTEM_GET_PROPERTY =
            InvocationMatcher.parse("java.lang.System#getProperty(_*)");

    private final Class<?> type;

    AbstractHardCodedConstructorArgsVisitor(Class<?> constructorType) {
        super(ASTConstructorCall.class);
        this.type = constructorType;
    }

    @Override
    public Object visit(ASTConstructorCall node, Object data) {
        // skip anonymous classes with body - they might transform their byte[] constructor argument - we don't analyze this
        ASTAnonymousClassDeclaration anonClass = node.getAnonymousClassDeclaration();
        if (anonClass != null && !anonClass.getBody().isEmpty()) {
            return null;
        }

        if (TypeTestUtil.isExactlyA(type, node)) {
            ASTArgumentList arguments = node.getArguments();
            if (arguments.size() > 0) {
                validateProperKeyArgument(data, arguments.get(0));
            }
        }
        return data;
    }

    /**
     * Recursively resolves the argument again, if the variable initializer
     * is itself a expression.
     *
     * <p>Then checks the expression for being a string literal or array
     */
    private void validateProperKeyArgument(Object data, ASTExpression firstArgumentExpression) {
        if (firstArgumentExpression == null) {
            return;
        }

        ASTVariableAccess varAccess = null;
        if (firstArgumentExpression instanceof ASTMethodCall) {
            // check for method call on a named variable
            ASTExpression expr = ((ASTMethodCall) firstArgumentExpression).getQualifier();
            if (expr instanceof ASTVariableAccess) {
                varAccess = (ASTVariableAccess) expr;
            }
        } else if (firstArgumentExpression instanceof ASTVariableAccess) {
            // check for named variable
            varAccess = (ASTVariableAccess) firstArgumentExpression;
        }

        if (varAccess != null && varAccess.getSignature() != null && varAccess.getSignature().getSymbol() != null) {
            // named variable or method call on named variable found
            ASTVariableId varDecl = varAccess.getSignature().getSymbol().tryGetNode();
            if (varDecl != null) {
                if (isArrayModifiedOrLeaked(varDecl, varAccess)) {
                    return;
                }

                validateProperKeyArgument(data, varDecl.getInitializer());
                validateVarUsages(data, varAccess, varDecl);
            } else if (varAccess.isCompileTimeConstant()
                    && varAccess.getConstValue() instanceof String) {
                asCtx(data).addViolation(varAccess);
            }
        } else if (firstArgumentExpression instanceof ASTConstructorCall) {
            ASTConstructorCall constructorCall = (ASTConstructorCall) firstArgumentExpression;
            ASTArgumentList arguments = constructorCall.getArguments();
            if (arguments.size() == 1
                    && TypeTestUtil.isExactlyA(String.class, constructorCall.getTypeNode())
                    && TypeTestUtil.isExactlyA(char[].class, arguments.get(0))) {
                validateHardCodedCharArray(data, arguments.get(0));
            } else {
                addViolationForStringLiteral(data, firstArgumentExpression);
            }
        } else if (firstArgumentExpression instanceof ASTArrayAllocation) {
            // hard-coded array (default initialized or with constant values)
            ASTArrayInitializer arrayInit = ((ASTArrayAllocation) firstArgumentExpression).getArrayInitializer();
            if (arrayInit != null) {
                asCtx(data).addViolation(arrayInit);
            } else {
                // default-initialized array with length > 0
                Optional<Number> length = determineArrayLength((ASTArrayAllocation) firstArgumentExpression);
                // when length is null, then the expression is not a constant (e.g. method call)
                if (!length.isPresent() || length.get().intValue() > 0) {
                    asCtx(data).addViolation(firstArgumentExpression);
                }
            }
        } else if (firstArgumentExpression instanceof ASTArrayInitializer) {
            // hard coded array
            asCtx(data).addViolation(firstArgumentExpression);
        } else {
            // string literal
            addViolationForStringLiteral(data, firstArgumentExpression);
        }
    }

    private Optional<Number> determineArrayLength(ASTArrayAllocation arrayAllocation) {
        ASTArrayTypeDim astArrayTypeDim = arrayAllocation.getTypeNode().getDimensions().get(0);
        if (astArrayTypeDim instanceof ASTArrayDimExpr) {
            // Constant folding also resolves known lengths that getConstValue() does not expose.
            ASTExpression lengthExpression = ((ASTArrayDimExpr) astArrayTypeDim).getLengthExpression();
            Object length = lengthExpression.getConstFoldingResult().getValue();
            // Array dimensions promote char values to int.
            if (length instanceof Character) {
                length = (int) (Character) length;
            }
            if (length instanceof Number) {
                return Optional.of((Number) length);
            }
        }
        return Optional.empty();
    }

    /**
     * If the named variable is an array (that is passed to the constructor),
     * we need to check additional conditions. This is a simplified data flow
     * analysis that only checks:
     *
     * <ul>
     *   <li>modification/reassignment (if the array is modified locally before usage)</li>
     *   <li>leaks (if the array is passed to other methods or fields)</li>
     * </ul>
     *
     * @param varDecl
     * @param varAccess
     * @return
     */
    private boolean isArrayModifiedOrLeaked(ASTVariableId varDecl, ASTVariableAccess varAccess) {
        boolean isArray = TypeTestUtil.isExactlyA(byte[].class, varDecl.getTypeMirror());
        if (!isArray) {
            return false;
        }

        List<ASTNamedReferenceExpr> localUsages = new ArrayList<>(varDecl.getLocalUsages());
        localUsages.sort(Node.COORDS_COMPARATOR);
        // remove accesses after the current usage (e.g. ctor call)
        if (localUsages.contains(varAccess)) {
            localUsages = localUsages.subList(0, localUsages.indexOf(varAccess));
        }

        return arrayIsLeaking(varAccess, localUsages) || arrayIsModifiedLocally(varAccess, localUsages);
    }

    /**
     * Checks whether the array is modified locally either by assigning to array elements
     * or by reassigning the reference.
     *
     * @param varAccess
     * @param localUsages
     * @return
     */
    private boolean arrayIsModifiedLocally(ASTVariableAccess varAccess, List<ASTNamedReferenceExpr> localUsages) {
        // if there are any writes to array elements which calculate values (e.g. return values of method calls)
        boolean anyWritesToArrayElements = localUsages.stream()
                .filter(ref -> ref.getParent() instanceof ASTArrayAccess)
                .map(JavaNode::getParent)
                .filter(ASTAssignableExpr.class::isInstance)
                .map(ASTAssignableExpr.class::cast)
                .filter(ref -> ref.getAccessType() == AccessType.WRITE)
                .filter(ref -> ref.getParent() instanceof ASTAssignmentExpression)
                .anyMatch(ref -> {
                    ASTAssignmentExpression assignmentExpr = (ASTAssignmentExpression) ref.getParent();
                    return assignmentExpr.getRightOperand() instanceof InvocationNode;
                });

        boolean reassignment = localUsages.stream()
                .filter(ref -> ref.getParent() instanceof ASTAssignmentExpression)
                .anyMatch(ref -> {
                    ASTAssignmentExpression assignmentExpr = (ASTAssignmentExpression) ref.getParent();
                    ASTAssignableExpr leftOperand = assignmentExpr.getLeftOperand();
                    ASTExpression rightOperand = assignmentExpr.getRightOperand();
                    return JavaAstUtils.isReferenceToSameVar(varAccess, leftOperand)
                            && rightOperand instanceof ASTNamedReferenceExpr;
                });

        return anyWritesToArrayElements || reassignment;
    }

    /**
     * Checks whether the array is passed to a method call or passed to a
     * field. It then leaks the current local scope and might be modified.
     *
     * @param varAccess
     * @param localUsages
     */
    private boolean arrayIsLeaking(ASTVariableAccess varAccess, List<ASTNamedReferenceExpr> localUsages) {
        // the variable is used in a method call - the method might change the array
        boolean leakIntoMethodCalls = localUsages.stream()
                .anyMatch(ref -> ref.getParent() instanceof ASTArgumentList);

        // the var is stored into a field
        boolean leakIntoFields = localUsages.stream()
                .filter(ref -> ref.getParent() instanceof ASTAssignmentExpression)
                .anyMatch(ref -> {
                    ASTAssignmentExpression assignmentExpr = (ASTAssignmentExpression) ref.getParent();
                    ASTAssignableExpr leftOperand = assignmentExpr.getLeftOperand();
                    ASTExpression rightOperand = assignmentExpr.getRightOperand();
                    return JavaAstUtils.isRefToFieldOfThisClass(leftOperand)
                            && JavaAstUtils.isReferenceToSameVar(varAccess, rightOperand);
                });

        return leakIntoMethodCalls || leakIntoFields;
    }

    private void validateHardCodedCharArray(Object data, ASTExpression expression) {
        if (expression instanceof ASTVariableAccess) {
            ASTVariableId varDecl = ((ASTVariableAccess) expression).getReferencedSym().tryGetNode();
            if (varDecl != null) {
                validateHardCodedCharArray(data, varDecl.getInitializer());
            }
        } else if (expression instanceof ASTArrayAllocation) {
            ASTArrayInitializer arrayInit = ((ASTArrayAllocation) expression).getArrayInitializer();
            if (isConstantCharArray(arrayInit)) {
                asCtx(data).addViolation(arrayInit);
            }
        } else if (expression instanceof ASTArrayInitializer && isConstantCharArray((ASTArrayInitializer) expression)) {
            asCtx(data).addViolation(expression);
        }
    }

    private boolean isConstantCharArray(ASTArrayInitializer arrayInit) {
        if (arrayInit == null) {
            return false;
        }
        for (ASTExpression element : arrayInit) {
            if (!(element.getConstValue() instanceof Character)) {
                return false;
            }
        }
        return true;
    }

    private void addViolationForStringLiteral(Object data, ASTExpression expression) {
        ASTStringLiteral literal = expression.descendantsOrSelf()
                .filterIs(ASTStringLiteral.class)
                .filterNot(AbstractHardCodedConstructorArgsVisitor::isPropertyName)
                .first();
        if (literal != null) {
            asCtx(data).addViolation(literal);
        }
    }

    /**
     * Whether the literal is the property name of a {@code System.getProperty} call.
     */
    private static boolean isPropertyName(ASTStringLiteral literal) {
        ASTMethodCall call = literal.ancestors(ASTMethodCall.class).first();
        if (!SYSTEM_GET_PROPERTY.matchesCall(call)) {
            return false;
        }
        ASTArgumentList arguments = call.getArguments();
        return !arguments.isEmpty() && arguments.get(0).descendantsOrSelf().any(node -> node == literal);
    }

    private void validateVarUsages(Object data, ASTVariableAccess varAccess, ASTVariableId varDecl) {
        List<ASTNamedReferenceExpr> localUsages = new ArrayList<>(varDecl.getLocalUsages());
        localUsages.sort(Node.COORDS_COMPARATOR);
        // remove accesses after the current usage (e.g. ctor call)
        if (localUsages.contains(varAccess)) {
            localUsages = localUsages.subList(0, localUsages.indexOf(varAccess));
        }
        localUsages.stream()
            .filter(u -> u.getAccessType() == AccessType.WRITE)
            .filter(u -> u.getParent() instanceof ASTAssignmentExpression)
            .forEach(usage -> {
                ASTAssignmentExpression assignment = (ASTAssignmentExpression) usage.getParent();
                validateProperKeyArgument(data, assignment.getRightOperand());
            });
    }
}
