/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import static net.sourceforge.pmd.util.CollectionUtil.setOf;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.java.ast.ASTAnnotation;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodReference;
import net.sourceforge.pmd.lang.java.ast.ASTModifierList;
import net.sourceforge.pmd.lang.java.ast.ASTStringLiteral;
import net.sourceforge.pmd.lang.java.ast.AccessNode.Visibility;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.ast.MethodUsage;
import net.sourceforge.pmd.lang.java.ast.internal.PrettyPrintingUtil;
import net.sourceforge.pmd.lang.java.rule.AbstractIgnoredAnnotationRule;
import net.sourceforge.pmd.lang.java.symbols.JExecutableSymbol;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;
import net.sourceforge.pmd.util.CollectionUtil;

/**
 * This rule detects private methods, that are not used and can therefore be
 * deleted.
 */
public class UnusedPrivateMethodRule extends AbstractIgnoredAnnotationRule {

    private static final Set<String> SERIALIZATION_METHODS =
        setOf("readObject", "writeObject", "readResolve", "writeReplace");

    @Override
    protected Collection<String> defaultSuppressionAnnotations() {
        return Collections.singletonList("java.lang.Deprecated");
    }

    @Override
    public Object visit(ASTCompilationUnit file, Object param) {
        // We do three traversals:
        // - one to find methods referenced by Junit5 MethodSource annotations
        // - one to find the "interesting methods", ie those that may be violations
        // - another to find the possible usages. We only try to resolve
        //   method calls/method refs that may refer to a method in the
        //   first set, ie, not every call in the file.

        Set<String> methodsUsedByAnnotations = file.descendants(ASTMethodDeclaration.class)
            .children(ASTModifierList.class)
            .children(ASTAnnotation.class)
            .filter(t -> TypeTestUtil.isA("org.junit.jupiter.params.provider.MethodSource", t))
            .descendants(ASTStringLiteral.class)
            .toStream()
            .map(ASTStringLiteral::getConstValue)
            .collect(Collectors.toSet());

        Map<String, Set<ASTMethodDeclaration>> consideredNames =
            file.descendants(ASTMethodDeclaration.class)
                .crossFindBoundaries()
                // get methods whose usages are all in this file
                // TODO we could use getEffectiveVisibility here, but we need to consider overrides then.
                .filter(it -> it.getVisibility() == Visibility.V_PRIVATE)
                .filter(it -> !hasIgnoredAnnotation(it)
                        && !hasExcludedName(it) 
                        && !(it.getArity() == 0 && methodsUsedByAnnotations.contains(it.getName())))
                .toStream()
                .collect(Collectors.groupingBy(ASTMethodDeclaration::getName, HashMap::new, CollectionUtil.toMutableSet()));

        file.descendants()
            .crossFindBoundaries()
            .map(NodeStream.<MethodUsage>asInstanceOf(ASTMethodCall.class, ASTMethodReference.class))
            .forEach(ref -> {
                String methodName = ref.getMethodName();
                // the considered names might be mutated during the traversal
                if (!consideredNames.containsKey(methodName)) {
                    return;
                }
                JExecutableSymbol sym;
                if (ref instanceof ASTMethodCall) {
                    sym = ((ASTMethodCall) ref).getMethodType().getSymbol();
                } else if (ref instanceof ASTMethodReference) {
                    sym = ((ASTMethodReference) ref).getReferencedMethod().getSymbol();
                } else {
                    return;
                }

                JavaNode reffed = sym.tryGetNode();
                if (reffed instanceof ASTMethodDeclaration
                    && ref.ancestors(ASTMethodDeclaration.class).first() != reffed) {
                    // remove from set, but only if it is called outside of itself
                    Set<ASTMethodDeclaration> remainingUnused = consideredNames.get(methodName);
                    if (remainingUnused != null
                        && remainingUnused.remove(reffed) // note: side-effect
                        && remainingUnused.isEmpty()) {
                        consideredNames.remove(methodName); // clear this name
                    }
                }
            });

        // those that remain are unused
        consideredNames.forEach((name, unused) -> {
            for (ASTMethodDeclaration m : unused) {
                addViolation(param, m, PrettyPrintingUtil.displaySignature(m));
            }
        });

        return null;
    }

    private boolean hasExcludedName(ASTMethodDeclaration node) {
        return SERIALIZATION_METHODS.contains(node.getName());
    }
}
