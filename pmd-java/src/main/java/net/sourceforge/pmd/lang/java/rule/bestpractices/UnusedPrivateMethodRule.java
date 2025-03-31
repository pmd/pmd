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
        RuleContext ctx = asCtx(param);
        for (ASTMethodDeclaration violation : findViolations(file, findCandidates(file)).values()) {
            ctx.addViolation(violation, PrettyPrintingUtil.displaySignature(violation));
        }
        return null;
    }

    /**
     * does the following traversals:
     * <ol>
     * <li>find annotations that potentially reference a method.</li>
     * <li>collect potentially unused methods.</li>
     * <li>delete used methods from the set by walking through all usages.</li>
     * </ol>
     */
    private Map<JExecutableSymbol, ASTMethodDeclaration> findViolations(ASTCompilationUnit file,
                                                                        Map<JExecutableSymbol, ASTMethodDeclaration> methods) {
        file.descendants()
            .crossFindBoundaries()
            .<MethodUsage>map(asInstanceOf(ASTMethodCall.class, ASTMethodReference.class))
            .forEach(ref -> removeUsedMethods(methods, ref));
        return methods;
    }

    private static void removeUsedMethods(Map<JExecutableSymbol, ASTMethodDeclaration> candidates, MethodUsage ref) {
        candidates.compute(
            getMethodSymbol(ref),
            (s, reffed) -> reffed != null && ref.ancestors(ASTMethodDeclaration.class).first() != reffed
                ? null // remove mapping, but only if it is called from outside itself
                : reffed);
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
     * Collect potential unused private methods and index them by their symbol.
     * We don't use {@link JExecutableSymbol#tryGetNode()} because it may return
     * null for types that are treated specially by the type inference system. For
     * instance for java.lang.String, the ASM symbol is preferred over the AST symbol.
     */
    private Map<JExecutableSymbol, ASTMethodDeclaration> findCandidates(ASTCompilationUnit file) {
        return file.descendants(ASTMethodDeclaration.class)
            .crossFindBoundaries()
            .filter(
                it -> it.getVisibility() == Visibility.V_PRIVATE
                    && !hasIgnoredAnnotation(it)
                    && !hasExcludedName(it)
                    && !(it.getArity() == 0 && methodsUsedByAnnotations(file).contains(it.getName())))
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
