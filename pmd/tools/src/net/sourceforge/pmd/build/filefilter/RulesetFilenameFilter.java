/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.build.filefilter;

import java.io.File;
import java.io.FilenameFilter;

/**
 *
 * @author Romain PELISSE, belaran@gmail.com
 *
 */

public class RulesetFilenameFilter implements FilenameFilter {

	// FUTURE: Make this somehow configurable ? Turn into an array passed by constructor ?
	// TODO: move to compiled regex to improve perf.
	private static final String[] patterns = { "^[0-9][0-9].*\\.xml", "^.*dogfood.*\\.xml", "^all-.*\\.xml", "^migrating_.*\\.xml", "^pmdspecific.xml"} ;
	
	public boolean accept(File file, String name) {
	    if ( doesNotMatchExcludeNames(name) ) 
	    	return (name.endsWith(".xml"));
	    else
	    	return false;
	}
	
	private boolean doesNotMatchExcludeNames(String name) {
		for ( String pattern : patterns ) {
			if ( name.matches(pattern))
				return false;
		}
		return true;
	}
}
