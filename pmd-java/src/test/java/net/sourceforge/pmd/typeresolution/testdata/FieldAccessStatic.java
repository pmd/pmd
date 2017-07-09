package net.sourceforge.pmd.typeresolution.testdata;

import static net.sourceforge.pmd.typeresolution.testdata.dummytypes.StaticFields.staticPrimitive;

import net.sourceforge.pmd.typeresolution.testdata.dummytypes.StaticFields;

import static net.sourceforge.pmd.typeresolution.testdata.dummytypes.StaticFields.*;

public class FieldAccessStatic {
    public static double staticChar;

    void foo() {
        // static field import by explicit import
        // Primary[Prefix[Name[...]]]
        staticPrimitive = 10;

        // static field import by on-demand import
        // Primary[Prefix[Name[...]]]
        staticGeneric.first = new Long(0);

        StaticFields.staticPrimitive = 10;

        // Fully qualified static field access
        // Primary[Prefix[Name[...]]]
        net.sourceforge.pmd.typeresolution.testdata.dummytypes.StaticFields.staticPrimitive = 10;

        // Fully qualified multiple static field access
        // Primary[Prefix[Name[...]]]
        net.sourceforge.pmd.typeresolution.testdata.dummytypes.StaticFields
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

class StaticSuper {
    static String staticPrimitive;
}
