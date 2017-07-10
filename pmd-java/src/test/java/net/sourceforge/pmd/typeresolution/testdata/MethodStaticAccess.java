/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.typeresolution.testdata;

import static net.sourceforge.pmd.typeresolution.testdata.dummytypes.StaticMembers.*;
import static net.sourceforge.pmd.typeresolution.testdata.dummytypes.StaticMembers.primitiveStaticMethod;

import net.sourceforge.pmd.typeresolution.testdata.dummytypes.StaticMembers;
import net.sourceforge.pmd.typeresolution.testdata.dummytypes.StaticSuper;


public class MethodStaticAccess {
    public static double staticChar;

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
            // qualified access
            String c = MethodStaticAccess.Nested.primitiveStaticMethod();
        }
    }
}
