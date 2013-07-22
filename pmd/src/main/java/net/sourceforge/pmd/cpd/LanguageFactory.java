/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.cpd;

import java.util.Properties;

public class LanguageFactory {

    /*
     * TODO derive and provide this at runtime instead, used by outside IDEs
     * FIXME Can't we do something cleaner and
     * more dynamic ? Maybe externalise to a properties files that will
     * be generated when building pmd ? This will not have to add manually
     * new language here ?
    */
   public static String[] supportedLanguages =
           new String[]{"java", "jsp", "cpp", "c", "php", "ruby", "fortran", "ecmascript", "cs", "plsql" };

   private static final String SUFFIX = "Language";
   public static final String EXTENSION = "extension";
   public static final String BY_EXTENSION = "by_extension";
   private static final String PACKAGE = "net.sourceforge.pmd.cpd.";

    public Language createLanguage(String language) {
        return createLanguage(language, new Properties());
    }

   public Language createLanguage(String language, Properties properties)
   {
     language = this.languageAliases(language);
     // First, we look for a parser following this syntax 'RubyLanguage'
     Language implementation;
     try {
       implementation = this.dynamicLanguageImplementationLoad(this.languageConventionSyntax(language));
       if ( implementation == null )
       {
         // if it failed, we look for a parser following this syntax 'CPPLanguage'
         implementation = this.dynamicLanguageImplementationLoad(language.toUpperCase());
         //TODO: Should we try to break the coupling with PACKAGE by try to load the class
         // based on her sole name ?
         if ( implementation == null )
         {
           // No proper implementation
           // FIXME: We should log a warning, shouldn't we ?
           return new AnyLanguage(language);
         }
       }
       return implementation;
     } catch (InstantiationException e) {
       e.printStackTrace();
     } catch (IllegalAccessException e) {
       e.printStackTrace();
     }
     return null;
   }

     private String languageAliases(String language)
     {
       // CPD and C language share the same parser
       if ( "c".equals(language) ) {
         return "cpp";
       }
       return language;
     }

    private Language dynamicLanguageImplementationLoad(String language) throws InstantiationException, IllegalAccessException
    {
        try {
            return (Language) this.getClass().getClassLoader().loadClass(
                PACKAGE + language + SUFFIX).newInstance();
        } catch (ClassNotFoundException e) {
            // No class found, returning default implementation
            // FIXME: There should be somekind of log of this
            return null;
        } catch (NoClassDefFoundError e) {
            // Windows is case insensitive, so it may find the file, even though
            // the name has a different case. Since Java is case sensitive, it
            // will not accept the classname inside the file that was found and
            // will throw a NoClassDefFoundError
            return null;
        }
    }

   /*
    * This method does simply this:
    * ruby -> Ruby
    * fortran -> Fortran
    * ...
    */
   private String languageConventionSyntax(String language) {
       return Character.toUpperCase(language.charAt(0)) + language.substring(1, language.length()).toLowerCase();
    }
}
