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

    @Override
    public Object visit(ASTCompilationUnit file, Object param) {
        // We do three traversals:
        // - one to find methods:
        // --- referenced by any attribute of any annotation
        // --- with name same as a method annotated with Junit5 MethodSource if the annotation value is empty
        // - one to find the "interesting methods", ie those that may be violations
        // - another to find the possible usages. We only try to resolve
        //   method calls/method refs that may refer to a method in the
        //   first set, ie, not every call in the file.
        Set<String> methodsUsedByAnnotations =
                file.descendants(ASTAnnotation.class)
                        .crossFindBoundaries()
                        .toStream()
                        .flatMap(a -> Stream.concat(
                                        a.getFlatValues().toStream()
                                                .map(ASTMemberValue::getConstValue)
                                                .filter(String.class::isInstance)
                                                .map(String.class::cast)
                                                .filter(StringUtils::isNotEmpty),
                                        NodeStream.of(a)
                                                .filter(it -> TypeTestUtil.isA("org.junit.jupiter.params.provider.MethodSource", it)
                                                        && it.getFlatValue("value").isEmpty())
                                                .ancestors(ASTMethodDeclaration.class)
                                                .take(1)
                                                .toStream()
                                                .map(ASTMethodDeclaration::getName)
                                )
                        )
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
                if (reffed == null) {
                    reffed = findDeclarationInCompilationUnit(file, sym);
                }
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
