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

public class DirectoryFileFilter implements FilenameFilter {

	public boolean accept(File dir, String name) {
		return ( dir.exists() && dir.isDirectory() ) ? true : false; 			
	}

}
