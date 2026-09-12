/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.design.internalapiusage;

import org.apiguardian.api.API;

@API(status = API.Status.INTERNAL)
public class ClassLevelInternal {
    private ClassLevelInternal() {}

    public static void internal() {}
}
