/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

public class LocalInterfacesAndEnums {

    {
        class MyLocalClass {}

        // static local classes are not allowed (neither Java15 nor Java15 Preview)
        //static class MyLocalStaticClass {}

        interface MyLocalInterface {}

        enum MyLocalEnum { A }

        // not supported anymore with Java16
        //@interface MyLocalAnnotation {}
    }
}
