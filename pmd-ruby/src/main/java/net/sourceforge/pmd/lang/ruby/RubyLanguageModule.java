/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ruby;

import net.sourceforge.pmd.cpd.AnyTokenizer;
import net.sourceforge.pmd.cpd.Tokenizer;
import net.sourceforge.pmd.lang.CpdOnlyLanguageModuleBase;
import net.sourceforge.pmd.lang.LanguagePropertyBundle;

/**
 * Language implementation for Ruby.
 *
 * @author Zev Blut zb@ubit.com
 */
public class RubyLanguageModule extends CpdOnlyLanguageModuleBase {

    public RubyLanguageModule() {
        super(LanguageMetadata.withId("ruby").name("Ruby").extensions("rb", "cgi", "class"));
    }

    @Override
    public Tokenizer createCpdTokenizer(LanguagePropertyBundle bundle) {
        return new AnyTokenizer("#");
    }
}
