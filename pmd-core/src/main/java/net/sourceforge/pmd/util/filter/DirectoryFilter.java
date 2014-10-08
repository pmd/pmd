/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.util.filter;

import java.io.File;

/**
 * Directory filter.
 */
public final class DirectoryFilter implements Filter<File> {
	public static final DirectoryFilter INSTANCE = new DirectoryFilter();

	private DirectoryFilter() {
	}

	public boolean filter(File file) {
		return file.isDirectory();
	}
	
	public String toString() {
	    return "is Directory";
	}
}
