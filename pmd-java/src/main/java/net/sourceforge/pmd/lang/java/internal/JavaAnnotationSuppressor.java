/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.internal;

import static net.sourceforge.pmd.util.CollectionUtil.listOf;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.java.ast.ASTAnnotation;
import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr;
import net.sourceforge.pmd.lang.java.ast.ASTClassDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassType;
import net.sourceforge.pmd.lang.java.ast.ASTExecutableDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMemberValue;
import net.sourceforge.pmd.lang.java.ast.ASTMemberValuePair;
import net.sourceforge.pmd.lang.java.ast.ASTModifierList;
import net.sourceforge.pmd.lang.java.ast.ASTTypeParameter;
import net.sourceforge.pmd.lang.java.ast.ASTTypeParameters;
import net.sourceforge.pmd.lang.java.ast.ASTVariableId;
import net.sourceforge.pmd.lang.java.ast.Annotatable;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.ast.JavaVisitorBase;
import net.sourceforge.pmd.lang.java.ast.ModifierOwner.Visibility;
import net.sourceforge.pmd.lang.java.rule.errorprone.ImplicitSwitchFallThroughRule;
import net.sourceforge.pmd.lang.java.symbols.JExecutableSymbol;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.JTypeVar;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;
import net.sourceforge.pmd.lang.rule.Rule;
import net.sourceforge.pmd.reporting.AbstractAnnotationSuppressor;
import net.sourceforge.pmd.reporting.ViolationSuppressor;
import net.sourceforge.pmd.util.OptionalBool;

/**
 * Helper methods to suppress violations based on annotations.
 *
 * An annotation suppresses a rule if the annotation is a {@link SuppressWarnings},
 * and if the set of suppressed warnings ({@link SuppressWarnings#value()})
 * contains at least one of those:
 * <ul>
 * <li>"PMD" (suppresses all rules);
 * <li>"PMD.rulename", where rulename is the name of the given rule;
 * <li>"all" (conventional value to suppress all warnings).
 * </ul>
 *
 * <p>Additionally, the following values suppress a specific set of rules:
 * <ul>
 * <li>{@code "unused"}: suppresses rules like UnusedLocalVariable or UnusedPrivateField;
 * <li>{@code "serial"}: suppresses BeanMembersShouldSerialize, NonSerializableClass and MissingSerialVersionUID;
 * <li>{@code "fallthrough"}: suppresses ImplicitSwitchFallthrough #1899
 * </ul>
 *
 * @see <a href="https://docs.oracle.com/javase/specs/jls/se21/html/jls-9.html#jls-9.6.4.5">JLS 9.6.4.5. @SuppressWarnings</a>
 * @since 7.14.0
 */
final class JavaAnnotationSuppressor extends AbstractAnnotationSuppressor<ASTAnnotation> {

    private static final Set<String> UNUSED_RULES = new HashSet<>(Arrays.asList("UnusedPrivateField", "UnusedLocalVariable", "UnusedPrivateMethod", "UnusedFormalParameter", "UnusedAssignment", "SingularField"));
    private static final Set<String> SERIAL_RULES = new HashSet<>(Arrays.asList("BeanMembersShouldSerialize", "NonSerializableClass", "MissingSerialVersionUID"));


    static final List<ViolationSuppressor> ALL_JAVA_SUPPRESSORS = listOf(new JavaAnnotationSuppressor());

    private JavaAnnotationSuppressor() {
        super(ASTAnnotation.class);
    }


    @Override
    protected NodeStream<ASTAnnotation> getAnnotations(Node n) {
        if (n instanceof Annotatable) {
            return ((Annotatable) n).getDeclaredAnnotations();
        }
        return NodeStream.empty();
    }

    @Override
    protected boolean annotationParamSuppresses(String stringVal, Rule rule) {
        return super.annotationParamSuppresses(stringVal, rule)
            || "serial".equals(stringVal) && SERIAL_RULES.contains(rule.getName())
            || "unused".equals(stringVal) && UNUSED_RULES.contains(rule.getName())
            || "fallthrough".equals(stringVal) && rule instanceof ImplicitSwitchFallThroughRule;
    }

