/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.java.ast.*;
import net.sourceforge.pmd.lang.java.rule.internal.AbstractIgnoredAnnotationRule;
import net.sourceforge.pmd.lang.java.symbols.JExecutableSymbol;
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
import static org.apache.commons.lang3.ObjectUtils.notEqual;

/**
 * This rule identifies private methods that are never used within the code
 * and can be safely removed. It aims to improve code maintainability
 * by eliminating unnecessary methods.
 */
public class UnusedPrivateMethodRule extends AbstractIgnoredAnnotationRule {

    private static final Set<String> SERIALIZATION_METHODS =
            setOf("readObject", "writeObject", "readResolve", "writeReplace");

    /**
     * Visits the compilation unit and checks for unused private methods.
     * <p>
     * This involves three main steps:
     * <ol>
     * <li>Identify methods referenced by annotations.</li>
     * <li>Identify potentially unused private methods.</li>
     * <li>Track method references to determine if any identified
     * methods are actually used.</li>
     * </ol>
     *
     * @param compilationUnit the compilation unit being analyzed
     * @param data            additional data for reporting violations
     * @return null
     */
    @Override
    public Object visit(ASTCompilationUnit compilationUnit, Object data) {
        reportUnusedPrivateMethods(data,
                trackMethodReferences(compilationUnit,
                        identifyUnusedPrivateMethods(compilationUnit, findMethodsUsedByAnnotations(compilationUnit))));
        return null;
    }

    /**
     * Identifies and returns a map of unused private methods in the given
     * compilation unit.
     *
     * @param compilationUnit          the compilation unit being analyzed
     * @param methodsUsedByAnnotations a set of methods used via annotations
     * @return a map of method names to their corresponding unused
     * method declarations
     */
    private Map<String, Set<ASTMethodDeclaration>> identifyUnusedPrivateMethods(final ASTCompilationUnit compilationUnit,
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
     * Checks if the specified method is related to serialization
     * based on its name.
     *
     * @param method the method declaration to check
     * @return true if the method is a serialization method, false otherwise
     */
    private boolean isSerializationMethod(ASTMethodDeclaration method) {
        return SERIALIZATION_METHODS.contains(method.getName());
    }

    /**
     * Finds method names that are used via annotations within the
     * provided compilation unit.
     *
     * @param compilationUnit the compilation unit being analyzed
     * @return a set of method names that are used via annotations
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
     * Tracks method references and removes methods from the unused methods map
     * if they are invoked.
     *
     * @param compilationUnit      the compilation unit being analyzed
     * @param unusedPrivateMethods a map of unused private methods to check
     * @return the updated map of unused private methods
     */
    private static Map<String, Set<ASTMethodDeclaration>> trackMethodReferences(final ASTCompilationUnit compilationUnit,
                                                                                final Map<String,
                                                                                        Set<ASTMethodDeclaration>> unusedPrivateMethods) {
        compilationUnit.descendants()
                .crossFindBoundaries()
                .map(NodeStream.<MethodUsage>asInstanceOf(ASTMethodCall.class, ASTMethodReference.class))
                .forEach(ref -> {
                    if (!unusedPrivateMethods.containsKey(ref.getMethodName())) {
                        return; // early exit
                    }
                    isUnused(ref,
                            unusedPrivateMethods,
                            unusedPrivateMethods.get(ref.getMethodName()));
                });
        return unusedPrivateMethods;
    }

    private static JExecutableSymbol getMethodOrReference(final MethodUsage ref) {
        return ref instanceof ASTMethodCall
                ? ((ASTMethodCall) ref).getMethodType().getSymbol()
                : ((ASTMethodReference) ref).getReferencedMethod().getSymbol();
    }

    /**
     * Processes a method reference, removing the method from the unused set
     * if it is called outside of itself.
     * <p>
     * This method removes the method from the unused set only if it is called
     * from outside its own definition.
     *
     * @param referencedNode         the referenced method declaration node
     * @param ref                    the method usage reference
     * @param unusedPrivateMethods   the map of unused private methods
     * @param remainingUnusedMethods the set of remaining unused methods
     */
    private static void isUnused(final MethodUsage ref,
                                 final Map<String, Set<ASTMethodDeclaration>> unusedPrivateMethods,
                                 final Set<ASTMethodDeclaration> remainingUnusedMethods) {
        isUnused(getMethodOrReference(ref).tryGetNode(), ref, remainingUnusedMethods, unusedPrivateMethods);
    }

    private static void isUnused(final JavaNode referencedNode, final MethodUsage ref,
                                 final Set<ASTMethodDeclaration> remainingUnusedMethods,
                                 final Map<String, Set<ASTMethodDeclaration>> unusedPrivateMethods) {
        if (referencedNode instanceof ASTMethodDeclaration
                && notEqual(ref.ancestors(ASTMethodDeclaration.class).first(), referencedNode)
                && nonNull(remainingUnusedMethods)
                && remainingUnusedMethods.remove(referencedNode) // note: side effect
                && remainingUnusedMethods.isEmpty()) {
            unusedPrivateMethods.remove(ref.getMethodName()); // clear this name
        }
    }

    /**
     * Reports the methods that remain unused after analysis.
     *
     * @param data                 additional data for reporting violations
     * @param unusedPrivateMethods a map of unused private methods
     */
    private void reportUnusedPrivateMethods(final Object data,
                                            final Map<String, Set<ASTMethodDeclaration>> unusedPrivateMethods) {
        unusedPrivateMethods.forEach((methodName, unusedMethods) -> {
            for (ASTMethodDeclaration method : unusedMethods) {
                asCtx(data).addViolation(method, displaySignature(method));
            }
        });
    }

    /**
     * Provides the default list of annotations that can suppress the
     * reporting of unused private methods.
     *
     * @return a collection of suppression annotations
     */
    @Override
    protected Collection<String> defaultSuppressionAnnotations() {
        return listOf(
                "java.lang.Deprecated",
                "jakarta.annotation.PostConstruct",
                "jakarta.annotation.PreDestroy"
        );
    }
}
