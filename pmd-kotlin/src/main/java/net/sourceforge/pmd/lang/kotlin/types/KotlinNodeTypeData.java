/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.types;

import java.util.Collections;
import java.util.List;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinNode;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtKotlinFile;
import net.sourceforge.pmd.lang.kotlin.rule.internal.KotlinTypeAnalysisContext;
import net.sourceforge.pmd.util.DataMap;
import net.sourceforge.pmd.util.DataMap.SimpleDataKey;

/**
 * Stores and retrieves type-mapper data on Kotlin AST nodes via {@link DataMap} keys.
 *
 * <p>DataKeys are private to this class. The kotlin-type-mapper library
 * uses the {@code set*} methods (via {@link InternalApiBridge}) to populate
 * values during its pre-analysis pass; rule code uses the {@code get*} methods
 * to read them.
 *
 * @since 7.26.0
 * @experimental
 */
@Experimental
public final class KotlinNodeTypeData {

    private static final SimpleDataKey<String> TYPE_NAME_KEY =
            DataMap.simpleDataKey("kotlin.typeName");

    private static final SimpleDataKey<String> RETURN_TYPE_KEY =
            DataMap.simpleDataKey("kotlin.returnTypeName");

    private static final SimpleDataKey<List<String>> ANNOTATION_NAMES_KEY =
            DataMap.simpleDataKey("kotlin.annotationNames");

    private static final SimpleDataKey<Boolean> TYPE_INFO_AVAILABLE_KEY =
            DataMap.simpleDataKey("kotlin.typeInfoAvailable");

    private static final SimpleDataKey<KotlinTypeAnalysisContext> ANALYSIS_CONTEXT_KEY =
            DataMap.simpleDataKey("kotlin.analysisContext");

    private KotlinNodeTypeData() {}

    /**
     * Returns the resolved type name stored on this node,
     * or {@code null} when type analysis has not been run or the node has no type.
     * Used on variable declarations, function parameters, annotation nodes,
     * catch blocks, delegation specifiers, and for-loop variables.
     */
    public static @Nullable String getTypeName(KotlinNode node) {
        return node.getUserMap().get(TYPE_NAME_KEY);
    }

    /**
     * Stores the resolved type name on a node.
     * Called by the kotlin-type-mapper pre-analysis pass via {@link InternalApiBridge}.
     */
    static void setTypeName(KotlinNode node, String typeName) {
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
     * Called by the kotlin-type-mapper pre-analysis pass via {@link InternalApiBridge}.
     */
    static void setReturnTypeName(KotlinNode node, String returnTypeName) {
        node.getUserMap().set(RETURN_TYPE_KEY, returnTypeName);
    }

    /**
     * Returns an unmodifiable list of fully-qualified annotation class names
     * for a declaration node, or an empty list if none are present or type
     * analysis has not been run.
     */
    public static List<String> getAnnotationFqNames(KotlinNode node) {
        List<String> stored = node.getUserMap().get(ANNOTATION_NAMES_KEY);
        return stored != null ? stored : Collections.emptyList();
    }

    /**
     * Stores the fully-qualified annotation class names on a declaration node.
     * Called by the kotlin-type-mapper pre-analysis pass via {@link InternalApiBridge}.
     */
    static void setAnnotationFqNames(KotlinNode node, List<String> annotationFqNames) {
        node.getUserMap().set(ANNOTATION_NAMES_KEY, annotationFqNames);
    }

    /**
     * Returns {@code true} when the kotlin-type-mapper pre-analysis ran successfully
     * for the file represented by this root node, {@code false} otherwise.
     */
    public static boolean isTypeInfoAvailable(KtKotlinFile rootNode) {
        Boolean value = rootNode.getUserMap().get(TYPE_INFO_AVAILABLE_KEY);
        return Boolean.TRUE.equals(value);
    }

    /**
     * Marks a root node as having completed type analysis.
     * Called via {@link InternalApiBridge}.
     */
    static void setTypeInfoAvailable(KtKotlinFile rootNode) {
        rootNode.getUserMap().set(TYPE_INFO_AVAILABLE_KEY, Boolean.TRUE);
    }

    /**
     * Returns the {@link KotlinTypeAnalysisContext} stored on this root node,
     * or {@link KotlinTypeAnalysisContext#empty()} when not set.
     * XPath functions use this to retrieve per-run type data from the context node's root.
     */
    public static KotlinTypeAnalysisContext getAnalysisContext(KtKotlinFile rootNode) {
        KotlinTypeAnalysisContext ctx = rootNode.getUserMap().get(ANALYSIS_CONTEXT_KEY);
        return ctx != null ? ctx : KotlinTypeAnalysisContext.empty();
    }

    /**
     * Stores the analysis context on a root node.
     * Called via {@link InternalApiBridge} after type analysis completes for a file.
     */
    static void setAnalysisContext(KtKotlinFile rootNode, KotlinTypeAnalysisContext ctx) {
        rootNode.getUserMap().set(ANALYSIS_CONTEXT_KEY, ctx);
    }
}
