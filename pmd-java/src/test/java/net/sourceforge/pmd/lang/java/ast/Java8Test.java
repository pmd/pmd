/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.java.JavaParsingHelper;

class Java8Test {
    private final JavaParsingHelper java8 =
            JavaParsingHelper.DEFAULT.withDefaultVersion("8")
                                     .withResourceContext(Java8Test.class);

    @Test
    void interfaceMethodShouldBeParseable() {
        java8.parse("interface WithStaticAndDefaultMethod {\n"
                        + "        static void performOn() {\n"
                        + "        }\n"
                        + "\n"
                        + "        default void myToString() {\n"
                        + "        }\n"
                        + "    }\n");
    }

    @Test
    void repeatableAnnotationsMethodShouldBeParseable() {
        java8.parse("@Multitude(\"1\")\n"
                        + "@Multitude(\"2\")\n"
                        + "@Multitude(\"3\")\n"
                        + "@Multitude(\"4\")\n"
                        + "public class UsesRepeatableAnnotations {\n"
                        + "\n"
                        + "    @Repeatable(Multitudes.class)\n"
                        + "    @Retention(RetentionPolicy.RUNTIME)\n"
                        + "    @interface Multitude {\n"
                        + "        String value();\n"
                        + "    }\n"
                        + "\n"
                        + "    @Retention(RetentionPolicy.RUNTIME)\n"
                        + "    @interface Multitudes {\n"
                        + "        Multitude[] value();\n"
                        + "    }\n"
                        + "\n"
                        + "}");
    }
}
