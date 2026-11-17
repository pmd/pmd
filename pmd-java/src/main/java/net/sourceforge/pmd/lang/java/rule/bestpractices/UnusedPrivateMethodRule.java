/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import static net.sourceforge.pmd.lang.ast.NodeStream.asInstanceOf;
import static net.sourceforge.pmd.util.CollectionUtil.buildMap;
import static net.sourceforge.pmd.util.CollectionUtil.listOf;
import static net.sourceforge.pmd.util.CollectionUtil.mapOf;
import static net.sourceforge.pmd.util.CollectionUtil.setOf;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
import net.sourceforge.pmd.lang.java.rule.internal.JavaRuleUtil;
import net.sourceforge.pmd.lang.java.symbols.JExecutableSymbol;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.OverloadSelectionResult;
import net.sourceforge.pmd.lang.java.types.TypeOps;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;
import net.sourceforge.pmd.reporting.RuleContext;

/**
 * This rule detects private methods, that are not used and can therefore be
 * deleted.
 */
public class UnusedPrivateMethodRule extends AbstractIgnoredAnnotationRule {

    private static final Set<String> SERIALIZATION_METHODS =
        setOf("readObject", "readObjectNoData", "writeObject", "readResolve", "writeReplace");

    private static final String JUNIT_METHOD_SOURCE =
        "org.junit.jupiter.params.provider.MethodSource";
    private static final String MAPSTRUCT_NAMED = "org.mapstruct.Named";

    // Empty attribute set: every string member names a method.
    private static final Set<String> EVERY_STRING_ATTRIBUTE = Collections.emptySet();
    private static final Set<String> DEPENDS_ON_METHODS = setOf("dependsOnMethods");
    private static final Set<String> DATA_PROVIDER = setOf("dataProvider");
    private static final Set<String> QUALIFIED_BY_NAME = setOf("qualifiedByName");
    private static final Set<String> MAPPING_METHOD_NAMES = setOf("qualifiedByName", "conditionQualifiedByName");
    private static final Set<String> MAP_MAPPING_NAMES = setOf("keyQualifiedByName", "valueQualifiedByName");
    private static final Set<String> INHERIT_METHOD_NAME = setOf("name");
    private static final Set<String> MYBATIS_STATEMENT = setOf("select");
    private static final Set<String> MYBATIS_PROVIDER_METHOD = setOf("method");

    private static final List<String> TESTNG_CONFIGURATION_ANNOTATIONS = listOf(
        "org.testng.annotations.BeforeSuite",
        "org.testng.annotations.AfterSuite",
        "org.testng.annotations.BeforeTest",
        "org.testng.annotations.AfterTest",
        "org.testng.annotations.BeforeClass",
        "org.testng.annotations.AfterClass",
        "org.testng.annotations.BeforeGroups",
        "org.testng.annotations.AfterGroups",
        "org.testng.annotations.BeforeMethod",
        "org.testng.annotations.AfterMethod"
    );

    // Known method-referencing annotations. This set can be extended.
    // Only attributes that name methods are listed. Other strings in the same
    // annotation (descriptions, SQL, property names, Java expressions) are not uses.
    // JUnit @MethodSource and Lombok @ObtainVia match no-arg methods, including
    // MethodSource's Class#method syntax. The other annotations match any arity,
    // including Class.method statement ids used by TestNG and MyBatis.
    private static final Map<String, Set<String>> NO_ARG_METHOD_REFERENCES = mapOf(
        JUNIT_METHOD_SOURCE, EVERY_STRING_ATTRIBUTE,
        "lombok.Builder.ObtainVia", EVERY_STRING_ATTRIBUTE
    );

