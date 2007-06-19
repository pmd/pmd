/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.cpd;

import java.util.Properties;

public class LanguageFactory {

   // FIXME: A refactoring should be done to remove those
   public static final String JAVA_KEY = "java";
    public static final String JSP_KEY = "jsp";
    public static final String CPP_KEY = "cpp";
    public static final String C_KEY = "c";
    public static final String PHP_KEY = "php";
    public static final String RUBY_KEY = "ruby";
   public static final String FORTRAN_KEY = "fortran";

   private static final String SUFFIX = "Language";
    public static final String EXTENSION = "extension";
    public static final String BY_EXTENSION = "by_extension";
   private static final String PACKAGE = "net.sourceforge.pmd.cpd.";

    public Language createLanguage(String language) {
        return createLanguage(language, new Properties());
    }

    public Language createLanguage(String language, Properties properties) {

       try {
           return (Language) this.getClass().getClassLoader().loadClass(PACKAGE +this.languageConventionSyntax(language) + SUFFIX).newInstance();
       } catch (ClassNotFoundException e) {
           if (language.equals(CPP_KEY) || language.equals(C_KEY)) {
               return new CPPLanguage();
           } else if (language.equals(PHP_KEY)) {
               return new PHPLanguage();
           } else if (language.equals(JSP_KEY)) {
               return new JSPLanguage();
           }
           // No class found, returning default implementation
           // FIXME: There should be somekind of log of this
           return new AnyLanguage(language);
       } catch (InstantiationException e) {
            e.printStackTrace();
       } catch (IllegalAccessException e) {
           e.printStackTrace();
       }
       return null;
   }

   /*
    * This method does simply this:
    * ruby -> Ruby
    * fortran -> Fortran
    * ...
    */
   private String languageConventionSyntax(String language) {
       return (language.charAt(0) + "").toUpperCase() + language.substring(1, language.length()).toLowerCase();
    }
}
