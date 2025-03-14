/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.document;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.LanguageVersionDiscoverer;
import net.sourceforge.pmd.util.log.PmdReporter;

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
    private InternalApiBridge() {}

    public static FileCollector newCollector(LanguageVersionDiscoverer discoverer, PmdReporter reporter) {
        return FileCollector.newCollector(discoverer, reporter);
    }

    public static FileCollector newCollector(FileCollector collector, PmdReporter reporter) {
        return collector.newCollector(reporter);
    }
}
