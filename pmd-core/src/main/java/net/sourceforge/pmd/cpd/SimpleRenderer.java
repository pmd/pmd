/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Iterator;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.cpd.renderer.CPDRenderer;
import net.sourceforge.pmd.util.StringUtil;

public class SimpleRenderer implements Renderer, CPDRenderer {

    private String separator;
    private boolean trimLeadingWhitespace;

    public static final String DEFAULT_SEPARATOR = "=====================================================================";

    public SimpleRenderer() {
        this(false);
    }

    public SimpleRenderer(boolean trimLeadingWhitespace) {
        this(DEFAULT_SEPARATOR);
        this.trimLeadingWhitespace = trimLeadingWhitespace;
    }

    public SimpleRenderer(String theSeparator) {
        separator = theSeparator;
    }

    private void renderOn(Writer writer, Match match) throws IOException {

        writer.append("Found a ").append(String.valueOf(match.getLineCount())).append(" line (").append(String.valueOf(match.getTokenCount()))
                .append(" tokens) duplication in the following files: ").append(PMD.EOL);

        for (Iterator<Mark> occurrences = match.iterator(); occurrences.hasNext();) {
            Mark mark = occurrences.next();
            writer.append("Starting at line ").append(String.valueOf(mark.getBeginLine())).append(" of ").append(mark.getFilename())
                    .append(PMD.EOL);
        }

        writer.append(PMD.EOL); // add a line to separate the source from the desc above

        String source = match.getSourceCodeSlice();

        if (trimLeadingWhitespace) {
            String[] lines = source.split('[' + PMD.EOL + ']');
            int trimDepth = StringUtil.maxCommonLeadingWhitespaceForAll(lines);
            if (trimDepth > 0) {
                lines = StringUtil.trimStartOn(lines, trimDepth);
            }
            for (int i = 0; i < lines.length; i++) {
                writer.append(lines[i]).append(PMD.EOL);
            }
            return;
        }

        writer.append(source).append(PMD.EOL);
    }

    @Override
    public String render(Iterator<Match> matches) {
        StringWriter writer = new StringWriter(300);
        try {
            render(matches, writer);
        } catch (IOException ignored) {
            // Not really possible with a StringWriter
        }
        return writer.toString();
    }

    @Override
    public void render(Iterator<Match> matches, Writer writer) throws IOException {
        if (matches.hasNext()) {
            renderOn(writer, matches.next());
        }

        Match match;
        while (matches.hasNext()) {
            match = matches.next();
            writer.append(separator).append(PMD.EOL);
            renderOn(writer, match);
        }
        writer.flush();
    }
}
