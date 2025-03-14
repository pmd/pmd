/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

/**
 * @see <a href="http://cr.openjdk.java.net/~gbierman/jep395/jep395-20201019/specs/local-statics-jls.html">Local and Nested Static Declarations</a>
 */
public class LocalClassAndInterfaceDeclarations {

    {
        class MyLocalClass {
            // constant fields are always allowed
            static final int constantField = 1;
            // static members in local classes are allowed with Java16
            static int staticField;
            static void staticMethod() { }
        }

        // static local classes are not allowed (neither Java16 nor Java16 Preview)
        // Note: PMD's parser allows this, but it would actually be a compile error
        //static class MyLocalStaticClass {}

        // local interfaces are allowed with Java16
        interface MyLocalInterface {}

        // local enums are allowed with Java16
        enum MyLocalEnum { A }

        // local annotation types are not allowed in Java16 (have been with Java15 Preview)
        //@interface MyLocalAnnotation {}
    }
}
