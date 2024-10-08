/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.java.ast.*;
import net.sourceforge.pmd.lang.java.ast.ModifierOwner.Visibility;
import net.sourceforge.pmd.lang.java.ast.internal.PrettyPrintingUtil;
import net.sourceforge.pmd.lang.java.rule.internal.AbstractIgnoredAnnotationRule;
import net.sourceforge.pmd.lang.java.symbols.JExecutableSymbol;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;
import net.sourceforge.pmd.util.CollectionUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.sourceforge.pmd.util.CollectionUtil.listOf;
import static net.sourceforge.pmd.util.CollectionUtil.setOf;

/**
 * This rule detects private methods, that are not used and can therefore be
 * deleted.
 */
public class UnusedPrivateMethodRule extends AbstractIgnoredAnnotationRule {

    private static final Set<String> SERIALIZATION_METHODS =
            setOf("readObject", "writeObject", "readResolve", "writeReplace");

    @Override
    protected Collection<String> defaultSuppressionAnnotations() {
        return listOf(
                "java.lang.Deprecated",
                "jakarta.annotation.PostConstruct",
                "jakarta.annotation.PreDestroy"
        );
    }

    @Override
    public Object visit(ASTCompilationUnit file, Object param) {
        Set<String> methodsUsedByAnnotations = methodsUsedByAnnotations(file);
        Map<String, Set<ASTMethodDeclaration>> consideredNames = consideredNames(file, methodsUsedByAnnotations);
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
                asCtx(param).addViolation(m, PrettyPrintingUtil.displaySignature(m));
            }
        });
        return null;
    }

    private HashMap<String, Set<ASTMethodDeclaration>> consideredNames(final ASTCompilationUnit file,
                                                                       final Set<String> methodsUsedByAnnotations) {
        return file.descendants(ASTMethodDeclaration.class)
                .crossFindBoundaries()
                // get methods whose usages are all in this file
                // TODO we could use getEffectiveVisibility here, but we need to consider overrides then.
                .filter(it -> it.getVisibility() == Visibility.V_PRIVATE)
                .filter(it -> !hasIgnoredAnnotation(it)
                        && !hasExcludedName(it)
                        && !(it.getArity() == 0 && methodsUsedByAnnotations.contains(it.getName())))
                .toStream()
                .collect(Collectors.groupingBy(ASTMethodDeclaration::getName, HashMap::new,
                        CollectionUtil.toMutableSet()));
    }

    private static Set<String> methodsUsedByAnnotations(final ASTCompilationUnit file) {
        return file.descendants(ASTAnnotation.class)
                .crossFindBoundaries()
                .toStream()
                .flatMap(a -> Stream.concat(
                                a.getFlatValues().toStream()
                                        .map(ASTMemberValue::getConstValue)
                                        .filter(String.class::isInstance)
                                        .map(String.class::cast)
                                        .filter(StringUtils::isNotEmpty),
                                NodeStream.of(a)
                                        .filter(it -> TypeTestUtil.isA("org.junit.jupiter.params.provider" +
                                                ".MethodSource", it)
                                                && it.getFlatValue("value").isEmpty())
                                        .ancestors(ASTMethodDeclaration.class)
                                        .take(1)
                                        .toStream()
                                        .map(ASTMethodDeclaration::getName)
                        )
                )
                .collect(Collectors.toSet());
    }

    private boolean hasExcludedName(ASTMethodDeclaration node) {
        return SERIALIZATION_METHODS.contains(node.getName());
    }
}
