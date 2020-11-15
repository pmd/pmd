/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast;

import java.util.Collections;
import java.util.Map;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.internal.util.AssertionUtil;
import net.sourceforge.pmd.lang.ast.Parser.ParserTask;
import net.sourceforge.pmd.util.document.TextDocument;

/**
 * The output of {@link Parser#parse(ParserTask)}.
 *
 * @param <T> Type of root nodes
 */
public final class AstInfo<T extends RootNode> {

    private final TextDocument textDocument;
    private final T rootNode;
    private final Map<Integer, String> suppressionComments;


    public AstInfo(ParserTask task, T rootNode) {
        this(task, rootNode, Collections.emptyMap());
    }

    public AstInfo(ParserTask task, T rootNode, Map<Integer, String> suppressionComments) {
        this(task.getTextDocument(), rootNode, suppressionComments);
    }

    public AstInfo(TextDocument textDocument,
                   T rootNode,
                   Map<Integer, String> suppressionComments) {
        this.textDocument = AssertionUtil.requireParamNotNull("text document", textDocument);
        this.rootNode = AssertionUtil.requireParamNotNull("root node", rootNode);
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
     * Returns the map of line numbers to suppression / review comments.
     * Only single line comments are considered, that start with the configured
     * "suppressMarker", which by default is "PMD". The text after the
     * suppressMarker is used as a "review comment" and included in this map.
     *
     * <p>
     * This map is later used to determine, if a violation is being suppressed.
     * It is suppressed, if the line of the violation is contained in this suppress map.
     *
     * @return map of the suppress lines with the corresponding review comments.
     */
    @Experimental
    public Map<Integer, String> getSuppressionComments() {
        return suppressionComments;
    }

}
