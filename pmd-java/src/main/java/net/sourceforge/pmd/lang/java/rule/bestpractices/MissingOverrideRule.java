/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import java.util.BitSet;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collector;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.JModifier;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.ast.internal.PrettyPrintingUtil;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JMethodSymbol;
import net.sourceforge.pmd.lang.java.symbols.table.internal.SuperTypesEnumerator;
import net.sourceforge.pmd.lang.java.types.JMethodSig;
import net.sourceforge.pmd.lang.java.types.TypeOps;
import net.sourceforge.pmd.lang.rule.RuleTargetSelector;


/**
 * Flags missing @Override annotations.
 *
 * @author Clément Fournier
 * @since 6.2.0
 */
public class MissingOverrideRule extends AbstractJavaRule {

    @Override
    protected @NonNull RuleTargetSelector buildTargetSelector() {
        return RuleTargetSelector.forTypes(ASTAnyTypeDeclaration.class);
    }

    @Override
    public Object visitJavaNode(JavaNode node, Object data) {
        visitTypeDecl((ASTAnyTypeDeclaration) node, (RuleContext) data);
        return data;
    }

    private void visitTypeDecl(ASTAnyTypeDeclaration node, RuleContext data) {
        // collect methods that may be violations, ie:
        // - may override another method (non private, non static)
        // - not already annotated @Override

        RelevantMethodSet relevantMethods = new RelevantMethodSet(node.getSymbol());

        for (ASTMethodDeclaration methodDecl : node.getDeclarations().filterIs(ASTMethodDeclaration.class)) {
            relevantMethods.addIfRelevant(methodDecl);
        }

        if (relevantMethods.tracked.isEmpty()) {
            return;
        }

        Set<ASTMethodDeclaration> violatingMethods =
            // stream all methods of supertypes
            SuperTypesEnumerator.ALL_STRICT_SUPERTYPES
                .stream(node.getTypeMirror())
                // Filter down to those that may be overridden by one of the possible violations
                // This considers name, arity, and accessibility
                //                                      vvvvvvvvvvvvvvvvvvvvvvvvvvv
                .flatMap(st -> st.streamDeclaredMethods(relevantMethods::isRelevant))
                // For those methods, a simple override-equivalence check is enough,
                // because we already know they're accessible
                .collect(collectOverriddenRelevantMethods(relevantMethods));

        for (ASTMethodDeclaration violatingMethod : violatingMethods) {
            addViolation(data, violatingMethod, new Object[] {PrettyPrintingUtil.displaySignature(violatingMethod)});
        }
    }

    /**
     * This does a prefilter, so that we only collect methods of supertypes
     * that may be overridden by a sub method. For a method to be potentially
     * a super method, it must have same arity
     */
    private static final class RelevantMethodSet {

        // name to arity
        private final Map<String, BitSet> map = new HashMap<>();
        // note: this is mutated by the other collector
        private final Set<ASTMethodDeclaration> tracked = new LinkedHashSet<>();

        private final JClassSymbol site;

        private RelevantMethodSet(JClassSymbol site) {
            this.site = site;
        }

        boolean isRelevant(JMethodSymbol superMethod) {
            if (!TypeOps.isOverridableIn(superMethod, site)) {
                return false;
            }
            BitSet aritySet = map.get(superMethod.getSimpleName());
            return aritySet != null && aritySet.get(superMethod.getArity());
        }

        void addIfRelevant(ASTMethodDeclaration m) {
            if (m.isAnnotationPresent(Override.class)
                || m.getModifiers().hasAny(JModifier.STATIC, JModifier.PRIVATE)) {
                return;
            }
            BitSet aritySet = map.computeIfAbsent(m.getName(), n -> new BitSet(m.getArity() + 1));
            aritySet.set(m.getArity());
            tracked.add(m);
        }
    }


    static Collector<JMethodSig, ?, Set<ASTMethodDeclaration>> collectOverriddenRelevantMethods(RelevantMethodSet relevant) {
        return Collector.of(
            () -> new OverridingMethodCollector(relevant),
            OverridingMethodCollector::addMethod,
            (map1, map2) -> {
                throw new UnsupportedOperationException("Dont use a parallel stream");
            },
            sms -> sms.overriding
        );
    }

    private static final class OverridingMethodCollector {

        // set of violations
        private final Set<ASTMethodDeclaration> overriding = new LinkedHashSet<>();
        private final RelevantMethodSet relevant;

        private OverridingMethodCollector(RelevantMethodSet relevant) {
            this.relevant = relevant;
        }

        void addMethod(JMethodSig superSig) {
            ASTMethodDeclaration subSig = null;
            for (ASTMethodDeclaration it : relevant.tracked) {
                if (TypeOps.areOverrideEquivalent(it.getGenericSignature(), superSig)) {
                    subSig = it;
                    // we assume there is a single relevant method that may match,
                    // otherwise it would be a compile-time error
                    break;
                }
            }
            if (subSig != null) {
                overriding.add(subSig);
                relevant.tracked.remove(subSig); // speedup the check for later
            }
        }
    }

}



