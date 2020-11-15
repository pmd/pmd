/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast;

import java.util.Collections;
import java.util.Map;

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


    public AstInfo(ParserTask task,
                   T rootNode,
                   Map<Integer, String> suppressionComments) {
        this.filename = task.getFileDisplayName();
        this.sourceText = task.getSourceText();
        this.languageVersion = task.getLanguageVersion();
        this.rootNode = rootNode;
        this.suppressionComments = suppressionComments;
    }

    public AstInfo(String filename,
                   LanguageVersion languageVersion,
                   String sourceText,
                   T rootNode,
                   Map<Integer, String> suppressionComments) {
        this.filename = filename;
        this.languageVersion = languageVersion;
        this.sourceText = sourceText;
        this.rootNode = rootNode;
        this.suppressionComments = suppressionComments;
    }

    public AstInfo(ParserTask task, T rootNode) {
        this(task, rootNode, Collections.emptyMap());
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

    public Map<Integer, String> getSuppressionComments() {
        return suppressionComments;
    }

    public LanguageVersion getLanguageVersion() {
        return languageVersion;
    }
}
