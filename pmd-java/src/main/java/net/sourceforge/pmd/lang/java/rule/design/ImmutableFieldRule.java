/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.design;

import static net.sourceforge.pmd.util.CollectionUtil.setOf;

import java.util.Set;
import java.util.function.Function;

import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr.ASTNamedReferenceExpr;
import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr.AccessType;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTLambdaExpression;
import net.sourceforge.pmd.lang.java.ast.ASTTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTVariableId;
import net.sourceforge.pmd.lang.java.ast.JModifier;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.ast.ModifierOwner.Visibility;
import net.sourceforge.pmd.lang.java.ast.internal.JavaAstUtils;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.rule.internal.DataflowPass;
import net.sourceforge.pmd.lang.java.rule.internal.DataflowPass.AssignmentEntry;
import net.sourceforge.pmd.lang.java.rule.internal.DataflowPass.DataflowResult;
import net.sourceforge.pmd.util.CollectionUtil;

public class ImmutableFieldRule extends AbstractJavaRulechainRule {

    private static final Set<String> INVALIDATING_CLASS_ANNOT =
        setOf(
            "lombok.Builder",
            "lombok.Data",
            "lombok.Setter",
            "lombok.Value"
        );

    private static final Set<String> INVALIDATING_FIELD_ANNOT =
        setOf(
            "lombok.Setter"
        );
    private static final Function<Object, JavaNode> INTERESTING_ANCESTOR =
        NodeStream.asInstanceOf(ASTLambdaExpression.class,
                                ASTTypeDeclaration.class,
                                ASTConstructorDeclaration.class);

    public ImmutableFieldRule() {
        super(ASTFieldDeclaration.class);
    }


    @Override
    public Object visit(ASTFieldDeclaration field, Object data) {
        ASTTypeDeclaration enclosingType = field.getEnclosingType();
        if (field.getEffectiveVisibility().isAtMost(Visibility.V_PRIVATE)
            && !field.getModifiers().hasAny(JModifier.VOLATILE, JModifier.STATIC, JModifier.FINAL)
            && !JavaAstUtils.hasAnyAnnotation(enclosingType, INVALIDATING_CLASS_ANNOT)
            && !JavaAstUtils.hasAnyAnnotation(field, INVALIDATING_FIELD_ANNOT)) {

            DataflowResult dataflow = DataflowPass.getDataflowResult(field.getRoot());

            outer:
            for (ASTVariableId varId : field.getVarIds()) {

                boolean hasWrite = false;
                for (ASTNamedReferenceExpr usage : varId.getLocalUsages()) {
                    if (usage.getAccessType() == AccessType.WRITE) {
                        hasWrite = true;

                        JavaNode enclosing = usage.ancestors().map(INTERESTING_ANCESTOR).first();
                        if (!(enclosing instanceof ASTConstructorDeclaration)
                            || enclosing.getEnclosingType() != enclosingType) {
                            continue outer; // written-to outside ctor
                        }
                    }
                }

                // we now know that the field is maybe not written to,
                // or maybe just inside constructors.

                boolean isBlank = varId.getInitializer() == null;

                if (!hasWrite && !isBlank) {
                    //todo this case may also handle static fields easily.
                    asCtx(data).addViolation(varId, varId.getName());
                } else if (hasWrite && defaultValueDoesNotReachEndOfCtor(dataflow, varId)) {
                    asCtx(data).addViolation(varId, varId.getName());
                }
            }

        }
        return null;
    }

    private boolean defaultValueDoesNotReachEndOfCtor(DataflowResult dataflow, ASTVariableId varId) {
        AssignmentEntry fieldDef = DataflowPass.getFieldDefinition(varId);
        // first assignments to the field
        Set<AssignmentEntry> killers = dataflow.getKillers(fieldDef);
        // no killer isFieldAssignmentAtEndOfCtor => the field is assigned on all code paths
        // no killer isReassignedOnSomeCodePath => the field is assigned at most once
        // => the field is assigned exactly once.
        return CollectionUtil.none(
            killers,
            killer -> killer.isFieldAssignmentAtEndOfCtor() || isReassignedOnSomeCodePath(dataflow, killer)
        );
    }

    private boolean isReassignedOnSomeCodePath(DataflowResult dataflow, AssignmentEntry anAssignment) {
        // Ie, return whether there exists an assignment that overwrites the given assignment,
        // and simultaneously is not unbound (=happens for sure). Unbound assignments are introduced by the
        // dataflow pass to help analysis, but are overly conservative. They represent the
        // "final value" of a field when a ctor ends, and also the value a field may have
        // been set to when an instance method is called (we don't know whether the method
        // actually sets anything, but we assume it does). The first case can be ignored
        // because we already check for that in the logic of this rule. The second case
        // can be ignored because we already made sure that the field is only written to
        // in ctors, but not in any instance method.
        Set<AssignmentEntry> killers = dataflow.getKillers(anAssignment);
        return CollectionUtil.any(killers, killer -> !killer.isUnbound());
    }
}
