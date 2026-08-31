/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.types.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.lang.kotlin.ast.KotlinNode;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtAnnotation;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtConstructorInvocation;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtModifiers;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtSingleAnnotation;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtUnescapedAnnotation;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtUserType;
import net.sourceforge.pmd.lang.kotlin.types.InternalApiBridge;
import net.sourceforge.pmd.util.AssertionUtil;

import nl.stokpop.typemapper.model.AnnotationAst;

/**
 * Annotates declaration nodes with annotation FQNs.
 *
 * <p>{@link #setAnnotationFqns} sets type data on the declaration node itself
 * and type data on each {@code KtUnescapedAnnotation} / {@code KtSingleAnnotation}
 * child, matching by simple (unqualified) name.
 *
 * <p>For function parameter type annotation, see {@link FunctionParameterAnnotator}.
 *
 * @since 7.27.0
 */
final class AnnotationFqnAnnotator {

    private AnnotationFqnAnnotator() {
    }

    /**
     * Sets type data on the declaration node and
     * type data on each of its {@code KtUnescapedAnnotation}
     * children, matching by simple (unqualified) name.
     */
    static void setAnnotationFqns(KotlinNode declNode, List<AnnotationAst> annotations) {
        if (annotations.isEmpty()) {
            return;
        }
        // Build a simple-name -> FQN lookup (last match wins; duplicates are rare)
        Map<String, String> simpleToFqn = new HashMap<>();
        List<String> fqnList = new ArrayList<>();
        for (AnnotationAst ann : annotations) {
            String fqn = ann.getFqName();
            if (fqn.isEmpty()) {
                continue;
            }
            simpleToFqn.put(KotlinTypeAnnotationVisitor.simpleNameOf(fqn), fqn);
            fqnList.add(fqn);
        }
        if (!fqnList.isEmpty()) {
            InternalApiBridge.setAnnotationFqNames(declNode, fqnList);
        }

        // Set @TypeName on each KtUnescapedAnnotation in the declaration's modifiers
        List<KtUnescapedAnnotation> annNodes = collectAnnotationNodes(declNode);
        for (KtUnescapedAnnotation annNode : annNodes) {
            String writtenName = getAnnotationWrittenName(annNode);
            if (writtenName == null) {
                continue;
            }
            // Try exact FQN match first, then simple-name match
            String fqn = simpleToFqn.get(KotlinTypeAnnotationVisitor.simpleNameOf(writtenName));
            if (fqn == null) {
                fqn = simpleToFqn.get(writtenName); // handles fully-qualified written name
            }
            if (fqn != null) {
                InternalApiBridge.setTypeName(annNode, fqn);
                // Also set on the parent SingleAnnotation so users can query
                // //SingleAnnotation[@TypeName='org.example.Foo'] directly.
                KotlinNode parent = annNode.getParent();
                if (parent instanceof KtSingleAnnotation) {
                    InternalApiBridge.setTypeName(parent, fqn);
                }
            }
        }
    }

    /**
     * Collects all {@code KtUnescapedAnnotation} nodes from the direct modifiers
     * of a declaration node (does not recurse into function/class bodies).
     */
    private static List<KtUnescapedAnnotation> collectAnnotationNodes(KotlinNode declNode) {
        return declNode
                .children(KtModifiers.class)
                .children(KtAnnotation.class)
                .children() // Either KtSingleAnnotation or KtMultiAnnotation
                .children(KtUnescapedAnnotation.class)
                .toList();
    }

    /**
     * Extracts the annotation name as written in source from a
     * {@code KtUnescapedAnnotation} node, using the text region of the contained
     * {@code KtUserType} node. Returns e.g. {@code "Column"} or
     * {@code "javax.persistence.Column"}, or {@code null} on failure.
     */
    private static String getAnnotationWrittenName(KtUnescapedAnnotation annNode) {
        KtUserType userType = findUserType(annNode);
        if (userType == null) {
            return null;
        }
        try {
            return userType.getTextDocument()
                    .sliceOriginalText(userType.getTextRegion())
                    .toString();
        } catch (IndexOutOfBoundsException e) {
            throw AssertionUtil.contexted(e)
                    .addContextValue("annotation node", annNode)
                    .addContextValue("userType node", userType);
        }
    }

    /** Finds the {@code KtUserType} directly inside a {@code KtUnescapedAnnotation}. */
    private static KtUserType findUserType(KtUnescapedAnnotation annNode) {
        for (int i = 0; i < annNode.getNumChildren(); i++) {
            KotlinNode child = annNode.getChild(i);
            if (child instanceof KtUserType) {
                return (KtUserType) child;
            }
            if (child instanceof KtConstructorInvocation) {
                return KotlinTypeAnnotationVisitor.findUserTypeInConstructorInvocation((KtConstructorInvocation) child);
            }
        }
        return null;
    }
}
