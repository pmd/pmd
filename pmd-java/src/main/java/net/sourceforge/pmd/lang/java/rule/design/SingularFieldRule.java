/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.design;

import static net.sourceforge.pmd.util.CollectionUtil.setOf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr.ASTNamedReferenceExpr;
import net.sourceforge.pmd.lang.java.ast.ASTBodyDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTInitializer;
import net.sourceforge.pmd.lang.java.ast.ASTLambdaExpression;
import net.sourceforge.pmd.lang.java.ast.ASTTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTVariableId;
import net.sourceforge.pmd.lang.java.ast.JModifier;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.ast.ModifierOwner;
import net.sourceforge.pmd.lang.java.ast.ModifierOwner.Visibility;
import net.sourceforge.pmd.lang.java.ast.internal.JavaAstUtils;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.rule.internal.DataflowPass;
import net.sourceforge.pmd.lang.java.rule.internal.DataflowPass.DataflowResult;
import net.sourceforge.pmd.lang.java.rule.internal.DataflowPass.ReachingDefinitionSet;
import net.sourceforge.pmd.lang.java.rule.internal.JavaPropertyUtil;
import net.sourceforge.pmd.properties.PropertyDescriptor;


/**
 * A singular field is a field that may be converted to a local variable.
 * This means, that in every method the field is used, there is no path
 * that uses the value that the field has before the method is called.
 * In other words, the field is overwritten before any read.
 *
 * @author Eric Olander
 * @author Wouter Zelle
 * @author Clément Fournier
 * @since Created on April 17, 2005, 9:49 PM
 */
public class SingularFieldRule extends AbstractJavaRulechainRule {

    private static final Set<String> INVALIDATING_CLASS_ANNOT = setOf(
        "lombok.Builder",
        "lombok.EqualsAndHashCode",
        "lombok.Getter",
        "lombok.Setter",
        "lombok.Data",
        "lombok.Value"
    );

    private static final PropertyDescriptor<List<String>> IGNORED_FIELD_ANNOTATIONS =
        JavaPropertyUtil.ignoredAnnotationsDescriptor(
            "lombok.Setter",
            "lombok.Getter",
            "java.lang.Deprecated",
            "lombok.experimental.Delegate",
            "javafx.fxml.FXML"
        );

    public SingularFieldRule() {
        super(ASTTypeDeclaration.class);
        definePropertyDescriptor(IGNORED_FIELD_ANNOTATIONS);
    }

    @Override
    public Object visitJavaNode(JavaNode node, Object data) {
        ASTTypeDeclaration enclosingType = (ASTTypeDeclaration) node;
        if (JavaAstUtils.hasAnyAnnotation(enclosingType, INVALIDATING_CLASS_ANNOT)) {
            return null;
        }

        DataflowResult dataflow = null;
        for (ASTFieldDeclaration fieldDecl : enclosingType.getDeclarations(ASTFieldDeclaration.class)) {
            if (!isPrivateNotFinal(fieldDecl)
                || JavaAstUtils.hasAnyAnnotation(fieldDecl, getProperty(IGNORED_FIELD_ANNOTATIONS))) {
                continue;
            }
            for (ASTVariableId varId : fieldDecl.getVarIds()) {
                if (dataflow == null) { //compute lazily
                    dataflow = DataflowPass.getDataflowResult(node.getRoot());
                }
                if (isSingularField(enclosingType, varId, dataflow)) {
                    asCtx(data).addViolation(varId, varId.getName());
                }
            }
        }
        return null;
    }

    /**
     * This method is only relevant for this rule. It will be removed in the future.
     *
     * @deprecated This method will be removed. Don't use it.
     */
    @Deprecated //(since = "7.1.0", forRemoval = true)
    public static boolean mayBeSingular(ModifierOwner varId) {
        return isPrivateNotFinal(varId);
    }

    private static boolean isPrivateNotFinal(ModifierOwner varId) {
        return varId.getEffectiveVisibility().isAtMost(Visibility.V_PRIVATE)
                // We ignore final variables for a reason:
                // - if they're static they are there to share a value and that is not a mistake
                // - if they're not static then if this rule matches, then so does FinalFieldCouldBeStatic,
                // and that rule has the better fix.
                && !varId.hasModifiers(JModifier.FINAL);
    }

    private boolean isSingularField(ASTTypeDeclaration fieldOwner, ASTVariableId varId, DataflowResult dataflow) {
        if (JavaAstUtils.isNeverUsed(varId)) {
            return false; // don't report unused field
        }

        boolean isStaticField = varId.isStatic();
        //Check usages for validity & group them by scope
        //They're valid if they don't escape the scope of their method, eg by being in a nested class or lambda
        Map<ASTBodyDeclaration, List<ASTNamedReferenceExpr>> usagesByScope = new HashMap<>();
        for (ASTNamedReferenceExpr usage : varId.getLocalUsages()) {
            if (usage.getEnclosingType() != fieldOwner
                || !isStaticField && !JavaAstUtils.isThisFieldAccess(usage)) {
                return false; // give up
            }
            ASTBodyDeclaration enclosing = getEnclosingBodyDecl(fieldOwner, usage);
            if (hasEnclosingLambda(enclosing, usage)) {
                return false;
            }
            usagesByScope.computeIfAbsent(enclosing, k -> new ArrayList<>()).add(usage);
        }

        // the field is singular if it is used as a local var in every method.
        for (ASTBodyDeclaration method : usagesByScope.keySet()) {
            if (method != null && usagesObserveValueBeforeMethodCall(usagesByScope.get(method), dataflow)) {
                return false;
            }
        }

        return true;
    }

    private @Nullable ASTBodyDeclaration getEnclosingBodyDecl(ASTTypeDeclaration enclosingType, ASTNamedReferenceExpr usage) {
        ASTBodyDeclaration decl = usage.ancestors()
                                       .takeWhile(it -> it != enclosingType)
                                       .first(ASTBodyDeclaration.class);
        if (decl instanceof ASTFieldDeclaration
            || decl instanceof ASTInitializer) {
            // then the usage is logically part of the ctors.
            return enclosingType;
        }
        return decl;
    }

    private boolean hasEnclosingLambda(JavaNode stop, ASTNamedReferenceExpr usage) {
        return usage.ancestors()
                    .takeWhile(it -> it != stop)
                    .any(it -> it instanceof ASTLambdaExpression);
    }

    private boolean usagesObserveValueBeforeMethodCall(List<ASTNamedReferenceExpr> usages, DataflowResult dataflow) {
        for (ASTNamedReferenceExpr usage : usages) {
            if (JavaAstUtils.isVarAccessStrictlyWrite(usage)) {
                continue;
            }
            ReachingDefinitionSet reaching = dataflow.getReachingDefinitions(usage);
            if (reaching.containsInitialFieldValue()) {
                return true;
            }
        }
        return false;
    }
}
