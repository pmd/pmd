/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.scm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ServiceLoader;

public final class MinimizerLanguageFactory {
    public static final MinimizerLanguageFactory INSTANCE = new MinimizerLanguageFactory();

    private final Map<String, Language> languages = new LinkedHashMap<>();

    private final String supportedLanguageNames;

    private String createLanguageHelp(List<Language> handlers) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < handlers.size(); ++i) {
            Language lang = handlers.get(i);
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(lang.getTerseName());
            if (lang.getLanguageVersions().size() > 1) {
                sb.append(" (");
                for (int j = 0; j < lang.getLanguageVersions().size(); ++j) {
                    if (j > 0) {
                        sb.append(", ");
                    }
                    sb.append(lang.getLanguageVersions().get(j));
                }
                sb.append(")");
            }
        }
        return sb.toString();
    }

    private MinimizerLanguageFactory() {
        List<Language> handlers = new ArrayList<>();
        // Use current class' classloader instead of the threads context classloader, see https://github.com/pmd/pmd/issues/1788
        ServiceLoader<Language> languageLoader = ServiceLoader.load(Language.class, getClass().getClassLoader());
        for (Language handler: languageLoader) {
            handlers.add(handler);
        }

        Collections.sort(handlers, new Comparator<Language>() {
            @Override
            public int compare(Language o1, Language o2) {
                return o1.getTerseName().compareToIgnoreCase(o2.getTerseName());
            }
        });

        for (Language handler: handlers) {
            languages.put(handler.getTerseName().toLowerCase(Locale.ROOT), handler);
        }


        supportedLanguageNames = createLanguageHelp(handlers);
    }

    public String getSupportedLanguagesWithVersions() {
        return supportedLanguageNames;
    }

    public Language getLanguage(String name) {
        return languages.get(name.toLowerCase(Locale.ROOT));
    }
}
