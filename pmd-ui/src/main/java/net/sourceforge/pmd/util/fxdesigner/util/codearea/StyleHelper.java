/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.util.codearea;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.fxmisc.richtext.model.Paragraph;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import net.sourceforge.pmd.lang.ast.Node;


/**
 * @author Clément Fournier
 * @since 6.4.0
 */
class StyleHelper {
    private static final Pattern TAB_INDENT = Pattern.compile("^(\t*).*$");
    private final CustomCodeArea codeArea;


    StyleHelper(CustomCodeArea codeArea) {
        this.codeArea = codeArea;
    }


    private int getAbsolutePosition(int line, int column) {
        return codeArea.getAbsolutePosition(line - 1, column) - indentationOffset(line - 1);
    }


    // CodeArea counts a tab as 1 column width but displays it as 8 columns width.
    // PMD counts it correctly as 8 columns, so we must offset the position
    private int indentationOffset(int paragraph) {
        Paragraph<Collection<String>, String, Collection<String>> p = codeArea.getParagraph(paragraph);
        Matcher m = TAB_INDENT.matcher(p.getText());
        if (m.matches()) {
            return m.group(1).length() * 7;
        }
        return 0;
    }


    // FIXME this is not very resilient to brutal changes in the text occurring during the computation
    Collection<StyleSpans<Collection<String>>> style(Collection<? extends Node> nodes, Set<String> cssClasses, Function<Node, Set<String>> extraClassesFinder) {
        if (nodes.isEmpty() || cssClasses.isEmpty()) {
            return Collections.emptySet();
        }

        Stack<StyleSpans<Collection<String>>> result = new Stack<>();

        // Sort in document order
        final List<Node> sortedNodes = new ArrayList<>(nodes);
        sortedNodes.sort(Comparator.comparingInt(Node::getBeginLine).thenComparingInt(Node::getBeginColumn));

        final StyleSpansBuilder<Collection<String>> builder = new StyleSpansBuilder<>();

        boolean first = true;
        int offset = 0;
        int spanLength = 0;
        Set<String> previousClasses = Collections.emptySet();

        Set<Node> nextPass = new HashSet<>();

        for (Node n : sortedNodes) {
            int newOffset = getAbsolutePosition(n.getBeginLine(), n.getBeginColumn() - 1);

            Set<String> newClasses = new HashSet<>(cssClasses);
            newClasses.addAll(extraClassesFinder.apply(n));

            if (newOffset < offset + spanLength) {
                // This node overlaps with the previous one

                if (newClasses.equals(previousClasses)) {
                    // accumulate the length and move on
                    spanLength = Math.max(getAbsolutePosition(n.getBeginLine(), n.getBeginColumn()) - offset, spanLength);
                } else {
                    // We have overlap and different classes, we'll treat that on the next pass
                    // TODO This is a lazy technique to have the overlay work done by RichtextFX instead of here
                    nextPass.add(n);
                }
                continue;
            }

            // no overlap

            if (!first) {
                // This is the span computed at the previous iteration
                builder.add(previousClasses, spanLength);
            }
            first = false;

            // This is the in-between, empty span
            builder.add(Collections.emptySet(), newOffset - (offset + spanLength));

            offset = newOffset;
            previousClasses = newClasses;

            spanLength = getAbsolutePosition(n.getEndLine(), n.getEndColumn()) - offset;
        }

        // this is the last span to be styled
        builder.add(previousClasses, spanLength);

        // add the remainder
        builder.add(Collections.emptySet(), codeArea.getLength() - (offset + spanLength));

        result.push(builder.create());
        result.addAll(style(nextPass, cssClasses, extraClassesFinder));

        return result;
    }


}
