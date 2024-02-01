/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types;

import net.sourceforge.pmd.annotation.InternalApi;

/**
 * @apiNote Internal API
 */
@InternalApi
public final class InternalApiBridge {
    private InternalApiBridge() {
        // internal api
    }

    public static boolean isSameTypeInInference(JTypeMirror t, JTypeMirror s) {
        return TypeOps.isSameTypeInInference(t, s);
    }
}
