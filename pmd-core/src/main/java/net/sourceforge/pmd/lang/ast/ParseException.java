/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccToken;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccTokenDocument;
import net.sourceforge.pmd.util.StringUtil;
import net.sourceforge.pmd.util.document.FileLocation;

public class ParseException extends FileAnalysisException {

    /**
     * This is the last token that has been consumed successfully.  If
     * this object has been created due to a parse error, the token
     * followng this token will (therefore) be the first error token.
     */
    public final @Nullable GenericToken currentToken;

    public ParseException() {
        super();
        this.currentToken = null;
    }

    public ParseException(String message) {
        super(message);
        this.currentToken = null;
    }

    public ParseException(Throwable cause) {
        super(cause);
        this.currentToken = null;
    }

    public ParseException(String message, Throwable cause) {
        super(message, cause);
        this.currentToken = null;
    }

    public ParseException(String message, JavaccToken token) {
        super(message);
        this.currentToken = token;
    }

    /**
     * This constructor is called by Javacc.
     */
    public ParseException(@NonNull JavaccToken currentTokenVal,
                          int[][] expectedTokenSequencesVal) {
        super(makeMessage(currentTokenVal, expectedTokenSequencesVal));
        currentToken = currentTokenVal;
    }

    /**
     * It uses "currentToken" and "expectedTokenSequences" to generate a parse
     * error message and returns it.  If this object has been created
     * due to a parse error, and you do not catch it (it gets thrown
     * from the parser) the correct error message
     * gets displayed.
     */
    private static String makeMessage(@NonNull JavaccToken currentToken,
                                      int[][] expectedTokenSequences) {

        JavaccTokenDocument document = currentToken.getDocument();
        String eol = System.lineSeparator();
        Set<String> expectedBranches = new LinkedHashSet<>();
        int maxSize = 0;
        for (int[] expectedTokenSequence : expectedTokenSequences) {
            StringBuilder expected = new StringBuilder();
            if (maxSize < expectedTokenSequence.length) {
                maxSize = expectedTokenSequence.length;
            }
            for (int i : expectedTokenSequence) {
                expected.append(document.describeKind(i)).append(' ');
            }
            if (expectedTokenSequence[expectedTokenSequence.length - 1] != 0) {
                expected.append("...");
            }
            expectedBranches.add(expected.toString());
        }

        String expected = expectedBranches.stream().collect(Collectors.joining(System.lineSeparator() + "    "));

        StringBuilder retval = new StringBuilder("Encountered ");
        if (maxSize > 1) {
            retval.append('[');
        }
        JavaccToken tok = currentToken.next;
        for (int i = 0; i < maxSize; i++) {
            if (i != 0) {
                retval.append(' ');
            }
            if (tok.kind == 0) {
                retval.append(document.describeKind(0));
                break;
            }

            String kindStr = document.describeKind(tok.kind);

            String image = StringUtil.escapeJava(tok.getImage());

            retval.append(kindStr);

            if (!isEnquotedVersion(kindStr, image)) {
                // then it's an angle-braced name
                retval.deleteCharAt(retval.length() - 1); // remove '>'
                retval.append(": \"");
                retval.append(image);
                retval.append("\">");
            }

            tok = tok.next;
        }

        if (maxSize > 1) {
            retval.append(']');
        }
        FileLocation loc = currentToken.next.getReportLocation();
        retval.append(" at line ").append(loc.getBeginLine()).append(", column ").append(loc.getBeginColumn());
        retval.append('.').append(eol);
        if (expectedTokenSequences.length == 1) {
            retval.append("Was expecting:").append(eol).append("    ");
        } else {
            retval.append("Was expecting one of:").append(eol).append("    ");
        }
        retval.append(expected);
        return retval.toString();
    }

    private static boolean isEnquotedVersion(String kindStr, String image) {
        return kindStr.equals('"' + image + '"');
    }

}
