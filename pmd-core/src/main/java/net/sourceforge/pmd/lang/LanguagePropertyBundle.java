/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang;

import java.util.stream.Collectors;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.properties.AbstractPropertySource;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;

/**
 * A bundle of properties used by languages (see {@link Language#newPropertyBundle()}).
 * This class declares language properties that are common to all languages.
 * Subclasses may define more properties and provide convenient accessors to them.
 *
 * @author Clément Fournier
 */
public class LanguagePropertyBundle extends AbstractPropertySource {

    // todo for now i think an empty value might interpret every comment
    //  as a suppression. I think it should disable suppression comments.
    //  #4846
    public static final PropertyDescriptor<String> SUPPRESS_MARKER
        = PropertyFactory.stringProperty("suppressMarker")
                         .desc("Marker to identify suppression comments. "
                                   + "Eg a value of NOPMD will make `// NOPMD` a suppression comment in Java or JavaScript.")
                         .defaultValue(PMDConfiguration.DEFAULT_SUPPRESS_MARKER)
                         .build();
    public static final String LANGUAGE_VERSION = "version";

    private final PropertyDescriptor<String> languageVersion;
    private final Language language;

    /**
     * Create a new bundle for the given language.
     */
    public LanguagePropertyBundle(@NonNull Language language) {
        this.language = language;
        languageVersion = PropertyFactory.stringProperty(LANGUAGE_VERSION)
                .desc("Language version to use for this language. See the --use-version CLI switch as well.")
                .defaultValue(language.getDefaultVersion().getVersion())
                .build();

        definePropertyDescriptor(SUPPRESS_MARKER);
        definePropertyDescriptor(languageVersion);
    }

    public void setLanguageVersion(String string) {
        LanguageVersion version = language.getVersion(string);
        if (version == null) {
            throw new IllegalArgumentException("'" + string + "' should be one of "
                    + language.getVersions().stream()
                        .map(LanguageVersion::getVersion)
                        .map(s -> "'" + s + "'")
                        .collect(Collectors.joining(", ")));
        }
        setProperty(languageVersion, version.getVersion());
    }

    @Override
    protected String getPropertySourceType() {
        return "Language";
    }

    @Override
    public String getName() {
        return language.getName();
    }

    public Language getLanguage() {
        return language;
    }

    public LanguageVersion getLanguageVersion() {
        return language.getVersion(getProperty(languageVersion));
    }

    public String getSuppressMarker() {
        return getProperty(SUPPRESS_MARKER);
    }
}
