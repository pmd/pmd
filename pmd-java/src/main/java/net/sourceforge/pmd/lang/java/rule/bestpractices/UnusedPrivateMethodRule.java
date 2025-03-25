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

import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.java.ast.ASTAnnotation;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTMemberValue;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodReference;
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
        visit(
            file,
            findPrivateMethods(file, methodsUsedByAnnotations(file)),
            param);
        return null;
    }

    private void visit(ASTCompilationUnit file,
                       Map<String, Set<ASTMethodDeclaration>> privateMethods,
                       Object param) {
        findUnusedMethods(
            file,
            privateMethods,
            methodDeclarationsCache(file)
        ).forEach((name, unused) -> addViolation(param, unused));
    }

    private void addViolation(Object param, Set<ASTMethodDeclaration> unused) {
        for (ASTMethodDeclaration m : unused) {
            asCtx(param).addViolation(m, PrettyPrintingUtil.displaySignature(m));
        }
    }

    private static Map<String, Set<ASTMethodDeclaration>> findUnusedMethods(ASTCompilationUnit file,
                                                                            Map<String,
                                                                                Set<ASTMethodDeclaration>> methods,
                                                                            Map<JExecutableSymbol,
                                                                                ASTMethodDeclaration>
                                                                                cache) {
        file.descendants()
            .crossFindBoundaries()
            .map(NodeStream.<MethodUsage>asInstanceOf(ASTMethodCall.class, ASTMethodReference.class))
            .forEach(ref -> {
                final JExecutableSymbol sym = getJExecutableSymbol(ref);
                filterUsedMethods(methods,
                    ref,
                    ref.getMethodName(),
                    sym.tryGetNode() != null
                        ? sym.tryGetNode()
                        : cache.get(sym));
            });
        return methods;
    }

    private static void filterUsedMethods(Map<String, Set<ASTMethodDeclaration>> privateMethods,
                                          MethodUsage ref,
                                          String methodName,
                                          JavaNode reffed) {
        if (privateMethods.containsKey(methodName)
            && reffed instanceof ASTMethodDeclaration
            && ref.ancestors(ASTMethodDeclaration.class).first() != reffed) {
            final Set<ASTMethodDeclaration> remainingUnused = privateMethods.get(methodName);
            if (remainingUnused != null
                && remainingUnused.remove(reffed)
                && remainingUnused.isEmpty()) {
                privateMethods.remove(methodName);
            }
        }
    }

    private static Map<JExecutableSymbol, ASTMethodDeclaration> methodDeclarationsCache(ASTCompilationUnit file) {
        return file.descendants(ASTMethodDeclaration.class)
            .crossFindBoundaries()
            .toStream()
            .collect(Collectors.toMap(ASTMethodDeclaration::getSymbol, m -> m));
    }

    private static JExecutableSymbol getJExecutableSymbol(MethodUsage ref) {
        if (ref instanceof ASTMethodCall) {
            return ((ASTMethodCall) ref).getMethodType().getSymbol();
        } else if (ref instanceof ASTMethodReference) {
            return ((ASTMethodReference) ref).getReferencedMethod().getSymbol();
        }
        throw new IllegalStateException("unknown type: " + ref);
    }

    private Map<String, Set<ASTMethodDeclaration>> findPrivateMethods(ASTCompilationUnit file,
                                                                      Set<String> methodsUsedByAnnotations) {
        return file.descendants(ASTMethodDeclaration.class)
            .crossFindBoundaries()
            .filter(it -> it.getVisibility() == Visibility.V_PRIVATE)
            .filter(it -> !hasIgnoredAnnotation(it)
                && !hasExcludedName(it)
                && !(it.getArity() == 0 && methodsUsedByAnnotations.contains(it.getName())))
            .toStream()
            .collect(Collectors.groupingBy(
                ASTMethodDeclaration::getName,
                HashMap::new,
                CollectionUtil.toMutableSet()
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
        );
    }

    private boolean hasExcludedName(ASTMethodDeclaration node) {
        return SERIALIZATION_METHODS.contains(node.getName());
    }
}
