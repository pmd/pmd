/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.cpp;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;

/**
 * A custom {@link Reader} which completely omits C/C++ continuation character
 * sequences from an underlying reader. Specifically the sequences {@code \ \n}
 * (backslash, carriage return), or {@code \ \r \n} (backslash, line feed,
 * carriage return).
 * <p>
 * This reader exists because to modify a JavaCC lexer to understand arbitrary
 * continuations inside of any token is cumbersome, and just removing them from
 * the input entirely is easier to implement. See this discussion on the JavaCC
 * mailing list on <a href=
 * "http://java.net/projects/javacc/lists/users/archive/2005-06/message/16">line
 * continuation character</a>.
 */
public class ContinuationReader extends Reader {
	private static final int EOF = -1;
	private static final char BACKSLASH = '\\';
	private static final char CARRIAGE_RETURN = '\n';
	private static final char LINE_FEED = '\r';

	protected final PushbackReader in;

	public ContinuationReader(Reader in) {
		this.in = new PushbackReader(in, 2);
	}

	@Override
	public int read(char[] cbuf, int off, int len) throws IOException {
		int count = 0;
		while (count < len) {
			int c1 = in.read();
			if (c1 == EOF) {
				break;
			} else if (c1 == BACKSLASH) {
				int c2 = in.read();
				if (c2 == EOF) {
					// No match
				} else if (c2 == CARRIAGE_RETURN) {
					// Match: backslash, carriage return
					continue;
				} else if (c2 == LINE_FEED) {
					int c3 = in.read();
					if (c3 == EOF) {
						// No match
						in.unread(c2);
					} else if (c3 == CARRIAGE_RETURN) {
						// Match: backslash, line feed, carriage return
						continue;
					} else {
						// No match
						in.unread(c3);
						in.unread(c2);
					}
				} else {
					// No match
					in.unread(c2);
				}
			}
			cbuf[off + count] = (char) c1;
			count++;
		}

		return count > 0 ? count : -1;
	}

	@Override
	public void close() throws IOException {
		in.close();
	}
}
