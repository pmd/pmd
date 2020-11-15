/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast;

import java.util.Collections;
import java.util.Map;

import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.internal.util.AssertionUtil;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.ast.Parser.ParserTask;

/**
 * The output of {@link Parser#parse(ParserTask)}.
 *
 * @param <T> Type of root nodes
 */
public final class AstInfo<T extends RootNode> {

    private final String filename;
    private final LanguageVersion languageVersion;
    private final String sourceText;
    private final T rootNode;
    private final Map<Integer, String> suppressionComments;


    public AstInfo(ParserTask task, T rootNode) {
        this(task, rootNode, Collections.emptyMap());
    }

    public AstInfo(ParserTask task, T rootNode, Map<Integer, String> suppressionComments) {
        this(task.getFileDisplayName(),
             task.getLanguageVersion(),
             task.getSourceText(),
             rootNode,
             suppressionComments);
    }

    public AstInfo(String filename,
                   LanguageVersion languageVersion,
                   String sourceText,
                   T rootNode,
                   Map<Integer, String> suppressionComments) {
        this.filename = AssertionUtil.requireParamNotNull("file name", filename);
        this.languageVersion = AssertionUtil.requireParamNotNull("language version", languageVersion);
        this.sourceText = AssertionUtil.requireParamNotNull("text", sourceText);
        this.rootNode = AssertionUtil.requireParamNotNull("root node", rootNode);
        this.suppressionComments = AssertionUtil.requireParamNotNull("suppress map", suppressionComments);
    }


    public T getRootNode() {
        return rootNode;
    }

    public String getFileName() {
        return filename;
    }

    public String getSourceText() {
        return sourceText;
    }

    public LanguageVersion getLanguageVersion() {
        return languageVersion;
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
