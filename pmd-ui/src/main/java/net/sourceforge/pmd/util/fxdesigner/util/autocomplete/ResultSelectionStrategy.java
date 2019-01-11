/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.util.autocomplete;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;


/**
 * Selects the best match results given a list of candidates and a query.
 * We can abstract that later if we need it. E.g. we could provide more
 * informed guesses based on what nodes are frequently found in that position
 * in known XPath queries, or parse JJDoc output and suggest nodes that we
 * know can be children of the previous node.
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
class ResultSelectionStrategy {

    private static final int MIN_QUERY_LENGTH = 1;


    Stream<MatchResult> filterResults(List<String> candidates, String query, int limit) {
        if (query.length() < MIN_QUERY_LENGTH) {
            return Stream.empty();
        }

        return candidates.stream()
                         .map(cand -> computeMatchingSegments(cand, query, false))
                         .sorted(Comparator.comparingInt(MatchResult::getScore).reversed())
                         // second pass is done only on those we know we'll keep
                         .limit(limit)
                         .map(prev -> {
                             // try to break ties between the top results, e.g.
                             //
                             // without second pass, we have a tie:
                             //      query       coit
                             //      candidate   ClassOrInterfaceType            : 32
                             //      candidate   ClassOrInterfaceBodyDeclaration : 32
                             //                  ^    ^ ^ ^
                             // with second pass:
                             //
                             //      query       coit
                             //      candidate   ClassOrInterfaceType            : 40 -> and indeed it's a better match
                             //                  ^    ^ ^        ^
                             //      candidate   ClassOrInterfaceDeclaration     : 32
                             //                  ^    ^ ^ ^

                             MatchResult refined = computeMatchingSegments(prev.getNodeName(), query, true);
                             // keep the best
                             return refined.getScore() > prev.getScore() ? refined : prev;
                         })
                         .sorted(Comparator.comparingInt(MatchResult::getScore).reversed());


    }



    private Text makeHighlightedText(String match) {
        Text matchLabel = new Text(match);
        matchLabel.getStyleClass().add("autocomplete-match");
        return matchLabel;
    }


    /**
     * Computes a match result with its score for the candidate and query.
     *
     * @param candidate           Candidate string
     * @param query               Query
     * @param matchOnlyWordStarts Whether to only match word starts. This is a more unfair strategy
     *                            that can be used to break ties.
     */
    private MatchResult computeMatchingSegments(String candidate, String query, boolean matchOnlyWordStarts) {
        // Performs a left-to-right scan of the candidate string,
        // trying to assign each of the chars of the query to a
        // location in the string (also left-to-right)

        // Score is computed a bit ad-hoc:
        // +2 for a lonely char
        // +10 for a character matching the start of a camelcase word (an uppercase char)
        // the longer the submatch, the higher the match counts
        // submatches occurring at the beginning of a word count more than in other places
        // chars from the query that remain at the end penalise the score

        // This algorithm is greedy and doesn't always select the best possible match result
        // The second pass is even more unfair and allows to break ties

        int candIdx = 0;  // current index in the candidate
        int queryIdx = 0; // current index in the query
        int score = 0;

        // these are reset when a submatch ends
        int lastMatchEnd = 0;
        int curMatchStart = -1;
        int matchLength = 0;
        boolean isStartOfWord = true; // whether the current submatch is at the start of a camelcase word

        TextFlow flow = new TextFlow(); // result

        while (candIdx < candidate.length() && queryIdx < query.length()) {

            char candChar = candidate.charAt(candIdx);
            char queryChar = query.charAt(queryIdx);

            if (Character.toLowerCase(candChar) == Character.toLowerCase(queryChar)) {
                // it's the same char

                matchLength++;

                if (curMatchStart == -1) {
                    // start of a match

                    if (matchOnlyWordStarts && !isStartOfWord && !Character.isUpperCase(candChar)) {
                        // not the start of a word, don't record it as a match
                        candIdx++;
                        continue;
                    }

                    // set match start to current
                    curMatchStart = candIdx;

                    if (Character.isUpperCase(candChar)) {
                        // start of a match on the start of a word
                        // e.g. query       coit
                        //      candidate   ClassOrInterfaceType
                        //                  ^    ^ ^ ^
                        //      score       34

                        isStartOfWord = true;
                        score += 10;
                    } else {
                        isStartOfWord = false;
                        score += 2;
                    }

                } else {
                    // match is running-on

                    // e.g. query       wur
                    //      candidate   Würstchen
                    //                  ^^^
                    //      candidate   BratWurst
                    //                      ^^^
                    //      score       40 = 4 + 8 + 16 + (start of word : 10)
                    //------------------
                    //      query       wur
                    //      candidate   Bratwurst
                    //                      ^^^
                    //      score       14 = 2 + 4 + 8
                    //------------------
                    //      query       wur
                    //      candidate   zweihundert
                    //                   ^   ^   ^
                    //      score       6 = 2 + 2 + 2

                    int multiplier = isStartOfWord ? 4 : 2;
                    score += matchLength * multiplier;
                }

                candIdx++;
                queryIdx++;

            } else {
                // the current chars don't match

                if (curMatchStart != -1) {
                    // end of a match
                    // assert matchLength > 0;

                    String before = candidate.substring(lastMatchEnd, curMatchStart);
                    String match = candidate.substring(curMatchStart, curMatchStart + matchLength);

                    if (before.length() > 0) {
                        flow.getChildren().add(new Text(before));
                    }

                    flow.getChildren().add(makeHighlightedText(match));

                    lastMatchEnd = curMatchStart + matchLength;
                }

                candIdx++;
                // stay on same query index

                // reset match
                curMatchStart = -1;
                matchLength = 0;
                isStartOfWord = false;
            }
        }

        // end of loop

        if (curMatchStart != -1 && candIdx <= candidate.length()) {
            // the query ends inside a match, we must complete the current match

            String before = candidate.substring(lastMatchEnd, curMatchStart);
            String match = candidate.substring(curMatchStart, candIdx);

            if (before.length() > 0) {
                flow.getChildren().add(new Text(before));
            }

            flow.getChildren().add(makeHighlightedText(match));

            lastMatchEnd = candIdx; // shift
        }

        // add the rest of the candidate
        String rest = candidate.substring(lastMatchEnd);
        if (!rest.isEmpty()) {
            flow.getChildren().add(new Text(rest));
        }

        int remainingChars = query.length() - queryIdx;

        if (remainingChars > 0) {
            // some chars were not found, penalize that
            score -= remainingChars * 2;
        }

        return new MatchResult(score, candidate, flow);
    }

}
