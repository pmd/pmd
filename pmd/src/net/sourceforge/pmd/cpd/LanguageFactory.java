/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package net.sourceforge.pmd.cpd;

public class LanguageFactory {

    public static final String JAVA_KEY = "java";
    public static final String CPP_KEY = "cpp";
    public static final String PHP_KEY = "php";

    public Language createLanguage(String language) {
        if (language.equals(CPP_KEY)) {
            return new CPPLanguage();
        } else if (language.equals(JAVA_KEY)) {
            return new JavaLanguage();
        } else if (language.equals(PHP_KEY)) {
            return new PHPLanguage();
        }
        throw new RuntimeException("Can't create language " + language);
    }
}
