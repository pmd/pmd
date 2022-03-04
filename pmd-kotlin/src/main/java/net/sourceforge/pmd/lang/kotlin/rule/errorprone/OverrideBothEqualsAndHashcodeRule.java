/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.kotlin.rule.errorprone;

import java.util.List;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.kotlin.AbstractKotlinRule;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtClassDeclaration;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtClassMemberDeclaration;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtClassMemberDeclarations;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtDeclaration;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtFunctionDeclaration;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtFunctionValueParameter;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtFunctionValueParameters;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtModifiers;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtSimpleIdentifier;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinTerminalNode;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinVisitor;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinVisitorBase;

public class OverrideBothEqualsAndHashcodeRule extends AbstractKotlinRule {

    private static final Visitor INSTANCE = new Visitor();

    @Override
    public KotlinVisitor<RuleContext, ?> buildVisitor() {
        return INSTANCE;
    }

    private static class Visitor extends KotlinVisitorBase<RuleContext, Void> {
        @Override
        public Void visitClassMemberDeclarations(KtClassMemberDeclarations node, RuleContext data) {
            List<KtFunctionDeclaration> functions = node.children(KtClassMemberDeclaration.class)
                .children(KtDeclaration.class)
                .children(KtFunctionDeclaration.class)
                .toList();

            boolean hasEqualMethod = functions.stream().filter(this::isEqualsMethod).count() == 1L;
            boolean hasHashCodeMethod = functions.stream().filter(this::isHashCodeMethod).count() == 1L;

            if (hasEqualMethod ^ hasHashCodeMethod) {
                data.addViolation(node.ancestors(KtClassDeclaration.class).first());
            }

            return super.visitClassMemberDeclarations(node, data);
        }

        private boolean isEqualsMethod(KtFunctionDeclaration fun) {
            String name = getFunctionName(fun);
            int arity = fun.children(KtFunctionValueParameters.class).children(KtFunctionValueParameter.class).count();
            return "equals".equals(name) && hasOverrideModifier(fun) && arity == 1;
        }

        private boolean isHashCodeMethod(KtFunctionDeclaration fun) {
            String name = getFunctionName(fun);
            int arity = fun.children(KtFunctionValueParameters.class).children(KtFunctionValueParameter.class).count();
            return "hashCode".equals(name) && hasOverrideModifier(fun) && arity == 0;
        }

        private String getFunctionName(KtFunctionDeclaration fun) {
            return fun.children(KtSimpleIdentifier.class).children(KotlinTerminalNode.class).first().getText();
        }

        private boolean hasOverrideModifier(KtFunctionDeclaration fun) {
            return fun.children(KtModifiers.class).descendants(KotlinTerminalNode.class)
                    .any(t -> "override".equals(t.getText()));
        }
    }
}
