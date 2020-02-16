/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Properties;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.cpd.internal.JavaCCTokenizer;
import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.cpp.CppTokenManager;
import net.sourceforge.pmd.util.IOUtil;

/**
 * The C++ tokenizer.
 */
public class CPPTokenizer extends JavaCCTokenizer {

    private boolean skipBlocks = true;
    private String skipBlocksStart;
    private String skipBlocksEnd;

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

    private String maybeSkipBlocks(String test) throws IOException {
        if (!skipBlocks) {
            return test;
        }

        try (BufferedReader reader = new BufferedReader(new StringReader(test))) {
            StringBuilder filtered = new StringBuilder(test.length());
            String line;
            boolean skip = false;
            while ((line = reader.readLine()) != null) {
                if (skipBlocksStart.equalsIgnoreCase(line.trim())) {
                    skip = true;
                } else if (skip && skipBlocksEnd.equalsIgnoreCase(line.trim())) {
                    skip = false;
                }
                if (!skip) {
                    filtered.append(line);
                }
                // always add a new line to keep the line-numbering
                filtered.append(PMD.EOL);
            }
            return filtered.toString();
        }
    }

    @Override
    protected TokenManager getLexerForSource(SourceCode sourceCode) {
        try {
            StringBuilder buffer = sourceCode.getCodeBuffer();
            return new CppTokenManager(IOUtil.skipBOM(new StringReader(maybeSkipBlocks(buffer.toString()))));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
