/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import net.sourceforge.pmd.util.StringUtil;

/**
 * Simple tokenization into words and separators. Can ignore end-of-line
 * comments and recognize double/single quoted string literals. It is
 * not a goal to be very customizable, or have very high quality.
 * Higher-quality lexers should be implemented with a lexer generator.
 *
 * <p>In PMD 7, this replaces AbstractTokenizer, which provided nearly
 * no more functionality and whose API was hard to update.
 */
public class AnyTokenizer implements Tokenizer {

    private static final Pattern DEFAULT_PATTERN = makePattern("");

    private static Pattern makePattern(String singleLineCommentStart) {
        return Pattern.compile(
            "\\w++" // either a word
                + eolCommentFragment(singleLineCommentStart) // a comment
                + "|[^\"'\\s]" // a single separator char
                + "|\"(?:[^\"\\\\]++|\\\\\")*+\"" // a double-quoted string
                + "|'(?:[^'\\\\]++|\\\\')*+'" // a single-quoted string
                + "|\n" // or a newline (to count lines), note that sourcecode normalizes line endings
        );
    }

    private final Pattern pattern;
    private final String commentStart;

    public AnyTokenizer() {
        this(DEFAULT_PATTERN, "");
    }

    public AnyTokenizer(String eolCommentStart) {
        this(makePattern(eolCommentStart), eolCommentStart);
    }

    private AnyTokenizer(Pattern pattern, String commentStart) {
        this.pattern = pattern;
        this.commentStart = commentStart;
    }

    private static String eolCommentFragment(String start) {
        if (StringUtils.isBlank(start)) {
            return "";
        } else {
            return "|(?:" + Pattern.quote(start) + "[^\n]++)"; // note: sourcecode normalizes line endings
        }
    }

    @Override
    public void tokenize(SourceCode sourceCode, Tokens tokenEntries) {
        StringBuilder text = sourceCode.getCodeBuffer();
        Matcher matcher = pattern.matcher(text);
        int lineNo = 1;
        int lastLineStart = 0;
        try {
            while (matcher.find()) {
                String image = matcher.group();
                if (isComment(image)) {
                    continue;
                } else if (StringUtils.isWhitespace(image)) {
                    lineNo++;
                    lastLineStart = matcher.end();
                    continue;
                }

                int bcol = 1 + matcher.start() - lastLineStart; // + 1 because columns are 1 based
                int ecol = StringUtil.columnNumberAt(image, image.length()); // this already outputs a 1-based column
                tokenEntries.add(new TokenEntry(image, sourceCode.getFileName(), lineNo, bcol, ecol));
            }
        } finally {
            tokenEntries.add(TokenEntry.EOF);
        }
    }

    private boolean isComment(String tok) {
        return !commentStart.isEmpty() && tok.startsWith(commentStart);
    }
}
