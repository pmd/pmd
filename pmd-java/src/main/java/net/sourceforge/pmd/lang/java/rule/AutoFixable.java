/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule;

import java.io.File;

public interface AutoFixable {
    /**
     * @param rawSource Original source code
     * @param file Target file being analyzed
     * @return Modified source code with fixes applied
     */
    String apply(String rawSource, File file);
}