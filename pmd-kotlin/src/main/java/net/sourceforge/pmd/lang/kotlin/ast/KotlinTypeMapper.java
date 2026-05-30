/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.ast;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.util.DataMap;
import net.sourceforge.pmd.util.DataMap.SimpleDataKey;

/**
 * Contract class for type-mapper data stored on Kotlin AST nodes.
 *
 * <p>DataKeys are private to this class. The kotlin-type-mapper library
 * uses the {@code set*} methods to populate values during its pre-analysis
 * pass; rule code and {@link AttributeView} subclasses use the {@code get*}
 * methods to read them.
 *
 * @since 7.25.0
 * @experimental
 */
@Experimental
public final class KotlinTypeMapper {

    private static final SimpleDataKey<String> TYPE_NAME_KEY =
            DataMap.simpleDataKey("kotlin.typeName");

    private static final SimpleDataKey<String> RETURN_TYPE_KEY =
            DataMap.simpleDataKey("kotlin.returnTypeName");

    private static final SimpleDataKey<String> ANNOTATION_NAMES_KEY =
            DataMap.simpleDataKey("kotlin.annotationNames");

    private KotlinTypeMapper() {
        // utility class
    }

    /**
     * Returns the resolved type name stored on this node,
     * or {@code null} when type analysis has not been run.
     * Used on variable declarations, function parameters, annotation nodes,
     * catch blocks, delegation specifiers, and for-loop variables.
     */
    public static @Nullable String getTypeName(KotlinNode node) {
        return node.getUserMap().get(TYPE_NAME_KEY);
    }

    /**
     * Stores the resolved type name on a node.
     * Called by the kotlin-type-mapper pre-analysis pass.
     */
    public static void setTypeName(KotlinNode node, String typeName) {
        node.getUserMap().set(TYPE_NAME_KEY, typeName);
    }

    /**
     * Returns the resolved return type name for a function declaration node,
     * or {@code null} when type analysis has not been run.
     */
    public static @Nullable String getReturnTypeName(KotlinNode node) {
        return node.getUserMap().get(RETURN_TYPE_KEY);
    }

    /**
     * Stores the resolved return type name on a function declaration node.
     * Called by the kotlin-type-mapper pre-analysis pass.
     */
    public static void setReturnTypeName(KotlinNode node, String returnTypeName) {
        node.getUserMap().set(RETURN_TYPE_KEY, returnTypeName);
    }

    /**
     * Returns an unmodifiable list of fully-qualified annotation class names
     * for a declaration node, or an empty list if none are present or type
     * analysis has not been run.
     * Used by {@code pmd-kotlin:hasAnnotation()}.
     */
    public static List<String> getAnnotationFqNames(KotlinNode node) {
        String stored = node.getUserMap().get(ANNOTATION_NAMES_KEY);
        if (stored == null || stored.isEmpty()) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(Arrays.asList(stored.split(",")));
    }

    /**
     * Stores the fully-qualified annotation class names on a declaration node.
     * Called by the kotlin-type-mapper pre-analysis pass.
     *
     * @param annotationFqNames comma-separated FQN annotation class names
     */
    public static void setAnnotationFqNames(KotlinNode node, String annotationFqNames) {
        node.getUserMap().set(ANNOTATION_NAMES_KEY, annotationFqNames);
    }
}
