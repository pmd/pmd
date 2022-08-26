/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cli.commands.typesupport.internal;

import java.util.Iterator;
import java.util.Optional;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.sourceforge.pmd.lang.Language;
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

    private static final String LATEST_SUFFIX = "-latest";
    
    @Override
    public Iterator<String> iterator() {
        // Raw language names / -latest versions, such as "java" or "java-latest"
        final Stream<String> latestLangReferences = LanguageRegistry.getLanguages().stream()
                .map(PmdLanguageTypeSupport::normalizeName).flatMap(name -> Stream.of(name, name + LATEST_SUFFIX));

        // Explicit language-version pairs, such as "java-18" or "apex-54"
        final Stream<String> allLangVersionReferences = LanguageRegistry.getLanguages().stream()
                .flatMap(PmdLanguageVersionTypeSupport::getNormalizedLangVerStream);
        
        // Collect to a TreeSet to ensure alphabetical order
        final TreeSet<String> candidates = Stream.concat(latestLangReferences, allLangVersionReferences)
                .collect(Collectors.toCollection(TreeSet::new));

        return candidates.iterator();
    }

    @Override
    public LanguageVersion convert(final String value) throws Exception {
        // Is it an exact match?
        final Optional<LanguageVersion> langVer = LanguageRegistry.getLanguages().stream()
                .flatMap(l ->
                    getNormalizedLangVerStream(l).filter(lv -> lv.equals(value))
                        .map(lv -> l.getVersion(lv.substring(l.getTerseName().length() + 1))))
                .findFirst();

        if (langVer.isPresent()) {
            return langVer.get();
        }

        // This is either a -latest or standalone language name
        final String langName;
        if (value.endsWith(LATEST_SUFFIX)) {
            langName = value.substring(0, value.length() - LATEST_SUFFIX.length());
        } else {
            langName = value;
        }

        return LanguageRegistry.getLanguages().stream()
                .filter(l -> PmdLanguageTypeSupport.normalizeName(l).equals(langName))
                .map(Language::getDefaultVersion).findFirst()
                .orElseThrow(() -> new TypeConversionException("Unknown language version: " + value));
    }

    private static Stream<String> getNormalizedLangVerStream(final Language lang) {
        return lang.getVersionNamesAndAliases().stream().map(v -> normalizeName(lang.getTerseName() + " " + v));
    }

    public static String normalizeName(final String langVer) {
        return langVer.trim().replace(' ', '-');
    }
    
    public static String normalizeName(final LanguageVersion langVer) {
        return normalizeName(langVer.getTerseName());
    }
}
