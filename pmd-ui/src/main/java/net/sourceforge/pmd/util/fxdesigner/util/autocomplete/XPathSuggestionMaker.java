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
public final class XPathSuggestionMaker {

    private final NodeNameFinder myNameFinder;
    private final ResultSelectionStrategy mySelectionStrategy = new ResultSelectionStrategy();
    // if we don't cache them the classpath exploration is done on each character typed
    private static Map<Language, XPathSuggestionMaker> byLanguage = new HashMap<>();

    private XPathSuggestionMaker(NodeNameFinder nodeNameFinder) {
        this.myNameFinder = nodeNameFinder;
    }


    /**
     * Returns a stream of pre-built TextFlows sorted by relevance.
     */
    public Stream<MatchResult> getSortedMatches(String input) {
        return mySelectionStrategy.filterResults(myNameFinder.getNodeNames(), input);
    }

    /**
     * Gets a suggestion tool suited to the given language.
     */
    public static XPathSuggestionMaker forLanguage(Language language) {
        return byLanguage.computeIfAbsent(language, l -> new XPathSuggestionMaker(NodeNameFinder.forLanguage(l)));
    }
}
