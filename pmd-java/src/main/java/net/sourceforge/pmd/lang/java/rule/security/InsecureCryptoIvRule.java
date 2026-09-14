/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.security;

import net.sourceforge.pmd.lang.java.ast.ASTArgumentList;
import net.sourceforge.pmd.lang.java.ast.ASTArrayAllocation;
import net.sourceforge.pmd.lang.java.ast.ASTArrayDimExpr;
import net.sourceforge.pmd.lang.java.ast.ASTAssignmentExpression;
import net.sourceforge.pmd.lang.java.ast.ASTBlock;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorCall;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTExpressionStatement;
import net.sourceforge.pmd.lang.java.ast.ASTFieldAccess;
import net.sourceforge.pmd.lang.java.ast.ASTLambdaExpression;
import net.sourceforge.pmd.lang.java.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.java.ast.ASTTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTVariableAccess;
import net.sourceforge.pmd.lang.java.ast.ASTVariableId;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;

/**
 * Finds hardcoded static Initialization Vectors vectors used with cryptographic
 * operations.
 *
 * <code>
 * //bad: byte[] ivBytes = new byte[] {32, 87, -14, 25, 78, -104, 98, 40};
 * //bad: byte[] ivBytes = "hardcoded".getBytes();
 * //bad: byte[] ivBytes = someString.getBytes();
 * </code>
 *
 * <p>{@link javax.crypto.spec.IvParameterSpec} must not be created from a static sources
 *
 * @author sergeygorbaty
 * @since 6.3.0
 *
 */
// The inherited visit checks arguments without traversing children.
@SuppressWarnings("PMD.DontCallSuperVisitWhenUsingRuleChain")
public class InsecureCryptoIvRule extends AbstractHardCodedConstructorArgsVisitor {

    public InsecureCryptoIvRule() {
        super(javax.crypto.spec.IvParameterSpec.class);
    }

    @Override
    public Object visit(ASTConstructorCall node, Object data) {
        // This superclass checks arguments only; it does not traverse children.
        super.visit(node, data);
        // Subclasses may transform their arguments or override getIV().
        if (!node.isAnonymousClass() && TypeTestUtil.isExactlyA(javax.crypto.spec.IvParameterSpec.class, node)
                && node.getArguments().size() > 0) {
            ASTExpression argument = node.getArguments().get(0);
            if (isDefaultArray(argument)
                    || (argument instanceof ASTVariableAccess && isUnchangedArray((ASTVariableAccess) argument, node))) {
                asCtx(data).addViolation(node);
            }
        }
        return data;
    }

    private boolean isDefaultArray(ASTExpression expression) {
        if (!(expression instanceof ASTArrayAllocation) || !TypeTestUtil.isExactlyA(byte[].class, expression)) {
            return false;
        }
        ASTArrayAllocation array = (ASTArrayAllocation) expression;
        JavaNode dimension = array.getTypeNode().getDimensions().get(0);
        if (!(dimension instanceof ASTArrayDimExpr)) {
            return false;
        }
        // Constant folding also resolves known lengths that getConstValue() does not expose.
        Object length = ((ASTArrayDimExpr) dimension).getLengthExpression().getConstFoldingResult().getValue();
        // Array dimensions promote char values to int.
        if (length instanceof Character) {
            length = (int) (Character) length;
        }
        return array.getArrayInitializer() == null && length instanceof Number && ((Number) length).intValue() > 0;
    }

    private boolean isUnchangedArray(ASTVariableAccess argument, ASTConstructorCall constructor) {
        if (argument.getReferencedSym() == null) {
            return false;
        }
        ASTVariableId variable = argument.getReferencedSym().tryGetNode();
        ASTBlock block = constructor.ancestors(ASTBlock.class).first();
        if (variable == null || !variable.isLocalVariable() || block == null
                || !isStraightLineStatement(constructor, block)) {
            return false;
        }

        ASTExpression allocation = variable.getInitializer();
        JavaNode initialAssignment = null;
        if (allocation == null) {
            ASTAssignmentExpression assignment = findInitialAssignment(variable, argument, block);
            if (assignment == null) {
                return false;
            }
            allocation = assignment.getRightOperand();
            initialAssignment = assignment.getLeftOperand();
        }
        if (!isDefaultArray(allocation) || !isStraightLineStatement(allocation, block)) {
            return false;
        }

        for (JavaNode usage : variable.getLocalUsages()) {
            if (usage == argument || usage == initialAssignment
                    || usage.getTextRegion().getStartOffset() >= constructor.getTextRegion().getEndOffset()) {
                continue;
            }
            // A nested block may run repeatedly or capture the array for execution in a different order.
            // Do not infer execution order from source positions in that case.
            if (!isStraightLineStatement(usage, block)) {
                return false;
            }
            if (!isReadOnlyUse(usage)) {
                return false;
            }
        }
        return true;
    }

    private ASTAssignmentExpression findInitialAssignment(ASTVariableId variable, ASTVariableAccess argument,
                                                          ASTBlock block) {
        ASTAssignmentExpression result = null;
        // Accept one unconditional assignment before the use, without following reassignment chains.
        for (JavaNode usage : variable.getLocalUsages()) {
            if (usage.getParent() instanceof ASTAssignmentExpression) {
                ASTAssignmentExpression assignment = (ASTAssignmentExpression) usage.getParent();
                if (assignment.getLeftOperand() == usage && !assignment.isCompound()
                        && assignment.getParent() instanceof ASTExpressionStatement
                        && assignment.getParent().getParent() == block
                        && assignment.getTextRegion().getEndOffset() < argument.getTextRegion().getStartOffset()) {
                    if (result != null) {
                        return null;
                    }
                    result = assignment;
                }
            }
        }
        return result;
    }

    private boolean isReadOnlyUse(JavaNode usage) {
        boolean readsLength = usage.getParent() instanceof ASTFieldAccess
            && "length".equals(((ASTFieldAccess) usage.getParent()).getName());
        // IvParameterSpec copies its input. A previous construction does not change the array.
        return readsLength || usage.getParent() instanceof ASTArgumentList && usage.getIndexInParent() == 0
            && usage.getParent().getParent() instanceof ASTConstructorCall
            && TypeTestUtil.isExactlyA(javax.crypto.spec.IvParameterSpec.class,
                                      (ASTConstructorCall) usage.getParent().getParent());
    }

    private boolean isStraightLineStatement(JavaNode node, ASTBlock block) {
        JavaNode statement = node;
        while (statement.getParent() != null && statement.getParent() != block) {
            if (statement instanceof ASTBlock || statement instanceof ASTLambdaExpression
                    || statement instanceof ASTTypeDeclaration) {
                return false;
            }
            statement = statement.getParent();
        }
        return statement.getParent() == block
            && (statement instanceof ASTExpressionStatement || statement instanceof ASTLocalVariableDeclaration
                || statement instanceof ASTReturnStatement);
    }
}
