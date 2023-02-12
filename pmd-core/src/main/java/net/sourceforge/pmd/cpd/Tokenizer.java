/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.io.IOException;

import net.sourceforge.pmd.lang.document.TextDocument;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;

public interface Tokenizer {

    PropertyDescriptor<Boolean> CPD_IGNORE_LITERAL_SEQUENCES =
        PropertyFactory.booleanProperty("cpdIgnoreLiteralSequences")
                       .defaultValue(false)
                       .desc("Ignore sequences of literals, eg `0, 0, 0, 0`")
                       .build();

    PropertyDescriptor<Boolean> CPD_ANONYMiZE_LITERALS =
        PropertyFactory.booleanProperty("cpdAnonymizeLiterals")
                       .defaultValue(false)
                       .desc("Anonymize literals. They are still part of the token stream but all literals appear to have the same value.")
                       .build();
    PropertyDescriptor<Boolean> CPD_ANONYMIZE_IDENTIFIERS =
        PropertyFactory.booleanProperty("cpdAnonymizeIdentifiers")
                       .defaultValue(false)
                       .desc("Anonymize identifiers. They are still part of the token stream but all literals appear to have the same value.")
                       .build();


    PropertyDescriptor<Boolean> CPD_IGNORE_IMPORTS =
        PropertyFactory.booleanProperty("cpdIgnoreImports")
                       .defaultValue(true)
                       .desc("Ignore import statements and equivalent (eg using statements in C#).")
                       .build();

    PropertyDescriptor<Boolean> CPD_IGNORE_METADATA =
        PropertyFactory.booleanProperty("cpdIgnoreMetadata")
                       .defaultValue(false)
                       .desc("Ignore metadata such as Java annotations or C# attributes.")
                       .build();


    PropertyDescriptor<Boolean> CPD_CASE_SENSITIVE =
        PropertyFactory.booleanProperty("cpdCaseSensitive")
                       .defaultValue(true)
                       .desc("Whether CPD should ignore the case of tokens. Affects all tokens.")
                       .build();


    String IGNORE_LITERALS = "ignore_literals";
    String IGNORE_IDENTIFIERS = "ignore_identifiers";
    String IGNORE_ANNOTATIONS = "ignore_annotations";

    /**
     * Ignore sequences of literals (e.g, <code>0,0,0,0...</code>).
     */
    String OPTION_IGNORE_LITERAL_SEQUENCES = "net.sourceforge.pmd.cpd.Tokenizer.skipLiteralSequences";
    /**
     * Ignore using directives in C#. The default value is <code>false</code>.
     */
    String IGNORE_USINGS = "ignore_usings";

    /**
     * Enables or disabled skipping of blocks like a pre-processor. It is a
     * boolean property. The default value is <code>true</code>.
     *
     * @see #OPTION_SKIP_BLOCKS_PATTERN
     */
    String OPTION_SKIP_BLOCKS = "net.sourceforge.pmd.cpd.Tokenizer.skipBlocks";
    /**
     * Configures the pattern, to find the blocks to skip. It is a string
     * property and contains of two parts, separated by {@code |}. The first
     * part is the start pattern, the second part is the ending pattern. Default
     * value is "{@code #if 0|#endif}".
     *
     * @see #DEFAULT_SKIP_BLOCKS_PATTERN
     */
    String OPTION_SKIP_BLOCKS_PATTERN = "net.sourceforge.pmd.cpd.Tokenizer.skipBlocksPattern";

    String DEFAULT_SKIP_BLOCKS_PATTERN = "#if 0|#endif";

    void tokenize(TextDocument sourceCode, TokenFactory tokens) throws IOException;

    static void tokenize(Tokenizer tokenizer, TextDocument textDocument, Tokens tokens) throws IOException {
        try (TokenFactory tf = TokenFactory.forFile(textDocument, tokens)) {
            tokenizer.tokenize(textDocument, tf);
        }
    }
}
