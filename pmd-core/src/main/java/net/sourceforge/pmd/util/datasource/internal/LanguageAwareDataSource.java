/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.datasource.internal;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.LanguageVersion;

@InternalApi
public interface LanguageAwareDataSource {
    LanguageVersion getLanguageVersion();
}
