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
    static void internal() {}

    @API(status = API.Status.INTERNAL, consumers = "missing.package")
    static void internalForOtherPackage() {}

    @API(status = API.Status.INTERNAL, consumers = "net.sourceforge.pmd.lang.java.rule.design.internalapiusage")
    static void internalForThisPackage() {}

    @ApiStatus.Internal
    static void internalJB() {}
}
