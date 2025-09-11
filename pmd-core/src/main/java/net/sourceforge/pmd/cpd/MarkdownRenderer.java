/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import net.sourceforge.pmd.lang.document.Chars;
import net.sourceforge.pmd.lang.document.FileLocation;

/**
 * @since 7.17.0
 */
public class MarkdownRenderer implements CPDReportRenderer {

    //separator must be surrounded by empty lines to be rendered properly
    private static final String SEPARATOR = "\n---\n\n";
    private static final String FOUND_DUPLICATION_TEMPLATE = "Found a %d line (%d tokens) duplication in the following files:\n";
    private static final String STARTING_AT_LINE_TEMPLATE = "* Starting at line %d of %s\n";

    @Override
    public void render(CPDReport report, Writer writer) throws IOException {
        Iterator<Match> matches = report.getMatches().iterator();
        if (matches.hasNext()) {
            renderDuplication(report, writer, matches.next());
        }

        while (matches.hasNext()) {
            Match match = matches.next();
            writer.write(SEPARATOR);
            renderDuplication(report, writer, match);
        }

        writer.flush();
    }

    private void renderDuplication(CPDReport report, Writer writer, Match match) throws IOException {
        writer.append(String.format(FOUND_DUPLICATION_TEMPLATE, match.getLineCount(), match.getTokenCount()));

        for (Mark mark : match) {
            FileLocation loc = mark.getLocation();
            writer.append(String.format(STARTING_AT_LINE_TEMPLATE, loc.getStartLine(), report.getDisplayName(loc.getFileId())));
        }

        Mark firstMark = match.getFirstMark();
        String filename = firstMark.getFileId().getFileName().toLowerCase(Locale.ROOT);
        String highlightLanguage = null;
        if (filename.endsWith(".java") || filename.endsWith(".jav")) {
            highlightLanguage = "java";
        }

        Chars source = report.getSourceCodeSlice(firstMark);
        final MarkdownCodeBlock markdownCodeBlock = new MarkdownCodeBlock(source, highlightLanguage);
        writer.append(markdownCodeBlock.toString());
    }

    static class MarkdownCodeBlock {
        private static final int MIN_CODE_FENCE_LENGTH = 3;
        private static final Pattern CODE_FENCE_PATTERN = Pattern.compile(String.format("`{%d,}", MIN_CODE_FENCE_LENGTH));

        private final CharSequence source;
        private final String highlightLanguage;

        MarkdownCodeBlock(CharSequence source, String highlightLanguage) {
            this.source = source;
            this.highlightLanguage = highlightLanguage != null ? highlightLanguage : "";
        }

        private int calculateFenceLength() {
            // ` and `` are already escaped by the standard code fence
            final String defaultCodeFence = StringUtils.repeat("`", MIN_CODE_FENCE_LENGTH);
            if (!source.toString().contains(defaultCodeFence)) {
                return MIN_CODE_FENCE_LENGTH;
            }

            int maximumCodeFenceLength = defaultCodeFence.length();
            final Matcher matcher = CODE_FENCE_PATTERN.matcher(source);
            while (matcher.find()) {
                final String codeFenceMatch = matcher.group();
                maximumCodeFenceLength = Math.max(maximumCodeFenceLength, codeFenceMatch.length());
            }

            return maximumCodeFenceLength + 1;
        }

        @Override
        public String toString() {
            final int codeFenceLength = calculateFenceLength();
            String codeFence = StringUtils.repeat("`", codeFenceLength);

            final String codeBlock =
                    codeFence.concat(highlightLanguage)
                             .concat("\n")
                             .concat(source.toString())
                             .concat(codeFence);
            return StringUtils.wrap(codeBlock, "\n");
        }
    }
}
