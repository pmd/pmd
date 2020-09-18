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
        RelevantMethodSet relevantMethods = node.getDeclarations()
                                                .filterIs(ASTMethodDeclaration.class)
                                                .collect(RelevantMethodSet.collector(node.getSymbol()));

        Set<ASTMethodDeclaration> violatingMethods =
            SuperTypesEnumerator.ALL_STRICT_SUPERTYPES
                .stream(node.getTypeMirror())
                .flatMap(st -> st.streamDeclaredMethods(relevantMethods::isRelevant))
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
        private final Set<ASTMethodDeclaration> tracked = new LinkedHashSet<>();

        private final JClassSymbol site;

        private RelevantMethodSet(JClassSymbol site) {
            this.site = site;
        }

        static Collector<ASTMethodDeclaration, ?, RelevantMethodSet> collector(JClassSymbol site) {
            return Collector.of(
                () -> new RelevantMethodSet(site),
                RelevantMethodSet::addMethod,
                (map1, map2) -> {
                    throw new UnsupportedOperationException("Dont use a parallel stream");
                }
            );
        }

        boolean isRelevant(JMethodSymbol superMethod) {
            if (!TypeOps.isOverridableIn(superMethod, site)) {
                return false;
            }
            BitSet aritySet = map.get(superMethod.getSimpleName());
            return aritySet != null && aritySet.get(superMethod.getArity());
        }

        void addMethod(ASTMethodDeclaration m) {
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
            () -> new SuperMethodSet(relevant),
            SuperMethodSet::addMethod,
            (map1, map2) -> {
                throw new UnsupportedOperationException("Dont use a parallel stream");
            },
            sms -> sms.overriding
        );
    }

    private static final class SuperMethodSet {


        // set of methods declared with
        private final Set<ASTMethodDeclaration> overriding = new LinkedHashSet<>();
        private final RelevantMethodSet relevant;

        private SuperMethodSet(RelevantMethodSet relevant) {
            this.relevant = relevant;
        }

        void addMethod(JMethodSig superSig) {
            // we know the sig is relevant
            ASTMethodDeclaration subSig = null;
            for (ASTMethodDeclaration it : relevant.tracked) {
                if (TypeOps.areOverrideEquivalent(it.getGenericSignature(), superSig)) {
                    subSig = it;
                    break;
                }
            }
            if (subSig != null) {
                overriding.add(subSig);
                relevant.tracked.remove(subSig);
            }
        }
    }

}



