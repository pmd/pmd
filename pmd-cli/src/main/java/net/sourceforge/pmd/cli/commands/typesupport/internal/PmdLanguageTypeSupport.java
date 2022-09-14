/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cli.commands.typesupport.internal;

import java.util.Iterator;

import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageRegistry;

import picocli.CommandLine.ITypeConverter;
import picocli.CommandLine.TypeConversionException;

/**
 * Provider of candidates / conversion support for supported PMD languages.
 * 
 * Beware, the help will report this on runtime, and be accurate to available
 * modules in the classpath, but autocomplete will include all available at build time.
 */
public class PmdLanguageTypeSupport implements ITypeConverter<Language>, Iterable<String> {

    @Override
    public Language convert(final String value) throws Exception {
        return LanguageRegistry.PMD.getLanguages().stream()
                .filter(l -> l.getTerseName().equals(value)).findFirst()
                .orElseThrow(() -> new TypeConversionException("Unknown language: " + value));
    }

    @Override
    public Iterator<String> iterator() {
        return LanguageRegistry.PMD.getLanguages().stream().map(Language::getTerseName).iterator();
    }
}
