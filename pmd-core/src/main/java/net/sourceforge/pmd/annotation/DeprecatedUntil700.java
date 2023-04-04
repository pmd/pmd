/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.annotation;

/**
 * Tags a deprecated member that should not be removed before PMD 7.0.0.
 * Such members were made deprecated on the PMD 7 development branch and
 * may be kept for backwards compatibility on the day of the PMD 7 release,
 * because the replacement API cannot be backported to PMD 6.
 */
public @interface DeprecatedUntil700 {
}
