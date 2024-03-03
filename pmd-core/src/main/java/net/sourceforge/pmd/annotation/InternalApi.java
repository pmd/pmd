/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.annotation;

import java.lang.annotation.Documented;


/**
 * Tags API members that are not publicly supported API but have to live in
 * public packages (outside `internal` packages).
 *
 * <p>Such members may be removed, renamed, moved, or otherwise broken at any time and should not be
 * relied upon outside the main PMD codebase.
 *
 * @since 6.7.0
 */
@Documented
public @interface InternalApi {
}
