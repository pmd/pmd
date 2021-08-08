/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast;

import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.util.DataMap;
import net.sourceforge.pmd.util.DataMap.SimpleDataKey;

/**
 * This interface can be used to tag the root node of various ASTs.
 */
public interface RootNode extends Node {

    /**
     * The name of the file, including its extension. This
     * excludes any segments for containing directories.
     */
    @Experimental
    SimpleDataKey<String> FILE_NAME_KEY = DataMap.simpleDataKey("pmd.fileName");

    // that's only a marker interface.
}
