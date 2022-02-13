/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.document;

import java.io.IOException;

import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.util.datasource.DataSource;

/**
 * Collects files to analyse before a PMD run. This API allows opening
 * zip files and makes sure they will be closed at the end of a run.
 *
 * @author Clément Fournier
 */
@Experimental
public interface TextFile {

    String getPathId();

    String getDisplayName();

    String readContents() throws IOException;

    LanguageVersion getLanguageVersion();

    DataSource toDataSourceCompat();
}
