/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.testframework;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;


public final class StreamUtil {

    private StreamUtil() {
        // utility class
    }

	public static String toString(InputStream in) {
		StringBuilder sb = new StringBuilder();
		int c;
		try {
			while ((c = in.read()) != -1) {
				sb.append((char) c);
			}
		} catch (IOException e) {
			// ignored
		} finally {
			IOUtils.closeQuietly(in);
		}
		return sb.toString();
	}

}
