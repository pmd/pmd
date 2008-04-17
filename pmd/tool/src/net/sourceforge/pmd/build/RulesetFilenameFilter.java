/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.build;

import java.io.File;
import java.io.FilenameFilter;

/**
 * @author rpelisse
 *
 */
public class RulesetFilenameFilter implements FilenameFilter {

	/* (non-Javadoc)
	 * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
	 */
	public boolean accept(File file, String name) {
	    if ( ! name.startsWith("migrating_") && ! name.startsWith("scratchpad") && ! name.startsWith("Favorites") )
	    	return (name.endsWith(".xml"));
	    else
		return false;
	}

}
