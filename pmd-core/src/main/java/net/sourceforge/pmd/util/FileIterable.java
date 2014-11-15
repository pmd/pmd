/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.Iterator;

/**
 *
 * <p>Handy class to easily iterate over a file, line by line, using
 * a Java 5 for loop.</p>
 *
 * @author Romain Pelisse <belaran@gmail.com>
 *
 */
public class FileIterable implements Iterable<String> {

	private  LineNumberReader lineReader = null;

	public FileIterable(File file) {

     	try {
    		lineReader = new LineNumberReader( new FileReader(file) );
    	}
    	catch (FileNotFoundException e) {
    		throw new IllegalStateException(e);
    	}
	}

	protected void finalize() throws Throwable {
		try {
			if (lineReader!= null) {
				lineReader.close();
			}
		}
		catch (IOException e) {
    		throw new IllegalStateException(e);
		}
		super.finalize();
	}

	public Iterator<String> iterator() {
		return new FileIterator();
	}

	class FileIterator implements Iterator<String> {

		private boolean hasNext = true;

		public boolean hasNext() {
			return hasNext;
		}

		public String next() {
			String line = null;
			try {
				if ( hasNext ) {
					line = lineReader.readLine();
					if ( line == null ) {
						hasNext = false;
						line = "";
					}
				}
				return line;
			} catch (IOException e) {
				throw new IllegalStateException(e);
			}
		}

		public void remove() {
			throw new UnsupportedOperationException("remove is not supported by " + this.getClass().getName());
		}

	}

}