    private static final Map<String, Set<String>> ANY_ARITY_METHOD_REFERENCES = buildMap(annotations -> {
        annotations.put("org.testng.annotations.Test", setOf("dataProvider", "dependsOnMethods"));
        annotations.put("org.testng.annotations.Factory", DATA_PROVIDER);
        for (String configuration : TESTNG_CONFIGURATION_ANNOTATIONS) {
            annotations.put(configuration, DEPENDS_ON_METHODS);
        }
        annotations.put("org.mapstruct.Mapping", MAPPING_METHOD_NAMES);
        annotations.put("org.mapstruct.BeanMapping", QUALIFIED_BY_NAME);
        annotations.put("org.mapstruct.IterableMapping", QUALIFIED_BY_NAME);
        annotations.put("org.mapstruct.MapMapping", MAP_MAPPING_NAMES);
        annotations.put("org.mapstruct.SubclassMapping", QUALIFIED_BY_NAME);
        annotations.put("org.mapstruct.InheritConfiguration", INHERIT_METHOD_NAME);
        annotations.put("org.mapstruct.InheritInverseConfiguration", INHERIT_METHOD_NAME);
        annotations.put("org.apache.ibatis.annotations.One", MYBATIS_STATEMENT);
        annotations.put("org.apache.ibatis.annotations.Many", MYBATIS_STATEMENT);
        annotations.put("org.apache.ibatis.annotations.SelectProvider", MYBATIS_PROVIDER_METHOD);
        annotations.put("org.apache.ibatis.annotations.InsertProvider", MYBATIS_PROVIDER_METHOD);
        annotations.put("org.apache.ibatis.annotations.UpdateProvider", MYBATIS_PROVIDER_METHOD);
        annotations.put("org.apache.ibatis.annotations.DeleteProvider", MYBATIS_PROVIDER_METHOD);
    });

    @Override
    protected Collection<String> defaultSuppressionAnnotations() {
        return listOf(
            "java.lang.Deprecated",
            "jakarta.annotation.PostConstruct",
            "jakarta.annotation.PreDestroy",
            "javax.annotation.PostConstruct",
            "javax.annotation.PreDestroy",
            "lombok.EqualsAndHashCode.Include"
        );
    }

    @Override
    public Object visit(ASTCompilationUnit file, Object param) {
        RuleContext ctx = (RuleContext) param;

        // this does a couple of traversals:
        // - one to find no-arg method references (JUnit MethodSource, Lombok ObtainVia)
        // - one to find known method-name attributes (not every annotation string)
        // - one to collect candidates, that is, potentially unused methods
        // - one to walk through all possible usages of methods, and delete used methods from the set
        Set<String> noArgReferences = JavaRuleUtil.getMembersUsedByAnnotations(file,
            JUNIT_METHOD_SOURCE, NO_ARG_METHOD_REFERENCES);
        Set<String> anyArityReferences = JavaRuleUtil.getMembersUsedByAnnotations(file,
            null, ANY_ARITY_METHOD_REFERENCES);
        Map<JExecutableSymbol, ASTMethodDeclaration> candidates = findCandidates(file, noArgReferences, anyArityReferences);

        file.descendants()
            .crossFindBoundaries()
            .<MethodUsage>map(asInstanceOf(ASTMethodCall.class, ASTMethodReference.class))
            .forEach(ref -> {
                OverloadSelectionResult selectionInfo = ref.getOverloadSelectionInfo();
                JExecutableSymbol calledMethod = selectionInfo.getMethodType().getSymbol();
                if (calledMethod.isUnresolved() || selectionInfo.isFailed()) {
                    // When overload resolution failed, the selected symbol is an
                    // arbitrary fallback and we cannot trust which overload was
                    // actually called. Treat it like an unresolved call to avoid
                    // false positives on the other overloads.
                    handleUnresolvedCall(ref, selectionInfo, candidates);
                    return;
                }
                candidates.compute(calledMethod, (sym2, reffed) -> {
                    if (reffed != null && ref.ancestors(ASTMethodDeclaration.class).first() != reffed) {
                        // remove mapping, but only if it is called from outside itself
                        return null;
                    }
                    return reffed;
                });
            });

        for (ASTMethodDeclaration unusedMethod : candidates.values()) {
            ctx.addViolation(unusedMethod, PrettyPrintingUtil.displaySignature(unusedMethod));
        }
        return null;
    }