    @Override
    protected boolean walkAnnotation(ASTAnnotation annotation, AnnotationWalkCallbacks callbacks) {
        if (TypeTestUtil.isA(SuppressWarnings.class, annotation)) {
            for (ASTMemberValue value : annotation.getFlatValue(ASTMemberValuePair.VALUE_ATTR)) {
                Object constVal = value.getConstValue();
                if (constVal instanceof String) {
                    if (callbacks.processNode(value, (String) constVal)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    JavaNode getAnnotationScope(ASTAnnotation a) {
        if (a.getParent() instanceof ASTModifierList) {
            return a.getParent().getParent();
        }
        return null;
    }

    @SuppressWarnings("unused")
    public static void foo1(int i) {
        i = 2;
        foo2(i);
    }

    @SuppressWarnings("unused")
    private static void foo2(int i) {
        System.out.println("i = " + i);
    }

    private static OptionalBool hasUnusedWarning(JavaNode node) {
        if (node == null) {
            return OptionalBool.UNKNOWN;
        }

        if (hasUnusedVariables(node)) {
            return OptionalBool.YES;
        } else if (hasUnusedTypeParam(node)) {
            return OptionalBool.YES;
        } else if (hasUnusedMethod(node)) {
            return OptionalBool.YES;
        }

        if (node instanceof ASTFieldDeclaration) {
            // for annotated fields, "unused" annotation is unambiguous - it only is used for this field.
            return OptionalBool.NO;
        }

        if (node instanceof ASTClassDeclaration) {
            ASTClassDeclaration classDecl = (ASTClassDeclaration) node;
            if (classDecl.getEffectiveVisibility() == Visibility.V_PRIVATE) {
                // is this private class is used in this compilation unit?
                boolean used = node.getRoot().descendants(ASTClassType.class)
                        .toStream()
                        .map(ASTClassType::getTypeMirror)
                        .map(JTypeMirror::getSymbol)
                        .filter(Objects::nonNull)
                        .anyMatch(s -> s.equals(classDecl.getSymbol()));
                if (used) {
                    return OptionalBool.NO;
                }
            }
        }

        return OptionalBool.UNKNOWN;
    }

    /**
     * Searches for local variables, fields, formal parameters.
     */
    private static boolean hasUnusedVariables(JavaNode node) {
        Set<ASTAssignableExpr.AccessType> varAccesses = node.descendants(ASTVariableId.class)
                .crossFindBoundaries()
                .toStream()
                .flatMap(it -> it.getLocalUsages().stream())
                .map(ASTAssignableExpr::getAccessType)
                .collect(Collectors.toSet());
        return !varAccesses.isEmpty() && varAccesses.stream()
                .noneMatch(it -> it == ASTAssignableExpr.AccessType.READ);
    }

    private static boolean hasUnusedTypeParam(JavaNode node) {
        // we can do this in a single traversal because type params must
        // be declared before they are used (in tree order)
        Set<JTypeVar> unusedTypeParams = new HashSet<>();
        node.acceptVisitor(new JavaVisitorBase<Void, Void>() {
            @Override
            public Void visit(ASTTypeParameters node, Void p) {
                // add all params before visiting bounds
                for (ASTTypeParameter parm : node) {
                    unusedTypeParams.add(parm.getTypeMirror());
                }
                return super.visit(node, p);
            }

            @Override
            public Void visit(ASTClassType node, Void data) {
                JTypeMirror ty = node.getTypeMirror();
                if (ty instanceof JTypeVar) {
                    unusedTypeParams.remove(ty);
                }
                return super.visit(node, data);
            }

            @Override
            public Void visit(ASTModifierList node, Void data) {
                // no need to visit those
                return data;
            }
        }, null);

        return !unusedTypeParams.isEmpty();
    }

    private static boolean hasUnusedMethod(JavaNode node) {
        Set<JExecutableSymbol> privateMethods = new HashSet<>();
        for (ASTExecutableDeclaration decl : node.descendantsOrSelf()
                .crossFindBoundaries()
                .filterIs(ASTExecutableDeclaration.class)) {
            Visibility visibility = decl.getEffectiveVisibility();
            if (visibility.isAtMost(Visibility.V_PRIVATE)) {
                privateMethods.add(decl.getSymbol());
            } else {
                // We cannot know if non-private (package-private, protected, public) method is effectively used
                return false;
            }
        }

        return false;

        // if (!privateMethods.isEmpty()) {
        // node is a private method/ctor or is a class decl that contains only private methods
        // TODO we need to somehow sync this with UnusedPrivateMethod and check, if these private
        // methods are indeed unused or not. See also #5727.
        // for now, we give up and assume, all private methods are used
        // and always return false...
        // }
    }

    @Override
    protected OptionalBool isSuppressingNonPmdWarnings(String stringVal, ASTAnnotation annotation) {
        if ("unused".equals(stringVal)) {
            JavaNode scope = getAnnotationScope(annotation);
            if (hasUnusedWarning(scope) == OptionalBool.NO) {
                return OptionalBool.NO;
            }
        }
        return super.isSuppressingNonPmdWarnings(stringVal, annotation);
    }
}
