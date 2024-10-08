/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.java.ast.*;
import net.sourceforge.pmd.lang.java.rule.internal.AbstractIgnoredAnnotationRule;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.Stream.concat;
import static net.sourceforge.pmd.lang.java.ast.ModifierOwner.Visibility.V_PRIVATE;
import static net.sourceforge.pmd.lang.java.ast.internal.PrettyPrintingUtil.displaySignature;
import static net.sourceforge.pmd.lang.java.types.TypeTestUtil.isA;
import static net.sourceforge.pmd.util.CollectionUtil.listOf;
import static net.sourceforge.pmd.util.CollectionUtil.setOf;
import static net.sourceforge.pmd.util.CollectionUtil.toMutableSet;

/**
 * This rule detects private methods that are not used and can be deleted.
 */
public class UnusedPrivateMethodRule extends AbstractIgnoredAnnotationRule {

    private static final Set<String> SERIALIZATION_METHODS =
            setOf("readObject", "writeObject", "readResolve", "writeReplace");

    @Override
    public Object visit(ASTCompilationUnit compilationUnit, Object data) {
        Map<String, Set<ASTMethodDeclaration>> unusedPrivateMethods = identifyUnusedPrivateMethods(compilationUnit,
                findMethodsUsedByAnnotations(compilationUnit));
        trackMethodReferences(compilationUnit, unusedPrivateMethods);
        reportUnusedPrivateMethods(data, unusedPrivateMethods);
        return null;
    }

    /**
     * Identifies and returns a map of unused private methods in the file.
     */
    private HashMap<String, Set<ASTMethodDeclaration>> identifyUnusedPrivateMethods(final ASTCompilationUnit compilationUnit,
                                                                                    final Set<String> methodsUsedByAnnotations) {
        return compilationUnit.descendants(ASTMethodDeclaration.class)
                .crossFindBoundaries()
                .filter(method -> method.getVisibility() == V_PRIVATE)
                .filter(method -> !hasIgnoredAnnotation(method)
                        && !isSerializationMethod(method)
                        && !(method.getArity() == 0 && methodsUsedByAnnotations.contains(method.getName())))
                .toStream()
                .collect(groupingBy(ASTMethodDeclaration::getName, HashMap::new, toMutableSet()));
    }

    /**
     * Checks if the method is related to serialization.
     */
    private boolean isSerializationMethod(ASTMethodDeclaration method) {
        return SERIALIZATION_METHODS.contains(method.getName());
    }

    /**
     * Finds method names that are used via annotations in the file.
     */
    private static Set<String> findMethodsUsedByAnnotations(final ASTCompilationUnit compilationUnit) {
        return compilationUnit.descendants(ASTAnnotation.class)
                .crossFindBoundaries()
                .toStream()
                .flatMap(annotation -> concat(
                                annotation.getFlatValues().toStream()
                                        .map(ASTMemberValue::getConstValue)
                                        .filter(String.class::isInstance)
                                        .map(String.class::cast)
                                        .filter(StringUtils::isNotEmpty),
                                NodeStream.of(annotation)
                                        .filter(it -> isA("org.junit.jupiter.params.provider.MethodSource", it)
                                                && it.getFlatValue("value").isEmpty())
                                        .ancestors(ASTMethodDeclaration.class)
                                        .take(1)
                                        .toStream()
                                        .map(ASTMethodDeclaration::getName)
                        )
                )
                .collect(toSet());
    }

    /**
     * Tracks method references and removes them from the unused methods map if they are used.
     */
    private static void trackMethodReferences(final ASTCompilationUnit compilationUnit,
                                              final Map<String, Set<ASTMethodDeclaration>> unusedPrivateMethods) {
        compilationUnit.descendants()
                .crossFindBoundaries()
                .map(NodeStream.<MethodUsage>asInstanceOf(ASTMethodCall.class, ASTMethodReference.class))
                .forEach(ref -> {
                    if (!unusedPrivateMethods.containsKey(ref.getMethodName())) {
                        return;
                    }
                    processMethodReference(unusedPrivateMethods, ref, (ref instanceof ASTMethodCall
                            ? ((ASTMethodCall) ref).getMethodType().getSymbol()
                            : ((ASTMethodReference) ref).getReferencedMethod().getSymbol())
                            .tryGetNode());
                });
    }

    /**
     * Processes a method reference and removes the method from the unused set if called outside itself.
     */
    private static void processMethodReference(final Map<String, Set<ASTMethodDeclaration>> unusedPrivateMethods,
                                               final MethodUsage ref, final JavaNode referencedNode) {
        if (referencedNode instanceof ASTMethodDeclaration
                && ref.ancestors(ASTMethodDeclaration.class).first() != referencedNode) {
            // Remove from set only if the method is called outside of itself
            Set<ASTMethodDeclaration> remainingUnusedMethods = unusedPrivateMethods.get(ref.getMethodName());
            if (nonNull(remainingUnusedMethods)
                    && remainingUnusedMethods.remove(referencedNode) // note: side-effect
                    && remainingUnusedMethods.isEmpty()) {
                unusedPrivateMethods.remove(ref.getMethodName()); // clear this name
            }
        }
    }

    /**
     * Reports the methods that remain unused.
     */
    private void reportUnusedPrivateMethods(final Object data,
                                            final Map<String, Set<ASTMethodDeclaration>> unusedPrivateMethods) {
        unusedPrivateMethods.forEach((methodName, unusedMethods) -> {
            for (ASTMethodDeclaration method : unusedMethods) {
                asCtx(data).addViolation(method, displaySignature(method));
            }
        });
    }

    @Override
    protected Collection<String> defaultSuppressionAnnotations() {
        return listOf(
                "java.lang.Deprecated",
                "jakarta.annotation.PostConstruct",
                "jakarta.annotation.PreDestroy"
        );
    }

}
