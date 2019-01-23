/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.util.autocomplete;

import java.util.List;

import net.sourceforge.pmd.lang.Language;


/**
 * Language-specific tool that finds the available node names for XPath.
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
interface NodeNameFinder {

    /** Gets all the names available from XPath for a language. */
    List<String> getNodeNames();


    /**
     * Gets the name finder specific to the given language.
     * For Apex, Java, etc. it's enough to look into a classpath
     * directory. For XML we could eg use the names that are found
     * in the editor.
     */
    static NodeNameFinder forLanguage(Language language) {
        return new AstPackageExplorer(language);
    }
}
