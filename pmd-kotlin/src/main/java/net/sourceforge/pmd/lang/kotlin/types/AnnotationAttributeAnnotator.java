/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.pmd.lang.kotlin.ast.KotlinNode;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser;

import nl.stokpop.typemapper.model.AnnotationAst;
import nl.stokpop.typemapper.model.ParameterAst;

/**
 * Annotates declaration nodes with annotation FQNs and function parameter types.
 *
 * <p>Responsibility (1): {@link #setAnnotationAttributes} sets
 * type data on the declaration node itself and
 * type data on each {@code KtUnescapedAnnotation} /
 * {@code KtSingleAnnotation} child.
 *
 * <p>Responsibility (2): {@link #setFunctionParameterTypes} sets
 * type data on each {@code KtFunctionValueParameter}
 * child by matching against the {@link nl.stokpop.typemapper.model.ParameterAst}
 * list from kotlin-type-mapper.
 */
final class AnnotationAttributeAnnotator {

    private static final Logger LOG = LoggerFactory.getLogger(AnnotationAttributeAnnotator.class);

    private AnnotationAttributeAnnotator() {
    }

    /**
     * Sets type data on the declaration node and
     * type data on each of its {@code KtUnescapedAnnotation}
     * children, matching by simple (unqualified) name.
     */
    static void setAnnotationAttributes(KotlinNode declNode, List<AnnotationAst> annotations) {
        if (annotations.isEmpty()) {
            return;
        }
        // Build a simple-name -> FQN lookup (last match wins; duplicates are rare)
        Map<String, String> simpleToFqn = new HashMap<>();
        StringBuilder fqnList = new StringBuilder();
        for (AnnotationAst ann : annotations) {
            String fqn = ann.getFqName();
            if (fqn == null || fqn.isEmpty()) {
                continue;
            }
            simpleToFqn.put(KotlinTypeAnnotationVisitor.simpleNameOf(fqn), fqn);
            if (fqnList.length() > 0) {
                fqnList.append(',');
            }
            fqnList.append(fqn);
        }
        if (fqnList.length() > 0) {
            KotlinNodeTypeData.setAnnotationFqNames(declNode, fqnList.toString());
        }

        // Set @TypeName on each KtUnescapedAnnotation in the declaration's modifiers
        List<KotlinParser.KtUnescapedAnnotation> annNodes = collectAnnotationNodes(declNode);
        for (KotlinParser.KtUnescapedAnnotation annNode : annNodes) {
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
                KotlinNodeTypeData.setTypeName(annNode, fqn);
                // Also set on the parent SingleAnnotation so users can query
                // //SingleAnnotation[@TypeName='org.example.Foo'] directly.
                KotlinNode parent = annNode.getParent();
                if (parent instanceof KotlinParser.KtSingleAnnotation) {
                    KotlinNodeTypeData.setTypeName(parent, fqn);
                }
            }
        }
    }

    /**
     * Sets type data on each {@code KtFunctionValueParameter}
     * child of the given function declaration, matching by position against the
     * {@code parameters} list from the kotlin-type-mapper {@link nl.stokpop.typemapper.model.DeclarationAst}.
     */
    static void setFunctionParameterTypes(KotlinParser.KtFunctionDeclaration funcNode,
            List<ParameterAst> parameters) {
        if (parameters.isEmpty()) {
            return;
        }
        KotlinParser.KtFunctionValueParameters paramsNode = findFunctionValueParametersNode(funcNode);
        if (paramsNode != null) {
            annotateParameterNodes(paramsNode, parameters);
        }
    }

    private static KotlinParser.KtFunctionValueParameters findFunctionValueParametersNode(
            KotlinParser.KtFunctionDeclaration funcNode) {
        for (int i = 0; i < funcNode.getNumChildren(); i++) {
            KotlinNode child = funcNode.getChild(i);
            if (child instanceof KotlinParser.KtFunctionValueParameters) {
                return (KotlinParser.KtFunctionValueParameters) child;
            }
        }
        return null;
    }

    private static void annotateParameterNodes(KotlinParser.KtFunctionValueParameters paramsNode,
            List<ParameterAst> parameters) {
        int paramIdx = 0;
        for (int j = 0; j < paramsNode.getNumChildren(); j++) {
            KotlinNode sub = paramsNode.getChild(j);
            if (sub instanceof KotlinParser.KtFunctionValueParameter && paramIdx < parameters.size()) {
                String type = parameters.get(paramIdx).getType();
                if (type != null) {
                    KotlinNodeTypeData.setTypeName(sub, type);
                }
                paramIdx++;
            }
        }
    }

    /**
     * Collects all {@code KtUnescapedAnnotation} nodes from the direct modifiers
     * of a declaration node (does not recurse into function/class bodies).
     */
    private static List<KotlinParser.KtUnescapedAnnotation> collectAnnotationNodes(KotlinNode declNode) {
        List<KotlinParser.KtUnescapedAnnotation> result = new ArrayList<>();
        for (int i = 0; i < declNode.getNumChildren(); i++) {
            KotlinNode child = declNode.getChild(i);
            if (child instanceof KotlinParser.KtModifiers) {
                collectFromModifiers((KotlinParser.KtModifiers) child, result);
                break;
            }
        }
        return result;
    }

    private static void collectFromModifiers(KotlinParser.KtModifiers mods,
            List<KotlinParser.KtUnescapedAnnotation> result) {
        for (int i = 0; i < mods.getNumChildren(); i++) {
            KotlinNode child = mods.getChild(i);
            if (child instanceof KotlinParser.KtAnnotation) {
                collectFromAnnotationRule((KotlinParser.KtAnnotation) child, result);
            }
        }
    }

    private static void collectFromAnnotationRule(KotlinParser.KtAnnotation ann,
            List<KotlinParser.KtUnescapedAnnotation> result) {
        for (int i = 0; i < ann.getNumChildren(); i++) {
            KotlinNode child = ann.getChild(i);
            if (child instanceof KotlinParser.KtSingleAnnotation) {
                collectFromSingleAnnotation((KotlinParser.KtSingleAnnotation) child, result);
            } else if (child instanceof KotlinParser.KtMultiAnnotation) {
                collectFromMultiAnnotation((KotlinParser.KtMultiAnnotation) child, result);
            }
        }
    }

    private static void collectFromSingleAnnotation(KotlinParser.KtSingleAnnotation singleAnn,
            List<KotlinParser.KtUnescapedAnnotation> result) {
        for (int j = 0; j < singleAnn.getNumChildren(); j++) {
            KotlinNode sub = singleAnn.getChild(j);
            if (sub instanceof KotlinParser.KtUnescapedAnnotation) {
                result.add((KotlinParser.KtUnescapedAnnotation) sub);
                break;
            }
        }
    }

    private static void collectFromMultiAnnotation(KotlinParser.KtMultiAnnotation multiAnn,
            List<KotlinParser.KtUnescapedAnnotation> result) {
        for (int j = 0; j < multiAnn.getNumChildren(); j++) {
            KotlinNode sub = multiAnn.getChild(j);
            if (sub instanceof KotlinParser.KtUnescapedAnnotation) {
                result.add((KotlinParser.KtUnescapedAnnotation) sub);
            }
        }
    }

    /**
     * Extracts the annotation name as written in source from a
     * {@code KtUnescapedAnnotation} node, using the text region of the contained
     * {@code KtUserType} node. Returns e.g. {@code "Column"} or
     * {@code "javax.persistence.Column"}, or {@code null} on failure.
     */
    private static String getAnnotationWrittenName(KotlinParser.KtUnescapedAnnotation annNode) {
        KotlinParser.KtUserType userType = findUserType(annNode);
        if (userType == null) {
            return null;
        }
        try {
            return userType.getTextDocument()
                    .sliceOriginalText(userType.getTextRegion())
                    .toString();
        } catch (IndexOutOfBoundsException e) {
            LOG.debug("Could not read text region for annotation in {}", annNode, e);
            return null;
        }
    }

    /** Finds the {@code KtUserType} directly inside a {@code KtUnescapedAnnotation}. */
    private static KotlinParser.KtUserType findUserType(KotlinParser.KtUnescapedAnnotation annNode) {
        for (int i = 0; i < annNode.getNumChildren(); i++) {
            KotlinNode child = annNode.getChild(i);
            if (child instanceof KotlinParser.KtUserType) {
                return (KotlinParser.KtUserType) child;
            }
            if (child instanceof KotlinParser.KtConstructorInvocation) {
                return KotlinTypeAnnotationVisitor.findUserTypeInConstructorInvocation(
                        (KotlinParser.KtConstructorInvocation) child);
            }
        }
        return null;
    }
}
