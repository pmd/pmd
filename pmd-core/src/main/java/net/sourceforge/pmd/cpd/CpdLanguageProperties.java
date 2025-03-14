/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;

/**
 * These are language properties common to multiple {@link CpdCapableLanguage}s.
 *
 * @see net.sourceforge.pmd.lang.LanguagePropertyBundle
 */
public final class CpdLanguageProperties {
    private CpdLanguageProperties() {
        // utility class
    }

    public static final PropertyDescriptor<Boolean> CPD_IGNORE_LITERAL_SEQUENCES =
        PropertyFactory.booleanProperty("cpdIgnoreLiteralSequences")
                       .defaultValue(false)
                       .desc("Ignore sequences of literals, eg `0, 0, 0, 0`")
                       .build();
    public static final PropertyDescriptor<Boolean> CPD_IGNORE_LITERAL_AND_IDENTIFIER_SEQUENCES =
        PropertyFactory.booleanProperty("cpdIgnoreLiteralAndIdentifierSequences")
                       .defaultValue(false)
                       .desc("Ignore sequences of literals and identifiers, eg `a, b, 0, 0`")
                       .build();
    public static final PropertyDescriptor<Boolean> CPD_ANONYMIZE_LITERALS =
        PropertyFactory.booleanProperty("cpdAnonymizeLiterals")
                       .defaultValue(false)
                       .desc("Anonymize literals. They are still part of the token stream but all literals appear to have the same value.")
                       .build();
    public static final PropertyDescriptor<Boolean> CPD_ANONYMIZE_IDENTIFIERS =
        PropertyFactory.booleanProperty("cpdAnonymizeIdentifiers")
                       .defaultValue(false)
                       .desc("Anonymize identifiers. They are still part of the token stream but all identifiers appear to have the same value.")
                       .build();
    public static final PropertyDescriptor<Boolean> CPD_IGNORE_IMPORTS =
        PropertyFactory.booleanProperty("cpdIgnoreImports")
                       .defaultValue(true)
                       .desc("Ignore import statements and equivalent (eg using statements in C#).")
                       .build();
    public static final PropertyDescriptor<Boolean> CPD_IGNORE_METADATA =
        PropertyFactory.booleanProperty("cpdIgnoreMetadata")
                       .defaultValue(false)
                       .desc("Ignore metadata such as Java annotations or C# attributes.")
                       .build();
}
