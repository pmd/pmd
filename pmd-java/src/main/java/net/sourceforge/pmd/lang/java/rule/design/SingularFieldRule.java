/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.design;

import static net.sourceforge.pmd.lang.java.ast.JModifier.FINAL;
import static net.sourceforge.pmd.lang.java.ast.JModifier.STATIC;
import static net.sourceforge.pmd.util.CollectionUtil.setOf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr.ASTNamedReferenceExpr;
import net.sourceforge.pmd.lang.java.ast.ASTBodyDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTLambdaExpression;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.ast.AccessNode.Visibility;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.rule.internal.DataflowPass;
import net.sourceforge.pmd.lang.java.rule.internal.DataflowPass.ReachingDefinitionSet;
import net.sourceforge.pmd.lang.java.rule.internal.JavaPropertyUtil;
import net.sourceforge.pmd.lang.java.rule.internal.JavaRuleUtil;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.util.CollectionUtil;


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

    private static final PropertyDescriptor<List<String>> IGNORED_ANNOT_PROPERTY =
        JavaPropertyUtil.ignoredAnnotationsProperty(
            CollectionUtil.union(JavaRuleUtil.LOMBOK_ANNOTATIONS,
                                 setOf("lombok.experimental.Delegate",
                                       "lombok.EqualsAndHashCode"))
        );


    public SingularFieldRule() {
        super(ASTCompilationUnit.class, ASTFieldDeclaration.class);
        definePropertyDescriptor(IGNORED_ANNOT_PROPERTY);
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
            || node.hasModifiers(STATIC)
            || JavaRuleUtil.hasAnyAnnotation(enclosingType, getProperty(IGNORED_ANNOT_PROPERTY))
            || JavaRuleUtil.hasAnyAnnotation(node, getProperty(IGNORED_ANNOT_PROPERTY))) {
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
            && !varId.getModifiers().hasAny(STATIC, FINAL);
    }

    private boolean isSingularField(ASTAnyTypeDeclaration fieldOwner, ASTVariableDeclaratorId varId) {
        if (JavaRuleUtil.isNeverUsed(varId)) {
            return false;// don't report unused field
        }

        //Check usages for validity & group them by scope
        //They're valid if they don't escape the scope of their method, eg by being in a nested class or lambda
        Map<ASTBodyDeclaration, List<ASTNamedReferenceExpr>> usagesByScope = new HashMap<>();
        for (ASTNamedReferenceExpr usage : varId.getLocalUsages()) {
            if (usage.getEnclosingType() != fieldOwner || !JavaRuleUtil.isThisFieldAccess(usage)) {
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
