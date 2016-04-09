/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.cpd;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.ServiceLoader;

public final class LanguageFactory {

    public static final String EXTENSION = "extension";
    public static final String BY_EXTENSION = "by_extension";

    private static LanguageFactory instance = new LanguageFactory();

    public static String[] supportedLanguages;
    static {
       supportedLanguages = instance.languages.keySet().toArray(new String[instance.languages.size()]);
    }

   private Map<String, Language> languages = new HashMap<>();

   private LanguageFactory() {
       ServiceLoader<Language> languageLoader = ServiceLoader.load(Language.class);
       Iterator<Language> iterator = languageLoader.iterator();
       while (iterator.hasNext()) {
           try {
            Language language = iterator.next();
               languages.put(language.getTerseName().toLowerCase(), language);
        } catch (UnsupportedClassVersionError e) {
            // Some languages require java8 and are therefore only available
            // if java8 or later is used as runtime.
            System.err.println("Ignoring language for CPD: " + e.toString());
        }
       }
   }
   
    public static Language createLanguage(String language) {
        return createLanguage(language, new Properties());
    }

   public static Language createLanguage(String language, Properties properties)
   {
     Language implementation; 
     if (BY_EXTENSION.equals(language)) {
         implementation = instance.getLanguageByExtension(properties.getProperty(EXTENSION));
     } else {
         implementation = instance.languages.get(instance.languageAliases(language).toLowerCase());
     }
     if (implementation == null) {
         // No proper implementation
         // FIXME: We should log a warning, shouldn't we ?
         implementation = new AnyLanguage(language);
     }
     implementation.setProperties(properties);
     return implementation;
   }

     private String languageAliases(String language)
     {
       // CPD and C language share the same parser
       if ( "c".equals(language) ) {
         return "cpp";
       }
       return language;
     }
     
     private Language getLanguageByExtension(String extension) {
         Language result = null;

         for (Language language : languages.values()) {
             if (language.getExtensions().contains(extension)) {
                 result = language;
                 break;
             }
         }
         return result;
     }
}
