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

    PropertyDescriptor<Boolean> CPD_ANONYMIZE_LITERALS =
        PropertyFactory.booleanProperty("cpdAnonymizeLiterals")
                       .defaultValue(false)
                       .desc("Anonymize literals. They are still part of the token stream but all literals appear to have the same value.")
                       .build();
    PropertyDescriptor<Boolean> CPD_ANONYMIZE_IDENTIFIERS =
        PropertyFactory.booleanProperty("cpdAnonymizeIdentifiers")
                       .defaultValue(false)
                       .desc("Anonymize identifiers. They are still part of the token stream but all identifiers appear to have the same value.")
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
                       .defaultValue(false)
                       .desc("Whether CPD should ignore the case of tokens. Affects all tokens.")
                       .build();


    @Deprecated // TODO what to do with this?
    String DEFAULT_SKIP_BLOCKS_PATTERN = "#if 0|#endif";

    void tokenize(TextDocument document, TokenFactory tokens) throws IOException;

    static void tokenize(Tokenizer tokenizer, TextDocument textDocument, Tokens tokens) throws IOException {
        try (TokenFactory tf = TokenFactory.forFile(textDocument, tokens)) {
            tokenizer.tokenize(textDocument, tf);
        }
    }
}
