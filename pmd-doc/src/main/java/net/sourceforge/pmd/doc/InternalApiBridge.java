/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.doc;

import net.sourceforge.pmd.annotation.InternalApi;

/**
 * Internal API.
 *
 * <p>Acts as a bridge between outer parts of PMD and the restricted access
 * internal API of this package.
 *
 * <p><b>None of this is published API, and compatibility can be broken anytime!</b>
 * Use this only at your own risk.
 *
 * @apiNote Internal API
 */
@InternalApi
public final class InternalApiBridge {
    // note: this is empty - all classes in pmd-doc are internal.
    // this class is only here to fulfill the maven central requirement,
    // that every artefact has to have a javadoc jar.
}
