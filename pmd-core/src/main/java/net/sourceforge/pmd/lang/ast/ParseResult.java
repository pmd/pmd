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
public final class ParseResult<T extends RootNode> {

    private final T rootNode;
    private final AstInfo astInfo;

    public ParseResult(T rootNode, AstInfo info) {
        this.rootNode = rootNode;
        this.astInfo = info;
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
