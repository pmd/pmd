/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.util.autocomplete;

import java.util.stream.Stream;


/**
 * Language specific tool to suggest auto-completion results.
 */
public interface CompletionResultSource {


    /**
     * Returns a stream of pre-built TextFlows sorted by relevance.
     * The stream will contain at most "limit" elements.
     */
    Stream<CompletionResult> getSortedMatches(String input, int limit);


}
