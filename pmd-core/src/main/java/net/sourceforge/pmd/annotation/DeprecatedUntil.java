/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.annotation;

import java.lang.annotation.Documented;

/**
 * Tags a deprecated member that should not be removed before PMD {@link #major()}.
 * Such members were made deprecated on the PMD {@link #major()} development branch but
 * are to be kept for backwards compatibility on the day of the PMD {@link #major()} release.
 */
@Documented
public @interface DeprecatedUntil {
    /**
     * @return The major version until which the deprecated API is to be kept and supported.
     */
    String major();

    /**
     * @return The future outcome for this API, weather it's to be removed completely or converted to an {@link InternalApi}
     */
    FutureOutcome future();

    enum FutureOutcome {
        REMOVAL, INTERNALIZATION;
    }
}
