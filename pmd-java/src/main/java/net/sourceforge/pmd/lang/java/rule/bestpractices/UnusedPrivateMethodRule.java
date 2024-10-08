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
        Map<String, Set<ASTMethodDeclaration>> unusedMethodsMap = findUnusedPrivateMethods(compilationUnit,
                findMethodsUsedByAnnotations(compilationUnit));
        processUnusedMethods(compilationUnit, unusedMethodsMap);
        reportUnusedMethods(data, unusedMethodsMap);
        return null;
    }

    /**
     * Finds and returns a map of unused private methods in the file.
     */
    private HashMap<String, Set<ASTMethodDeclaration>> findUnusedPrivateMethods(final ASTCompilationUnit compilationUnit,
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
     * Checks if the method has a serialization-related name.
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
     * Processes method references and removes them from the unused methods map if they are used.
     */
    private static void processUnusedMethods(final ASTCompilationUnit compilationUnit,
                                             final Map<String, Set<ASTMethodDeclaration>> unusedMethodsMap) {
        compilationUnit.descendants()
                .crossFindBoundaries()
                .map(NodeStream.<MethodUsage>asInstanceOf(ASTMethodCall.class, ASTMethodReference.class))
                .forEach(ref -> {
                    if (!unusedMethodsMap.containsKey(ref.getMethodName())) {
                        return;
                    }
                    extracted(unusedMethodsMap, ref, (ref instanceof ASTMethodCall
                            ? ((ASTMethodCall) ref).getMethodType().getSymbol()
                            : ((ASTMethodReference) ref).getReferencedMethod().getSymbol())
                            .tryGetNode());
                });
    }

    private static void extracted(final Map<String, Set<ASTMethodDeclaration>> unusedMethodsMap,
                                  final MethodUsage ref, final JavaNode referencedNode) {
        if (referencedNode instanceof ASTMethodDeclaration
                && ref.ancestors(ASTMethodDeclaration.class).first() != referencedNode) {
            // Remove from set only if the method is called outside of itself
            Set<ASTMethodDeclaration> remainingUnusedMethods = unusedMethodsMap.get(ref.getMethodName());
            if (remainingUnusedMethods != null
                    && remainingUnusedMethods.remove(referencedNode) // note: side-effect
                    && remainingUnusedMethods.isEmpty()) {
                unusedMethodsMap.remove(ref.getMethodName()); // clear this name
            }
        }
    }

    /**
     * Reports the methods that remain unused.
     */
    private void reportUnusedMethods(final Object data, final Map<String, Set<ASTMethodDeclaration>> unusedMethodsMap) {
        unusedMethodsMap.forEach((methodName, unusedMethods) -> {
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
