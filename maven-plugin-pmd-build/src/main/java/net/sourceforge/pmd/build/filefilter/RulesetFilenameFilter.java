/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.build.filefilter;

import java.io.File;
import java.io.FilenameFilter;
import java.util.regex.Pattern;

/**
 *
 * @author Romain PELISSE, belaran@gmail.com
 *
 */

public class RulesetFilenameFilter implements FilenameFilter {

	// FUTURE: Make this somehow configurable ? Turn into an array passed by constructor ?
	private static final Pattern EXCLUDE = Pattern.compile(
		"(^[0-9][0-9].*\\.xml)" +
		"|(^.*dogfood.*\\.xml)" +
		"|(^all-.*\\.xml)" +
		"|(^migrating_.*\\.xml)" +
		"|(^pmdspecific.xml)"
	);
	
	public boolean accept(File file, String name) {
	    if ( doesNotMatchExcludeNames(name) ) 
	    	return (name.endsWith(".xml"));
	    else
	    	return false;
	}
	
	private boolean doesNotMatchExcludeNames(String name) {
	    return !EXCLUDE.matcher(name).matches();
	}
}
