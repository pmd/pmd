/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.design;

import static net.sourceforge.pmd.util.CollectionUtil.setOf;

import java.util.Set;

import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr.ASTNamedReferenceExpr;
import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr.AccessType;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTLambdaExpression;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.ast.AccessNode.Visibility;
import net.sourceforge.pmd.lang.java.ast.JModifier;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
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

    public ImmutableFieldRule() {
        super(ASTFieldDeclaration.class);
    }


    @Override
    public Object visit(ASTFieldDeclaration field, Object data) {
        ASTAnyTypeDeclaration enclosingType = field.getEnclosingType();
        if (field.getEffectiveVisibility().isAtMost(Visibility.V_PRIVATE)
            && !field.getModifiers().hasAny(JModifier.VOLATILE, JModifier.STATIC, JModifier.FINAL)
            && !JavaAstUtils.hasAnyAnnotation(enclosingType, INVALIDATING_CLASS_ANNOT)
            && !JavaAstUtils.hasAnyAnnotation(field, INVALIDATING_FIELD_ANNOT)) {

            DataflowResult dataflow = DataflowPass.getDataflowResult(field.getRoot());

            outer:
            for (ASTVariableDeclaratorId varId : field.getVarIds()) {

                boolean hasWrite = false;
                for (ASTNamedReferenceExpr usage : varId.getLocalUsages()) {
                    if (usage.getAccessType() == AccessType.WRITE) {
                        hasWrite = true;

                        JavaNode enclosing = usage.ancestors().map(NodeStream.asInstanceOf(ASTLambdaExpression.class, ASTAnyTypeDeclaration.class, ASTConstructorDeclaration.class)).first();
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

    private boolean defaultValueDoesNotReachEndOfCtor(DataflowResult dataflow, ASTVariableDeclaratorId varId) {
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
        Set<AssignmentEntry> killers = dataflow.getKillers(anAssignment);
        return CollectionUtil.any(killers, killer -> !killer.isFieldAssignmentAtEndOfCtor());
    }
}
