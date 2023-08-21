/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cli.commands.typesupport.internal;

import java.util.Iterator;
import java.util.Objects;
import java.util.TreeSet;
import java.util.stream.Collectors;

import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;

import picocli.CommandLine.ITypeConverter;
import picocli.CommandLine.TypeConversionException;

/**
 * Provider of candidates for valid language-version combinations.
 * 
 * Beware, the help will report this on runtime, and be accurate to available
 * modules in the classpath, but autocomplete will include all available at build time.
 */
public class PmdLanguageVersionTypeSupport implements ITypeConverter<LanguageVersion>, Iterable<String> {

    @Override
    public Iterator<String> iterator() {
        // Explicit language-version pairs, such as "java-18" or "apex-54".
        // We build these directly to retain aliases. "java-8" works, but the canonical name for the LanguageVersion is java-1.8
        return LanguageRegistry.PMD.getLanguages().stream()
                .flatMap(l -> l.getVersionNamesAndAliases().stream().map(v -> l.getTerseName() + "-" + v))
                .collect(Collectors.toCollection(TreeSet::new)).iterator();
    }

    @Override
    public LanguageVersion convert(final String value) throws Exception {
        return LanguageRegistry.PMD.getLanguages().stream()
            .filter(l -> value.startsWith(l.getTerseName() + "-"))
            .map(l -> l.getVersion(value.substring(l.getTerseName().length() + 1)))
            .filter(Objects::nonNull)
            .findFirst()
            .orElseThrow(() -> new TypeConversionException("Unknown language version: " + value));
    }
}
