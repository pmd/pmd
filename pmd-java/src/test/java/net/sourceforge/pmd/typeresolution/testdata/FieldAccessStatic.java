/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.typeresolution.testdata;


import static net.sourceforge.pmd.typeresolution.testdata.dummytypes.StaticMembers.*;
import static net.sourceforge.pmd.typeresolution.testdata.dummytypes.StaticMembers.staticPrimitive;

import net.sourceforge.pmd.typeresolution.testdata.dummytypes.StaticMembers;
import net.sourceforge.pmd.typeresolution.testdata.dummytypes.StaticSuper;

public class FieldAccessStatic {
    public static double staticChar;

    void foo() {
        // static field import by explicit import
        // Primary[Prefix[Name[...]]]
        staticPrimitive = 10;

        // static field import by on-demand import
        // Primary[Prefix[Name[...]]]
        staticGeneric.first = new Long(0);

        StaticMembers.staticPrimitive = 10;

        // Fully qualified static field access
        // Primary[Prefix[Name[...]]]
        StaticMembers.staticPrimitive = 10;

        // Fully qualified multiple static field access
        // Primary[Prefix[Name[...]]]
        StaticMembers
                .staticGeneric.generic.second = new Long(10);
    }

    public class Nested extends StaticSuper {
        void bar() {
            // import shadowed by inherited static
            // Primary[Prefix[Name[...]]]
            staticPrimitive = "";

            // enclosing scope staticChar shadows imported static field
            // Primary[Prefix[Name[...]]]
            staticChar = 3.1;

            // qualified access
            // Primary[Prefix[Name[...]]]
            Nested.staticPrimitive = "";
        }
    }
}



