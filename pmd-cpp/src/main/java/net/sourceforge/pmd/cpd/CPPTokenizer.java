/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.io.IOException;
import java.util.Properties;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.cpd.internal.JavaCCTokenizer;
import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccToken;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccTokenDocument;
import net.sourceforge.pmd.lang.ast.impl.javacc.CharStream;
import net.sourceforge.pmd.lang.ast.impl.javacc.io.EscapeAwareReader;
import net.sourceforge.pmd.lang.cpp.ast.CppEscapeReader;
import net.sourceforge.pmd.lang.cpp.ast.CppTokenKinds;
import net.sourceforge.pmd.util.document.Chars;
import net.sourceforge.pmd.util.document.TextDocument;
import net.sourceforge.pmd.util.document.TextFileContent;

/**
 * The C++ tokenizer.
 */
public class CPPTokenizer extends JavaCCTokenizer {

    private boolean skipBlocks;
    private String skipBlocksStart;
    private String skipBlocksEnd;

    public CPPTokenizer() {
        setProperties(new Properties()); // set the defaults
    }

    // override to make it visible in tests
    @Override
    protected TokenManager<JavaccToken> getLexerForSource(SourceCode sourceCode) throws IOException {
        return super.getLexerForSource(sourceCode);
    }

    /**
     * Sets the possible options for the C++ tokenizer.
     *
     * @param properties
     *            the properties
     * @see #OPTION_SKIP_BLOCKS
     * @see #OPTION_SKIP_BLOCKS_PATTERN
     */
    public void setProperties(Properties properties) {
        skipBlocks = Boolean.parseBoolean(properties.getProperty(OPTION_SKIP_BLOCKS, Boolean.TRUE.toString()));
        if (skipBlocks) {
            String skipBlocksPattern = properties.getProperty(OPTION_SKIP_BLOCKS_PATTERN, DEFAULT_SKIP_BLOCKS_PATTERN);
            String[] split = skipBlocksPattern.split("\\|", 2);
            skipBlocksStart = split[0];
            if (split.length == 1) {
                skipBlocksEnd = split[0];
            } else {
                skipBlocksEnd = split[1];
            }
        }
    }

    /**
     * @param chars Normalized chars
     */
    private CharSequence maybeSkipBlocks(Chars chars) {
        if (!skipBlocks) {
            return chars;
        }

        int i = 0;
        int lastLineStart = 0;
        boolean skip = false;
        StringBuilder filtered = new StringBuilder(chars.length());
        while (i < chars.length()) {
            if (chars.charAt(i) == TextFileContent.NORMALIZED_LINE_TERM_CHAR) {
                Chars lastLine = chars.subSequence(lastLineStart, i);
                Chars trimmed = lastLine.trim();
                if (trimmed.contentEquals(skipBlocksStart, true)) {
                    skip = true;
                } else if (trimmed.contentEquals(skipBlocksEnd, true)) {
                    skip = false;
                }
                if (!skip) {
                    lastLine.appendChars(filtered);
                }
                // always add newline, to preserve line numbers
                filtered.append(TextFileContent.NORMALIZED_LINE_TERM_CHAR);
                lastLineStart = i + 1;
            }
            i++;
        }
        if (lastLineStart < i && !skip) {
            chars.appendChars(filtered, lastLineStart, i - lastLineStart);
        }
        return filtered;
    }


    @Override
    protected JavaccTokenDocument newTokenDoc(TextDocument textDoc) {
        textDoc = TextDocument.readOnlyString(maybeSkipBlocks(textDoc.getText()), textDoc.getDisplayName(), textDoc.getLanguageVersion());
        return new JavaccTokenDocument(textDoc) {
            @Override
            public EscapeAwareReader newReader(Chars text) {
                return new CppEscapeReader(text);
            }

            @Override
            protected @Nullable String describeKindImpl(int kind) {
                return CppTokenKinds.describe(kind);
            }
        };
    }

    @Override
    protected TokenManager<JavaccToken> makeLexerImpl(CharStream sourceCode) {
        return CppTokenKinds.newTokenManager(sourceCode);
    }
}
