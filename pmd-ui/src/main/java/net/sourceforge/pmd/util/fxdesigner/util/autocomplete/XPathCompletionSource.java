/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.util.autocomplete;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import net.sourceforge.pmd.lang.Language;


/**
 * Language specific tool to suggest auto-completion results.
 */
public final class XPathCompletionSource implements CompletionResultSource {

    private final NodeNameFinder myNameFinder;
    private final ResultSelectionStrategy mySelectionStrategy = new ResultSelectionStrategy();
    // if we don't cache them the classpath exploration is done on each character typed
    private static Map<Language, XPathCompletionSource> byLanguage = new HashMap<>();

    private XPathCompletionSource(NodeNameFinder nodeNameFinder) {
        this.myNameFinder = nodeNameFinder;
    }


    /**
     * Returns a stream of pre-built TextFlows sorted by relevance.
     * The stream will contain at most "limit" elements.
     */
    @Override
    public Stream<CompletionResult> getSortedMatches(String input, int limit) {
        return mySelectionStrategy.filterResults(myNameFinder.getNodeNames(), input, limit);
    }

    /**
     * Gets a suggestion tool suited to the given language.
     */
    public static XPathCompletionSource forLanguage(Language language) {
        return byLanguage.computeIfAbsent(language, l -> new XPathCompletionSource(NodeNameFinder.forLanguage(l)));
    }
}
