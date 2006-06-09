/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.cpd;

import java.util.Properties;

public class LanguageFactory {

    public static final String JAVA_KEY = "java";
    public static final String JSP_KEY = "jsp";
    public static final String CPP_KEY = "cpp";
    public static final String C_KEY = "c";
    public static final String PHP_KEY = "php";
    public static final String RUBY_KEY = "ruby";
    public static final String EXTENSION = "extension";
    public static final String BY_EXTENSION = "by_extension";

    public Language createLanguage(String language) {
        return createLanguage(language, new Properties());
    }

    public Language createLanguage(String language, Properties properties) {
        if (language.equals(CPP_KEY) || language.equals(C_KEY)) {
            return new CPPLanguage();
        } else if (language.equals(JAVA_KEY)) {
            return new JavaLanguage(properties);
        } else if (language.equals(JSP_KEY)) {
            return new JSPLanguage();
        } else if (language.equals(BY_EXTENSION)) {
            return new AnyLanguage(properties.getProperty(EXTENSION));
        } else if (language.equals(PHP_KEY)) {
            return new PHPLanguage();
        } else if (language.equals(RUBY_KEY)) {
            return new RubyLanguage();
        }
        return new AnyLanguage(language);
    }
}
