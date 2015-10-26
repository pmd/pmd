/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.cpd;

import java.io.IOException;

public interface Tokenizer {
    String IGNORE_LITERALS = "ignore_literals";
    String IGNORE_IDENTIFIERS = "ignore_identifiers";
    String IGNORE_ANNOTATIONS = "ignore_annotations";

    /**
     * Ignore using directives in C#.
     * The default value is <code>false</code>.
     */
    String IGNORE_USINGS = "ignore_usings";

    /**
     * Enables or disabled skipping of blocks like a pre-processor.
     * It is a boolean property.
     * The default value is <code>true</code>.
     * @see #OPTION_SKIP_BLOCKS_PATTERN
     */
    String OPTION_SKIP_BLOCKS = "net.sourceforge.pmd.cpd.Tokenizer.skipBlocks";
    /**
     * Configures the pattern, to find the blocks to skip.
     * It is a string property and contains of two parts, separated by {@code |}.
     * The first part is the start pattern, the second part is the ending pattern.
     * Default value is "{@code #if 0|#endif}".
     * @see #DEFAULT_SKIP_BLOCKS_PATTERN
     */
    String OPTION_SKIP_BLOCKS_PATTERN = "net.sourceforge.pmd.cpd.Tokenizer.skipBlocksPattern";

    String DEFAULT_SKIP_BLOCKS_PATTERN = "#if 0|#endif";

    void tokenize(SourceCode sourceCode, Tokens tokenEntries) throws IOException;
}
