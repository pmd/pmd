/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.rule.bestpractices;

import java.util.HashSet;
import java.util.Set;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.kotlin.AbstractKotlinRule;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtFunctionDeclaration;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtLambdaLiteral;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtLambdaParameter;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtLambdaParameters;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtPropertyDeclaration;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtVariableDeclaration;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinVisitor;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinVisitorBase;
import net.sourceforge.pmd.lang.kotlin.util.KotlinAstUtil;
import net.sourceforge.pmd.lang.rule.RuleTargetSelector;
import net.sourceforge.pmd.reporting.RuleContext;

/**
 * Detects local variable declarations and lambda explicit parameters that
 * shadow a parameter of any enclosing named function.
 *
 * <p>Param names are collected once per function visit. Each property and
 * lambda parameter directly inside that function (checked via
 * {@link KotlinAstUtil#isWithin}) is then compared against the combined set
 * of params from that function and all its enclosing functions.
 */
public class LocalVariableShadowsParameterRule extends AbstractKotlinRule {

    private static final Visitor INSTANCE = new Visitor();

    @Override
    public KotlinVisitor<RuleContext, ?> buildVisitor() {
        return INSTANCE;
    }

    @Override
    protected @NonNull RuleTargetSelector buildTargetSelector() {
        return RuleTargetSelector.forTypes(KtFunctionDeclaration.class);
    }

    private static final class Visitor extends KotlinVisitorBase<RuleContext, Void> {

        @Override
        public Void visitFunctionDeclaration(KtFunctionDeclaration func, RuleContext data) {
            Set<String> shadowableParams = collectShadowableParams(func);
            if (shadowableParams.isEmpty()) {
                return null;
            }
            checkPropertyDeclarations(func, shadowableParams, data);
            checkLambdaParameters(func, shadowableParams, data);
            return null;
        }

        private static Set<String> collectShadowableParams(KtFunctionDeclaration func) {
            Set<String> params = new HashSet<>(KotlinAstUtil.collectParamNames(func));
            for (KtFunctionDeclaration outer : func.ancestors(KtFunctionDeclaration.class)) {
                params.addAll(KotlinAstUtil.collectParamNames(outer));
            }
            return params;
        }

        private static void checkPropertyDeclarations(KtFunctionDeclaration func,
                                                      Set<String> shadowableParams,
                                                      RuleContext data) {
            for (KtPropertyDeclaration propDecl : func.descendants(KtPropertyDeclaration.class)) {
                if (!KotlinAstUtil.isWithin(propDecl, KtFunctionDeclaration.class, func)) {
                    continue;
                }
                KtVariableDeclaration varDecl = propDecl.variableDeclaration();
                if (varDecl == null) {
                    continue;
                }
                String name = KotlinAstUtil.textOf(varDecl.simpleIdentifier());
                if (name != null && shadowableParams.contains(name)) {
                    data.addViolation(propDecl, name);
                }
            }
        }

        private static void checkLambdaParameters(KtFunctionDeclaration func,
                                                  Set<String> shadowableParams,
                                                  RuleContext data) {
            for (KtLambdaLiteral lambda : func.descendants(KtLambdaLiteral.class)) {
                if (!KotlinAstUtil.isWithin(lambda, KtFunctionDeclaration.class, func)) {
                    continue;
                }
                KtLambdaParameters lambdaParams = lambda.lambdaParameters();
                if (lambdaParams == null) {
                    continue;
                }
                for (KtLambdaParameter param : lambdaParams.lambdaParameter()) {
                    KtVariableDeclaration varDecl = param.variableDeclaration();
                    if (varDecl == null) {
                        continue;
                    }
                    String name = KotlinAstUtil.textOf(varDecl.simpleIdentifier());
                    if (name != null && shadowableParams.contains(name)) {
                        data.addViolation(param, name);
                    }
                }
            }
        }
    }
}
