/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.rule.xpath.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinNode;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtAnnotation;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtDeclaration;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtModifiers;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtStatement;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtUnescapedAnnotation;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtUserType;
import net.sourceforge.pmd.lang.kotlin.ast.internal.KotlinAstUtil;
import net.sourceforge.pmd.lang.kotlin.types.KotlinNodeTypeData;
import net.sourceforge.pmd.lang.kotlin.types.KotlinTypeName;
import net.sourceforge.pmd.lang.rule.xpath.impl.XPathFunctionException;

/**
 * XPath function {@code pmd-kotlin:hasAnnotation(className)}.
 *
 * <p>Returns {@code true} when the context node carries an annotation whose name
 * matches {@code className}.  The function is useful on declaration nodes such as
 * {@code functionDeclaration}, {@code propertyDeclaration}, {@code classDeclaration},
 * and {@code objectDeclaration}.
 *
 * <p><b>Name matching rules</b> (checked in order):
 * <ol>
 *   <li>If kotlin-type-mapper resolved the annotation FQN, the {@code @TypeName}
 *       attribute on child {@code UnescapedAnnotation} nodes is used for an exact
 *       FQN match, or a simple-name match when {@code className} itself is a simple
 *       name (no dots).</li>
 *   <li>Falls back to KotlinNodeTypeData on the declaration node, with the same
 *       FQN-or-simple-name rule.</li>
 *   <li>Falls back to the annotation name <em>as written in source</em> (via text region);
 *       only used when {@code className} is a simple name, since source text alone
 *       cannot confirm a FQN match without type resolution.</li>
 * </ol>
 *
 * <p>Both a fully-qualified name (e.g. {@code 'javax.persistence.Column'}) and a
 * simple name (e.g. {@code 'Column'}) are accepted. A simple-name query matches any
 * annotation whose simple name is the same, regardless of package. A fully-qualified
 * query only matches when the annotation's FQN was actually resolved (or written out
 * in source) and equals {@code className} exactly -- it never matches a different
 * package sharing the same simple name.</p>
 *
 * <p>Example XPath:
 * <pre>{@code
 * //functionDeclaration[pmd-kotlin:hasAnnotation('Deprecated')]
 * //propertyDeclaration[pmd-kotlin:hasAnnotation('javax.persistence.Column')]
 * //classDeclaration[pmd-kotlin:hasAnnotation('org.springframework.stereotype.Service')]
 * }</pre>
 *
 * @since 7.28.0
 */
public final class KotlinHasAnnotationFunction extends BaseKotlinXPathFunction {

    public static final KotlinHasAnnotationFunction INSTANCE = new KotlinHasAnnotationFunction();

    private static final Logger LOG = LoggerFactory.getLogger(KotlinHasAnnotationFunction.class);

    private KotlinHasAnnotationFunction() {
        super("hasAnnotation");
    }

    @Override
    public Type[] getArgumentTypes() {
        return new Type[]{Type.SINGLE_STRING};
    }

    @Override
    public Type getResultType() {
        return Type.SINGLE_BOOLEAN;
    }

    @Override
    public boolean dependsOnContext() {
        return true;
    }

    @Override
    public FunctionCall makeCallExpression() {
        return new HasAnnotationFunctionCall();
    }

    private static final class HasAnnotationFunctionCall implements FunctionCall {

        @Override
        public Object call(@Nullable Node contextNode, Object[] arguments) throws XPathFunctionException {
            if (!(contextNode instanceof KotlinNode)) {
                return false;
            }
            String className = (String) arguments[0];
            String simpleName = simpleNameOf(className);
            KotlinNode declNode = (KotlinNode) contextNode;

            return checkAnnotationChildrenByTypeName(declNode, className, simpleName)
                    || matchesAnnotationFqNames(declNode, className, simpleName)
                    || (!className.contains(".") && checkAnnotationChildrenBySourceText(declNode, className, simpleName));
        }

