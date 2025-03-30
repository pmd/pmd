/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import static net.sourceforge.pmd.lang.ast.NodeStream.asInstanceOf;
import static net.sourceforge.pmd.util.CollectionUtil.listOf;
import static net.sourceforge.pmd.util.CollectionUtil.setOf;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.java.ast.ASTAnnotation;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTMemberValue;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodReference;
import net.sourceforge.pmd.lang.java.ast.MethodUsage;
import net.sourceforge.pmd.lang.java.ast.ModifierOwner.Visibility;
import net.sourceforge.pmd.lang.java.ast.internal.PrettyPrintingUtil;
import net.sourceforge.pmd.lang.java.rule.internal.AbstractIgnoredAnnotationRule;
import net.sourceforge.pmd.lang.java.symbols.JExecutableSymbol;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;
import net.sourceforge.pmd.reporting.RuleContext;

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
        addViolations(findViolations(file, findCandidates(file, methodsUsedByAnnotations(file))), asCtx(param));
        return null;
    }

    private static Map<JExecutableSymbol, ASTMethodDeclaration> findViolations(ASTCompilationUnit file,
                                                                        Map<JExecutableSymbol, ASTMethodDeclaration> candidates) {
        // this does a couple of traversals:
        // - one to find annotations that potentially reference a method
        // - one to collect candidates, that is, potentially unused methods
        // - one to walk through all possible usages of methods, and delete used methods from the set
        file.descendants()
            .crossFindBoundaries()
            .<MethodUsage>map(asInstanceOf(ASTMethodCall.class, ASTMethodReference.class))
            .forEach(removeUsedMethods(candidates));
        return candidates;
    }

    private static Consumer<MethodUsage> removeUsedMethods(Map<JExecutableSymbol, ASTMethodDeclaration> candidates) {
        return ref -> candidates.compute(getMethodSymbol(ref), (sym, reffed) -> {
            if (reffed != null && ref.ancestors(ASTMethodDeclaration.class).first() != reffed) {
                return null; // remove mapping, but only if it is called from outside itself
            }
            return reffed;
        });
    }

    private static void addViolations(Map<JExecutableSymbol, ASTMethodDeclaration> violations, RuleContext ctx) {
        for (ASTMethodDeclaration violation : violations.values()) {
            ctx.addViolation(violation, PrettyPrintingUtil.displaySignature(violation));
        }
    }

    private static JExecutableSymbol getMethodSymbol(MethodUsage ref) {
        if (ref instanceof ASTMethodCall) {
            return ((ASTMethodCall) ref).getMethodType().getSymbol();
        } else if (ref instanceof ASTMethodReference) {
            return ((ASTMethodReference) ref).getReferencedMethod().getSymbol();
        }
        throw new IllegalStateException("unknown type: " + ref);
    }

    /**
     * @implNote
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
    private Map<JExecutableSymbol, ASTMethodDeclaration> findCandidates(ASTCompilationUnit file, Set<String> methodsUsedByAnnotations) {
        return file.descendants(ASTMethodDeclaration.class)
                   .crossFindBoundaries()
                   .filter(
                       it -> it.getVisibility() == Visibility.V_PRIVATE
                           && !hasIgnoredAnnotation(it)
                           && !hasExcludedName(it)
                           && !(it.getArity() == 0 && methodsUsedByAnnotations.contains(it.getName())))
                   .collect(Collectors.toMap(
                       ASTMethodDeclaration::getSymbol,
                       m -> m
                   ));
    }

    private static Set<String> methodsUsedByAnnotations(ASTCompilationUnit file) {
        return file.descendants(ASTAnnotation.class)
            .crossFindBoundaries()
            .toStream()
            .flatMap(UnusedPrivateMethodRule::extractMethodsFromAnnotation)
            .collect(Collectors.toSet());
    }

    private static Stream<String> extractMethodsFromAnnotation(ASTAnnotation a) {
        return Stream.concat(
            a.getFlatValues().toStream()
                .map(ASTMemberValue::getConstValue)
                .map(asInstanceOf(String.class))
                .filter(StringUtils::isNotEmpty),
            NodeStream.of(a)
                .filter(it -> TypeTestUtil.isA("org.junit.jupiter.params.provider.MethodSource", it)
                    && it.getFlatValue("value").isEmpty())
                .ancestors(ASTMethodDeclaration.class)
                .take(1)
                .toStream()
                .map(ASTMethodDeclaration::getName)
        );
    }

    private static boolean hasExcludedName(ASTMethodDeclaration node) {
        return SERIALIZATION_METHODS.contains(node.getName());
    }
}
