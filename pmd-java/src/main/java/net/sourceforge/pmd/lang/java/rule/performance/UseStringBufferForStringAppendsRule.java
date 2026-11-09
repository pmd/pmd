/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.performance;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTArgumentList;
import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr.ASTNamedReferenceExpr;
import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr.AccessType;
import net.sourceforge.pmd.lang.java.ast.ASTAssignmentExpression;
import net.sourceforge.pmd.lang.java.ast.ASTBodyDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTCastExpression;
import net.sourceforge.pmd.lang.java.ast.ASTLambdaExpression;
import net.sourceforge.pmd.lang.java.ast.ASTLoopStatement;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.ast.ASTVariableId;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.ast.internal.JavaAstUtils;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.types.InvocationMatcher;
import net.sourceforge.pmd.lang.java.types.InvocationMatcher.CompoundInvocationMatcher;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;
import net.sourceforge.pmd.reporting.RuleContext;

/**
 * This rule is used to check whether user appends string directly instead of using StringBuffer or StringBuilder
 * @since 3.1 (as xpath) / 3.8 (as Java)
 */
public class UseStringBufferForStringAppendsRule extends AbstractJavaRulechainRule {

    private static final CompoundInvocationMatcher LOOP_METHOD_MATCHER = InvocationMatcher.parseAll(
        "java.lang.Iterable#forEach(_*)",
        "java.util.Iterator#forEachRemaining(_*)",
        "java.util.Spliterator#forEachRemaining(_*)",
        "java.util.Map#forEach(_*)",
        "java.util.stream.Stream#forEach(_*)",
        "java.util.stream.Stream#forEachOrdered(_*)",
        "java.util.stream.IntStream#forEach(_*)",
        "java.util.stream.IntStream#forEachOrdered(_*)",
        "java.util.stream.LongStream#forEach(_*)",
        "java.util.stream.LongStream#forEachOrdered(_*)",
        "java.util.stream.DoubleStream#forEach(_*)",
        "java.util.stream.DoubleStream#forEachOrdered(_*)"
    );

    public UseStringBufferForStringAppendsRule() {
        super(ASTVariableId.class);
    }

    @Override
    public Object visit(ASTVariableId node, Object data) {
        RuleContext ctx = (RuleContext) data;
        if (!TypeTestUtil.isA(String.class, node) || node.isForeachVariable()) {
            return null;
        }

        // Remember how often the variable has been used on the right hand side
        int usageCounter = 0;

        List<ASTNamedReferenceExpr> possibleViolations = new ArrayList<>();

        for (ASTNamedReferenceExpr usage : node.getLocalUsages()) {
            if ((node.isField() || node.isFormalParameter())
                && isNotWithinLoop(usage)) {
                // ignore if the field or formal parameter is *not* used within loops
                continue;
            }

            boolean isSimpleAssignment = false;
            if (usage.getParent() instanceof ASTAssignmentExpression) {
                ASTAssignmentExpression assignment = (ASTAssignmentExpression) usage.getParent();
                // it is either a compound (a += x)
                if (assignment.isCompound()) {
                    usageCounter++;
                }

                int usageOnRightHandSide =
                    JavaAstUtils.flattenOperands(assignment.getRightOperand())
                                .filterIs(ASTNamedReferenceExpr.class)
                                .filterMatching(ASTNamedReferenceExpr::getReferencedSym, node.getSymbol())
                                .count();

                // or maybe a append in some way (a = a + x)
                // or a combination (a += a + x)
                usageCounter += usageOnRightHandSide;

                isSimpleAssignment = !assignment.isCompound() && usageOnRightHandSide == 0;
            }

            if (usage.getAccessType() == AccessType.WRITE && !isSimpleAssignment) {
                if (isWithinLoop(usage)) {
                    // always report appends within a loop
                    ctx.addViolation(usage);
                } else {
                    possibleViolations.add(usage);
                }
            }
        }

        // only report, if it is used more than once
        // then all usage locations are reported
        if (usageCounter > 1) {
            possibleViolations.forEach(v -> ctx.addViolation(v));
        }

        return null;
    }

    private boolean isNotWithinLoop(Node name) {
        return !isWithinLoop(name);
    }

    static boolean isWithinLoop(Node name) {
        for (Node ancestor : name.ancestors()) {
            if (ancestor instanceof ASTLoopStatement) {
                return true;
            } else if (ancestor instanceof ASTLambdaExpression && isLambdaLoop((ASTLambdaExpression) ancestor)) {
                return true;
            } else if (ancestor instanceof ASTBodyDeclaration) {
                return false;
            }
        }
        return false;
    }

    static boolean isLambdaLoop(ASTLambdaExpression lambda) {
        JavaNode node = lambda;
        if (node.getParent() instanceof ASTCastExpression) {
            node = (JavaNode) node.getParent();
        }
        JavaNode argumentList = node.getParent();
        if (argumentList instanceof ASTArgumentList && argumentList.getParent() instanceof ASTMethodCall) {
            return LOOP_METHOD_MATCHER.anyMatch(argumentList.getParent());
        }
        return false;
    }
}

