/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import static net.sourceforge.pmd.util.CollectionUtil.listOf;
import static net.sourceforge.pmd.util.CollectionUtil.setOf;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.java.ast.ASTAnnotation;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTMemberValue;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodReference;
import net.sourceforge.pmd.lang.java.ast.ASTTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.ast.MethodUsage;
import net.sourceforge.pmd.lang.java.ast.ModifierOwner.Visibility;
import net.sourceforge.pmd.lang.java.ast.internal.PrettyPrintingUtil;
import net.sourceforge.pmd.lang.java.rule.internal.AbstractIgnoredAnnotationRule;
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
        return listOf(
                "java.lang.Deprecated",
                "jakarta.annotation.PostConstruct",
                "jakarta.annotation.PreDestroy",
                "lombok.EqualsAndHashCode.Include"
        );
    }

    /**
     * Visits the compilation unit to detect unused private methods.
     *
     * @param file  the compilation unit to visit
     * @param param the context parameter
     * @return null
     */
    @Override
    public Object visit(ASTCompilationUnit file, Object param) {
        visit(file, param, consideredNames(file));
        return null;
    }

    /**
     * Visits the compilation unit and processes method references.
     *
     * @param file            the compilation unit to visit
     * @param param           the context parameter
     * @param consideredNames a map of method names to their declarations
     */
    private void visit(ASTCompilationUnit file, Object param, Map<String, Set<ASTMethodDeclaration>> consideredNames) {
        file.descendants()
                .crossFindBoundaries()
                .map(NodeStream.<MethodUsage>asInstanceOf(ASTMethodCall.class, ASTMethodReference.class))
                .forEach(ref -> methodName(file, consideredNames, ref, ref.getMethodName(), getJExecutableSymbol(ref)));
        handleUnused(param, consideredNames);
    }

    /**
     * Processes a method reference by checking if it is used.
     *
     * @param file            the compilation unit
     * @param consideredNames a map of method names to their declarations
     * @param ref             the method reference
     * @param methodName      the name of the method
     * @param sym             the executable symbol of the method
     */
    private static void methodName(ASTCompilationUnit file, Map<String, Set<ASTMethodDeclaration>> consideredNames,
                                   MethodUsage ref, String methodName, JExecutableSymbol sym) {
        // the considered names might be mutated during the traversal
        if (!consideredNames.containsKey(methodName)) {
            return;
        }
        methodName(consideredNames, ref, methodName, sym, findDeclarationInCompilationUnitIfNull(file, sym,
                sym.tryGetNode()));
    }

    /**
     * Gets the declaration of a method in the compilation unit if it is not already available.
     *
     * @param file   the compilation unit
     * @param sym    the executable symbol
     * @param reffed the referenced node
     * @return the declaration node
     */
    private static JavaNode findDeclarationInCompilationUnitIfNull(ASTCompilationUnit file, JExecutableSymbol sym,
                                                                   JavaNode reffed) {
        return reffed == null ? findDeclarationInCompilationUnit(file, sym) : reffed;
    }

    /**
     * Processes a method reference by checking if it is used.
     *
     * @param consideredNames a map of method names to their declarations
     * @param ref             the method reference
     * @param methodName      the name of the method
     * @param sym             the executable symbol of the method
     * @param reffed          the referenced node
     */
    private static void methodName(Map<String, Set<ASTMethodDeclaration>> consideredNames,
                                   MethodUsage ref, String methodName,
                                   @Nullable JExecutableSymbol sym,
                                   JavaNode reffed) {
        if (sym != null) {
            if (reffed instanceof ASTMethodDeclaration
                    && ref.ancestors(ASTMethodDeclaration.class).first() != reffed) {
                // remove from set, but only if it is called outside itself
                Set<ASTMethodDeclaration> remainingUnused = consideredNames.get(methodName);
                if (remainingUnused != null
                        && remainingUnused.remove(reffed) // note: side effect
                        && remainingUnused.isEmpty()) {
                    consideredNames.remove(methodName); // clear this name
                }
            }
        }
    }

    /**
     * Handles unused methods by adding violations for each unused method.
     *
     * @param param           the context parameter
     * @param consideredNames a map of method names to their declarations
     */
    private void handleUnused(Object param, Map<String, Set<ASTMethodDeclaration>> consideredNames) {
        consideredNames.forEach((name, unused) -> {
            for (ASTMethodDeclaration m : unused) {
                asCtx(param).addViolation(m, PrettyPrintingUtil.displaySignature(m));
            }
        });
    }

    /**
     * Gets the executable symbol from a method reference.
     *
     * @param ref the method reference
     * @return the executable symbol
     */
    private static JExecutableSymbol getJExecutableSymbol(MethodUsage ref) {
        if (ref instanceof ASTMethodCall) {
            return ((ASTMethodCall) ref).getMethodType().getSymbol();
        } else if (ref instanceof ASTMethodReference) {
            return ((ASTMethodReference) ref).getReferencedMethod().getSymbol();
        }
        return null;
    }

    /**
     * Collects the names of methods that are considered for the rule.
     *
     * @param file the compilation unit
     * @return a map of method names to their declarations
     */
    private Map<String, Set<ASTMethodDeclaration>> consideredNames(ASTCompilationUnit file) {
        return file.descendants(ASTMethodDeclaration.class)
                .crossFindBoundaries()
                // get methods whose usages are all in this file
                // TODO we could use getEffectiveVisibility here, but we need to consider overrides then.
                .filter(it -> it.getVisibility() == Visibility.V_PRIVATE)
                .filter(it -> !hasIgnoredAnnotation(it)
                        && !hasExcludedName(it)
                        && !(it.getArity() == 0 && methodsUsedByAnnotations(file).contains(it.getName())))
                .toStream()
                .collect(Collectors.groupingBy(ASTMethodDeclaration::getName, HashMap::new,
                        CollectionUtil.toMutableSet()));
    }

    /**
     * Collects the names of methods used by annotations.
     *
     * @param file the compilation unit
     * @return a set of method names
     */
    private static Set<String> methodsUsedByAnnotations(ASTCompilationUnit file) {
        return file.descendants(ASTAnnotation.class)
                .crossFindBoundaries()
                .toStream()
                .flatMap(UnusedPrivateMethodRule::extractMethodsFromAnnotation)
                .collect(Collectors.toSet());
    }

    /**
     * Extracts method names referenced by an annotation.
     *
     * @param a the annotation from which to extract method names
     * @return a stream of method names referenced by the annotation
     */
    private static Stream<String> extractMethodsFromAnnotation(ASTAnnotation a) {
        return Stream.concat(
                a.getFlatValues().toStream()
                        .map(ASTMemberValue::getConstValue)
                        .filter(String.class::isInstance)
                        .map(String.class::cast)
                        .filter(StringUtils::isNotEmpty),
                NodeStream.of(a)
                        .filter(it -> TypeTestUtil
                                .isA("org.junit.jupiter.params.provider.MethodSource", it)
                                && it.getFlatValue("value").isEmpty())
                        .ancestors(ASTMethodDeclaration.class)
                        .take(1)
                        .toStream()
                        .map(ASTMethodDeclaration::getName)
        );
    }

    /**
     * Checks if a method has an excluded name.
     *
     * @param node the method declaration
     * @return true if the method name is excluded, false otherwise
     */
    private boolean hasExcludedName(ASTMethodDeclaration node) {
        return SERIALIZATION_METHODS.contains(node.getName());
    }

    /**
     * Find the method in the compilation unit. Note that this is a patch to fix some
     * incorrect behavior of the rule in two cases:
     *
     * <p>1. While parsing and type resolving a referenced class, that itself
     * references the current class. In that case, we end up with the ASM symbols
     * and symbol.tryGetNode() returns null.
     *
     * <p>2. When dealing with classes in the java.lang package.
     * This is due to the fact that some
     * java.lang types (like Object, or primitive boxes) are
     * treated specially by the type resolution framework, and
     * for those the preexisting ASM symbol is preferred over
     * the AST symbol - symbol.tryGetNode() returns null in that
     * case. This is only relevant, when PMD is used to analyze OpenJDK
     * sources, like with the regression tester.
     */
    private static @Nullable ASTMethodDeclaration findDeclarationInCompilationUnit(ASTCompilationUnit acu, JExecutableSymbol symbol) {
        return acu.descendants(ASTTypeDeclaration.class)
                .crossFindBoundaries()
                .filter(it -> it.getSymbol().equals(symbol.getEnclosingClass()))
                .take(1)
                .flatMap(it -> it.getDeclarations(ASTMethodDeclaration.class))
                .first(m -> m.getSymbol().equals(symbol));
    }
}
