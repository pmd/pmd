package net.sourceforge.pmd.cpd;

public class LanguageFactory {

    public static final String JAVA_KEY = "java";

    public Language createLanguage(String language) {
        if (language.equals("cpp")) {
            return new CPPLanguage();
        } else if (language.equals("java")) {
            return new JavaLanguage();
        }
        throw new RuntimeException("Can't create language " + language);
    }
}
