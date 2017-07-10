package net.sourceforge.pmd.typeresolution.testdata;

import net.sourceforge.pmd.typeresolution.testdata.dummytypes.StaticMembers;
import net.sourceforge.pmd.typeresolution.testdata.dummytypes.StaticSuper;

import static net.sourceforge.pmd.typeresolution.testdata.dummytypes.StaticMembers.primitiveStaticMethod;
import static net.sourceforge.pmd.typeresolution.testdata.dummytypes.StaticMembers.*;

public class MethodStaticAccess {
    public static double staticChar;

    public static double staticCharMethod() {
        return 0;
    }

    void foo() {
        // static field import by explicit import
        int a = primitiveStaticMethod();

        // static field import by on-demand import
        StaticMembers b = staticInstanceMethod();

        // Fully qualified static field access
        int c = StaticMembers.primitiveStaticMethod();
    }

    public class Nested extends StaticSuper {
        void bar() {
            // import shadowed by inherited static
            String a = primitiveStaticMethod();

            // enclosing scope staticChar shadows imported static field
            double b = staticCharMethod();

            // qualified access
            String c = MethodStaticAccess.Nested.primitiveStaticMethod();
        }
    }
}