    private static void handleUnresolvedCall(MethodUsage ref, OverloadSelectionResult selectionInfo, Map<JExecutableSymbol, ASTMethodDeclaration> candidates) {
        // If the type may be an instance of this class, then the method may be
        // a call to a private method here. In that case we whitelist all methods
        // with that name.
        JTypeMirror receive = selectionInfo.getTypeToSearch();
        boolean receiverMayBeInstanceOfThisClass =
                receive == null
                || TypeOps.isSpecialUnresolved(receive)
                || receive.equals(ref.getEnclosingType().getTypeMirror());

        if (receiverMayBeInstanceOfThisClass) {
            candidates.values().removeIf(it -> it.getName().equals(ref.getMethodName()));
        }
    }

    /**
     * Collect potential unused private methods and index them by their symbol.
     * We don't use {@link JExecutableSymbol#tryGetNode()} because it may return
     * null for types that are treated specially by the type inference system. For
     * instance for java.lang.String, the ASM symbol is preferred over the AST symbol.
     */
    private Map<JExecutableSymbol, ASTMethodDeclaration> findCandidates(ASTCompilationUnit file,
                                                                        Set<String> noArgReferences,
                                                                        Set<String> anyArityReferences) {
        return file.descendants(ASTMethodDeclaration.class)
                   .crossFindBoundaries()
                   .filter(
                       it -> it.getVisibility() == Visibility.V_PRIVATE
                           && !hasIgnoredAnnotation(it)
                           && !hasExcludedName(it)
                           && !isReferencedByAnnotation(it, noArgReferences, anyArityReferences))
                   .collect(Collectors.toMap(
                       ASTMethodDeclaration::getSymbol,
                       m -> m
                   ));
    }


    private static boolean isReferencedByAnnotation(ASTMethodDeclaration method,
                                                    Set<String> noArgReferences,
                                                    Set<String> anyArityReferences) {
        if (matchesSimpleOrDottedName(method, anyArityReferences)
            || isNamedQualifierReferenced(method, anyArityReferences)) {
            return true;
        }
        if (method.getArity() != 0) {
            return false;
        }
        String name = method.getName();
        // JUnit MethodSource uses binary class names, including '$' for nested classes.
        String qualifiedName = method.getEnclosingType().getBinaryName() + "#" + name;
        return noArgReferences.contains(name) || noArgReferences.contains(name + "()")
            || noArgReferences.contains(qualifiedName) || noArgReferences.contains(qualifiedName + "()");
    }

    /**
     * TestNG {@code dependsOnMethods} and MyBatis statement ids use {@code Class.method}.
     * {@code Class.getName()} is the binary name, so nested classes use {@code '$'}.
     */
    private static boolean matchesSimpleOrDottedName(ASTMethodDeclaration method, Set<String> references) {
        String name = method.getName();
        if (references.contains(name)) {
            return true;
        }
        String binaryName = method.getEnclosingType().getBinaryName();
        if (references.contains(binaryName + "." + name)) {
            return true;
        }
        String canonicalName = method.getEnclosingType().getCanonicalName();
        return canonicalName != null && references.contains(canonicalName + "." + name);
    }

    /**
     * MapStruct {@code qualifiedByName} selects the method annotated with {@code @Named},
     * whose qualifier is not necessarily the Java method name.
     */
    private static boolean isNamedQualifierReferenced(ASTMethodDeclaration method, Set<String> references) {
        for (ASTAnnotation annotation : method.getDeclaredAnnotations()) {
            if (!TypeTestUtil.isA(MAPSTRUCT_NAMED, annotation)) {
                continue;
            }
            for (ASTMemberValue value : annotation.getFlatValue("value")) {
                Object constant = value.getConstValue();
                if (constant instanceof String && references.contains(constant)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean hasExcludedName(ASTMethodDeclaration node) {
        return SERIALIZATION_METHODS.contains(node.getName());
    }
}
