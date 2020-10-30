/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.design;

import static net.sourceforge.pmd.properties.PropertyFactory.booleanProperty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr.ASTNamedReferenceExpr;
import net.sourceforge.pmd.lang.java.ast.ASTBodyDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTLambdaExpression;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.ast.AccessNode.Visibility;
import net.sourceforge.pmd.lang.java.ast.JModifier;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.rule.AbstractLombokAwareRule;
import net.sourceforge.pmd.lang.java.rule.internal.DataflowPass;
import net.sourceforge.pmd.lang.java.rule.internal.DataflowPass.ReachingDefinitionSet;
import net.sourceforge.pmd.lang.java.rule.internal.JavaRuleUtil;
import net.sourceforge.pmd.lang.rule.RuleTargetSelector;
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
public class SingularFieldRule extends AbstractLombokAwareRule {

    // note: these properties are ignored now.

    /**
     * Restore old behavior by setting both properties to true, which will
     * result in many false positives
     */
    private static final PropertyDescriptor<Boolean> CHECK_INNER_CLASSES =
        booleanProperty("checkInnerClasses")
            .defaultValue(false)
            .desc("Check inner classes")
            .build();
    private static final PropertyDescriptor<Boolean> DISALLOW_NOT_ASSIGNMENT =
        booleanProperty("disallowNotAssignment")
            .defaultValue(false)
            .desc("Disallow violations where the first usage is not an assignment")
            .build();


    public SingularFieldRule() {
        definePropertyDescriptor(CHECK_INNER_CLASSES);
        definePropertyDescriptor(DISALLOW_NOT_ASSIGNMENT);
    }

    @Override
    protected @NonNull RuleTargetSelector buildTargetSelector() {
        return RuleTargetSelector.forTypes(ASTCompilationUnit.class, ASTFieldDeclaration.class);
    }

    @Override
    protected Collection<String> defaultSuppressionAnnotations() {
        Collection<String> defaultValues = new ArrayList<>(super.defaultSuppressionAnnotations());
        defaultValues.add("lombok.experimental.Delegate");
        defaultValues.add("lombok.EqualsAndHashCode");
        return defaultValues;
    }

    @Override
    public Object visit(ASTCompilationUnit node, Object data) {
        DataflowPass.ensureProcessed(node);
        return data;
    }

    @Override
    public Object visit(ASTFieldDeclaration node, Object data) {
        ASTAnyTypeDeclaration enclosingType = node.getEnclosingType();
        if (node.getVisibility() != Visibility.V_PRIVATE
            || node.hasModifiers(JModifier.STATIC)
            || hasIgnoredAnnotation(enclosingType)
            || hasIgnoredAnnotation(node)) {
            return data;
        }

        for (ASTVariableDeclaratorId varId : node.getVarIds()) {
            if (mayBeSingular(varId) && isSingularField(enclosingType, varId)) {
                addViolation(data, varId, varId.getName());
            }
        }

        return data;
    }

    public static boolean mayBeSingular(ASTVariableDeclaratorId varId) {
        return varId.getEffectiveVisibility().isAtMost(Visibility.V_PRIVATE)
            && !varId.isStatic()
            && !varId.isFinal();
    }

    private boolean isSingularField(ASTAnyTypeDeclaration fieldOwner, ASTVariableDeclaratorId varId) {
        List<ASTNamedReferenceExpr> usages = varId.getUsages();
        if (usages.isEmpty()) {
            return false;// don't report unused field
        }

        Map<ASTBodyDeclaration, List<ASTNamedReferenceExpr>> usagesByScope = new HashMap<>();
        for (ASTNamedReferenceExpr usage : usages) {
            if (usage.getEnclosingType() != fieldOwner
                || !JavaRuleUtil.isThisFieldAccess(usage)) {
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
            if (method != null && !usagesDontObserveValueBeforeMethodCall(usagesByScope.get(method))) {
                return false;
            }
        }

        return true;
    }

    private @Nullable ASTBodyDeclaration getEnclosingBodyDecl(JavaNode stop, ASTNamedReferenceExpr usage) {
        return usage.ancestors()
                    .takeWhile(it -> it != stop)
                    .first(ASTBodyDeclaration.class);
    }

    private boolean hasEnclosingLambda(JavaNode stop, ASTNamedReferenceExpr usage) {
        return usage.ancestors()
                    .takeWhile(it -> it != stop)
                    .any(it -> it instanceof ASTLambdaExpression);
    }

    private boolean usagesDontObserveValueBeforeMethodCall(List<ASTNamedReferenceExpr> usages) {
        for (ASTNamedReferenceExpr usage : usages) {
            ReachingDefinitionSet reaching = DataflowPass.getReachingDefinitions(usage);
            if (reaching != null && reaching.containsInitialFieldValue()) {
                return false;
            }
        }
        return true;
    }
}
