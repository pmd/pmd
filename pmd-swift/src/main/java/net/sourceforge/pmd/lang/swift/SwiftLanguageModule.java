/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.swift;

import net.sourceforge.pmd.cpd.Tokenizer;
import net.sourceforge.pmd.lang.LanguagePropertyBundle;
import net.sourceforge.pmd.lang.impl.SimpleLanguageModuleBase;
import net.sourceforge.pmd.lang.swift.cpd.SwiftTokenizer;

/**
 * Language Module for Swift
 */
public class SwiftLanguageModule extends SimpleLanguageModuleBase {

    /** The name. */
    public static final String NAME = "Swift";
    /** The terse name. */
    public static final String TERSE_NAME = "swift";

    /**
     * Create a new instance of Swift Language Module.
     */
    public SwiftLanguageModule() {
        super(LanguageMetadata.withId(TERSE_NAME).name(NAME).extensions("swift"), new SwiftHandler());
    }

    @Override
    public Tokenizer createCpdTokenizer(LanguagePropertyBundle bundle) {
        return new SwiftTokenizer();
    }
}
