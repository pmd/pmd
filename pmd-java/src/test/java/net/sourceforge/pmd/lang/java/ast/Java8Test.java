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
        java8.parse("""
                        interface WithStaticAndDefaultMethod {
                                static void performOn() {
                                }
                        
                                default void myToString() {
                                }
                            }
                        """);
    }

    @Test
    void repeatableAnnotationsMethodShouldBeParseable() {
        java8.parse("""
                        @Multitude("1")
                        @Multitude("2")
                        @Multitude("3")
                        @Multitude("4")
                        public class UsesRepeatableAnnotations {
                        
                            @Repeatable(Multitudes.class)
                            @Retention(RetentionPolicy.RUNTIME)
                            @interface Multitude {
                                String value();
                            }
                        
                            @Retention(RetentionPolicy.RUNTIME)
                            @interface Multitudes {
                                Multitude[] value();
                            }
                        
                        }\
                        """);
    }
}
