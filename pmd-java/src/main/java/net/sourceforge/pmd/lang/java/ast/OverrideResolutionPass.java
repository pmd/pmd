/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.BitSet;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import net.sourceforge.pmd.lang.java.symbols.JMethodSymbol;
import net.sourceforge.pmd.lang.java.symbols.table.internal.SuperTypesEnumerator;
import net.sourceforge.pmd.lang.java.types.JClassType;
import net.sourceforge.pmd.lang.java.types.JMethodSig;
import net.sourceforge.pmd.lang.java.types.TypeOps;


/**
 * Populates method declarations with the method they override.
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
final class OverrideResolutionPass {

    private OverrideResolutionPass() {

    }

    static void resolveOverrides(ASTAnyTypeDeclaration node) {
        // collect methods that may override another method (non private, non static)

        RelevantMethodSet relevantMethods = new RelevantMethodSet(node.getTypeMirror());

        for (ASTMethodDeclaration methodDecl : node.getDeclarations(ASTMethodDeclaration.class)) {
            relevantMethods.addIfRelevant(methodDecl);
        }

        if (relevantMethods.tracked.isEmpty()) {
            return;
        }

        // stream all methods of supertypes
        SuperTypesEnumerator.ALL_STRICT_SUPERTYPES
            .stream(node.getTypeMirror())
            // Filter down to those that may be overridden by one of the possible violations
            // This considers name, arity, and accessibility
            //                                      vvvvvvvvvvvvvvvvvvvvvvvvvvv
            .flatMap(st -> st.streamDeclaredMethods(relevantMethods::isRelevant))
            // For those methods, a simple override-equivalence check is enough,
            // because we already know they're accessible, and declared in a supertype
            .forEach(relevantMethods::findMethodOverridingThisSig);
    }


    /**
     * This does a prefilter, so that we only collect methods of supertypes
     * that may be overridden by a sub method. For a method to be potentially
     * a super method, it must have same arity
     */
    private static final class RelevantMethodSet {

        // name to considered arities
        private final Map<String, BitSet> map = new HashMap<>();
        // nodes that may be violations
        private final Set<ASTMethodDeclaration> tracked = new LinkedHashSet<>();

        private final JClassType site;

        private RelevantMethodSet(JClassType site) {
            this.site = site;
        }


        // add a method if it may be overriding another
        // this builds the data structure for isRelevant to work
        void addIfRelevant(ASTMethodDeclaration m) {
            if (m.getModifiers().hasAny(JModifier.STATIC, JModifier.PRIVATE)) {
                // cannot override anything
                return;
            } else if (m.isAnnotationPresent(Override.class)) {
                // will be overwritten if we find it
                m.setOverriddenMethod(m.getTypeSystem().UNRESOLVED_METHOD);
            }
            // then add it
            BitSet aritySet = map.computeIfAbsent(m.getName(), n -> new BitSet(m.getArity() + 1));
            aritySet.set(m.getArity());
            tracked.add(m);
        }

        // we use this to only consider methods that may produce a violation,
        // among the supertype methods
        boolean isRelevant(JMethodSymbol superMethod) {
            if (!TypeOps.isOverridableIn(superMethod, site.getSymbol())) {
                return false;
            }
            BitSet aritySet = map.get(superMethod.getSimpleName());
            return aritySet != null && aritySet.get(superMethod.getArity());
        }

        // if the superSig, which comes from a supertype, is overridden
        // by a relevant method, set the overridden method.
        void findMethodOverridingThisSig(JMethodSig superSig) {
            ASTMethodDeclaration subSig = null;
            for (ASTMethodDeclaration it : tracked) {
                // note: we don't use override-equivalence, the definition
                // of an override uses the concept of sub-signature instead,
                // which is slightly different. We could also use TypeOps.overrides
                // but at this point we already know much of what that method checks.
                // https://docs.oracle.com/javase/specs/jls/se15/html/jls-8.html#jls-8.4.8.1
                if (TypeOps.isSubSignature(it.getGenericSignature(), superSig)) {
                    subSig = it;
                    // we assume there is a single relevant method that may match,
                    // otherwise it would be a compile-time error
                    break;
                }
            }
            if (subSig != null) {
                subSig.setOverriddenMethod(superSig);
                tracked.remove(subSig); // speedup the check for later
            }
        }
    }
}
