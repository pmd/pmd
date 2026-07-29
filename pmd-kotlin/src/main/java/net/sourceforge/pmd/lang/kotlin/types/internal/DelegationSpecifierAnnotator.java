/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.types.internal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.lang.kotlin.ast.KotlinNode;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtAnnotatedDelegationSpecifier;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtClassDeclaration;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtDelegationSpecifier;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtDelegationSpecifiers;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtUserType;
import net.sourceforge.pmd.lang.kotlin.types.InternalApiBridge;
import net.sourceforge.pmd.util.AssertionUtil;

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
 * <p>This annotator sets type data on each
 * {@code KtDelegationSpecifier} node so that XPath rules can query the written
 * supertype by its fully-qualified name, e.g.
 * {@code //ClassDeclaration[DelegationSpecifier[@TypeName='java.io.Serializable']]}.
 *
 * @since 7.27.0
 */
final class DelegationSpecifierAnnotator {

    private DelegationSpecifierAnnotator() {
    }

    /**
     * Sets type data on each {@code KtDelegationSpecifier}
     * node inside the class declaration, matching the written supertype name (e.g.
     * {@code Serializable}) against the FQNs in {@code superTypes}.
     */
    static void setDelegationSpecifierTypes(KtClassDeclaration classNode, List<String> superTypes) {
        if (superTypes.isEmpty()) {
            return;
        }
        Map<String, String> simpleToFqn = new HashMap<>();
        for (String fqn : superTypes) {
            simpleToFqn.put(KotlinTypeAnnotationVisitor.simpleNameOf(fqn), fqn);
        }

        classNode
                .children(KtDelegationSpecifiers.class).take(1)
                .children(KtAnnotatedDelegationSpecifier.class)
                .children(KtDelegationSpecifier.class)
                .forEach(delegationSpecifier -> {
                    annotateDelegationSpecifier(delegationSpecifier, simpleToFqn);
                });
    }

    /**
     * Sets type data on a single {@code KtDelegationSpecifier}
     * by extracting the written type name from its contained {@code KtUserType}.
     */
    private static void annotateDelegationSpecifier(
            KtDelegationSpecifier spec, Map<String, String> simpleToFqn) {
        KtUserType userType = findUserTypeInDelegationSpecifier(spec);
        if (userType == null) {
            return;
        }
        try {
            String written = userType.getTextDocument()
                    .sliceOriginalText(userType.getTextRegion())
                    .toString();
            written = KotlinTypeAnnotationVisitor.rawTypeNameOf(written);
            String fqn = simpleToFqn.get(KotlinTypeAnnotationVisitor.simpleNameOf(written));
            if (fqn != null) {
                InternalApiBridge.setTypeName(spec, fqn);
            }
        } catch (IndexOutOfBoundsException e) {
            throw AssertionUtil.contexted(e).addContextValue("delegation specifier", spec);
        }
    }

    /**
     * Finds the {@code KtUserType} inside a {@code KtDelegationSpecifier}.
     * Handles both direct {@code userType()} cases and {@code constructorInvocation()}
     * (superclass with constructor call).
     */
    private static KtUserType findUserTypeInDelegationSpecifier(KtDelegationSpecifier spec) {
        for (int i = 0; i < spec.getNumChildren(); i++) {
            KotlinNode child = spec.getChild(i);
            if (child instanceof KtUserType) {
                return (KtUserType) child;
            }
            if (child instanceof KotlinParser.KtConstructorInvocation) {
                return KotlinTypeAnnotationVisitor.findUserTypeInConstructorInvocation(
                        (KotlinParser.KtConstructorInvocation) child);
            }
        }
        return null;
    }
}
