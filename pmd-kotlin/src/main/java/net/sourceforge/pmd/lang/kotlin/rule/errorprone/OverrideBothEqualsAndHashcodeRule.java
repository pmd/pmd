/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.kotlin.rule.errorprone;

import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.kotlin.AbstractKotlinRule;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtClassDeclaration;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtClassMemberDeclaration;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtClassMemberDeclarations;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtDeclaration;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtFunctionDeclaration;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinTerminalNode;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinVisitor;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinVisitorBase;
import net.sourceforge.pmd.lang.rule.RuleTargetSelector;
import net.sourceforge.pmd.reporting.RuleContext;

public class OverrideBothEqualsAndHashcodeRule extends AbstractKotlinRule {

    private static final Visitor INSTANCE = new Visitor();

    @Override
    public KotlinVisitor<RuleContext, ?> buildVisitor() {
        return INSTANCE;
    }

    @Override
    protected @NonNull RuleTargetSelector buildTargetSelector() {
        return RuleTargetSelector.forTypes(KtClassMemberDeclarations.class);
    }

    private static final class Visitor extends KotlinVisitorBase<RuleContext, Void> {
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
            int arity = getArity(fun);
            return "equals".equals(name) && hasOverrideModifier(fun) && arity == 1;
        }

        private boolean isHashCodeMethod(KtFunctionDeclaration fun) {
            String name = getFunctionName(fun);
            int arity = getArity(fun);
            return "hashCode".equals(name) && hasOverrideModifier(fun) && arity == 0;
        }

        private String getFunctionName(KtFunctionDeclaration fun) {
            return fun.simpleIdentifier().children(KotlinTerminalNode.class).first().getText();
        }

        private boolean hasOverrideModifier(KtFunctionDeclaration fun) {
            return fun.modifiers().descendants(KotlinTerminalNode.class)
                    .any(t -> "override".equals(t.getText()));
        }

        private int getArity(KtFunctionDeclaration fun) {
            return fun.functionValueParameters().functionValueParameter().size();
        }
    }
}