        private static boolean matchesAnnotationFqNames(
                KotlinNode declNode, String className, String simpleName) {
            for (String fqn : KotlinNodeTypeData.getAnnotationFqNames(declNode)) {
                if (fqn.equals(className) || (!className.contains(".") && simpleNameOf(fqn).equals(simpleName))) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * Returns the annotations declared directly on {@code declNode}: its (optional)
     * {@code Modifiers} child's {@code Annotation} children, each unwrapped down to
     * their {@code UnescapedAnnotation} node(s) ({@code SingleAnnotation} has one,
     * {@code MultiAnnotation} has one or more). This shape ({@code declNode ->
     * Modifiers -> Annotation -> SingleAnnotation|MultiAnnotation ->
     * UnescapedAnnotation}) is fixed by the grammar for every kind of declaration
     * that carries modifiers, so no recursive search or body-boundary guard is
     * needed: nested declarations (e.g. a local function in a {@code FunctionBody})
     * have their own {@code Modifiers} child and are never reached from here.
     *
     * <p>For a <em>local</em> declaration (one nested inside a function body), the
     * grammar instead attaches the annotation to the enclosing {@code Statement} as
     * a preceding sibling ({@code statement : (label | annotation)* (declaration |
     * ...)}), so {@link #annotationsOnEnclosingStatement} is also checked.
     */
    static List<KtUnescapedAnnotation> directAnnotationsOf(KotlinNode declNode) {
        List<KtUnescapedAnnotation> result = new ArrayList<>();
        KtModifiers modifiers = declNode.firstChild(KtModifiers.class);
        if (modifiers != null) {
            collectUnescapedAnnotations(modifiers.children(KtAnnotation.class), result);
        }
        collectUnescapedAnnotations(annotationsOnEnclosingStatement(declNode), result);
        return result;
    }

    private static void collectUnescapedAnnotations(
            Iterable<KtAnnotation> annotations, List<KtUnescapedAnnotation> result) {
        for (KtAnnotation annotation : annotations) {
            for (KtUnescapedAnnotation ann : annotation.descendants(KtUnescapedAnnotation.class)) {
                result.add(ann);
            }
        }
    }

    /**
     * For a local declaration ({@code declNode -> Declaration -> Statement}), returns
     * the {@code Statement}'s {@code Annotation} children (its preceding siblings).
     * Returns an empty list for member/top-level declarations, which aren't wrapped
     * this way.
     */
    private static List<KtAnnotation> annotationsOnEnclosingStatement(KotlinNode declNode) {
        Node parent = declNode.getParent();
        if (!(parent instanceof KtDeclaration) || !(parent.getParent() instanceof KtStatement)) {
            return Collections.emptyList();
        }
        return ((KotlinNode) parent.getParent()).children(KtAnnotation.class).toList();
    }

    /**
     * Looks among {@code declNode}'s direct annotations (see {@link #directAnnotationsOf})
     * for one whose {@code @TypeName} (FQN set by kotlin-type-mapper) matches
     * {@code className} or its simple-name suffix.
     */
    private static boolean checkAnnotationChildrenByTypeName(
            KotlinNode declNode, String className, String simpleName) {
        for (KtUnescapedAnnotation ann : directAnnotationsOf(declNode)) {
            KotlinTypeName type = KotlinNodeTypeData.getType(ann);
            if (type != null) {
                String fqName = type.getFqName();
                if (fqName.equals(className) || (!className.contains(".") && simpleNameOf(fqName).equals(simpleName))) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Looks among {@code declNode}'s direct annotations (see {@link #directAnnotationsOf})
     * for one whose name <em>as written in source</em> (via text region) matches
     * {@code className} or its simple-name suffix.
     */
    private static boolean checkAnnotationChildrenBySourceText(
            KotlinNode declNode, String className, String simpleName) {
        for (KtUnescapedAnnotation ann : directAnnotationsOf(declNode)) {
            String writtenName = getAnnotationSourceText(ann);
            if (writtenName != null
                    && (writtenName.equals(className) || simpleNameOf(writtenName).equals(simpleName))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the annotation type name as written in source (e.g. {@code "Column"}
     * or {@code "javax.persistence.Column"}) from the {@code KtUserType} inside the
     * given {@code UnescapedAnnotation} node. Returns {@code null} only if the source
     * text cannot be sliced (unexpected; defensive fallback).
     *
     * @throws IllegalStateException if {@code annNode} has neither a direct
     *     {@code KtUserType} nor a {@code KtConstructorInvocation} containing one —
     *     see {@link KotlinAstUtil#findUserTypeInAnnotation(KtUnescapedAnnotation)}.
     */
    static @Nullable String getAnnotationSourceText(KtUnescapedAnnotation annNode) {
        KtUserType userType = KotlinAstUtil.findUserTypeInAnnotation(annNode);
        try {
            return userType.getTextDocument()
                    .sliceOriginalText(userType.getTextRegion())
                    .toString();
        } catch (IndexOutOfBoundsException e) {
            LOG.debug("Could not slice source text for annotation node", e);
            return null;
        }
    }

    /** Returns the last dot-separated segment, e.g. {@code "Column"} from {@code "javax.persistence.Column"}. */
    static String simpleNameOf(String name) {
        int dot = name.lastIndexOf('.');
        return dot >= 0 ? name.substring(dot + 1) : name;
    }
}
