/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.design.internalapiusage;

import org.apiguardian.api.API;
import org.jetbrains.annotations.ApiStatus;

public class Internal {

    private Internal() {
        // utility class
    }

    @VisibleForTesting
    static void visibleForTesting() {}

    @TestOnly
    static void testOnly() {}

    @API(status = API.Status.INTERNAL)
    public static void internal() {}

    @API(status = API.Status.INTERNAL, consumers = "missing.package")
    public static void internalForOtherPackage() {}

    @API(status = API.Status.INTERNAL, consumers = "net.sourceforge.pmd.lang.java.rule.design.internalapiusage")
    public static void internalForThisPackage() {}

    @ApiStatus.Internal
    public static void internalJB() {}

    @API(status = API.Status.EXPERIMENTAL)
    public static void experimental() {}

    @ApiStatus.Experimental
    public static void experimentalJB() {}
}
