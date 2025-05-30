/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.lang.LanguageProcessor;
import net.sourceforge.pmd.lang.LanguageProcessorRegistry;
import net.sourceforge.pmd.lang.ast.Parser.ParserTask;
import net.sourceforge.pmd.lang.document.FileLocation;
import net.sourceforge.pmd.lang.document.TextDocument;
import net.sourceforge.pmd.reporting.Reportable;
import net.sourceforge.pmd.reporting.ViolationSuppressor;
import net.sourceforge.pmd.reporting.ViolationSuppressor.SuppressionCommentWrapper;
import net.sourceforge.pmd.util.AssertionUtil;
import net.sourceforge.pmd.util.CollectionUtil;
import net.sourceforge.pmd.util.DataMap;
import net.sourceforge.pmd.util.DataMap.DataKey;

/**
 * The output of {@link Parser#parse(ParserTask)}.
 *
 * @param <T> Type of root nodes
 */
public final class AstInfo<T extends RootNode> {

    private final TextDocument textDocument;
    private final T rootNode;
    private final LanguageProcessorRegistry lpReg;
    private final Map<Integer, SuppressionCommentWrapper> suppressionComments;
    private final DataMap<DataKey<?, ?>> userMap = DataMap.newDataMap();


    public AstInfo(ParserTask task, T rootNode) {
        this(task.getTextDocument(), rootNode, task.getLpRegistry(), Collections.emptyMap());
    }

    private AstInfo(TextDocument textDocument,
                    T rootNode,
                    LanguageProcessorRegistry lpReg,
                    Map<Integer, SuppressionCommentWrapper> suppressionComments) {
        this.textDocument = AssertionUtil.requireParamNotNull("text document", textDocument);
        this.rootNode = AssertionUtil.requireParamNotNull("root node", rootNode);
        this.lpReg = lpReg;
        this.suppressionComments = AssertionUtil.requireParamNotNull("suppress map", suppressionComments);
    }


    public T getRootNode() {
        return rootNode;
    }

    /**
     * Returns the text document that was parsed.
     * This has info like language version, etc.
     */
    public @NonNull TextDocument getTextDocument() {
        return textDocument;
    }

    /**
     * Returns the language processor that parsed the tree.
     */
    public LanguageProcessor getLanguageProcessor() {
        return lpReg.getProcessor(textDocument.getLanguageVersion().getLanguage());
    }

    /**
     * Returns the map of line numbers to suppression / review comments.
     * Only single line comments are considered, that start with the configured
     * "suppressMarker", which by default is "PMD". The text after the
     * suppressMarker is used as a "review comment" and included in this map.
     *
     * <p>This map is later used to determine, if a violation is being suppressed.
     * It is suppressed, if the line of the violation is contained in this suppress map.
     *
     * @return map of the suppressed lines with the corresponding review comments.
     * @deprecated Since 7.14.0. Use {@link #getAllSuppressionComments()} or {@link #getSuppressionComment(int)}
     */
    @Deprecated
    public Map<Integer, String> getSuppressionComments() {
        return CollectionUtil.mapView(suppressionComments, ViolationSuppressor.SuppressionCommentWrapper::getUserMessage);
    }


    /**
     * Return the suppresson comment at the given line, or null if there is none.
     *
     * @since 7.14.0
     */
    public @Nullable SuppressionCommentWrapper getSuppressionComment(int lineNumber) {
        return suppressionComments.get(lineNumber);
    }

    /**
     * Return all suppression comments in the file.
     * Only single line comments are considered, that start with the configured
     * "suppress marker", which by default is {@link PMDConfiguration#DEFAULT_SUPPRESS_MARKER}.
     * The text after the suppress marker is used as a "review comment" and included in this map.
     *
     * @since 7.14.0
     */
    public Collection<SuppressionCommentWrapper> getAllSuppressionComments() {
        return Collections.unmodifiableCollection(suppressionComments.values());
    }

    /**
     * Returns a data map used to store additional information on this ast info.
     *
     * @return The user data map of this node
     *
     * @since 7.14.0
     */
    public DataMap<DataKey<?, ?>> getUserMap() {
        return userMap;
    }


    /**
     * @deprecated Since 7.14.0. Use {@link #withSuppressionComments(Collection)}
     */
    @Deprecated
    public AstInfo<T> withSuppressMap(Map<Integer, String> map) {
        Set<SuppressionCommentWrapper> comments = new HashSet<>();
        for (Map.Entry<Integer, String> entry : map.entrySet()) {
            String comment = entry.getValue();
            int line = entry.getKey();
            comments.add(new SuppressionCommentWrapper() {
                @Override
                public String getUserMessage() {
                    return comment;
                }

                @Override
                public Reportable getLocation() {
                    return () -> FileLocation.caret(textDocument.getFileId(), line, 1);
                }
            });
        }
        return withSuppressionComments(comments);
    }


    /**
     * @since 7.14.0
     */
    public AstInfo<T> withSuppressionComments(Collection<? extends SuppressionCommentWrapper> suppressionComments) {
        Map<Integer, SuppressionCommentWrapper> suppressMap = new HashMap<>(suppressionComments.size());
        for (SuppressionCommentWrapper comment : suppressionComments) {
            suppressMap.put(comment.getLocation().getReportLocation().getStartLine(), comment);
        }
        return new AstInfo<>(
            textDocument,
            rootNode,
            lpReg,
            Collections.unmodifiableMap(suppressMap)
        );
    }

}
