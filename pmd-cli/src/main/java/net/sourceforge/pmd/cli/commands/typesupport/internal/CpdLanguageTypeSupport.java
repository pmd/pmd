/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cli.commands.typesupport.internal;

import java.util.Arrays;
import java.util.Iterator;

import net.sourceforge.pmd.cpd.Language;
import net.sourceforge.pmd.cpd.LanguageFactory;

import picocli.CommandLine.ITypeConverter;

/**
 * Provider of candidates / conversion support for supported CPD languages.
 * 
 * Beware, the help will report this on runtime, and be accurate to available
 * modules in the classpath, but autocomplete will include all available at build time.
 */
public class CpdLanguageTypeSupport implements ITypeConverter<Language>, Iterable<String> {

    @Override
    public Iterator<String> iterator() {
        return Arrays.stream(LanguageFactory.supportedLanguages).iterator();
    }
    
    @Override
    public Language convert(final String languageString) {
        // TODO : If an unknown value is passed, AnyLanguage is returned silently…
        return LanguageFactory.createLanguage(languageString);
    }
}
