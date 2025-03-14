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
 * Provider of candidates / conversion support for supported PMD/CPD languages.
 */
public class LanguageTypeSupport implements ITypeConverter<Language>, Iterable<String> {

    private final LanguageRegistry languageRegistry;

    public LanguageTypeSupport(LanguageRegistry languageRegistry) {
        this.languageRegistry = languageRegistry;
    }

    @Override
    public Language convert(final String value) {
        Language lang = languageRegistry.getLanguageById(value);
        if (lang == null) {
            throw new TypeConversionException("Unknown language: " + value);
        }
        return lang;
    }

    @Override
    public Iterator<String> iterator() {
        return languageRegistry.getLanguages().stream().map(Language::getId).iterator();
    }
}
