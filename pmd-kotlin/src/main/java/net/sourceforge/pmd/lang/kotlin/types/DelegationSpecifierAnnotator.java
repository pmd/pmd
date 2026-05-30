/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.types;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.pmd.lang.kotlin.ast.KotlinNode;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser;

/**
 * Annotates {@code KtDelegationSpecifier} nodes in a class declaration with resolved
 * supertype FQNs.
 *
 * <p>In Kotlin, the colon-separated list after a class name is called the
 * <em>delegation specifier list</em>. Each entry is a <em>delegation specifier</em>
 * and can be:
 * <ul>
 *   <li>a plain supertype / interface ({@code class Foo : Bar, Baz}),</li>
 *   <li>a superclass constructor call ({@code class Foo : Base(42)}), or</li>
 *   <li>an interface delegation ({@code class Foo : Bar by impl}).</li>
 * </ul>
 * The term comes directly from the Kotlin ANTLR grammar rule {@code delegationSpecifier}.
 *
 * <p>This annotator sets KotlinTypeMapper on each
 * {@code KtDelegationSpecifier} node so that XPath rules can query the written
 * supertype by its fully-qualified name, e.g.
 * {@code //ClassDeclaration[DelegationSpecifier[@TypeName='java.io.Serializable']]}.
 */
final class DelegationSpecifierAnnotator {

    private static final Logger LOG = LoggerFactory.getLogger(DelegationSpecifierAnnotator.class);

    private DelegationSpecifierAnnotator() {
    }

    /**
     * Sets KotlinTypeMapper on each {@code KtDelegationSpecifier}
     * node inside the class declaration, matching the written supertype name (e.g.
     * {@code Serializable}) against the FQNs in {@code superTypes}.
     */
    static void setDelegationSpecifierTypes(KotlinParser.KtClassDeclaration classNode,
            List<String> superTypes) {
        if (superTypes.isEmpty()) {
            return;
        }
        Map<String, String> simpleToFqn = new HashMap<>();
        for (String fqn : superTypes) {
            simpleToFqn.put(KotlinTypeAnnotationVisitor.simpleNameOf(fqn), fqn);
        }
        KotlinParser.KtDelegationSpecifiers specsNode = findDelegationSpecifiersNode(classNode);
        if (specsNode != null) {
            annotateDelegationSpecifiersNode(specsNode, simpleToFqn);
        }
    }

    private static KotlinParser.KtDelegationSpecifiers findDelegationSpecifiersNode(
            KotlinParser.KtClassDeclaration classNode) {
        for (int i = 0; i < classNode.getNumChildren(); i++) {
            KotlinNode child = classNode.getChild(i);
            if (child instanceof KotlinParser.KtDelegationSpecifiers) {
                return (KotlinParser.KtDelegationSpecifiers) child;
            }
        }
        return null;
    }

    private static void annotateDelegationSpecifiersNode(KotlinParser.KtDelegationSpecifiers specsNode,
            Map<String, String> simpleToFqn) {
        for (int j = 0; j < specsNode.getNumChildren(); j++) {
            KotlinNode spec = specsNode.getChild(j);
            if (spec instanceof KotlinParser.KtAnnotatedDelegationSpecifier) {
                annotateAnnotatedDelegationSpecifier(
                        (KotlinParser.KtAnnotatedDelegationSpecifier) spec, simpleToFqn);
            }
        }
    }

    private static void annotateAnnotatedDelegationSpecifier(
            KotlinParser.KtAnnotatedDelegationSpecifier annotated, Map<String, String> simpleToFqn) {
        for (int k = 0; k < annotated.getNumChildren(); k++) {
            KotlinNode inner = annotated.getChild(k);
            if (inner instanceof KotlinParser.KtDelegationSpecifier) {
                annotateDelegationSpecifier((KotlinParser.KtDelegationSpecifier) inner, simpleToFqn);
            }
        }
    }

    /**
     * Sets KotlinTypeMapper on a single {@code KtDelegationSpecifier}
     * by extracting the written type name from its contained {@code KtUserType}.
     */
    private static void annotateDelegationSpecifier(KotlinParser.KtDelegationSpecifier spec,
            Map<String, String> simpleToFqn) {
        KotlinParser.KtUserType userType = findUserTypeInDelegationSpecifier(spec);
        if (userType == null) {
            return;
        }
        try {
            String written = userType.getTextDocument()
                    .sliceOriginalText(userType.getTextRegion())
                    .toString();
            int angle = written.indexOf('<');
            if (angle >= 0) {
                written = written.substring(0, angle).trim();
            }
            String fqn = simpleToFqn.get(KotlinTypeAnnotationVisitor.simpleNameOf(written));
            if (fqn != null) {
                KotlinTypeMapper.setTypeName(spec, fqn);
            }
        } catch (IndexOutOfBoundsException e) {
            LOG.debug("Could not read text region for delegation specifier in {}", spec, e);
        }
    }

    /**
     * Finds the {@code KtUserType} inside a {@code KtDelegationSpecifier}.
     * Handles both direct {@code userType()} cases and {@code constructorInvocation()}
     * (superclass with constructor call).
     */
    private static KotlinParser.KtUserType findUserTypeInDelegationSpecifier(
            KotlinParser.KtDelegationSpecifier spec) {
        for (int i = 0; i < spec.getNumChildren(); i++) {
            KotlinNode child = spec.getChild(i);
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
