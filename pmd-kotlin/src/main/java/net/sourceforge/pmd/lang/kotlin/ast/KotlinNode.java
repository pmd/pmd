/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.ast;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.impl.antlr4.AntlrNode;
import net.sourceforge.pmd.lang.rule.xpath.NoAttribute;
import net.sourceforge.pmd.util.DataMap;
import net.sourceforge.pmd.util.DataMap.SimpleDataKey;

/**
 * Supertype of all kotlin nodes.
 */
public interface KotlinNode extends AntlrNode<KotlinNode> {

    /**
     * DataMap key for the resolved type of a {@code PropertyDeclaration} node,
     * e.g. {@code "java.util.Calendar"} or {@code "kotlin.String"}.
     * Populated by the type annotation pass when kotlin-type-mapper analysis is available.
     */
    SimpleDataKey<String> TYPE_NAME_KEY = DataMap.simpleDataKey("kotlin.typeName");

    /**
     * DataMap key for the resolved return type of a {@code FunctionDeclaration} node,
     * e.g. {@code "java.util.Calendar"} or {@code "kotlin.String"}.
     * Populated by the type annotation pass when kotlin-type-mapper analysis is available.
     */
    SimpleDataKey<String> RETURN_TYPE_KEY = DataMap.simpleDataKey("kotlin.returnTypeName");

    /**
     * DataMap key for the FQN annotation names on a declaration node
     * (property, function, class). Stored as a comma-separated string of
     * fully-qualified class names, e.g.
     * {@code "javax.persistence.Entity,javax.persistence.Table"}.
     * Populated by the type annotation pass when kotlin-type-mapper analysis is available.
     */
    SimpleDataKey<String> ANNOTATION_NAMES_KEY = DataMap.simpleDataKey("kotlin.annotationNames");

    /**
     * Returns an unmodifiable list of fully-qualified annotation class names
     * for this declaration node, or an empty list if none are present or type
     * analysis has not been run.
     * <p>Used by {@code pmd-kotlin:hasAnnotation()}; not exposed as an XPath
     * attribute to avoid noise on all non-declaration nodes in the Designer.
     */
    @NoAttribute
    default List<String> getAnnotationFqNames() {
        String stored = getUserMap().get(ANNOTATION_NAMES_KEY);
        if (stored == null || stored.isEmpty()) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(Arrays.asList(stored.split(",")));
    }

    /**
     * Returns the resolved type name for a {@code PropertyDeclaration} node (the property type),
     * for a {@code FunctionDeclaration} node (the return type), or for a
     * {@code KtUnescapedAnnotation} node (the annotation class FQN).
     * Returns {@code null} on all other nodes or when type analysis has not been run.
     * Exposed as XPath attribute {@code @TypeName}.
     */
    default @Nullable String getTypeName() {
        return getUserMap().get(TYPE_NAME_KEY);
    }

    /**
     * Returns the resolved return type name for a {@code FunctionDeclaration} node, or
     * {@code null} if type analysis has not been run or this node is not a function declaration.
     * Exposed as XPath attribute {@code @ReturnTypeName}.
     */
    default @Nullable String getReturnTypeName() {
        return getUserMap().get(RETURN_TYPE_KEY);
    }

    /**
     * Returns the explicit modifier keywords of this declaration node as a
     * space-separated string, e.g. {@code "override suspend"} or {@code "data open"}.
     * Returns {@code null} when this node has no {@code Modifiers} child or when
     * all children of {@code Modifiers} are annotations (annotations are excluded).
     * Exposed as XPath attribute {@code @Modifiers}.
     *
     * <p>Visible in the PMD Designer only on nodes that carry at least one keyword modifier.
     * Implemented in {@link KotlinInnerNode}; returns {@code null} by default (e.g. on terminal nodes).
     */
    default @Nullable String getModifiers() {
        return null;
    }

    /**
     * Returns the text of the first {@code SimpleIdentifier} direct child of this node,
     * or {@code null} if there is none. This makes it easy to write XPath expressions
     * like {@code //ClassDeclaration[@Identifier='Foo']} instead of the longer
     * {@code //ClassDeclaration/SimpleIdentifier/T-Identifier[@Text='Foo']}.
     * Exposed as XPath attribute {@code @Identifier}.
     *
     * <p>Visible in the PMD Designer only on nodes that have a SimpleIdentifier child.
     * Implemented in {@link KotlinInnerNode}; returns {@code null} by default.
     */
    default @Nullable String getIdentifier() {
        return null;
    }
}
