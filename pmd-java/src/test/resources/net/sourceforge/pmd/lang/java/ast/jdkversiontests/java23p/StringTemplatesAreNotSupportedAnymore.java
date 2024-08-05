/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

/**
 * It was decided, that String Templates will be removed.
 *
 * @see <a href="https://bugs.openjdk.org/browse/JDK-8329949">JDK-8329949 Remove the String Templates preview feature</a> (Java 23)
 */
public class StringTemplatesAreNotSupportedAnymore {
    static void STRTemplateProcessor() {
        // Embedded expressions can be strings
        String firstName = "Bill";
        String lastName = "Duck";
        String fullName = STR."\{firstName} \{lastName}";
        // | "Bill Duck"
        String sortName = STR."\{lastName}, \{firstName}";
        // | "Duck, Bill"
    }
}
