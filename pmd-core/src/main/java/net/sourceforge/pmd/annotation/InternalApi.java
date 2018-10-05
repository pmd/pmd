/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.annotation;

import java.lang.annotation.Documented;


/**
 * Tags API members that are not publicly supported API.
 * Such members may be removed, renamed, moved, or otherwise
 * broken at any time and should not be relied upon outside
 * of the main PMD codebase.
 *
 * <p>Members and types tagged with this annotation will remain
 * supported until 7.0.0, after which some will be moved to internal
 * packages, or will see their visibility reduced.
 *
 * @since 6.7.0
 */
// NOTE: use @Deprecated with this annotation to raise a compiler warning until 7.0.0
@Documented
public @interface InternalApi {
}
